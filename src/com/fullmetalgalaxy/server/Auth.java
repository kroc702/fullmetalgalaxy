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
package com.fullmetalgalaxy.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


/**
 * TODO manage open id or my own login system on top of google account
 * 
 * @author Vincent
 *
 */
public class Auth
{
  private static String getFullURI(HttpServletRequest p_request)
  {
    assert p_request != null;
    String querry = p_request.getQueryString();
    if( querry == null )
    {
      return p_request.getRequestURI();
    }
    return p_request.getRequestURI() + "?" + querry;
  }

  public static String getLogoutURL(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    return "/AccountServlet?logout=1&&continue=" + getFullURI( p_request );
  }

  public static String getFmgLoginURL(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    return "/auth.jsp?continue=" + getFullURI( p_request );
  }

  public static String getGoogleLoginURL(HttpServletRequest p_request,
      HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    UserService userService = UserServiceFactory.getUserService();
    return userService.createLoginURL( getFullURI( p_request ) );
  }

  private static boolean isUserGoogleLogged(HttpServletRequest p_request,
      HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    return UserServiceFactory.getUserService().isUserLoggedIn();
  }

  public static boolean isUserLogged(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    if( p_request.getSession( true ).getAttribute( "login" ) != null )
    {
      return true;
    }
    return isUserGoogleLogged( p_request, p_response );
  }

  public static boolean isUserAdmin(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    if( !Auth.isUserGoogleLogged( p_request, p_response ) )
    {
      return false;
    }
    return UserServiceFactory.getUserService().isUserAdmin();
  }

  public static String getUserLogin(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    Object login = p_request.getSession( true ).getAttribute( "login" );
    if( login != null )
    {
      return login.toString();
    }
    if( !isUserLogged( p_request, p_response ) )
    {
      return null;
    }
    return p_request.getUserPrincipal().getName();
  }


  /**
   * 
   * @param p_request
   * @param p_response
   * @return null if user ins't logged or not found in database.
   */
  public static EbAccount getUserAccount(HttpServletRequest p_request,
      HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    String login = getUserLogin( p_request, p_response );
    if( login == null )
    {
      return null;
    }
    // look account into session
    EbAccount account = (EbAccount)p_request.getSession().getAttribute( "account" );
    if( account == null )
    {
      // look account into datastore
      account = FmgDataStore.getAccount( login );
      p_request.getSession().setAttribute( "account", account );
    }
    if( account == null )
    {
      // user is logged but his account wasn't found in database.
      // It's likely a new google user: create his account
      account = new EbAccount();
      account.setLogin( login );
      account.setAuthProvider( AuthProvider.Google );
      FmgDataStore dataStore = new FmgDataStore();
      dataStore.save( account );
      dataStore.close();
    }
    else if( account.getPseudo() == null )
    {
      account.setLogin( account.getLogin() );
      FmgDataStore dataStore = new FmgDataStore();
      dataStore.save( account );
      dataStore.close();
    }

    return account;
  }

  public static String getUserPseudo(HttpServletRequest p_request, HttpServletResponse p_response)
  {
    assert p_request != null;
    assert p_response != null;
    if( !isUserLogged( p_request, p_response ) )
    {
      return p_request.getRemoteAddr();
    }
    String login = getUserAccount( p_request, p_response ).getPseudo();
    return login;
  }

  public static void connectUser(HttpServletRequest p_request, String p_login)
  {
    p_request.getSession( true ).setAttribute( "login", p_login );
  }

  public static void disconnectFmgUser(HttpServletRequest p_request)
  {
    p_request.getSession( true ).setAttribute( "login", null );
    p_request.getSession().setAttribute( "account", null );
  }


}