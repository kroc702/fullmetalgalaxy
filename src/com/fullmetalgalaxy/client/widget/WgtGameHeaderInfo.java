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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 * display and edit game ConfigGameTime
 */
public class WgtGameHeaderInfo extends Composite implements ModelUpdateEvent.Handler
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();

  private TextBox m_name = new TextBox();
  private TextArea m_description = new TextArea();
  private ListBox m_maxPlayerCount = new ListBox();
  
  /**
   * 
   */
  public WgtGameHeaderInfo()
  {
    super();

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Nom :" ) );
    m_name.addValueChangeHandler( new ValueChangeHandler<String>()
        {
          @Override
          public void onValueChange(ValueChangeEvent<String> p_event)
          {
            GameEngine
            .model()
            .getGame()
            .setName( m_name.getText() );
            AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
          }
      
        });
    hPanel.add( m_name );
    m_panel.add( hPanel );

    m_panel.add( new Label( "Description :" ) );
    m_description.addValueChangeHandler( new ValueChangeHandler<String>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<String> p_event)
      {
        GameEngine.model().getGame().setDescription( m_description.getText() );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
      }

    } );
    m_panel.add( m_description );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Nombre maxi de joueur :" ) );
    m_maxPlayerCount.addItem( "2" );
    m_maxPlayerCount.addItem( "3" );
    m_maxPlayerCount.addItem( "4" );
    m_maxPlayerCount.addItem( "5" );
    m_maxPlayerCount.addItem( "6" );
    m_maxPlayerCount.addItem( "7" );
    m_maxPlayerCount.addItem( "8" );
    m_maxPlayerCount.addItem( "9" );
    m_maxPlayerCount.addItem( "10" );
    m_maxPlayerCount.addItem( "11" );
    m_maxPlayerCount.addItem( "12" );
    m_maxPlayerCount.setVisibleItemCount( 1 );
    m_maxPlayerCount.setItemSelected( 2, true );
    m_maxPlayerCount.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        GameEngine
        .model()
        .getGame()
        .setMaxNumberOfPlayer( m_maxPlayerCount.getSelectedIndex()+2 );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      }
      
    });
    hPanel.add( m_maxPlayerCount );
    m_panel.add( hPanel );

    // fill UI
    onModelUpdate(GameEngine.model());

    initWidget( m_panel );

    // receive all model change
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }

  public void setReadOnly(boolean p_readOnly)
  {
    m_name.setEnabled( !p_readOnly );
    m_description.setEnabled( !p_readOnly );
    m_maxPlayerCount.setEnabled( !p_readOnly );
  }



  
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    m_name.setText( p_modelSender.getGame().getName() );
    m_description.setText( p_modelSender.getGame().getDescription() );
    m_maxPlayerCount.setItemSelected( p_modelSender.getGame().getMaxNumberOfPlayer()-2, true );
  }

}
