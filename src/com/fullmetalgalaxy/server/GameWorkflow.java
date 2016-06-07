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

package com.fullmetalgalaxy.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jskills.IPlayer;
import jskills.ITeam;
import jskills.Rating;
import jskills.TrueSkillCalculator;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.CompanyStatistics;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.GameStatistics;
import com.fullmetalgalaxy.model.persist.PlayerGameStatistics;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminAbort;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePause;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtChangePlayerOrder;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTimeStep;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.server.EbAccount.NotificationQty;
import com.fullmetalgalaxy.server.pm.FmgMessage;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author vlegendr
 *
 * This class regroup various method that handle "game workflow".
 * It can check for update or cancel a game an update corresponding user profile
 * 
 * Life cycle of a game:
 * 
 * Open <-> Paused <-> Run -> Finished -> Deleted (not a real status)
 *    \----------\--------\-> Aborted-/
 */
public class GameWorkflow
{
  private final static FmpLogger log = FmpLogger.getLogger( GameWorkflow.class.getName() );

  static public ArrayList<AnEvent> checkUpdate(Game p_game, AnEvent p_futurEvents)
      throws RpcFmpException
  {
    ArrayList<AnEvent> events = new ArrayList<AnEvent>();
    events.add( p_futurEvents );
    return checkUpdate( p_game, events );
  }


  static public ArrayList<AnEvent> checkUpdate(Game p_game, List<AnEvent> p_futurEvents)
  {
    ArrayList<AnEvent> eventAdded = checkUpdate( p_game );
    AnEvent nextEvent = p_futurEvents.get( 0 );
    if( nextEvent == null )
    {
      return eventAdded;
    }

    try
    {
      if( nextEvent instanceof EbEvtPlayerTurn
          && (p_game.getCurrentTimeStep() == p_game.getEbConfigGameTime().getTotalTimeStep()) )
      {
        // end turn action in the last turn...
        // check that his freighter take off, if not, take off automatically
        for( EbToken token : p_game.getSetToken() )
        {
          EbRegistration registration = p_game.getRegistration( ((EbEvtPlayerTurn)nextEvent)
              .getOldPlayerId( p_game ) );
          if( (token.getType() == TokenType.Freighter)
              && (token.getLocation() == Location.Board)
              && (token.getColor() != EnuColor.None)
              && (registration != null)
              && (registration.getEnuColor().isColored( token.getColor() )) )
          {
            // automatic take off for this freighter
            EbEvtTakeOff eventTakeOff = new EbEvtTakeOff();
            eventTakeOff.setGame( p_game );
            eventTakeOff.setToken( token );
            eventTakeOff.setRegistration( p_game.getRegistrationByColor( token.getColor() ) );
            eventTakeOff.setAuto( true );
            eventTakeOff.checkedExec( p_game );
            p_game.addEvent( eventTakeOff );
            eventAdded.add( eventTakeOff );
          }
        }
      }
    } catch( RpcFmpException e )
    {
      // do nothing more as it is better to cancel update than cancel whole
      // player action
      log.error( e );
    }

    if( nextEvent instanceof EbAdminTimePause
        && ((p_game.getCurrentNumberOfRegiteredPlayer() < p_game.getMaxNumberOfPlayer())) )
    {
      if( p_game.getCurrentNumberOfRegiteredPlayer() < p_game.getMaxNumberOfPlayer() )
      {
        // game was running, but it is now reopen
        gameOpen( p_game );
      }
      else
      {
        gamePause( p_game );
      }
    }
    if( nextEvent instanceof EbAdminTimePlay )
    {
      // game was paused, but it is now running
      gameRun( p_game );
    }
    if( nextEvent instanceof EbAdminAbort )
    {
      // game was aborted
      gameAbort( p_game );
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
  static public ArrayList<AnEvent> checkUpdate(Game p_game)
  {
    assert p_game != null;
    ArrayList<AnEvent> eventAdded = new ArrayList<AnEvent>();

    // in some case, game is never updated
    if( p_game.getStatus() != GameStatus.Running && p_game.getStatus() != GameStatus.Open
        && p_game.getStatus() != GameStatus.Pause )
    {
      return eventAdded;
    }

    try
    {
      AnEvent lastEvent = p_game.getLastLog();
      if( !p_game.isFinished() && lastEvent != null )
      {
        // game isn't finished: look for any update
        // search update according to the last action
        //

        if( p_game.isParallel() && lastEvent != null && lastEvent instanceof EbEvtLand
            && p_game.getCurrentTimeStep() == 1 )
        {
          // a player is just landed and game is parallel: next player
          EbEvtPlayerTurn action = new EbEvtPlayerTurn();
          action.setLastUpdate( ServerUtil.currentDate() );
          action.setAccountId( ((EbEvtLand)lastEvent).getAccountId() );
          action.setGame( p_game );
          action.setAuto( true );
          action.checkedExec( p_game );
          p_game.addEvent( action );
          eventAdded.add( action );
          lastEvent = action;
        }

        if( lastEvent instanceof EbGameJoin )
        {
          if( p_game.getCurrentNumberOfRegiteredPlayer() == p_game.getMaxNumberOfPlayer()
              && !p_game.getEbConfigGameTime().isQuick() )
          {
            // if the last player is just connected (and slow game),
            // automatically launch the game.
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

          if( p_game.getCurrentTimeStep() == 1
              && lastEvent instanceof EbAdminTimePlay
              && p_game.getFreighter(
                  p_game.getTeamByPlayOrder().get( 0 ).getPlayers( p_game.getPreview() ).get( 0 ) )
                  .getLocation() == Location.Orbit )
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
        if( p_game.getCurrentTimeStep() == 2 && !p_game.isParallel()
            && p_game.getLastGameLog() instanceof EbEvtTide )
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
        if( p_game.isParallel() && p_game.getStatus() == GameStatus.Running
            && p_game.getCurrentTimeStep() > 1 )
        {
          long currentTimeInMiliSec = System.currentTimeMillis();
          while( (!p_game.isFinished())
              && ((currentTimeInMiliSec - p_game.getLastTimeStepChange().getTime()) > p_game
                  .getEbConfigGameTime().getTimeStepDurationInMili()) )
          {
            // automatic take off for all freighter just before end game
            if( p_game.getCurrentTimeStep() >= p_game.getEbConfigGameTime().getTotalTimeStep() )
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
                  eventTakeOff.setRegistration( p_game.getRegistrationByColor( token.getColor() ) );
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
              eventTide.setNextTide( Tide.getRandom( p_game.getAverageTideLevel() ) );
              eventTide.setGame( p_game );
              eventTide.checkedExec( p_game );
              p_game.addEvent( eventTide );
              eventAdded.add( eventTide );
              lastEvent = eventTide;
            }
          }
        }
        else if( !p_game.isParallel()
            && p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
        {
          for( EbRegistration registration : p_game.getSetRegistration() )
          {
            if( (p_game.getCurrentPlayerIds().contains( registration.getId() ))
                && (registration.getTeam(p_game).getEndTurnDate() != null)
                && (registration.getTeam(p_game).getEndTurnDate().getTime() <= System.currentTimeMillis())
                && (p_game.getCurrentTimeStep() > 1) ) // never
                                                       // skip
                                                       // first
                                                       // turn
            {
              // change player's turn
              EbEvtPlayerTurn event = new EbEvtPlayerTurn();
              event.setAuto( true );
              event.setGame( p_game );
              event.setAccountId( registration.getAccount().getId() );
              event.setOldPlayerId( registration.getId() );
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
        }

        // are all player take off before the end ?
        // -> in this case, we don't mind new tide
        if( lastEvent instanceof EbEvtPlayerTurn
            && (p_game.getCurrentTimeStep() >= p_game.getEbConfigGameTime().getTakeOffTurns()
                .get( 0 )) )
        {
          boolean isGameFinish = true;
          for( EbRegistration registration : p_game.getSetRegistration() )
          {
            if( p_game.getCurrentPlayerIds().contains( registration.getId() ) )
            {
              for( EbToken freighter : p_game.getAllFreighter( registration.getColor() ) )
              {
                if( freighter.getLocation() == Location.Board )
                {
                  isGameFinish = false;
                }
              }
            }
          }
          while( isGameFinish && !p_game.isFinished() )
          {
            EbEvtPlayerTurn event = new EbEvtPlayerTurn();
            event.setAuto( true );
            event.setGame( p_game );
            if( !p_game.getCurrentPlayerIds().isEmpty() )
            {
              event.setOldPlayerId( p_game.getCurrentPlayerIds().get( 0 ) );
              EbRegistration oldPlayer = p_game.getRegistration( event.getOldPlayerId( p_game ) );
              if( oldPlayer.haveAccount() )
              {
                event.setAccountId( oldPlayer.getAccount().getId() );
              }
            }
            event.checkedExec( p_game );
            p_game.addEvent( event );
            eventAdded.add( event );
            lastEvent = event;
          }
        }

        // if new turn occur: trigger new tide
        if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep()
            && p_game.getStatus() == GameStatus.Running )
        {
          // next tide !
          EbEvtTide eventTide = new EbEvtTide();
          eventTide.setGame( p_game );
          eventTide.setNextTide( Tide.getRandom( p_game.getAverageTideLevel() ) );
          eventTide.checkedExec( p_game );
          p_game.addEvent( eventTide );
          eventAdded.add( eventTide );
          lastEvent = eventTide;
        }


        // triggers
        p_game.execTriggers();
      }
    } catch( RpcFmpException e )
    {
      // do nothing more as it is better to cancel update than cancel whole
      // player action
      log.error( e );
    }

    if( p_game.isFinished() && p_game.getStatus() != GameStatus.History )
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
    if( (p_game.getStatus() != GameStatus.Open && p_game.getStatus() != GameStatus.Pause && p_game
        .getStatus() != GameStatus.Running)
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
      new FmgMessage( "gameBlocked", p_game ).sendEMail( account );
    }

    if( p_game.getEbConfigGameTime().isParallel() )
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
            .getMyRegistration( p_game ).getColor() ) )
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
          event.setAccountId( ((EbEvtTakeOff)lastEvent).getMyRegistration( p_game ).getAccount()
              .getId() );
          event.setOldPlayerId( ((EbEvtTakeOff)lastEvent).getMyRegistration( p_game ).getId() );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          eventAdded.add( event );
          lastEvent = event;
        }
      }
    }

    long last48Hour = System.currentTimeMillis() - (1000 * 60 * 60 * 48);
    if( p_game.getStatus() == GameStatus.Running && p_game.getLastUpdate().getTime() < last48Hour )
    {
      // current player didn't play since 48 hours...
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        if( p_game.getCurrentPlayerIds().contains( registration.getId() ) )
        {
          if( registration != null && registration.getAccount() != null )
          {
            EbAccount account = FmgDataStore.dao().get( EbAccount.class,
                registration.getAccount().getId() );
            if( new FmgMessage( "playerDontPlay", p_game ).setNotifLevel( NotificationQty.Min ).send( account ) )
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
      }
    }

    return eventAdded;
  }



  public static class TSTeam extends HashMap<IPlayer, Rating> implements ITeam
  {
    private static final long serialVersionUID = 1L;
  }

  private static void getTeams(Game p_game, List<ITeam> p_teams, int[] p_teamRanks)
  {
    int previousScore = Integer.MAX_VALUE;
    int currentRank = 0;
    int index = 0;
    for(EbTeam ebteam : p_game.getTeamByWinningRank() )
    {
      ITeam team = new TSTeam();
      for(EbRegistration registration : ebteam.getPlayers( p_game.getPreview() ) )
      {
        EbAccount account = null;
        if( registration.getAccount() != null && !registration.getAccount().isTrancient() )
        {
          account = FmgDataStore.dao().get( EbAccount.class, registration.getAccount().getId() );
        }
        if( account == null )
        {
          log.error( "game " + p_game + " has account that wasn't found and then not updated." );
        }
        else
        {
          team.put( account, new Rating( account.getTrueSkillMean(), account.getTrueSkillSD() ) );
        }
      }
      if( !team.isEmpty() )
      {
        if( ebteam.estimateWinningScore( p_game ) < previousScore )
        {
          previousScore = ebteam.estimateWinningScore( p_game );
          currentRank++;
        }
        p_teamRanks[index] = currentRank;
        p_teams.add( team );
        index++;
      }
    }
    if( index == 0 )
    {
      log.error( "game " + p_game + " has not players found." );
    }
  }



  /**
   * called when a game is newly open due to his creation or paused with a missing player
   * @param p_game
   */
  public static void gameOpen(Game p_game)
  {
    p_game.setStatus( GameStatus.Open );
  }

  /**
   * called when a game start playing
   * @param p_game
   */
  public static void gamePause(Game p_game)
  {
    p_game.setStatus( GameStatus.Pause );
    GlobalVars.incrementCurrentGameCount( -1 );
  }

  /**
   * called when a game start playing
   * @param p_game
   */
  public static void gameRun(Game p_game)
  {
    p_game.setStatus( GameStatus.Running );
    GlobalVars.incrementCurrentGameCount( 1 );
  }


  /**
     * this function update game and accounts statistics
     * @param p_game
     */
  public static void updateStat4FinishedGame(Game p_game, boolean p_updateCompanyStat)
  {
    if( p_game.getStatus() != GameStatus.History || p_game.getGameType() != GameType.MultiPlayer )
    {
      p_game.setStats( null );
      return;
    }

    // update game stats
    p_game.setStats( new GameStatistics( p_game ) );
    FmgDataStore ds = new FmgDataStore( false );
    ds.put( p_game );

    // then players stats
    ArrayList<ITeam> teams = new ArrayList<ITeam>();
    int[] teamRanks = new int[p_game.getTeams().size()];
    GameWorkflow.getTeams( p_game, teams, teamRanks );
    Map<IPlayer, Rating> newRating = TrueSkillCalculator.calculateNewRatings(
        ServerUtil.getGameInfo(), teams, teamRanks );


    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime( p_game.getEndDate() );
    int gameEndYear = calendar.get( Calendar.YEAR );

    Date gameOldestDate = new Date( System.currentTimeMillis()
        - FmpConstant.currentStatsTimeWindowInMillis );

    // save accounts for updated true skill value
    for( Entry<IPlayer, Rating> entry : newRating.entrySet() )
    {
      PlayerGameStatistics playerStat = null;
      if( entry.getKey() instanceof EbAccount )
      {
        EbAccount account = (EbAccount)entry.getKey();
        // create or update GamePlayerStatistic
        Query<PlayerGameStatistics> query = ds.query( PlayerGameStatistics.class );
        query.ancestor( p_game.getPreview() );
        query.filter( "m_account.id", account.getId() );
        playerStat = query.get();
        if( playerStat != null )
        {
          log.error( "add existing player stat for account " + account.getPseudo() + " and game "
              + p_game.getName() );
          account.setTrueSkill( account.getTrueSkillMean() - playerStat.getTsMeanUpdate(),
              account.getTrueSkillSD() - playerStat.getTsSDUpdate() );
          account.getFullStats().removeStatistic( playerStat );
          if( gameOldestDate.before( playerStat.getGameEndDate() ) )
          {
            account.getCurrentStats().removeStatistic( playerStat );
          }
        }
        else
        {
          // stat wasn't found: created a new one
          playerStat = new PlayerGameStatistics();
        }
        playerStat.setStatistics( p_game, p_game.getRegistrationByIdAccount( account.getId() ) );
        playerStat.setKeyGamePreview( new Key<EbGamePreview>( EbGamePreview.class, p_game.getId() ) );
        playerStat.setTsMeanUpdate( entry.getValue().getMean() - account.getTrueSkillMean() );
        playerStat.setTsSDUpdate( entry.getValue().getStandardDeviation() - account.getTrueSkillSD() );
        playerStat.setGameStatus( p_game.getStatus() );
        ds.put( playerStat );

        // and save account
        account.setTrueSkill( entry.getValue() );
        account.getFullStats().addStatistic( playerStat );
        if( gameOldestDate.before( playerStat.getGameEndDate() ) )
        {
          account.getCurrentStats().addStatistic( playerStat );
        }
        FmgDataStore dsAccount = new FmgDataStore( false );
        dsAccount.put( account );
        dsAccount.close();
      }
      if( playerStat != null )
      {
        GlobalVars.incrementFGameFmpScore( playerStat.getPlayerScore() );
      }
    }
    ds.close();


    // now update company statistics
    if( p_updateCompanyStat )
    {
      for( EbTeam team : p_game.getTeams() )
      {
        Query<CompanyStatistics> query = FmgDataStore.dao().query( CompanyStatistics.class );
        query.filter( "m_company", team.getCompany() );
        query.filter( "m_year", gameEndYear );
        Key<CompanyStatistics> keyCompanyStat = query.getKey();
        ds = new FmgDataStore( false );
        CompanyStatistics companyStat = ds.find( keyCompanyStat );
        if( companyStat == null )
        {
          companyStat = new CompanyStatistics( team.getCompany() );
          companyStat.setYear( gameEndYear );
        }
        companyStat.addResult( team.estimateWinningScore( p_game ), p_game.getInitialScore() );
        ds.put( companyStat );
        ds.close();
      }
    }
  }



  private static class RemovePlayerGameStatistics extends LongDBTask<PlayerGameStatistics>
  {
    private static final long serialVersionUID = 1L;
    private Date m_gameEndDate = null;
    private long m_gameId = 0;

    // this record will be used to keep track of game that we remove stats
    // the tree map is to keep game id in date order
    Map<Date, Long> gameId2Recompute = new TreeMap<Date, Long>();

    public RemovePlayerGameStatistics(long p_gameId, Date p_gameEndDate)
    {
      m_gameId = p_gameId;
      m_gameEndDate = p_gameEndDate;
    }

    @Override
    protected Query<PlayerGameStatistics> getQuery()
    {
      Query<PlayerGameStatistics> query = FmgDataStore.dao().query( PlayerGameStatistics.class );
      query.filter( "m_gameEndDate >=", m_gameEndDate );
      return query;
    }

    @Override
    protected void processKey(Key<PlayerGameStatistics> p_key)
    {
      PlayerGameStatistics playerStat = FmgDataStore.dao().get( p_key );
      if( playerStat.getGameStatus() != GameStatus.History )
      {
        return;
      }
      // set aborted flag on stat that correspond to aborted game
      if( playerStat.getGameId() == m_gameId )
      {
        FmgDataStore ds = new FmgDataStore( false );
        PlayerGameStatistics stat = ds.find( p_key );
        if( stat != null )
        {
          stat.setGameStatus( GameStatus.Aborted );
          ds.put( stat );
        }
        ds.close();
      }
      else
      {
        gameId2Recompute.put( playerStat.getGameEndDate(), playerStat.getGameId() );
      }
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.find( EbAccount.class, playerStat.getAccount().getId() );
      if( account != null )
      {
        account.getFullStats().removeStatistic( playerStat );
        account.getCurrentStats().removeStatistic( playerStat );
        account.setTrueSkill( account.getTrueSkillMean() - playerStat.getTsMeanUpdate(),
            account.getTrueSkillSD() - playerStat.getTsSDUpdate() );
        ds.put( account );
      }
      ds.close();
    }

    @Override
    protected void finish()
    {
      Game game = FmgDataStore.dao().get( Game.class, m_gameId );
      // then, remove company statistics
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime( m_gameEndDate );
      int gameEndYear = calendar.get( Calendar.YEAR );
      for( EbTeam team : game.getTeams() )
      {
        Query<CompanyStatistics> queryCS = FmgDataStore.dao().query( CompanyStatistics.class );
        queryCS.filter( "m_company", team.getCompany() );
        queryCS.filter( "m_year", gameEndYear );
        Key<CompanyStatistics> keyCompanyStat = queryCS.getKey();
        FmgDataStore ds = new FmgDataStore( false );
        CompanyStatistics companyStat = ds.find( keyCompanyStat );
        if( companyStat == null )
        {
          companyStat = new CompanyStatistics( team.getCompany() );
          companyStat.setYear( gameEndYear );
        }
        companyStat.removeResult( team.estimateWinningScore( game ), game.getInitialScore() );
        ds.put( companyStat );
        ds.close();
      }

      // finally recompute stats for valid games
      for( Map.Entry<Date, Long> entry : gameId2Recompute.entrySet() )
      {
        QueueFactory.getDefaultQueue().add(
            TaskOptions.Builder.withPayload( new UpdateStat4FinishedGame( entry.getValue(), false ) ) );
      }
    }

  }


  private static class UpdateStat4FinishedGame implements DeferredTask
  {
    private static final long serialVersionUID = 1L;
    private long m_gameId = 0;
    boolean m_updateCompanyStat = true;

    public UpdateStat4FinishedGame(long p_gameId, boolean p_updateCompanyStat)
    {
      m_gameId = p_gameId;
      m_updateCompanyStat = p_updateCompanyStat;
    }
    @Override
    public void run()
    {
      Game game = FmgDataStore.dao().find( Game.class, m_gameId );
      if( game != null )
      {
        updateStat4FinishedGame( game, m_updateCompanyStat );
      }

    }

  }


  /**
   * called when a game finished normally
   * @param p_game
   */
  public static void gameFinished(Game p_game)
  {
    p_game.setStatus( GameStatus.History );
    GlobalVars.incrementCurrentGameCount( -1 );

    if( p_game.getGameType() == GameType.MultiPlayer )
    {
      // add all stat related to finished game
      GlobalVars.incrementFGameNbConfigGameTime( p_game.getConfigGameTime(), 1 );
      GlobalVars.incrementFGameNbOfHexagon( p_game.getNumberOfHexagon() );
      GlobalVars.incrementFGameNbPlayer( p_game.getSetRegistration().size() );

      updateStat4FinishedGame( p_game, true );
    }
    else if( p_game.getGameType() == GameType.Initiation )
    {
      GlobalVars.incrementFGameInitiationCount( 1 );
    }
  }

  /**
   * called when a game is aborted
   * @param p_game
   */
  public static void gameAbort(Game p_game)
  {
    if( p_game.getStatus() == GameStatus.History && p_game.getGameType() == GameType.MultiPlayer )
    {
      // for game that are History, we have to remove stat
      GlobalVars.incrementFGameNbConfigGameTime( p_game.getConfigGameTime(), -1 );
      GlobalVars.incrementFGameNbOfHexagon( -1 * p_game.getNumberOfHexagon() );
      GlobalVars.incrementFGameNbPlayer( -1 * p_game.getSetRegistration().size() );

      // almost the reverse of updateStat4FinishedGame.
      // must be call for game that will be cancelled after finished
      QueueFactory.getQueue( "longDBTask" ).add(
          TaskOptions.Builder.withPayload( new RemovePlayerGameStatistics( p_game.getId(), p_game
              .getEndDate() ) ) );
    }
    else if( p_game.getStatus() == GameStatus.Running )
    {
      GlobalVars.incrementCurrentGameCount( -1 );
    }
    p_game.setStatus( GameStatus.Aborted );
  }



}
