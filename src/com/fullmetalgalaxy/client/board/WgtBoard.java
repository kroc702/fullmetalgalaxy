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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.EnuNavigator;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoard extends FocusPanel implements ScrollListener, MouseDownHandler,
    MouseUpHandler, MouseOverHandler, MouseMoveHandler, MouseOutHandler
{
  AbsolutePanel m_panel = new AbsolutePanel();

  WgtBoardLayerLand m_layerLand = new WgtBoardLayerLand();
  WgtBoardLayerMap m_layerMap = new WgtBoardLayerMap();
  WgtBoardLayerFireCover m_layerCover = new WgtBoardLayerFireCover();
  WgtBoardLayerGrid m_layerGrid = new WgtBoardLayerGrid();
  WgtBoardLayerToken m_layerToken = new WgtBoardLayerToken();
  WgtBoardLayerAction m_layerAction = new WgtBoardLayerAction();
  WgtBoardLayerSelect m_layerSelect = new WgtBoardLayerSelect();
  WgtBoardLayerAtmosphere m_layerAtmosphere = new WgtBoardLayerAtmosphere();

  BoardLayerCollection m_layerCollection = new BoardLayerCollection();

  /**
   * 
   */
  public WgtBoard()
  {
    addLayer( m_layerLand );
    addLayer( m_layerMap );
    addLayer( m_layerCover );
    addLayer( m_layerGrid );
    addLayer( m_layerSelect );
    addLayer( m_layerToken );
    addLayer( m_layerAction );
    if( ClientUtil.getNavigator() == EnuNavigator.FF )
    {
      addLayer( m_layerAtmosphere );
    }
    // m_panel.setSize( "100%", "100%" );
    // setSize( "100%", "100%" );
    setWidget( m_panel );
    sinkEvents( Event.ONCONTEXTMENU );
    addMouseDownHandler( this );
    addMouseMoveHandler( this );
    addMouseOutHandler( this );
    addMouseOverHandler( this );
    addMouseUpHandler( this );
  }

  private void addLayer(BoardLayer p_layer)
  {
    m_panel.add( p_layer.getTopWidget(), 0, 0 );
    m_layerCollection.add( p_layer );
  }

  protected boolean m_isVisible = false;

  public void show(HistoryState p_state)
  {
    if( !m_isVisible )
    {
      m_layerCollection.show();
      m_isVisible = true;
      ClientUtil.scrollToTop();
      Window.enableScrolling( false );
    }

    // map or standard land layer ?
    // m_layerMap.setVisible( !ModelFmpMain.model().isStandardLandDisplayed() );
    // m_layerLand.setVisible( ModelFmpMain.model().isStandardLandDisplayed() );

    // grid
    m_layerGrid.setVisible( p_state.containsKey( MAppBoard.s_TokenGrid ) );

    // atmosphere
    m_layerAtmosphere.setVisible( ModelFmpMain.model().isAtmosphereDisplayed() );

    // zoom
    EnuZoom zoom = new EnuZoom( p_state.getInt( MAppBoard.s_TokenZoom ) );
    if( !p_state.containsKey( MAppBoard.s_TokenZoom ) )
    {
      zoom.setValue( MAppBoard.s_DefaultZoom );
    }
    setZoom( zoom );

    // fire cover
    m_layerCover.displayFireCover( false );
    if( p_state.containsKey( MAppBoard.s_TokenFireCover ) )
    {
      for( EbRegistration registration : ModelFmpMain.model().getGame().getSetRegistration() )
      {
        m_layerCover.displayFireCover( true, registration );
      }
      // TODO should we put back distinction between fire cover ?
      /*String[] fireCover = p_state.getStringArray( MAppBoard.s_TokenFireCover );
      for( int i = 0; i < fireCover.length; i++ )
      {
        EbRegistration registration = ModelFmpMain.model().getGame().getRegistration(
            Long.parseLong( fireCover[i] ) );
        if( registration != null )
        {
          m_layerCover.displayFireCover( true, registration );
        }
      }*/
    }

  }

  public void hide()
  {
    m_isVisible = false;
    m_layerCollection.hide();
    Window.enableScrolling( true );
  }

  
  
  /**
   * to get rid of browser contextual menu.
   */
  @Override
  public void onBrowserEvent(Event p_event)
  {
    switch (DOM.eventGetType(p_event)) 
    {
    case Event.ONCONTEXTMENU:
      //p_event.cancelBubble(true);
      p_event.stopPropagation();
      p_event.preventDefault();
      break;
    default:
      super.onBrowserEvent( p_event );
      break; 
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseDown(MouseDownEvent p_event)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
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
    AnBoardPosition position = WgtBoardLayerBase.convertPixPositionToHexPosition( new AnPair(
        p_event.getX(), p_event.getY() ), getZoom() );
    if( (position.getX() != m_hexagonHightlightPosition.getX())
        || (position.getY() != m_hexagonHightlightPosition.getY()) )
    {
      m_hexagonHightlightPosition = position;
      m_layerSelect.moveHightLightHexagon( m_hexagonHightlightPosition );
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(MouseUpEvent p_event)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    p_event.preventDefault();
    AnBoardPosition position = convertPixPositionToHexPosition( new AnPair( p_event.getX(), p_event
        .getY() ) );
    // EbActionPlay action = ModelFmpMain.model().getAction();
    // ActionPlayBuilder actionBuilder =
    // ModelFmpMain.model().getActionBuilder();


    try
    {
      EventBuilderMsg eventBuilderMsg = EventBuilderMsg.None;
      // this is a test to avoid select current token
      if( position.equals( ModelFmpMain.model().getActionBuilder().getLastUserClick() ) )
      {
        ModelFmpMain.model().getActionBuilder().check();

        ModelFmpMain.model().getActionBuilder().userOk();
        ModelFmpMain.model().runCurrentAction();
      }
      else 
      {
        boolean searchPath = p_event.isControlKeyDown() || p_event.getNativeButton() == NativeEvent.BUTTON_RIGHT;
        eventBuilderMsg = ModelFmpMain.model().getActionBuilder().userBoardClick( position, searchPath );
      }
      switch( eventBuilderMsg )
      {
      case Updated:
        ModelFmpMain.model().notifyModelUpdate();
        break;
      case MustRun:
        ModelFmpMain.model().runCurrentAction();
        break;
      default:
      }

    } catch( RpcFmpException ex )
    {
      if( ex.getLocalizedMessage() != null )
      {
        MAppMessagesStack.s_instance.showWarning( ex.getLocalizedMessage() );
      }
      ModelFmpMain.model().getActionBuilder().cancel();
      try
      {
        ModelFmpMain.model().getActionBuilder().userBoardClick( position, false );
      } catch( Throwable iniore )
      {
      }
      ModelFmpMain.model().notifyModelUpdate();
    } catch( Throwable ex )
    {
      Window.alert( "Une erreur est survenu, la page va être rechargé \n" + ex.getMessage() );
      ClientUtil.sendPM( "" + ModelFmpMain.model().getMyAccount().getId(), "5001", "js error",
          ex.getMessage() );
      ClientUtil.reload();
    }
  }

  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    return WgtBoardLayerBase.convertPixPositionToHexPosition( p_pixPosition, getZoom() );
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
      ModelFmpMain.model().setZoomDisplayed( p_enuZoom );
    }
  }

  protected EnuZoom getZoom()
  {
    return ModelFmpMain.model().getZoomDisplayed();
  }

  protected Game m_game = null;
  private int m_oldZoom = -1;

  public void notifyModelUpdate(SourceModelUpdateEvents p_ctrModelSender)
  {
    if( !m_isVisible )
    {
      return;
    }
    if( m_oldZoom != getZoom().getValue() )
    {
      m_oldZoom = getZoom().getValue();
      m_layerCollection.setZoom( getZoom() );
      m_panel.setPixelSize( m_layerLand.getOffsetWidth(), m_layerLand.getOffsetHeight() );
    }
    else
    {
      m_layerCollection.onModelChange();
      // FF fix.
      if( m_game != ModelFmpMain.model().getGame() )
      {
        m_game = ModelFmpMain.model().getGame();
        m_panel.setPixelSize( m_layerLand.getOffsetWidth(), m_layerLand.getOffsetHeight() );
      }
    }
  }

}
