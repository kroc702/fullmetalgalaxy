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
package com.fullmetalgalaxy.client.game.board.layertoken;

import com.google.gwt.animation.client.Animation;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public abstract class AnimEvent extends Animation
{
  protected WgtBoardLayerToken m_layerToken = null;

  public AnimEvent(WgtBoardLayerToken p_layerToken)
  {
    m_layerToken = p_layerToken;
  }

  /**
   * 
   * @return animation duration in milliseconds
   */
  public abstract int getDurration();

  /**
   * animation on game event should know their duration
   */
  public void run()
  {
    run( getDurration() );
  }


  @Override
  protected double interpolate(double p_progress)
  {
    return p_progress;
  }

  @Override
  protected void onComplete()
  {
    m_layerToken.nextAnimation();
  }


  @Override
  protected void onCancel()
  {
    m_layerToken.nextAnimation();
  }
  



}
