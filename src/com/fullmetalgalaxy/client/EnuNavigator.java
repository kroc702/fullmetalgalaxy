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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client;

import com.fullmetalgalaxy.model.MyEnum;

/**
 * @author Vincent Legendre
 *
 */
public class EnuNavigator extends MyEnum
{
  private static final long serialVersionUID = 1L;

  public static final int FF = 0;
  public static final int IE = 1;

  /**
   * 
   */
  public EnuNavigator()
  {
  }

  /**
   * @param p_value
   */
  public EnuNavigator(int p_value)
  {
    super( p_value );
  }

  @Override
  protected int getMaxValue()
  {
    return 1;
  }


}
