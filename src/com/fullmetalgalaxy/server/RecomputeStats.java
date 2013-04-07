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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.util.HashMap;
import java.util.Map.Entry;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.StatsPlayer;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 * contain several command that are used by admin servlet to recompute statistics.
 */
public class RecomputeStats
{
  public static void start()
  {
    QueueFactory.getDefaultQueue().add(
        TaskOptions.Builder.withPayload( new ResetAllStatsCommand() ) );
  }

  protected static class ResetAllStatsCommand extends LongDBTask<EbAccount>
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected Query<EbAccount> getQuery()
    {
      return FmgDataStore.dao().query( EbAccount.class );
    }

    @Override
    protected void processKey(Key<EbAccount> p_key)
    {
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.get( p_key );
      if( account != null )
      {
        account.clearComputedStats();
        ds.put( account );
        ds.close();
      }
    }

    @Override
    protected void finish()
    {
      // chain other command
      QueueFactory.getDefaultQueue().add(
          TaskOptions.Builder.withPayload( new UpdateAllGameStatsCommand() ) );
    }

  }


  public static class UpdateAllGameStatsCommand extends LongDBTask<EbGamePreview>
  {
    private static final long serialVersionUID = 1L;

    public UpdateAllGameStatsCommand()
    {
      GlobalVars.resetStyleRatioRepartition();
    }

    // for each finished game...
    @Override
    protected Query<EbGamePreview> getQuery()
    {
      // TODO update to m_status variable
      Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class );
      query.filter( "m_history", true ).order( "m_lastUpdate" );
      return query;
    }

    // compute true skill ratting
    @Override
    protected void processKey(Key<EbGamePreview> p_key)
    {
      Game game = FmgDataStore.dao().getGame( p_key );
      // game is in history, but it may have been canceled
      if( game.isFinished() )
      {
        GameWorkflow.updateAccountStat4FinishedGame( game );
      }
    }

    @Override
    protected void finish()
    {
      // chain other command
      QueueFactory.getDefaultQueue()
          .add( TaskOptions.Builder.withPayload( new StatFGameCommand() ) );
    }

  }




  /**
   * 
   * @author Vincent
   * update all global stat related to finished game.
   * then launch StatFGameAccountCommand
   */
  public static class StatFGameCommand extends LongDBTask<EbGamePreview>
  {
    private static final long serialVersionUID = 1L;
    private HashMap<ConfigGameTime, Integer> m_nbConfigGameTime = new HashMap<ConfigGameTime, Integer>();
    private long m_nbOfHexagon = 0;
    private int m_nbPlayer = 0;

    private int m_FGameInitiationCount = 0;

    private int m_ConstructionCount = 0;
    private int m_FireCount = 0;
    private int m_FmpScore = 0;
    private int m_FreighterControlCount = 0;
    private int m_OreCount = 0;
    private int m_TokenCount = 0;
    private int m_UnitControlCount = 0;


    public StatFGameCommand()
    {
      for( ConfigGameTime config : ConfigGameTime.values() )
      {
        m_nbConfigGameTime.put( config, 0 );
      }
    }

    @Override
    protected Query<EbGamePreview> getQuery()
    {
      Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class );
      query.filter( "m_history", true );
      return query;
    }

    @Override
    protected void processKey(Key<EbGamePreview> p_key)
    {
      Game game = FmgDataStore.dao().getGame( p_key );
      // game is in history, but it may have been canceled
      if( game.isFinished() )
      {
        if( game.getGameType() == GameType.Initiation )
        {
          m_FGameInitiationCount++;
        }
        else
        {
          m_nbConfigGameTime.put( game.getConfigGameTime(),
              m_nbConfigGameTime.get( game.getConfigGameTime() ) + 1 );
          m_nbOfHexagon += game.getNumberOfHexagon();
          m_nbPlayer += game.getSetRegistration().size();

          for( EbRegistration registration : game.getSetRegistration() )
          {
            StatsPlayer stats = registration.getStats();
            if( stats != null )
            {
              m_ConstructionCount += stats.getConstructionCount();
              m_FireCount += stats.getFireCount();
              m_FmpScore += stats.getFinalScore();
              m_FreighterControlCount += stats.getFreighterControlCount();
              m_OreCount += stats.getOreCount();
              m_TokenCount += stats.getTokenCount();
              m_UnitControlCount += stats.getUnitControlCount();
            }
          }
        }
      }
    }

    @Override
    protected void finish()
    {
      // save process stats into datastore
      for( Entry<ConfigGameTime, Integer> entry : m_nbConfigGameTime.entrySet() )
      {
        GlobalVars.setFGameNbConfigGameTime( entry.getKey(), entry.getValue() );
      }
      GlobalVars.setFGameNbOfHexagon( m_nbOfHexagon );
      GlobalVars.setFGameNbPlayer( m_nbPlayer );
      GlobalVars.setFGameInitiationCount( m_FGameInitiationCount );

      // save process stats into datastore
      GlobalVars.setFGameConstructionCount( m_ConstructionCount );
      GlobalVars.setFGameFireCount( m_FireCount );
      GlobalVars.setFGameFmpScore( m_FmpScore );
      GlobalVars.setFGameFreighterControlCount( m_FreighterControlCount );
      GlobalVars.setFGameOreCount( m_OreCount );
      GlobalVars.setFGameTokenCount( m_TokenCount );
      GlobalVars.setFGameUnitControlCount( m_UnitControlCount );
    }
  }



}
