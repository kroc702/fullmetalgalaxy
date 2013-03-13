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
package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimLoad extends AnimMoveBase
{

  public AnimLoad(WgtBoardLayerToken p_layerToken, EbEvtLoad p_event)
  {
    super( p_layerToken, p_event );
  }

  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenCarrier( GameEngine.model().getGame() ), false );
  }

  @Override
  protected AnBoardPosition getOldPosition()
  {
    return m_event.getOldPosition();
  }

  @Override
  protected AnBoardPosition getNewPosition()
  {
    return ((EbEvtLoad)m_event).getTokenCarrier( GameEngine.model().getGame() ).getPosition();
  }

  @Override
  public int getDurration()
  {
    return 1000;
  }

}
