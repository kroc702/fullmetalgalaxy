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

package com.fullmetalgalaxy.server;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminBan;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtChangePlayerOrder;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTimeStep;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;

/**
 * @author vlegendr
 *
 * This class regroup various method that handle "game workflow".
 * It can check for update or cancel a game an update corresponding user profile
 * 
 * Life cycle of a game:
 * 
 * Open <-> Paused <-> Run -> Finished -> Deleted
 *                        \-> Aborted-/
 */
public class GameWorkflow
{
  static public ArrayList<AnEvent> checkUpdate(Game p_game, AnEvent p_futurEvents)
      throws RpcFmpException
  {
    ArrayList<AnEvent> events = new ArrayList<AnEvent>();
    events.add( p_futurEvents );
    return checkUpdate( p_game, events );
  }


  static public ArrayList<AnEvent> checkUpdate(Game p_game, List<AnEvent> p_futurEvents)
      throws RpcFmpException
  {
    ArrayList<AnEvent> eventAdded = checkUpdate( p_game );
    AnEvent nextEvent = p_futurEvents.get( 0 );
    if( nextEvent == null )
    {
      return eventAdded;
    }

    if( (nextEvent.getType() == GameLogType.EvtPlayerTurn)
        && (p_game.getCurrentTimeStep() == p_game.getEbConfigGameTime().getTotalTimeStep()) )
    {
      // end turn action in the last turn...
      // check that his freighter take off, if not, take off automatically
      for( EbToken token : p_game.getSetToken() )
      {
        if( (token.getType() == TokenType.Freighter)
            && (p_game.getCurrentPlayerRegistration().getEnuColor().isColored( token.getColor() ))
            && (token.getLocation() == Location.Board) )
        {
          // automatic take off for this freighter
          EbEvtTakeOff eventTakeOff = new EbEvtTakeOff();
          eventTakeOff.setGame( p_game );
          eventTakeOff.setToken( token );
          eventTakeOff.setAccountId( p_game.getRegistrationByColor( token.getColor() ).getAccount()
              .getId() );
          eventTakeOff.setAuto( true );
          eventTakeOff.checkedExec( p_game );
          p_game.addEvent( eventTakeOff );
          eventAdded.add( eventTakeOff );
        }
      }
    }
    if( (nextEvent.getType() == GameLogType.AdminTimePause)
        && ((p_game.getCurrentNumberOfRegiteredPlayer() < p_game.getMaxNumberOfPlayer())) )
    {
      // game was running, but it is now reopen
      gameOpen( p_game );
    }
    if( (nextEvent.getType() == GameLogType.AdminTimePlay) )
    {
      // game was paused, but it is now running
      gameRun( p_game );
    }
    if( (nextEvent.getType() == GameLogType.AdminBan) )
    {
      EbAccount account = FmgDataStore.dao().get(
          EbAccount.class,
          p_game.getRegistration( ((EbAdminBan)nextEvent).getRegistrationId() ).getAccount()
              .getId() );
      AccountStatsManager.gameBan( account, p_game );
    }

    return eventAdded;
  }


  /**
   * update anything on game which can change without user intervention. 
   * ie:<br/>
   * - player actions pts (for asynchron game only)<br/>
   * - tides (for asynchron game only)<br/>
   * - current player's turn. (for turn by turn game only)<br/>
   * - any other defined triggers
   * - set history flag and compute stats
   * @param p_game
   * @return the getResultList of all event which occur during this update.
   */
  static public ArrayList<AnEvent> checkUpdate(Game p_game) throws RpcFmpException
  {
    assert p_game != null;
    ArrayList<AnEvent> eventAdded = new ArrayList<AnEvent>();

    // in some case, game is never updated
    if( p_game.isHistory() || p_game.getGameType() != GameType.MultiPlayer )
    {
      return eventAdded;
    }
    AnEvent lastEvent = p_game.getLastLog();
    if( !p_game.isFinished() && lastEvent != null )
    {
      // game isn't finished: look for any update
      // search update according to the last action
      //
      
      if( p_game.isAsynchron() && lastEvent != null && lastEvent.getType() == GameLogType.EvtLand )
      {
        // a player is just landed and game is parallel: next player
        EbEvtPlayerTurn action = new EbEvtPlayerTurn();
        action.setLastUpdate( ServerUtil.currentDate() );
        action.setAccountId( ((EbEvtLand)lastEvent).getAccountId() );
        action.setGame( p_game );
        action.checkedExec( p_game );
        p_game.addEvent( action );
        eventAdded.add( action );
        lastEvent = action;
      }

      if( lastEvent.getType() == GameLogType.GameJoin )
      {
        EbAccount account = FmgDataStore.dao().get( EbAccount.class,
            ((EbGameJoin)lastEvent).getAccountId() );
        AccountStatsManager.gameJoin( account, p_game );
        
        if( p_game.getCurrentNumberOfRegiteredPlayer() == p_game.getMaxNumberOfPlayer() )
        {
          // TODO we may prefer starting game not in live mode only (slow game
          // only)
          // if the last player is just connected, automatically launch the
          // game.
          EbAdminTimePlay action = new EbAdminTimePlay();
          action.setAuto( true );
          action.setLastUpdate( ServerUtil.currentDate() );
          action.setAccountId( ((EbGameJoin)lastEvent).getAccountId() );
          action.setGame( p_game );
          action.checkedExec( p_game );
          p_game.addEvent( action );
          eventAdded.add( action );
          lastEvent = action;
          gameRun( p_game );
        }

        if( p_game.getCurrentTimeStep() == 0
            && lastEvent.getType() == GameLogType.AdminTimePlay
            && p_game.getFreighter( p_game.getRegistrationByOrderIndex( 0 ) ).getLocation() == Location.Orbit )
        {
          // game is starting
          EbEvtChangePlayerOrder action = new EbEvtChangePlayerOrder();
          action.setLastUpdate( ServerUtil.currentDate() );
          action.initRandomOrder( p_game );
          action.setGame( p_game );
          action.checkedExec( p_game );
          p_game.addEvent( action );
          eventAdded.add( action );
          lastEvent = action;
        }
      }
      if( p_game.getCurrentTimeStep() == 1 && !p_game.isAsynchron()
          && p_game.getLastGameLog().getType() == GameLogType.EvtTide )
      {
        // second turn: everybody should be landed
        EbEvtChangePlayerOrder action = new EbEvtChangePlayerOrder();
        action.setLastUpdate( ServerUtil.currentDate() );
        action.initBoardOrder( p_game );
        action.setGame( p_game );
        action.checkedExec( p_game );
        p_game.addEvent( action );
        eventAdded.add( action );
        lastEvent = action;
      }


      // search any other update
      //
      if( p_game.isAsynchron() && p_game.isStarted() && p_game.getCurrentTimeStep() != 0 )
      {
        long currentTimeInMiliSec = System.currentTimeMillis();
        while( (!p_game.isFinished())
            && ((currentTimeInMiliSec - p_game.getLastTimeStepChange().getTime()) > p_game
                .getEbConfigGameTime().getTimeStepDurationInMili()) )
        {
          // automatic take off for all freighter just before end game
          if( p_game.getCurrentTimeStep() > p_game.getEbConfigGameTime().getTotalTimeStep() )
          {
            for( EbToken token : p_game.getSetToken() )
            {
              if( (token.getType() == TokenType.Freighter)
                  && (token.getLocation() == Location.Board) )
              {
                // automatic take off for this freighter
                EbEvtTakeOff eventTakeOff = new EbEvtTakeOff();
                eventTakeOff.setGame( p_game );
                eventTakeOff.setToken( token );
                eventTakeOff.setAccountId( p_game.getRegistrationByColor( token.getColor() )
                    .getAccount().getId() );
                eventTakeOff.setAuto( true );
                eventTakeOff.checkedExec( p_game );
                p_game.addEvent( eventTakeOff );
                eventAdded.add( eventTakeOff );
                lastEvent = eventTakeOff;
              }
            }
          }
          // new time step
          EbEvtTimeStep event = new EbEvtTimeStep();
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          eventAdded.add( event );
          lastEvent = event;
          if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
          {
            // next tide
            EbEvtTide eventTide = new EbEvtTide();
            eventTide.setNextTide( Tide.getRandom() );
            eventTide.setGame( p_game );
            eventTide.checkedExec( p_game );
            p_game.addEvent( eventTide );
            eventAdded.add( eventTide );
            lastEvent = eventTide;
          }
        }
      }
      else if( p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
      {
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate().getTime() 
                    <= System.currentTimeMillis())
            && (p_game.getCurrentTimeStep() != 0) ) // never skip first turn
        {
          // change player's turn
          p_game.getCurrentPlayerRegistration().getOrderIndex();
          EbEvtPlayerTurn event = new EbEvtPlayerTurn();
          event.setAuto( true );
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          eventAdded.add( event );
          lastEvent = event;
        }
        /* TODO
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() == null) )
        {
          ModelFmpUpdate updates = null;// FmpUpdateStatus.getModelUpdate( null,
                                        // p_game.getId(), null );
          if( updates.isUserConnected( p_game.getCurrentPlayerRegistration().getAccountPseudo() ) )
          {
            // current player is connected, update his end turn
            p_game.getCurrentPlayerRegistration().setEndTurnDate(
                new Date( System.currentTimeMillis()
                    + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
            // p_session.persist( p_game.getCurrentPlayerRegistration() );
            updates.getConnectedUser( p_game.getCurrentPlayerRegistration().getAccountId() )
                .setEndTurnDate(
                    new Date( System.currentTimeMillis()
                        + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );

            isUpdated = true;
          }
        }*/
      }

      // are all player take off before the end ?
      // -> in this case, we don't mind new tide
      if( lastEvent instanceof EbEvtPlayerTurn
          && (p_game.getCurrentTimeStep() >= p_game.getEbConfigGameTime().getTakeOffTurns().get( 0 ))
          && (p_game.getCurrentPlayerRegistration() != null) )
      {
        boolean isGameFinish = true;
        for( EbToken freighter : p_game.getAllFreighter( p_game.getCurrentPlayerRegistration() ) )
        {
          if( freighter.getLocation() == Location.Board )
          {
            isGameFinish = false;
          }
        }
        while( isGameFinish && !p_game.isFinished() )
        {
          EbEvtPlayerTurn event = new EbEvtPlayerTurn();
          event.setAuto( true );
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          eventAdded.add( event );
          lastEvent = event;
        }
      }

      // if new turn occur: trigger new tide
      int oldPlayerOrderIndex = Integer.MAX_VALUE;
      if( !p_game.isAsynchron() )
      {
        p_game.getPreviousPlayerRegistration().getOrderIndex();
      }
      if( lastEvent instanceof EbEvtPlayerTurn
          && (p_game.getCurrentPlayerRegistration() == null
               || p_game.getCurrentPlayerRegistration().getOrderIndex() <= oldPlayerOrderIndex ) )
      {
        // new turn !
        if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
        {
          // next tide
          EbEvtTide eventTide = new EbEvtTide();
          eventTide.setGame( p_game );
          eventTide.setNextTide( Tide.getRandom() );
          eventTide.checkedExec( p_game );
          p_game.addEvent( eventTide );
          eventAdded.add( eventTide );
          lastEvent = eventTide;
        }
      }


      // triggers
      p_game.execTriggers();
    }

    if( p_game.isFinished() && !p_game.isHistory() )
    {
      // game have to be archived and user stat updated
      gameFinished( p_game );
    }

    return eventAdded;
  }


  /**
   * these update are check less often (ie only by cron task) to see if a game is blocked
   * and if we can unblock it.
   * send email to player or game creator
   * play obvious action
   * @param p_game
   * @return
   * @throws RpcFmpException
   */
  static public ArrayList<AnEvent> checkUpdate2Unblock(Game p_game) throws RpcFmpException
  {
    assert p_game != null;
    ArrayList<AnEvent> eventAdded = new ArrayList<AnEvent>();

    // in some case, game is never updated
    long lastHour = System.currentTimeMillis() - (1000 * 60 * 60);
    if( p_game.isHistory() || p_game.getGameType() != GameType.MultiPlayer
        || p_game.getLastUpdate().getTime() > lastHour )
    {
      // in all these case, game isn't blocked
      return eventAdded;
    }

    // if game is blocked since a long time, send email to game creator
    long last10Days = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 10);
    if( p_game.getLastUpdate().getTime() < last10Days )
    {
      // this message will keep track of this, save game and update last update
      // -> creator will have to do something or he will receive this message
      // every 10 days.
      EbAdmin event = new EbAdmin();
      event.setAuto( true );
      event.setMessage( "send a game blocked message to game creator" );
      p_game.addEvent( event );
      eventAdded.add( event );

      // send message to game creator
      EbAccount account = FmgDataStore.dao().get( EbAccount.class,
          p_game.getAccountCreator().getId() );
      new FmgMessage( "gameBlocked" ).sendEMail( account );
    }

    if( p_game.getEbConfigGameTime().isAsynchron() )
    {
      // game isn't blocked
      return eventAdded;
    }

    AnEvent lastEvent = p_game.getLastLog();
    if( !p_game.isFinished() && lastEvent != null )
    {
      // player may forget to set next turn after take off
      if( lastEvent instanceof EbEvtTakeOff )
      {
        boolean isGameFinish = true;
        for( EbToken freighter : p_game.getAllFreighter( ((EbEvtTakeOff)lastEvent)
            .getMyRegistration( p_game ) ) )
        {
          if( freighter.getLocation() == Location.Board )
          {
            isGameFinish = false;
          }
        }
        if( isGameFinish )
        {
          EbEvtPlayerTurn event = new EbEvtPlayerTurn();
          event.setAuto( true );
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          eventAdded.add( event );
          lastEvent = event;
        }
      }
    }

    long last48Hour = System.currentTimeMillis() - (1000 * 60 * 60 * 48);
    if( p_game.isStarted() && p_game.getLastUpdate().getTime() < last48Hour )
    {
      // current player didn't play since 48 hours...
      EbRegistration registration = p_game.getCurrentPlayerRegistration();
      if( registration != null && registration.getAccount() != null )
      {
        EbAccount account = FmgDataStore.dao().get( EbAccount.class,
            registration.getAccount().getId() );
        if( new FmgMessage( "playerDontPlay" ).sendEMail( account ) )
        {
          registration.addNotifSended( "playerDontPlay" );
          // to save game & registration
          EbAdmin event = new EbAdmin();
          event.setAuto( true );
          event.setMessage( "send a notification to current player" );
          p_game.addEvent( event );
          eventAdded.add( event );
        }
      }
      
    }

    return eventAdded;
  }


  /**
   * called when a game is newly open due to his creation or paused with a missing player
   * @param p_game
   */
  public static void gameOpen(Game p_game)
  {
    if( p_game.getLastLog() != null )
    {
      // if game have some event in his log
      // its mean that it was running before
      GlobalVars.incrementRunningGameCount( -1 );
    }
    GlobalVars.incrementOpenGameCount( 1 );

  }

  /**
   * called when a game start playing
   * @param p_game
   */
  public static void gameRun(Game p_game)
  {
    GlobalVars.incrementOpenGameCount( -1 );
    GlobalVars.incrementRunningGameCount( 1 );
  }


  /**
   * called when a game finished normally
   * @param p_game
   */
  public static void gameFinished(Game p_game)
  {
    AccountStatsManager.gameFinish( p_game );
    p_game.setHistory( true );
    GlobalVars.incrementRunningGameCount( -1 );
    GlobalVars.incrementFinishedGameCount( 1 );

    // add all stat related to finished game
    GlobalVars.incrementFGameNbConfigGameTime( p_game.getConfigGameTime(), 1 );
    GlobalVars.incrementFGameNbConfigGameVariant( p_game.getConfigGameVariant(), 1 );
    GlobalVars.incrementFGameNbOfHexagon( p_game.getNumberOfHexagon() );
    GlobalVars.incrementFGameNbPlayer( p_game.getSetRegistration().size() );
  }

  /**
   * called when a game is aborted
   * @param p_game
   */
  public static void gameAbort(Game p_game)
  {
    AccountStatsManager.gameAbort( p_game );
    if( p_game.isOpen() )
    {
      GlobalVars.incrementOpenGameCount( -1 );
    }
    else if( !p_game.isFinished() )
    {
      GlobalVars.incrementRunningGameCount( -1 );
    }
    else
    {
      // well, game is finished, why do we cancel it ?
      GlobalVars.incrementFinishedGameCount( -1 );
    }
    GlobalVars.incrementAbortedGameCount( 1 );
    p_game.setHistory( true );
  }

  /**
   * called when a game is deleted
   * I may want to use this to make a difference between a deleted and aborted game.
   * After this call, game is deleted from database.
   * @param p_game
   */
  public static void gameDelete(Game p_game)
  {
    if( !p_game.isFinished() )
    {
      gameAbort(p_game);
    }
    GlobalVars.incrementDeletedGameCount( 1 );
    AccountStatsManager.gameDelete( p_game );
    p_game.setHistory( true );
    FmgDataStore dataStore = new FmgDataStore( false );
    dataStore.delete( Game.class, p_game.getId() );
    dataStore.close();
  }

}
