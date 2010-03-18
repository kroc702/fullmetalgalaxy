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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Version;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * @author Vincent
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersistEntity
{
  @Id
  @javax.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key m_key = null;

  @Version
  private long m_version = 0;

  /** serialized instance */
  private com.google.appengine.api.datastore.Blob m_data = null;


  public PersistEntity()
  {
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
      // TODO why do I have to do this ?
      m_version++;
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
    return EbBase.class.cast( obj );
  }


  public long getId()
  {
    if( m_key == null )
    {
      return 0;
    }
    return m_key.getId();
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
