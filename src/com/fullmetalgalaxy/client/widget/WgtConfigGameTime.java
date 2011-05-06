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


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * @BeanClass com.fullmetalgalaxy.model.EbConfigGameTime
 */
public class WgtConfigGameTime extends WgtBean implements ValueChangeHandler<Boolean>
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();

  private RadioButton m_modeTbtButton = new RadioButton( "mode",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/css/icon_tbt.gif'/> : Partie en mode tour par tour" ) );
  private RadioButton m_modeParallelButton = new RadioButton( "mode",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/css/icon_parallele.gif'/> : Partie en mode parallèle" ) );
  private RadioButton m_speedSlowButton = new RadioButton( "speed",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/css/icon_slow.cache.png'/> : Partie lente (25 jours ou illimité)" ) );
  private RadioButton m_speedQuickButton = new RadioButton( "speed",
      new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          " <img src='/images/css/icon_fast.cache.png'/> : Partie rapide (1h30)" ) );


  /**
   * 
   */
  public WgtConfigGameTime()
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
    initUI();

    initWidget( m_panel );

    // receive all model change
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
  }

  @Override
  public void setReadOnly(boolean p_readOnly)
  {
    m_modeTbtButton.setEnabled( !p_readOnly );
    m_modeParallelButton.setEnabled( !p_readOnly );
    m_speedSlowButton.setEnabled( !p_readOnly );
    m_speedQuickButton.setEnabled( !p_readOnly );
  }

  /**
   * @return the panel
   */
  protected VerticalPanel getPanel()
  {
    return m_panel;
  }

  protected void initUI()
  {
    ConfigGameTime config = ModelFmpMain.model().getGame().getConfigGameTime();
    m_modeParallelButton.setValue( config.isParallele(), false );
    m_modeTbtButton.setValue( !config.isParallele(), false );

    m_speedQuickButton.setValue( config.isQuick(), false );
    m_speedSlowButton.setValue( !config.isQuick(), false );
  }


  @Override
  public void onValueChange(ValueChangeEvent<Boolean> p_event)
  {
    ModelFmpMain
        .model()
        .getGame()
        .setConfigGameTime(
            ConfigGameTime.getFromProperties( m_speedQuickButton.getValue(),
                m_modeParallelButton.getValue() ) );
    ModelFmpMain.model().fireModelUpdate();
  }



}
