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
package com.fullmetalgalaxy.model;


/**
 * @author LEG88888
 *
 */
public class EnuZoom extends MyEnum
{
  static final long serialVersionUID = 16;

  public static final int Small = 0;
  public static final int Medium = 1;
  public static final int Large = 2;


  public EnuZoom(int p_value)
  {
    super( p_value );
  }

  public EnuZoom()
  {
    super();
  }

  @Override
  protected int getMaxValue()
  {
    return 2;
  }

  @Override
  public String toString()
  {
    switch( getValue() )
    {
    case EnuZoom.Small:
      return "strategy";
    case EnuZoom.Medium:
      return "tactic";
    case EnuZoom.Large:
      return "large";
    case EnuZoom.Unknown:
    default:
      return super.toString();
    }
  }

  public String getUrl()
  {
    return "";
  }
}
