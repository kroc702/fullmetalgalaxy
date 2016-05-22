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
package com.fullmetalgalaxy.server.serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.HexCoordinateSystem;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTransfer;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtUnLoad;
import com.fullmetalgalaxy.server.FmpLogger;

/**
 * @author vlegendr
 *
 *
 * format for AI made by bob arning.
 * 
 * --fmp.start---
HEX. x. y. terrain
ORE. x. y.
for terrain: FMPWater=1 FMPReef=2 FMPSwamp=3 FMPLand=4 FMPMountain=5

---fmp.moves---
'create'. newUnit idNumber. newUnit className
'liftoff'. freighter idNumber
'turn'. currentTurn. application tideAtTurn: currentTurn
'player'. currentPlayer playerNumber
'repair'. turret idNumber
'land'. currentFreighter idNumber. aPoint x. aPoint y. orientation
'captureTurret'. turret idNumber. unit1 idNumber    
'capture'. enemy idNumber. unit1 idNumber. unit2 idNumber
'shoot'. eachEnemy idNumber. unit1 idNumber. unit2 idNumber
'move'. unit idNumber. x1. y1. x2. y2
'build'. newUnit idNumber. x1. y1. weatherHen idNumber.
'load'. unit idNumber. receiver idNumber
'loadOre'. ore idNumber. freighter idNumber
'repair'. turret idNumber
'unload'. unit idNumber. x1. y1. fromWhom idNumber
'transfer'. anItem idNumber. fromUnit idNumber. toUnit idNumber


player,1
create,1,FMPFreighter
create,2,FMPTurret
create,3,FMPTurret
create,4,FMPTurret
create,5,FMPTank
create,6,FMPTank
create,7,FMPTank
create,8,FMPTank
create,9,FMPAttackBoat
create,10,FMPAttackBoat
create,11,FMPHeap
create,12,FMPBarge
create,13,FMPCrab
create,14,FMPWeatherHen
create,15,FMPPontoon
player,2
create,16,FMPFreighter
create,17,FMPTurret
create,18,FMPTurret
create,19,FMPTurret
create,20,FMPTank
create,21,FMPTank
create,22,FMPTank
...
turn,5,high
player,2
move,26,21,24,20,24
move,24,19,19,20,20
move,20,19,24,19,23
move,25,20,19,21,20
move,28,22,23,22,22
load,76,28
load,77,29
build,78,17,23,29
move,28,22,22,22,23
move,28,22,23,23,24
load,79,28
move,28,23,24,22,24
move,28,22,24,21,23
load,28,16
move,27,17,20,17,19
player,4
move,57,34,1,34,2
load,80,57
move,57,34,2,33,0
move,57,33,0,32,0
move,57,32,0,31,0
move,57,31,0,30,0
move,57,30,0,29,0
move,57,29,0,28,0
load,57,46
...
etc.
 */
public class DriverSTAI extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverSTAI.class.getName() );
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#loadGame(java.io.InputStream)
   */
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    LOG.error( "unimplemented" );
    return null;
  }
  
  @Override
  public ModelFmpUpdate loadGameUpdate(InputStream p_input, String gameId)
  {
    BufferedReader reader = new BufferedReader( new InputStreamReader( p_input ) );

    ModelFmpUpdate model = new ModelFmpUpdate();
    model.setGameId( Long.parseLong( gameId ) );
    model.setGameEvents( new ArrayList<AnEvent>() );


    String[] line = readLine( reader );
    long gameVersion = 0;
    long playerId = 0;
    long accountId = 0;
    HexCoordinateSystem hexCoordinateSystem = new HexCoordinateSystem();
    
    while( line != null )
    {
      AnEventPlay evt = null;
      switch( line[0] )
      {
      case "id":
        model.setGameId( Long.parseLong( line[1] ) );
        break;
      case "version":
        model.setFromVersion( Long.parseLong( line[1] ) );
        gameVersion = model.getFromVersion();
        break;
      // 'player'. currentPlayer playerNumber
      case "player":
        playerId = getLong( line[1] );
        accountId = getLong( line[2] );
        break;
      // 'turn'. currentTurn. application tideAtTurn: currentTurn
      case "turn":
        break;
      // 'create'. newUnit idNumber. newUnit className
      case "create":
        break;
      // 'land'. currentFreighter idNumber. aPoint x. aPoint y. orientation
      case "land":
        evt = new EbEvtLand();
        evt.setPosition( new AnBoardPosition( getInt( line[3] ), getInt( line[2] ), getSector( line[4] ) ) );
        evt.setToken( getLong( line[1] ) );
        break;
      // 'move'. unit idNumber. x1. y1. x2. y2
      case "move":
        evt = new EbEvtMove();
        evt.setToken( getLong( line[1] ) );
        evt.setPosition( new AnBoardPosition( getInt( line[3] ), getInt( line[2] ) ) );
        evt.setNewPosition( new AnBoardPosition( getInt( line[5] ), getInt( line[4] ) ) );
        evt.getNewPosition().setSector( hexCoordinateSystem.getSector( evt.getPosition(), evt.getNewPosition() ) );
        break;
      // 'shoot'. eachEnemy idNumber. unit1 idNumber. unit2 idNumber
      case "shoot":
        evt = new EbEvtFire();
        evt.setToken( getLong( line[1] ) );
        evt.setTokenDestroyer1( getLong( line[2] ) );
        evt.setTokenDestroyer2( getLong( line[3] ) );
        break;
      // 'liftoff'. freighter idNumber
      case "liftoff":
        evt = new EbEvtTakeOff();
        evt.setToken( getLong( line[1] ) );
        break;
      // 'repair'. turret idNumber
      case "repair":
        // evt = new EbEvtRepair();

        break;
      // 'captureTurret'. turret idNumber. unit1 idNumber
      case "captureTurret":
        break;
      // 'capture'. enemy idNumber. unit1 idNumber. unit2 idNumber
      case "capture":
        evt = new EbEvtControl();
        evt.setToken( getLong( line[1] ) );
        evt.setTokenDestroyer1( getLong( line[2] ) );
        evt.setTokenDestroyer2( getLong( line[3] ) );
        break;
      // 'build'. newUnit idNumber. x1. y1. weatherHen idNumber.
      case "build":
        // evt = new EbEvtConstruct();

        break;
      // 'load'. unit idNumber. receiver idNumber
      case "load":
        evt = new EbEvtLoad();
        evt.setToken( getLong( line[1] ) );
        evt.setTokenCarrier( getLong( line[2] ) );
        break;
      // 'loadOre'. ore idNumber. freighter idNumber
      case "loadOre":
        break;
      // 'unload'. unit idNumber. x1. y1. fromWhom idNumber
      case "unload":
        evt = new EbEvtUnLoad();
        evt.setToken( getLong( line[1] ) );
        evt.setNewPosition( new AnBoardPosition( getInt( line[3] ), getInt( line[2] ) ) );
        evt.setTokenCarrier( getLong( line[4] ) );
        break;
      // 'transfer'. anItem idNumber. fromUnit idNumber. toUnit idNumber
      case "transfer":
        evt = new EbEvtTransfer();
        evt.setToken( getLong( line[1] ) );
        evt.setTokenCarrier( getLong( line[2] ) );
        evt.setNewTokenCarrier( getLong( line[3] ) );
        break;
      default:
        break;
      }

      if( evt != null )
      {
        evt.setRegistrationId( playerId );
        evt.setAccountId( accountId );
        evt.setGameId( model.getGameId() );
        evt.setGameVersion( gameVersion );
        model.getGameEvents().add( evt );
        // gameVersion++;
      }

      line = readLine( reader );
    }

    return model;
  }

  private String[] readLine(BufferedReader reader)
  {
    String[] result = null;
    String line = null;
    try
    {
      line = reader.readLine();
    } catch( IOException e )
    {
    }
    if( line != null )
    {
      result = line.split( "[, ]+" );
    }
    return result;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#saveGame(com.fullmetalgalaxy.model.ModelFmpInit, java.io.OutputStream)
   */
  @Override
  public void saveGame(ModelFmpInit p_game, OutputStream p_output)
  {
    PrintStream printStream = new PrintStream( p_output );

    // print general information
    printStream.println( "id," + p_game.getGame().getId() );
    printStream.println( "version," + p_game.getGame().getVersion() );
    printStream.println( "size," + p_game.getGame().getLandHeight() + "," + p_game.getGame().getLandWidth() );
    printStream.println( "turn," + p_game.getGame().getCurrentTimeStep() );
    printStream.println( "tides," + p_game.getGame().getCurrentTide() + "," + p_game.getGame().getNextTide() + ","
        + p_game.getGame().getNextTide2() );
    printStream.println( "" );

    // print terrain
    for( int y = 0; y < p_game.getGame().getLandHeight(); y++ )
    {
      for( int x = 0; x < p_game.getGame().getLandWidth(); x++ )
      {
        printStream.println( "HEX," + y + "," + x + "," + getConstant( p_game.getGame().getLand( x, y ) ) );
      }
    }

    // print ore and other colorless units
    for( EbToken token : p_game.getGame().getSetToken() )
    {
      if( token.getLocation() == Location.Board && token.getColor() == EnuColor.None )
      {
        printStream.println( getConstant( token.getType() ) + "," + token.getPosition().getY() + ","
            + token.getPosition().getX() + "," + token.getId() );
      }
    }

    printStream.println( "" );

    // FIXME what about, multiple freither for a single player ?
    // does not comply with given file format
    // print players units
    for( EbTeam team : p_game.getGame().getTeamByPlayOrder() )
    {
      for( EbRegistration player : team.getPlayers( p_game.getGame().getPreview() ) )
      {
        printStream.print( "player," + player.getId() );
        if( player.getAccount() != null )
        {
          printStream.print( "," + player.getAccount().getId() );
        }
        printStream.println( "" );
        for( EbToken token : p_game.getGame().getSetToken() )
        {
          if( token.getColor() != EnuColor.None && player.getEnuColor().contain( token.getColor() ) )
          {
            if( token.getLocation() == Location.Board )
            {
              printStream.println( "create," + token.getId() + "," + getConstant( token.getType() ) + ","
                  + token.getPosition().getY() + "," + token.getPosition().getX() + ","
                  + getConstant( token.getPosition().getSector() ) );
            }
          }
        }
        for( EbToken token : p_game.getGame().getSetToken() )
        {
          if( token.getLocation() == Location.Token )
          {
            if( player.getEnuColor().contain( token.getCarrierToken().getColor() ) )
            {
              printStream.println( "create," + token.getId() + "," + getConstant( token.getType() ) + ","
                  + token.getCarrierToken().getId() );
            }
          }
        }
      }
    }

    printStream.close();

  }

  private int getInt(String str)
  {
    return Integer.parseInt( str );
  }

  private long getLong(String str)
  {
    return Long.parseLong( str );
  }

  private int getConstant(Sector sector)
  {
    return sector.ordinal();
  }

  private Sector getSector(String str)
  {
    return Sector.getFromOrdinal( Integer.parseInt( str ) );
  }

  private int getConstant(LandType land){
    switch(land){
    default:
    case None:
      return 0;
    case Sea:
      return 1;
    case Reef:
      return 2;
    case Marsh:
      return 3;
    case Plain:
      return 4;
    case Montain:
      return 5;
    }
  }

  /**
   * TODO other unit type ?
   * @param type
   * @return
   * 
   * create,1,FMPFreighter
  create,2,FMPTurret
  create,3,FMPTurret
  create,4,FMPTurret
  create,5,FMPTank
  create,6,FMPTank
  create,7,FMPTank
  create,8,FMPTank
  create,9,FMPAttackBoat
  create,10,FMPAttackBoat
  create,11,FMPHeap
  create,12,FMPBarge
  create,13,FMPCrab
  create,14,FMPWeatherHen
  create,15,FMPPontoon
   */
  private String getConstant(TokenType type)
  {
    switch( type )
    {
    case Barge:
    case Crab:
    case Crayfish:
    case Tank:
    case Tarask:
    case Teleporter:
    case Turret:
    case Warp:
    case WeatherHen:
    case Destroyer:
    case Freighter:
    case Heap:
    case Hovertank:
    case Ore2Generator:
    case Ore3Generator:
    case Pontoon:
    case Sluice:
      return "FMP" + type;
    case Speedboat:
      return "FMPAttackBoat";
    case Ore:
    case Ore0:
    case Ore3:
    case Ore5:
      return "ORE";
    case None:
    default:
      return "None";
    }
  }
}
