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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Vincent Legendre
 *
 */
public class MAppSwitchMenu extends MApp implements ClickHandler, NativePreviewHandler
{
  public static final String HISTORY_ID = "switch";

  private Image m_button = new Image( "images/clear.cache.gif" );
  private boolean m_isMenuVisible = true;

  /**
   * Create a new timer that will hide menu
   */
  private Timer m_hideTimer = new Timer()
  {
    @Override
    public void run()
    {
      hideMenu();
    }
  };


  /**
   * 
   */
  public MAppSwitchMenu()
  {
    super();
    m_button.addClickHandler( this );
    m_button.setSize( "7px", "100%" );

    initWidget( m_button );
    setWidth( "7px" );
    setStyleName( "fmp-switch-menu" );
  }


  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MApp#hide()
   */
  @Override
  public void hide()
  {
    super.hide();
    // m_hideTimer.cancel();
    showMenu();
    AppMain.instance().removePreviewListener( this );
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MApp#show(com.fullmetalgalaxy.client.HistoryState)
   */
  @Override
  public void show(HistoryState p_state)
  {
    super.show( p_state );
    showMenu();
    // leave menu longuer at beginning
    m_hideTimer.cancel();
    m_hideTimer.schedule( 15 * 1000 );
    AppMain.instance().addPreviewListener( this );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  @Override
  public void onPreviewNativeEvent(NativePreviewEvent p_event)
  {
    if( (p_event.getTypeInt() == Event.ONKEYPRESS) && (p_event.getNativeEvent().getCtrlKey()) )
    {
      if( p_event.getNativeEvent().getKeyCode() == 'm'
          || p_event.getNativeEvent().getKeyCode() == 'M' )
      {
        onClick( null );
        // cancel event
        return;
      }
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(ClickEvent p_event)
  {
    if( m_isMenuVisible )
    {
      hideMenu();
    }
    else
    {
      showMenu();
    }
    // m_hideTimer.schedule( 3000 );
  }


  private void showMenu()
  {
    RootPanel menu = RootPanel.get( "menu" );
    if( menu != null )
    {
      menu.setVisible( true );
    }
    m_isMenuVisible = true;
    m_hideTimer.schedule( 5 * 1000 );
  }

  private void hideMenu()
  {
    RootPanel menu = RootPanel.get( "menu" );
    if( menu != null )
    {
      menu.setVisible( false );
    }
    m_isMenuVisible = false;
  }


}
