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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
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
  private IntegerBox m_intActionPoints = new IntegerBox();
  private IntegerBox m_intColors = new IntegerBox();
  private Button m_btnBan = new Button( "Bannir ce joueur" );


  /**
   * 
   */
  public WgtEditOneRegistration()
  {
    VerticalPanel panel = new VerticalPanel();
    panel.add( m_lblAccount );
    panel.add( new Label( "action points:" ) );
    panel.add( m_intActionPoints );
    m_intActionPoints.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        if( m_registration == null )
          return;
        if( m_intActionPoints.getValue() == null )
        {
          m_intActionPoints.setValue( 0 );
        }
        m_registration.setPtAction( m_intActionPoints.getValue() );
      }
    } );
    panel.add( new Label( "colors:" ) );
    panel.add( m_intColors );
    m_intColors.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        if( m_registration == null )
          return;
        if( m_intColors.getValue() == null )
        {
          m_intColors.setValue( 0 );
        }
        m_registration.setColor( m_intColors.getValue() );
      }
    } );
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
      m_registration.setAccount( null );
      loadRegistration( m_registration );
    }

  }

  public void loadRegistration(EbRegistration p_reg)
  {
    m_registration = p_reg;
    m_lblAccount.setText( "" );
    if( p_reg.haveAccount() )
    {
      m_lblAccount.setText( p_reg.getAccount().getPseudo() );
    }
    m_intActionPoints.setValue( p_reg.getPtAction() );
    m_intColors.setValue( p_reg.getColor() );
  }



}
