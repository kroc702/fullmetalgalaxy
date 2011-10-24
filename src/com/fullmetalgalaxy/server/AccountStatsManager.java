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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbAccountStats;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.PlayerFiability;
import com.fullmetalgalaxy.model.persist.PlayerStyle;
import com.fullmetalgalaxy.model.persist.StatsErosion;
import com.fullmetalgalaxy.model.persist.StatsGame;
import com.fullmetalgalaxy.model.persist.StatsGame.Status;
import com.fullmetalgalaxy.model.persist.StatsGamePlayer;

/**
 * Update account statistic according to some event
 *
 * erosion decrease player point according to time to allow new player becoming on top of ranking
 * cf erosion.xls for erosion test and formulas
 * 
 * @author Vincent
 *
 */
public class AccountStatsManager
{
  private final static FmpLogger log = FmpLogger.getLogger( AccountStatsManager.class.getName() );

  // coef of erosion polynom formulas
  private final static float EROSION_A = -1 * FmpConstant.SCORE_EROSION_REF
      * FmpConstant.SCORE_EROSION_REF
      / (4 * (FmpConstant.SCORE_EROSION_MIN - FmpConstant.SCORE_REF));
  private final static int EROSION_B = FmpConstant.SCORE_EROSION_REF;
  private final static int EROSION_C = FmpConstant.SCORE_REF;
  private final static float EROSION_TIME_MIN = -1 * EROSION_B / (2 * EROSION_A);

  private final static long MONTH_IN_MILLIS = 1000l * 60 * 60 * 24 * 30;


  /**
   * @param p_time unit is one month
   * @return point
   */
  private static float polynom(float p_time)
  {
    return EROSION_A * p_time * p_time + EROSION_B * p_time + EROSION_C;
  }

  /**
   * @param p_point
   * @return time in month
   */
  private static float polynom_inv(float p_point)
  {
    return (float)(-1
        * Math.sqrt( Math.abs( p_point / EROSION_A
            + (EROSION_B * EROSION_B - 4 * EROSION_A * EROSION_C) / (4 * EROSION_A * EROSION_A) ) ) - EROSION_B
        / (2 * EROSION_A));
  }

  /**
   * According to an amount of point and a duration, compute the new players points
   * @param p_point
   * @param p_durationInMillis
   * @return
   */
  private static int erosion(int p_point, long p_durationInMillis)
  {
    float months = p_durationInMillis / MONTH_IN_MILLIS;
    float time = polynom_inv( p_point );
    time += months;
    if( time >= EROSION_TIME_MIN )
    {
      return FmpConstant.SCORE_EROSION_MIN;
    }
    return ((int)polynom( time )) + 1;
  }



  /**
   *  may differ from oreCount + tokenCount as ore may have different value.
   * More important, this score depend of other players level !
   *
   * finalScore = (fmpScore - 14)*(sum(otherLevel)/(myLevel*otherPlayerCount))^sign(fmpScore) 
   *
   * for winner, we also add the sum of other players bonus
   * @param p_game
   * @param p_registration
   * @return
   */
  private static int processFinalScore(Game p_game, EbRegistration p_registration)
  {
    // process normal fmp score
    int fmpScore = p_registration.getWinningScore( p_game );

    // process level ratio and bonus
    float levelRatio = 0;
    int otherPlayerCount = 0;
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      if( registration != p_registration && registration.getAccount() != null )
      {
        levelRatio += registration.getAccount().getCurrentLevel();
        otherPlayerCount++;
      }
    }
    levelRatio /= p_registration.getAccount().getCurrentLevel() * otherPlayerCount;

    // process final score
    int finalScore = 0;
    if( fmpScore > 0 )
    {
      finalScore = (int)Math.round( fmpScore * levelRatio );
    }
    else if( fmpScore < 0 )
    {
      finalScore = (int)Math.round( fmpScore / levelRatio );
    }
    if( p_game.getWinnerRegistration() == p_registration )
    {
      finalScore += p_game.getScoreBonus() - p_registration.getAccount().getScoreBonus();
    }

    return finalScore;
  }

  private static StatsGame getLastStats(EbAccount p_account, long p_gameId)
  {
    if( p_account == null || p_account.getStats() == null || p_account.getStats().isEmpty() )
    {
      return null;
    }
    for( int i = p_account.getStats().size() - 1; i >= 0; i-- )
    {
      EbAccountStats stat = p_account.getStats().get( i );
      if( stat instanceof StatsGame )
      {
        if( ((StatsGame)stat).getGameId() == p_gameId )
        {
          return (StatsGame)stat;
        }
      }
    }
    return null;
  }


  private static StatsErosion getLastErosion(EbAccount p_account)
  {
    if( p_account.getStats() == null || p_account.getStats().isEmpty() )
    {
      return null;
    }
    int index = p_account.getStats().size();
    while( index > 0 )
    {
      index--;
      EbAccountStats stat = p_account.getStats().get( index );
      if( stat instanceof StatsErosion )
      {
        return (StatsErosion)stat;
      }
    }
    return null;
  }


  private static EbAccountStats getLastFixedStats(EbAccount p_account)
  {
    if( p_account.getStats() == null || p_account.getStats().isEmpty() )
    {
      return null;
    }
    int index = p_account.getStats().size();
    while( index > 0 )
    {
      index--;
      EbAccountStats stat = p_account.getStats().get( index );
      if( !stat.lastUpdateCanChange() )
      {
        return stat;
      }
    }
    return null;
  }


  @SuppressWarnings("unchecked")
  public static void UpdateStats(EbAccount p_account)
  {
    if( p_account == null )
    {
      return;
    }
    // sort stat according to their date
    Collections.sort( p_account.getStats() );
    // update player level and other style
    int level = 1;
    int banCount = 0;
    int sheepCount = 0;
    float style = 0;
    int styleCount = 0;
    int colors[] = new int[EnuColor.getTotalNumberOfColor()];
    int colorCount = 0;
    List<EbAccountStats> stat2Remove = new ArrayList<EbAccountStats>();
    for( EbAccountStats stat : p_account.getStats() )
    {
      level += stat.getFinalScore();
      if( stat instanceof StatsGame && ((StatsGame)stat).getStatus() == Status.Aborted
          && stat.getFinalScore() == 0
          && stat.getLastUpdate().getTime() < System.currentTimeMillis() - MONTH_IN_MILLIS )
      {
        // game was cancelled and older than one month: remove it
        stat2Remove.add( stat );
      }
      if( stat instanceof StatsGame && ((StatsGame)stat).getStatus() == Status.Running )
      {
        // game is running... due to somewhere bug, check that still the case.
        EbGamePreview gamePreview = FmgDataStore.dao().find( EbGamePreview.class,
            ((StatsGame)stat).getGameId() );
        if( gamePreview == null || gamePreview.isAborted() )
        {
          // well, game was deleted !
          ((StatsGame)stat).setStatus( Status.Aborted );
          log.error( "Game '" + ((StatsGame)stat).getGameName() + "("
              + ((StatsGame)stat).getGameId() + ")' was deleted but a stat was found for user "
              + p_account.getPseudo() );
        }
        else if( stat instanceof StatsGamePlayer && gamePreview.isHistory() )
        {
          // game is history, but this stat wasn't updated !
          StatsGamePlayer gameStat = ((StatsGamePlayer)stat);
          Game game = FmgDataStore.dao().getGame( gamePreview );
          EbRegistration registration = game.getRegistrationByIdAccount( p_account.getId() );
          if( registration == null )
          {
            // registration isn't found...
            // player have been banned.
            gameStat.setStatus( Status.Banned );
            log.error( "Game '" + gameStat.getGameName() + "(" + gameStat.getGameId()
                + ")' is history but user " + p_account.getPseudo()
                + " have a Running stat on it. as it wasn't found in game, he should be banned" );
          }
          else
          {
            // player finished game normally
            // for an unknown reason, this stat wasn't computed...
            gameStat.setPlayer( game, registration );
            gameStat.setStatus( Status.Finished );
            gameStat.setFmpScore( registration.getWinningScore( game ) );
            gameStat.setFinalScore( processFinalScore( game, registration ) );
            gameStat.setLastUpdate( new Date() );
            log.error( "Game '" + gameStat.getGameName() + "(" + gameStat.getGameId()
                + ")' is history but user " + p_account.getPseudo()
                + " have a Running stat on it. player finished game normally" );
          }
        }
        else if( gamePreview.isHistory() )
        {
          // account is simply game creator, but he didn't play
          ((StatsGame)stat).setStatus( Status.Finished );
          log.error( "Game '" + gamePreview.getName() + "(" + gamePreview.getId()
              + ")' is history but user " + p_account.getPseudo()
              + " have a Running stat on it. he was game creator" );
        }
      }
      if( stat instanceof StatsGamePlayer )
      {
        if( ((StatsGamePlayer)stat).getStatus() == Status.Banned )
        {
          banCount++;
        }
        else if( ((StatsGamePlayer)stat).getStatus() == Status.Finished )
        {
          PlayerStyle playerStyle = ((StatsGamePlayer)stat).getPlayerStyle();
          switch( playerStyle )
          {
          case Sheep:
            sheepCount++;
            break;
          case Pacific:
            style--;
            styleCount++;
            break;
          default:
          case Balanced:
            styleCount++;
            break;
          case Aggressive:
            style++;
            styleCount++;
            break;
          }
        }
        // look his color
        int colorIndex = new EnuColor(((StatsGamePlayer)stat).getInitialColor()).getColorIndex();
        if( colorIndex >= 0 && colorIndex < colors.length )
        {
          colors[colorIndex]++;
          colorCount++;
        }
      }
    }
    // little correction on level
    if( level <= 1 && (styleCount >= 1 || sheepCount >= 1) )
    {
      level = 2;
    }
    if( level <= 0 )
    {
      level = 1;
    }

    // remove too old and canceled games
    p_account.getStats().removeAll( stat2Remove );

    // set player color
    p_account.setMainColor( EnuColor.None );
    for(int colorIndex = 0; colorIndex < colors.length; colorIndex++ )
    {
      if( colors[colorIndex] > colorCount/2 )
      {
        p_account.setMainColor( EnuColor.getColorFromIndex( colorIndex ).getValue() );
      }
    }
    // set player level & fiability
    p_account.setCurrentLevel( level );
    if( banCount > styleCount + sheepCount )
    {
      p_account.setFiability( PlayerFiability.Banned );
    }
    // set player style
    if( p_account.getStats().size() == 0 )
    {
      p_account.setPlayerStyle( PlayerStyle.Mysterious );
    }
    else if( sheepCount > styleCount )
    {
      p_account.setPlayerStyle( PlayerStyle.Sheep );
    }
    else
    {
      style /= styleCount;
      if( style < -0.35 )
      {
        p_account.setPlayerStyle( PlayerStyle.Pacific );
      }
      else if( style > 0.35 )
      {
        p_account.setPlayerStyle( PlayerStyle.Aggressive );
      }
      else
      {
        p_account.setPlayerStyle( PlayerStyle.Balanced );
      }
    }
  }

  private static void saveAndUpdate(EbAccount p_account)
  {
    if( p_account == null )
    {
      return;
    }
    UpdateStats( p_account );
    // save account to datastore
    FmgDataStore ds = new FmgDataStore( false );
    ds.put( p_account );
    ds.close();
  }


  /**
   * warning: can't be called while game is transient
   * @param account
   * @param p_game
   */
  public static void gameCreate(long p_accountId, Game p_game)
  {
    EbAccount account = FmgDataStore.dao().find( EbAccount.class, p_accountId );
    StatsGame stat = getLastStats( account, p_game.getId() );
    if( stat == null )
    {
      // stat is likely to be null as game is just created
      stat = new StatsGame( p_game );
      stat.setCreator( true );
      account.getStats().add( stat );
    }
    stat.setLastUpdate( new Date() );
    
    saveAndUpdate( account );
  }

  public static void gameJoin(EbAccount p_account, Game p_game)
  {
    StatsGame lastStat = getLastStats( p_account, p_game.getId() );
    if( lastStat != null )
    {
      // player join a game he has created, remplace this stat
      p_account.getStats().remove( lastStat );
    }
    StatsGamePlayer newStat = new StatsGamePlayer( p_game );
    p_account.getStats().add( newStat );
    EbRegistration registration = p_game.getRegistrationByIdAccount( p_account.getId() );
    newStat.setPlayer( p_game, registration );
    if( lastStat != null )
    {
      newStat.setCreator( lastStat.isCreator() );
    }
    saveAndUpdate( p_account );
  }

  public static void gameBan(EbAccount p_account, Game p_game)
  {
    StatsGamePlayer lastStat = StatsGamePlayer.class.cast( getLastStats( p_account, p_game.getId() ) );
    if( lastStat == null )
    {
      lastStat = new StatsGamePlayer( p_game );
      p_account.getStats().add( lastStat );
    }
    
    lastStat.setStatus( Status.Banned );
    lastStat.setFinalScore( 0 - p_game.getEbConfigGameVariant().getInitialScore()
        - p_account.getScoreBonus() );
    lastStat.setLastUpdate( new Date() );
    
    // no this is useless as account is banned...
    // EbRegistration registration = p_game.getRegistrationByIdAccount( p_account.getId() );
    // lastStat.setPlayer( p_game, registration );

    saveAndUpdate( p_account );
  }

  protected static void gameFinish(Game p_game)
  {
    // for player
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      if( registration.getAccount() != null )
      {
        try {
          EbAccount account = FmgDataStore.dao().get( EbAccount.class,
              registration.getAccount().getId() );
          StatsGamePlayer lastStat = StatsGamePlayer.class.cast( getLastStats( account,
              p_game.getId() ) );
          if( lastStat == null && account != null )
          {
            lastStat = new StatsGamePlayer( p_game );
            account.getStats().add( lastStat );
          }
          if( lastStat != null )
          {
            lastStat.setPlayer( p_game, registration );
            lastStat.setStatus( Status.Finished );
            lastStat.setFmpScore( registration.getWinningScore( p_game ) );
            lastStat.setFinalScore( processFinalScore( p_game, registration ) );
            lastStat.setLastUpdate( new Date() );
            saveAndUpdate( account );

            // stat for finished games
            GlobalVars.incrementFGameConstructionCount( lastStat.getConstructionCount() );
            GlobalVars.incrementFGameFireCount( lastStat.getFireCount() );
            GlobalVars.incrementFGameFmpScore( lastStat.getFmpScore() );
            GlobalVars.incrementFGameFreighterControlCount( lastStat.getFreighterControlCount() );
            GlobalVars.incrementFGameOreCount( lastStat.getOreCount() );
            GlobalVars.incrementFGameTokenCount( lastStat.getTokenCount() );
            GlobalVars.incrementFGameUnitControlCount( lastStat.getUnitControlCount() );
          }
        } catch(Exception e)
        {
          ServerUtil.logger.warning( e.getMessage() );
        }
      }
    }

    // for creator
    EbAccount account = FmgDataStore.dao()
        .find( EbAccount.class, p_game.getAccountCreator().getId() );
    if( account != null )
    {
      StatsGame lastStat = StatsGame.class.cast( getLastStats( account, p_game.getId() ) );
      if( lastStat == null )
      {
        lastStat = new StatsGame( p_game );
        account.getStats().add( lastStat );
      }
      lastStat.setStatus( Status.Finished );
      lastStat.setLastUpdate( new Date() );
      saveAndUpdate( account );
    }

  }

  /**
   * I may want to use this to make a difference between a deleted and aborted game.
   * @param p_game
   */
  protected static void gameDelete(Game p_game)
  {
    // we may want to flag corresponding stat as deleted game in future...
    /*if( !p_game.isFinished() )
    {
      gameAbort( p_game );
    }*/
  }

  protected static void gameAbort(Game p_game)
  {
    // for player
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      if( registration.getAccount() != null )
      {
        try {
          EbAccount account = FmgDataStore.dao().get( EbAccount.class,
              registration.getAccount().getId() );
          StatsGamePlayer lastStat = StatsGamePlayer.class.cast( getLastStats( account,
              p_game.getId() ) );
          if( lastStat == null )
          {
            lastStat = new StatsGamePlayer( p_game );
            lastStat.setPlayer( p_game, registration );
            account.getStats().add( lastStat );
          }
          lastStat.setStatus( Status.Aborted );
          lastStat.setFinalScore( 0 );
          lastStat.setLastUpdate( new Date() );
          saveAndUpdate( account );
        } catch(Exception e)
        {
          ServerUtil.logger.warning( e.getMessage() );
        }
      }
    }

    // for creator
    EbAccount account = FmgDataStore.dao()
        .find( EbAccount.class, p_game.getAccountCreator().getId() );
    if( account != null )
    {
      StatsGame lastStat = StatsGame.class.cast( getLastStats( account, p_game.getId() ) );
      if( lastStat == null )
      {
        lastStat = new StatsGame( p_game );
        account.getStats().add( lastStat );
      }
      lastStat.setStatus( Status.Aborted );
      lastStat.setFinalScore( 0 );
      lastStat.setLastUpdate( new Date() );
      saveAndUpdate( account );
    }
  }

  public static void erosion(EbAccount p_account)
  {
    int oldPoint = p_account.getCurrentLevel();
    int newPoint = oldPoint;
    EbAccountStats lastFixedStat = getLastFixedStats( p_account );
    StatsErosion lastErosion = getLastErosion( p_account );

    if( lastErosion == null )
    {
      lastErosion = new StatsErosion();
      p_account.getStats().add( lastErosion );
    }
    else if( lastErosion == lastFixedStat )
    {
      // add erosion with existing previous erosion
      oldPoint = p_account.getCurrentLevel() - lastErosion.getFinalScore();
      newPoint = erosion( oldPoint, System.currentTimeMillis()
          - lastErosion.getFromDate().getTime() );
    }
    else
    {
      oldPoint = p_account.getCurrentLevel();
      newPoint = erosion( oldPoint, System.currentTimeMillis()
          - lastFixedStat.getLastUpdate().getTime() );
      lastErosion = new StatsErosion();
      p_account.getStats().add( lastErosion );
    }

    lastErosion.setLastUpdate( new Date() );
    lastErosion.setFinalScore( newPoint - oldPoint );

    saveAndUpdate( p_account );
  }


}
