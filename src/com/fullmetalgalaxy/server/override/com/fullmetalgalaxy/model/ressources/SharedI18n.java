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

package com.fullmetalgalaxy.model.ressources;

import com.fullmetalgalaxy.model.ressources.MessagesRpc;
import com.fullmetalgalaxy.model.ressources.MessagesRpcException;
import com.fullmetalgalaxy.model.ressources.Misc;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.GWTi18nServer;
import com.google.gwt.core.client.GWT;

/**
 * This version is used by server side and client in debug mode.
 * 
 * @author Vincent
 *
 */
public class SharedI18n
{
  public static MessagesRpcException getMessagesError(long p_accountId)
  {
    if( GWT.isClient() )
    {
      // this is for debug only.
      // in production, the other class is compiled by gwt
      return GWT.create( MessagesRpcException.class );
    }
    else
    {
      try
      {
        return GWTi18nServer.create( MessagesRpcException.class, getLocale(p_accountId) );
      }catch (Exception e) {
      }
    }
    return null;
  }
  
  public static MessagesRpc getMessages(long p_accountId)
  {
    if( GWT.isClient() )
    {
      // this is for debug only.
      // in production, the other class is compiled by gwt
      return GWT.create( MessagesRpc.class );
    }
    else
    {
      try
      {
        return GWTi18nServer.create( MessagesRpc.class, getLocale(p_accountId) );
      }catch (Exception e) {
      }
    }
    return null;
  }
  
  public static Misc getMisc(long p_accountId)
  {
    if( GWT.isClient() )
    {
      // this is for debug only.
      // in production, the other class is compiled by gwt
      return GWT.create( Misc.class );
    }
    else
    {
      try
      {
        return GWTi18nServer.create( Misc.class, getLocale(p_accountId) );
      }catch (Exception e) {
      }
    }
    return null;
  }
  
  private static String getLocale(long p_accountId)
  {
    EbAccount account = null;
    if(p_accountId != 0)
    {
      account = FmgDataStore.dao().find( EbAccount.class, p_accountId );
    }
    String locale = null;
    if( account != null )
    {
      locale = account.getLocale();
    }
    return locale;
  }
}
