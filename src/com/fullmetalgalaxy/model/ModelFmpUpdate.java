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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 * This class contain all informations send from server to client
 * to update client model.
 * - game events
 * - chat messages
 * - player connection list
 */
public class ModelFmpUpdate implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 202;

  private long m_gameId = 0;

  private List<AnEvent> m_gameEvents = null;

  private List<ChatMessage> m_chatMessages = null;

  // connected players (or any other peoples)
  private Set<ConnectedUser> m_connectedUsers = null;

  private Date m_lastUpdate = new Date( 1 );
  /** is only use for events (set in setGameEvents) */
  private Date m_fromUpdate = new Date( 1 );


  private Map<Long, EbAccount> m_accounts = null;



  public ModelFmpUpdate()
  {
  }

  private EbAccount getAccount(String p_pseudo)
  {
    assert p_pseudo != null;
    for( Map.Entry<Long, EbAccount> entry : getMapAccounts().entrySet() )
    {
      if( p_pseudo.equals( entry.getValue().getPseudo() ) )
      {
        return entry.getValue();
      }
    }
    return null;
  }

  public ModelFmpUpdate(EbGame p_game, Date p_fromUpdate)
  {
    setLastUpdate( new Date( System.currentTimeMillis() ) );
    if( p_game != null )
    {
      setGameId( p_game.getId() );
      setGameEvents( p_game.getLogs(), p_fromUpdate );
    }
    setChatMessages( new ArrayList<ChatMessage>() );
  }

  public ModelFmpUpdate getNewModelUpdate(Date p_fromUpdate)
  {
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setGameEvents( getGameEvents(), p_fromUpdate );
    updates.setChatMessages( getListChatMessages( getChatMessages(), p_fromUpdate ) );
    updates.setConnectedUsers( getConnectedUsers() );
    updates.setLastUpdate( getLastUpdate() );
    updates.setMapAccounts( getMapAccounts() );
    return updates;
  }


  public boolean deleteTooOldChatMessages()
  {
    boolean messagesDeleted = false;
    // search too old messages
    Date tooOld = new Date( System.currentTimeMillis() - FmpConstant.chatMessagesLivePeriod * 1000 );
    int i = 0;
    while( (i < getChatMessages().size())
        && (getChatMessages().get( i ).getDate().before( tooOld )) )
    {
      i++;
    }
    i--;
    // then add new message to messages
    while( i >= 0 )
    {
      getChatMessages().remove( 0 );
      i--;
      messagesDeleted = true;
    }
    return messagesDeleted;
  }


  private static List<ChatMessage> getListChatMessages(List<ChatMessage> p_chatMessages,
      Date p_lastVersion)
  {
    List<ChatMessage> messages = new ArrayList<ChatMessage>();
    // search the first message since p_lastVersion
    int i = p_chatMessages.size() - 1;
    while( (i >= 0) && (p_chatMessages.get( i ).getDate().after( p_lastVersion )) )
    {
      i--;
    }
    i++;
    // then add new message to messages
    while( i < p_chatMessages.size() )
    {
      messages.add( p_chatMessages.get( i ) );
      i++;
    }
    return messages;
  }


  /**
   * after a call to this method all returned event (witch are also contained by p_game)
   * are packed and ready to be serialized.
   * @param p_game
   * @param p_lastVersion
   * @return
   */
  private static List<AnEvent> getListEvents(List<AnEvent> p_logs, Date p_lastVersion)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    // search the first event since p_lastVersion
    int i = p_logs.size() - 1;
    while( (i >= 0) && (p_logs.get( i ).getLastUpdate().after( p_lastVersion )) )
    {
      i--;
    }
    i++;
    // then add them to events
    while( i < p_logs.size() )
    {
      events.add( p_logs.get( i ) );
      i++;
    }
    return events;
  }


  /**
   * @param p_gameEvents the gameEvents to set
   */
  public void setGameEvents(List<AnEvent> p_gameEvents, Date p_fromUpdate)
  {
    setGameEvents( getListEvents( p_gameEvents, p_fromUpdate ) );
    m_fromUpdate.setTime( p_fromUpdate.getTime() );
    Date lastUpdate = getLastUpdate();
    for( AnEvent event : getGameEvents() )
    {
      if( event.getLastUpdate().after( lastUpdate ) )
      {
        lastUpdate.setTime( event.getLastUpdate().getTime() );
      }
    }
    setLastUpdate( lastUpdate );
  }


  public boolean connectUser(String p_user)
  {
    // update last connection date for current user
    if( p_user == null )
    {
      return false;
    }
    if( getConnectedUsers() == null )
    {
      setConnectedUsers( new HashSet<ConnectedUser>() );
    }

    boolean isReConnected = false;
    boolean isDone = false;
    for( ConnectedUser connectedUser : getConnectedUsers() )
    {
      if( p_user.equals( connectedUser.getPseudo() ) )
      {
        if( connectedUser.getLastConnexion().getTime() < System.currentTimeMillis()
            - ((FmpConstant.chatConnectionTimeout - FmpConstant.inactiveResfreshingPeriod) * 1000) )
        {
          connectedUser.getLastConnexion().setTime( System.currentTimeMillis() );
          isReConnected = true;
        }
        isDone = true;
        break;
      }
    }
    if( isDone == false )
    {
      ConnectedUser connectedUser = new ConnectedUser( p_user, null );
      EbAccount account = getAccount( p_user );
      if( account != null )
      {
        connectedUser.setId( account.getId() );
      }
      getConnectedUsers().add( connectedUser );
      isDone = true;
      isReConnected = true;
    }
    if( isReConnected )
    {
      getLastUpdate().setTime( System.currentTimeMillis() );
    }
    return isReConnected;
  }

  /**
   * remove a user from connected user to a given game
   * @param p_gameId
   * @param user
   * @return true if a user was disconnected
   */
  public boolean disconnectUser(String p_user)
  {
    if( getConnectedUsers() == null )
    {
      return false;
    }
    assert p_user != null;
    ConnectedUser userToRemove = null;
    for( ConnectedUser connectedUser : getConnectedUsers() )
    {
      if( p_user.equals( connectedUser.getPseudo() ) )
      {
        userToRemove = connectedUser;
        break;
      }
    }
    if( userToRemove != null )
    {
      getConnectedUsers().remove( userToRemove );
      getLastUpdate().setTime( System.currentTimeMillis() );
      return true;
    }
    return false;
  }

  /**
   * 
   * @return true if a user was disconnected
   */
  public boolean disconnectTooOldUser()
  {
    boolean isDisconnected = false;
    Set<ConnectedUser> connectedUsers = getConnectedUsers();
    Set<ConnectedUser> oldUsers = new HashSet<ConnectedUser>();
    // remove too old connected
    long tooOldTime = System.currentTimeMillis() - FmpConstant.chatConnectionTimeout * 1000;
    for( ConnectedUser connectedUser : connectedUsers )
    {
      if( connectedUser.getLastConnexion().getTime() < tooOldTime )
      {
        oldUsers.add( connectedUser );
        isDisconnected = true;
      }
    }
    if( !oldUsers.isEmpty() )
    {
      connectedUsers.removeAll( oldUsers );
      getLastUpdate().setTime( System.currentTimeMillis() );
    }
    return isDisconnected;
  }

  public ConnectedUser getConnectedUser(String p_pseudo)
  {
    assert p_pseudo != null;

    Set<ConnectedUser> connectedUsers = getConnectedUsers();
    for( ConnectedUser connectedUser : connectedUsers )
    {
      if( p_pseudo.equals( connectedUser.getPseudo() ) )
      {
        return connectedUser;
      }
    }
    return null;
  }

  public ConnectedUser getConnectedUser(long p_accountId)
  {
    assert p_accountId != 0;

    Set<ConnectedUser> connectedUsers = getConnectedUsers();
    for( ConnectedUser connectedUser : connectedUsers )
    {
      if( connectedUser.getId() == p_accountId )
      {
        return connectedUser;
      }
    }
    return null;
  }


  /**
   * determine if user 'p_login' is currently viewing game 'm_gameId'
   * @param p_pseudo
   * @return
   */
  public boolean isUserConnected(String p_pseudo)
  {
    return getConnectedUser( p_pseudo ) != null;
  }


  /**
   * @return the gameEvents
   */
  public List<AnEvent> getGameEvents()
  {
    if( m_gameEvents == null )
    {
      m_gameEvents = new ArrayList<AnEvent>();
    }
    return m_gameEvents;
  }

  /**
   * @param p_gameEvents the gameEvents to set
   */
  public void setGameEvents(List<AnEvent> p_gameEvents)
  {
    m_gameEvents = p_gameEvents;
  }

  public void addChatMessages(ChatMessage p_messages)
  {
    getChatMessages().add( p_messages );
    if( p_messages.getDate().after( getLastUpdate() ) )
    {
      getLastUpdate().setTime( p_messages.getDate().getTime() );
    }
  }

  /**
   * @return the chatMessages
   */
  public List<ChatMessage> getChatMessages()
  {
    if( m_chatMessages == null )
    {
      m_chatMessages = new ArrayList<ChatMessage>();
    }
    return m_chatMessages;
  }

  /**
   * @param p_chatMessages the chatMessages to set
   */
  public void setChatMessages(List<ChatMessage> p_chatMessages)
  {
    m_chatMessages = p_chatMessages;
  }

  /**
   * @return the connectedPlayer
   */
  public Set<ConnectedUser> getConnectedUsers()
  {
    if( m_connectedUsers == null )
    {
      m_connectedUsers = new HashSet<ConnectedUser>();
    }
    return m_connectedUsers;
  }

  /**
   * @param p_connectedPlayer the connectedPlayer to set
   */
  public void setConnectedUsers(Set<ConnectedUser> p_connectedUsers)
  {
    m_connectedUsers = p_connectedUsers;
  }

  /**
   * @return the lastUpdate
   */
  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  /**
   * @param p_lastUpdate the lastUpdate to set
   */
  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }


  /**
   * @return the gameId
   */
  public long getGameId()
  {
    return m_gameId;
  }


  /**
   * @param p_gameId the gameId to set
   */
  public void setGameId(long p_gameId)
  {
    m_gameId = p_gameId;
  }

  /**
   * @return the fromUpdate
   */
  public Date getFromUpdate()
  {
    return m_fromUpdate;
  }

  /**
   * @param p_fromUpdate the fromUpdate to set
   */
  public void setFromUpdate(Date p_fromUpdate)
  {
    m_fromUpdate = p_fromUpdate;
  }


  /**
   * @return the accounts
   */
  public Map<Long, EbAccount> getMapAccounts()
  {
    if( m_accounts == null )
    {
      m_accounts = new HashMap<Long, EbAccount>();
    }
    return m_accounts;
  }

  /**
   * @param p_accounts the accounts to set
   */
  public void setMapAccounts(Map<Long, EbAccount> p_accounts)
  {
    m_accounts = p_accounts;
  }


}
