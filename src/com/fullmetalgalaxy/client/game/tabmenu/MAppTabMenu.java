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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.tabmenu;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.event.GameActionEvent;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class MAppTabMenu extends GuiEntryPoint implements ValueChangeHandler<Boolean>,
    NativePreviewHandler, GameActionEvent.Handler, ModelUpdateEvent.Handler
{
  public static final String HISTORY_ID = "tabmenu";

  private HorizontalPanel m_hPanel = new HorizontalPanel();
  private VerticalPanel m_vTabPanel = new VerticalPanel();
  private ToggleButton m_btnWebLinks = new ToggleButton( Icons.s_instance.webLinks32().createImage() );
  private ToggleButton m_btnInfo = new ToggleButton( Icons.s_instance.info32().createImage() );
  private ToggleButton m_btnMessage = new ToggleButton( Icons.s_instance.message32().createImage() );
  private ToggleButton m_btnPlayer = new ToggleButton( Icons.s_instance.player32().createImage() );
  private ToggleButton m_btnReserve = new ToggleButton( Icons.s_instance.reserve32().createImage() );
  private ToggleButton m_btnTime = new ToggleButton( Icons.s_instance.time32().createImage() );
  
  private PushButton m_btnSwitchOff = new PushButton();

  private Widget m_wgtCurrentTab = null;
  
  
  /**
   * Create a new timer that will hide menu
   */
  private Timer m_hideTimer = new Timer()
  {
    @Override
    public void run()
    {
      closeAllTab();
    }
  };

  private void addTabButton(ToggleButton p_button, String p_title)
  {
    p_button.addValueChangeHandler( this );
    p_button.setTitle( p_title );
    p_button.setStyleName( "fmp-PushButton32" );
    m_vTabPanel.add( p_button );
  }
  
  /**
   * 
   */
  public MAppTabMenu()
  {
    super();
    m_btnSwitchOff.setStyleName( "fmp-tab-switchoff" );
    m_btnSwitchOff.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        closeAllTab();
      }
    });
    
    m_hPanel.add( m_vTabPanel );
    addTabButton(m_btnWebLinks,"Menu du site web");
    addTabButton(m_btnInfo,"Autre info");
    addTabButton(m_btnMessage,"Messages");
    addTabButton(m_btnPlayer,"Afficher les joueurs");
    addTabButton( m_btnReserve, "Réserve" );
    addTabButton(m_btnTime,"Voir l'historique");
    
    m_vTabPanel.setSize( "100%", "100%" );
    m_vTabPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    m_vTabPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    m_hPanel.setHeight( "100%" );
    initWidget( m_hPanel );
    AppMain.getEventBus().addHandler( GameActionEvent.TYPE, this );
    
    if( GameEngine.model().getGame().getGameType() != GameType.MultiPlayer 
        && GameEngine.model().getGame().getMessage() != null
        && !GameEngine.model().getGame().getMessage().isEmpty() )
    {
      openTab(m_btnMessage);
    }
    else if( GameEngine.model().getGame().getGameType() == GameType.MultiPlayer
        && GameEngine.model().getMyRegistration() != null
        && GameEngine.model().getGame()
        .haveNewMessage( GameEngine.model().getMyRegistration().getLastConnexion() ) )
    {
      openTab( m_btnMessage );
    }
    else
    {
      openTab(m_btnWebLinks);
      m_hideTimer.schedule( 4000 );
    }
  }


  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
    AppMain.instance().addPreviewListener( this );
    AppMain.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }
  
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MApp#hide()
   */
  @Override
  public void hide()
  {
    super.hide();
    AppMain.instance().removePreviewListener( this );
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
        // cancel event
        return;
      }
    }
  }

  private void closeAllTab()
  {
    m_hideTimer.cancel();
    m_btnWebLinks.setDown( false );
    m_btnInfo.setDown( false );
    m_btnMessage.setDown( false );
    m_btnPlayer.setDown( false );
    m_btnReserve.setDown( false );
    m_btnTime.setDown( false );
    if( m_btnSwitchOff != null )
    {
      m_hPanel.remove( m_btnSwitchOff );
    }
    if( m_wgtCurrentTab != null )
    {
      m_hPanel.remove( m_wgtCurrentTab );
    }
    m_wgtCurrentTab = null;
  }

  private void openTab(Object p_button)
  {
    if( p_button == m_btnWebLinks )
    {
      m_btnWebLinks.setDown( true );
      m_wgtCurrentTab = new WgtWebLinks();
    }
    if( p_button == m_btnInfo )
    {
      m_btnInfo.setDown( true );
      m_wgtCurrentTab = new WgtGameInfo();
    }
    if( p_button == m_btnMessage )
    {
      m_btnMessage.setDown( true );
      m_wgtCurrentTab = new WgtMessages();
    }
    if( p_button == m_btnPlayer )
    {
      m_btnPlayer.setDown( true );
      m_wgtCurrentTab = new WgtPlayers();
    }
    if( p_button == m_btnReserve )
    {
      m_btnReserve.setDown( true );
      m_wgtCurrentTab = new WgtConstructReserve();
    }
    if( p_button == m_btnTime )
    {
      m_btnTime.setDown( true );
      m_wgtCurrentTab = new WgtGameTimeMode();
    }
    m_hPanel.add( m_wgtCurrentTab );
    m_hPanel.add( m_btnSwitchOff );

    // HMI action that can't be done before widget is displayed
    if( m_wgtCurrentTab instanceof WgtMessages )
    {
      ((WgtMessages)m_wgtCurrentTab).scrollToBottom();
    }
  }
  
  
  @Override
  public void onValueChange(ValueChangeEvent<Boolean> p_event)
  {
    // remove current widget
    closeAllTab();
    if( p_event.getValue() == false )
    {
      // unselect a tab
      return;
    }
    
    openTab(p_event.getSource());
  }

  @Override
  public void onGameEvent(AnEvent p_message)
  {
    if( p_message instanceof EbEvtMessage )
    if( AppMain.instance().getMyAccount().getId() == 0
          || ((EbEvtMessage)p_message).getAccountId() != AppMain.instance().getMyAccount().getId() )
    {
      closeAllTab();
      openTab( m_btnMessage );
    }
  }

  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    if( !p_modelSender.isTimeLineMode() && m_btnTime.isDown() )
    {
      closeAllTab();
    }

  }



}
