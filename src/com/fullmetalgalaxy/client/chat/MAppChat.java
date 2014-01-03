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

package com.fullmetalgalaxy.client.chat;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.event.ChannelMessageEventHandler;
import com.fullmetalgalaxy.client.ressources.smiley.SmileyCollection;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.model.ChatMessage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class MAppChat extends GuiEntryPoint implements ChannelMessageEventHandler, ClickHandler, KeyDownHandler
{
  public static final String HISTORY_ID = "Chat";

  private Panel m_msgList = new VerticalPanel();
  private Button m_btnOk = new Button( "Envoyer" );
  private TextBox m_text = new TextBox();
  private ScrollPanel scrollPanel = new ScrollPanel();
  private Panel m_panel = new VerticalPanel();
  
  
  public MAppChat()
  {
    m_panel.setSize( "100%", "100%" );

    scrollPanel.setHeight( "400px" );
    m_msgList.setStyleName( "msglist" );
    scrollPanel.add( m_msgList );
    m_panel.add( scrollPanel );

    Panel hpanel = new HorizontalPanel();
    hpanel.setSize( "100%", "100%" );
    hpanel.add( m_text );
    m_text.setWidth( "100%" );
    m_text.addKeyDownHandler( this );
    hpanel.add( m_btnOk );
    m_btnOk.addClickHandler( this );
    m_panel.add( hpanel );

    initWidget( m_panel );
  }
  
  

  
  @Override
  protected void onLoad()
  {
    super.onLoad();
    onChannelMessage( AppMain.instance().getPresenceRoom() );
    AppMain.instance().addChannelMessageEventHandler( ChatMessage.class, this );
  }

  @Override
  protected void onUnload()
  {
    super.onUnload();
    AppMain.instance().removeChannelMessageEventHandler( ChatMessage.class, this );
  }



  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }


  @Override
  public void onChannelMessage(Object p_message)
  {
    if( p_message instanceof ChatMessage )
    {
      ChatMessage p_msg = (ChatMessage)p_message;
      if( !p_msg.isEmpty() )
      {
        String text = SafeHtmlUtils.htmlEscape( p_msg.getText() );
        text = SmileyCollection.INSTANCE.replace( text );
        text = text.replace( "\n", "<br/>" );
        HTML label = new HTML( "<b>[" + p_msg.getFromPseudo() + "]</b> " + text );
        m_msgList.add( label );
        scrollPanel.ensureVisible( label );
      }
    }
  }

  protected void sendMessage()
  {
    if( !m_text.getText().isEmpty() )
    {
      ChatEngine.sendMessage( m_text.getText() );
    }
    m_text.setText( "" );
    setFocus( true );
  }


  @Override
  public void onClick(ClickEvent p_event)
  {
    sendMessage();
  }

  /**
   * @param p_focused
   * @see com.google.gwt.user.client.ui.FocusWidget#setFocus(boolean)
   */
  public void setFocus(boolean p_focused)
  {
    m_text.setFocus( p_focused );
  }


  @Override
  public void onKeyDown(KeyDownEvent p_event)
  {
    if( p_event.getNativeKeyCode() == KeyCodes.KEY_ENTER )
    {
      sendMessage();
    }
  }

}
