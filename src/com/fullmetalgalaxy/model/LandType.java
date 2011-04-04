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
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;



/**
 * 
 */

/**
 * @author LEG88888
 *
 */
public enum LandType implements IsSerializable
{
  /**
   * Theses values are store as is in data base.
   * TODO do something to be sure that values are 0..5
   */
  None, Sea, Reef, Marsh, Plain, Montain;


  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static LandType getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
    /* This code is safer, but slower...
    LandType[] values = values();
    for( int i = 0; i < values.length; i++ )
    {
      if( p_value == values[i].ordinal() )
      {
        return values[i];
      }
    }
    return None;*/
  }

  public static LandType getRandom()
  {
    return getFromOrdinal( (int)Math.floor( Math.random() * values().length ) );
  }

  /**
   * return land value according to a tide. It's mean that this method convert 'Reef' and 'Marsh' into
   * 'Plain' or 'Sea' according to tide.
   * @param p_tide
   * @return
   */
  public LandType getLandValue(Tide p_tide)
  {
    LandType landValue = this;
    switch( p_tide )
    {
    case Low:
      if( (landValue == LandType.Marsh) || (landValue == LandType.Reef) )
      {
        landValue = LandType.Plain;
      }
      break;
    case Hight:
      if( (landValue == LandType.Marsh) || (landValue == LandType.Reef) )
      {
        landValue = LandType.Sea;
      }
      break;
    case Medium:
    default:
      if( landValue == LandType.Marsh )
      {
        landValue = LandType.Plain;
      }
      if( landValue == LandType.Reef )
      {
        landValue = LandType.Sea;
      }
      break;
    }
    return landValue;
  }



  public String getImageName()
  {
    return getImageName( Tide.Medium );
  }


  public String getImageName(Tide p_tide)
  {
    switch( this )
    {
    case Sea:
      return "sea.png";
    case Reef:
      if( p_tide == Tide.Low )
      {
        return "reef_low.png";
      }
      else
      {
        return "reef_hight.png";
      }
    case Marsh:
      if( p_tide == Tide.Hight )
      {
        return "swamp_hight.png";
      }
      else
      {
        return "swamp_low.png";
      }
    case Plain:
      return "plain.png";
    case Montain:
      return "montain.png";
    case None:
    default:
      return "grid.gif";
    }
  }

}
