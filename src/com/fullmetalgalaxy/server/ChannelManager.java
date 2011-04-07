/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.Presence.ClientType;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.server.xmpp.XMPPMessageServlet;
import com.fullmetalgalaxy.server.xmpp.XMPPProbeServlet;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * @author Vincent
 * This class keep track of all presences on all games/rooms and allow to broadcast any message to any room.
 * 
 * TODO use cron task to remove too old presence
 */
public class ChannelManager
{
  /**  */
  private final static int CACHE_ROOM_TTL_SEC = 60 * 60 * 2; // 2h
  /** if last connection is closer than this value, we don't need to update cache */
  private final static int CACHE_PRESENCE_MIN_DIFF_MS = 1000 * 60 * 4; // 4min
  /** if last connection is farer (older) than this value we should ask client for an update
   *  because he may have crash. In this case, we will disconnect it. */
  private final static int CACHE_PRESENCE_ASK_TTL_MS = 1000 * 60 * 15; // 15min
  /** if last connection is farer (older) than this value we will disconnect it. */
  private final static int CACHE_PRESENCE_TTL_MS = 1000 * 60 * 20; // 20min
  /** if last connection is closer than this value, we don't need to update cache */
  private final static int CONNECTION_MIN_DIFF_MS = 1000 * 5; // 5sec

  private static MemcacheService s_cache = MemcacheServiceFactory.getMemcacheService();
  private static ChannelService s_channelService = ChannelServiceFactory.getChannelService();
  private final static FmpLogger log = FmpLogger.getLogger( ChannelManager.class.getName() );



  /**
   * WARNING p_presence shouldn't be modified after this call, as it's only copy.
   * moreover, presence is cached but further modification won't update this cache.
   * @param p_pseudo
   * @param p_gameId
   * @param out p_presence created by this connection
   * @return channelToken to be send to client
   */
  public static String connect(String p_pseudo, long p_gameId, ClientType p_clientType, Presence p_presence)
  {
    assert p_pseudo != null;
    if( p_presence != null )
    {
      p_presence.reinit();
    }
    else
    {
      p_presence = new Presence();
    }

    // set presence information
    p_presence.setPseudo( p_pseudo );
    p_presence.setGameId( p_gameId );
    p_presence.setPageId( RpcUtil.random( Integer.MAX_VALUE-1 )+1 );
    p_presence.setClientType( p_clientType );

    PresenceRoom room = getRoom( p_presence.getGameId() );
    Presence lastPresence = room.getLastPresence( p_pseudo );
    if( lastPresence != null && lastPresence.getLastConnexion().after( new Date(System.currentTimeMillis()-CONNECTION_MIN_DIFF_MS) ) )
    {
      // these two connections are too close... consider as the same
      // its a workaround because game.jsp is sometime called twice and I don't
      // know why !
      return null;
    }
    
    // connect presence
    return connect(p_presence);
  }



  public static String connect(Presence p_presence)
  {
    assert p_presence != null;
    PresenceRoom room = getRoom( p_presence.getGameId() );

    // add presence to room
    room.connect( p_presence );

    // reopen channel
    String channelToken = null;
    if( p_presence.getClientType() == ClientType.CHAT || p_presence.getClientType() == ClientType.GAME )
    {
      channelToken = s_channelService.createChannel( p_presence.getChannelId() );
    }
    
    // room was updated: update cache
    getCache().put( p_presence.getGameId(), room, Expiration.byDeltaSeconds( CACHE_ROOM_TTL_SEC ) );

    // then broadcast connection to all connected users
    broadcast( room );
    return channelToken;
  }


  public static void disconnect(Presence p_presence)
  {
    if( p_presence == null )
    {
      return;
    }
    PresenceRoom room = getRoom( p_presence.getGameId() );
    room.disconnect( p_presence.getPseudo(), p_presence.getPageId() );
    if( room.isEmpty() )
    {
      // room empty: clear cache
      getCache().delete( p_presence.getGameId() );
    }
    else
    {
      // room was updated: update cache
      getCache()
          .put( p_presence.getGameId(), room, Expiration.byDeltaSeconds( CACHE_ROOM_TTL_SEC ) );

      // then broadcast connection to all connected users
      broadcast( room );
    }
  }

  /**
   * 
   * @param p_pseudo
   * @param p_gameId
   * @return
   */
  public static List<Presence> getPresence(String p_pseudo, long p_gameId)
  {
    PresenceRoom room = getRoom( p_gameId );
    return room.getPresence( p_pseudo );
  }

  /**
   * broadcast PresenceRoom over all user connected to this room.
   * @param p_room
   */
  protected static void broadcast(PresenceRoom p_room)
  {
    String response = Serializer.toClient( p_room );

    if( response != null )
    {
      for( Presence presence : p_room )
      {
        if( presence.getClientType() == ClientType.XMPP )
        {
          // send presence to xmpp clients
          XMPPProbeServlet.sendPresence( presence.getPseudo() );
        }
        else
        {
          // send presence to web client
          try
          {
            s_channelService.sendMessage( new ChannelMessage( presence.getChannelId(), response ) );
          } catch( Exception e )
          {
            log.error( e );
          }
        }
      }
    }
  }

  /**
   * @param p_room
   * @param p_msg if empty, don't send anything
   */
  public static void broadcast(PresenceRoom p_room, ChatMessage p_msg)
  {
    boolean isRoomUpdated = false;
    List<Presence> toRemove = new ArrayList<Presence>();
    isRoomUpdated |= updateLastConnexion( p_room, p_msg.getFromPseudo(), p_msg.getFromPageId() );
    isRoomUpdated |= removeTooOld( p_room );
    
    String response = Serializer.toClient( p_msg );

    if( response != null && !p_msg.isEmpty() )
    {
      for( Presence presence : p_room )
      {
        if( presence.getClientType() == ClientType.XMPP )
        {
          // this presence is a Jabber client: if it is the sender
          // we shouldn't send him back his message
          if( !presence.getPseudo().equalsIgnoreCase( p_msg.getFromPseudo() ) )
          {
            // send chat message to a jabber client
            boolean isSend = XMPPMessageServlet.sendXmppMessage( presence.getPseudo(), p_msg );
            if( !isSend )
            {
              // message send fail: remove presence
              toRemove.add( presence );
            }
          }
        }
        else
        {
          // send chat message to web client
          try
          {
            s_channelService.sendMessage( new ChannelMessage( presence.getChannelId(), response ) );
          } catch( Exception e )
          {
            log.error( e );
            // message send fail: remove presence
            toRemove.add( presence );
          }
        }
      }
    }
    
    if( !toRemove.isEmpty() )
    {
      p_room.removeAll( toRemove );
      isRoomUpdated = true;
    }
    if( isRoomUpdated )
    {
      // room was updated: update cache
      getCache().put( p_room.getGameId(), p_room, Expiration.byDeltaSeconds( CACHE_ROOM_TTL_SEC ) );
      broadcast( p_room );
    }
  }

  protected static void broadcast(PresenceRoom p_room, ModelFmpUpdate p_modelUpdate)
  {
    boolean isRoomUpdated = false;
    // TODO this can't currently work as runAction/runEvent method don't receive pageId
    // => merge these two method and use a modelUpdate
    //isRoomUpdated |= updateLastConnexion( p_room, p_modelUpdate.getFromPseudo(), p_modelUpdate.getFromPageId() );
    isRoomUpdated |= removeTooOld( p_room );
    if( isRoomUpdated )
    {
      // room was updated: update cache
      getCache().put( p_room.getGameId(), p_room, Expiration.byDeltaSeconds( CACHE_ROOM_TTL_SEC ) );
    }
    
    String response = Serializer.toClient( p_modelUpdate );

    if( response != null )
    {
      for( Presence presence : p_room )
      {
        try
        {
          if( presence.getClientType() == ClientType.GAME )
          {
            s_channelService.sendMessage( new ChannelMessage( presence.getChannelId(), response ) );
          }
        } catch( Exception e )
        {
          log.error( e );
        }
      }
    }
  }
  
  
  /**
   * update last connexion date for one client
   * @param p_room
   * @param p_pseudo
   * @param p_pageId
   * @return true if p_room was modified (so you should update cache)
   */
  private static boolean  updateLastConnexion(PresenceRoom p_room, String p_pseudo, int p_pageId)
  {
    assert p_room != null && p_pseudo != null;
    boolean isRoomUpdated = false;
    Presence presence = p_room.getPresence( p_pseudo, p_pageId );
    if( presence == null )
    {
      // presence of the sender wasn't found in room...
      presence = new Presence( p_pseudo, p_room.getGameId(), p_pageId );
      presence.setClientType( ClientType.GAME );
      if( p_pageId == 0 )
      {
        presence.setClientType( ClientType.XMPP );
      }
      p_room.connect( presence );
      isRoomUpdated = true;
    }
    else if( presence.getLastConnexion().before( new Date(System.currentTimeMillis()-CACHE_PRESENCE_MIN_DIFF_MS)) )
    {
      // presence of sender was a bit old: refresh it 
      presence.setLastConnexion();
      isRoomUpdated = true;
    }
    return isRoomUpdated;
  }

  /**
   * remove too old presence and ask quite old presence to send an empty message
   * @param p_room
   * @return true if p_room was modified (so you should update cache)
   */
  private static boolean removeTooOld(PresenceRoom p_room)
  {
    boolean isRoomUpdated = false;
    // search for old presence
    ArrayList<Presence> toRemove = new ArrayList<Presence>();
    ArrayList<Presence> toAsk = new ArrayList<Presence>();
    for( Presence presence : p_room )
    {
      if( presence.getLastConnexion().before( new Date(System.currentTimeMillis()-CACHE_PRESENCE_ASK_TTL_MS)) )
      {
        if( presence.getLastConnexion().before( new Date(System.currentTimeMillis()-CACHE_PRESENCE_TTL_MS)) )
        {
          toRemove.add( presence );
        }
        else
        {
          toAsk.add( presence );
        }
      }
    }
    
    // remove too old presence
    if( !toRemove.isEmpty() )
    {
      p_room.removeAll( toRemove );
      isRoomUpdated = true;
    }
    
    // ask old presence to send and empty message
    String response = Serializer.toClient( new ChatMessage() );
    for( Presence presence : toAsk )
    {
      try
      {
        s_channelService.sendMessage( new ChannelMessage( presence.getChannelId(), response ) );
      } catch( Exception e )
      {
        log.error( e );
      }
    }
    return isRoomUpdated;
  }
  
  public static PresenceRoom getRoom(long p_gameId)
  {
    PresenceRoom room = (PresenceRoom)getCache().get( p_gameId );
    if( room == null )
    {
      room = new PresenceRoom( p_gameId );
    }
    return room;
  }




  /**
   * @return the s_cache
   */
  private static MemcacheService getCache()
  {
    return s_cache;
  }

}
