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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.server.datastore;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author Vincent
 *
 */
public class DataStore
{

  static
  {
    ObjectifyService.register( PersistGame.class );
    ObjectifyService.register( PersistAccount.class );
    
    ObjectifyService.setDatastoreTimeoutRetryCount( 3 );
  }


  static private <T> PersistEntity getPersistEntity(Objectify p_ofy,
      Class<? extends T> p_persistEntityClass,
      long p_id)
  {
    assert p_ofy != null;
    if( p_id == 0 )
    {
      return null;
    }
    PersistEntity entity = null;
    // find persist entity
    try
    {
      T t = p_ofy.get( p_persistEntityClass, p_id );
      entity = PersistEntity.class.cast( t );
    } catch( EntityNotFoundException e )
    {
    }
    return entity;
  }

  static private long savePersistEntity(Objectify p_ofy, PersistEntity p_entity)
  {
    assert p_ofy != null;
    // find persist entity
    p_ofy.put( p_entity );
    return p_entity.getId();
  }

  static private <T> void deletePersistEntity(Objectify p_ofy,
      Class<? extends T> p_persistEntityClass, long p_id)
  {
    assert p_ofy != null;
    assert p_id != 0;
    // find persist entity
    p_ofy.delete( p_persistEntityClass, p_id );
  }


  static protected <T> com.googlecode.objectify.Query<T> getList(
      Class<? extends T> p_persistEntityClass, java.lang.String p_condition,
      java.lang.Object p_value)
  {
    assert p_persistEntityClass != null;

    com.googlecode.objectify.Query<T> query = (com.googlecode.objectify.Query<T>)ObjectifyService.begin().query(p_persistEntityClass);
    if(p_condition != null && p_value != null)
    {
      query = query.filter(p_condition, p_value);
    }
    
    return query;
  }

  static protected PersistEntity getEntity(Class<?> p_persistEntityClass, long p_id)
  {
    return getPersistEntity( ObjectifyService.begin(), p_persistEntityClass, p_id );
  }


  private Objectify m_ofy = null;
  private boolean m_isOpen = true;

  
  protected DataStore()
  {
  }

  /**
   * start a transaction
   * @return
   */
  private Objectify getThisObjectify()
  {
    if( m_ofy == null )
    {
      m_ofy = ObjectifyService.beginTransaction();
    }
    return m_ofy;
  }

  /**
   * for debug: insure the close() or rollback() was called.
   */
  @Override
  protected void finalize() throws Throwable
  {
    super.finalize();
    if( m_isOpen )
    {
      rollback();
      throw new Exception( "A FmgDataStore wasn't close properly" );
    }
  }



  protected PersistEntity getPersistEntity(Class<?> p_persistEntityClass,
      long p_id)
  {
    assert m_isOpen == true;
    if( p_id == 0 )
    {
      return null;
    }
    return getPersistEntity( getThisObjectify(), p_persistEntityClass, p_id );
  }



  /**
  * save an EbBase into database. p_obj will be really saved after a properly close of this datastore.
  * @param p_obj can be transient.
  */
  protected void save(Class<?> p_persistEntityClass, EbBase p_obj)
  {
    assert p_obj != null;
    assert m_isOpen == true;
    assert p_persistEntityClass != null;

    PersistEntity entity = null;
    if( p_obj.isTrancient() )
    {
      try
      {
        entity = (PersistEntity)p_persistEntityClass.newInstance();
      } catch( Exception e )
      {
        e.printStackTrace();
        return;
      }
    }
    else
    {
      // TODO we can save datastore cpu use by caching PersistEntity
      // like it was done with JPA
      entity = getPersistEntity( getThisObjectify(), p_persistEntityClass, p_obj.getId() );
    }

    entity.setEb( p_obj );
    savePersistEntity( getThisObjectify(), entity );

    // TODO we may want to patch PersistEntity here
    p_obj.setVersion( entity.getVersion() );
    p_obj.setId( entity.getId() );
  }




  protected void delete(Class<?> p_persistEntityClass, long p_id)
  {
    assert p_id != 0;
    assert m_isOpen == true;
    deletePersistEntity( getThisObjectify(), p_persistEntityClass, p_id );
  }



  /** close tt les em & commit des trans */
  public void close()
  {
    assert m_isOpen == true;
    if( m_ofy != null )
    {
      m_ofy.getTxn().commit();
    }
    m_isOpen = false;
  }

  /** rollback de tt les em/trans ouverte */
  public void rollback()
  {
    assert m_isOpen == true;
    if( m_ofy != null )
    {
      m_ofy.getTxn().rollback();
    }
    m_isOpen = false;
  }


}
