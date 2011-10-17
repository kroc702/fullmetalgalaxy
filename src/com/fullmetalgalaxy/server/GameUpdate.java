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

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.server.forum.SynchroForum;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author vlegendr
 *
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
     * This method is called as many time needed for one forum synchronization session.
     */
    @Override
    public void run()
    {
      long startTime = System.currentTimeMillis();
      Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class );
      query.filter( "m_history", false );
      query.filter( "m_gameType", GameType.MultiPlayer );
      if( m_cursor != null )
      {
        query.startCursor( m_cursor );
      }

      QueryResultIterator<Key<EbGamePreview>> iterator = query.fetchKeys().iterator();
      while( iterator.hasNext() )
      {
        Game game = FmgDataStore.dao().getGame( iterator.next() );
        ModelFmpUpdate modelUpdate = new ModelFmpUpdate( game );
        ArrayList<AnEvent> eventAdded = new ArrayList<AnEvent>();
        try
        {
          eventAdded = GameWorkflow.checkUpdate( game );
        } catch( RpcFmpException e )
        {
          log.error( e );
        }

        if( !eventAdded.isEmpty() || game.isHistory() )
        {
          // something changed in this game: save it
          FmgDataStore ds = new FmgDataStore( false );

          // do we need to send an email ?
          modelUpdate.getGameEvents().addAll( eventAdded );
          modelUpdate.setToVersion( game.getVersion() );
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
              TaskOptions.Builder.withPayload( new GameUpdateCommand( cursor ) ) );
          break;
        }
      }


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
    QueueFactory.getDefaultQueue()
      .add( TaskOptions.Builder.withPayload( new GameUpdateCommand( null ) ) );
  }

  
}
