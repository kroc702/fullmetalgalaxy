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

package com.fullmetalgalaxy.model.persist;

/**
 * @author Vincent
 *
 */
public enum PlayerStyle
{
  Sheep, Pacific, Balanced, Aggressive;

  public String getIconUrl()
  {
    switch( this )
    {
    case Sheep:
      return "/images/icons/sheep.png";
    case Pacific:
      return "/images/icons/pacific.png";
    default:
    case Balanced:
      return "/images/icons/balanced.png";
    case Aggressive:
      return "/images/icons/aggressive.png";
    }
  }
}
