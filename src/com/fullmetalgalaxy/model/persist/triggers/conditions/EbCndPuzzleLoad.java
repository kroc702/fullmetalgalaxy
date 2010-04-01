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
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import com.fullmetalgalaxy.model.persist.EbBase;


/**
 * @author Vincent Legendre
 *  This condition is true at game load.
 */
public class EbCndPuzzleLoad extends AnCondition
{
  static final long serialVersionUID = 124;

  /**
   * 
   */
  public EbCndPuzzleLoad()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbCndPuzzleLoad(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }
}
