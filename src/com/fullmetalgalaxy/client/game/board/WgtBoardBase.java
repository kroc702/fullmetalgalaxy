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
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.event.GameLoadEvent;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ScrollListener;

/**
 * @author Vincent Legendre
 *
 */
public abstract class WgtBoardBase extends FocusPanel implements ScrollListener, MouseDownHandler,
    MouseUpHandler, MouseOverHandler, MouseMoveHandler, MouseOutHandler, GameLoadEvent.Handler
{
  AbsolutePanel m_panel = new AbsolutePanel();

  /**
   * 
   */
  public WgtBoardBase()
  {
    super();
    setWidget( m_panel );
    sinkEvents( Event.ONCONTEXTMENU );
    addMouseDownHandler( this );
    addMouseMoveHandler( this );
    addMouseOutHandler( this );
    addMouseOverHandler( this );
    addMouseUpHandler( this );
    AppRoot.getEventBus().addHandler( GameLoadEvent.TYPE, this );
  }


  protected boolean m_isVisible = false;

  public void show()
  {
    if( !m_isVisible )
    {
      m_isVisible = true;
      ClientUtil.scrollToTop();
      //Window.enableScrolling( false );
    }
  }

  public void hide()
  {
    m_isVisible = false;
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
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(MouseUpEvent p_event)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    p_event.preventDefault();
    AnBoardPosition position = convertPixPositionToHexPosition( new AnPair( p_event.getX(), p_event
        .getY() ) );

    try
    {
      EventBuilderMsg eventBuilderMsg = EventBuilderMsg.None;
      boolean searchPath = p_event.isControlKeyDown()
          || p_event.getNativeButton() == NativeEvent.BUTTON_RIGHT;
      eventBuilderMsg = GameEngine.model().getActionBuilder().userBoardClick( position, searchPath );
      switch( eventBuilderMsg )
      {
      case Updated:
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
        break;
      case MustRun:
        GameEngine.model().runCurrentAction();
        break;
      default:
      }

    } catch( RpcFmpException ex )
    {
      if( ex.getLocalizedMessage() != null )
      {
        MAppMessagesStack.s_instance.showWarning( ex.getLocalizedMessage() );
      }
      GameEngine.model().getActionBuilder().cancel();
      try
      {
        GameEngine.model().getActionBuilder().userBoardClick( position, false );
      } catch( Throwable iniore )
      {
      }
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    } catch( Throwable ex )
    {
      Window.alert( "Une erreur est survenu, la page va être rechargée \n" + ex.getMessage() );
      ClientUtil.sendPM( "" + AppMain.instance().getMyAccount().getId(), "5001", "js error",
          ex.getMessage() );
      ClientUtil.reload();
    }
  }

  protected AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition)
  {
    return BoardConvert.convertPixPositionToHexPosition( p_pixPosition, getZoom(), new AnPair(0,0) );
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

  public abstract void notifyModelUpdate(GameEngine p_modelSender);
  
}
