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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.tabmenu.WgtIntBox;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author kroc
 *
 * this widget will be used by admin only
 */
public class WgtConfigGameTime extends Composite
{
  private EbConfigGameTime m_config = new EbConfigGameTime();
  private ConfigGameTime m_configEnum = ConfigGameTime.Custom;
  
  private VerticalPanel m_panel = new VerticalPanel();
  
  private Label     m_lblConfigGameTime = new Label();
  private WgtIntBox m_intTimeStepDurationInSec = new WgtIntBox();
  private WgtIntBox m_intTideChangeFrequency = new WgtIntBox();
  private WgtIntBox m_intTotalTimeStep = new WgtIntBox();
  private WgtIntBox m_intActionPtPerTimeStep = new WgtIntBox();
  private WgtIntBox m_intActionPtPerExtraShip = new WgtIntBox();
  private WgtIntBox m_intActionPtMaxReserve = new WgtIntBox();
  private WgtIntBox m_intActionPtMaxPerExtraShip = new WgtIntBox();
  private WgtIntBox m_intBulletCountIncrement = new WgtIntBox();
  private TextBox   m_txtTakeOffTurns = new TextBox();
  private CheckBox  m_chkIsParallel = new CheckBox();
  private WgtIntBox m_intLockGameInMillis = new WgtIntBox();
  private WgtIntBox m_intRoundActionPt = new WgtIntBox();
  private WgtIntBox m_intDeploymentTimeStep = new WgtIntBox();
  
  

  
  /**
   * 
   */
  public WgtConfigGameTime()
  {
    super();
    
    m_panel.add( m_lblConfigGameTime );
    
    m_panel.add( new Label("m_chkIsParallel") );
    m_panel.add( m_chkIsParallel );
    m_chkIsParallel.addValueChangeHandler( new ValueChangeHandler<Boolean>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> p_event)
      {
        m_config.setParallel( m_chkIsParallel.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_txtTakeOffTurns") );
    m_panel.add( m_txtTakeOffTurns );
    m_txtTakeOffTurns.addValueChangeHandler( new ValueChangeHandler<String>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<String> p_event)
      {
        String[] txtValues = m_txtTakeOffTurns.getValue().split( "[ ,;./\\-]" );
        m_config.getTakeOffTurns().clear();
        for(String txtValue : txtValues )
        {
          try
          {
            m_config.getTakeOffTurns().add( Integer.valueOf( txtValue ) );
          } catch( NumberFormatException e )
          {
          }
        }
        String newTxtValue = "";
        for(int i : m_config.getTakeOffTurns())
        {
          newTxtValue += i + " ";
        }
        m_txtTakeOffTurns.setText( newTxtValue );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intTimeStepDurationInSec") );
    m_panel.add( m_intTimeStepDurationInSec );
    m_intTimeStepDurationInSec.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setTimeStepDurationInSec( m_intTimeStepDurationInSec.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intTideChangeFrequency") );
    m_panel.add( m_intTideChangeFrequency );
    m_intTideChangeFrequency.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setTideChangeFrequency( m_intTideChangeFrequency.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intTotalTimeStep") );
    m_panel.add( m_intTotalTimeStep );
    m_intTotalTimeStep.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setTotalTimeStep( m_intTotalTimeStep.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intActionPtPerTimeStep") );
    m_panel.add( m_intActionPtPerTimeStep );
    m_intActionPtPerTimeStep.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setActionPtPerTimeStep( m_intActionPtPerTimeStep.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intActionPtPerExtraShip") );
    m_panel.add( m_intActionPtPerExtraShip );
    m_intActionPtPerExtraShip.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setActionPtPerExtraShip( m_intActionPtPerExtraShip.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intActionPtMaxReserve") );
    m_panel.add( m_intActionPtMaxReserve );
    m_intActionPtMaxReserve.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setActionPtMaxReserve( m_intActionPtMaxReserve.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intActionPtMaxPerExtraShip") );
    m_panel.add( m_intActionPtMaxPerExtraShip );
    m_intActionPtMaxPerExtraShip.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setActionPtMaxPerExtraShip( m_intActionPtMaxPerExtraShip.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intBulletCountIncrement") );
    m_panel.add( m_intBulletCountIncrement );
    m_intBulletCountIncrement.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.m_bulletCountIncrement = ( m_intBulletCountIncrement.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intLockGameInMillis") );
    m_panel.add( m_intLockGameInMillis );
    m_intLockGameInMillis.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setLockGameInMillis( m_intLockGameInMillis.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intRoundActionPt") );
    m_panel.add( m_intRoundActionPt );
    m_intRoundActionPt.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setRoundActionPt( m_intRoundActionPt.getValue() );
        editConfigGameTime();
      }
    } );
    
    m_panel.add( new Label("m_intDeploymentTimeStep") );
    m_panel.add( m_intDeploymentTimeStep );
    m_intDeploymentTimeStep.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        m_config.setDeploymentTimeStep( m_intDeploymentTimeStep.getValue() );
        editConfigGameTime();
      }
    } );
    
    
    
    initWidget( m_panel );
  }

  
  public void loadConfigGameTime(EbConfigGameTime p_config, ConfigGameTime p_configEnum )
  {
    if( p_config == null || p_configEnum == null ) return;
    m_config = p_config;
    if( p_configEnum != ConfigGameTime.Custom ) m_config = new EbConfigGameTime(p_config);
    m_configEnum = p_configEnum;
    
    m_lblConfigGameTime.setText( m_configEnum.toString() );
    m_chkIsParallel.setValue( m_config.isParallel() );
    String newTxtValue = "";
    for(int i : m_config.getTakeOffTurns())
    {
      newTxtValue += i + " ";
    }
    m_txtTakeOffTurns.setText( newTxtValue );

    m_intTimeStepDurationInSec.setValue( m_config.getTimeStepDurationInSec() );
    m_intTideChangeFrequency.setValue( m_config.getTideChangeFrequency() );
    m_intTotalTimeStep.setValue( m_config.getTotalTimeStep() );
    m_intActionPtPerTimeStep.setValue( m_config.getActionPtPerTimeStep() );
    m_intActionPtPerExtraShip.setValue( m_config.getActionPtPerExtraShip() );
    m_intActionPtMaxReserve.setValue( m_config.getActionPtMaxReserve() );
    m_intActionPtMaxPerExtraShip.setValue( m_config.getActionPtMaxPerExtraShip() );
    m_intBulletCountIncrement.setValue( m_config.m_bulletCountIncrement );
    m_intLockGameInMillis.setValue( m_config.getLockGameInMillis() );
    m_intRoundActionPt.setValue( m_config.getRoundActionPt() );
    m_intDeploymentTimeStep.setValue( m_config.getDeploymentTimeStep() );
  
  }
  
  public void editConfigGameTime()
  {
    if( m_configEnum == ConfigGameTime.Custom ) return;
    m_configEnum = ConfigGameTime.Custom;
    m_lblConfigGameTime.setText( m_configEnum.toString() );
    GameEngine.model().getGame().setConfigGameTime(ConfigGameTime.Custom);
    GameEngine.model().getGame().setEbConfigGameTime( m_config );
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }
}
