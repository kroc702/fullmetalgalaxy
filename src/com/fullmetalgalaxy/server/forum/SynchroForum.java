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
import com.fullmetalgalaxy.server.ServerUtil;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
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
    public static final long LIMIT_MILLIS = 1000 * 15; // provide a little
                                                       // leeway

    Cursor m_cursor = null;

    public SynchroForumCommand(Cursor p_cursor)
    {
      m_cursor = p_cursor;
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
          if( account.getForumId() == null )//&& account.isIsforumIdConfirmed() )
          {
            // no Forum account found: search one
            //
            account.setForumId( ServerUtil.forumConnector().getUserId( account.getPseudo() ) );
          }
          
          /*if( account.getForumId() != null && !account.isIsforumIdConfirmed() )
          {
            // A Forum account is found, but we're not sure it belong to the same people
            //
            if( account.getForumKey() == null )
            {
              // we never send PM to link both account: let's do it
              //
              account.setForumKey( ServerUtil.randomString( 10 ) );
              ServerUtil.forumConnector().sendPMessage( "[FMG] lier les deux comptes", 
                  "Bonjour\n" +
                  "Pour lier les deux comptes '"+account.getPseudo()+"' entre FMG et le Forum veuillez visiter cette URL:\n" +
                  "http://www.fullmetalgalaxy.com/AccountServlet?link="+account.getForumKey()+" \n" +
                  "\n" +
                  "Cordialement\n" +
                  "Full Metal Galaxy", 
                  account.getPseudo() );
            }
          }
          else*/ if( account.getForumId() != null && account.isIsforumIdConfirmed() )
          {
            // FMG and Forum account belong to the same people
            //
            // copy data from forum to FMG
            ServerUtil.forumConnector().pullAccount( account );
            // copy data from FMG to forum
            ServerUtil.forumConnector().pushAccount( account );
          }
          
          // some account update for legacy
          if( account.getCompactPseudo() == null || account.getCompactPseudo().isEmpty() )
          {
            account.setPseudo( account.getPseudo() );
          }
          
          // TODO we can optimize by doing this datastore put only if required
          ds.put( account );
        }
        ds.close();

        if( System.currentTimeMillis() - startTime > LIMIT_MILLIS )
        {
          Cursor cursor = iterator.getCursor();
          // synchro isn't finished: add task
          QueueFactory.getDefaultQueue().add(
              TaskOptions.Builder.withPayload( new SynchroForumCommand( cursor ) ) );
          break;
        }
      }


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
      .add( TaskOptions.Builder.withPayload( new SynchroForumCommand( null ) ) );
    addTask();
  }

  

  /**
   * add a task to remove too old presence in a near future.
   * This task stay alive by itself.
   */
  public static void addTask()
  {
    Queue queue = QueueFactory.getQueue( "synchroforum" );
    queue.add();
  }



}
