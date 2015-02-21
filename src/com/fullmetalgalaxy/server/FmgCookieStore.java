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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A cookie store for only on single domain
 * @author vlegendr
 * CookieStore interface isn't implemented as I didn't need it.
 */
public class FmgCookieStore
{
  private final static Pattern s_patternInvalidCookie = Pattern.compile( ".*expires=...$" );
  
  private List<HttpCookie> m_cookies = new ArrayList<HttpCookie>();

  public FmgCookieStore()
  {
    super();
  }

  
  public HttpCookie getCookie(String p_name)
  {
    removeExpired();
    for( HttpCookie cookie : m_cookies )
    {
      if( cookie!=null && cookie.getName()!=null && cookie.getName().equalsIgnoreCase( p_name ))
      {
        return cookie;
      }
    }
    return null;
  }

  
  public void add(String p_header)
  {
    // HttpCookie.parse didn't work for me :(
    List<HttpCookie> list = new ArrayList<HttpCookie>();
    String[] array = p_header.split( "," );
    for(int i=0; i<array.length; i++)
    {
      String cookieStr = null;
      Matcher matcher = s_patternInvalidCookie.matcher( array[i] );
      if( matcher.matches() && (i<array.length+1) )
      {
        // we found a coma in date: join with next string to get full cookie
        cookieStr = array[i]+","+array[i+1];
        i++;
      }
      else
      {
        cookieStr = array[i];
      }
      HttpCookie cookie = HttpCookie.parse( cookieStr ).get( 0 );
      if( cookie != null )
      {
        list.add( cookie );
      }
    }
    
    add( list );
  }

  public void add(List<HttpCookie> p_list)
  {
    for( HttpCookie cookie : p_list )
    {
      add( cookie );
    }
  }

  /**
   * add, remove or remplace a cookie
   * @param p_cookie
   */
  public void add(HttpCookie p_cookie)
  {
    // first remove similar cookies
    List<HttpCookie> toRemove = new ArrayList<HttpCookie>();
    for( HttpCookie cookie : m_cookies )
    {
      if( cookie.getName().equals( p_cookie.getName()) )
      {
        toRemove.add( cookie );
      }
    }
    m_cookies.removeAll( toRemove );
    
    // then add new cookie
    if( "deleted".equalsIgnoreCase( p_cookie.getValue() ) )
    {
      // do nothing as already removed
    }
    else
    {
      m_cookies.add( p_cookie );
    }
  }

  public void removeExpired()
  {
    List<HttpCookie> toRemove = new ArrayList<HttpCookie>();
    for( HttpCookie cookie : m_cookies )
    {
      if( cookie==null || cookie.hasExpired() )
      {
        toRemove.add( cookie );
      }
    }
    m_cookies.removeAll( toRemove );
  }

  @Override
  public String toString()
  {
    removeExpired();
    StringBuffer buf = new StringBuffer();
    for( HttpCookie cookie : m_cookies )
    {
      buf.append( cookie.toString() );
      buf.append( ", " );
    }
    if( buf.length() > 2 )
    {
      buf.setLength( buf.length() - 2 );
    }
    return buf.toString();
  }




  public List<HttpCookie> getCookies()
  {
    return m_cookies;
  }


}
