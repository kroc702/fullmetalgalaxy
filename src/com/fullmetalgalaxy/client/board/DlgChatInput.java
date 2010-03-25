/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.Services;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Vincent Legendre
 *
 */
public class DlgChatInput extends DialogBox implements ClickHandler, KeyDownHandler,
    KeyPressHandler
{
  // UI
  private Button m_btnOk = new Button( "OK" );
  private Panel m_panel = new HorizontalPanel();
  private TextBox m_text = new TextBox();
  private boolean m_isChatMode = false;

  public DlgChatInput()
  {
    // auto hide / modal
    super( true, true );

    // Set the dialog box's caption.
    setText( "tapez votre message" );
    m_text.addKeyDownHandler( this );
    m_text.addKeyPressHandler( this );
    m_text.setWidth( "400px" );
    m_panel.add( m_text );

    m_btnOk.addClickHandler( this );
    m_btnOk.setWidth( "50px" );
    m_panel.add( m_btnOk );

    setWidget( m_panel );
  }

  protected void sendMessage()
  {
    if( m_text.getText().length() > 0 )
    {
      ChatMessage message = new ChatMessage();
      message.setGameId( ModelFmpMain.model().getGame().getId() );
      message.setFromLogin( ModelFmpMain.model().getMyPseudo() );
      message.setText( m_text.getText() );
      Services.Util.getInstance().sendChatMessage( message,
          ModelFmpMain.model().getLastServerUpdate(), ModelFmpMain.model().getCallbackEvents() );
    }
    hide();
  }


  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnOk )
    {
      sendMessage();
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.KeyUpHandler#onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent)
   */
  @Override
  public void onKeyDown(KeyDownEvent p_event)
  {
    switch( p_event.getNativeKeyCode() )
    {
    case KeyCodes.KEY_ESCAPE:
      hide();
      break;
    // case KeyCodes.KEY_ENTER:
    // sendMessage();
    // break;
    default:
      break;
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
   */
  @Override
  public void onKeyPress(KeyPressEvent p_event)
  {
    if( p_event.getCharCode() == 13 )
    {
      // KEY_ENTER
      sendMessage();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    if( isChatMode() )
    {
      return;
    }
    m_text.setText( "" );
    m_isChatMode = true;
    // center call show method
    // center();
    DeferredCommand.addCommand( new Command()
    {
      public void execute()
      {
        m_text.setFocus( true );
      }
    } );
    super.show();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#hide()
   */
  @Override
  public void hide()
  {
    super.hide();
    m_isChatMode = false;
  }

  public boolean isChatMode()
  {
    return m_isChatMode;
  }

}
