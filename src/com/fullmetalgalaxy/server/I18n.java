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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vincent
 *
 */
public class I18n
{

  @SuppressWarnings("unchecked")
  public static String getURI(HttpServletRequest p_request, String p_locale)
  {
    assert p_request != null;
    String querry = p_request.getQueryString();
    if( querry == null )
    {
      querry = "locale=" + LocaleFmg.fromString( p_locale );
    }
    else
    {
      querry = "";
      for( Entry<String, String[]> param : (Set<Map.Entry<String, String[]>>)p_request
          .getParameterMap().entrySet() )
      {
        if( !param.getKey().equalsIgnoreCase( "locale" ) )
        {
          querry += param.getKey() + "=" + param.getValue()[0] + "&";
        }
      }
      querry += "locale=" + LocaleFmg.fromString( p_locale );
    }
    return p_request.getRequestURI() + "?" + querry;
  }

  private static Pattern s_patternUrl = Pattern.compile( "(.*)(\\.\\w+)$" );
  private static Pattern s_patternLocalized = Pattern.compile( ".*_\\w\\w$" );
  
  /**
   * return a localized version of the given url. (add _en or _fr)
   * this method don't check whether the resource exist or not.
   * if already localized, don't change url
   * @param p_request
   * @param p_response
   * @param p_url
   * @return
   */
  public static String localizeUrl(HttpServletRequest p_request, HttpServletResponse p_response,
      String p_url)
  {
    String locale = LocaleFmg.getDefault().toString();
    if( p_request != null && p_response != null )
    {
      locale = I18n.getLocale( p_request, p_response );
    }

    Matcher matcher = s_patternUrl.matcher( p_url );
    if( matcher.matches() )
    {
      p_url = matcher.group( 1 );
      // check if p_url is already localized !
      Matcher matcherLocalized = s_patternLocalized.matcher( p_url );
      if( !matcherLocalized.matches() )
      {
        p_url += "_" + locale;
      }
      p_url += matcher.group( 2 );
    }
    return p_url;
  }



  public static String getLocale(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    // search for locale in URL parameter
    String strLocale = p_request.getParameter( "locale" );
    if( strLocale != null )
    {
      strLocale = LocaleFmg.fromString( strLocale ).name();
      p_request.getSession( true ).setAttribute( "locale", strLocale );
      // if user is logged, change his locale preference
      if( Auth.isUserLogged( p_request, p_response ) )
      {
        EbAccount account = Auth.getUserAccount( p_request, p_response );
        account.setLocale( strLocale );
        FmgDataStore dataStore = new FmgDataStore( false );
        dataStore.put( account );
        dataStore.close();
        // to reload account data from datastore
        p_request.getSession().setAttribute( "account", null );
      }
      return strLocale;
    }
    // search in session
    strLocale = (String)p_request.getSession( true ).getAttribute( "locale" );
    if( strLocale != null )
    {
      return strLocale;
    }
    // search in account preference
    EbAccount account = Auth.getUserAccount( p_request, p_response );
    if( account != null && account.getLocale() != null )
    {
      return account.getLocale().name();
    }
    // search in request header
    Locale locale = p_request.getLocale();
    if( locale != null )
    {
      strLocale = LocaleFmg.fromString( locale.getLanguage() ).name();
      if( account != null )
      {
        account.setLocale( strLocale );
        FmgDataStore dataStore = new FmgDataStore( false );
        dataStore.put( account );
        dataStore.close();
      }
      return strLocale;
    }
    // then give a default value
    return LocaleFmg.getDefault().name();
  }


  public static String getRessource(HttpServletRequest p_request, HttpServletResponse p_response,
      String p_key)
  {
    LocaleFmg locale = LocaleFmg.fromString( getLocale( p_request, p_response ) );
    ResourceBundle res = ResourceBundle.getBundle( "index", locale.locale() );
    return res.getString( p_key );

  }

}
