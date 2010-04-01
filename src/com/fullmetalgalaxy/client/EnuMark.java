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
package com.fullmetalgalaxy.client;

import com.fullmetalgalaxy.model.MyEnum;

/**
 * @author Kroc
 *
 */
public class EnuMark extends MyEnum
{
  public static final int HiLight = 0;
  public static final int Selection = 1;
  public static final int Foots = 2;
  public static final int Target = 3;
  public static final int DisableWater = 4;
  public static final int DisableFire = 5;
  public static final int Warning = 6;


  public EnuMark(int p_value)
  {
    super( p_value );
  }

  public EnuMark()
  {
    super();
  }

  protected int getMaxValue()
  {
    return Warning;
  }

  public String toString()
  {
    return getUrl();
  }

  public String getUrl()
  {
    switch( getValue() )
    {
    case EnuMark.HiLight:
      return "case_contours.gif";
    case EnuMark.Selection:
      return "selection.gif";
    case EnuMark.Foots:
      return "pas.gif";
    case EnuMark.Target:
      return "cible.gif";
    case EnuMark.DisableWater:
      return "disable_water.gif";
    case EnuMark.DisableFire:
      return "disable_fire.gif";
    case EnuMark.Warning:
      return "warning.gif";
    default:
      return "blank.gif";
    }
  }

}
