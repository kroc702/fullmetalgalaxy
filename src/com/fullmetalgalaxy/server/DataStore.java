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

import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.AsyncObjectify;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

/**
 * This DAO can be instantiated as read only (ie non transactional)
 * or read/write (ie transactional).
 * 
 * It remove ability to write entity (ie put()) for non transactional DAO.
 * 
 * Note that, if open in read only mode, all transaction interface won't
 * do anything.
 * 
 * To tune datastore behavior, you should only override methods:
 * "get(Class,key)", "delete(Object)", "delete(Class,key)", "put(Object)"
 * 
 * @author vlegendr
 *
 */
public class DataStore implements Objectify, Transaction
{
  public final static Logger logger = Logger.getLogger( DataStore.class.getName() );

  private boolean m_isReadOnly = true;
  private DAOBase m_dao = null;

  public DataStore(boolean p_isReadOnly)
  {
    m_dao = new DAOBase( !p_isReadOnly );
    m_isReadOnly = p_isReadOnly;
  }



  public boolean isReadOnly()
  {
    return m_isReadOnly;
  }

  /**
   * close properly this datastore connexion.
   * If transaction is active (ie read/write mode), update are committed.
   */
  public void close()
  {
    if( isActive() )
    {
      commit();
    }
  }

  protected void delete(Object p_object)
  {
    m_dao.ofy().delete( p_object );
  }

  // ===================
  // Objectify interface
  // ===================

  @Override
  public AsyncObjectify async()
  {
    logger.severe( new Exception( "unimplemented" ).getMessage() );
    return null;
  }



  @Override
  public void delete(Object... p_arg0)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
    }
    else
    {
      for( Object object : p_arg0 )
      {
        delete( object );
      }
    }
  }


  /**
   * This method don't use objectify method to allow subclass
   * to override easily delete method.
   * This may lead to performance degradation.
   */
  @Override
  public void delete(Iterable<?> p_arg0)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
    }
    else
    {
      for( Object object : p_arg0 )
      {
        delete( object );
      }
    }
  }



  @Override
  public <T> void delete(Class<T> p_arg0, long p_arg1)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
    }
    else
    {
      m_dao.ofy().delete( p_arg0, p_arg1 );
    }
  }



  @Override
  public <T> void delete(Class<T> p_arg0, String p_arg1)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
    }
    else
    {
      m_dao.ofy().delete( p_arg0, p_arg1 );
    }
  }



  /**
   * We don't use Objectify find method to let DataStore subclass
   * override get method
   */
  @Override
  public <T> T find(Key<? extends T> p_arg0)
  {
    try
    {
      return get( p_arg0 );
    } catch( NotFoundException e )
    {
      logger.fine( e.getMessage() );
    }
    return null;
  }



  /**
   * We don't use Objectify find method to let DataStore subclass
   * override get method
   */
  @Override
  public <T> T find(Class<? extends T> p_arg0, long p_arg1)
  {
    try
    {
      return get( p_arg0, p_arg1 );
    } catch( Exception e )
    {
      logger.fine( e.getMessage() );
    }
    return null;
  }



  /**
   * We don't use Objectify find method to let DataStore subclass
   * override get method
   */
  @Override
  public <T> T find(Class<? extends T> p_arg0, String p_arg1)
  {
    try
    {
      return get( p_arg0, p_arg1 );
    } catch( NotFoundException e )
    {
      logger.fine( e.getMessage() );
    }
    return null;
  }



  @Override
  public <T> Map<Key<T>, T> get(Iterable<? extends Key<? extends T>> p_arg0)
  {
    return m_dao.ofy().get( p_arg0 );
  }



  @Override
  public <T> T get(Key<? extends T> p_arg0) throws NotFoundException
  {
    return m_dao.ofy().get( p_arg0 );
  }



  @Override
  public <T> T get(Class<? extends T> p_arg0, long p_arg1) throws NotFoundException
  {
    return m_dao.ofy().get( p_arg0, p_arg1 );
  }



  @Override
  public <T> T get(Class<? extends T> p_arg0, String p_arg1) throws NotFoundException
  {
    return m_dao.ofy().get( p_arg0, p_arg1 );
  }



  @Override
  public <S, T> Map<S, T> get(Class<? extends T> p_arg0, Iterable<S> p_arg1)
  {
    return m_dao.ofy().get( p_arg0, p_arg1 );
  }



  @Override
  public <S, T> Map<S, T> get(Class<? extends T> p_arg0, S... p_arg1)
  {
    return m_dao.ofy().get( p_arg0, p_arg1 );
  }



  @Override
  public DatastoreService getDatastore()
  {
    return m_dao.ofy().getDatastore();
  }



  @Override
  public ObjectifyFactory getFactory()
  {
    return m_dao.ofy().getFactory();
  }



  @Override
  public Transaction getTxn()
  {
    return m_dao.ofy().getTxn();
  }



  @Override
  public <T> Key<T> put(T p_arg0)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
    else
    {
      return m_dao.ofy().put( p_arg0 );
    }
  }



  @Override
  public <T> Map<Key<T>, T> put(Iterable<? extends T> p_arg0)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
    else
    {
      return m_dao.ofy().put( p_arg0 );
    }
  }



  @Override
  public <T> Map<Key<T>, T> put(T... p_arg0)
  {
    if( isReadOnly() )
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
    else
    {
      return m_dao.ofy().put( p_arg0 );
    }
  }



  @Override
  public <T> Query<T> query()
  {
    return m_dao.ofy().query();
  }



  @Override
  public <T> Query<T> query(Class<T> p_arg0)
  {
    return m_dao.ofy().query( p_arg0 );
  }


  // =====================
  // Transaction interface
  // =====================

  @Override
  public void commit()
  {
    if( getTxn() != null )
    {
      getTxn().commit();
    }
  }



  @Override
  public Future<Void> commitAsync()
  {
    if( getTxn() != null )
    {
      return getTxn().commitAsync();
    }
    else
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
  }



  @Override
  public String getApp()
  {
    if( getTxn() != null )
    {
      return getTxn().getApp();
    }
    else
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
  }



  @Override
  public String getId()
  {
    if( getTxn() != null )
    {
      return getTxn().getId();
    }
    else
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
  }



  @Override
  public boolean isActive()
  {
    if( getTxn() != null )
    {
      return getTxn().isActive();
    }
    return false;
  }



  @Override
  public void rollback()
  {
    if( getTxn() != null )
    {
      getTxn().rollback();
    }
    else
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
    }
  }



  @Override
  public Future<Void> rollbackAsync()
  {
    if( getTxn() != null )
    {
      return getTxn().rollbackAsync();
    }
    else
    {
      logger.severe( new Exception( "not available in read only" ).getMessage() );
      return null;
    }
  }


}
