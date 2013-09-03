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

package com.fullmetalgalaxy.server.cron;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AccountStatistics;
import com.fullmetalgalaxy.model.persist.PlayerGameStatistics;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.GlobalVars;
import com.fullmetalgalaxy.server.LongDBTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 *
 * cron task used to update account statistic
 */
public class AccountUpdate extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  protected class AccountUpdateCommand extends LongDBTask<EbAccount>
  {
    private static final long serialVersionUID = 1L;
    private int m_playerCount = 0;
    private Date m_gameOldestDate = null;

    public AccountUpdateCommand()
    {
      m_gameOldestDate = new Date( System.currentTimeMillis()
          - FmpConstant.currentStatsTimeWindowInMillis );
    }

    @Override
    protected Query<EbAccount> getQuery()
    {
      return FmgDataStore.dao().query( EbAccount.class )
          .filter( "m_currentStats.m_finshedGameCount !=", 0 );
    }

    @Override
    protected void processKey(Key<EbAccount> p_key)
    {
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.get( p_key );
      if( m_gameOldestDate.after( account.getCurrentStats().getFirstGameDate() ) )
      {
        Query<PlayerGameStatistics> query = FmgDataStore.dao().query( PlayerGameStatistics.class )
            .filter( "m_account.id", p_key.getId() ).filter( "m_gameEndDate >=", m_gameOldestDate );
        AccountStatistics stats = new AccountStatistics( query );
        account.setCurrentStats( stats );
        ds.put( account );
      }
      ds.close();
      
      if( account.getCurrentStats().isIncludedInRanking() )
        m_playerCount++;
    }

    @Override
    protected void finish()
    {
      GlobalVars.setActiveAccount( m_playerCount );
    }

  }


  /**
   * this method is called once every week because of "accountupdate" cron.
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    QueueFactory.getDefaultQueue().add(
        TaskOptions.Builder.withPayload( new AccountUpdateCommand() ).header(
            "X-AppEngine-FailFast", "true" ) );
  }
}
