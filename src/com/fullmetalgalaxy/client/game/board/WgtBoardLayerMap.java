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
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerMap extends WgtBoardLayerBase
{
  protected Image m_image = new Image();

  /**
   * 
   */
  public WgtBoardLayerMap()
  {
    super();
    add( m_image, 0, 0 );

  }

  private Tide m_lastTideValue = Tide.Unknown;
  protected long m_lastGameId = 0;

  /**
   * 
   * @see com.fullmetalgalaxy.client.game.board.WgtBoardLayerBase#redraw()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    if( isVisible() != GameEngine.model().isCustomMapDisplayed() )
    {
      setVisible( GameEngine.model().isCustomMapDisplayed() );
    }
    if( !isVisible() )
    {
      return;
    }

    if( (m_lastGameId != GameEngine.model().getGame().getId()) || (p_forceRedraw) )
    {
      Game game = GameEngine.model().getGame();
      m_lastGameId = game.getId();
      m_lastTideValue = game.getCurrentTide();

      m_image.setUrl( game.getMapUri() );

      setZoom( getZoom() );
    }
    if( m_lastTideValue != GameEngine.model().getGame().getCurrentTide() )
    {
      m_lastTideValue = GameEngine.model().getGame().getCurrentTide();
      onTideChange();
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );

    int pxW = BoardConvert.landWidthHex2Pix( GameEngine.game().getLandWidth(), getZoom() );
    int pxH = BoardConvert.landHeightHex2Pix( GameEngine.game().getLandHeight(), getZoom() );
    m_image.setPixelSize( pxW, pxH );
    cropDisplay( m_cropLeftHex, m_cropTopHex, m_cropRightHex, m_cropBotomHex );

    onTideChange();
    show();
  }

  /**
   * called when tide as changed.
   */
  public void onTideChange()
  {
  }

  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    super.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );

    int pxX = -1 * m_cropLeftHex * (FmpConstant.getHexWidth( getZoom() ) * 3 / 4);
    int pxY = -1 * m_cropTopHex * FmpConstant.getHexHeight( getZoom() );
    add( m_image, pxX, pxY );
  }
}
