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
package com.fullmetalgalaxy.model.constant;

import java.util.ArrayList;
import java.util.HashMap;

import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 *
 */
public enum ConfigGameTime implements IsSerializable
{
  Standard, QuickAsynch, QuickTurnBased, StandardAsynch, Custom;

  /**
   * construct an html fragment to display information about game as little icons
   * @return
   */
  public String getIconsAsHtml()
  {
    StringBuffer strBuf = new StringBuffer( " " );
    if( !isParallele() ) {
      strBuf.append( "<img src='/images/icons/turnbyturn16.png' title='"+getEbConfigGameTime().getDescription()+"' /> " );
    } else {
      strBuf.append( "<img src='/images/icons/parallele16.png' title='"+getEbConfigGameTime().getDescription()+"' /> " );
    }
    if( isQuick() ) {
      strBuf.append( "<img src='/images/icons/fast16.png' title='"+getEbConfigGameTime().getDescription()+"' /> " );
    } else {
      strBuf.append( "<img src='/images/icons/slow16.png' title='"+getEbConfigGameTime().getDescription()+"' /> " );
    }
    
    return strBuf.toString();
  }



  public static ConfigGameTime getFromProperties(boolean p_isQuick, boolean p_isParallel)
  {
    if( p_isQuick )
    {
      if( p_isParallel )
      {
        return QuickAsynch;
      }
      else
      {
        return QuickTurnBased;
      }
    }
    else
    {
      if( p_isParallel )
      {
        return StandardAsynch;
      }
      else
      {
        return Standard;
      }
    }
  }

  public static EbConfigGameTime getEbConfigGameTime(ConfigGameTime p_config)
  {
    return s_configMap.get( p_config );
  }

  public boolean isParallele()
  {
    return this == QuickAsynch || this == StandardAsynch;
  }

  public boolean isQuick()
  {
    return this == QuickAsynch || this == QuickTurnBased;
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
    timeConfig.setDeploymentTimeStep( 2 );
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
    timeConfig.setDeploymentTimeStep( 2 );
    s_configMap.put( QuickTurnBased, timeConfig );

    // Parallel
    // ========
    // time config : StandardAsynch
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 21000 ); 
    // (5h50) so 49h to get 25pts (DLA time is changing every day and user can skip one day)
    timeConfig.setTideChangeFrequency( 5 ); // tide change every days
    timeConfig.setTotalTimeStep( 125 ); // 125 time step (ie turn)
    timeConfig.setActionPtPerTimeStep( 3 ); // 3 pt per time step
    timeConfig.setActionPtPerExtraShip( 1 ); // 1 more pt per extra ship
    timeConfig.setBulletCountIncrement( 0.5f );
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
    timeConfig.setDescription( "30 jours en parallèle" );
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
    timeConfig.setDescription( "1h20 en parallèle" );
    timeConfig.setDeploymentTimeStep( 4 );
    s_configMap.put( QuickAsynch, timeConfig );

  }

}
