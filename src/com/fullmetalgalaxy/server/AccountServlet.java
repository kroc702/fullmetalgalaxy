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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.model.AuthProvider;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 *
 */
public class AccountServlet extends HttpServlet
{
  private static final long serialVersionUID = -4916146982326069190L;
  private final static FmpLogger log = FmpLogger.getLogger( AccountServlet.class.getName() );



  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    if( p_request.getParameter( "logout" ) != null )
    {
      if( Auth.isUserLogged( p_request, p_response ) )
      {
        Auth.disconnectFmgUser( p_request );
      }
      String continueUrl = p_request.getParameter( "continue" );
      if( continueUrl == null )
      {
        continueUrl = "/";
      }
      String redirectUrl = UserServiceFactory.getUserService().createLogoutURL( continueUrl );
      p_response.sendRedirect( redirectUrl );
    }
    else
    {
      p_response.sendRedirect( "/" );
    }
  }


  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    Map<String, String> params = new HashMap<String, String>();
    boolean isConnexion = false;

    try
    {
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          if( item.getFieldName().equalsIgnoreCase( "connexion" ) )
          {
            isConnexion = true;
          }
          params.put( item.getFieldName(), Streams.asString( item.openStream(), "UTF-8" ) );
        }
      }
    } catch( FileUploadException e )
    {
      log.error( e );
    }

    if( isConnexion )
    {
      boolean isConnected = true;
      if( !Auth.isUserLogged( p_request, p_response ) )
      {
        isConnected = connectFmgUser( p_request, p_response, params );
      }
      if( isConnected )
      {
        String continueUrl = params.get( "continue" );
        if( continueUrl == null )
        {
          // by default, my games is the default url
          continueUrl = "/gamelist.jsp";
        }
        p_response.sendRedirect( continueUrl );
      }
      return;
    }
    else
    {
      String msg = checkParams( params );
      if( msg != null )
      {
        p_response.sendRedirect( "/account.jsp?msg=" + msg );
        return;
      }
      msg = saveAccount( p_request, p_response, params );
      if( msg != null )
      {
        p_response.sendRedirect( "/account.jsp?msg=" + msg );
        return;
      }
      else
      {
        if( !Auth.isUserLogged( p_request, p_response ) )
        {
          Auth.connectUser( p_request, params.get( "login" ) );
        }
        if( "0".equalsIgnoreCase( params.get( "accountid" ) ) )
        {
          // return page new games
          p_response.sendRedirect( "/gamelist.jsp?tab=0" );
        }
        else
        {
          // stay editing profile
          p_response.sendRedirect( "/profile.jsp?id=" + params.get( "accountid" ) );
        }
        return;
      }
    }


  }


  /**
   * try to connect an FMG (not google or other credential) user
   * @param p_request
   * @param p_response
   * @param params
   * @return false if connection failed and p_response is redirected. 
   * @throws IOException
   */
  private boolean connectFmgUser(HttpServletRequest p_request, HttpServletResponse p_response,
      Map<String, String> params) throws IOException
  {
    String login = params.get( "login" );
    if( login == null || login.isEmpty() )
    {
      p_response.sendRedirect( "/auth.jsp?msg=login ou mot de passe invalide" );
      return false;
    }
    FmgDataStore ds = new FmgDataStore(true);
    Query<EbAccount> query = ds.query( EbAccount.class ).filter( "m_login", login );
    EbAccount account = query.get();

    if( account == null )
    {
      p_response.sendRedirect( "/auth.jsp?msg=login ou mot de passe invalide" );
      return false;
    }
    if( account.getAuthProvider() != AuthProvider.Fmg )
    {
      p_response.sendRedirect( Auth.getGoogleLoginURL( p_request, p_response ) );
      return false;
    }
    String password = params.get( "password" );
    if( password == null )
    {
      p_response.sendRedirect( "/auth.jsp?msg=login ou mot de passe invalide" );
      return false;
    }
    if( account == null || account.getPassword() == null
        || !account.getPassword().equals( password ) )
    {
      p_response.sendRedirect( "/auth.jsp?msg=login ou mot de passe invalide" );
      return false;
    }

    // all seams ok: connect user
    Auth.connectUser( p_request, login );
    return true;
  }


  /**
   * 
   * @param params
   * @return null if all ok, an error message otherwise
   */
  private String checkParams(Map<String, String> params)
  {
    if( params.get( "authprovider" ).equalsIgnoreCase( "Fmg" ) )
    {
      String pass1 = params.get( "password1" );
      String pass2 = params.get( "password2" );
      if( pass1 == null || pass2 == null || !pass1.equals( pass2 ) )
      {
        return "vous devez tapper le meme mot de passe";
      }
      if( !pass1.isEmpty() )
      {
        params.put( "password", pass1 );
      }
    }
    if( params.get( "accountid" ) == null )
      return "pas de champs accountid";
    if( params.get( "login" ) == null || params.get( "login" ).length() < 4 )
      return "votre login doit faire plus de 3 caracteres";
    if( params.get( "pseudo" ) != null && params.get( "pseudo" ).length() < 4 )
      return "votre pseudo doit faire plus de 3 caracteres";
    return null;
  }

  /**
   * 
   * @param params
   * @return null if saved successfully, an error message otherwise
   */
  private String saveAccount(HttpServletRequest p_request, HttpServletResponse p_response,
      Map<String, String> params)
  {
    String strid = params.get( "accountid" );
    assert strid != null;
    long id = Long.parseLong( strid );
    FmgDataStore store = new FmgDataStore(false);
    EbAccount account = null;
    if( id == 0 )
    {
      // we are creating a new account
      account = new EbAccount();
      // lets check that login ins't took already
      if( FmgDataStore.isPseudoExist( params.get( "login" ) ) )
      {
        store.rollback();
        return "Ce pseudo existe deja";
      }
      if( EbAccount.isValidPseudo( params.get( "login" ) ) )
      {
        store.rollback();
        return "Ce pseudo est invalide";
      }
    }
    else
    {
      if( id != Auth.getUserAccount( p_request, p_response ).getId()
          && !Auth.isUserAdmin( p_request, p_response ) )
      {
        store.rollback();
        return "Vous n'avez pas le droit de faire ces modifs";
      }
      // just update an account
      account = store.get( EbAccount.class, id );
      if( params.get( "pseudo" ) != null
          && (account.getPseudo() == null || !account.getPseudo().equalsIgnoreCase(
              params.get( "pseudo" ) )) )
      {
        // lets check that pseudo ins't took already
        if( FmgDataStore.isPseudoExist( params.get( "pseudo" ) ) )
        {
          store.rollback();
          return "Ce pseudo existe deja";
        }
        // check that user is allowed to change his pseudo
        if( !account.canChangePseudo() && !Auth.isUserAdmin( p_request, p_response ) )
        {
          store.rollback();
          return "Vous ne pouvez pas modifier votre pseudo";
        }
        account.setPseudo( params.get( "pseudo" ) );
      }
    }

    account.setEmail( params.get( "email" ) );
    account.setAllowMailFromGame( params.get( "AllowMailFromGame" ) != null );
    account.setAllowMailFromNewsLetter( params.get( "AllowMailFromNewsLetter" ) != null );
    account.setAllowPrivateMsg( params.get( "AllowPrivateMsg" ) != null );
    account.setJabberId( params.get( "jabberId" ) );
    account.setDescription( params.get( "description" ) );
    account.setLogin( params.get( "login" ) );
    account.setAuthProvider( AuthProvider.Fmg );

    if( params.get( "password" ) != null )
    {
      account.setPassword( params.get( "password" ) );
    }
    if( account.getAuthProvider() == AuthProvider.Fmg && account.getPassword().isEmpty() )
    {
      store.rollback();
      return "Vous devez definir un mot de passe";
    }
    store.put( account );
    store.close();
    // to reload account data from datastore
    p_request.getSession().setAttribute( "account", null );
    return null;
  }

}
