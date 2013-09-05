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
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.FmpLogger;
import com.fullmetalgalaxy.server.GameNotification;
import com.fullmetalgalaxy.server.GameWorkflow;
import com.fullmetalgalaxy.server.LongDBTask;
import com.fullmetalgalaxy.server.forum.SynchroForum;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author Vincent Legendre
 *
 * This serlvet is a cron task that check possible update on games.
 * it also delete cancelled games
 */
public class GameUpdate extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private final static FmpLogger log = FmpLogger.getLogger( SynchroForum.class.getName() );

  protected class GameUpdateCommand implements DeferredTask
  {
    private static final long serialVersionUID = 1L;
    public static final long LIMIT_MILLIS = 1000 * 20; // provide a little
                                                       // leeway

    Cursor m_cursor = null;

    public GameUpdateCommand(Cursor p_cursor)
    {
      m_cursor = p_cursor;
    }

    /**
     * This method is called as many time needed for one synchronization session.
     */
    @Override
    public void run()
    {
      long startTime = System.currentTimeMillis();
      Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class );

      query.filter( "m_history", false );
      query.filter( "m_status", GameStatus.Running );
      if( m_cursor != null )
      {
        query.startCursor( m_cursor );
      }

      QueryResultIterator<Key<EbGamePreview>> iterator = query.fetchKeys().iterator();
      while( iterator.hasNext() )
      {
        FmgDataStore ds = new FmgDataStore( false );
        Game game = ds.getGame( iterator.next() );
        ModelFmpUpdate modelUpdate = new ModelFmpUpdate( game );
        ArrayList<AnEvent> eventAdded = new ArrayList<AnEvent>();
        try
        {
          eventAdded = GameWorkflow.checkUpdate2Unblock( game );
          eventAdded.addAll( GameWorkflow.checkUpdate( game ) );
        } catch( RpcFmpException e )
        {
          log.error( e );
        }

        if( !eventAdded.isEmpty() || game.getStatus() == GameStatus.History )
        {
          // something changed in this game: save it

          // do we need to send an email ?
          modelUpdate.getGameEvents().addAll( eventAdded );
          GameNotification.sendMail( game, modelUpdate );

          // and save game
          ds.put( game );
          ds.close();
        }

        if( System.currentTimeMillis() - startTime > LIMIT_MILLIS )
        {
          Cursor cursor = iterator.getCursor();
          // synchro isn't finished: add task
          QueueFactory.getDefaultQueue().add(
              TaskOptions.Builder.withPayload( new GameUpdateCommand( cursor ) ).header(
                  "X-AppEngine-FailFast", "true" ) );
          break;
        }
      }

      // after job is finished, delete too old aborted game
      if( !iterator.hasNext() )
      {
        QueueFactory.getDefaultQueue().add(
            TaskOptions.Builder.withPayload( new DeleteOldAbortedGameCommand() ).header(
                "X-AppEngine-FailFast", "true" ) );

      }
    }
  }


  public static class DeleteOldAbortedGameCommand extends LongDBTask<EbGamePreview>
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected Query<EbGamePreview> getQuery()
    {
      Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class );
      query.filter( "m_status", GameStatus.Aborted );
      // one week older
      Date lastWeek = new Date( System.currentTimeMillis() - (1000l * 60 * 60 * 24 * 7) );
      query.filter( "m_lastUpdate <", lastWeek );
      return query;
    }

    @Override
    protected void processKey(Key<EbGamePreview> p_key)
    {
      FmgDataStore dataStore = new FmgDataStore( false );
      dataStore.delete( Game.class, p_key );
      dataStore.close();
    }

    @Override
    protected void finish()
    {
      // noting to do
    }
  }
  
  /**
   * this method is called once every day because of "gameupdate" cron.
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    QueueFactory.getDefaultQueue().add(
        TaskOptions.Builder.withPayload( new GameUpdateCommand( null ) ).header(
            "X-AppEngine-FailFast", "true" ) );
  }

  
}
