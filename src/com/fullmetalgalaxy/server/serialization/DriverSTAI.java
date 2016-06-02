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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtDeployment;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtRepair;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTransfer;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtUnLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbRemember;
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
  public static final String VERSION = "2";
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
  public ModelFmpUpdate loadGameUpdate(InputStream p_input, String gameId) throws ParseException
  {
    BufferedReader reader = new BufferedReader( new InputStreamReader( p_input ) );
    currentLineIndex = 0;
    currentLine = null;

    // FmgDataStore dataStore = new FmgDataStore( true );
    // Game game = dataStore.getGame( getLong( gameId ) );
    // if( game == null )
    // {
    // throw new RuntimeException( "run action on unknown game: " + gameId );
    // }

    ModelFmpUpdate model = new ModelFmpUpdate();
    model.setGameId( Long.parseLong( gameId ) );
    model.setGameEvents( new ArrayList<AnEvent>() );

    try
    {
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
        case "stai":
          // nothing to do yet
          break;
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
        case "password":
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
          evt = new EbEvtRepair();
          evt.setToken( getLong( line[1] ) );
          break;
        // 'captureTurret'. turret idNumber. unit1 idNumber
        case "captureTurret":
          evt = new EbEvtControlFreighter();
          // evt.setCost(-1 * p_game.getEbConfigGameTime().getActionPtMaxPerExtraShip() );
          evt.setCost( -5 );
          evt.setToken( getLong( line[2] ) );
          evt.setTokenCarrier( getLong( line[1] ) );
          evt.setRegistrationId( playerId );
          evt.setAccountId( accountId );
          evt.setGameId( model.getGameId() );
          evt.setGameVersion( gameVersion );
          evt.setTransientComment( "line " + currentLineIndex + ": " + currentLine );
          model.getGameEvents().add( evt );

          evt = new EbEvtLoad();
          evt.setToken( getLong( line[2] ) );
          evt.setTokenCarrier( getLong( line[1] ) );
          break;
        // 'capture'. enemy idNumber. unit1 idNumber. unit2 idNumber
        case "capture":
          evt = new EbEvtControl();
          evt.setToken( getLong( line[1] ) );
          evt.setTokenDestroyer1( getLong( line[2] ) );
          evt.setTokenDestroyer2( getLong( line[3] ) );
          break;
        // 'build'. newUnit idNumber. x1. y1. weatherHen idNumber. type
        case "build":
          // newUnit idNumber shall be oreId for fmg
          evt = new EbEvtConstruct();
          evt.setToken( getLong( line[1] ) );
          evt.setTokenCarrier( getLong( line[4] ) );
          ((EbEvtConstruct)evt).setConstructType( getTokenType( line[5] ) );
          evt.setRegistrationId( playerId );
          evt.setAccountId( accountId );
          evt.setGameId( model.getGameId() );
          evt.setGameVersion( gameVersion );
          evt.setTransientComment( "line " + currentLineIndex + ": " + currentLine );
          model.getGameEvents().add( evt );

          evt = new EbEvtUnLoad();
          evt.setToken( getLong( line[1] ) );
          evt.setNewPosition( new AnBoardPosition( getInt( line[3] ), getInt( line[2] ) ) );
          evt.setTokenCarrier( getLong( line[4] ) );
          break;
        // 'loadOre'. ore idNumber. freighter idNumber
        case "loadOre":
          // 'load'. unit idNumber. receiver idNumber
          // load,unitId,carrierId,unitContentId,...
        case "load":
          evt = new EbEvtLoad();
          evt.setToken( getLong( line[1] ) );
          evt.setTokenCarrier( getLong( line[2] ) );

          for( int i = 3; i < line.length; i++ )
          {
            evt.setRegistrationId( playerId );
            evt.setAccountId( accountId );
            evt.setGameId( model.getGameId() );
            evt.setGameVersion( gameVersion );
            evt.setTransientComment( "line " + currentLineIndex + ": " + currentLine );
            model.getGameEvents().add( evt );

            evt = new EbEvtTransfer();
            evt.setToken( getLong( line[i] ) );
            evt.setTokenCarrier( getLong( line[1] ) );
            evt.setNewTokenCarrier( getLong( line[2] ) );
            evt.setCost( 0 );
          }
          break;
        // 'unload'. unit idNumber. x1. y1. fromWhom idNumber
        // unload,unitId,carrierId,x1,y1
        // unload,unitId,carrierId,x1,y1,sector
        // unload,unitId,carrierId,x1,y1,x2,y2
        case "unload":
          evt = new EbEvtUnLoad();
          evt.setToken( getLong( line[1] ) );
          evt.setTokenCarrier( getLong( line[2] ) );
          evt.setNewPosition( new AnBoardPosition( getInt( line[4] ), getInt( line[3] ) ) );
          if( line.length == 6 )
          {
            evt.getNewPosition().setSector( getSector( line[5] ) );
          }
          else if( line.length >= 7 )
          {
            evt.getNewPosition().setSector(
                hexCoordinateSystem.getSector( evt.getNewPosition(), new AnBoardPosition( getInt( line[6] ),
                    getInt( line[5] ) ) ) );
          }
          break;
        // 'transfer'. anItem idNumber. fromUnit idNumber. toUnit idNumber
        case "transfer":
          evt = new EbEvtTransfer();
          evt.setToken( getLong( line[1] ) );
          evt.setTokenCarrier( getLong( line[2] ) );
          evt.setNewTokenCarrier( getLong( line[3] ) );
          break;
        // deploy,id,x1,y1
        // deploy,id,x1,y1,sector
        // deploy,id,x1,y1,x2,y2
        case "deploy":
          evt = new EbEvtDeployment();
          evt.setToken( getLong( line[1] ) );
          evt.setPosition( new AnBoardPosition( getInt( line[3] ), getInt( line[2] ) ) );
          if( line.length == 5 )
          {
            evt.getPosition().setSector( getSector( line[4] ) );
          }
          else if( line.length >= 6 )
          {
            evt.getPosition().setSector(
                hexCoordinateSystem.getSector( evt.getPosition(), new AnBoardPosition( getInt( line[5] ),
                    getInt( line[4] ) ) ) );
          }
          break;
        case "endPlayer":
          EbEvtPlayerTurn endTurn = new EbEvtPlayerTurn();
          endTurn.setAccountId( accountId );
          endTurn.setGameId( model.getGameId() );
          endTurn.setGameVersion( gameVersion );
          model.getGameEvents().add( endTurn );
          break;
        case "message":
          EbEvtMessage evtmsg = new EbEvtMessage();
          evtmsg.setMessage( currentLine.substring( 8 ) );
          evtmsg.setAccountId( accountId );
          evtmsg.setGameId( model.getGameId() );
          evtmsg.setGameVersion( gameVersion );
          evtmsg.setTransientComment( "line " + currentLineIndex + ": " + currentLine );
          model.getGameEvents().add( evtmsg );
          break;
        case "remember":
          String toRemember = null;
          if( currentLine.length() > 9 )
          {
            toRemember = currentLine.substring( 9 );
          }
          evt = new EbRemember( toRemember );
          break;
        // comment
        case "":
          break;
        default:
          throw new Exception( "unknown statement: " + line[0] );
        }

        if( evt != null )
        {
          evt.setRegistrationId( playerId );
          evt.setAccountId( accountId );
          evt.setGameId( model.getGameId() );
          evt.setGameVersion( gameVersion );
          evt.setTransientComment( "line " + currentLineIndex + ": " + currentLine );
          model.getGameEvents().add( evt );
          // gameVersion++;
        }

        line = readLine( reader );
      }
    } catch( Throwable th )
    {
      throw new ParseException( "parse error: '" + currentLine + "' at line " + currentLineIndex, th );
    }
    // dataStore.close();

    return model;
  }

  private int currentLineIndex = 0;
  private String currentLine = null;

  private String[] readLine(BufferedReader reader)
  {
    String[] result = null;
    try
    {
      currentLineIndex++;
      currentLine = reader.readLine();
    } catch( IOException e )
    {
    }
    if( currentLine != null )
    {
      result = currentLine.split( "[, ]+" );
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
    printStream.println( "stai," + VERSION );
    printStream.println( "id," + p_game.getGame().getId() );
    printStream.println( "version," + p_game.getGame().getVersion() );
    printStream.println( "size," + p_game.getGame().getLandHeight() + "," + p_game.getGame().getLandWidth() );
    printStream.println( "turn," + p_game.getGame().getCurrentTimeStep() );
    printStream.println( "tides," + p_game.getGame().getCurrentTide() + "," + p_game.getGame().getNextTide() + ","
        + p_game.getGame().getNextTide2() );
    printStream.print( "currentPlayer" );
    for( long playerId : p_game.getGame().getCurrentPlayerIds() )
    {
      printStream.print( "," + p_game.getGame().getRegistration( playerId ).getId() );
    }
    printStream.println( "" );

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

    // print players units
    for( EbTeam team : p_game.getGame().getTeamByPlayOrder() )
    {
      printStream.println( "team," + team.getId() );
      for( EbRegistration player : team.getPlayers( p_game.getGame().getPreview() ) )
      {
        // export player information
        printStream.print( "player," + player.getId() );
        if( player.getAccount() != null )
        {
          printStream.print( "," + player.getAccount().getId() );
        }
        else
        {
          printStream.print( ",0" );
        }
        printStream.println( "," + player.getPtAction() );
        if( player.getRemember() != null )
        {
          printStream.println( "remember," + player.getRemember() );
        }
        for( EbToken token : p_game.getGame().getSetToken() )
        {
          if( token.getLocation() == Location.Orbit )
          {
            if( player.getEnuColor().contain( token.getColor() ) )
            {
              printStream.println( "create," + token.getId() + "," + getConstant( token.getType() ) + ","
                  + token.getColor() );
            }
          }
        }
        // export token on board and select turrets
        Map<AnBoardPosition, EbToken> turrets = new HashMap<AnBoardPosition, EbToken>();
        for( EbToken token : p_game.getGame().getSetToken() )
        {
          if( token.getColor() != EnuColor.None && player.getEnuColor().contain( token.getColor() ) )
          {
            if( token.getType() == TokenType.Turret )
            {
              EbToken currentSelectedTurret = turrets.get( token.getPosition() );
              if( currentSelectedTurret == null || token.getLocation() == Location.Board )
              {
                // keep this turret
                turrets.put( token.getPosition(), token );
              }
              else if( currentSelectedTurret.getLocation() == Location.Board )
              {
                // the selected turret is better
              }
              else
              {
                // the previousely selected turret and this turret are in graveyard
                // we don't mind which one we choose.
              }
              // don't export turret now.
              continue;
            }

            if( token.getLocation() == Location.Board )
            {
              printStream.print( "create," + token.getId() + "," + getConstant( token.getType() ) + ","
                  + token.getColor() + "," + token.getPosition().getY() + "," + token.getPosition().getX() );
              if( token.getHexagonSize() == 2 )
              {
                for( AnBoardPosition position : token.getExtraPositions( p_game.getGame().getCoordinateSystem() ) )
                {
                  printStream.print( "," + position.getY() + "," + position.getX() );
                }
                printStream.println( "" );
              }
              else
              {
                printStream.println( "," + getConstant( token.getPosition().getSector() ) );
              }
            }
          }
        }
        // export turrets
        for( Entry<AnBoardPosition, EbToken> entry : turrets.entrySet() )
        {
          EbToken turret = entry.getValue();
          EbToken freighter = p_game.getGame().getToken( turret.getPosition(), TokenType.Freighter );
          if( freighter != null )
          {
            String turretContant = getConstant( turret.getType() );
            if( turret.getLocation() == Location.Graveyard )
            {
              if( EbEvtRepair.isRepairable( p_game.getGame(), freighter.getId(), turret.getPosition() ) )
              {
                turretContant = "FMPDamagedTurret";
              }
              else
              {
                turretContant = "FMPIrreparableTurret";
              }
            }
            printStream.print( "create," + turret.getId() + "," + turretContant + "," + turret.getColor() + ","
                + turret.getPosition().getY() + "," + turret.getPosition().getX() );
            printStream.println( "," + getConstant( turret.getPosition().getSector() ) );
          }
        }
        // export token indise other token
        for( EbToken token : p_game.getGame().getSetToken() )
        {
          if( token.getLocation() == Location.Token )
          {
            if( player.getEnuColor().contain( token.getCarrierToken().getColor() ) )
            {
              printStream.println( "create," + token.getId() + "," + getConstant( token.getType() ) + ","
                  + token.getColor() + ","
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

  /**
   * 0 = west, 1 = NW, 2 = NE, 3 = east, 4 = SE, 5 = SW -- this is in STAI's normal coordinate system
   * @param sector
   * @return
   */
  private int getConstant(Sector sector)
  {
    switch( sector )
    {
    default:
    case North:
      // west
      return 0;
    case NorthEast:
      // SW
      return 5;
    case NorthWest:
      // NW
      return 1;
    case South:
      // east
      return 3;
    case SouthEast:
      // SE
      return 4;
    case SouthWest:
      // NE
      return 2;
    }
  }

  private Sector getSector(String str)
  {
    switch( getInt( str ) )
    {
    default:
    case 0:
      return Sector.North;
    case 1:
      return Sector.NorthWest;
    case 3:
      return Sector.South;
    case 4:
      return Sector.SouthEast;
    case 5:
      return Sector.NorthEast;
    }
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

  private TokenType getTokenType(String str)
  {
    switch( str )
    {
    case "ORE":
      return TokenType.Ore;
    case "FMPAttackBoat":
      return TokenType.Speedboat;
    default:
      str = str.substring( 3 );
      return TokenType.valueOf( str );
    }
  }
}
