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
package com.fullmetalgalaxy.client.ressources.tokens;

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Vincent Legendre
 *
 */
public class TokenImages
{

  public static AbstractImagePrototype getTokenImage(EbToken p_token, int p_zoom)
  {
    if( p_token.getLocation() == Location.Graveyard )
    {
      switch( p_zoom )
      {
      default:
      case EnuZoom.Medium:
        return Icons.s_instance.tactic_wreck();
      case EnuZoom.Small:
        return Icons.s_instance.strategy_wreck();
      }
    }
    return getTokenImage( p_token.getEnuColor(), p_zoom, p_token.getType(), p_token.getPosition()
        .getSector() );
  }

  public static AbstractImagePrototype getTokenImage(EnuColor p_color, int p_zoom,
      TokenType p_token, Sector p_sector)
  {
    if( p_color.isSingleColor() )
    {
      switch( p_zoom )
      {
      default:
      case EnuZoom.Medium:
        return getTokenImageTactic( p_color.getValue(), p_token, p_sector );
      case EnuZoom.Small:
        return getTokenImageStrategy( p_color.getValue(), p_token, p_sector );
      }
    }
    else
    {
      // no specific color, it could be an ore or pontoon
      switch( p_zoom )
      {
      default:
      case EnuZoom.Medium:
        return getColorlessTokenImageTactic( p_token, p_sector );
      case EnuZoom.Small:
        return getColorlessTokenImageStrategy( p_token, p_sector );
      }
    }
  }



  protected static AbstractImagePrototype getColorlessTokenImageTactic(TokenType p_token,
      Sector p_sector)
  {
    switch( p_token )
    {
    case Pontoon:
      switch( p_sector )
      {
      case North:
        return Icons.s_instance.tactic_pontoon_n();
      case NorthEast:
        return Icons.s_instance.tactic_pontoon_ne();
      case NorthWest:
        return Icons.s_instance.tactic_pontoon_nw();
      case South:
        return Icons.s_instance.tactic_pontoon_s();
      case SouthEast:
        return Icons.s_instance.tactic_pontoon_se();
      case SouthWest:
        return Icons.s_instance.tactic_pontoon_sw();
      default:
      }
    case Ore:
      switch( p_sector )
      {
      case South:
      case North:
        return Icons.s_instance.tactic_ore_01();
      case NorthEast:
      case SouthEast:
        return Icons.s_instance.tactic_ore_02();
      case NorthWest:
      case SouthWest:
        return Icons.s_instance.tactic_ore_03();
      default:
      }
    default:
      return getTokenImageTactic( EnuColor.None, p_token, p_sector );
    }
  }

  protected static AbstractImagePrototype getColorlessTokenImageStrategy(TokenType p_token,
      Sector p_sector)
  {
    switch( p_token )
    {
    case Pontoon:
      switch( p_sector )
      {
      case North:
        return Icons.s_instance.strategy_pontoon_n();
      case NorthEast:
        return Icons.s_instance.strategy_pontoon_ne();
      case NorthWest:
        return Icons.s_instance.strategy_pontoon_nw();
      case South:
        return Icons.s_instance.strategy_pontoon_s();
      case SouthEast:
        return Icons.s_instance.strategy_pontoon_se();
      case SouthWest:
        return Icons.s_instance.strategy_pontoon_sw();
      default:
      }
    case Ore:
      switch( p_sector )
      {
      case South:
      case North:
        return Icons.s_instance.strategy_ore_01();
      case SouthEast:
      case NorthEast:
        return Icons.s_instance.strategy_ore_02();
      case SouthWest:
      case NorthWest:
        return Icons.s_instance.strategy_ore_03();
      default:
      }
    default:
      return getTokenImageStrategy( EnuColor.None, p_token, p_sector );
    }
  }



  private static Map<Integer, TokenImageBundle> s_bundle = new HashMap<Integer, TokenImageBundle>();
  static
  {
    s_bundle.put( EnuColor.Purple, (Purple)GWT.create( Purple.class ) );
    s_bundle.put( EnuColor.Blue, (Blue)GWT.create( Blue.class ) );
    s_bundle.put( EnuColor.Cyan, (Cyan)GWT.create( Cyan.class ) );
    s_bundle.put( EnuColor.Green, (Green)GWT.create( Green.class ) );
    s_bundle.put( EnuColor.Grey, (Grey)GWT.create( Grey.class ) );
    s_bundle.put( EnuColor.Olive, (Olive)GWT.create( Olive.class ) );
    s_bundle.put( EnuColor.Orange, (Orange)GWT.create( Orange.class ) );
    s_bundle.put( EnuColor.Red, (Red)GWT.create( Red.class ) );
    s_bundle.put( EnuColor.Yellow, (Yellow)GWT.create( Yellow.class ) );
    s_bundle.put( EnuColor.None, (Colorless)GWT.create( Colorless.class ) );
  }

  private static Map<Integer, TokenImageFreighterBundle> s_bundleFreighter = new HashMap<Integer, TokenImageFreighterBundle>();
  static
  {
    s_bundleFreighter.put( EnuColor.Purple, (PurpleFreighter)GWT.create( PurpleFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Blue, (BlueFreighter)GWT.create( BlueFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Cyan, (CyanFreighter)GWT.create( CyanFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Green, (GreenFreighter)GWT.create( GreenFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Grey, (GreyFreighter)GWT.create( GreyFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Olive, (OliveFreighter)GWT.create( OliveFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Orange, (OrangeFreighter)GWT.create( OrangeFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Red, (RedFreighter)GWT.create( RedFreighter.class ) );
    s_bundleFreighter.put( EnuColor.Yellow, (YellowFreighter)GWT.create( YellowFreighter.class ) );
    s_bundleFreighter
        .put( EnuColor.None, (ColorlessFreighter)GWT.create( ColorlessFreighter.class ) );
  }



  protected static AbstractImagePrototype getTokenImageTactic(int p_color, TokenType p_token,
      Sector p_sector)
  {
    switch( p_token )
    {
    case Barge:
      return getTokenImageTacticBarge( s_bundle.get( p_color ), p_sector );
    case Crab:
      return getTokenImageTacticCrab( s_bundle.get( p_color ), p_sector );
    case Freighter:
      return getTokenImageTacticFreighter( s_bundleFreighter.get( p_color ), p_sector );
    case Heap:
      return getTokenImageTacticHeap( s_bundle.get( p_color ), p_sector );
    case Speedboat:
      return getTokenImageTacticSpeedBoat( s_bundle.get( p_color ), p_sector );
    case Tank:
      return getTokenImageTacticTank( s_bundle.get( p_color ), p_sector );
    default:
    case Turret:
      return getTokenImageTacticTurret( s_bundle.get( p_color ), p_sector );
    case WeatherHen:
      return getTokenImageTacticWeatherHen( s_bundle.get( p_color ), p_sector );
    }
  }

  protected static AbstractImagePrototype getTokenImageStrategy(int p_color, TokenType p_token,
      Sector p_sector)
  {
    switch( p_token )
    {
    case Barge:
      return getTokenImageStrategyBarge( s_bundle.get( p_color ), p_sector );
    case Crab:
      return getTokenImageStrategyCrab( s_bundle.get( p_color ), p_sector );
    case Freighter:
      return getTokenImageStrategyFreighter( s_bundleFreighter.get( p_color ), p_sector );
    case Heap:
      return getTokenImageStrategyHeap( s_bundle.get( p_color ), p_sector );
    case Speedboat:
      return getTokenImageStrategySpeedBoat( s_bundle.get( p_color ), p_sector );
    case Tank:
      return getTokenImageStrategyTank( s_bundle.get( p_color ), p_sector );
    default:
    case Turret:
      return getTokenImageStrategyTurret( s_bundle.get( p_color ), p_sector );
    case WeatherHen:
      return getTokenImageStrategyWeatherHen( s_bundle.get( p_color ), p_sector );
    }
  }

  protected static AbstractImagePrototype getTokenImageStrategyBarge(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_barge_n();
    case NorthEast:
      return p_bundle.strategy_barge_ne();
    case NorthWest:
      return p_bundle.strategy_barge_nw();
    case South:
      return p_bundle.strategy_barge_s();
    case SouthEast:
      return p_bundle.strategy_barge_se();
    case SouthWest:
      return p_bundle.strategy_barge_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyCrab(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_crab_n();
    case NorthEast:
      return p_bundle.strategy_crab_ne();
    case NorthWest:
      return p_bundle.strategy_crab_nw();
    case South:
      return p_bundle.strategy_crab_s();
    case SouthEast:
      return p_bundle.strategy_crab_se();
    case SouthWest:
      return p_bundle.strategy_crab_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyFreighter(
      TokenImageFreighterBundle p_bundle, Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_freighter_n();
    case NorthEast:
      return p_bundle.strategy_freighter_ne();
    case NorthWest:
      return p_bundle.strategy_freighter_nw();
    case South:
      return p_bundle.strategy_freighter_s();
    case SouthEast:
      return p_bundle.strategy_freighter_se();
    case SouthWest:
      return p_bundle.strategy_freighter_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyHeap(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_heap_n();
    case NorthEast:
      return p_bundle.strategy_heap_ne();
    case NorthWest:
      return p_bundle.strategy_heap_nw();
    case South:
      return p_bundle.strategy_heap_s();
    case SouthEast:
      return p_bundle.strategy_heap_se();
    case SouthWest:
      return p_bundle.strategy_heap_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategySpeedBoat(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_speedboat_n();
    case NorthEast:
      return p_bundle.strategy_speedboat_ne();
    case NorthWest:
      return p_bundle.strategy_speedboat_nw();
    case South:
      return p_bundle.strategy_speedboat_s();
    case SouthEast:
      return p_bundle.strategy_speedboat_se();
    case SouthWest:
      return p_bundle.strategy_speedboat_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyTank(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_tank_n();
    case NorthEast:
      return p_bundle.strategy_tank_ne();
    case NorthWest:
      return p_bundle.strategy_tank_nw();
    case South:
      return p_bundle.strategy_tank_s();
    case SouthEast:
      return p_bundle.strategy_tank_se();
    case SouthWest:
      return p_bundle.strategy_tank_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyTurret(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_turret_n();
    case NorthEast:
      return p_bundle.strategy_turret_ne();
    case NorthWest:
      return p_bundle.strategy_turret_nw();
    case South:
      return p_bundle.strategy_turret_s();
    case SouthEast:
      return p_bundle.strategy_turret_se();
    case SouthWest:
      return p_bundle.strategy_turret_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageStrategyWeatherHen(
      TokenImageBundle p_bundle, Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.strategy_weatherhen_n();
    case NorthEast:
      return p_bundle.strategy_weatherhen_ne();
    case NorthWest:
      return p_bundle.strategy_weatherhen_nw();
    case South:
      return p_bundle.strategy_weatherhen_s();
    case SouthEast:
      return p_bundle.strategy_weatherhen_se();
    case SouthWest:
      return p_bundle.strategy_weatherhen_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticBarge(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_barge_n();
    case NorthEast:
      return p_bundle.tactic_barge_ne();
    case NorthWest:
      return p_bundle.tactic_barge_nw();
    case South:
      return p_bundle.tactic_barge_s();
    case SouthEast:
      return p_bundle.tactic_barge_se();
    case SouthWest:
      return p_bundle.tactic_barge_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticCrab(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_crab_n();
    case NorthEast:
      return p_bundle.tactic_crab_ne();
    case NorthWest:
      return p_bundle.tactic_crab_nw();
    case South:
      return p_bundle.tactic_crab_s();
    case SouthEast:
      return p_bundle.tactic_crab_se();
    case SouthWest:
      return p_bundle.tactic_crab_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticFreighter(
      TokenImageFreighterBundle p_bundle, Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_freighter_n();
    case NorthEast:
      return p_bundle.tactic_freighter_ne();
    case NorthWest:
      return p_bundle.tactic_freighter_nw();
    case South:
      return p_bundle.tactic_freighter_s();
    case SouthEast:
      return p_bundle.tactic_freighter_se();
    case SouthWest:
      return p_bundle.tactic_freighter_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticHeap(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_heap_n();
    case NorthEast:
      return p_bundle.tactic_heap_ne();
    case NorthWest:
      return p_bundle.tactic_heap_nw();
    case South:
      return p_bundle.tactic_heap_s();
    case SouthEast:
      return p_bundle.tactic_heap_se();
    case SouthWest:
      return p_bundle.tactic_heap_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticSpeedBoat(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_speedboat_n();
    case NorthEast:
      return p_bundle.tactic_speedboat_ne();
    case NorthWest:
      return p_bundle.tactic_speedboat_nw();
    case South:
      return p_bundle.tactic_speedboat_s();
    case SouthEast:
      return p_bundle.tactic_speedboat_se();
    case SouthWest:
      return p_bundle.tactic_speedboat_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticTank(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_tank_n();
    case NorthEast:
      return p_bundle.tactic_tank_ne();
    case NorthWest:
      return p_bundle.tactic_tank_nw();
    case South:
      return p_bundle.tactic_tank_s();
    case SouthEast:
      return p_bundle.tactic_tank_se();
    case SouthWest:
      return p_bundle.tactic_tank_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticTurret(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_turret_n();
    case NorthEast:
      return p_bundle.tactic_turret_ne();
    case NorthWest:
      return p_bundle.tactic_turret_nw();
    case South:
      return p_bundle.tactic_turret_s();
    case SouthEast:
      return p_bundle.tactic_turret_se();
    case SouthWest:
      return p_bundle.tactic_turret_sw();
    }
  }


  protected static AbstractImagePrototype getTokenImageTacticWeatherHen(TokenImageBundle p_bundle,
      Sector p_sector)
  {
    switch( p_sector )
    {
    default:
    case North:
      return p_bundle.tactic_weatherhen_n();
    case NorthEast:
      return p_bundle.tactic_weatherhen_ne();
    case NorthWest:
      return p_bundle.tactic_weatherhen_nw();
    case South:
      return p_bundle.tactic_weatherhen_s();
    case SouthEast:
      return p_bundle.tactic_weatherhen_se();
    case SouthWest:
      return p_bundle.tactic_weatherhen_sw();
    }
  }

}
