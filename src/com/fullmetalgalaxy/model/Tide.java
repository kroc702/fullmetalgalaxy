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
 * @author Kroc
 *
 * I known there is a misspelling in enum name... but changing that cause issue in loading database
 */
public enum Tide implements IsSerializable
{

  Unknown, Low, Medium, Hight;


  public static Tide getRandom(int p_averageLevel)
  {
    if( p_averageLevel > 2 )
    {
      switch( (int)Math.floor( Math.random() * 2 ) )
      {
      case 0:
        return Low;
      default:
      case 1:
        return Medium;
      }
    }
    else if( p_averageLevel < -2 )
    {
      switch( (int)Math.floor( Math.random() * 2 ) )
      {
      case 0:
        return Medium;
      default:
      case 1:
        return Hight;
      }
    }
    switch( (int)Math.floor( Math.random() * 3 ) )
    {
    case 0:
      return Low;
    default:
    case 1:
      return Medium;
    case 2:
      return Hight;
    }
  }

  public int getLevel()
  {
    switch( this )
    {
    case Low:
      return -1;
    case Hight:
      return 1;
    default:
      return 0;
    }

  }

}
