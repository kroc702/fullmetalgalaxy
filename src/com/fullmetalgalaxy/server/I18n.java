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

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vincent
 *
 */
public class I18n
{

  private static String[] s_validLocale = { /*"en",*/"fr" };

  /**
   * 
   * @return a list of all supported locale
   */
  public static String[] getLocales()
  {
    return s_validLocale;
  }

  public static String getDefaultLocale()
  {
    return "fr";
  }


  private static String validLocale(String p_locale)
  {
    assert p_locale != null;
    /*if( p_locale.length() < 2 )
      return "en";
    p_locale = p_locale.substring( 0, 2 ).toLowerCase();
    for( String locale : getLocales() )
    {
      if( p_locale.equals( locale ) )
      {
        return p_locale;
      }
    }*/
    return getDefaultLocale();
  }

  @SuppressWarnings("unchecked")
  public static String getURI(HttpServletRequest p_request, String p_locale)
  {
    assert p_request != null;
    String querry = p_request.getQueryString();
    if( querry == null )
    {
      querry = "locale=" + validLocale( p_locale );
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
      querry += "locale=" + validLocale( p_locale );
    }
    return p_request.getRequestURI() + "?" + querry;
  }

  /**
   * 
   * @param p_url relative to localized folder
   * @return absolute url
   */
  public static String localize(HttpServletRequest p_request, HttpServletResponse p_response,
      String p_url)
  {
    return "/i18n/" + getLocale( p_request, p_response ) + p_url;
  }

  public static String getLocale(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    // search for locale in URL parameter
    String strLocale = p_request.getParameter( "locale" );
    if( strLocale != null )
    {
      strLocale = validLocale( strLocale );
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
    if( account != null && account.getLocale() != null && !account.getLocale().isEmpty() )
    {
      return account.getLocale();
    }
    // search in request header
    Locale locale = p_request.getLocale();
    if( locale != null )
    {
      strLocale = validLocale( locale.getLanguage() );
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
    return getDefaultLocale();
  }


}
