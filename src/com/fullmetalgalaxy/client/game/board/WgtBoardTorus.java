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


import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.EnuNavigator;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.layertoken.WgtBoardLayerToken;
import com.fullmetalgalaxy.client.widget.WgtScroll;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * 
 * this widget is similar to WgtBoard but can display map shape:
 * flat,cylinder or torus
 */
public class WgtBoardTorus extends WgtBoardBase
{
  private BoardLayerCollection m_globalLayers = new BoardLayerCollection();
  WgtBoardLayerSelect m_layerSelect = new WgtBoardLayerSelect();
  
  BoardLayerCollection[][] m_cadranLayers = new BoardLayerCollection[3][3];
  int m_middleLocalLayerIX = 1;
  int m_middleLocalLayerIY = 1;

  /**
   * 
   */
  public WgtBoardTorus()
  {
    super();
    for( int ix = 0; ix < 3; ix++ )
      for( int iy = 0; iy < 3; iy++ )
      {
        BoardLayerCollection cadranLayers = new BoardLayerCollection();
        cadranLayers.addLayer( new WgtBoardLayerLand() );
        cadranLayers.addLayer( new WgtBoardLayerMap() );
        // cadranLayers.addLayer( new WgtBoardLayerFireCover() );
        //cadranLayers.addLayer( new WgtBoardLayerToken() );
        //cadranLayers.addLayer( new WgtBoardLayerAction() );
        //cadranLayers.addLayer( new WgtBoardLayerSelect() );
        // cadranLayers.addLayer( new WgtBoardLayerLocked() );
        m_cadranLayers[ix][iy] = cadranLayers;
      }

    m_globalLayers.addLayer( new WgtBoardLayerFireCover() );
    m_globalLayers.addLayer( new WgtBoardLayerGrid() );
    m_globalLayers.addLayer( m_layerSelect );
    m_globalLayers.addLayer( new WgtBoardLayerToken() );
    m_globalLayers.addLayer( new WgtBoardLayerAction() );
    m_globalLayers.addLayer( new WgtBoardLayerLocked() );
    if( ClientUtil.getNavigator() == EnuNavigator.FF )
    {
      m_globalLayers.addLayer( new WgtBoardLayerAtmosphere() );
    }
    
    // in case game already loaded
    onGameLoad( GameEngine.model().getGame() );
  }



  protected boolean m_isVisible = false;

  public void show()
  {
    if( !m_isVisible )
    {
      for( int ix = 0; ix < 3; ix++ )
        for( int iy = 0; iy < 3; iy++ )
        {
          m_cadranLayers[ix][iy].show();
        }
      m_globalLayers.show();
      m_isVisible = true;
      ClientUtil.scrollToTop();
      //Window.enableScrolling( false );
    }

    m_panel.setPixelSize( m_globalLayers.asWidget().getOffsetWidth(), m_globalLayers.asWidget()
        .getOffsetHeight() );

    /* TODO they should subscribe to event...
    // map or standard land layer ?
    // m_layerMap.setVisible( !ModelFmpMain.model().isStandardLandDisplayed() );
    // m_layerLand.setVisible( ModelFmpMain.model().isStandardLandDisplayed() );

    // grid
    m_layerGrid.setVisible( GameEngine.model().isGridDisplayed() );

    // atmosphere
    m_layerAtmosphere.setVisible( GameEngine.model().isAtmosphereDisplayed() );

    // fire cover
    m_layerCover.displayFireCover( GameEngine.model().isFireCoverDisplayed() );

    */
  }

  public void hide()
  {
    m_isVisible = false;
    for( int ix = 0; ix < 3; ix++ )
      for( int iy = 0; iy < 3; iy++ )
      {
        m_cadranLayers[ix][iy].hide();
      }
    m_globalLayers.hide();
    Window.enableScrolling( true );
  }

  
  

  protected AnBoardPosition m_hexagonHightlightPosition = new AnBoardPosition( 0, 0 );

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseMove(MouseMoveEvent p_event)
  {
    AnBoardPosition position = convertPixPositionToHexPosition( new AnPair(
        p_event.getX(), p_event.getY() ) );
    if( (position.getX() != m_hexagonHightlightPosition.getX())
        || (position.getY() != m_hexagonHightlightPosition.getY()) )
    {
      m_hexagonHightlightPosition = position;
      m_layerSelect.moveHightLightHexagon( m_hexagonHightlightPosition );
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ScrollListener#onScroll(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onScroll(Widget p_widget, int p_scrollLeft, int p_scrollTop)
  {
    // should we move local layers
    AnPair pixCenter = new AnPair( p_scrollLeft + p_widget.getOffsetWidth() / 2, p_scrollTop
        + p_widget.getOffsetHeight() / 2 );
    AnPair hexCenter = BoardConvert.convertPixPositionToHexPosition( pixCenter, getZoom(), new AnPair(0,0) );
    int middleLocalLayerIX = 1;
    int middleLocalLayerIY = 1;
    if( GameEngine.game().getMapShape().isEWLinked() )
    {
      // east / west are linked (ie borderless)
      if( hexCenter.getX() < GameEngine.model().getGame().getLandWidth() / 3 )
        middleLocalLayerIX = 0;
      if( hexCenter.getX() >= GameEngine.model().getGame().getLandWidth() * 2 / 3 )
        middleLocalLayerIX = 2;
      // up to here middleLocalLayerIX is an offset on current m_middleLocalLayerI?
      // TODO remove that by using local convertPixPositionToHexPosition
      middleLocalLayerIX += m_middleLocalLayerIX -1;
      if( middleLocalLayerIX > 2 ) middleLocalLayerIX -= 3;
      if( middleLocalLayerIX < 0 ) middleLocalLayerIX += 3;
    }
    if( GameEngine.game().getMapShape().isNSLinked() )
    {
      // north / south are linked (ie borderless)
      if( hexCenter.getY() < GameEngine.model().getGame().getLandHeight() / 3 )
        middleLocalLayerIY = 0;
      if( hexCenter.getY() >= GameEngine.model().getGame().getLandHeight() * 2 / 3 )
        middleLocalLayerIY = 2;
      // up to here middleLocalLayerIY is an offset on current m_middleLocalLayerI?
      middleLocalLayerIY += m_middleLocalLayerIY -1;
      if( middleLocalLayerIY > 2 ) middleLocalLayerIY -= 3;
      if( middleLocalLayerIY < 0 ) middleLocalLayerIY += 3;
    }
    if( middleLocalLayerIX != m_middleLocalLayerIX || middleLocalLayerIY != m_middleLocalLayerIY )
    {
      if( p_widget instanceof WgtScroll)
      {
        int pxOffsetX = 0;
        int pxOffsetY = 0;
        if( m_middleLocalLayerIX == 0 && middleLocalLayerIX == 2 ) pxOffsetX -= m_localLayerHexWidth[1];
        if( m_middleLocalLayerIX == 0 && middleLocalLayerIX == 1 ) pxOffsetX += m_localLayerHexWidth[2];
        if( m_middleLocalLayerIX == 1 && middleLocalLayerIX == 2 ) pxOffsetX += m_localLayerHexWidth[0];
        if( m_middleLocalLayerIX == 1 && middleLocalLayerIX == 0 ) pxOffsetX -= m_localLayerHexWidth[2];
        if( m_middleLocalLayerIX == 2 && middleLocalLayerIX == 0 ) pxOffsetX += m_localLayerHexWidth[1];
        if( m_middleLocalLayerIX == 2 && middleLocalLayerIX == 1 ) pxOffsetX -= m_localLayerHexWidth[0];
        if( m_middleLocalLayerIY == 0 && middleLocalLayerIY == 2 ) pxOffsetY -= m_localLayerHexHeight[1];
        if( m_middleLocalLayerIY == 0 && middleLocalLayerIY == 1 ) pxOffsetY += m_localLayerHexHeight[2];
        if( m_middleLocalLayerIY == 1 && middleLocalLayerIY == 2 ) pxOffsetY += m_localLayerHexHeight[0];
        if( m_middleLocalLayerIY == 1 && middleLocalLayerIY == 0 ) pxOffsetY -= m_localLayerHexHeight[2];
        if( m_middleLocalLayerIY == 2 && middleLocalLayerIY == 0 ) pxOffsetY += m_localLayerHexHeight[1];
        if( m_middleLocalLayerIY == 2 && middleLocalLayerIY == 1 ) pxOffsetY -= m_localLayerHexHeight[0];
        pxOffsetX *= FmpConstant.getHexWidth( getZoom() ) * 3 / 4;
        pxOffsetY *= FmpConstant.getHexHeight( getZoom() );
        pxOffsetX = ((WgtScroll)p_widget).getHorizontalScrollPosition() - pxOffsetX;
        pxOffsetY = ((WgtScroll)p_widget).getVerticalScrollPosition() - pxOffsetY;
        ((WgtScroll)p_widget).setScrollPositionSilent( pxOffsetX, pxOffsetY );
            
      }
      m_middleLocalLayerIY = middleLocalLayerIY;
      m_middleLocalLayerIX = middleLocalLayerIX;
      setLayersPositions( GameEngine.model().getGame() );
    }

    for( int ix = 0; ix < 3; ix++ )
      for( int iy = 0; iy < 3; iy++ )
      {
        int visibleLeftPix = p_scrollLeft -  m_panel.getWidgetLeft( m_cadranLayers[ix][iy].asWidget() );
        int visibleTopPix = p_scrollTop -  m_panel.getWidgetTop( m_cadranLayers[ix][iy].asWidget() );
        m_cadranLayers[ix][iy].redraw( visibleLeftPix, visibleTopPix,
            visibleLeftPix + p_widget.getOffsetWidth(), visibleTopPix + p_widget.getOffsetHeight() );
      }
    m_globalLayers.redraw( p_scrollLeft, p_scrollTop, p_scrollLeft + p_widget.getOffsetWidth(),
        p_scrollTop + p_widget.getOffsetHeight() );
  }



  private int m_oldZoom = -1;

  public void notifyModelUpdate(GameEngine p_modelSender)
  {
    if( !m_isVisible )
    {
      return;
    }
    if( m_oldZoom != getZoom().getValue() )
    {
      m_oldZoom = getZoom().getValue();
      for( int ix = 0; ix < 3; ix++ )
        for( int iy = 0; iy < 3; iy++ )
        {
          m_cadranLayers[ix][iy].setZoom( getZoom() );
        }
      m_globalLayers.setZoom( getZoom() );
      m_panel.setPixelSize( m_globalLayers.asWidget().getOffsetWidth(), m_globalLayers.asWidget()
          .getOffsetHeight() );
      setLayersPositions( p_modelSender.getGame() );
    }
    else
    {
      for( int ix = 0; ix < 3; ix++ )
        for( int iy = 0; iy < 3; iy++ )
        {
          m_cadranLayers[ix][iy].onModelChange();
        }
      m_globalLayers.onModelChange();
      /* FF fix
      if( m_gameId != p_modelSender.getGame() )
      {
        m_panel.setPixelSize( m_globalLayers.asWidget().getOffsetWidth(), m_globalLayers.asWidget()
            .getOffsetHeight() );
        setLayersPositions( p_modelSender.getGame() );
      }*/
    }
  }

  @Override
  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    AnBoardPosition position = BoardConvert.convertPixPositionToHexPosition( p_pixPosition, getZoom(), m_globalLayers.getCropTopLeft() );
    if( position.getX() >= GameEngine.game().getLandWidth() )
    {
      position.setX( position.getX() - GameEngine.game().getLandWidth() );
    }
    if( position.getY() >= GameEngine.game().getLandHeight() )
    {
      position.setY( position.getY() - GameEngine.game().getLandHeight() );
    }
    return position;
  }
  
  private static final int[][] CADRAN = { { 2, 0, 1 }, { 0, 1, 2 }, { 1, 2, 0 } };

  /**
   * position given local layer at given cadran position
   * @param p_layer
   * @param p_iX [0;2]
   * @param p_iY [0;2]
   */
  private void setLayerPosition(Game p_game, BoardLayer p_layer, int p_ix, int p_iy)
  {
    int pxOffsetX = 0;
    int pxOffsetY = 0;
    if( p_ix > 0 ) pxOffsetX += m_localLayerHexWidth[CADRAN[m_middleLocalLayerIX][0]];
    if( p_iy > 0 ) pxOffsetY += m_localLayerHexHeight[CADRAN[m_middleLocalLayerIY][0]];
    if( p_ix > 1 ) pxOffsetX += m_localLayerHexWidth[CADRAN[m_middleLocalLayerIX][1]];
    if( p_iy > 1 ) pxOffsetY += m_localLayerHexHeight[CADRAN[m_middleLocalLayerIY][1]];

    pxOffsetX *= FmpConstant.getHexWidth( getZoom() ) * 3 / 4;
    pxOffsetY *= FmpConstant.getHexHeight( getZoom() );
    m_panel.add( p_layer, pxOffsetX, pxOffsetY );
  }

  public void setLayersPositions(Game p_game)
  {
    for( int ix = 0; ix < 3; ix++ )
      for( int iy = 0; iy < 3; iy++ )
        setLayerPosition( p_game,
            m_cadranLayers[CADRAN[m_middleLocalLayerIX][ix]][CADRAN[m_middleLocalLayerIY][iy]], ix,
            iy );
    
    int cropLeft = 0;
    int cropTop = 0;
    if( m_middleLocalLayerIX == 2 )
    {
      cropLeft += m_localLayerHexWidth[0];
    }
    if( m_middleLocalLayerIX == 0 )
    {
      cropLeft += m_localLayerHexWidth[0] + m_localLayerHexWidth[1];
    }
    if( m_middleLocalLayerIY == 2 )
    {
      cropTop += m_localLayerHexHeight[0];
    }
    if( m_middleLocalLayerIY == 0 )
    {
      cropTop += m_localLayerHexHeight[0] + m_localLayerHexHeight[1];
    }
    m_globalLayers.cropDisplay( cropLeft, cropTop, 
        cropLeft+p_game.getLandWidth(), cropTop+p_game.getLandHeight() );
    m_panel.add( m_globalLayers, 0, 0 );
  }


  private int[] m_localLayerHexWidth = new int[3];
  private int[] m_localLayerHexHeight = new int[3];

  @Override
  public void onGameLoad(Game p_game)
  {
    m_localLayerHexWidth[0] = p_game.getLandWidth() / 3;
    m_localLayerHexWidth[1] = (p_game.getLandWidth() - m_localLayerHexWidth[0]) / 2;
    m_localLayerHexWidth[2] = p_game.getLandWidth() - m_localLayerHexWidth[0] - m_localLayerHexWidth[1];
    m_localLayerHexHeight[0] = p_game.getLandHeight() / 3;
    m_localLayerHexHeight[1] = (p_game.getLandHeight() - m_localLayerHexHeight[0]) / 2;
    m_localLayerHexHeight[2] = p_game.getLandHeight() - m_localLayerHexHeight[0] - m_localLayerHexHeight[1];
    int cropLeft = 0;
    int cropRight = 0;
    for( int ix = 0; ix < 3; ix++ )
    {
      cropLeft = cropRight;
      cropRight += m_localLayerHexWidth[ix];
      int cropTop = 0;
      int cropBottom = 0;
      for( int iy = 0; iy < 3; iy++ )
      {
        cropTop = cropBottom;
        cropBottom += m_localLayerHexHeight[iy];
        m_cadranLayers[ix][iy].cropDisplay( cropLeft, cropTop, cropRight, cropBottom );
      }
    }

    setLayersPositions( p_game );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseOver(MouseOverEvent event)
  {
    m_layerSelect.setHexagonHightVisible( true );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseOut(MouseOutEvent p_event)
  {
    m_layerSelect.setHexagonHightVisible( false );
  }
}
