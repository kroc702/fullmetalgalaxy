/**
 * 
 */
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtEditOneRegistration extends Composite implements ClickListener
{
  // model
  private EbRegistration m_registration = null;

  // UI
  private Label m_lblAccount = new Label( "" );
  private Button m_btnBan = new Button( "Bannir ce joueur" );


  /**
   * 
   */
  public WgtEditOneRegistration()
  {
    VerticalPanel panel = new VerticalPanel();
    panel.add( m_lblAccount );
    m_btnBan.addClickListener( this );
    panel.add( m_btnBan );
    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( m_registration == null )
    {
      return;
    }

    if( p_sender == m_btnBan )
    {
      // TODO ajouter un log admin
      m_registration.setAccountId( 0 );
      loadRegistration( m_registration );
    }

  }

  public void loadRegistration(EbRegistration p_reg)
  {
    m_registration = p_reg;
    m_lblAccount.setText( "" );
    if( p_reg.haveAccount() )
    {
      m_lblAccount.setText( ModelFmpMain.model().getAccount( p_reg.getAccountId() ).getLogin() );
    }
  }

}
