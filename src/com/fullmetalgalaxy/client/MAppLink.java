/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public abstract class MAppLink extends MApp implements ClickListener, MouseListener
{
  private Label m_createAccountLabel = new Label( "link" );

  public MAppLink()
  {
    super();
    m_createAccountLabel.addClickListener( this );
    m_createAccountLabel.addMouseListener( this );
    m_createAccountLabel.setStyleName( "gwt-Hyperlink" );
    initWidget( m_createAccountLabel );
  }



  /**
   * @return
   * @see com.google.gwt.user.client.ui.Label#getText()
   */
  public String getText()
  {
    return m_createAccountLabel.getText();
  }



  /**
   * @param p_text
   * @see com.google.gwt.user.client.ui.Label#setText(java.lang.String)
   */
  public void setText(String p_text)
  {
    m_createAccountLabel.setText( p_text );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_createAccountLabel )
    {
      p_sender.removeStyleName( "underline" );
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseDown(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseEnter(Widget p_sender)
  {
    p_sender.addStyleName( "underline" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseLeave(Widget p_sender)
  {
    p_sender.removeStyleName( "underline" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseMove(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseUp(Widget p_sender, int p_x, int p_y)
  {
  }


}
