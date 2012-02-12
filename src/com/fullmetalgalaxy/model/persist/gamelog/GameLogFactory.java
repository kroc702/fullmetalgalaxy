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
package com.fullmetalgalaxy.model.persist.gamelog;


/**
 * @author vincent
 *
 */
public class GameLogFactory
{
  static public AnEvent newAdminAbort(long p_accountId)
  {
    EbAdminAbort event = new EbAdminAbort();
    event.setAccountId( p_accountId );
    return event;
  }

  static public AnEvent newAdminTimePause(long p_accountId)
  {
    EbAdminTimePause event = new EbAdminTimePause();
    event.setAccountId( p_accountId );
    return event;
  }

  static public AnEvent newAdminTimePlay(long p_accountId)
  {
    EbAdminTimePlay event = new EbAdminTimePlay();
    event.setAccountId( p_accountId );
    return event;
  }

}
