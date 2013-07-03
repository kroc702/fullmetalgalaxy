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

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.GameType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 * display and edit game ConfigGameTime
 */
public class WgtGameHeaderAdvanced extends Composite implements ModelUpdateEvent.Handler
{
  // UI
  // TODO i18n
  private DisclosurePanel m_dpanel = new DisclosurePanel( "Options avancées" );

  private TextBox m_password = new TextBox();
  private CheckBox m_training = new CheckBox();
  private ListBox m_maxTeamAllowed = new ListBox();
  
  /**
   * 
   */
  public WgtGameHeaderAdvanced()
  {
    super();

    VerticalPanel vpanel = new VerticalPanel();

    vpanel.add( new Label( "Mot de passe si partie privé :" ) );
    m_password.addValueChangeHandler( new ValueChangeHandler<String>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<String> p_event)
      {
        GameEngine.model().getGame().setPassword( m_password.getText() );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
      }

    } );
    vpanel.add( m_password );
    
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Partie d'entrainement (ne compte pas au classement) :" ) );
    m_training.addValueChangeHandler( new ValueChangeHandler<Boolean>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> p_event)
      {
        GameEngine.model().getGame()
            .setGameType( m_training.getValue() ? GameType.Initiation : GameType.Initiation );
      }

    } );
    hPanel.add( m_training );
    vpanel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "équipes :" ) );
    m_maxTeamAllowed.addItem( "Pas d'équipe", "0" );
    for(int i=2; i<Company.values().length; i++ )
    {
      m_maxTeamAllowed.addItem( ""+i );
    }
    m_maxTeamAllowed.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        int i=0;
        try
        {
          i=Integer.parseInt( m_maxTeamAllowed.getValue( m_maxTeamAllowed.getSelectedIndex() ) );
        } catch( NumberFormatException e )
        {
        }
        GameEngine.model().getGame().setMaxTeamAllowed( i );
      }

    } );
    // init team count selection
    if( GameEngine.model().getGame().getMaxTeamAllowed() == 0 )
    {
      m_maxTeamAllowed.setItemSelected( 0, true );
    } else {
      m_maxTeamAllowed.setItemSelected( GameEngine.model().getGame().getMaxTeamAllowed()-1, true );
    }
    hPanel.add( m_maxTeamAllowed );
    vpanel.add( hPanel );

    // fill UI
    onModelUpdate(GameEngine.model());

    m_dpanel.add( vpanel );

    initWidget( m_dpanel );

    // receive all model change
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }

  public void setReadOnly(boolean p_readOnly)
  {
    m_password.setEnabled( !p_readOnly );
    m_training.setEnabled( !p_readOnly );
  }



  
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    m_training.setValue( GameEngine.model().getGame().getGameType() == GameType.Initiation );
  }

}
