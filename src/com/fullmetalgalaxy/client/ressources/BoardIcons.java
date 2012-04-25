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
package com.fullmetalgalaxy.client.ressources;


import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Vincent Legendre
 *
 */
public class BoardIcons
{

  public static AbstractImagePrototype iconTide(Tide p_tide)
  {
    switch( p_tide )
    {
    case Low:
      return Icons.s_instance.tide_low();
    case Medium:
      return Icons.s_instance.tide_medium();
    case Hight:
      return Icons.s_instance.tide_hight();
    default:
    case Unknown:
      return Icons.s_instance.tide_unknown();
    }
  }


  public static AbstractImagePrototype iconLoad(int p_zoom, int p_loadSize)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      switch( p_loadSize )
      {
      case 1:
        return Icons.s_instance.tactic_icon_load1();
      case 2:
        return Icons.s_instance.tactic_icon_load2();
      case 3:
        return Icons.s_instance.tactic_icon_load3();
      case 4:
        return Icons.s_instance.tactic_icon_load4();
      default:
        return null;
      }
    case EnuZoom.Small:
      switch( p_loadSize )
      {
      case 1:
        return Icons.s_instance.strategy_icon_load1();
      case 2:
        return Icons.s_instance.strategy_icon_load2();
      case 3:
        return Icons.s_instance.strategy_icon_load3();
      case 4:
        return Icons.s_instance.strategy_icon_load4();
      default:
        return null;
      }
    }
}

  public static AbstractImagePrototype iconBullet(int p_zoom, int p_bulletCount)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      switch( p_bulletCount )
      {
      case 0:
        return null;
      case 1:
        return Icons.s_instance.tactic_icon_bullet1();
      default:
      case 2:
        return Icons.s_instance.tactic_icon_bullet2();
      }
    case EnuZoom.Small:
      switch( p_bulletCount )
      {
      case 0:
        return null;
      case 1:
        return Icons.s_instance.strategy_icon_bullet1();
      default:
      case 2:
        return Icons.s_instance.strategy_icon_bullet2();
      }
    }
  }

  public static AbstractImagePrototype hightlight_hexagon(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_hightlight_hexagon();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_hightlight_hexagon();
    }
  }

  public static AbstractImagePrototype icon16(int p_colorValue)
  {
    switch( p_colorValue )
    {
    default:
    case EnuColor.Blue:
      return Icons.s_instance.blue_icon16();
    case EnuColor.Cyan:
      return Icons.s_instance.cyan_icon16();
    case EnuColor.Green:
      return Icons.s_instance.green_icon16();
    case EnuColor.Grey:
      return Icons.s_instance.grey_icon16();
    case EnuColor.Olive:
      return Icons.s_instance.olive_icon16();
    case EnuColor.Orange:
      return Icons.s_instance.orange_icon16();
    case EnuColor.Purple:
      return Icons.s_instance.purple_icon16();
    case EnuColor.Red:
      return Icons.s_instance.red_icon16();
    case EnuColor.Yellow:
      return Icons.s_instance.yellow_icon16();
    }
  }

  public static AbstractImagePrototype icon64(int p_colorValue)
  {
    switch( p_colorValue )
    {
    default:
    case EnuColor.Blue:
      return Icons.s_instance.blue_icon64();
    case EnuColor.Cyan:
      return Icons.s_instance.cyan_icon64();
    case EnuColor.Green:
      return Icons.s_instance.green_icon64();
    case EnuColor.Grey:
      return Icons.s_instance.grey_icon64();
    case EnuColor.Olive:
      return Icons.s_instance.olive_icon64();
    case EnuColor.Orange:
      return Icons.s_instance.orange_icon64();
    case EnuColor.Purple:
      return Icons.s_instance.purple_icon64();
    case EnuColor.Red:
      return Icons.s_instance.red_icon64();
    case EnuColor.Yellow:
      return Icons.s_instance.yellow_icon64();
    }
  }

  public static AbstractImagePrototype deployment4(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_deployment4();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_deployment4();
    }
  }

  public static AbstractImagePrototype select_hexagon(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_select_hexagon();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_select_hexagon();
    }
  }

  public static AbstractImagePrototype target_control(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_target_control();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_target_control();
    }
  }

  public static AbstractImagePrototype target(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_target();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_target();
    }
  }

  public static AbstractImagePrototype arrow(int p_zoom, Sector p_sector)
  {
    if( p_zoom == EnuZoom.Medium )
    {
      switch( p_sector )
      {
      case North:
        return Icons.s_instance.tactic_arrow_n();
      case NorthEast:
        return Icons.s_instance.tactic_arrow_ne();
      case SouthEast:
        return Icons.s_instance.tactic_arrow_se();
      case South:
        return Icons.s_instance.tactic_arrow_s();
      case SouthWest:
        return Icons.s_instance.tactic_arrow_sw();
      case NorthWest:
        return Icons.s_instance.tactic_arrow_nw();
      }
    }
    else
    {
      switch( p_sector )
      {
      case North:
        return Icons.s_instance.strategy_arrow_n();
      case NorthEast:
        return Icons.s_instance.strategy_arrow_ne();
      case SouthEast:
        return Icons.s_instance.strategy_arrow_se();
      case South:
        return Icons.s_instance.strategy_arrow_s();
      case SouthWest:
        return Icons.s_instance.strategy_arrow_sw();
      case NorthWest:
        return Icons.s_instance.strategy_arrow_nw();
      }
    }
    return Icons.s_instance.strategy_arrow_n();
  }


  public static AbstractImagePrototype warning(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_warning();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_warning();
    }
  }

  public static AbstractImagePrototype disable_fire(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_disable_fire();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_disable_fire();
    }
  }

  public static AbstractImagePrototype disabling_fire(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_disabling_fire();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_disabling_fire();
    }
  }

  public static AbstractImagePrototype disable_water(int p_zoom)
  {
    switch( p_zoom )
    {
    default:
    case EnuZoom.Medium:
      return Icons.s_instance.tactic_disable_water();
    case EnuZoom.Small:
      return Icons.s_instance.strategy_disable_water();
    }
  }



}
