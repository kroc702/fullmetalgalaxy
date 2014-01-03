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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 * these enum represent different kind of landscape graphism
 */
public enum PlanetType implements IsSerializable
{
  Desert, Grass, Ice, Lava;

  public String getFolderName()
  {
    switch( this )
    {
    default:
    case Desert:
      return "desert";
    case Grass:
      return "grass";
    case Ice:
      return "ice";
    case Lava:
      return "lava";
    }
  }
}
