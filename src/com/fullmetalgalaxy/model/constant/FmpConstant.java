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
import java.util.Map;
import java.util.Map.Entry;

import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.TokenType;



public class FmpConstant
{
  public static final long currentStatsTimeWindowInMillis = 1000*60*60*24*30*18; // about 18 months  
  
  public static final int maximumActionPtWithoutLanding = 10;

  public static final int parallelLockRadius = 6;
  public static final int minSpaceBetweenFreighter = 9;
  public static final int deployementRadius = 3;

  // in second
  public static final int clientMessagesLivePeriod = 15;

  public static final int miniMapWidth = 240;
  public static final int miniMapHeight = 160;

  // public static final int imageHexWidth = 70;
  // public static final int imageHexHeight = 61;

  /**
   * build a new map that represent the default common construction reserve
   * @param p_playerCount
   * @return
   */
  public static Map<TokenType, Integer> getDefaultReserve(int p_playerCount)
  {
    Map<TokenType, Integer> constructReserve = new HashMap<TokenType, Integer>();
    // build default construct reserve
    constructReserve.put( TokenType.Pontoon, 1 );
    constructReserve.put( TokenType.Crab, 1 );
    constructReserve.put( TokenType.Tank, 4 );
    for( Entry<TokenType, Integer> entry : constructReserve.entrySet() )
    {
      entry.setValue( entry.getValue() * p_playerCount );
    }
    return constructReserve;
  }

  /**
   * build a new map that represent the default freighter initial holds during landing
   * @return
   */
  public static Map<TokenType, Integer> getDefaultInitialHolds()
  {
    Map<TokenType, Integer> initialHold = new HashMap<TokenType, Integer>();
    // build default construct reserve
    initialHold.put( TokenType.Pontoon, 1 );
    initialHold.put( TokenType.Crab, 1 );
    initialHold.put( TokenType.Tank, 4 );
    initialHold.put( TokenType.Heap, 1 );
    initialHold.put( TokenType.Speedboat, 2 );
    initialHold.put( TokenType.Barge, 1 );
    initialHold.put( TokenType.WeatherHen, 1 );
    initialHold.put( TokenType.Turret, 3 );
    return initialHold;
  }

  public static String getBaseUrl()
  {
    return "";// http://fullmetalgalaxy.com";
  }

  public static String getForumHost()
  {
    return "fullmetalplanete.forum2jeux.com";
  }

  public static String getForumUrl()
  {
    return "http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy";
  }


  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexWidth(EnuZoom p_zoom)
  {
    return getHexWidth( p_zoom.getValue() );
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexWidth(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 34;// 30;
    default:
    case EnuZoom.Medium:
      return 76;// 50;
    }
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexHeight(EnuZoom p_zoom)
  {
    return getHexHeight( p_zoom.getValue() );
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexHeight(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 29;// 26;
    default:
    case EnuZoom.Medium:
      return 40;// 44;
    }
  }


}
