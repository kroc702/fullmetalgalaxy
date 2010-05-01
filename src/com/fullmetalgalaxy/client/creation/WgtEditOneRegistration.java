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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */

public class WgtEditOneRegistration extends Composite implements ClickHandler
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
    m_btnBan.addClickHandler( this );
    panel.add( m_btnBan );
    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( m_registration == null )
    {
      return;
    }

    if( p_event.getSource() == m_btnBan )
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
