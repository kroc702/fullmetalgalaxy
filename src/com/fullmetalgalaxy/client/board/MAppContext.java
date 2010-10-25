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
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * display information about the selected token
 */

public final class MAppContext extends MApp implements NativePreviewHandler
{
  public static final String HISTORY_ID = "context";

  private VerticalPanel m_panelMiniMap = new VerticalPanel();
  private HorizontalPanel m_panelAction = new HorizontalPanel();
  private HorizontalPanel m_panelExtra = new HorizontalPanel();
  private WgtContextMinimap m_wgtMinimap = new WgtContextMinimap();
  private WgtContextToken m_wgtToken = new WgtContextToken();
  private WgtContextTimeMode m_wgtTimeMode = new WgtContextTimeMode();
  private WgtContextPlayers m_wgtPlayers = null;

  private DlgChatInput m_dlgChat = new DlgChatInput();


  public MAppContext()
  {
    super();
    m_wgtPlayers = new WgtContextPlayers( m_dlgChat );

    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    VerticalPanel vpanel = new VerticalPanel();
    vpanel.add( m_panelAction );
    vpanel.setCellHorizontalAlignment( m_panelAction, HasHorizontalAlignment.ALIGN_RIGHT );
    HorizontalPanel hpanel = new HorizontalPanel();

    hpanel.add( m_panelExtra );
    hpanel.setCellHeight( m_panelExtra, "100%" );
    m_panelExtra.setHeight( "100%" );
    hpanel.add( m_panelMiniMap );

    vpanel.add( hpanel );


    m_panelAction.add( new WgtContextAction() );
    m_panelAction.setStylePrimaryName( "fmp-context-action" );
    m_panelMiniMap.setStylePrimaryName( "fmp-context-minimap" );
    m_panelMiniMap.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    m_panelMiniMap.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    // m_panelMiniMap.addStyleName( "transparent50" );
    m_panelExtra.add( new WgtContextExtra() );
    m_panelExtra.setStylePrimaryName( "fmp-context-extra" );
    // m_panelExtra.addStyleName( "transparent50" );

    initWidget( vpanel );
    hpanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_BOTTOM );
  }


  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  @Override
  public void onPreviewNativeEvent(NativePreviewEvent p_event)
  {
    if( p_event.getTypeInt() == Event.ONKEYPRESS )
    {
      if( m_dlgChat.isChatMode() )
      {
        // don't catch any key if chat dialog is visible
        return;
      }
      if( p_event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER )
      {
        if( ModelFmpMain.model().getGame().getGameType() == GameType.MultiPlayer )
        {
          m_dlgChat.center();
          m_dlgChat.show();
        }
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == 'f'
          || p_event.getNativeEvent().getKeyCode() == 'F' )
      {
        if( ModelFmpMain.model().isFireCoverDisplayed() )
        {
          ModelFmpMain.model().setFireCoverDisplayed( false );
        }
        else
        {
          ModelFmpMain.model().setFireCoverDisplayed( true );
        }
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == '+' )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Medium );
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == '-' )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Small );
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == 'g'
          || p_event.getNativeEvent().getKeyCode() == 'G' )
      {
        ModelFmpMain.model().setGridDisplayed( !ModelFmpMain.model().isGridDisplayed() );
        // cancel event
        p_event.cancel();
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE )
      {
        try
        {
          ModelFmpMain.model().getActionBuilder().userCancel();
          ModelFmpMain.model().notifyModelUpdate();
        } catch( RpcFmpException e )
        {
          MAppMessagesStack.s_instance.showWarning( Messages.getString( e ) );
        }
        // cancel action
        return;
      }
    }
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
   * @see com.fullmetalgalaxy.client.MApp#show(com.fullmetalgalaxy.client.HistoryState)
   */
  @Override
  public void show(HistoryState p_state)
  {
    super.show( p_state );

    EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
    if( p_state.containsKey( MAppBoard.HISTORY_ID ) )
    {
      AppMain.instance().addPreviewListener( this );
      m_panelMiniMap.remove( m_wgtMinimap );
      m_panelMiniMap.remove( m_wgtPlayers );
      m_panelMiniMap.remove( m_wgtToken );
      m_panelMiniMap.remove( m_wgtTimeMode );
      if( actionBuilder.isTokenSelected() )
      {
        // display current selected token
        m_panelMiniMap.add( m_wgtToken );
        m_wgtToken.redraw();
      }
      else if( ModelFmpMain.model().isTimeLineMode() )
      {
        // display time line to navigate in past actions
        m_panelMiniMap.add( m_wgtTimeMode );
        m_wgtTimeMode.redraw();
      }
      else if( ModelFmpMain.model().isMiniMapDisplayed() )
      {
        // display minimap
        m_panelMiniMap.add( m_wgtMinimap );
        m_wgtMinimap.redraw();
      }
      else
      {
        // display players connections informations
        m_panelMiniMap.add( m_wgtPlayers );
        m_wgtPlayers.redraw();
      }
    }
    else
    {
      AppMain.instance().removePreviewListener( this );
      // nothing special to display
      m_panelMiniMap.clear();
      // m_panelMiniMap.add( new Image( "../images/logo.gif" ) );
    }
  }
}
