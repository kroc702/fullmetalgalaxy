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
package com.fullmetalgalaxy.client.ressources.tokens;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * @author Vincent Legendre
 *
 */
public interface PanteraFreighter extends ClientBundle, TokenFreighterImageBundle
{
  @Override
  @Source("render/pantera/tactic/freighter0-0001.png")
  public ImageResource tactic_freighter_n();

  @Override
  @Source("render/pantera/tactic/freighter60-0001.png")
  public ImageResource tactic_freighter_ne();

  @Override
  @Source("render/pantera/tactic/freighter60-0001.png")
  public ImageResource tactic_freighter_nw();

  @Override
  @Source("render/pantera/tactic/freighter60-0001.png")
  public ImageResource tactic_freighter_s();

  @Override
  @Source("render/pantera/tactic/freighter0-0001.png")
  public ImageResource tactic_freighter_sw();

  @Override
  @Source("render/pantera/tactic/freighter0-0001.png")
  public ImageResource tactic_freighter_se();


  @Override
  @Source("render/pantera/strategy/freighter0-0001.png")
  public ImageResource strategy_freighter_n();

  @Override
  @Source("render/pantera/strategy/freighter60-0001.png")
  public ImageResource strategy_freighter_ne();

  @Override
  @Source("render/pantera/strategy/freighter60-0001.png")
  public ImageResource strategy_freighter_nw();

  @Override
  @Source("render/pantera/strategy/freighter60-0001.png")
  public ImageResource strategy_freighter_s();

  @Override
  @Source("render/pantera/strategy/freighter0-0001.png")
  public ImageResource strategy_freighter_sw();

  @Override
  @Source("render/pantera/strategy/freighter0-0001.png")
  public ImageResource strategy_freighter_se();


}
