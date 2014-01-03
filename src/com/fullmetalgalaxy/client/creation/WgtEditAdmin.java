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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;


import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.tabmenu.WgtIntBox;
import com.fullmetalgalaxy.client.widget.WgtConfigGameTime;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * other actions
 */

public class WgtEditAdmin extends Composite  
{
  private VerticalPanel m_panel = new VerticalPanel();

  // to change current turn
  private WgtIntBox m_intCurrentTurn = new WgtIntBox();
  private ListBox m_lstStatus = new ListBox(false);
  private TextBox m_txtCreatorPseudo = new TextBox();
  private TextBox m_txtCreatorId = new TextBox();
  
  // to edit registration
  private Map<String, EbRegistration> m_mapReg = new HashMap<String, EbRegistration>();
  private ListBox m_lstReg = new ListBox( false );
  private WgtEditOneRegistration m_wgtOneReg = new WgtEditOneRegistration();

  // to fine tune time config
  private WgtConfigGameTime m_wgtConfigGameTime = new WgtConfigGameTime();

  public WgtEditAdmin()
  {
    m_panel.add( new Label( "current turn:" ) );
    m_panel.add( m_intCurrentTurn );
    m_intCurrentTurn.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        int delta = m_intCurrentTurn.getValue() - GameEngine.model().getGame().getCurrentTimeStep();
        GameEngine.model().getGame()
            .setLastTideChange( GameEngine.model().getGame().getLastTideChange() + delta );
        GameEngine.model().getGame().setCurrentTimeStep( m_intCurrentTurn.getValue() );
      }
    } );
    
    m_panel.add( new Label( "game status:" ) );
    for( GameStatus status : GameStatus.values() )
    {
      m_lstStatus.addItem( status.toString() );
    }
    m_lstStatus.setVisibleItemCount( 1 );
    m_lstStatus.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        GameEngine.model().getGame().setStatus( GameStatus.valueOf( 
        m_lstStatus.getItemText( m_lstStatus.getSelectedIndex() ) ) );
      }
    } );
    m_panel.add( m_lstStatus );
    
    m_panel.add( new Label( "creator pseudo:" ) );
    m_panel.add( m_txtCreatorPseudo );
    m_txtCreatorPseudo.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        GameEngine.model().getGame().getAccountCreator().setPseudo( m_txtCreatorPseudo.getText() );
      }
    } );
    m_panel.add( new Label( "creator ID:" ) );
    m_panel.add( m_txtCreatorId );
    m_txtCreatorId.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        GameEngine.model().getGame().getAccountCreator().setId( Long.parseLong( m_txtCreatorId.getText()) );
      }
    } );

    m_panel.add( new HTML( "<hr>" ) );
    // ===================

    m_lstReg.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        m_wgtOneReg.loadRegistration( m_mapReg.get( m_lstReg.getItemText( m_lstReg
            .getSelectedIndex() ) ) );

      }
    } );
    m_lstReg.setVisibleItemCount( 10 );
    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.add( m_lstReg );
    hpanel.add( m_wgtOneReg );
    m_panel.add( hpanel );

    m_panel.add( new HTML( "<hr>" ) );
    // ===================
    m_panel.add( m_wgtConfigGameTime );
    
    
    initWidget( m_panel );
  }


  public void onTabSelected()
  {
    // load current turn
    m_intCurrentTurn.setValue( GameEngine.model().getGame().getCurrentTimeStep() );
    m_lstStatus.setItemSelected( GameEngine.model().getGame().getStatus().ordinal(), true );
    
    // load account creator 
    if( GameEngine.model().getGame().getAccountCreator() != null )
    {
      m_txtCreatorPseudo.setText( GameEngine.model().getGame().getAccountCreator().getPseudo() );
      m_txtCreatorId.setText( ""+GameEngine.model().getGame().getAccountCreator().getId() );
    }
    
    // load current time config
    m_wgtConfigGameTime.loadConfigGameTime( GameEngine.model().getGame().getEbConfigGameTime(), 
        GameEngine.model().getGame().getConfigGameTime() );
    
    // reload all registration
    m_mapReg = new HashMap<String, EbRegistration>();
    int selectedIndex = m_lstReg.getSelectedIndex();
    m_lstReg.clear();
    for( EbRegistration registration : GameEngine.model().getGame().getSetRegistration() )
    {
      m_lstReg.addItem( Messages.getColorString( 0, registration.getColor() ) );
      m_mapReg.put( Messages.getColorString( 0, registration.getColor() ), registration );
    }
    if( selectedIndex >= 0 ) m_lstReg.setSelectedIndex( selectedIndex );
    
  }

  FmpCallback<Void> m_callback = new FmpCallback<Void>()
  {
    @Override
    public void onSuccess(Void p_result)
    {
      super.onSuccess( p_result );
      // load newly created game to show it
      Window.alert( "Operation reussi" );
    }

    /* (non-Javadoc)
     * @see com.fullmetalgalaxy.client.FmpCallback#onFailure(java.lang.Throwable)
     */
    @Override
    public void onFailure(Throwable p_caught)
    {
      try
      {
        Window.alert( ((RpcFmpException)p_caught).getLocalizedMessage() );
      } catch( Throwable th )
      {
        if( (p_caught.getMessage() == null) || (p_caught.getMessage().length() == 0) )
        {
          Window.alert( "Unknown error or serveur is unreachable\n" );
        }
        else
        {
          Window.alert( p_caught.getMessage() );
        }
      }
    }


  };



}
