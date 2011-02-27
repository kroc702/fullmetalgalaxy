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

/**
 * @author Kroc
 *
 */
public enum ActionType
{
  /** no action */
  None,
  /** it's not a real action, just a first step during action construction */
  Selected,
  /** games actions */
  Move, Landing, Fire, Control, TakeOff, Unload, EndTurn, RepairTurret,
  /** game admin action */
  Pause, Play;

  public boolean isAdminAction()
  {
    switch( this )
    {
    case Pause:
    case Play:
      return true;
    default:
      return false;
    }
  }

  public boolean isGameAction()
  {
    switch( this )
    {
    case Move:
    case Landing:
    case Fire:
    case Control:
    case TakeOff:
    case Unload:
    case EndTurn:
    case RepairTurret:
      return true;
    default:
      return false;
    }
  }



}
