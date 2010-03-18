/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class DlgMessageEvent extends DialogBox implements ClickListener
{
  private VerticalPanel m_panel = new VerticalPanel();
  private Button m_btnOk = new Button( "OK" );

  /**
   * 
   */
  public DlgMessageEvent(EbEvtMessage m_message)
  {
    // auto hide / modal
    super( true, true );
    if( m_message.getMessage() != null )
    {
      if( m_message.getMessage().startsWith( "./" ) || m_message.getMessage().startsWith( "/" )
          || m_message.getMessage().startsWith( "http://" ) )
      {
        Frame frame = new Frame( m_message.getMessage() );
        frame.setPixelSize( 700, 400 );
        m_panel.add( frame );
      }
      else
      {
        m_panel.add( new Label( m_message.getMessage() ) );
      }
      setText( m_message.getTitle() );
    }
    m_btnOk.addClickListener( this );
    m_panel.add( m_btnOk );
    setWidget( m_panel );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnOk )
    {
      hide();
    }
  }

}
