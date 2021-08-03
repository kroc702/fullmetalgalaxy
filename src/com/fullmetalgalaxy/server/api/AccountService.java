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

package com.fullmetalgalaxy.server.api;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fullmetalgalaxy.server.Auth;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.googlecode.objectify.Query;

/**
 * 
 *
 */
@Path("/account")
public class AccountService
{

  static protected DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
  static protected DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );

  @Path("me")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getMe(@Context HttpServletRequest request, @Context HttpServletResponse response)
  {
    Map<String, Object> jsonData = new HashMap<>();
    EbAccount account = Auth.getUserAccount( request, response );
    if( account != null )
    {
      jsonData.put( "public", toPublicJson( account ) );
      jsonData.put( "isUserAdmin", Auth.isUserAdmin( request, response ) );
      jsonData.put( "logoutUrl", Auth.getLogoutURL( request.getHeader( "Origin" ) ) );
    }
    else
    {
      jsonData.put( "fmgLoginUrl", Auth.getFmgLoginURL( request.getHeader( "Origin" ) ) );
      jsonData.put( "googleLoginUrl", Auth.getGoogleLoginURL( request.getHeader( "Origin" ) ) );
    }
    return new Gson().toJson( jsonData );

  }

  @Path("")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getAccount(@QueryParam("id") long id)
  {
    EbAccount account = FmgDataStore.dao().get( EbAccount.class, id );
    return new Gson().toJson( toPublicJson( account ) );
  }

  @Path("/list")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getList(@DefaultValue("0") @QueryParam("from") int from,
      @DefaultValue("20") @QueryParam("to") int to,
      @DefaultValue("") @QueryParam("orderby") String orderby,
      @DefaultValue("false") @QueryParam("all") boolean allPlayers)
  {
    if( orderby == null || orderby.isEmpty() )
    {
      if( allPlayers )
        orderby = "-m_trueSkillLevel";
      else
        orderby = "-m_currentStats.m_averageNormalizedRank";
    }

    Query<EbAccount> accountQuery = FmgDataStore.dao().query( EbAccount.class );

    if( !allPlayers )
    {
      accountQuery.filter( "m_currentStats.m_includedInRanking", true );
    }
    accountQuery.order( orderby );

    List<Object> list = new ArrayList<Object>();
    for( EbAccount account : accountQuery.offset( from ).limit( to - from ) )
    {
      list.add( toPublicJson( account ) );
    }

    return new Gson().toJson( list );
  }

  protected Map<String, Object> toPublicJson(EbAccount account)
  {
    Map<String, Object> jsonAccount = new HashMap<>();
    jsonAccount.put( "id", account.getId() );
    jsonAccount.put( "pseudo", account.getPseudo() );
    jsonAccount.put( "avatarUrl", account.getAvatarUrl() );
    jsonAccount.put( "profileUrl", account.getProfileUrl() );
    jsonAccount.put( "gradUrl", account.getGradUrl() );
    jsonAccount.put( "fullStats", account.getFullStats() );
    jsonAccount.put( "currentStats", account.getCurrentStats() );
    jsonAccount.put( "level", decimalFormat.format( account.getCurrentLevel() ) );
    jsonAccount.put( "lastConnexion", dateFormat.format( account.getLastConnexion() ) );
    jsonAccount.put( "subscription", dateFormat.format( account.getSubscriptionDate() ) );
    if( (account.allowMsgFromPlayer() && account.haveEmail()) )
    {
      jsonAccount.put( "emailUrl", account.getEMailUrl() );
    }
    return jsonAccount;
  }

}
