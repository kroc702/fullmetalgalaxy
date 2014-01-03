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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.Game;



/**
 * @author Vincent Legendre
 * is token X color X ?
 */
public class EbCndTokenColor extends AnCondition
{
  static final long serialVersionUID = 126;

  private int m_color = EnuColor.None;
  private long m_tokenId = 0;

  /**
   * 
   */
  public EbCndTokenColor()
  {
    init();
  }

  private void init()
  {
    m_color = EnuColor.None;
    m_tokenId = 0;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#isTrue(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public boolean isTrue(Game p_game)
  {
    if( m_tokenId == 0 )
    {
      return false;
    }
    return p_game.getToken( m_tokenId ).getColor() == getColor();
  }



  /**
   * @return the color
   */
  public int getColor()
  {
    return m_color;
  }

  /**
   * @param p_color the color to set
   */
  public void setColor(int p_color)
  {
    m_color = p_color;
  }

}
