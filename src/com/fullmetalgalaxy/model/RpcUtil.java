/**
 * 
 */
package com.fullmetalgalaxy.model;

import com.google.gwt.core.client.GWT;


/**
 * @author Kroc
 *
 */
public class RpcUtil
{
  /**
   * 
   * @param p_maxValue
   * @return a random integer in [0;p_maxValue[
   */
  public static int random(int p_maxValue)
  {
    return (int)Math.floor( Math.random() * p_maxValue );
  }


  private static native boolean isJs() /*-{
          return true;
         }-*/;

  public static boolean isJsEnable()
  {
    boolean ret = false;
    try
    {
      ret = isJs();
    } catch( Throwable e )
    {
    }
    return ret;
  }


  // TODO send error to server - use log4j
  public static void logError(String p_message)
  {
    // exception is here to keep track of stack
    logError( p_message, new Exception() );
  }

  public static void logError(String p_message, Throwable p_e)
  {
    // exception is here to keep track of stack
    GWT.log( p_message, p_e );
  }

  public static void logDebug(String p_message)
  {
    GWT.log( p_message, null );
  }

  public static void logDebug(String p_message, Exception p_e)
  {
    logError( p_message, p_e );
  }

  public static void assertTrue(boolean p_expr) throws Exception
  {
    if( !p_expr )
    {
      Exception e = new Exception( "assert failure" );
      logError( "assert failure", e );
      throw e;
    }
  }

}
