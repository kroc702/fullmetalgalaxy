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
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoard extends WgtBoardBase
{
  WgtBoardLayerLand m_layerLand = new WgtBoardLayerLand();
  WgtBoardLayerMap m_layerMap = new WgtBoardLayerMap();
  WgtBoardLayerFireCover m_layerCover = new WgtBoardLayerFireCover();
  WgtBoardLayerGrid m_layerGrid = new WgtBoardLayerGrid();
  WgtBoardLayerToken m_layerToken = new WgtBoardLayerToken();
  WgtBoardLayerAction m_layerAction = new WgtBoardLayerAction();
  WgtBoardLayerSelect m_layerSelect = new WgtBoardLayerSelect();
  WgtBoardLayerAtmosphere m_layerAtmosphere = new WgtBoardLayerAtmosphere();
  WgtBoardLayerLocked m_layerLock = new WgtBoardLayerLocked();

  BoardLayerCollection m_layerCollection = new BoardLayerCollection();

  /**
   * 
   */
  public WgtBoard()
  {
    super();
    m_layerCollection.addLayer( m_layerLand );
    m_layerCollection.addLayer( m_layerMap );
    m_layerCollection.addLayer( m_layerCover );
    m_layerCollection.addLayer( m_layerGrid );
    m_layerCollection.addLayer( m_layerSelect );
    m_layerCollection.addLayer( m_layerToken );
    m_layerCollection.addLayer( m_layerLock );
    m_layerCollection.addLayer( m_layerAction );
    if( ClientUtil.getNavigator() == EnuNavigator.FF )
    {
      m_layerCollection.addLayer( m_layerAtmosphere );
    }

    m_panel.add( m_layerCollection,0,0);
    // m_vPanel.setSize( "100%", "100%" );
    // setSize( "100%", "100%" );

    // in case game already loaded
    onGameLoad( GameEngine.model().getGame() );
  }

  @Override
  public void onGameLoad(Game p_game)
  {
    m_layerCollection.cropDisplay( 0, 0, p_game.getLandWidth(), p_game.getLandHeight() );
  }

  @Override
  public void show()
  {
    super.show();
    if( !m_isVisible )
    {
      m_layerCollection.show();
    }

    // map or standard land layer ?
    // m_layerMap.setVisible( !ModelFmpMain.model().isStandardLandDisplayed() );
    // m_layerLand.setVisible( ModelFmpMain.model().isStandardLandDisplayed() );

    // grid
    m_layerGrid.setVisible( GameEngine.model().isGridDisplayed() );

    // atmosphere
    m_layerAtmosphere.setVisible( GameEngine.model().isAtmosphereDisplayed() );

    // zoom
    EnuZoom zoom = GameEngine.model().getZoomDisplayed();
    setZoom( zoom );

    // fire cover
    m_layerCover.displayFireCover( GameEngine.model().isFireCoverDisplayed() );
  }

  @Override
  public void hide()
  {
   super.hide();
    m_layerCollection.hide();
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


  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    return BoardConvert.convertPixPositionToHexPosition( p_pixPosition, getZoom(), new AnPair(0,0) );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ScrollListener#onScroll(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onScroll(Widget p_widget, int p_scrollLeft, int p_scrollTop)
  {
    m_layerCollection.redraw( p_scrollLeft, p_scrollTop, p_scrollLeft + p_widget.getOffsetWidth(),
        p_scrollTop + p_widget.getOffsetHeight() );
  }


  protected void setZoom(EnuZoom p_enuZoom)
  {
    if( p_enuZoom.getValue() != getZoom().getValue() )
    {
      GameEngine.model().setZoomDisplayed( p_enuZoom );
    }
  }

  protected EnuZoom getZoom()
  {
    return GameEngine.model().getZoomDisplayed();
  }

  protected Game m_game = null;
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
      m_layerCollection.setZoom( getZoom() );
      if( m_layerLand.isVisible() )
      {
        m_panel.setPixelSize( m_layerLand.getOffsetWidth(), m_layerLand.getOffsetHeight() );
      } else if( m_layerMap.isVisible() )
      {
        m_panel.setPixelSize( m_layerMap.getOffsetWidth(), m_layerMap.getOffsetHeight() );
      }
    }
    else
    {
      m_layerCollection.onModelChange();
      // FF fix.
      if( m_game != p_modelSender.getGame() )
      {
        m_game = p_modelSender.getGame();
        if( m_layerLand.isVisible() )
        {
          m_panel.setPixelSize( m_layerLand.getOffsetWidth(), m_layerLand.getOffsetHeight() );
        } else if( m_layerMap.isVisible() )
        {
          m_panel.setPixelSize( m_layerMap.getOffsetWidth(), m_layerMap.getOffsetHeight() );
        }
      }
    }
  }

}
