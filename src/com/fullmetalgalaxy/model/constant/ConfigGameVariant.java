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
package com.fullmetalgalaxy.model.constant;

import java.util.HashMap;

import com.fullmetalgalaxy.model.EbConfigGameVariant;


public enum ConfigGameVariant
{
  Standard;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static ConfigGameVariant getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }


  public static EbConfigGameVariant getEbConfigGameVariant(ConfigGameVariant p_config)
  {
    return s_configMap.get( p_config );
  }

  private static HashMap<ConfigGameVariant, EbConfigGameVariant> s_configMap = new HashMap<ConfigGameVariant, EbConfigGameVariant>();
  static
  {
    // TODO
    s_configMap.put( Standard, new EbConfigGameVariant() );
  }
}