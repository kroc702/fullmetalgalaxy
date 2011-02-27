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
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import java.util.List;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;


/**
 * @author Vincent Legendre
 *
 */
public class AnCondition extends EbBase
{
  static final long serialVersionUID = 121;

  private boolean m_negative = false;

  /**
   * 
   */
  public AnCondition()
  {
    init();
  }

  /**
   * @param p_base
   */
  public AnCondition(EbBase p_base)
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

  // main trigger condition interface
  // --------------------------------
  /**
   * To be overloaded
   * @return true if that condition is verified on game p_game
   */
  // @Transient
  protected boolean isTrue(EbGame p_game)
  {
    return false;
  }

  /**
   * to be overloaded
   * @return a list of parameters to pass to actions
   */
  // @Transient
  public List<Object> getActParams(EbGame p_game)
  {
    return null;
  }

  // @Transient
  public final boolean isVerify(EbGame p_game)
  {
    if( isNegative() )
    {
      return !isTrue( p_game );
    }
    return isTrue( p_game );
  }

  /**
   * if negative flag is set, actions will be executed when condition is false.
   * @return the negative
   */
  public boolean isNegative()
  {
    return m_negative;
  }

  /**
   * @param p_negative the negative to set
   */
  public void setNegative(boolean p_negative)
  {
    m_negative = p_negative;
  }


}
