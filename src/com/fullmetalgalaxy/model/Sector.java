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
package com.fullmetalgalaxy.model;

/**
 * @author Vincent Legendre
 *
 */
public enum Sector
{
  North, NorthEast, SouthEast, South, SouthWest, NorthWest;


  public Sector getOposite()
  {
    switch( this )
    {
    default:
    case North:
      return South;
    case NorthEast:
      return SouthWest;
    case SouthEast:
      return NorthWest;
    case South:
      return North;
    case SouthWest:
      return NorthEast;
    case NorthWest:
      return SouthEast;
    }
  }

  public Sector getNext()
  {
    int index = ordinal() + 1;
    if( index >= values().length )
    {
      index = 0;
    }
    return values()[index];
  }

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static Sector getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }

  public static Sector getRandom()
  {
    return getFromOrdinal( (int)Math.floor( Math.random() * values().length ) );
  }

}
