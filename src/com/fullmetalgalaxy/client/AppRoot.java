/**
 * 
 */
package com.fullmetalgalaxy.client;

import java.util.HashMap;
import java.util.Map;


import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class AppRoot implements EntryPoint, WindowResizeListener, ClickListener, HistoryListener,
    SourcesPreviewEvents, EventPreview
{
  protected PopupPanel m_loadingPanel = new PopupPanel();
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

    m_loadingPanel.setWidget( new Image( ClientUtil.getBaseUrl() + "icons/loading.cache.gif" ) );


    // Hook the window resize event, so that we can adjust the UI.
    // onWindowResized( Window.getClientWidth(), Window.getClientHeight() );
    Window.addWindowResizeListener( this );

    // Hook the preview event.
    // no other element should hook this event as only one can receive it.
    DOM.addEventPreview( this );


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
  public void addPreviewListener(EventPreview p_listener)
  {
    m_previewListenerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.SourcesPreviewEvents#removePreviewListener(com.google.gwt.user.client.EventPreview)
   */
  public void removePreviewListener(EventPreview p_listener)
  {
    m_previewListenerCollection.remove( p_listener );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  public boolean onEventPreview(Event p_event)
  {
    return m_previewListenerCollection.fireEventPreview( p_event );
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

  public void onClick(Widget sender)
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
