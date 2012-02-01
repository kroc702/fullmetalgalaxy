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

package com.fullmetalgalaxy.server;

import com.fullmetalgalaxy.model.persist.StatsPlayer;

/**
 * @author Vincent
 *
 */
public enum PlayerStyle
{
  Mysterious, Sheep, Pacific, Balanced, Aggressive;

  public String getIconUrl()
  {
    switch( this )
    {
    case Mysterious:
      return "/images/icons/mysterious.png";
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


  public static PlayerStyle fromStatsPlayer(StatsPlayer p_stats)
  {
    if( p_stats.getLosedFreighterCount() > p_stats.getFreighterControlCount() )
    {
      return PlayerStyle.Sheep;
    }
    return GlobalVars.getPlayerStyle( p_stats.getStyleRatio() );
  }

  /**
   * @param p_styleRatio
   * @return
   */
  public static PlayerStyle fromStyleRatio(float p_styleRatio)
  {
    return GlobalVars.getPlayerStyle( p_styleRatio );
  }


}
