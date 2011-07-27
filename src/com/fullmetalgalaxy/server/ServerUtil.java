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

import java.util.Date;

import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.server.forum.ConectorImpl;
import com.fullmetalgalaxy.server.forum.ForumConector;
import com.fullmetalgalaxy.server.forum.NewsConector;

/**
 * @author Kroc
 *
 */
public class ServerUtil
{

  private static final String PLAIN_ASCII = "AaEeIiOoUu" // grave
      + "AaEeIiOoUuYy" // acute
      + "AaEeIiOoUuYy" // circumflex
      + "AaOoNn" // tilde
      + "AaEeIiOoUuYy" // umlaut
      + "Aa" // ring
      + "Cc" // cedilla
      + "OoUu" // double acute
  ;

  private static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
      + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
      + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
      + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
      + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
      + "\u00C5\u00E5"
      + "\u00C7\u00E7" 
      + "\u0150\u0151\u0170\u0171";

  private static final String ALPHABET = "azertyuiopqsdfghjklmwxcvbn0123456789";


  
  private static String s_basePath = null;
  public static void setBasePath(String p_path)
  {
    s_basePath = p_path;
  }
  
  public static String getBasePath()
  {
    return s_basePath;
  }
  
  /**
   * remove accentued from a string and replace with ascii equivalent
   */
  public static String convertNonAscii(String s)
  {
    if( s == null )
      return null;
    StringBuilder sb = new StringBuilder();
    int n = s.length();
    for( int i = 0; i < n; i++ )
    {
      char c = s.charAt( i );
      int pos = UNICODE.indexOf( c );
      if( pos > -1 )
      {
        sb.append( PLAIN_ASCII.charAt( pos ) );
      }
      else
      {
        sb.append( c );
      }
    }
    return sb.toString();
  }



  public static String randomString(int p_length)
  {
    StringBuffer str = new StringBuffer();
    while( p_length > 0 )
    {
      str.append( ALPHABET.charAt( RpcUtil.random( ALPHABET.length() ) ) );
      p_length--;
    }
    return str.toString();
  }

  public static Date currentDate()
  {
    // TimeZone timezone = TimeZone.getDefault();
    Date date = new Date( System.currentTimeMillis() );
    // date.
    return date;
  }

  /*
  public static String negateRegexStr(String p_str)
  {
    String out = "";
    for()
    for( char ch : p_str)
    
    return out;
  }
  */
  
  /*
  set exp {^([^f]|f[^o]|fo[^o])*.{0,2}$}
  The following proc will build the expression for any given string
   proc reg_negate {str} {
      set partial ""
      set branches [list]
      foreach c [split $str ""] {
          lappend branches [format {%s[^%s]} $partial $c]
          append partial $c
      }
      set exp [format {^(%s)*.{0,%d}$} [join $branches "|"] \
          [expr [string length $str] -1]]
  }
   
   */


  private static ConectorImpl s_forumConnector = new ConectorImpl();

  public static ForumConector forumConnector()
  {
    return s_forumConnector;
  }

  public static NewsConector newsConnector()
  {
    return s_forumConnector;
  }
}
