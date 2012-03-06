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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.creation.GameGenerator;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.MapSize;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 * display and edit game map size
 * TODO add custom size
 */
public class WgtMapSize extends Composite implements ValueChangeHandler<Boolean>, ModelUpdateEvent.Handler
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();

  private RadioButton m_sizeSmallButton = new RadioButton( "size",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/small16.png'/> : Petite" ) );
  private RadioButton m_sizeMediumButton = new RadioButton( "size",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/normal16.png'/> : Moyenne" ) );
  private RadioButton m_sizeLargeButton = new RadioButton( "size",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/big16.png'/> : Grande" ) );
  private Label m_lblSize = new Label("");
  
  // for logic
  private int m_maxPlayerCount = 4;
  
  
  /**
   * 
   */
  public WgtMapSize()
  {
    super();

    m_panel.add( new Label( "Taille de carte" ) );
    m_sizeSmallButton.addValueChangeHandler( this );
    m_panel.add( m_sizeSmallButton );
    m_sizeMediumButton.addValueChangeHandler( this );
    m_panel.add( m_sizeMediumButton );
    m_sizeLargeButton.addValueChangeHandler( this );
    m_panel.add( m_sizeLargeButton );
    
    m_panel.add( m_lblSize );

    m_panel.add( new HTML( "<br/>plus elle sera petite plus il y aura de combats" ) );
    // fill UI
    m_maxPlayerCount = GameEngine.model().getGame().getMaxNumberOfPlayer();
    onModelUpdate(GameEngine.model());

    initWidget( m_panel );

    // receive all model change
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }

  public void setReadOnly(boolean p_readOnly)
  {
    m_sizeSmallButton.setEnabled( !p_readOnly );
    m_sizeMediumButton.setEnabled( !p_readOnly );
    m_sizeLargeButton.setEnabled( !p_readOnly );
  }


  private MapSize getSelectedMapSize()
  {
    MapSize mapSize = MapSize.Medium;
    if( m_sizeSmallButton.getValue() ) mapSize = MapSize.Small;
    if( m_sizeMediumButton.getValue() ) mapSize = MapSize.Medium;
    if( m_sizeLargeButton.getValue() ) mapSize = MapSize.Large;
    return mapSize;
  }
  
  @Override
  public void onValueChange(ValueChangeEvent<Boolean> p_event)
  {
    GameGenerator.setSize( getSelectedMapSize() );
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }


  
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    if( m_maxPlayerCount != p_modelSender.getGame().getMaxNumberOfPlayer() )
    {
      m_maxPlayerCount = p_modelSender.getGame().getMaxNumberOfPlayer();
      if( p_modelSender.getGame().isTrancient() )
      {
        // if game is transient, this mean that we are building map and we
        // change player number:
        // so we need to regenerate map according to new size !
        GameGenerator.setSize( getSelectedMapSize() );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
        return;
      }
    }
    MapSize mapSize = MapSize.getFromGame( p_modelSender.getGame() );
    m_sizeMediumButton.setValue( mapSize == MapSize.Medium, false );
    m_sizeSmallButton.setValue( mapSize == MapSize.Small, false );
    m_sizeLargeButton.setValue( mapSize == MapSize.Large, false );
    
    m_lblSize.setText( ""+p_modelSender.getGame().getLandWidth()+" x "+p_modelSender.getGame().getLandHeight() );
  }

}
