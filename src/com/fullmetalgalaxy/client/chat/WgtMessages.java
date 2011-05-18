/**
 * 
 */
package com.fullmetalgalaxy.client.chat;

import com.fullmetalgalaxy.client.ressources.smiley.SmileyCollection;
import com.fullmetalgalaxy.model.ChatMessage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent
 * UI to send/receive message
 */
public class WgtMessages extends Composite implements ClickHandler, KeyDownHandler
{
  private Panel m_msgList = new VerticalPanel();
  private Button m_btnOk = new Button( "Envoyer" );
  private TextBox m_text = new TextBox();
  ScrollPanel scrollPanel = new ScrollPanel();
  private Chat m_chat = null;
  /**
   * 
   */
  public WgtMessages(Chat p_chat)
  {
    assert p_chat != null;
    m_chat = p_chat;

    Panel panel = new VerticalPanel();
    panel.setSize( "100%", "100%" );

    scrollPanel.setHeight( "400px" );
    m_msgList.setStyleName( "msglist" );
    scrollPanel.add( m_msgList );
    panel.add( scrollPanel );

    Panel hpanel = new HorizontalPanel();
    hpanel.setSize( "100%", "100%" );
    hpanel.add( m_text );
    m_text.setWidth( "100%" );
    m_text.addKeyDownHandler( this );
    hpanel.add( m_btnOk );
    m_btnOk.addClickHandler( this );
    panel.add( hpanel );

    initWidget( panel );
  }


  public void addMessage(String p_msg)
  {
    Label label = new Label( p_msg );
    m_msgList.add( label );
    scrollPanel.ensureVisible( label );
  }

  public void addMessage(ChatMessage p_msg)
  {
    String text = SafeHtmlUtils.htmlEscape( p_msg.getText() );
    text = SmileyCollection.INSTANCE.remplace( text );
    text = text.replace( "\n", "<br/>" );
    HTML label = new HTML( "<b>["+p_msg.getFromPseudo()+"]</b> "+text );
    m_msgList.add( label );
    scrollPanel.ensureVisible( label );
  }

  protected void sendMessage()
  {
    if( !m_text.getText().isEmpty() )
    {
      m_chat.sendMessage( m_text.getText() );
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
