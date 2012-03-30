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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 * used for research.
 */
public enum GameStatus implements java.io.Serializable, IsSerializable
{
  Unknown,
  // multiplayer games status
  Open, Pause, Running, Aborted, History,
  // other game type
  Puzzle, Scenario,
  //Practice is similar to Puzzle mode but for game that are originally
  // Multiplayer
  // -> some more restriction
  Practice;
  
  
  private String getIconFileName()
  {
    switch( this )
    {
    case Open:
      return "/images/icons/open16.png";
    case Aborted:
      return "/images/icons/canceled16.png";
    case Pause:
      return "/images/icons/pause16.png";
    case History:
      return "/images/icons/history16.png";
    default:
    case Scenario:
    case Practice:
    case Puzzle:
      return "/images/icons/puzzle16.png";
    case Running:
      return "/images/icons/running16.png";
    }
  }
  
  public String getIconAsHtml()
  {
    return "<image src='"+getIconFileName()+"' />";
  }
  
  

}
