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

package com.fullmetalgalaxy.client.game.tabmenu;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.widget.WgtPlayerMessage;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class WgtMessages extends Composite implements BlurHandler
{
  private TextArea m_text = new TextArea();
  private boolean m_isEditableMode = false;
  private ScrollPanel m_scrollPanel = null;
  
  public WgtMessages()
  {
    super();
    
    if( GameEngine.model().getGame().isMessageWebUrl() )
    {
      Frame frame = new Frame( GameEngine.model().getGame().getMessage() );
      frame.setPixelSize( 700, 350 );
      Panel panel = new SimplePanel();
      panel.add( frame );
      initWidget( panel );
    }
    else if( (GameEngine.model().getGame().getGameType() == GameType.MultiPlayer || GameEngine
.model().getGame().getGameType() == GameType.Practice || GameEngine.model()
        .getGame().getGameType() == GameType.Initiation)
        && !GameEngine.model().getGame().isRecordingScript() )
    {
      VerticalPanel verticalPanel = new VerticalPanel();
      m_scrollPanel = new ScrollPanel();
      m_scrollPanel.setStyleName( "fmp-msg-panel" );
      m_scrollPanel.add( verticalPanel );
      Panel panel = new SimplePanel();
      panel.add( m_scrollPanel );
      initMsgList( verticalPanel );
      initWidget( panel );
    }
    else
    {
      // this case become quite rare now...
      // it is mainly used to record test
      Panel panel = new VerticalPanel();
      initEditableMsg( (VerticalPanel)panel, GameEngine.model().getGame().getMessage() );
      m_isEditableMode = true;
      initWidget( panel );
    }
    
  }
  
  public void scrollToBottom()
  {
    if( m_scrollPanel != null )
    {
      m_scrollPanel.scrollToBottom();
    }
  }

  private void initMsgList(VerticalPanel p_panel)
  {
    for( AnEvent event : GameEngine.model().getGame().getLogs() )
    {
      if( event instanceof EbEvtMessage )
      {
        p_panel.add( new WgtPlayerMessage( GameEngine.model().getGame(), (EbEvtMessage)event ) );
      }
    }
    if( p_panel.getWidgetCount() == 0 )
    {
      p_panel.add( new Label( MAppBoard.s_messages.noMessages() ) );
    }
    p_panel.add( m_text );
    m_text.setPixelSize( 400, 60 );
    m_text.addBlurHandler( this );
  }


  /**
   * init widget to edit main game message
   * @param p_text
   */
  private void initEditableMsg(VerticalPanel p_panel, String p_text)
  {
    p_panel.clear();
    p_panel.add( m_text );
    m_text.setPixelSize( 400, 350 );
    m_text.setText( p_text );
    m_text.addBlurHandler( this );
  }


  @Override
  public void onBlur(BlurEvent p_event)
  {
    if( !m_isEditableMode && m_text.getText().isEmpty() )
    {
      return;
    }
    if( m_isEditableMode
        && m_text.getText().equalsIgnoreCase( GameEngine.model().getGame().getMessage() ) )
    {
      // message didn't change: don't send message event
      return;
    }
    if( GameEngine.model().getGame().isRecordingScript() )
    {
      // or we are recording user event
      GameEngine.model().getGame().setMessage( m_text.getText() );
      return;
    }
    EbEvtMessage message = new EbEvtMessage();
    message.setGame( GameEngine.model().getGame() );
    message.setMessage( m_text.getText().trim() );
    message.setAccountId( AppMain.instance().getMyAccount().getId() );
    GameEngine.model().runSingleAction( message );
  }


}
