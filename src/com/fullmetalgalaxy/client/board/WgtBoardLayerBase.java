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
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerBase extends AbsolutePanel implements BoardLayer
{
  // these variable are here to keep track of the visible area.
  protected int m_left = 0;
  protected int m_top = 0;
  protected int m_botom = 0;
  protected int m_right = 0;


  /**
   * 
   */
  public WgtBoardLayerBase()
  {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return this;
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
    m_top = p_top;
    m_botom = p_botom;
    m_left = p_left;
    m_right = p_right;
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
    EbGame game = ModelFmpMain.model().getGame();
    if( (game.getId() != m_lastGameId) || (p_forceRedraw) )
    {
      int pxW = game.getLandPixWidth( getZoom() );
      int pxH = game.getLandPixHeight( getZoom() );
      setPixelSize( pxW, pxH );
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
    EbGame game = ModelFmpMain.model().getGame();
    int pxW = game.getLandPixWidth( getZoom() );
    int pxH = game.getLandPixHeight( getZoom() );
    setPixelSize( pxW, pxH );
  }


  protected WgtBoard getWgtBoard()
  {
    return (WgtBoard)getParent().getParent();
  }

  protected EnuZoom getZoom()
  {
    return ModelFmpMain.model().getZoomDisplayed();
  }

  /**
   * @param p_wgtHexPosition position in hexagon
   * @return true if a part of the given hexagon is visible.
   */
  protected boolean isHexVisible(AnPair p_wgtHexPosition)
  {
    AnPair wgtPxPosition = convertHexPositionToPixPosition( p_wgtHexPosition );
    int hexLeft = wgtPxPosition.getX() - FmpConstant.getHexWidth( getZoom() ) / 2;
    int hexRight = hexLeft + FmpConstant.getHexWidth( getZoom() );
    int hexTop = wgtPxPosition.getY() - FmpConstant.getHexHeight( getZoom() ) / 2;
    int hexBotom = hexTop + FmpConstant.getHexWidth( getZoom() );

    return (hexRight > m_left) && (hexLeft < m_right) && (hexBotom > m_top) && (hexTop < m_botom);
  }

  /**
   * @param p_wgtPixPosition position in pixel
   * @return true if the given position is visible.
   */
  protected boolean isPixVisible(AnPair p_wgtPixPosition)
  {
    return (p_wgtPixPosition.getX() > m_left) && (p_wgtPixPosition.getX() < m_right)
        && (p_wgtPixPosition.getY() > m_top) && (p_wgtPixPosition.getY() < m_botom);
  }

  /**
   * set position of a widget as the widget is centered on an hexagon.
   * @param p_w
   * @param p_wgtHexPosition position in hexagon
   */
  protected void setWidgetHexPosition(Widget p_w, AnPair p_wgtHexPosition)
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
  protected void setWidgetPixPosition(Widget p_w, AnPair p_wgtPixPosition)
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
  protected void setWidgetPixPosition(Image p_w, AnPair p_wgtPixPosition)
  {
    super.setWidgetPosition( p_w, p_wgtPixPosition.getX() - p_w.getWidth() / 2, p_wgtPixPosition
        .getY()
        - p_w.getHeight() / 2 );
    // p_w.setVisible( true );
  }



  /**
   * @param p_boardPosition board position in hexagon
   * @param p_zoom
   * @return widget center position in pixel
   */
  protected static AnPair convertHexPositionToPixPosition(AnPair p_wgtHexPosition, EnuZoom p_zoom)
  {
    AnPair wgtPxPosition = new AnPair();

    wgtPxPosition.setX( p_wgtHexPosition.getX() * (FmpConstant.getHexWidth( p_zoom ) * 3 / 4)
        + FmpConstant.getHexWidth( p_zoom ) / 2 );
    if( p_wgtHexPosition.getX() % 2 == 0 )
    {
      wgtPxPosition.setY( p_wgtHexPosition.getY() * FmpConstant.getHexHeight( p_zoom )
          + FmpConstant.getHexHeight( p_zoom ) / 2 );
    }
    else
    {
      wgtPxPosition.setY( p_wgtHexPosition.getY() * FmpConstant.getHexHeight( p_zoom )
          + FmpConstant.getHexHeight( p_zoom ) );
    }
    return wgtPxPosition;
  }

  protected AnPair convertHexPositionToPixPosition(AnPair p_wgtHexPosition)
  {
    return convertHexPositionToPixPosition( p_wgtHexPosition, getZoom() );
  }


  /**
   * 
   * @param p_wgtPosition widget position in pixel
   * @return board position in hexagon
   */
  public static AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition, EnuZoom p_zoom)
  {
    AnBoardPosition anBoardPosition = new AnBoardPosition( 0, 0 );

    anBoardPosition.setX( (p_pixPosition.getX() - FmpConstant.getHexWidth( p_zoom ) / 8)
        / (FmpConstant.getHexWidth( p_zoom ) * 3 / 4) );

    if( anBoardPosition.getX() % 2 == 0 )
    {
      anBoardPosition.setY( p_pixPosition.getY() / FmpConstant.getHexHeight( p_zoom ) );
    }
    else
    {
      anBoardPosition.setY( (p_pixPosition.getY() - FmpConstant.getHexHeight( p_zoom ) / 2)
          / FmpConstant.getHexHeight( p_zoom ) );
    }
    return anBoardPosition;
  }

  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    return convertPixPositionToHexPosition( p_pixPosition, getZoom() );
  }


}
