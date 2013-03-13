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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.constant;

import java.util.HashMap;

import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbConfigGameVariant;
import com.google.gwt.user.client.rpc.IsSerializable;


public enum ConfigGameVariant implements IsSerializable
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

  public EbConfigGameVariant getEbConfigGameVariant()
  {
    return getEbConfigGameVariant( this );
  }

  private static HashMap<ConfigGameVariant, EbConfigGameVariant> s_configMap = new HashMap<ConfigGameVariant, EbConfigGameVariant>();
  static
  {
    EbConfigGameVariant variantConfig = null;
    
    variantConfig = new EbConfigGameVariant();
    variantConfig.setConstructQty( TokenType.Pontoon, 1 );
    variantConfig.setConstructQty( TokenType.Crab, 1 );
    variantConfig.setConstructQty( TokenType.Tank, 4 );
    variantConfig.setDescription( "l'unique variante" );
    s_configMap.put( Standard, variantConfig );
  }
}
