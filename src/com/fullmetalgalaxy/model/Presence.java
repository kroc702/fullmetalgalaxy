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
package com.fullmetalgalaxy.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author vincent
 * This class represent presence (or connexion) of a single user/page onto a PresenceRoom
 */
public class Presence implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 203;

  private String m_pseudo = "";
  private long m_gameId = 0;
  private int m_pageId = 0;
  private String m_jabberId = null;
  private Date m_lastConnexion = new Date( System.currentTimeMillis() );
  private ClientType m_clientType = ClientType.UNKNOWN;
  
  public enum ClientType implements IsSerializable, java.io.Serializable
  {
    UNKNOWN, GAME, CHAT, XMPP; 
  }

  public Presence()
  {
  }

  public Presence(String p_pseudo, long p_gameId, int p_pageId)
  {
    super();
    m_pseudo = p_pseudo;
    m_gameId = p_gameId;
    m_pageId = p_pageId;
  }

  public void reinit()
  {
    setPseudo( "" );
    setGameId( 0 );
    setPageId( 0 );
    m_jabberId = null;
    setLastConnexion();
  }

  public void setPresence(Presence p_presence)
  {
    setPseudo( p_presence.getPseudo() );
    setGameId( p_presence.getGameId() );
    setPageId( p_presence.getPageId() );
    setLastConnexion( p_presence.getLastConnexion() );
    setClientType( m_clientType );
  }


  public String getAvatarUrl()
  {
    return "/ImageServlet?avatar=" + getPseudo();
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int)(m_gameId ^ (m_gameId >>> 32));
    result = prime * result + ((m_jabberId == null) ? 0 : m_jabberId.hashCode());
    result = prime * result + m_pageId;
    result = prime * result + ((m_pseudo == null) ? 0 : m_pseudo.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    Presence other = (Presence)obj;
    if( m_gameId != other.m_gameId )
      return false;
    if( m_jabberId == null )
    {
      if( other.m_jabberId != null )
        return false;
    }
    else if( !m_jabberId.equals( other.m_jabberId ) )
      return false;
    if( m_pageId != other.m_pageId )
      return false;
    if( m_pseudo == null )
    {
      if( other.m_pseudo != null )
        return false;
    }
    else if( !m_pseudo.equals( other.m_pseudo ) )
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getPseudo() + "[" + getClientType() + "]";
  }

  /**
   * may be referenced as channel key.
   * do not confuse with channel token which is given by channel services.
   * @return a unique id for this presence
   */
  public String getChannelId()
  {
    return getPseudo() + getGameId() + getPageId();
  }

  /**
   * Set last connexion to current time.
   */
  public void setLastConnexion()
  {
    getLastConnexion().setTime( System.currentTimeMillis() );
  }



  public String getPseudo()
  {
    return m_pseudo;
  }

  public void setPseudo(String p_pseudo)
  {
    if(p_pseudo==null) p_pseudo = "";
    m_pseudo = p_pseudo;
  }

  public Date getLastConnexion()
  {
    return m_lastConnexion;
  }

  public void setLastConnexion(Date p_lastConnexion)
  {
    m_lastConnexion = p_lastConnexion;
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
   * @return the pageId
   */
  public int getPageId()
  {
    return m_pageId;
  }

  /**
   * @param p_pageId the pageId to set
   */
  public void setPageId(int p_pageId)
  {
    m_pageId = p_pageId;
  }

  public ClientType getClientType()
  {
    return m_clientType;
  }

  public void setClientType(ClientType p_clientType)
  {
    m_clientType = p_clientType;
  }

  public String getJabberId()
  {
    return m_jabberId;
  }

  public void setJabberId(String p_jabberId)
  {
    if( p_jabberId != null )
    {
      setClientType(ClientType.XMPP);
    }
    m_jabberId = p_jabberId;
  }


}
