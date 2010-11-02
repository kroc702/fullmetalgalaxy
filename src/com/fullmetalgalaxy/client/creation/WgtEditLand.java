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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtScroll;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtEditLand extends WgtView implements WindowResizeListener
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private WgtScroll m_wgtScroll = new WgtScroll();
  private WgtBoardEditLand m_wgtBoard = new WgtBoardEditLand();
  private WgtToolsEditLands m_tools = new WgtToolsEditLands( m_wgtBoard );

  /**
   * 
   */
  public WgtEditLand()
  {
    Window.addWindowResizeListener( this );
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    m_wgtScroll.setWidget( m_wgtBoard );
    m_panel.add( m_tools );
    m_panel.setCellWidth( m_tools, "100px" );
    m_panel.setCellHeight( m_tools, "100%" );
    m_panel.add( m_wgtScroll );
    m_panel.setCellWidth( m_wgtScroll, "100%" );
    m_panel.setCellHeight( m_wgtScroll, "100%" );
    initWidget( m_panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
   */
  @Override
  public void onWindowResized(int p_width, int p_height)
  {
    if( !isVisible() )
    {
      return;
    }
    m_wgtScroll.fireScroll();
  }


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
      m_wgtBoard.onModelChange();
      int newBoardWidth = m_wgtBoard.getOffsetWidth();
      int newBoardHeight = m_wgtBoard.getOffsetHeight();
      int newScrollPositionX = (int)((float)newBoardWidth / oldBoardWidth
          * (oldScrollPositionX + screenWidth / 2) - screenWidth / 2);
      int newScrollPositionY = (int)((float)newBoardHeight / oldBoardHeight
          * (oldScrollPositionY + screenHeight / 2) - screenHeight / 2);
      m_wgtScroll.setScrollPosition( newScrollPositionX, newScrollPositionY );
    }
    else
    {
      m_wgtBoard.onModelChange();
    }
    m_wgtScroll.ensureWidgetIsVisible();
    m_tools.redraw();
  }


}
