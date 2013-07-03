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
import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */

public class WgtEditOneRegistration extends Composite
{
  // model
  private EbRegistration m_registration = null;

  // UI
  private Label m_lblAccount = new Label( "" );
  private WgtIntBox m_intActionPoints = new WgtIntBox();
  private Label m_lblColors = new Label("colors:");
  private IntegerBox m_intColors = new IntegerBox();
  // remove ban button from here as there is one on game page that is better
  // private Button m_btnBan = new Button( "Bannir ce joueur" );
  private CheckBox m_chkCurrentPlayer = new CheckBox( "Current player" );
  private Button m_btnCancelMyEvents = new Button( "Cancel his actions" );
  private ListBox m_lstTeam = new ListBox(false);
  
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
        if( m_registration == null )
          return;
        m_registration.setPtAction( m_intActionPoints.getValue() );
      }
    } );
    panel.add( m_lblColors );
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
        m_lblColors.setText( "colors: "+m_registration.getEnuColor() );
      }
    } );
    m_chkCurrentPlayer.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        if( m_registration == null )
          return;
        if( m_chkCurrentPlayer.getValue() )
        {
          GameEngine.model().getGame().getCurrentPlayerIds().add( m_registration.getId() );
        }
        else
        {
          GameEngine.model().getGame().getCurrentPlayerIds().remove( (Long)m_registration.getId() );
        }
      }
    } );
    panel.add( m_chkCurrentPlayer );
    // m_btnBan.addClickHandler( this );
    // panel.add( m_btnBan );
    m_btnCancelMyEvents.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        if( m_registration == null )
          return;
        m_registration.getTeam(GameEngine.model().getGame()).clearMyEvents();
        m_btnCancelMyEvents.setEnabled( false );
      }
    } );
    panel.add( m_btnCancelMyEvents );
    
    panel.add( new Label( "Team:" ) );
    m_lstTeam.setVisibleItemCount( 1 );
    for( Company company : Company.values() )
    {
      m_lstTeam.addItem( company.toString() );
    }
    m_lstTeam.setSelectedIndex( m_registration.getTeam( GameEngine.model().getGame() ).getCompany().ordinal() );
    
    m_lstTeam.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        EbTeam oldTeam = m_registration.getTeam( GameEngine.model().getGame() );
        // find corresponding team
        Company company = Company.values()[m_lstTeam.getSelectedIndex()];
        EbTeam newTeam = null;
        if( company != Company.Freelancer )
        {
          newTeam = GameEngine.model().getGame().getTeam( company );
        }
        if( oldTeam == newTeam ) return;
        if( newTeam == null )
        {
          // the chosen company has no team in this game: create a new one
          newTeam = new EbTeam();
          newTeam.setFireColor( m_registration.getEnuColor().getSingleColor().getValue() );
          GameEngine.model().getGame().addTeam( newTeam );
          if( oldTeam.getPlayerIds().size() <= 1 )
          {
            newTeam.setOrderIndex( oldTeam.getOrderIndex() );
          }
          else
          {
            newTeam.setOrderIndex( GameEngine.model().getGame().getTeams().size() );
          }
        }
        newTeam.clearColorsCache();
        newTeam.getPlayerIds().add( m_registration.getId() );
        m_registration.setTeamId( newTeam.getId() );
        if( oldTeam.getPlayerIds().size() <= 1 )
        {
          // delete old team
          GameEngine.model().getGame().getTeams().remove( oldTeam );
        } else
        {
          oldTeam.getPlayerIds().remove( m_registration.getId() );
          oldTeam.clearColorsCache();
          EnuColor teamColors = new EnuColor( oldTeam.getColors( GameEngine.model().getGame().getPreview() ) );
          oldTeam.setFireColor( teamColors.getSingleColor().getValue() );
        }
        
      }
    } );
    panel.add( m_lstTeam );
    
    initWidget( panel );
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
    m_lblColors.setText( "colors: "+p_reg.getEnuColor() );
    m_chkCurrentPlayer.setValue( GameEngine.model().getGame().getCurrentPlayerIds()
        .contains( p_reg.getId() ) );
    m_btnCancelMyEvents.setEnabled( !p_reg.getTeam(GameEngine.model().getGame()).getMyEvents().isEmpty() );
  }



}
