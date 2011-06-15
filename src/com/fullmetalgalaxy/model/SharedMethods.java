package com.fullmetalgalaxy.model;

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
    return URL.encodePathSegment( decodedURLComponent );
  }
}
