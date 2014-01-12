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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.util.HashMap;
import java.util.Map.Entry;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.CompanyStatistics;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.Game;
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
    System.err.println( "RecomputeStats.start()" );
    QueueFactory.getDefaultQueue().add(
        TaskOptions.Builder.withPayload( new ResetAllStatsCommand() ) );
  }

  protected static class ResetAllStatsCommand extends LongDBTask<EbAccount>
  {
    private static final long serialVersionUID = 1L;
    private int m_accountCount = 0;

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
        m_accountCount++;
        account.clearComputedStats();
        ds.put( account );
        ds.close();
      }
    }

    @Override
    protected void finish()
    {
      GlobalVars.setFGameFmpScore( 0 );
      GlobalVars.setAccountCount( m_accountCount );

      // chain other command
      QueueFactory.getDefaultQueue().add(
          TaskOptions.Builder.withPayload( new ResetAllCompanyStatistics() ) );
    }

  }

  // delete all company stats
  protected static class ResetAllCompanyStatistics extends LongDBTask<CompanyStatistics>
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected Query<CompanyStatistics> getQuery()
    {
      return FmgDataStore.dao().query( CompanyStatistics.class );
    }

    @Override
    protected void processKey(Key<CompanyStatistics> p_key)
    {
      FmgDataStore ds = new FmgDataStore( false );
      ds.delete( p_key );
      ds.close();
    }

    @Override
    protected void finish()
    {
      // chain other command
      QueueFactory.getDefaultQueue().add(
          TaskOptions.Builder.withPayload( new UpdateAllGameStatsCommand() ) );
    }

  }


  /**
   * 
   * @author Vincent
   * update all player stats as well as global stat related to finished game.
   */
  public static class UpdateAllGameStatsCommand extends LongDBTask<EbGamePreview>
  {
    private static final long serialVersionUID = 1L;
    private int m_finishedGameCount = 0;

    private HashMap<ConfigGameTime, Integer> m_nbConfigGameTime = new HashMap<ConfigGameTime, Integer>();
    private long m_nbOfHexagon = 0;
    private int m_nbPlayer = 0;

    private int m_FGameInitiationCount = 0;

    public UpdateAllGameStatsCommand()
    {
      for( ConfigGameTime config : ConfigGameTime.values() )
      {
        m_nbConfigGameTime.put( config, 0 );
      }
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

    // compute true skill rating
    @Override
    protected void processKey(Key<EbGamePreview> p_key)
    {
      Game game = FmgDataStore.dao().getGame( p_key );
      if( game.getGameType() == GameType.Initiation )
      {
        m_FGameInitiationCount++;
      }
      else
      {
        m_finishedGameCount++;
        m_nbConfigGameTime.put( game.getConfigGameTime(),
            m_nbConfigGameTime.get( game.getConfigGameTime() ) + 1 );
        m_nbOfHexagon += game.getNumberOfHexagon();
        m_nbPlayer += game.getSetRegistration().size();
      }
      GameWorkflow.updateStat4FinishedGame( game, true );
    }

    @Override
    protected void finish()
    {
      log.warning( "entity processed: " + getEntityProcessed() );
      log.warning( "finished game processed: " + m_finishedGameCount );

      // save process stats into datastore
      for( Entry<ConfigGameTime, Integer> entry : m_nbConfigGameTime.entrySet() )
      {
        GlobalVars.setFGameNbConfigGameTime( entry.getKey(), entry.getValue() );
      }
      GlobalVars.setFGameNbOfHexagon( m_nbOfHexagon );
      GlobalVars.setFGameNbPlayer( m_nbPlayer );
      GlobalVars.setFGameInitiationCount( m_FGameInitiationCount );
    }

  }




}
