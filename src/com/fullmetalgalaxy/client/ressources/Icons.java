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
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * @author Vincent Legendre
 * image contained by folder icons. for user interface
 */
public interface Icons extends ImageBundle
{
  public static Icons s_instance = (Icons)GWT.create( Icons.class );

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/loading.png")
  public AbstractImagePrototype loading();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/logout.gif")
  public AbstractImagePrototype logout();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/login.gif")
  public AbstractImagePrototype login();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/planet/desert/medium.png")
  public AbstractImagePrototype desert_planet_medium();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Pause32.png")
  public AbstractImagePrototype pause32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Play32.png")
  public AbstractImagePrototype play32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/FastPlay32.png")
  public AbstractImagePrototype fastPlay32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Back32.png")
  public AbstractImagePrototype back32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/FastBack32.png")
  public AbstractImagePrototype fastBack32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Winner32.png")
  public AbstractImagePrototype winner32();


  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Cancel32.png")
  public AbstractImagePrototype cancel32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Control32.png")
  public AbstractImagePrototype control32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Info32.png")
  public AbstractImagePrototype info32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Ok32.png")
  public AbstractImagePrototype ok32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Repair32.png")
  public AbstractImagePrototype repair32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Shoot32.png")
  public AbstractImagePrototype shoot32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/ZoomIn32.png")
  public AbstractImagePrototype zoomIn32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/ZoomOut32.png")
  public AbstractImagePrototype zoomOut32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Grid32.png")
  public AbstractImagePrototype grid32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/EndTurn32.png")
  public AbstractImagePrototype endTurn32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/FireCoverOn32.png")
  public AbstractImagePrototype fireCoverOn32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/FireCoverOff32.png")
  public AbstractImagePrototype fireCoverOff32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Register32.png")
  public AbstractImagePrototype register32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/TakeOff32.png")
  public AbstractImagePrototype takeOff32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Player32.png")
  public AbstractImagePrototype player32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Map32.png")
  public AbstractImagePrototype map32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Action16.png")
  public AbstractImagePrototype action16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Char16.png")
  public AbstractImagePrototype char16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Ore16.png")
  public AbstractImagePrototype ore16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Time16.png")
  public AbstractImagePrototype time16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/Time32.png")
  public AbstractImagePrototype time32();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/TakeOff16.png")
  public AbstractImagePrototype takeOff16();


  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/moon16.png")
  public AbstractImagePrototype moon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tide_low.png")
  public AbstractImagePrototype tide_low();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tide_medium.png")
  public AbstractImagePrototype tide_medium();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tide_hight.png")
  public AbstractImagePrototype tide_hight();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tide_unknown.png")
  public AbstractImagePrototype tide_unknown();



  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/blue/icon16.png")
  public AbstractImagePrototype blue_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/cyan/icon16.png")
  public AbstractImagePrototype cyan_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/green/icon16.png")
  public AbstractImagePrototype green_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/grey/icon16.png")
  public AbstractImagePrototype grey_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/olive/icon16.png")
  public AbstractImagePrototype olive_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/orange/icon16.png")
  public AbstractImagePrototype orange_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/purple/icon16.png")
  public AbstractImagePrototype purple_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/red/icon16.png")
  public AbstractImagePrototype red_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/yellow/icon16.png")
  public AbstractImagePrototype yellow_icon16();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/blue/icon48.png")
  public AbstractImagePrototype blue_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/cyan/icon48.png")
  public AbstractImagePrototype cyan_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/green/icon48.png")
  public AbstractImagePrototype green_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/grey/icon48.png")
  public AbstractImagePrototype grey_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/olive/icon48.png")
  public AbstractImagePrototype olive_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/orange/icon48.png")
  public AbstractImagePrototype orange_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/purple/icon48.png")
  public AbstractImagePrototype purple_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/red/icon48.png")
  public AbstractImagePrototype red_icon64();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/ui/yellow/icon48.png")
  public AbstractImagePrototype yellow_icon64();


  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/arrow_n.png")
  public AbstractImagePrototype arrow_n();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/arrow_e.png")
  public AbstractImagePrototype arrow_e();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/arrow_s.png")
  public AbstractImagePrototype arrow_s();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/arrow_w.png")
  public AbstractImagePrototype arrow_w();



  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/hightlight_hexagon.png")
  public AbstractImagePrototype tactic_hightlight_hexagon();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/hightlight_hexagon.png")
  public AbstractImagePrototype strategy_hightlight_hexagon();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/select_hexagon.png")
  public AbstractImagePrototype tactic_select_hexagon();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/select_hexagon.png")
  public AbstractImagePrototype strategy_select_hexagon();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/target.png")
  public AbstractImagePrototype tactic_target();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/target.png")
  public AbstractImagePrototype strategy_target();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/target_control.png")
  public AbstractImagePrototype tactic_target_control();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/target_control.png")
  public AbstractImagePrototype strategy_target_control();


  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_n.png")
  public AbstractImagePrototype tactic_arrow_n();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_ne.png")
  public AbstractImagePrototype tactic_arrow_ne();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_se.png")
  public AbstractImagePrototype tactic_arrow_se();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_s.png")
  public AbstractImagePrototype tactic_arrow_s();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_sw.png")
  public AbstractImagePrototype tactic_arrow_sw();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/arrow_nw.png")
  public AbstractImagePrototype tactic_arrow_nw();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_n.png")
  public AbstractImagePrototype strategy_arrow_n();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_ne.png")
  public AbstractImagePrototype strategy_arrow_ne();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_se.png")
  public AbstractImagePrototype strategy_arrow_se();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_s.png")
  public AbstractImagePrototype strategy_arrow_s();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_sw.png")
  public AbstractImagePrototype strategy_arrow_sw();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/arrow_nw.png")
  public AbstractImagePrototype strategy_arrow_nw();


  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/warning.png")
  public AbstractImagePrototype tactic_warning();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/warning.png")
  public AbstractImagePrototype strategy_warning();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/disable_fire.png")
  public AbstractImagePrototype tactic_disable_fire();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/disable_fire.png")
  public AbstractImagePrototype strategy_disable_fire();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/disabling_fire.png")
  public AbstractImagePrototype tactic_disabling_fire();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/disabling_fire.png")
  public AbstractImagePrototype strategy_disabling_fire();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/disable_water.png")
  public AbstractImagePrototype tactic_disable_water();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/disable_water.png")
  public AbstractImagePrototype strategy_disable_water();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_load1.png")
  public AbstractImagePrototype strategy_icon_load1();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_load2.png")
  public AbstractImagePrototype strategy_icon_load2();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_load3.png")
  public AbstractImagePrototype strategy_icon_load3();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_load4.png")
  public AbstractImagePrototype strategy_icon_load4();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_load1.png")
  public AbstractImagePrototype tactic_icon_load1();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_load2.png")
  public AbstractImagePrototype tactic_icon_load2();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_load3.png")
  public AbstractImagePrototype tactic_icon_load3();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_load4.png")
  public AbstractImagePrototype tactic_icon_load4();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_bullet1.png")
  public AbstractImagePrototype strategy_icon_bullet1();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/strategy/icon_bullet2.png")
  public AbstractImagePrototype strategy_icon_bullet2();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_bullet1.png")
  public AbstractImagePrototype tactic_icon_bullet1();

  @Resource("com/fullmetalgalaxy/client/ressources/icons/board/tactic/icon_bullet2.png")
  public AbstractImagePrototype tactic_icon_bullet2();



  // colorless token
  // ===============

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/ore20-0001.png")
  public AbstractImagePrototype tactic_ore_01();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/ore2180-0001.png")
  public AbstractImagePrototype tactic_ore_02();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/ore2300-0001.png")
  public AbstractImagePrototype tactic_ore_03();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/ore20-0001.png")
  public AbstractImagePrototype strategy_ore_01();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/ore2180-0001.png")
  public AbstractImagePrototype strategy_ore_02();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/ore2180-0001.png")
  public AbstractImagePrototype strategy_ore_03();



  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon0-0001.png")
  public AbstractImagePrototype tactic_pontoon_n();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon300-0001.png")
  public AbstractImagePrototype tactic_pontoon_ne();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon60-0001.png")
  public AbstractImagePrototype tactic_pontoon_nw();

  // as pontoon is symetric, use same images
  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon180-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon0-0001.png")
  public AbstractImagePrototype tactic_pontoon_s();

  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon120-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon300-0001.png")
  public AbstractImagePrototype tactic_pontoon_sw();

  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon240-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/tactic/pontoon60-0001.png")
  public AbstractImagePrototype tactic_pontoon_se();


  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon0-0001.png")
  public AbstractImagePrototype strategy_pontoon_n();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon300-0001.png")
  public AbstractImagePrototype strategy_pontoon_ne();

  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon60-0001.png")
  public AbstractImagePrototype strategy_pontoon_nw();

  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon180-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon0-0001.png")
  public AbstractImagePrototype strategy_pontoon_s();

  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon120-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon300-0001.png")
  public AbstractImagePrototype strategy_pontoon_sw();

  // @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon240-0001.png")
  @Resource("com/fullmetalgalaxy/client/ressources/tokens/render/colorless/strategy/pontoon60-0001.png")
  public AbstractImagePrototype strategy_pontoon_se();

}
