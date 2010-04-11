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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.google.appengine.api.datastore.Blob;

/** 
 * @author Vincent
 *
 */
@Entity
public class PersistEntity
{
  @Id
  private Long m_id = null;

  private long m_version = 1;

  /** serialized instance */
  private com.google.appengine.api.datastore.Blob m_data = null;


  public PersistEntity()
  {
  }

  @SuppressWarnings("unused")
  @PrePersist
  private void PrePersist()
  {
    m_version++;
  }

  /**
   * serialize EbBase
   * @param p_ebBase
   */
  public void setEb(EbBase p_ebBase)
  {
    ByteArrayOutputStream outStream = null;
    ObjectOutputStream out = null;
    try
    {
      outStream = new ByteArrayOutputStream();
      out = new ObjectOutputStream( outStream );
      out.writeObject( p_ebBase );
      out.flush();
      setData( outStream.toByteArray() );
    } catch( IOException e )
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        if( out != null )
        {
          out.close();
        }
      } catch( IOException e )
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * deserialize EbBase
   * @return
   */
  protected EbBase getEb()
  {
    if( getData() == null )
    {
      return null;
    }
    Object obj = null;
    InputStream inStream = null;
    ObjectInputStream in = null;
    try
    {
      inStream = new ByteArrayInputStream( getData() );
      in = new ObjectInputStream( inStream );
      obj = in.readObject();
    } catch( IOException e )
    {
      e.printStackTrace();
    } catch( ClassNotFoundException e )
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        if( in != null )
        {
          in.close();
        }
      } catch( IOException e )
      {
        e.printStackTrace();
      }
    }
    EbBase base = EbBase.class.cast( obj );
    base.setVersion( getVersion() );
    base.setId( getId() );
    return base;
  }

  public void setTransient()
  {
    m_id = null;
  }

  public long getId()
  {
    if( m_id == null )
    {
      return 0;
    }
    return m_id;
  }


  protected byte[] getData()
  {
    return m_data.getBytes();
  }


  /**
   * @param p_data the data to set
   */
  protected void setData(byte[] p_data)
  {
    m_data = new Blob( p_data );
  }


  /**
   * @return the version
   */
  public long getVersion()
  {
    return m_version;
  }


  /**
   * @param p_version the version to set
   */
  public void setVersion(long p_version)
  {
    m_version = p_version;
  }


}
