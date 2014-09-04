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
import com.fullmetalgalaxy.model.MapShape;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerBase extends AbsolutePanel implements BoardLayer
{
  // these variable are here to keep track of the visible area of widget in pixel.
  protected int m_leftPix = 0;
  protected int m_topPix = 0;
  protected int m_botomPix = 0;
  protected int m_rightPix = 0;

  // private AnPair hexPositionLeftTop = new AnPair( 0, 0 );
  // private AnPair hexPositionRightBotom = new AnPair( 900, 900 );

  // cropped area in hexagon
  protected int m_cropLeftHex = 0;
  protected int m_cropTopHex = 0;
  protected int m_cropBotomHex = 10;
  protected int m_cropRightHex = 10;

  /**
   * 
   */
  public WgtBoardLayerBase()
  {
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#hide()
   */
  @Override
  public void hide()
  {
  }


  /**
   * you usually don't need to override this method (override redraw() instead)
   */
  @Override
  public void redraw(int p_left, int p_top, int p_right, int p_botom)
  {
    m_topPix = p_top;
    m_botomPix = p_botom;
    if( m_botomPix > getOffsetHeight() )
    {
      m_botomPix = getOffsetHeight();
    }
    m_leftPix = p_left;
    m_rightPix = p_right;
    if( m_rightPix > getOffsetWidth() )
    {
      m_rightPix = getOffsetWidth();
    }

    // save currently visible map area for isHexVisible method
    /* the following optimization doesn't work since torus map :(
    AnPair pixPositionLeftTop = new AnPair( m_leftPix, m_topPix );
    AnPair pixPositionRightBotom = new AnPair( m_rightPix, m_botomPix );
    hexPositionLeftTop = convertPixPositionToHexPosition( pixPositionLeftTop );
    hexPositionLeftTop.setX( Math.max( m_cropLeftHex - 1, hexPositionLeftTop.getX() - 2 ) );
    hexPositionLeftTop.setY( Math.max( m_cropTopHex - 1, hexPositionLeftTop.getY() - 2 ) );
    hexPositionRightBotom = convertPixPositionToHexPosition( pixPositionRightBotom );
    hexPositionRightBotom.setX( Math.min( m_cropRightHex + 1, hexPositionRightBotom.getX() + 2 ) );
    hexPositionRightBotom.setY( Math.min( m_cropBotomHex + 1, hexPositionRightBotom.getY() + 2 ) );
    */
    redraw();
  }

  /**
   * override this method to redraw a little part of the widget after dragging board.
   */
  public void redraw()
  {

  }

  private long m_lastGameId = 0;

  public void onModelChange(boolean p_forceRedraw)
  {
    Game game = GameEngine.model().getGame();
    if( (game.getId() != m_lastGameId) || (p_forceRedraw) )
    {
      resetPixelSize();
      m_lastGameId = game.getId();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#onModelChange()
   */
  @Override
  public final void onModelChange()
  {
    onModelChange( false );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#show()
   */
  @Override
  public void show()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    resetPixelSize();
  }

  /**
   * recompute and set widget size in pixel
   */
  protected void resetPixelSize()
  {
    int width = m_cropRightHex - m_cropLeftHex;
    int height = m_cropBotomHex - m_cropTopHex;
    int pxW = BoardConvert.landWidthHex2Pix( width, getZoom() );
    int pxH = BoardConvert.landHeightHex2Pix( height, getZoom() );
    setPixelSize( pxW, pxH );
  }

  protected WgtBoard getWgtBoard()
  {
    return (WgtBoard)getParent().getParent();
  }

  public EnuZoom getZoom()
  {
    return GameEngine.model().getZoomDisplayed();
  }

  /**
   * the following method doesn't work since torus map shape
   * @param p_wgtHexPosition position in hexagon
   * @return true if a part of the given hexagon is visible.
   */
  protected boolean isHexVisible(AnPair p_wgtHexPosition)
  {
    return true;
    /*boolean isXVisible = false;    
    if( hexPositionLeftTop.getX() < hexPositionRightBotom.getX() )
    {
      isXVisible = (p_wgtHexPosition.getX() > hexPositionLeftTop.getX()) 
          && (p_wgtHexPosition.getX() < hexPositionRightBotom.getX());
    } else {
      isXVisible = (p_wgtHexPosition.getX() > hexPositionLeftTop.getX()) 
          || (p_wgtHexPosition.getX() < hexPositionRightBotom.getX());
    }
    boolean isYVisible = false;    
    if( hexPositionLeftTop.getY() < hexPositionRightBotom.getY() )
    {
      isYVisible = (p_wgtHexPosition.getY() > hexPositionLeftTop.getY())
          && (p_wgtHexPosition.getY() < hexPositionRightBotom.getY());
    } else {
      isYVisible = (p_wgtHexPosition.getY() > hexPositionLeftTop.getY())
          || (p_wgtHexPosition.getY() < hexPositionRightBotom.getY());
    }
    return isXVisible && isYVisible;*/
  }

  /**
   * @param p_wgtPixPosition position in pixel
   * @return true if the given position is visible.
   */
  protected boolean isPixVisible(AnPair p_wgtPixPosition)
  {
    return (p_wgtPixPosition.getX() > m_leftPix) && (p_wgtPixPosition.getX() < m_rightPix)
        && (p_wgtPixPosition.getY() > m_topPix) && (p_wgtPixPosition.getY() < m_botomPix);
  }

  /**
   * set position of a widget as the widget is centered on an hexagon.
   * @param p_w
   * @param p_wgtHexPosition position in hexagon
   */
  public void setWidgetHexPosition(Widget p_w, AnPair p_wgtHexPosition)
  {
    AnPair wgtPxPosition = convertHexPositionToPixPosition( p_wgtHexPosition );
    setWidgetPixPosition( p_w, wgtPxPosition );
  }

  /**
   * set position of a widget as the widget is centered on an hexagon.
   * @param p_w
   * @param p_wgtHexPosition position in hexagon
   */
  protected void setWidgetHexPosition(Widget p_w, AnPair p_wgtHexPosition, int p_heightPixOffset)
  {
    AnPair wgtPxPosition = convertHexPositionToPixPosition( p_wgtHexPosition );
    wgtPxPosition.setY( wgtPxPosition.getY() + p_heightPixOffset );
    setWidgetPixPosition( p_w, wgtPxPosition );
  }

  /**
   * set position of a widget as the widget is centered on the given position.
   * @param p_w
   * @param p_wgtPixPosition position in pixel
   */
  private void setWidgetPixPosition(Widget p_w, AnPair p_wgtPixPosition)
  {
    super.setWidgetPosition( p_w, p_wgtPixPosition.getX() - p_w.getOffsetWidth() / 2,
        p_wgtPixPosition.getY() - p_w.getOffsetHeight() / 2 );
    // p_w.setVisible( true );
  }



  /**
   * set position of a widget as the widget is centered on the given position.
   * @param p_w
   * @param p_wgtPixPosition position in pixel
   */
  public void setWidgetPixPosition(Image p_w, AnPair p_wgtPixPosition)
  {
    super.setWidgetPosition( p_w, p_wgtPixPosition.getX() - p_w.getWidth() / 2, p_wgtPixPosition
        .getY()
        - p_w.getHeight() / 2 );
    // p_w.setVisible( true );
  }



  public AnPair convertHexPositionToPixPosition(AnPair p_wgtHexPosition)
  {
    AnPair wgtHexPosition = p_wgtHexPosition;
    if( GameEngine.game().getMapShape() != MapShape.Flat )
    {
      wgtHexPosition = new AnPair( p_wgtHexPosition );
    }
    if( GameEngine.game().getMapShape().isEWLinked() )
    {    
      if( wgtHexPosition.getX() < m_cropLeftHex )
      {
        wgtHexPosition.setX( p_wgtHexPosition.getX() + GameEngine.game().getLandWidth() );
      }
    }
    if( GameEngine.game().getMapShape().isNSLinked() )
    {
      if( wgtHexPosition.getY() < m_cropTopHex )
      {
        wgtHexPosition.setY( p_wgtHexPosition.getY() + GameEngine.game().getLandHeight() );
      }
    }
    return BoardConvert.convertHexPositionToPixPosition( wgtHexPosition, getZoom(), new AnPair(m_cropLeftHex,m_cropTopHex) );
  }


  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    return GameEngine
        .game()
        .getCoordinateSystem()
        .normalizePosition(
            BoardConvert.convertPixPositionToHexPosition( p_pixPosition, getZoom(), new AnPair(
                m_cropLeftHex, m_cropTopHex ) ) );
  }

  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    m_cropLeftHex = p_cropLeftHex;
    if( m_cropLeftHex < 0 )
      m_cropLeftHex = 0;
    m_cropTopHex = p_cropTopHex;
    if( m_cropTopHex < 0 )
      m_cropTopHex = 0;
    m_cropBotomHex = p_cropBotomHex;
    m_cropRightHex = p_cropRightHex;
  }

  @Override
  public AnPair getCropTopLeft()
  {
    return new AnPair(m_cropLeftHex,m_cropTopHex);
  }


}
