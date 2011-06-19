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
package com.fullmetalgalaxy.server.image;


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
