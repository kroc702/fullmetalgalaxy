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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.MessagesAppBoard;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.client.widget.WgtScroll;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.MapShape;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * @author Vincent Legendre
 *
 */

public class MAppBoard extends GuiEntryPoint implements ResizeHandler, ModelUpdateEvent.Handler
{
  public static final String HISTORY_ID = "board";

  public static MessagesAppBoard s_messages = (MessagesAppBoard)GWT.create( MessagesAppBoard.class );

  public static final int s_DefaultZoom = EnuZoom.Medium;

  protected static MAppBoard s_instance = null;

  private WgtScroll m_wgtScroll = new WgtScroll();
  private WgtBoardBase m_wgtBoard = null;


  /**
   * 
   */
  public MAppBoard()
  {
    s_instance = this;
    initWgtBoard();
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
    // Hook the window resize event, so that we can adjust the UI.
    Window.addResizeHandler( this );
    initWidget( m_wgtScroll );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  
  private void initWgtBoard()
  {
    if( m_wgtBoard != null )
    {
      m_wgtScroll.removeScrollListener( m_wgtBoard );
    }
    if( GameEngine.game().getMapShape() == MapShape.Flat )
    {
      m_wgtBoard = new WgtBoard();
    } else
    {
      m_wgtBoard = new WgtBoardTorus();
    }
    m_wgtScroll.addScrollListener( m_wgtBoard );
    m_wgtScroll.setWidget( m_wgtBoard );
  }
  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
   */
  @Override
  public void onResize(ResizeEvent event)
  {
    if( !isVisible() )
    {
      return;
    }
    m_wgtScroll.fireScroll();
  }

  private long m_idGame = -1;
  private int m_oldZoomValue = EnuZoom.Unknown;

  /**
   * TODO this doesn't work with torus map shape
   * @param p_hexX
   * @param p_hexY
   */
  public void setScrollPosition(int p_hexX, int p_hexY)
  {
    int boardWidth = m_wgtBoard.getOffsetWidth();
    int boardHeight = m_wgtBoard.getOffsetHeight();
    // cast are here to avoid wrong approximations
    int newScrollPositionX = (int)(((float)boardWidth * p_hexX) / GameEngine.model().getGame()
        .getLandWidth());
    int newScrollPositionY = (int)(((float)boardHeight * p_hexY) / GameEngine.model().getGame()
        .getLandHeight());
    // now compute the corresponding top/left position for newScrollPosition.
    int screenWidth = m_wgtScroll.getOffsetWidth();
    int screenHeight = m_wgtScroll.getOffsetHeight();
    newScrollPositionX -= screenWidth / 2;
    newScrollPositionY -= screenHeight / 2;
    m_wgtScroll.setScrollPosition( newScrollPositionX, newScrollPositionY );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  @Override
  public void hide()
  {
    m_wgtBoard.hide();
  }

  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
    m_wgtBoard.show( );
  }
  
  
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    // redraw everything after any model update
    //
    if( !isVisible() )
    {
      return;
    }
 
    if( m_oldZoomValue != GameEngine.model().getZoomDisplayed().getValue() )
    {
      m_oldZoomValue = GameEngine.model().getZoomDisplayed().getValue();
      int oldBoardWidth = m_wgtBoard.getOffsetWidth();
      int oldBoardHeight = m_wgtBoard.getOffsetHeight();
      int oldScrollPositionX = m_wgtScroll.getHorizontalScrollPosition();
      int oldScrollPositionY = m_wgtScroll.getVerticalScrollPosition();
      int screenWidth = m_wgtScroll.getOffsetWidth();
      int screenHeight = m_wgtScroll.getOffsetHeight();
      m_wgtBoard.notifyModelUpdate( p_modelSender );
      int newBoardWidth = m_wgtBoard.getOffsetWidth();
      int newBoardHeight = m_wgtBoard.getOffsetHeight();
      // cast are here to avoid wrong approximations
      int newScrollPositionX = (int)((float)newBoardWidth / oldBoardWidth
          * (oldScrollPositionX + screenWidth / 2) - screenWidth / 2);
      int newScrollPositionY = (int)((float)newBoardHeight / oldBoardHeight
          * (oldScrollPositionY + screenHeight / 2) - screenHeight / 2);
      m_wgtScroll.setScrollPosition( newScrollPositionX, newScrollPositionY );
    }
    else
    {
      m_wgtBoard.notifyModelUpdate( p_modelSender );
    }
    if( m_idGame != GameEngine.model().getGame().getId() )
    {
      //initWgtBoard();
      m_idGame = GameEngine.model().getGame().getId();
      EbToken myFreighter = GameEngine.model().getGame()
          .getFreighter( GameEngine.model().getMyRegistration() );
      if( myFreighter != null && myFreighter.getLocation() == Location.Board )
      {
        setScrollPosition( myFreighter.getPosition().getX(), myFreighter.getPosition().getY() );
      }
      else
      {
        m_wgtScroll.centerContentWidget();
      }
    }
    m_wgtScroll.ensureWidgetIsVisible();
  }


}
