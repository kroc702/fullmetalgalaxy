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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.chat;

import java.util.logging.Level;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.event.ChannelMessageEventHandler;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * TODO it seems that after two hours, our reconnexion mechanism don't work.
 */
public class ChatEngine implements EntryPoint, ChannelMessageEventHandler
{
  public static final String HISTORY_ID = "ChatEngine";
  private static ChatEngine s_instance = null;
  
  private ChatMessage m_lastMsg = new ChatMessage();

  // connected players (or any other peoples)
  private PresenceRoom m_presenceRoom = new PresenceRoom( 0 );

  
  public ChatEngine()
  {
    super();
    s_instance = this;
  }
  
  
  private static AsyncCallback<Void> m_sendMessageCallback = new AsyncCallback<Void>()
  {

    @Override
    public void onFailure(Throwable p_caught)
    {
      // TODO send error to inform user !
      //m_wgtMessages.addMessage( "last message failed" );
    }

    @Override
    public void onSuccess(Void p_result)
    {
    }

  };


  public static void sendMessage( String p_msg )
  {
    if( s_instance == null || p_msg == null || p_msg.isEmpty() )
    {
      return;
    }
    s_instance.m_lastMsg.setFromPseudo( AppMain.instance().getMyAccount().getPseudo() );
    s_instance.m_lastMsg.setGameId( s_instance.m_presenceRoom.getGameId() );
    s_instance.m_lastMsg.setFromPageId( (int)AppMain.instance().getPageId() );
    s_instance.m_lastMsg.setText( p_msg );
    s_instance.m_lastMsg.getDate().setTime( ClientUtil.serverTimeMillis() );
    AppMain.getRpcService().sendChatMessage( s_instance.m_lastMsg, m_sendMessageCallback );
  }

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad()
  {
    // init some constant
    String id = ClientUtil.getUrlParameter( "id" );
    long gameId = 0;
    try
    {
      gameId = Long.parseLong( id );
    } catch( Exception e )
    {
      gameId = 0;
    }
    m_presenceRoom.setGameId( gameId );
    loadPresenceRoomFromPage();
    
    AppMain.instance().addChannelMessageEventHandler( PresenceRoom.class, this );
    
  }


  public void loadPresenceRoomFromPage()
  {
    String strRoom = ClientUtil.getJSString( "fmp_room" );
    if( strRoom != null && !strRoom.equalsIgnoreCase( "null" ) )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( GameServices.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strRoom );
        Object object = reader.readObject();
        if( object instanceof PresenceRoom )
        {
          m_presenceRoom = (PresenceRoom)object;
        }
      } catch( SerializationException e )
      {
        AppRoot.logger.log( Level.WARNING, e.getMessage() );
      }
    }

  }

  
  public boolean isUserConnected(String p_pseudo)
  {
    assert p_pseudo != null;
    return getPresenceRoom().isConnected( p_pseudo );
  }


  public PresenceRoom getPresenceRoom()
  {
    return m_presenceRoom;
  }

  
  @Override
  public void onChannelMessage(Object p_message)
  {
    if( p_message instanceof PresenceRoom)
    {
      m_presenceRoom = (PresenceRoom)p_message;
      //AppRoot.getEventBus().fireEvent( new PresenceRoomEvent(getPresenceRoom()) );
    }
  }
}
