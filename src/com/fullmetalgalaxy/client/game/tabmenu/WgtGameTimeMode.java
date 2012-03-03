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
package com.fullmetalgalaxy.client.game.tabmenu;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.EventPresenter;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtGameTimeMode extends Composite implements ClickHandler, ModelUpdateEvent.Handler
{
  private Panel m_panel = new VerticalPanel();

  Label m_lblTimePosition = new Label();
  HTML m_lblCurrentEvent = new HTML();
  
  PushButton m_btnFastBack = new PushButton( Icons.s_instance.fastBack32().createImage() );
  PushButton m_btnFastPlay = new PushButton( Icons.s_instance.fastPlay32().createImage() );
  PushButton m_btnBack = new PushButton( Icons.s_instance.back32().createImage() );
  PushButton m_btnPlay = new PushButton( Icons.s_instance.play32().createImage() );
  PushButton m_btnOk = new PushButton( Icons.s_instance.ok32().createImage() );

  private Panel m_btnPanel = new HorizontalPanel();
  private WgtGameLogs m_wgtGameLog = new WgtGameLogs();
  
  
  private HandlerRegistration m_hdlRegistration = null; 

  /**
   * 
   */
  public WgtGameTimeMode()
  {
    super();
    
    m_btnFastBack.addClickHandler( this );
    m_btnFastBack.setTitle( "" );
    m_btnFastBack.setStyleName( "fmp-PushButton32" );
    m_btnFastPlay.addClickHandler( this );
    m_btnFastPlay.setTitle( "" );
    m_btnFastPlay.setStyleName( "fmp-PushButton32" );
    m_btnBack.addClickHandler( this );
    m_btnBack.setTitle( "" );
    m_btnBack.setStyleName( "fmp-PushButton32" );
    m_btnPlay.addClickHandler( this );
    m_btnPlay.setTitle( "" );
    m_btnPlay.setStyleName( "fmp-PushButton32" );

    m_btnOk.addClickHandler( this );
    m_btnOk.setTitle( "Ok" );
    m_btnOk.setStyleName( "fmp-PushButton32" );

    m_panel.add( m_lblTimePosition );
    m_panel.add( m_lblCurrentEvent );
    m_btnPanel.add( m_btnFastBack );
    m_btnPanel.add( m_btnBack );
    m_btnPanel.add( m_btnPlay );
    m_btnPanel.add( m_btnFastPlay );
    m_panel.add( m_btnPanel );
    m_panel.add( m_wgtGameLog );
    
    initWidget( m_panel );
  }

  private void redraw()
  {
    assert GameEngine.model() != null;
    Game game = GameEngine.model().getGame();

    m_lblTimePosition.setText( (GameEngine.model().getCurrentActionIndex() + game
        .getAdditionalEventCount())
        + "/"
        + (game.getLogs().size() + game.getAdditionalEventCount()) );
    AnEvent currentEvent = GameEngine.model().getCurrentAction();
    if( currentEvent == null )
    {
      m_lblCurrentEvent.setHTML( "" );
    }
    else
    {
      m_lblCurrentEvent.setHTML( EventPresenter.getDetailAsHtml( currentEvent ) );
    }
        
    if( AppMain.instance().iAmAdmin() || GameEngine.model().canCancelAction() )
    {
      // in puzzle or turn by turn on several day we allow cancel action
      m_btnPanel.add( m_btnOk );
    }
    else
    {
      m_btnPanel.remove( m_btnOk );
    }
    
    m_btnPanel.remove( m_wgtGameLog );

    if( m_wgtGameLog.getAdditionalEventCount() != game.getAdditionalEventCount() )
    {
      m_wgtGameLog.redraw();
    }
    m_panel.add( m_wgtGameLog );
}


  @Override
  protected void onLoad()
  {
    super.onLoad();
    GameEngine.model().setTimeLineMode( true );
    // register event
    m_hdlRegistration = AppMain.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
    redraw();
  }

  @Override
  protected void onUnload()
  {
    super.onUnload();
    GameEngine.model().setTimeLineMode( false );
    // unregister event
    if( m_hdlRegistration != null )
    {
      m_hdlRegistration.removeHandler();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    Object sender = p_event.getSource();
    if( sender == m_btnOk )
    {
      if( GameEngine.model().getCurrentActionIndex() >= GameEngine.model().getGame().getLogs()
          .size() )
      {
        // user click ok to cancel action
        // but action selected was then last one: no cancel
        GameEngine.model().setTimeLineMode( false );
        return;
      }

      if( AppMain.instance().iAmAdmin() && !GameEngine.model().canCancelAction() )
      {
        // admin is going to perform admin action, show confirm dialog
        // no i18n as it is only for admin
        if( !Window.confirm( "Perform admin cancel ?" ) )
        {
          return;
        }
      }

      // just in case another action was in preparation
      EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
      actionBuilder.clear();
      EbEvtCancel evtCancel = new EbEvtCancel();
      evtCancel.setGame( GameEngine.model().getGame() );
      evtCancel.setFromActionIndex( GameEngine.model().getGame() );
      evtCancel.setToActionIndex( GameEngine.model().getCurrentActionIndex() );
      evtCancel.setAccountId( AppMain.instance().getMyAccount().getId() );
      // this action is required to send the last game version to server
      GameEngine.model().setTimeLineMode( false );
      GameEngine.model().runSingleAction( evtCancel );
    }
    else if( sender == m_btnPlay )
    {
      GameEngine.model().timePlay( 1 );
    }
    else if( sender == m_btnFastPlay )
    {
      GameEngine.model().timePlay( 10 );
    }
    else if( sender == m_btnBack )
    {
      GameEngine.model().timeBack( 1 );
    }
    else if( sender == m_btnFastBack )
    {
      GameEngine.model().timeBack( 10 );
    }
  }

  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    redraw();
  }


}
