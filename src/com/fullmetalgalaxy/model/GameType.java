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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 * 
 * TODO should be merge with GameStatus
 */
public enum GameType implements java.io.Serializable, IsSerializable
{
  // Standard game
  MultiPlayer,
  // Initiation is similar to MultiPlayer but creator have all admin rights
  // and doesn't count for ranking
  Initiation,
  // Game is loaded once and never modified on server. player is alone.
  // in Puzzle game, m_currentPlayerRegistration indicate which registration
  // player have to play.
  Puzzle,
  // Scenario can't be played at all. it's only a game template.
  Scenario,
  // Practice is similar to Puzzle mode but for game that are originally
  // Multiplayer
  // -> some more restriction
  Practice;


  private String getIconFileName()
  {
    switch( this )
    {
    default:
    case MultiPlayer:
      return "";
    case Initiation:
      return "/images/icons/initiation16.png";
    case Scenario:
    case Practice:
    case Puzzle:
      return "/images/icons/puzzle16.png";
    }
  }

  public String getIconAsHtml()
  {
    if( getIconFileName().isEmpty() )
    {
      return "";
    }
    return "<image src='" + getIconFileName() + "' />";
  }


}
