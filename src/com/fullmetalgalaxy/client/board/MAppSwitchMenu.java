/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class MAppSwitchMenu extends MApp implements ClickListener, EventPreview
{
  public static final String HISTORY_ID = "switch";

  private Image m_button = new Image( "images/blank.gif" );
  private boolean m_isMenuVisible = true;


  /**
   * 
   */
  public MAppSwitchMenu()
  {
    super();
    m_button.addClickListener( this );
    m_button.setSize( "7px", "100%" );

    initWidget( m_button );
    setWidth( "7px" );
    setStyleName( "fmp-switch-menu" );
  }


  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MApp#hide()
   */
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
  public void show(HistoryState p_state)
  {
    super.show( p_state );
    hideMenu();
    AppMain.instance().addPreviewListener( this );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  public boolean onEventPreview(Event p_event)
  {
    if( (DOM.eventGetType( p_event ) == Event.ONKEYPRESS) && (DOM.eventGetCtrlKey( p_event )) )
    {
      if( DOM.eventGetKeyCode( p_event ) == 'm' || DOM.eventGetKeyCode( p_event ) == 'M' )
      {
        onClick( m_button );
        // cancel event
        return false;
      }
    }
    return true;
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
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

  private Timer m_hideTimer = new Timer()
  {
    public void run()
    {
      hideMenu();
    }
  };

}
