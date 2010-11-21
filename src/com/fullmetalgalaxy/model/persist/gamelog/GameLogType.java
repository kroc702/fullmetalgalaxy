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
package com.fullmetalgalaxy.model.persist.gamelog;


/**
 * @author Vincent
 *
 */
public enum GameLogType
{
  None,
  AdminTimePause,
  AdminTimePlay,
  EvtConstruct,
  EvtControl,
  EvtControlFreighter,
  EvtFire,
  EvtLand,
  EvtLoad,
  EvtMessage,
  EvtMove,
  EvtPlayerTurn,
  EvtRepair,
  EvtTakeOff,
  EvtTide,
  EvtTimeStep,
  EvtTransfer,
  EvtUnLoad,
  GameJoin,
  EvtCancel,
 EvtChangePlayerOrder, EvtDeployment, AdminBan;

  public boolean isEventUser()
  {
    return isEventPlay() || isEventAdmin() || this == GameJoin;
  }
  
  public boolean isEventPlay()
  {
    switch( this )
    {
    case EvtConstruct:
    case EvtControl:
    case EvtControlFreighter:
    case EvtFire:
    case EvtLand:
    case EvtLoad:
    case EvtMove:
    case EvtRepair:
    case EvtTakeOff:
    case EvtTransfer:
    case EvtUnLoad:
    case EvtDeployment:
      return true;
    }
    return false;
  }
  
  public boolean isEventAdmin()
  {
    switch( this )
    {
    case AdminTimePause:
    case AdminTimePlay:
    case AdminBan:
      return true;
    }
    return false;
  }



}
