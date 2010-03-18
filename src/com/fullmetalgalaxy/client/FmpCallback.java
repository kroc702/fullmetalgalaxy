/**
 * 
 */
package com.fullmetalgalaxy.client;


import com.fullmetalgalaxy.client.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Kroc
 *
 */
public class FmpCallback<ReturnedType> implements AsyncCallback<ReturnedType>
{

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
   */
  public void onFailure(Throwable p_caught)
  {
    // AppMain.instance().stopLoading();

    if( p_caught != null )
    {
      RpcUtil.logError( p_caught.getMessage(), p_caught );
    }

    try
    {
      MAppMessagesStack.s_instance.showWarning( Messages.getString( (RpcFmpException)p_caught ) );
    } catch( Throwable th )
    {
      try
      {
        if( (p_caught.getMessage() == null) || (p_caught.getMessage().length() == 0) )
        {
          // lets try it: don't display error to not confuse user...
          MAppMessagesStack.s_instance.showWarning( "Unknown error or serveur is unreachable\n" );
          // + p_caught.toString() );
        }
        else
        {
          MAppMessagesStack.s_instance.showWarning( p_caught.getMessage() );
        }
      } catch( Throwable th2 )
      {
        RpcUtil.logError( th2.getMessage(), th2 );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
   */
  public void onSuccess(ReturnedType p_result)
  {
    // AppMain.instance().stopLoading();
  }

}
