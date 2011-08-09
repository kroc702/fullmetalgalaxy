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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * @author vlegendr
 * 
 * warning this class is not thread safe !
 */
public class GlobalVarBase
{
  private static final String ENTITY_KIND = "GlobalVar";
  private static final String ENTITY_VALUE = "v";
  
  private static DatastoreService s_datastore = DatastoreServiceFactory.getDatastoreService();
  
  
  public static void put(String p_key, Object p_value)
  {
    Entity entity = new Entity( ENTITY_KIND, p_key );
    try
    {
      entity.setUnindexedProperty( ENTITY_VALUE, p_value );
    } catch( java.lang.IllegalArgumentException  e )
    {
      // ok then, we will serialize Object
      
      ObjectOutputStream out = null;
      try
      {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        out = new ObjectOutputStream( o );
        out.writeObject( p_value );
        out.close();
        entity.setUnindexedProperty( ENTITY_VALUE, new Blob(o.toByteArray()) );
      } catch( Exception ex )
      {
        ex.printStackTrace();
      }
    }
    s_datastore.put( entity );
  }
  
  public static void delete(String p_key)
  {
    Key k = KeyFactory.createKey( ENTITY_KIND, p_key );
    s_datastore.delete( k );
  }
  
  public static Object get(String p_key)
  {
    Key k = KeyFactory.createKey( ENTITY_KIND, p_key );
    try
    {
      Entity entity = s_datastore.get( k );
      Object obj = entity.getProperty( ENTITY_VALUE );
      if( obj instanceof Blob)
      {
        ObjectInputStream in;
        try
        {
          in = new ObjectInputStream( new ByteArrayInputStream(((Blob)obj).getBytes() ));
          Object typedObj = in.readObject();
          in.close();
          return typedObj;
        } catch( Exception e )
        {
        }
      }
      return obj;
    } catch( EntityNotFoundException e )
    {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * increment an integer inside a transaction.
   * warning, convert value to an integer if he can.
   * @param p_key
   * @param p_toAdd
   * @return Integer.MAX_VALUE if error or new value if succeed
   */
  public static int increment(String p_key, int p_toAdd)
  {
    Entity entity = new Entity( ENTITY_KIND, p_key );
    Key k = KeyFactory.createKey( ENTITY_KIND, p_key );
    Transaction txn = s_datastore.beginTransaction();
    int value = p_toAdd;
    try
    {
      Object obj = s_datastore.get( k );
      if( obj != null && obj instanceof Number )
      {
        value += ((Number)obj).intValue();
        entity.setUnindexedProperty( ENTITY_VALUE, value );
        s_datastore.put( entity );
        txn.commit();
        return value;
      }
    } catch( EntityNotFoundException e )
    {
      e.printStackTrace();
      txn.rollback();
    } finally
    {
      txn = s_datastore.beginTransaction();
      entity.setUnindexedProperty( ENTITY_VALUE, value );
      s_datastore.put( entity );
      txn.commit();
    }
    return value;
  }

  public static int getInt(String p_key)
  {
    Object obj = get(p_key);
    if( obj != null && obj instanceof Number)
    {
      return ((Number)obj).intValue();
    }
    return 0;
  }

  public static float getFloat(String p_key)
  {
    Object obj = get(p_key);
    if( obj != null && obj instanceof Number)
    {
      return ((Number)obj).floatValue();
    }
    return 0;
  }

  public static String getString(String p_key)
  {
    Object obj = get(p_key);
    if( obj != null && obj instanceof String)
    {
      return ((String)obj);
    }
    return null;
  }

}
