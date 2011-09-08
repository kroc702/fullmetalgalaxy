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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.WgtScroll;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtEditTokens extends WgtView implements WindowResizeListener
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private WgtScroll m_wgtScroll = new WgtScroll();
  private WgtToolsEditTokens m_tools = null;
  private WgtBoardEditTokens m_wgtBoard = new WgtBoardEditTokens();

  /**
   * 
   */
  public WgtEditTokens()
  {
    m_tools = new WgtToolsEditTokens( m_wgtBoard );
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
    m_wgtScroll.addScrollListener( m_wgtBoard );
    m_wgtScroll.setWidget( m_wgtBoard );
    m_panel.add( m_tools );
    m_panel.add( m_wgtScroll );
    initWidget( m_panel );
    setSize( "100%", "100%" );
    m_panel.setCellWidth( m_wgtScroll, "100%" );
    m_panel.setCellHeight( m_wgtScroll, "100%" );
    m_panel.setCellWidth( m_tools, "100px" );
    m_panel.setCellHeight( m_tools, "100%" );
    m_tools.setSize( "100px", "100%" );
    Window.addWindowResizeListener( this );
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
    // setSize( "100%", "100%" );
    m_wgtScroll.fireScroll();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    if( isVisible() )
    {
      m_wgtScroll.fireScroll();
      m_wgtBoard.notifyModelUpdate( p_modelSender );
    }
  }



}
