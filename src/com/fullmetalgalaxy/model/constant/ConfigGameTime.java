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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model.constant;

import java.util.ArrayList;
import java.util.HashMap;

import com.fullmetalgalaxy.model.persist.EbConfigGameTime;


/**
 * @author Vincent Legendre
 *
 */
public enum ConfigGameTime
{
  Standard, QuickAsynch, QuickTurnBased, StandardAsynch;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static ConfigGameTime getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }

  public static EbConfigGameTime getEbConfigGameTime(ConfigGameTime p_config)
  {
    return s_configMap.get( p_config );
  }

  public EbConfigGameTime getEbConfigGameTime()
  {
    return ConfigGameTime.getEbConfigGameTime( this );
  }

  private static HashMap<ConfigGameTime, EbConfigGameTime> s_configMap = new HashMap<ConfigGameTime, EbConfigGameTime>();
  static
  {
    ArrayList<Integer> takeOffTurns = null;
    EbConfigGameTime timeConfig = null;

    // Turn Based
    // ==========
    // time config : Standard
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 0 ); // no limit
    timeConfig.setTideChangeFrequency( 1 ); // tide change every time steps
    timeConfig.setTotalTimeStep( 25 ); // 25 turns
    timeConfig.setActionPtPerTimeStep( 15 ); // 15 pt per time step
    timeConfig.setActionPtPerExtraShip( 5 ); // 5 more pt per extra ship
    timeConfig.setBulletCountIncrement( 2 ); // 2 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 21 );
    takeOffTurns.add( 25 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setAsynchron( false );
    timeConfig.setRoundActionPt( 5 );
    timeConfig.setDescription( "25 tours (pas de temps limite)" );
    timeConfig.setDeploymentTimeStep( 1 );
    s_configMap.put( Standard, timeConfig );

    // time config : QuickTurnBased
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 180 ); // 3 min
    timeConfig.setTideChangeFrequency( 1 ); // tide change every time steps
    timeConfig.setTotalTimeStep( 25 ); // 25 turns
    timeConfig.setActionPtPerTimeStep( 15 ); // 15 pt per time step
    timeConfig.setActionPtPerExtraShip( 5 ); // 5 more pt per extra ship
    timeConfig.setBulletCountIncrement( 2 ); // 2 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 21 );
    takeOffTurns.add( 25 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setAsynchron( false );
    timeConfig.setRoundActionPt( 1 );
    timeConfig.setDescription( "25 tours (3 min pour jouer)" );
    timeConfig.setDeploymentTimeStep( 1 );
    s_configMap.put( QuickTurnBased, timeConfig );

    // Asynchron
    // =========
    // time config : StandardAsynch
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 17280 ); // 1/5 day (4h50)
    timeConfig.setTideChangeFrequency( 5 ); // tide change every days
    timeConfig.setTotalTimeStep( 125 ); // 125 time step (ie turn)
    timeConfig.setActionPtPerTimeStep( 3 ); // 3 pt per time step
    timeConfig.setActionPtPerExtraShip( 1 ); // 1 more pt per extra ship
    // 1 more bullet every time step.. this is a problem as it can fire 5 times
    // every days !
    timeConfig.setBulletCountIncrement( 1 );
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 103 );
    takeOffTurns.add( 104 );
    takeOffTurns.add( 105 );
    takeOffTurns.add( 106 );
    takeOffTurns.add( 107 );
    takeOffTurns.add( 125 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setAsynchron( true );
    timeConfig.setRoundActionPt( 1 );
    timeConfig.setDescription( "25 jours en asynchrone" );
    timeConfig.setDeploymentTimeStep( 8 );
    s_configMap.put( StandardAsynch, timeConfig );

    // time config : QuickAsynch
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 100 ); // 1:40 min
    timeConfig.setTideChangeFrequency( 2 ); // tide change every 3:20 min
    timeConfig.setTotalTimeStep( 50 ); // 50 time step
    timeConfig.setActionPtPerTimeStep( 8 ); // 8 pt per time step
    timeConfig.setActionPtPerExtraShip( 3 ); // 3 more pt per extra ship
    timeConfig.setBulletCountIncrement( 1 ); // 1 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 41 );
    takeOffTurns.add( 42 );
    takeOffTurns.add( 50 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setAsynchron( true );
    timeConfig.setRoundActionPt( 1 );
    timeConfig.setDescription( "1h20 en asynchrone" );
    timeConfig.setDeploymentTimeStep( 3 );
    s_configMap.put( QuickAsynch, timeConfig );

  }

}
