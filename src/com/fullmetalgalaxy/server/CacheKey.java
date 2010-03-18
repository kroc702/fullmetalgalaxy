/**
 * 
 */
package com.fullmetalgalaxy.server;


/**
 * @author Vincent
 *
 */
public class CacheKey implements java.io.Serializable
{
  private static final long serialVersionUID = -3419926096547184672L;

  public enum CacheKeyType
  {
    Image, ModelUpdate;
  }

  private long m_id = 0L;
  private CacheKeyType m_type = CacheKeyType.Image;

  public CacheKey(CacheKeyType p_type, long p_id)
  {
    m_type = p_type;
    m_id = p_id;
  }

  public CacheKey(CacheKeyType p_type, String p_id)
  {
    m_type = p_type;
    m_id = p_id == null ? 0 : p_id.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int)(m_id ^ (m_id >>> 32));
    result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    CacheKey other = (CacheKey)obj;
    if( m_id != other.m_id )
      return false;
    if( m_type == null )
    {
      if( other.m_type != null )
        return false;
    }
    else if( !m_type.equals( other.m_type ) )
      return false;
    return true;
  }

  
}
