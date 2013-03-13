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

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 * this class is an helper to create long task across datastore
 * 
 */
public abstract class LongDBTask<T> implements DeferredTask
{
  private static final long serialVersionUID = 1L;
  public static final long LIMIT_MILLIS = 1000 * 20; // provide a little
                                                     // leeway
  private Cursor m_cursor = null;

  @Override
  public void run()
  {
    Query<T> query = getQuery();
    if( query == null )
    {
      return;
    }
    long startTime = System.currentTimeMillis();
    if( m_cursor != null )
    {
      query.startCursor( m_cursor );
    }
    QueryResultIterator<Key<T>> iterator = query.fetchKeys().iterator();
    while( iterator.hasNext() )
    {
      processKey( iterator.next() );

      if( System.currentTimeMillis() - startTime > LIMIT_MILLIS )
      {
        // synchro isn't finished: add task
        m_cursor = iterator.getCursor();
        QueueFactory.getQueue( "longDBTask" ).add(
            TaskOptions.Builder.withPayload( this ).header( "X-AppEngine-FailFast", "true" ) );
        return;
      }
    }
    m_cursor = null;

    finish();
  }

  protected abstract Query<T> getQuery();

  protected abstract void processKey(Key<T> p_key);

  protected abstract void finish();



}
