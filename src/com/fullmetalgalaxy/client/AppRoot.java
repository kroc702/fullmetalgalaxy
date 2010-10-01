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
package com.fullmetalgalaxy.client;

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Vincent Legendre
 *
 */

public class AppRoot implements EntryPoint, WindowResizeListener, ClickHandler, HistoryListener,
    SourcesPreviewEvents, NativePreviewHandler
{
  protected PopupPanel m_loadingPanel = new PopupPanel( false, true );
  protected int m_isLoading = 0;
  protected Map m_dialogMap = new HashMap();

  private HistoryState m_historyState = new HistoryState();
  private EventPreviewListenerCollection m_previewListenerCollection = new EventPreviewListenerCollection();

  /**
   * 
   */
  public AppRoot()
  {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
   */
  public void onModuleLoad()
  {
    m_loadingPanel.setWidget( new Image( ClientUtil.getBaseUrl() + "images/loading.cache.gif" ) );
    m_loadingPanel.setVisible( true );
    m_loadingPanel.setStyleName( "gwt-DialogBox" );


    // Hook the window resize event, so that we can adjust the UI.
    // onWindowResized( Window.getClientWidth(), Window.getClientHeight() );
    Window.addWindowResizeListener( this );

    // Hook the preview event.
    // no other element should hook this event as only one can receive it.
    Event.addNativePreviewHandler( this );

    // Add history listener
    History.addHistoryListener( this );

    // If the application starts with no history token, start it off in the
    // 'baz' state.
    String initToken = History.getToken();

    // onHistoryChanged() is not called when the application first runs. Call
    // it now in order to reflect the initial state.
    onHistoryChanged( initToken );

  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.SourcesPreviewEvents#addPreviewListener(com.google.gwt.user.client.EventPreview)
   */
  public void addPreviewListener(NativePreviewHandler p_listener)
  {
    if( !m_previewListenerCollection.contains( p_listener ) )
    {
      m_previewListenerCollection.add( p_listener );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.SourcesPreviewEvents#removePreviewListener(com.google.gwt.user.client.EventPreview)
   */
  public void removePreviewListener(NativePreviewHandler p_listener)
  {
    m_previewListenerCollection.remove( p_listener );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
   */
  @Override
  public void onPreviewNativeEvent(NativePreviewEvent p_event)
  {
    m_previewListenerCollection.fireEventPreview( p_event );
  }


  /**
   * 
   * @return the current history state.
   */
  public HistoryState getHistoryState()
  {
    return m_historyState;
  }

  /**
   * Called when the browser window is resized.
   */
  public void onWindowResized(int p_width, int p_height)
  {
    if( m_isLoading > 0 )
    {
      m_loadingPanel.center();
    }
    // m_dockPanel.setHeight( "" + (p_height - 20) + "px" );
  }

  public void onClick(ClickEvent p_event)
  {

  }

  /**
   * this method should return the list of all possible MiniApp this application can show.
   * key is a string which match the div id of the html source
   * value is the corresponding MiniApp. 
   * @return
   */
  protected MiniApp getMApp(String p_key)
  {
    return null;
  }

  /**
   * this method should return the default history token which contain the first MiniApp
   * which have to be displayed on module loading.
   * @return
   */
  public HistoryState getDefaultHistoryState()
  {
    if( RootPanel.get( "app_history" ) != null )
    {
      return new HistoryState( DOM.getElementAttribute(
          RootPanel.get( "app_history" ).getElement(), "content" ) );
    }
    return new HistoryState();
  }

  /**
   * display all MiniApp found in p_historyToken and hide the other one.
   * @see com.google.gwt.user.client.HistoryListener#onHistoryChanged(java.lang.String)
   */
  public void onHistoryChanged(String p_historyToken)
  {
    HistoryState oldHistoryState = m_historyState;
    if( p_historyToken.length() == 0 )
    {
      m_historyState = getDefaultHistoryState();
    }
    else
    {
      m_historyState = new HistoryState( p_historyToken );
    }
    for( String key : m_historyState.keySet() )
    {
      if( getMApp( key ) != null )
      {
        // this mini app is present in history: show it
        show( key, getMApp( key ) );
      }
    }
    // hide useless mini app
    for( String key : oldHistoryState.keySet() )
    {
      if( (!m_historyState.containsKey( key ) ) && (getMApp( key ) != null))
      {
        hide( key, getMApp( key ) );
      }
    }
  }

  protected void show(String p_id, MiniApp p_miniApp)
  {
    assert p_miniApp != null;
    RootPanel panel = RootPanel.get( p_id );
    if( panel != null )
    {
      panel.setVisible( true );
      if( p_miniApp.getTopWidget() != null )
      {
        if( panel.getWidgetCount() == 0 )
        {
          panel.add( p_miniApp.getTopWidget() );
        }
      }
    }
    else
    {
      RpcUtil.logDebug( "couldn't display mini app " + p_id );
    }
    p_miniApp.show( getHistoryState() );
  }

  private void hide(String p_id, MiniApp p_miniApp)
  {
    if( p_miniApp == null )
    {
      return;
    }
    p_miniApp.hide();
    RootPanel panel = RootPanel.get( p_id );
    if( panel != null )
    {
      if( p_miniApp.getTopWidget() != null )
      {
        panel.remove( p_miniApp.getTopWidget() );
      }
      panel.setVisible( false );
    }
  }

  public void startLoading()
  {
    if( m_isLoading < 0 )
    {
      m_isLoading = 0;
    }
    m_isLoading++;
    m_loadingPanel.show();
    m_loadingPanel.center();
  }

  public void stopLoading()
  {

    if( m_isLoading > 0 )
    {
      m_isLoading--;
    }
    if( m_isLoading == 0 )
    {
      m_loadingPanel.hide();
    }
  }



}
