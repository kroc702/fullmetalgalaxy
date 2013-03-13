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

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
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

  private String m_fromPseudo = "";
  private int m_fromPageId = 0;

  private long m_fromVersion = 0;
  // this field is used only from server to client, because client can't change
  // game version.
  private long m_toVersion = 0;


  public ModelFmpUpdate()
  {
  }

  public ModelFmpUpdate(Game p_game)
  {
    setGameId( p_game.getId() );
    setFromVersion( p_game.getVersion() );
  }


  /**
   * search for the first UserEvent and return his account id
   * @return
   */
  public long getAccountId()
  {
    for( AnEvent event : getGameEvents() )
    {
      if( event instanceof AnEventUser )
      {
        return ((AnEventUser)event).getAccountId();
      }
    }
    return 0;
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
   * @return the fromVersion
   */
  public long getFromVersion()
  {
    return m_fromVersion;
  }



  /**
   * @param p_fromVersion the fromVersion to set
   */
  public void setFromVersion(long p_fromVersion)
  {
    m_fromVersion = p_fromVersion;
  }



  public long getToVersion()
  {
    return m_toVersion;
  }

  public void setToVersion(long p_toVersion)
  {
    m_toVersion = p_toVersion;
  }

  public String getFromPseudo()
  {
    return m_fromPseudo;
  }

  public void setFromPseudo(String p_fromPseudo)
  {
    m_fromPseudo = p_fromPseudo;
  }

  public int getFromPageId()
  {
    return m_fromPageId;
  }


  public void setFromPageId(int p_fromPageId)
  {
    m_fromPageId = p_fromPageId;
  }


}
