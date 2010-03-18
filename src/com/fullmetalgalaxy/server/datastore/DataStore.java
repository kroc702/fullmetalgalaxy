/**
 * 
 */
package com.fullmetalgalaxy.server.datastore;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Vincent
 *
 */
public class DataStore
{

  private static EntityManagerFactory s_emf = null;
  private static EntityManager s_em = null;
  private static Boolean s_createSemaphore = false;

  /**
   * Lazily acquire the EntityManagerFactory and cache it.
   */
  private static EntityManagerFactory getEMF()
  {
    if( s_emf == null )
    {
      synchronized( s_createSemaphore )
      {
        if( s_emf == null )
        {
          s_emf = Persistence.createEntityManagerFactory( "transactions-optional" );
        }
      }
    }
    return s_emf;
  }

  /**
   * @return a static entity manager used for read only
   */
  protected static EntityManager getEntityManager()
  {
    if( s_em == null )
    {
      s_em = getEMF().createEntityManager();
    }
    return s_em;
  }

  public static PersistEntity getEntity(Class<?> p_class, long p_id)
  {
    return getEntity( s_em, p_class, p_id );
  }

  private static PersistEntity getEntity(EntityManager p_em, Class<?> p_class, long p_id)
  {
    PersistEntity obj = null;
    try
    {
      obj = (PersistEntity)p_em.getReference( p_class, KeyFactory.createKey( p_class
          .getSimpleName(), p_id ) );
    } catch( Exception e )
    {
      return null;
    }
    return obj;
  }


  private boolean m_isOpen = true;
  private List<DataInstance> m_dataInstances = new ArrayList<DataInstance>();

  protected DataStore()
  {
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

  /** load a dataInstance either from cache or database */
  private DataInstance loadDataInstance(Class<?> p_persistEntityClass, long p_id)
  {
    // find persist entity
    PersistEntity entity = null;
    EntityManager em = null;
    DataInstance dataInstance = findDataInstance( p_persistEntityClass, p_id );
    if( dataInstance != null )
    {
      entity = dataInstance.entity;
    }
    else
    {
      em = getEMF().createEntityManager();
      entity = getEntity( em, p_persistEntityClass, p_id );
    }
    if( entity == null )
    {
      return null;
    }
    assert entity.getData() != null;
    // put persist entity in cache
    if( dataInstance == null )
    {
      dataInstance = new DataInstance( p_id, entity, em );
      m_dataInstances.add( dataInstance );
    }
    return dataInstance;
  }


  protected PersistEntity getPersistEntity(Class<?> p_persistEntityClass,
      long p_id)
  {
    assert m_isOpen == true;
    if( p_id == 0 )
    {
      return null;
    }
    // find persist entity
    DataInstance dataInstance = loadDataInstance( p_persistEntityClass, p_id );
    if( dataInstance == null )
    {
      return null;
    }
    return dataInstance.entity;
  }


  /** ouvrir une transaction du em */
  private void setRW(DataInstance dataInstance)
  {
    if( dataInstance.isRW )
    {
      dataInstance.em.getTransaction().begin();
      dataInstance.isRW = true;
    }
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

    DataInstance dataInstance = null;
    if( p_obj.isTrancient() )
    {
      // persist object for the first time
      PersistEntity entity = null;
      try
      {
        entity = (PersistEntity)p_persistEntityClass.newInstance();
      } catch( Exception e )
      {
        e.printStackTrace();
        return;
      }
      EntityManager em = getEMF().createEntityManager();
      em.getTransaction().begin();
      em.persist( entity );
      em.flush();
      dataInstance = new DataInstance( entity.getId(), entity, em );
      dataInstance.isRW = true;
      m_dataInstances.add( dataInstance );
      p_obj.setId( entity.getId() );
    }
    else
    {
      dataInstance = loadDataInstance( p_persistEntityClass, p_obj.getId() );
      setRW( dataInstance );
    }

    dataInstance.entity.setEb( p_obj );

    // TODO we may want to patch PersistEntity here
    p_obj.setVersion( dataInstance.entity.getVersion() );
  }



  @SuppressWarnings("unchecked")
  static protected List<?> getList(Class<?> p_persistEntityClass, String p_whereClase)
  {
    assert p_persistEntityClass != null;

    Query query = null;
    if( p_whereClase == null )
    {
      query = getEntityManager()
          .createQuery( "select from " + p_persistEntityClass.getSimpleName() );
    }
    else
    {
      query = getEntityManager().createQuery(
          "select from " + p_persistEntityClass.getSimpleName() + " where " + p_whereClase );
    }
    List<Object> resultList = query.getResultList();
    return resultList;
  }



  protected void delete(Class<?> p_persistEntityClass, long p_id)
  {
    assert p_id != 0;
    assert m_isOpen == true;
    DataInstance dataInstance = loadDataInstance( p_persistEntityClass, p_id );
    if( dataInstance == null )
    {
      System.err.println( "delete " + p_persistEntityClass + "(" + p_id + ") failed" );
      return;
    }
    if( !dataInstance.isRW )
    {
      setRW( dataInstance );
    }
    dataInstance.em.remove( dataInstance.entity );
  }



  /** close tt les em & commit des trans */
  public void close()
  {
    assert m_isOpen == true;
    for( DataInstance di : m_dataInstances )
    {
      if( di.isRW )
      {
        di.em.getTransaction().commit();
      }
      di.em.close();
    }
    m_isOpen = false;
  }

  /** rollback de tt les em/trans ouverte */
  public void rollback()
  {
    assert m_isOpen == true;
    for( DataInstance di : m_dataInstances )
    {
      if( di.isRW )
      {
        if( di.entity.getData() == null )
        {
          di.em.remove( di.entity );
          di.em.getTransaction().commit();
        }
        else
        {
          di.em.getTransaction().rollback();
        }
      }
      di.em.close();
    }
    m_isOpen = false;
  }

  private DataInstance findDataInstance(Class<?> p_class, long p_id)
  {
    for( DataInstance di : m_dataInstances )
    {
      if( di.entity.getClass() == p_class && di.id == p_id )
      {
        return di;
      }
    }
    return null;
  }

  private class DataInstance
  {
    public long id = 0;
    public PersistEntity entity = null;
    public EntityManager em = null;
    public boolean isRW = false;

    public DataInstance(long p_id, PersistEntity p_entity, EntityManager p_em)
    {
      assert p_id != 0;
      assert p_entity != null;
      assert p_em != null;
      id = p_id;
      entity = p_entity;
      em = p_em;
    }

    @SuppressWarnings("unused")
    public DataInstance(EntityManager p_em)
    {
      assert p_em != null;
      em = p_em;
    }
  }

}
