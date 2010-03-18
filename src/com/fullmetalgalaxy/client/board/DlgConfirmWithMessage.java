/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class DlgConfirmWithMessage extends DialogBox implements ClickListener
{
  // UI
  private Button m_btnOk = new Button( "OK" );
  private Button m_btnCancel = new Button( "Cancel" );
  private VerticalPanel m_panel = new VerticalPanel();
  private TextArea m_textArea = new TextArea();

  public DlgConfirmWithMessage(String p_question)
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "You sure ?" );

    m_btnOk.addClickListener( this );
    m_btnCancel.addClickListener( this );

    m_panel.add( new Label( p_question ) );
    m_panel.add( m_textArea );

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( m_btnOk );
    hPanel.add( m_btnCancel );
    m_panel.add( hPanel );

    setWidget( m_panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnOk )
    {

    }
    else if( p_sender == m_btnCancel )
    {
      this.hide();
      this.removeFromParent();
    }
  }

}
