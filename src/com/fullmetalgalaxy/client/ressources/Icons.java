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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Vincent Legendre
 * image contained by folder icons. for user interface
 */
public interface Icons extends ClientBundle
{
  public static Icons s_instance = (Icons)GWT.create( Icons.class );

  @Source("icons/ui/loading.png")
  public ImageResource loading();

  @Source("icons/ui/Pause32.png")
  public ImageResource pause32();

  @Source("icons/ui/Forward32.png")
  public ImageResource forward32();

  @Source("icons/ui/Play32.png")
  public ImageResource play32();

  @Source("icons/ui/FastPlay32.png")
  public ImageResource fastPlay32();

  @Source("icons/ui/Back32.png")
  public ImageResource back32();

  @Source("icons/ui/FastBack32.png")
  public ImageResource fastBack32();

  @Source("icons/ui/Winner32.png")
  public ImageResource winner32();


  @Source("icons/ui/Cancel32.png")
  public ImageResource cancel32();

  @Source("icons/ui/Control32.png")
  public ImageResource control32();

  @Source("icons/ui/Info32.png")
  public ImageResource info32();

  @Source("icons/ui/WebLinks32.png")
  public ImageResource webLinks32();

  @Source("icons/ui/Message32.png")
  public ImageResource message32();

  @Source("icons/ui/Chat32.png")
  public ImageResource chat32();

  @Source("icons/ui/Ok32.png")
  public ImageResource ok32();

  @Source("icons/ui/Repair32.png")
  public ImageResource repair32();

  @Source("icons/ui/Shoot32.png")
  public ImageResource shoot32();

  @Source("icons/ui/ZoomIn32.png")
  public ImageResource zoomIn32();

  @Source("icons/ui/ZoomOut32.png")
  public ImageResource zoomOut32();

  @Source("icons/ui/GridOn32.png")
  public ImageResource gridOn32();

  @Source("icons/ui/GridOff32.png")
  public ImageResource gridOff32();

  @Source("icons/ui/EndTurn32.png")
  public ImageResource endTurn32();

  @Source("icons/ui/FireCoverOn32.png")
  public ImageResource fireCoverOn32();

  @Source("icons/ui/FireCoverOff32.png")
  public ImageResource fireCoverOff32();

  @Source("icons/ui/Register32.png")
  public ImageResource register32();

  @Source("icons/ui/Reserve32.png")
  public ImageResource reserve32();

  @Source("icons/ui/TakeOff32.png")
  public ImageResource takeOff32();

  @Source("icons/ui/Player32.png")
  public ImageResource player32();

  @Source("icons/ui/Practice32.png")
  public ImageResource practice32();

  @Source("icons/ui/Action16.png")
  public ImageResource action16();

  @Source("icons/ui/Char16.png")
  public ImageResource char16();

  @Source("icons/ui/Ore16.png")
  public ImageResource ore16();

  @Source("icons/ui/Time32.png")
  public ImageResource time32();

  @Source("icons/ui/TakeOff16.png")
  public ImageResource takeOff16();

  @Source("icons/ui/TakeOffBW16.png")
  public ImageResource takeOffBW16();


  @Source("icons/ui/moon16.png")
  public ImageResource moon16();

  @Source("icons/board/tide_low.png")
  public ImageResource tide_low();

  @Source("icons/board/tide_medium.png")
  public ImageResource tide_medium();

  @Source("icons/board/tide_hight.png")
  public ImageResource tide_hight();

  @Source("icons/board/tide_unknown.png")
  public ImageResource tide_unknown();



  @Source("icons/ui/colors/blue16.png")
  public ImageResource blue_icon16();

  @Source("icons/ui/colors/cyan16.png")
  public ImageResource cyan_icon16();

  @Source("icons/ui/colors/green16.png")
  public ImageResource green_icon16();

  @Source("icons/ui/colors/grey16.png")
  public ImageResource grey_icon16();

  @Source("icons/ui/colors/olive16.png")
  public ImageResource olive_icon16();

  @Source("icons/ui/colors/orange16.png")
  public ImageResource orange_icon16();

  @Source("icons/ui/colors/purple16.png")
  public ImageResource purple_icon16();

  @Source("icons/ui/colors/red16.png")
  public ImageResource red_icon16();

  @Source("icons/ui/colors/yellow16.png")
  public ImageResource yellow_icon16();

  @Source("icons/ui/colors/brown16.png")
  public ImageResource brown_icon16();

  @Source("icons/ui/colors/pink16.png")
  public ImageResource pink_icon16();

  @Source("icons/ui/colors/white16.png")
  public ImageResource white_icon16();

  @Source("icons/ui/colors/camouflage16.png")
  public ImageResource camouflage_icon16();

  @Source("icons/ui/colors/lightning16.png")
  public ImageResource lightning_icon16();

  @Source("icons/ui/colors/panter16.png")
  public ImageResource panter_icon16();

  @Source("icons/ui/colors/zebra16.png")
  public ImageResource zebra_icon16();

  @Source("icons/ui/colors/colorless16.png")
  public ImageResource colorless_icon16();


  @Source("icons/board/arrow_n.png")
  public ImageResource arrow_n();

  @Source("icons/board/arrow_e.png")
  public ImageResource arrow_e();

  @Source("icons/board/arrow_s.png")
  public ImageResource arrow_s();

  @Source("icons/board/arrow_w.png")
  public ImageResource arrow_w();



  @Source("icons/board/tactic/deployment4.png")
  public ImageResource tactic_deployment4();

  @Source("icons/board/strategy/deployment4.png")
  public ImageResource strategy_deployment4();

  @Source("icons/board/tactic/hightlight_hexagon.png")
  public ImageResource tactic_hightlight_hexagon();

  @Source("icons/board/strategy/hightlight_hexagon.png")
  public ImageResource strategy_hightlight_hexagon();

  @Source("icons/board/tactic/select_hexagon.png")
  public ImageResource tactic_select_hexagon();

  @Source("icons/board/strategy/select_hexagon.png")
  public ImageResource strategy_select_hexagon();

  @Source("icons/board/tactic/target.png")
  public ImageResource tactic_target();

  @Source("icons/board/strategy/target.png")
  public ImageResource strategy_target();

  @Source("icons/board/tactic/target_control.png")
  public ImageResource tactic_target_control();

  @Source("icons/board/strategy/target_control.png")
  public ImageResource strategy_target_control();


  @Source("icons/board/tactic/arrow_n.png")
  public ImageResource tactic_arrow_n();

  @Source("icons/board/tactic/arrow_ne.png")
  public ImageResource tactic_arrow_ne();

  @Source("icons/board/tactic/arrow_se.png")
  public ImageResource tactic_arrow_se();

  @Source("icons/board/tactic/arrow_s.png")
  public ImageResource tactic_arrow_s();

  @Source("icons/board/tactic/arrow_sw.png")
  public ImageResource tactic_arrow_sw();

  @Source("icons/board/tactic/arrow_nw.png")
  public ImageResource tactic_arrow_nw();

  @Source("icons/board/strategy/arrow_n.png")
  public ImageResource strategy_arrow_n();

  @Source("icons/board/strategy/arrow_ne.png")
  public ImageResource strategy_arrow_ne();

  @Source("icons/board/strategy/arrow_se.png")
  public ImageResource strategy_arrow_se();

  @Source("icons/board/strategy/arrow_s.png")
  public ImageResource strategy_arrow_s();

  @Source("icons/board/strategy/arrow_sw.png")
  public ImageResource strategy_arrow_sw();

  @Source("icons/board/strategy/arrow_nw.png")
  public ImageResource strategy_arrow_nw();

  @Source("icons/board/strategy/padlock.png")
  public ImageResource strategy_padlock();

  @Source("icons/board/tactic/padlock.png")
  public ImageResource tactic_padlock();

  @Source("icons/board/tactic/warning.png")
  public ImageResource tactic_warning();

  @Source("icons/board/strategy/warning.png")
  public ImageResource strategy_warning();

  @Source("icons/board/tactic/disable_fire.png")
  public ImageResource tactic_disable_fire();

  @Source("icons/board/strategy/disable_fire.png")
  public ImageResource strategy_disable_fire();

  @Source("icons/board/tactic/disabling_fire.png")
  public ImageResource tactic_disabling_fire();

  @Source("icons/board/strategy/disabling_fire.png")
  public ImageResource strategy_disabling_fire();

  @Source("icons/board/tactic/disable_water.png")
  public ImageResource tactic_disable_water();

  @Source("icons/board/strategy/disable_water.png")
  public ImageResource strategy_disable_water();

  @Source("icons/board/strategy/icon_load1.png")
  public ImageResource strategy_icon_load1();

  @Source("icons/board/strategy/icon_load2.png")
  public ImageResource strategy_icon_load2();

  @Source("icons/board/strategy/icon_load3.png")
  public ImageResource strategy_icon_load3();

  @Source("icons/board/strategy/icon_load4.png")
  public ImageResource strategy_icon_load4();

  @Source("icons/board/tactic/icon_load1.png")
  public ImageResource tactic_icon_load1();

  @Source("icons/board/tactic/icon_load2.png")
  public ImageResource tactic_icon_load2();

  @Source("icons/board/tactic/icon_load3.png")
  public ImageResource tactic_icon_load3();

  @Source("icons/board/tactic/icon_load4.png")
  public ImageResource tactic_icon_load4();

  @Source("icons/board/strategy/icon_bullet1.png")
  public ImageResource strategy_icon_bullet1();

  @Source("icons/board/strategy/icon_bullet2.png")
  public ImageResource strategy_icon_bullet2();

  @Source("icons/board/tactic/icon_bullet1.png")
  public ImageResource tactic_icon_bullet1();

  @Source("icons/board/tactic/icon_bullet2.png")
  public ImageResource tactic_icon_bullet2();


  @Source("icons/board/tactic/wreck.png")
  public ImageResource tactic_wreck();

  @Source("icons/board/strategy/wreck.png")
  public ImageResource strategy_wreck();


  // colorless token
  // ===============

  @Source("tokens/render/colorless/tactic/ore20-0001.png")
  public ImageResource tactic_ore_01();

  @Source("tokens/render/colorless/tactic/ore2180-0001.png")
  public ImageResource tactic_ore_02();

  @Source("tokens/render/colorless/tactic/ore2300-0001.png")
  public ImageResource tactic_ore_03();

  @Source("tokens/render/colorless/strategy/ore20-0001.png")
  public ImageResource strategy_ore_01();

  @Source("tokens/render/colorless/strategy/ore2180-0001.png")
  public ImageResource strategy_ore_02();

  @Source("tokens/render/colorless/strategy/ore2180-0001.png")
  public ImageResource strategy_ore_03();



  @Source("tokens/render/colorless/tactic/pontoon0-0001.png")
  public ImageResource tactic_pontoon_n();

  @Source("tokens/render/colorless/tactic/pontoon300-0001.png")
  public ImageResource tactic_pontoon_ne();

  @Source("tokens/render/colorless/tactic/pontoon60-0001.png")
  public ImageResource tactic_pontoon_nw();

  // as pontoon is symetric, use same images
  // @Source("tokens/render/colorless/tactic/pontoon180-0001.png")
  @Source("tokens/render/colorless/tactic/pontoon0-0001.png")
  public ImageResource tactic_pontoon_s();

  // @Source("tokens/render/colorless/tactic/pontoon120-0001.png")
  @Source("tokens/render/colorless/tactic/pontoon300-0001.png")
  public ImageResource tactic_pontoon_sw();

  // @Source("tokens/render/colorless/tactic/pontoon240-0001.png")
  @Source("tokens/render/colorless/tactic/pontoon60-0001.png")
  public ImageResource tactic_pontoon_se();


  @Source("tokens/render/colorless/strategy/pontoon0-0001.png")
  public ImageResource strategy_pontoon_n();

  @Source("tokens/render/colorless/strategy/pontoon300-0001.png")
  public ImageResource strategy_pontoon_ne();

  @Source("tokens/render/colorless/strategy/pontoon60-0001.png")
  public ImageResource strategy_pontoon_nw();

  // @Source("tokens/render/colorless/strategy/pontoon180-0001.png")
  @Source("tokens/render/colorless/strategy/pontoon0-0001.png")
  public ImageResource strategy_pontoon_s();

  // @Source("tokens/render/colorless/strategy/pontoon120-0001.png")
  @Source("tokens/render/colorless/strategy/pontoon300-0001.png")
  public ImageResource strategy_pontoon_sw();

  // @Source("tokens/render/colorless/strategy/pontoon240-0001.png")
  @Source("tokens/render/colorless/strategy/pontoon60-0001.png")
  public ImageResource strategy_pontoon_se();

}
