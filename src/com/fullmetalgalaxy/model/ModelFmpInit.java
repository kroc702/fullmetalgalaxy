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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbGame;

/**
 * @author Vincent Legendre
 * All data client need to load a games
 */
public class ModelFmpInit implements java.io.Serializable
{
  static final long serialVersionUID = 203;

  private EbGame m_game = null;

  private Set<ConnectedUser> m_connectedUsers = null;

  private Map<Long, EbAccount> m_accounts = null;


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



  /**
   * @return the game
   */
  public EbGame getGame()
  {
    return m_game;
  }

  /**
   * @param p_game the game to set
   */
  public void setGame(EbGame p_game)
  {
    m_game = p_game;
  }

  /**
   * @return the connectedPlayer
   */
  public Set<ConnectedUser> getConnectedUsers()
  {
    return m_connectedUsers;
  }

  /**
   * @param p_connectedPlayer the connectedPlayer to set
   */
  public void setConnectedUsers(Set<ConnectedUser> p_connectedUsers)
  {
    m_connectedUsers = p_connectedUsers;
  }


}
