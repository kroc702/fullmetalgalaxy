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


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.tabmenu.WgtIntBox;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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
  private WgtIntBox m_intActionPoints = new WgtIntBox();
  private IntegerBox m_intColors = new IntegerBox();
  private Button m_btnBan = new Button( "Bannir ce joueur" );
  private CheckBox m_chkCurrentPlayer = new CheckBox( "Current player" );

  /**
   * 
   */
  public WgtEditOneRegistration()
  {
    VerticalPanel panel = new VerticalPanel();
    panel.add( m_lblAccount );
    panel.add( new Label( "action points:" ) );
    m_intActionPoints.setMinValue( 0 );
    panel.add( m_intActionPoints );
    m_intActionPoints.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
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
    m_chkCurrentPlayer.addClickHandler( this );
    panel.add( m_chkCurrentPlayer );
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
    else if( p_event.getSource() == m_chkCurrentPlayer )
    {
      if( m_chkCurrentPlayer.getValue() )
      {
        GameEngine.model().getGame().getCurrentPlayerIds().add( m_registration.getId() );
      }
      else
      {
        GameEngine.model().getGame().getCurrentPlayerIds().remove( (Long)m_registration.getId() );
      }
    }

  }

  public void loadRegistration(EbRegistration p_reg)
  {
    m_registration = p_reg;
    m_lblAccount.setText( "???" );
    if( p_reg.haveAccount() )
    {
      m_lblAccount.setText( p_reg.getAccount().getPseudo() );
    }
    m_intActionPoints.setValue( p_reg.getPtAction() );
    m_intColors.setValue( p_reg.getColor() );
    m_chkCurrentPlayer.setValue( GameEngine.model().getGame().getCurrentPlayerIds()
        .contains( p_reg.getId() ) );
  }



}
