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

import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 * All data client need to load a games
 * 
 * TODO get rid of this class
 */
public class ModelFmpInit implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 203;

  private EbGame m_game = null;

  private Game game = null;

  private PresenceRoom m_presenceRoom = null;



  /**
   * @return the game
   */
  public Game getGame()
  {
    if( game == null && m_game != null )
    {
      game = m_game.createGame();
      m_game = null;
    }
    return game;
  }

  /**
   * @param p_game the game to set
   */
  public void setGame(Game p_game)
  {
    game = p_game;
  }


  /**
   * @return the presenceRoom
   */
  public PresenceRoom getPresenceRoom()
  {
    return m_presenceRoom;
  }

  /**
   * @param p_presenceRoom the presenceRoom to set
   */
  public void setPresenceRoom(PresenceRoom p_presenceRoom)
  {
    m_presenceRoom = p_presenceRoom;
  }



}
