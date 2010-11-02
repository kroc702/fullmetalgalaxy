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
package com.fullmetalgalaxy.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Vincent Legendre
 * Store a list of informations (key=value) which can be serialized to/from a string and used as 
 * a gwt history token.
 * Warning: key and value can't contain char: '_', '#', ' ', '-'
 */
public class HistoryState
{
  protected Map<String, String> m_properties = new HashMap<String, String>();


  /**
   * 
   */
  public HistoryState()
  {
  }

  public HistoryState(String p_strToken)
  {
    fromString( p_strToken );
  }


  /* (non-Javadoc)
   * @see java.lang.Object#clone()
   */
  public HistoryState cloneHistory()
  {
    HistoryState state = new HistoryState( toString() );
    return state;
  }

  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    Set entrySet = m_properties.entrySet();
    for( Iterator it = entrySet.iterator(); it.hasNext(); )
    {
      Map.Entry entry = (Map.Entry)it.next();
      buffer.append( (String)entry.getKey() );
      buffer.append( "_" );
      String value = (String)entry.getValue();
      if( value != null )
      {
        buffer.append( value );
      }
      buffer.append( "_" );
    }
    return buffer.toString();
  }

  /**
   * Read p_str into this history token.
   * @param p_str
   */
  public void fromString(String p_strToken)
  {
    clear();
    String[] tokens = p_strToken.split( "_" );
    String key = null;
    String value = null;
    int i = 0;
    while( i < tokens.length )
    {
      key = tokens[i];
      i++;
      if( i < tokens.length )
      {
        value = tokens[i];
        if( value.length() == 0 )
        {
          value = null;
        }
        i++;
      }
      else
      {
        value = null;
      }
      setString( key, value );
    }
  }

  public void clear()
  {
    m_properties.clear();
  }

  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty()
  {
    return m_properties.isEmpty();
  }

  /**
   * @return
   * @see java.util.Map#keySet()
   */
  public Set<String> keySet()
  {
    return m_properties.keySet();
  }

  /** 
   * @param p_key
   * @return true if p_key is contained by this history token.
   */
  public boolean containsKey(String p_key)
  {
    return m_properties.containsKey( p_key );
  }

  public void removeKey(String p_key)
  {
    m_properties.remove( p_key );
  }

  public void addKey(String p_key)
  {
    setString( p_key, null );
  }

  public String getString(String p_key)
  {
    return (String)m_properties.get( p_key );
  }

  public void setString(String p_key, String p_value)
  {
    assert p_key != null;
    m_properties.put( p_key, p_value );
  }

  public String[] getStringArray(String p_key)
  {
    String str = getString( p_key );
    if( str == null )
    {
      return new String[0];
    }
    return str.split( "-" );
  }

  public void setStringArray(String p_key, String[] p_value)
  {
    StringBuffer str = new StringBuffer();
    for( int i = 0; i < p_value.length; i++ )
    {
      if( p_value[i] != null )
      {
        str.append( p_value[i] );
        str.append( "-" );
      }
    }
    setString( p_key, str.toString() );
  }

  public int getInt(String p_key)
  {
    String str = getString( p_key );
    int value = 0;
    if( str != null )
    {
      value = Integer.parseInt( str );
    }
    return value;
  }

  public void setInt(String p_key, int p_value)
  {
    setString( p_key, Integer.toString( p_value ) );
  }

  public long getLong(String p_key)
  {
    String str = getString( p_key );
    long value = 0;
    if( str != null )
    {
      value = Long.parseLong( str );
    }
    return value;
  }

  public void setLong(String p_key, long p_value)
  {
    setString( p_key, Long.toString( p_value ) );
  }

}
