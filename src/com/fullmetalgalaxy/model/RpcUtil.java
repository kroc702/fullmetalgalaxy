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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
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
