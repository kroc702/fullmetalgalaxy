/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.Services;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class DlgChatInput extends DialogBox implements ClickListener, KeyboardListener
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
    m_text.addKeyboardListener( this );
    m_text.setWidth( "400px" );
    m_panel.add( m_text );

    m_btnOk.addClickListener( this );
    m_btnOk.setWidth( "50px" );
    m_panel.add( m_btnOk );

    setWidget( m_panel );
  }

  protected void sendMessage()
  {
    ChatMessage message = new ChatMessage();
    message.setGameId( ModelFmpMain.model().getGame().getId() );
    message.setFromLogin( ModelFmpMain.model().getMyPseudo() );
    message.setText( m_text.getText() );
    Services.Util.getInstance().sendChatMessage( message,
        ModelFmpMain.model().getLastServerUpdate(), ModelFmpMain.model().getCallbackEvents() );
    hide();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnOk )
    {
      sendMessage();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyPress(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyPress(Widget p_sender, char p_keyCode, int p_modifiers)
  {
    switch( p_keyCode )
    {
    case KeyboardListener.KEY_ESCAPE:
      hide();
      break;
    case KeyboardListener.KEY_ENTER:
      if( m_text.getText().length() > 0 )
      {
        sendMessage();
      }
      break;
    default:
      break;
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyDown(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyDown(Widget p_sender, char p_keyCode, int p_modifiers)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyUp(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyUp(Widget p_sender, char p_keyCode, int p_modifiers)
  {
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
    DeferredCommand.add( new Command()
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
