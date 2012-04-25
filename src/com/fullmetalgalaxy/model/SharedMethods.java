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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import com.fullmetalgalaxy.client.ClientUtil;
import com.google.gwt.http.client.URL;

/**
 * Method collection used in shared packaged but have different implementation 
 * Whether it's on server or client side.
 * 
 * @author Vincent
 *
 */
public class SharedMethods
{
  /**
   * Returns a string where all characters that are not valid for a URL
   * component have been escaped. The escaping of a character is done by
   * converting it into its UTF-8 encoding and then encoding each of the
   * resulting bytes as a %xx hexadecimal escape sequence.
   * 
   * <p>
   * The following character sets are <em>not</em> escaped by this method:
   * <ul>
   * <li>ASCII digits or letters</li>
   * <li>ASCII punctuation characters:
   * 
   * <pre>- _ . ! ~ * ' ( )</pre>
   * </li>
   * </ul>
   * </p>
   * 
   * <p>
   * Notice that this method <em>does</em> encode the URL component delimiter
   * characters:<blockquote>
   * 
   * <pre>
   * ; / ? : &amp; = + $ , #
   * </pre>
   * 
   * </blockquote>
   * </p>
   * 
   * @param decodedURLComponent a string containing invalid URL characters
   * @return a string with all invalid URL characters escaped
   * 
   * @throws NullPointerException if decodedURLComponent is <code>null</code>
   */
  public static String encodePathSegment(String decodedURLComponent)
  {
    if( decodedURLComponent == null )
    {
      return null;
    }
    return URL.encodePathSegment( decodedURLComponent );
  }

  /**
   * on server, same as original method (ie System.currentTimeMillis())
   * on client, this method return current time as seen by server ! (ie it remove an offset given at page loading) 
   * @return current time in millis
   */
  public static long currentTimeMillis()
  {
    return ClientUtil.serverTimeMillis();
  }

}
