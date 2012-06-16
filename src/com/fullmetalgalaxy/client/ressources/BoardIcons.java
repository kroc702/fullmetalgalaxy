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
    return AbstractImagePrototype.create( BoardIconsRessource.iconTide( p_tide ) );
  }


  public static AbstractImagePrototype iconLoad(int p_zoom, int p_loadSize)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.iconLoad( p_zoom, p_loadSize ) );
  }

  public static AbstractImagePrototype iconBullet(int p_zoom, int p_bulletCount)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.iconBullet( p_zoom, p_bulletCount ) );
  }

  public static AbstractImagePrototype hightlight_hexagon(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.hightlight_hexagon( p_zoom ) );
  }

  public static AbstractImagePrototype icon16(int p_colorValue)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.icon16( p_colorValue ) );
  }


  public static AbstractImagePrototype deployment4(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.deployment4( p_zoom ) );
  }

  public static AbstractImagePrototype select_hexagon(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.select_hexagon( p_zoom ) );
  }

  public static AbstractImagePrototype target_control(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.target_control( p_zoom ) );
  }

  public static AbstractImagePrototype target(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.target( p_zoom ) );
  }

  public static AbstractImagePrototype arrow(int p_zoom, Sector p_sector)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.arrow( p_zoom, p_sector ) );
  }


  public static AbstractImagePrototype warning(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.warning( p_zoom ) );
  }

  public static AbstractImagePrototype disable_fire(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.disable_fire( p_zoom ) );
  }

  public static AbstractImagePrototype disabling_fire(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.disabling_fire( p_zoom ) );
  }

  public static AbstractImagePrototype disable_water(int p_zoom)
  {
    return AbstractImagePrototype.create( BoardIconsRessource.disable_water( p_zoom ) );
  }



}
