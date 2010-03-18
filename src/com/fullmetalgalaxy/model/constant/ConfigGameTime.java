/**
 * 
 */
package com.fullmetalgalaxy.model.constant;

import java.util.ArrayList;
import java.util.HashMap;

import com.fullmetalgalaxy.model.EbConfigGameTime;


/**
 * @author Vincent Legendre
 *
 */
public enum ConfigGameTime
{
  Quick, Standard;

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

  public static EbConfigGameTime getEbConfigGameTime(ConfigGameTime p_config, boolean p_isAsynchron)
  {
    if( p_isAsynchron )
    {
      return s_configMapAsynchron.get( p_config );
    }
    else
    {
      return s_configMapTurnByTurn.get( p_config );
    }
  }

  public EbConfigGameTime getEbConfigGameTime(boolean p_isAsynchron)
  {
    return ConfigGameTime.getEbConfigGameTime( this, p_isAsynchron );
  }

  private static HashMap<ConfigGameTime, EbConfigGameTime> s_configMapAsynchron = new HashMap<ConfigGameTime, EbConfigGameTime>();
  private static HashMap<ConfigGameTime, EbConfigGameTime> s_configMapTurnByTurn = new HashMap<ConfigGameTime, EbConfigGameTime>();
  static
  {
    ArrayList<Integer> takeOffTurns = null;
    EbConfigGameTime timeConfig = null;

    // time config : Standard
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 86400 ); // one day
    timeConfig.setTideChangeFrequency( 1 ); // tide change every time steps
    timeConfig.setTotalTimeStep( 25 ); // 25 time step (ie turn)
    timeConfig.setActionPtPerTimeStep( 15 ); // 15 pt per time step
    timeConfig.setActionPtPerExtraShip( 5 ); // 5 more pt per extra ship
    timeConfig.setBulletCountIncrement( 2 ); // 2 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 21 );
    takeOffTurns.add( 25 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setDescription( "25 tours (1 par jour)" );
    s_configMapTurnByTurn.put( Standard, timeConfig );
    s_configMapAsynchron.put( Standard, timeConfig );

    // time config : Quick turn by turn
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 180 ); // 3 min
    timeConfig.setTideChangeFrequency( 1 ); // tide change every time steps
    timeConfig.setTotalTimeStep( 25 ); // 25 time step (ie turn)
    timeConfig.setActionPtPerTimeStep( 15 ); // 15 pt per time step
    timeConfig.setActionPtPerExtraShip( 5 ); // 5 more pt per extra ship
    timeConfig.setBulletCountIncrement( 2 ); // 2 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 21 );
    takeOffTurns.add( 25 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setDescription( "25 tours (1 tout les 3 min)" );
    s_configMapTurnByTurn.put( Quick, timeConfig );

    // time config : Quick asynchron
    timeConfig = new EbConfigGameTime();
    timeConfig.setTimeStepDurationInSec( 100 ); // 1:40 min
    timeConfig.setTideChangeFrequency( 2 ); // tide change every 2 time steps
    timeConfig.setTotalTimeStep( 50 ); // 25 time step (ie turn)
    timeConfig.setActionPtPerTimeStep( 8 ); // 8 pt per time step
    timeConfig.setActionPtPerExtraShip( 3 ); // 3 more pt per extra ship
    timeConfig.setBulletCountIncrement( 1 ); // 1 more bullet every time step
    takeOffTurns = new ArrayList<Integer>();
    takeOffTurns.add( 41 );
    takeOffTurns.add( 42 );
    takeOffTurns.add( 50 );
    timeConfig.setTakeOffTurns( takeOffTurns );
    timeConfig.setDescription( "partie d'1h20" );
    s_configMapAsynchron.put( Quick, timeConfig );

  }

}
