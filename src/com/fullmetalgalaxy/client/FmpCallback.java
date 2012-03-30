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
package com.fullmetalgalaxy.client;


import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * @author Kroc
 *
 */
public class FmpCallback<ReturnedType> implements AsyncCallback<ReturnedType>
{

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
   */
  @Override
  public void onFailure(Throwable p_caught)
  {
    // AppMain.instance().stopLoading();

    if( p_caught != null )
    {
      RpcUtil.logError( p_caught.getMessage(), p_caught );
    }

    if( p_caught instanceof RpcFmpException )
    {
      MAppMessagesStack.s_instance.showWarning( ((RpcFmpException)p_caught).getLocalizedMessage() );
    }
    else if( p_caught instanceof SerializationException
        || p_caught instanceof IncompatibleRemoteServiceException )
    {
      Window.alert( MAppBoard.s_messages.wrongGameVersion() );
      ClientUtil.reload();
    }
    else
    {
      // lets try it: don't display error to not confuse user...
      MAppMessagesStack.s_instance.showWarning( MAppBoard.s_messages.unknownError() );
    }

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
   */
  @Override
  public void onSuccess(ReturnedType p_result)
  {
    // AppMain.instance().stopLoading();
  }

}
