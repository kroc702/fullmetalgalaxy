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
package com.fullmetalgalaxy.server.forum;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.FmgMessage;
import com.fullmetalgalaxy.server.GlobalVars;
import com.fullmetalgalaxy.server.ServerUtil;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * 
 * @author Vincent
 *
 */
public class SynchroForum extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  protected class SynchroForumCommand implements DeferredTask
  {
    private static final long serialVersionUID = 1L;
    public static final long LIMIT_MILLIS = 1000 * 20; // provide a little
                                                       // leeway

    Cursor m_cursor = null;
    int m_accountProcessed = 0;
    int m_activeAccount = 0;
    int m_maxLevel = 0;

    public SynchroForumCommand(Cursor p_cursor, int p_accountProcessed, int p_activeAccount,
        int p_maxLevel)
    {
      m_cursor = p_cursor;
      m_accountProcessed = p_accountProcessed;
      m_activeAccount = p_activeAccount;
      m_maxLevel = p_maxLevel;
    }

    /**
     * This method is called as many time needed for one forum synchronization session.
     */
    @Override
    public void run()
    {
      long startTime = System.currentTimeMillis();
      Query<EbAccount> query = FmgDataStore.dao().query( EbAccount.class );
      if( m_cursor != null )
      {
        query.startCursor( m_cursor );
      }

      QueryResultIterator<Key<EbAccount>> iterator = query.fetchKeys().iterator();
      while( iterator.hasNext() )
      {
        FmgDataStore ds = new FmgDataStore( false );
        EbAccount account = ds.find( iterator.next() );
        if( account != null )
        {
          ConectorImpl.logger.fine( "start synchro for " + account.getPseudo() );

          if( account.getForumId() == null )//&& account.isIsforumIdConfirmed() )
          {
            // no Forum account found: search one
            //
            account.setForumId( ServerUtil.forumConnector().getUserId( account.getPseudo() ) );
          }
          
          if( account.getForumId() != null && !account.isIsforumIdConfirmed() && 
              account.isActive() )
          {
            // A Forum account is found, but we're not sure it belong to the same people
            //
            if( account.getForumKey() == null )
            {
              // we never send PM to link both account: let's do it
              //
              account.setForumKey( ServerUtil.randomString( 10 ) );
              if( new FmgMessage( "linkAccount" ).sendPM( account ) == false )
              {
                account.setForumKey( null );
              }
            }
          }
          else if( account.getForumId() != null && account.isIsforumIdConfirmed() )
          {
            // FMG and Forum account belong to the same people
            //
            // copy data from forum to FMG
            boolean succeed = ServerUtil.forumConnector().pullAccount( account );
            if( succeed )
            {
              // copy data from FMG to forum
              succeed = ServerUtil.forumConnector().pushAccount( account );
            }
            if( !succeed )
            {
              ConectorImpl.logger.warning( "synchro for " + account.getPseudo() + " failed" );
            }
          }
          else
          {
            // well, forumId is confirmed but no forumId was found...
            // Forum account was probably created by FMG, but it's still not activated
            // OR account isn't active
          }
          
          // TODO add erosion here
          // errosion should start only if one player reach SCORE_REF

          
          // compute some global stats
          if( account.isActive() )
          {
            m_activeAccount++;
          }
          if( account.getCurrentLevel() > m_maxLevel )
          {
            m_maxLevel = account.getCurrentLevel();
          }
          m_accountProcessed++;

          // TODO we can optimize by doing this datastore put only if required
          ds.put( account );
          ds.commit();
        }
        ds.close();

        if( System.currentTimeMillis() - startTime > LIMIT_MILLIS )
        {
          Cursor cursor = iterator.getCursor();
          // synchro isn't finished: add task
          QueueFactory.getDefaultQueue().add(
              TaskOptions.Builder.withPayload( new SynchroForumCommand( cursor, m_accountProcessed,
                  m_activeAccount, m_maxLevel ) ) );
          break;
        }
      }

      // all account are processed
      // update global statistics
      GlobalVars.setAccountCount( m_accountProcessed );
      GlobalVars.setActiveAccount( m_activeAccount );
      GlobalVars.setMaxLevel( m_maxLevel );
    }
  }

  /**
   * this method is called once every day because of "synchroforum" queue.
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    QueueFactory.getDefaultQueue()
.add(
        TaskOptions.Builder.withPayload( new SynchroForumCommand( null, 0, 0, 0 ) ) );
  }

  




}
