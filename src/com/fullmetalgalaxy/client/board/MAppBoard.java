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


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtScroll;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * @author Vincent Legendre
 *
 */

public class MAppBoard extends MApp implements ResizeHandler
{
  public static final String HISTORY_ID = "board";

  public static MessagesAppBoard s_messages = (MessagesAppBoard)GWT.create( MessagesAppBoard.class );

  public static final int s_DefaultZoom = EnuZoom.Medium;

  protected static MAppBoard s_instance = null;

  public static final String s_TokenZoom = "zoom";
  public static final String s_TokenGrid = "grid";
  public static final String s_TokenIdGame = "idGame";
  public static final String s_TokenFireCover = "fireCover";

  private WgtScroll m_wgtScroll = new WgtScroll();
  private WgtBoard m_wgtBoard = new WgtBoard();


  /**
   * 
   */
  public MAppBoard()
  {
    s_instance = this;
    m_wgtScroll.addScrollListener( m_wgtBoard );
    m_wgtScroll.setWidget( m_wgtBoard );
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    // Hook the window resize event, so that we can adjust the UI.
    Window.addResizeHandler( this );
    initWidget( m_wgtScroll );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
   */
  public void onResize(ResizeEvent event)
  {
    if( !isVisible() )
    {
      return;
    }
    m_wgtScroll.fireScroll();
  }

  private long m_idGame = 0;
  private int m_oldZoomValue = EnuZoom.Unknown;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    if( !isVisible() )
    {
      return;
    }
    if( m_oldZoomValue != ModelFmpMain.model().getZoomDisplayed().getValue() )
    {
      m_oldZoomValue = ModelFmpMain.model().getZoomDisplayed().getValue();
      int oldBoardWidth = m_wgtBoard.getOffsetWidth();
      int oldBoardHeight = m_wgtBoard.getOffsetHeight();
      int oldScrollPositionX = m_wgtScroll.getHorizontalScrollPosition();
      int oldScrollPositionY = m_wgtScroll.getVerticalScrollPosition();
      int screenWidth = m_wgtScroll.getOffsetWidth();
      int screenHeight = m_wgtScroll.getOffsetHeight();
      m_wgtBoard.notifyModelUpdate( p_ModelSender );
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
      m_wgtBoard.notifyModelUpdate( p_ModelSender );
    }
    if( m_idGame != ModelFmpMain.model().getGame().getId() )
    {
      m_idGame = ModelFmpMain.model().getGame().getId();
      m_wgtScroll.centerContentWidget();
    }
    m_wgtScroll.ensureWidgetIsVisible();
  }

  public void setScrollPosition(int p_hexX, int p_hexY)
  {
    int boardWidth = m_wgtBoard.getOffsetWidth();
    int boardHeight = m_wgtBoard.getOffsetHeight();
    // cast are here to avoid wrong approximations
    int newScrollPositionX = (int)(((float)boardWidth * p_hexX) / ModelFmpMain.model().getGame()
        .getLandWidth());
    int newScrollPositionY = (int)(((float)boardHeight * p_hexY) / ModelFmpMain.model().getGame()
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

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  @Override
  public void show(HistoryState p_state)
  {
    // m_wgtScroll.setScrollPosition( 0, 0 );
    m_wgtBoard.show( p_state );


    String strIdGame = p_state.getString( MAppBoard.s_TokenIdGame );
    if( (strIdGame != null) && (!ModelFmpMain.model().getGameId().equals( strIdGame )) )
    {
      ModelFmpMain.model().setGameId( strIdGame );
      AppMain.instance().loadModelFmpBoard( strIdGame );
    }
  }

}
