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
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
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
 * display and edit game ConfigGameTime
 */
public class WgtGameTime extends Composite implements ValueChangeHandler<Boolean>, ModelUpdateEvent.Handler
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();

  private RadioButton m_modeTbtButton = new RadioButton( "mode",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/turnbyturn16.png'/> : Partie en mode tour par tour" ) );
  private RadioButton m_modeParallelButton = new RadioButton( "mode",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/parallele16.png'/> : Partie en mode parallèle" ) );
  private RadioButton m_speedSlowButton = new RadioButton( "speed",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/slow16.png'/> : Partie lente (25 jours ou illimité)" ) );
  private RadioButton m_speedQuickButton = new RadioButton( "speed",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/icons/fast16.png'/> : Partie rapide (1h30)" ) );

  private boolean m_readOnly = false;
  /**
   * 
   */
  public WgtGameTime()
  {
    super();

    m_panel.add( new Label( "Mode de jeu" ) );
    m_modeTbtButton.addValueChangeHandler( this );
    m_panel.add( m_modeTbtButton );
    m_modeParallelButton.addValueChangeHandler( this );
    m_panel.add( m_modeParallelButton );

    m_panel.add( new Label( "Vitesse" ) );
    m_speedSlowButton.addValueChangeHandler( this );
    m_panel.add( m_speedSlowButton );
    m_speedQuickButton.addValueChangeHandler( this );
    m_panel.add( m_speedQuickButton );

    m_panel.add( new HTML( "<br/><a href='/help/gamemodes.jsp'>plus de détail ici</a>" ) );
    // fill UI
    onModelUpdate(GameEngine.model());

    initWidget( m_panel );

    // receive all model change
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }

  public void setReadOnly(boolean p_readOnly)
  {
    m_readOnly = p_readOnly;
    m_modeTbtButton.setEnabled( !p_readOnly );
    m_modeParallelButton.setEnabled( !p_readOnly );
    m_speedSlowButton.setEnabled( !p_readOnly );
    m_speedQuickButton.setEnabled( !p_readOnly );
    onModelUpdate(GameEngine.model());
  }


  @Override
  public void onValueChange(ValueChangeEvent<Boolean> p_event)
  {
    GameEngine
        .model()
        .getGame()
        .setConfigGameTime(
            ConfigGameTime.getFromProperties( m_speedQuickButton.getValue(),
                m_modeParallelButton.getValue() ) );
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }


  
  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    ConfigGameTime config = p_modelSender.getGame().getConfigGameTime();
    m_modeParallelButton.setValue( config.isParallele(), false );
    m_modeTbtButton.setValue( !config.isParallele(), false );

    m_speedQuickButton.setValue( config.isQuick(), false );
    m_speedSlowButton.setValue( !config.isQuick(), false );
    
    m_modeParallelButton.setVisible( true );
    m_modeTbtButton.setVisible( true );
    m_speedQuickButton.setVisible( true );
    m_speedSlowButton.setVisible( true );
    if( m_readOnly )
    {
      m_modeParallelButton.setVisible( config.isParallele() );
      m_modeTbtButton.setVisible( !config.isParallele() );

      m_speedQuickButton.setVisible( config.isQuick() );
      m_speedSlowButton.setVisible( !config.isQuick() );
    }
  }

}
