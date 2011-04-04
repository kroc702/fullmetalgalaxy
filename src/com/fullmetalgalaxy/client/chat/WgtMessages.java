/**
 * 
 */
package com.fullmetalgalaxy.client.chat;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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
public class WgtMessages extends Composite implements ClickHandler, KeyPressHandler
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
    m_text.addKeyPressHandler( this );
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
  public void onKeyPress(KeyPressEvent p_event)
  {
    // System.out.println( "char=" + (int)p_event.getUnicodeCharCode() );
    if( p_event.getCharCode() == 13 || p_event.getCharCode() == 0 )
    {
      // KEY_ENTER
      sendMessage();
    }
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

}
