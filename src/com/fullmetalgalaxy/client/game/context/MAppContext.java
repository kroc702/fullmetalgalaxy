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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.context;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.chat.DlgChatInput;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
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

public final class MAppContext extends GuiEntryPoint implements NativePreviewHandler, ModelUpdateEvent.Handler
{
  public static final String HISTORY_ID = "context";

  private VerticalPanel m_panelMiniMap = new VerticalPanel();
  private HorizontalPanel m_panelAction = new HorizontalPanel();
  private HorizontalPanel m_panelExtra = new HorizontalPanel();
  private WgtContextMinimap m_wgtMinimap = new WgtContextMinimap();
  private WgtContextToken m_wgtToken = new WgtContextToken();
  private WgtContextLand m_wgtLand = new WgtContextLand();

  public MAppContext()
  {
    super();

    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
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
      if( DlgChatInput.isChatMode() )
      {
        // don't catch any key if chat dialog is visible
        return;
      }
      if( p_event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER )
      {
        if( GameEngine.model().getGame().getGameType() == GameType.MultiPlayer
            || GameEngine.model().getGame().getGameType() == GameType.Initiation )
        {
          DlgChatInput.showDialog();
        }
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == 'f'
          || p_event.getNativeEvent().getKeyCode() == 'F' )
      {
        if( GameEngine.model().isFireCoverDisplayed() )
        {
          GameEngine.model().setFireCoverDisplayed( false );
        }
        else
        {
          GameEngine.model().setFireCoverDisplayed( true );
        }
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == '+' )
      {
        GameEngine.model().setZoomDisplayed( EnuZoom.Medium );
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == '-' )
      {
        GameEngine.model().setZoomDisplayed( EnuZoom.Small );
        // cancel event
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == 'g'
          || p_event.getNativeEvent().getKeyCode() == 'G' )
      {
        GameEngine.model().setGridDisplayed( !GameEngine.model().isGridDisplayed() );
        // cancel event
        p_event.cancel();
        return;
      }
      else if( p_event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE )
      {
        try
        {
          GameEngine.model().getActionBuilder().userCancel();
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
        } catch( RpcFmpException e )
        {
          MAppMessagesStack.s_instance.showWarning( e.getLocalizedMessage() );
        }
        // cancel action
        return;
      }
    }
  }


  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
    AppMain.instance().addPreviewListener( this );
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





  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    // redraw everything after any model update
    //
    EventsPlayBuilder actionBuilder = p_modelSender.getActionBuilder();
    
    m_panelMiniMap.remove( m_wgtMinimap );
    m_panelMiniMap.remove( m_wgtToken );
    m_panelMiniMap.remove( m_wgtLand );

    if( actionBuilder.isTokenSelected() )
    {
      // display current selected token
      m_panelMiniMap.add( m_wgtToken );
      m_wgtToken.redraw();
    }
    else if( actionBuilder.isEmptyLandSelected() )
    {
      // display information on selected empty land
      m_panelMiniMap.add( m_wgtLand );
      m_wgtLand.redraw();
    }
    else 
    {
      // display minimap
      m_panelMiniMap.add( m_wgtMinimap );
      m_wgtMinimap.redraw();
    }
  }
}
