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
import com.fullmetalgalaxy.model.persist.EbRegistration;



/**
 * @author Vincent Legendre
 * is player control a given color ?
 */
public class EbCndPlayerColor extends AnCondition
{
  static final long serialVersionUID = 126;

  private int m_color = EnuColor.None;
  private EbRegistration m_player = null;

  /**
   * 
   */
  public EbCndPlayerColor()
  {
    init();
  }

  private void init()
  {
    m_color = EnuColor.None;
    m_player = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /** true if player control given color
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#isTrue(com.fullmetalgalaxy.model.persist.Game)
   */
  @Override
  public boolean isTrue(Game p_game)
  {
    if( getPlayer() == null )
    {
      return false;
    }
    return getPlayer().getEnuColor().isColored( getColor() );
  }



  /**
   * @return the player
   */
  public EbRegistration getPlayer()
  {
    return m_player;
  }

  /**
   * @param p_player the player to set
   */
  public void setPlayer(EbRegistration p_player)
  {
    m_player = p_player;
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
