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

import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.Game;

/**
 * @author Vincent Legendre
 *
 */
public enum MapSize
{
  Small, Medium, Large, Custom;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static MapSize getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }

  private static MapSize getFromGame(EbGamePreview p_game, int p_hexCount)
  {
    int diffSmall = Math.abs( Small.getHexagonPerPlayer() * p_game.getMaxNumberOfPlayer() - p_hexCount );
    int diffMedium = Math.abs( Medium.getHexagonPerPlayer() * p_game.getMaxNumberOfPlayer() - p_hexCount );
    int diffLarge = Math.abs( Large.getHexagonPerPlayer() * p_game.getMaxNumberOfPlayer() - p_hexCount );
    
    if( diffSmall < diffMedium && diffSmall < diffLarge )
    {
      return Small;
    }
    if( diffLarge < diffMedium && diffLarge < diffSmall  )
    {
      return Large;
    }
    return Medium;
  }

  public static MapSize getFromGame(EbGamePreview p_game)
  {
    int hexCount = p_game.getNumberOfHexagon(); 
    return getFromGame( p_game, hexCount );
  }

  public static MapSize getFromGame(Game p_game)
  {
    return getFromGame( p_game.getPreview(), p_game.getNumberOfHexagon() ); 
  }

  public int getHexagonPerPlayer()
  {
    switch( this )
    {
    case Small:
      return 180;
    default:
    case Medium:
      return 213;
    case Large:
      return 283;
    }
  }

  public String getIconAsHtml()
  {
    switch( this )
    {
    case Small:
      return "<img src='/images/icons/small16.png' title='small' /> ";
    default:
    case Medium:
      return "<img src='/images/icons/normal16.png' title='medium' /> ";
    case Large:
      return "<img src='/images/icons/big16.png' title='large' /> ";
    }
  }
}
