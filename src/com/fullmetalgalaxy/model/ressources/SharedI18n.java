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

package com.fullmetalgalaxy.model.ressources;

import com.google.gwt.core.client.GWT;

/**
 * This version is only used by client side.
 * 
 * @author Vincent
 *
 */
public class SharedI18n
{
  private static MessagesRpcException s_msgError = null;
  private static MessagesRpc s_msg = null;
  private static Misc s_misc = null;

  /**
   * account id is used to decide which language to chose on server side (unused on client side)
   * @param p_accountId id of the account that should receive this message.
   * @return
   */
  public static MessagesRpcException getMessagesError(long p_accountId)
  {
    if( s_msgError == null )
    {
      s_msgError = GWT.create( MessagesRpcException.class );
    }
    return s_msgError;
  }

  public static MessagesRpc getMessages(long p_accountId)
  {
    if( s_msg == null )
    {
      s_msg = GWT.create( MessagesRpc.class );
    }
    return s_msg;
  }

  public static Misc getMisc(long p_accountId)
  {
    if( s_misc == null )
    {
      s_misc = GWT.create( Misc.class );
    }
    return s_misc;
  }

}
