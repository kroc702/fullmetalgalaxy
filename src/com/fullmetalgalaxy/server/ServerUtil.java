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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import jskills.GameInfo;

import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.server.forum.ConectorImpl;
import com.fullmetalgalaxy.server.forum.ForumConector;
import com.fullmetalgalaxy.server.forum.NewsConector;
import com.google.appengine.api.utils.SystemProperty;

/**
 * @author Kroc
 *
 */
public class ServerUtil
{
  public final static Logger logger = Logger.getLogger( "Server" );
  
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


  public static final boolean PRODUCTION_MODE = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
  public static final String URL_PREFIX = PRODUCTION_MODE ? "" : "http://localhost:8888";
  
  private static String s_basePath = null;
  public static void setBasePath(String p_path)
  {
    synchronized( ServerUtil.class )
    {
      s_basePath = p_path;
    }
  }
  
  public static String getBasePath()
  {
    // well, I'm not sure it's fully thread safe ;)
    return s_basePath;
  }
  

  /**
   * this method is here to allow us modifying GameInfo parameters
   * @return
   */
  public static GameInfo getGameInfo()
  {
    return GameInfo.getDefaultGameInfo();
  }


  /**
   * remove accented from a string and replace with ascii equivalent
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

  
  
  public static List<EbAccount> findRequestedAccounts(HttpServletRequest p_request)
  {
    List<EbAccount> accountList = new ArrayList<EbAccount>();
    // read account from primary ID
    String strArray[] = p_request.getParameterValues( "id" );
    if( strArray != null )
      for( String str : strArray )
    {
      try
      {
       EbAccount account = FmgDataStore.dao().find( EbAccount.class, Long.parseLong(str));
       if( account != null )
       {
         accountList.add( account );
       }
      } catch(Exception e) {}
    }
    // from forum ID
    strArray = p_request.getParameterValues( "forumid" );
    if( strArray != null )
    for( String str : p_request.getParameterValues("forumid") )
    {
      EbAccount account = FmgDataStore.dao().query(EbAccount.class).filter("m_forumId ==", str ).get();
      if( account != null )
      {
        accountList.add( account );
      }
    }
    // from pseudo
    strArray = p_request.getParameterValues( "pseudo" );
    if( strArray != null )
    for( String str : p_request.getParameterValues("pseudo") )
    {
      EbAccount account = FmgDataStore.dao().query(EbAccount.class).filter("m_pseudo ==", str ).get();
      if( account != null )
      {
        accountList.add( account );
      }
    }
    return accountList;
  }
  

  public static EbAccount findRequestedAccount(HttpServletRequest p_request)
  {
    EbAccount account = null;
    try
    {
      account = FmgDataStore.dao().find( EbAccount.class, Long.parseLong(p_request.getParameter("id")));
    } catch(Exception e) {}
    if( account == null ) 
    {
        String forumid = p_request.getParameter("forumid");
        if( forumid != null )
        {
            account = FmgDataStore.dao().query(EbAccount.class).filter("m_forumId ==", forumid ).get();
        }
    }
    if( account == null ) 
    {
      try
      {
        account = FmgDataStore.dao().query(EbAccount.class).filter("m_pseudo ==", p_request.getParameter("pseudo")).get();
      } catch(Exception e) {}
    }
    return account;
  }
  


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
