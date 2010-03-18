/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.util.Date;

/**
 * @author Kroc
 *
 */
public class ServerUtil
{



  public static Date currentDate()
  {
    // TimeZone timezone = TimeZone.getDefault();
    Date date = new Date( System.currentTimeMillis() );
    // date.
    return date;
  }


}
