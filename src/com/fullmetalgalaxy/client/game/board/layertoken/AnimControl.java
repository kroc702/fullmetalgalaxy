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
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimControl extends AnimEvent
{
  protected EbEvtControl m_event = null;
  private EbToken m_target = null;
  private TokenWidget m_tokenWidget = null;

  public AnimControl(WgtBoardLayerToken p_layerToken, EbEvtControl p_event)
  {
    super( p_layerToken );
    m_event = p_event;
    m_target = m_event.getTokenTarget( GameEngine.model().getGame() );
  }


  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenTarget( GameEngine.model().getGame() ), true );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer1( GameEngine.model().getGame() ),
        false );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer2( GameEngine.model().getGame() ),
        false );
  }



  @Override
  protected void onStart()
  {
    if( !m_layerToken.isVisible( m_target ) )
    {
      cancel();
      return;
    }
  }



  @Override
  protected void onUpdate(double p_progress)
  {
    if( m_tokenWidget == null )
    {
      m_tokenWidget = m_layerToken.getTokenWidget( m_target );
    }
    if( m_tokenWidget == null )
    {
      return;
    }

    EnuColor color = null;
    if( ((int)(p_progress * 10) % 2) == 0 )
    {
      color = m_event.getTokenDestroyer1( GameEngine.model().getGame() ).getEnuColor();
    }
    else
    {
      color = new EnuColor( m_event.getOldColor() );
    }
    
    m_tokenWidget.setTokenImage( TokenImages.getTokenImage( color, m_layerToken.getZoom()
        .getValue(), m_target.getType(), m_target.getPosition().getSector() ) );
  }



  @Override
  public int getDurration()
  {
    return 2000;
  }

}
