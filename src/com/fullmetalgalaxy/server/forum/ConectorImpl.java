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
package com.fullmetalgalaxy.server.forum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.EbAccount.AllowMessage;
import com.fullmetalgalaxy.server.EbAccount.NotificationQty;
import com.fullmetalgalaxy.server.FmgCookieStore;
import com.fullmetalgalaxy.server.ServerUtil;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.myjavatools.web.ClientHttpRequest;


/**
 * 
 * I used firefox livehttpheaders extension to help constructing this.
 * 
 * As google infrastructure change his IP address every request, we arn't able to connect to forum.
 * To bypass this limitation, I usea web proxy located on another server.
 */

public class ConectorImpl implements ForumConector, NewsConector
{
  public static String FORUM_NEWS_THREAD_ID = "40";
  public static String FORUM_GAMES_THREAD_ID = "41";

  public static Logger logger = Logger.getLogger( "Conector" );

  private static String COOKIE_SID = "fa_" + FmpConstant.getForumHost().replace( '.', '_' )
      + "_sid";
  private static String FORUM_USERNAME = "";
  private static String FORUM_PASS = "";
  private static String PROXY_KEY = "";

  // various pattern on forum pages
  private static Pattern s_confirmPassPattern = Pattern.compile(
      ".*<input type=\"hidden\" name=\"confirm_pass\" value=\"(.+)\" />.*", Pattern.DOTALL );
  private static Pattern s_addHiddenFieldsPattern = Pattern
      .compile(
          ".*addHiddenFields\\('.+', \\{'auth\\[\\]':\\[\\['(.+)',(.+)\\],\\['(.+)',(.+)\\]\\]\\}\\);\\}.*",
          Pattern.DOTALL );
  

  // profil field
  private static final String FIELD_USERNAME = "username_edit";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_LEVEL = "profile_field_10_2";
  private static final String FIELD_GRADICON = "profile_field_6_3[]";
  private static final String FIELD_JABBER = "profile_field_3_1";
  private static final String FIELD_FMG_NOTIF_MODE = "profile_field_7_4";
  private static final String FIELD_FMG_NOTIF_QTY = "profile_field_7_5";

  // field pattern for profil forum page
  private static final Pattern s_usernamePattern = fieldTextPattern( FIELD_USERNAME );
  private static final Pattern s_emailPattern = fieldTextPattern( FIELD_EMAIL );
  private static final Pattern s_jabberPattern = fieldTextPattern( FIELD_JABBER );
  private static final Pattern s_avatarUrlPattern = Pattern.compile(
      ".*Image Actuelle</span><br /><img src=\"([^\"]*)\".*", Pattern.DOTALL );
  private static final Pattern s_notifModePattern = fieldSelectPattern( FIELD_FMG_NOTIF_MODE );
  private static final Pattern s_notifQtyPattern = fieldSelectPattern( FIELD_FMG_NOTIF_QTY );
  private static final Pattern s_sendEmailPattern = fieldRadioPattern( "viewemail" );

  // field pattern for profil page that we need to backup to avoid override
  private static Map<String, Pattern> s_fieldPatternMap = new HashMap<String, Pattern>();
  static
  {
    s_fieldPatternMap.put( "viewemail", fieldRadioPattern( "viewemail" ) );
    s_fieldPatternMap.put( "newsletter", fieldRadioPattern( "newsletter" ) );
    s_fieldPatternMap.put( "hideonline", fieldRadioPattern( "hideonline" ) );
    s_fieldPatternMap.put( "notifyreply", fieldRadioPattern( "notifyreply" ) );
    s_fieldPatternMap.put( "notifypm", fieldRadioPattern( "notifypm" ) );
    s_fieldPatternMap.put( "popup_pm", fieldRadioPattern( "popup_pm" ) );
    s_fieldPatternMap.put( "post_prevent", fieldRadioPattern( "post_prevent" ) );
    //s_fieldPatternMap.put( "no_report_popup", fieldRadioPattern( "no_report_popup" ) );
    //s_fieldPatternMap.put( "no_report_mail", fieldRadioPattern( "no_report_mail" ) );
    s_fieldPatternMap.put( "attachsig", fieldRadioPattern( "attachsig" ) );
    s_fieldPatternMap.put( "language", fieldSelectPattern( "language" ) );
    // s_fieldPatternMap.put( "timezone", fieldSelectPattern( "timezone" ) );
    s_fieldPatternMap.put( "dateformat", fieldSelectPattern( "dateformat" ) );
    s_fieldPatternMap.put( "next_birthday_greeting", fieldTextPattern( "next_birthday_greeting" ) );
    s_fieldPatternMap.put( "user_status", fieldRadioPattern( "user_status" ) );
    s_fieldPatternMap.put( "user_allowpm", fieldRadioPattern( "user_allowpm" ) );
    s_fieldPatternMap.put( "user_allowavatar", fieldRadioPattern( "user_allowavatar" ) );
    s_fieldPatternMap.put( "user_allow_att", fieldRadioPattern( "user_allow_att" ) );
    // s_fieldPatternMap.put( "user_rank", fieldSelectPattern( "user_rank" ) );
    s_fieldPatternMap.put( "user_rank", Pattern.compile(
        ".*<select name=\"user_rank\">.*<option value=\"([^\"]*)\" selected=\"selected\">.*",
        Pattern.DOTALL ) );
  }
  

  private FmgCookieStore m_cookieStore = new FmgCookieStore();
  private static final String DEFAULT_ADMIN_URL = "http://" + FmpConstant.getForumHost()
      + "/admin/index.forum?part=admin";
  private String m_adminUrl = DEFAULT_ADMIN_URL;



  private static Properties s_forumConfig = new Properties();

  static
  {
    // try retrieve data from file
    try
    {
      s_forumConfig.load( ConectorImpl.class.getResourceAsStream( "forumaccount.properties" ) );
      FORUM_USERNAME = s_forumConfig.getProperty( "username" );
      FORUM_PASS = s_forumConfig.getProperty( "password" );
      PROXY_KEY = s_forumConfig.getProperty( "proxykey" );
    } catch( Exception e )
    {
      logger.severe( e.getMessage() );
    }
  }


  private static Pattern fieldTextPattern(String p_field)
  {
    return Pattern.compile( ".*<input[^>]*type=\"text\"[^>]*name=\"" + p_field
        + "\"[^>]*value=\"([^\"]*)\" />.*", Pattern.DOTALL );
  }

  private static Pattern fieldRadioPattern(String p_field)
  {
    return Pattern.compile( ".*<input type=\"radio\"[^>]*name=\"" + p_field
        + "\"[^>]*value=\"([^\"]*)\" checked=\"checked\".*",
        Pattern.DOTALL );
  }

  private static Pattern fieldSelectPattern(String p_field)
  {
    return Pattern.compile( ".*<select[^>]*name=\"" + p_field
        + "\"(?:[^<]*|</?o){0,20}<option value=\"([^\"]*)\" selected.*",
        Pattern.DOTALL );
  }



  private static final String PROXY_HOST = "www.fullmetalgalaxy.web-address.fr";


  private static String proxyfyUrl(String p_url)
  {
    if( !p_url.startsWith( "http://" ) )
    {
      p_url = "http://" + p_url;
    }
    try
    {
      return "http://" + PROXY_HOST + "/browse.php?url="
          + URLEncoder.encode( p_url, "UTF-8" ) + "&b=12";
    } catch( UnsupportedEncodingException e )
    {
      e.printStackTrace( System.err );
    }
    return "http://" + PROXY_HOST + "/browse.php?url=" + p_url;
  }

  private static Pattern s_deproxyfyPattern = Pattern.compile( ".*url=([^&]*)($|&.*)" );

  private static String deproxyfyUrl(String p_url)
  {
    Matcher matcher = s_deproxyfyPattern.matcher( p_url );
    if( !matcher.matches() )
    {
      return p_url;
    }
    p_url = matcher.group( 1 );
    try
    {
      p_url = URLDecoder.decode( p_url, "UTF-8" );
    } catch( UnsupportedEncodingException e )
    {
      logger.severe( e.getMessage() );
    }
    return p_url;
  }

  protected boolean isConnected()
  {
    return (m_cookieStore.getCookie( COOKIE_SID ) != null)
        || (!m_adminUrl.equals( DEFAULT_ADMIN_URL ));
  }

  protected void disconnect()
  {
    m_cookieStore = new FmgCookieStore();
    m_adminUrl = DEFAULT_ADMIN_URL;
  }

  /**
   * 
   */
  protected void connect()
  {
    if( isConnected() )
    {
      // done already !
      return;
    }
    URL url = null;
    try
    {
      // post login to get cookies
      // =========================
      url = new URL( proxyfyUrl( "http://" + FmpConstant.getForumHost() + "/login" ) );

      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder
          .withDefaults().doNotFollowRedirects() );
      request
          .setPayload( ("username=" + FORUM_USERNAME + "&password=" + FORUM_PASS + "&redirect=&query=&login=Connexion")
              .getBytes( "UTF-8" ) );
      request.addHeader( new HTTPHeader( "Host", PROXY_HOST ) );
      // request.addHeader( new HTTPHeader( "User-Agent",
      // "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 (CK-Ifremer) Firefox/3.0 ( .NET CLR 3.5.30729; .NET4.0E)"
      // ) );
      // request.addHeader( new HTTPHeader( "Accept",
      // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" ) );
      // request.addHeader( new HTTPHeader( "Accept-Language",
      // "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3" ) );
      // /request.addHeader( new HTTPHeader( "Accept-Encoding", "gzip,deflate" )
      // );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      // request.addHeader( new HTTPHeader( "Keep-Alive", "300" ) );
      // request.addHeader( new HTTPHeader( "Connection", "keep-alive" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/login" ) );
      // request.addHeader( new HTTPHeader( "Cookie",
      // "extendedview=; __utma=197449400.1261798819.1301581035.1301581035.1301581035.1; __utmz=197449400.1301581035.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _csuid=X8b6019a8aa1ad"
      // ) );
      request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );

      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );

      System.out.println( "response code: " + response.getResponseCode() );
      System.out.println( "final url: " + response.getFinalUrl() );
      for( HTTPHeader header : response.getHeaders() )
      {
        System.out.println( header.getName() + ": " + header.getValue() );
        if( "Set-Cookie".equalsIgnoreCase( header.getName() ) )
        {
          m_cookieStore.add( header.getValue() );
          // System.out.println( "Set-Cookie: " + header.getValue() );
        }
      }
      // System.out.println( new String( response.getContent(), getCharset(
      // response ) ) );
      

      // get admin panel to read tid param
      // =================================
      url = new URL( proxyfyUrl( m_adminUrl ) );
      request = new HTTPRequest( url );
      request.addHeader( new HTTPHeader( "Host", PROXY_HOST ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Cookie", m_cookieStore.getCookies().toString() ) );
      response = URLFetchServiceFactory.getURLFetchService().fetch( request );

      if( m_cookieStore.getCookie( COOKIE_SID ) != null )
      {
        m_adminUrl += "&sid=" + m_cookieStore.getCookie( COOKIE_SID ).getValue();
      }
      if( response.getFinalUrl() != null )
      {
        m_adminUrl = response.getFinalUrl().toString();
        // m_adminUrl is now a proxyfied url: we need to deproxyfy it
        m_adminUrl = deproxyfyUrl( m_adminUrl );
      }


    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
      disconnect();
    }
  }


  @Override
  public String getUserId(String p_pseudo)
  {
    // we don't need to be connected

    Pattern pattern = Pattern.compile(
        ".*href=\"[^\"]*(?:/|%2F)u([^\"&]*)[^\"]*\">(?:<span style=\"color:#......\">)?(?:<strong>)?"
            + Pattern.quote( p_pseudo ) + "<.*",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL );

    try
    {
      URL url = new URL(
          ("http://" + FmpConstant.getForumHost()
          + "/memberlist?username=" + URLEncoder.encode( p_pseudo, "UTF-8" ) ) );
      BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );

      StringBuffer page = new StringBuffer();
      String line;
      while( (line = reader.readLine()) != null )
      {
        page.append( line );
      }
      reader.close();
      // System.out.println( page.toString() );

      Matcher matcher = pattern.matcher( page );
      if( matcher.matches() )
      {
        // System.out.println( matcher.group( 1 ) );
        return matcher.group( 1 );
      }

    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
    }
    return null;
  }


  /**
   * This method was reverse engineering from forum page.
   * <code>
   * (function(){document.write(unescape('%3C%73%63%72%69%70%74%20%74%79%70%65%3D%22%74%65%78%74%2F%6A%61%76%61%73%63%72%69%70%74%22%3E%0A%66%75%6E%63%74%69%6F%6E%20%64%46%28%73%2C%6E%29%7B%6E%3D%70%61%72%73%65%49%6E%74%28%6E%29%3B%76%61%72%20%73%31%3D%75%6E%65%73%63%61%70%65%28%73%2E%73%75%62%73%74%72%28%30%2C%6E%29%2B%73%2E%73%75%62%73%74%72%28%6E%2B%31%2C%73%2E%6C%65%6E%67%74%68%2D%6E%2D%31%29%29%3B%76%61%72%20%74%3D%27%27%3B%66%6F%72%28%69%3D%30%3B%69%3C%73%31%2E%6C%65%6E%67%74%68%3B%69%2B%2B%29%74%2B%3D%53%74%72%69%6E%67%2E%66%72%6F%6D%43%68%61%72%43%6F%64%65%28%73%31%2E%63%68%61%72%43%6F%64%65%41%74%28%69%29%2D%73%2E%73%75%62%73%74%72%28%6E%2C%31%29%29%3B%72%65%74%75%72%6E%28%75%6E%65%73%63%61%70%65%28%74%29%29%3B%7D%0A%64%6F%63%75%6D%65%6E%74%2E%77%72%69%74%65%28%64%46%28%27%2B%39%49%79%69%78%6F%76%7A%2B%38%36%7A%25%37%46%76%6B%2B%39%4A%2B%38%38%7A%6B%25%37%45%7A%35%70%67%25%37%43%67%79%69%78%6F%76%7A%2B%38%38%2B%39%4B%6C%25%37%42%74%69%7A%6F%75%74%2B%38%36%78%6B%76%72%67%69%6B%4C%75%78%73%47%69%7A%6F%75%74%2B%38%25%33%45%6F%6A%2B%38%49%2B%38%36%67%69%7A%6F%75%74%2B%38%25%33%46%2B%25%33%44%48%6A%75%69%25%37%42%73%6B%74%7A%34%6C%75%78%73%79%2B%25%33%42%48%6F%6A%2B%25%33%42%4A%34%79%6B%7A%47%7A%7A%78%6F%68%25%37%42%7A%6B%2B%38%25%33%45%2B%38%25%33%44%67%69%7A%6F%75%74%2B%38%25%33%44%2B%38%49%67%69%7A%6F%75%74%2B%38%25%33%46%2B%39%48%2B%25%33%44%4A%6C%25%37%42%74%69%7A%6F%75%74%2B%38%36%67%6A%6A%4E%6F%6A%6A%6B%74%4C%6F%6B%72%6A%79%2B%38%25%33%45%6F%6A%2B%38%49%2B%38%36%6C%6F%6B%72%6A%79%2B%38%25%33%46%2B%25%33%44%48%6C%2B%38%36%2B%39%4A%2B%38%36%6A%75%69%25%37%42%73%6B%74%7A%34%6C%75%78%73%79%2B%25%33%42%48%6F%6A%2B%25%33%42%4A%2B%39%48%6C%75%78%2B%38%36%2B%38%25%33%45%2B%38%36%6C%6F%6B%72%6A%74%67%73%6B%2B%38%36%6F%74%2B%38%36%6C%6F%6B%72%6A%79%2B%38%36%2B%38%25%33%46%2B%25%33%44%48%25%37%43%67%72%25%37%42%6B%2B%38%36%2B%39%4A%2B%38%36%6C%6F%6B%72%6A%79%2B%25%33%42%48%6C%6F%6B%72%6A%74%67%73%6B%2B%25%33%42%4A%2B%39%48%6F%6C%2B%38%36%2B%38%25%33%45%2B%38%36%7A%25%37%46%76%6B%75%6C%2B%38%25%33%45%25%37%43%67%72%25%37%42%6B%2B%38%25%33%46%2B%38%36%2B%39%4A%2B%39%4A%2B%38%36%2B%38%25%33%44%75%68%70%6B%69%7A%2B%38%25%33%44%2B%38%36%2B%38%25%33%46%2B%25%33%44%48%6C%75%78%2B%38%36%2B%38%25%33%45%2B%38%36%70%2B%38%36%6F%74%2B%38%36%25%37%43%67%72%25%37%42%6B%2B%38%36%2B%38%25%33%46%2B%38%36%2B%25%33%44%48%6B%72%2B%38%36%2B%39%4A%2B%38%36%6A%75%69%25%37%42%73%6B%74%7A%34%69%78%6B%67%7A%6B%4B%72%6B%73%6B%74%7A%2B%38%25%33%45%2B%38%25%33%44%6F%74%76%25%37%42%7A%2B%38%25%33%44%2B%38%25%33%46%2B%39%48%6B%72%34%7A%25%37%46%76%6B%2B%38%36%2B%39%4A%2B%38%36%2B%38%25%33%44%6E%6F%6A%6A%6B%74%2B%38%25%33%44%2B%39%48%6B%72%34%74%67%73%6B%2B%38%36%2B%39%4A%2B%38%36%6C%6F%6B%72%6A%74%67%73%6B%2B%39%48%6B%72%34%25%37%43%67%72%25%37%42%6B%2B%38%36%2B%39%4A%2B%38%36%6A%4C%2B%38%25%33%45%25%37%43%67%72%25%37%42%6B%2B%25%33%42%48%36%70%2B%25%33%42%4A%2B%25%33%42%48%36%2B%25%33%42%4A%2B%38%49%25%37%43%67%72%25%37%42%6B%2B%25%33%42%48%70%2B%25%33%42%4A%2B%25%33%42%48%37%2B%25%33%42%4A%2B%38%25%33%46%2B%39%48%6C%34%67%76%76%6B%74%6A%49%6E%6F%72%6A%2B%38%25%33%45%6B%72%2B%38%25%33%46%2B%39%48%2B%25%33%44%4A%2B%25%33%44%4A%6B%72%79%6B%2B%38%36%2B%25%33%44%48%6B%72%2B%38%36%2B%39%4A%2B%38%36%6A%75%69%25%37%42%73%6B%74%7A%34%69%78%6B%67%7A%6B%4B%72%6B%73%6B%74%7A%2B%38%25%33%45%2B%38%25%33%44%6F%74%76%25%37%42%7A%2B%38%25%33%44%2B%38%25%33%46%2B%39%48%6B%72%34%7A%25%37%46%76%6B%2B%38%36%2B%39%4A%2B%38%36%2B%38%25%33%44%6E%6F%6A%6A%6B%74%2B%38%25%33%44%2B%39%48%6B%72%34%74%67%73%6B%2B%38%36%2B%39%4A%2B%38%36%6F%2B%39%48%6B%72%34%25%37%43%67%72%25%37%42%6B%2B%38%36%2B%39%4A%2B%38%36%6A%4C%2B%38%25%33%45%6C%6F%6B%72%6A%79%2B%25%33%42%48%6F%2B%25%33%42%4A%2B%38%25%33%46%2B%39%48%6C%34%67%76%76%6B%74%6A%49%6E%6F%72%6A%2B%38%25%33%45%6B%72%2B%38%25%33%46%2B%39%48%2B%25%33%44%4A%2B%25%33%44%4A%2B%25%33%44%4A%2B%39%49%35%79%69%78%6F%76%7A%2B%39%4B%27%2C%36%38%39%29%29%3B%0A%3C%2F%73%63%72%69%70%74%3E'));}());
   * </code> 
   * If this method change, this connector won't be able to create
   * new account.
   * @param s
   * @param n
   * @return
   */
  private static String decrypt(String s, int n)
  {
    String decrypted = "";
    try
    {
      char code = (char)Integer.parseInt( "" + s.charAt( n ) );
      // System.out.println( "code="+(int)code );
      String s1 = s.substring( 0, n );
      s1 += s.substring( n + 1, s.length() );
      // System.out.println( s );
      // System.out.println( s1 );
      s1 = URLDecoder.decode( s1, "UTF-8" );
      // not sure why this...
      // maybe javascript escape don't behave the same as URLDecoder
      s1 = s1.replace( ' ', (char)('%' + code) );
      // System.out.println( s1 );

      StringBuffer t = new StringBuffer();

      for( int i = 0; i < s1.length(); i++ )
      {
        t.append( (char)(s1.charAt( i ) - code) );
      }

      decrypted = URLDecoder.decode( t.toString(), "UTF-8" );
      // System.out.println( t.toString() );
      // System.out.println();
      // System.out.println("===========================");
    } catch( UnsupportedEncodingException e )
    {
      logger.severe( e.getMessage() );
    }
    return decrypted;
  }


  private static Pattern s_charsetPattern = Pattern.compile( ".*charset=(.+)[; $].*" );

  private static String getCharset(HTTPResponse p_response)
  {
    String responseCharset = "UTF-8";


    for( HTTPHeader header : p_response.getHeaders() )
    {
      if( "Content-Type".equalsIgnoreCase( header.getName() ) )
      {
        Matcher matcher = s_charsetPattern.matcher( header.getValue() );
        if( matcher.matches() )
        {
          responseCharset = matcher.group( 1 );
        }
      }
    }
    return responseCharset;
  }



  @Override
  public boolean createAccount(EbAccount p_account)
  {
    // we don't need to be connected

    // we need a password to create forum account
    if( p_account.getPassword() == null || p_account.getPassword().isEmpty() )
    {
      p_account.setPassword( ServerUtil.randomString( 8 ) );
    }


    // first request: send username, email and password
    // ================================================
    FmgCookieStore cookieStore = new FmgCookieStore();

    try
    {
      URL url = new URL( "http://" + FmpConstant.getForumHost() + "/register?agreed=true&step=2" );
      String payload = "username=" + URLEncoder.encode( p_account.getPseudo(), "UTF-8" )
          + "&email=" + URLEncoder.encode( p_account.getEmail(), "UTF-8" ) + "&password="
          + URLEncoder.encode( p_account.getPassword(), "UTF-8" ) + "&submit=Enregistrer";
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder
          .withDefaults().doNotFollowRedirects() );
      request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/register?agreed=true&step=2" ) );
      request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );
      request.setPayload( payload.getBytes( "UTF-8" ) );

      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );

      // System.out.println( "response code: " + response.getResponseCode() );
      // System.out.println( "final url: " + response.getFinalUrl() );
      for( HTTPHeader header : response.getHeaders() )
      {
        // System.out.println( header.getName() + ": " + header.getValue() );
        if( "Set-Cookie".equalsIgnoreCase( header.getName() ) )
        {
          cookieStore.add( header.getValue() );
        }
      }


      // read auth variable used by forum for security
      // =============================================
      String confirm_pass = "";
      String auth1 = null;
      String auth2 = null;
      String page = new String( response.getContent(), getCharset( response ) );

      Matcher matcher = s_confirmPassPattern.matcher( page );
      if( matcher.matches() )
      {
        confirm_pass = matcher.group( 1 );
      }
      matcher = s_addHiddenFieldsPattern.matcher( page );
      if( matcher.matches() )
      {
        auth1 = decrypt( matcher.group( 1 ), Integer.parseInt( matcher.group( 2 ) ) );
        auth2 = decrypt( matcher.group( 3 ), Integer.parseInt( matcher.group( 4 ) ) );
      }

      // second request: confirm password
      // ================================
      payload = "";
      if( auth1 != null && auth2 != null )
      {
        payload += "auth[]=" + URLEncoder.encode( auth1, "UTF-8" ) + "&auth[]="
            + URLEncoder.encode( auth2, "UTF-8" );
      }
      payload += "&password_confirm=" + URLEncoder.encode( p_account.getPassword(), "UTF-8" )
          + "&username=" + URLEncoder.encode( p_account.getPseudo(), "UTF-8" ) + "&email="
          + URLEncoder.encode( p_account.getEmail(), "UTF-8" ) + "&password="
          + URLEncoder.encode( p_account.getPassword(), "UTF-8" ) + "&confirm_pass="
          + URLEncoder.encode( confirm_pass, "UTF-8" ) + "&submit=Enregistrer";

      // in theory we should parse response to get second url. but it's the
      // same.
      request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder.withDefaults()
          .doNotFollowRedirects() );
      request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/register?agreed=true&step=2" ) );
      request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );
      request.setPayload( payload.getBytes( "UTF-8" ) );

      response = URLFetchServiceFactory.getURLFetchService().fetch( request );

      page = new String( response.getContent(), getCharset( response ) );

    } catch( Exception e )
    {
      logger.severe( e.getMessage() );
      return false;
    }
    return true;
  }



  @Override
  public boolean pushAccount(EbAccount p_account)
  {
    // we need to be connected as admin
    connect();

    try
    {
      URL url = new URL( proxyfyUrl( m_adminUrl + "&part=users_groups&sub=users&mode=edit&u="
          + p_account.getForumId() + "&extended_admin=1" ) );

      ClientHttpRequest clientPostRequest = null;
      clientPostRequest = new ClientHttpRequest( url );
      clientPostRequest.setParameter( FIELD_USERNAME, p_account.getPseudo() );
      clientPostRequest.setParameter( FIELD_EMAIL, p_account.getEmail() );
      clientPostRequest.setParameter( "password", "" );
      clientPostRequest.setParameter( "password_confirm", "" );

      // a list of many parameters
      clientPostRequest.setParameter( FIELD_LEVEL, p_account.getCurrentLevel() );
      clientPostRequest.setParameter( FIELD_GRADICON, p_account.getGradUrl() );
      
      Map<String,String> forumData = new HashMap<String,String>();
      if( p_account.getForumConnectorData() != null 
          && p_account.getForumConnectorData() instanceof HashMap<?,?> )
      {
        forumData = (HashMap<String,String>)p_account.getForumConnectorData();
      }
      

      // for dateformat if == "D j M - G:i" set to "D j M Y - G:i"
      if( forumData.get( "dateformat" ) == null
          || forumData.get( "dateformat" ).equals( "D j M - G:i" ) )
      {
        forumData.put( "dateformat", "D j M Y - G:i" );
      }
      if( forumData.get( "user_status" ) == null )
      {
        forumData.put( "user_status", "1" );
      }
      if( forumData.get( "user_allowpm" ) == null )
      {
        forumData.put( "user_allowpm", "1" );
      }
      if( forumData.get( "user_allowavatar" ) == null )
      {
        forumData.put( "user_allowavatar", "1" );
      }
      if( forumData.get( "user_allow_att" ) == null )
      {
        forumData.put( "user_allow_att", "1" );
      }

      // put back saved field
      for( Entry<String, Pattern> entry : s_fieldPatternMap.entrySet() )
      {
        if( forumData.get( entry.getKey() ) != null )
        {
          clientPostRequest.setParameter( entry.getKey(), forumData.get( entry.getKey() ) );
        }
      }

      clientPostRequest.setParameter( "submit", "Enregistrer" );
      clientPostRequest.setParameter( "mode", "save" );
      clientPostRequest.setParameter( "agreed", "true" );
      clientPostRequest.setParameter( "id", p_account.getForumId() );

      clientPostRequest.setCookie( m_cookieStore.getCookies() );
      InputStream is = clientPostRequest.post();
      
      // look for "Le profil de l'utilisateur a été mis à jour avec succès"
      // read response
      if( is != null )
      {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try
        {
          InputStreamReader reader = new InputStreamReader( is, "iso-8859-1" );
          int n;
          while( (n = reader.read( buffer )) != -1 )
          {
            writer.write( buffer, 0, n );
          }
        } finally
        {
          is.close();
        }
        // System.out.println( writer.toString() );
        if( !writer.toString().contains( "Le profil de l'utilisateur a été mis à jour avec succès" ) )
        {
          disconnect();
          return false;
        }
      }

      
    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
      disconnect();
      return false;
    }
    return true;
  }


  
  @Override
  public boolean pullAccount(EbAccount p_account)
  {
    // we need to be connected as admin
    connect();

    try
    {
      // we should have all we want on this page
      //
      String urlStr = m_adminUrl + "&part=users_groups&sub=users&mode=edit&u="
          + p_account.getForumId() + "&extended_admin=1";
      URL url = new URL( proxyfyUrl( urlStr ) );

      HTTPRequest request = new HTTPRequest( url );
      request.addHeader( new HTTPHeader( "Host", PROXY_HOST ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Cookie", m_cookieStore.getCookies().toString() ) );

      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );
      String page = new String( response.getContent(), getCharset( response ) );


      Matcher matcher = s_usernamePattern.matcher( page );
      if( matcher.matches() )
      {
        p_account.setPseudo( matcher.group( 1 ) );
      }
      else
      {
        // if username isn't found, consider this method as failed
        disconnect();
        return false;
      }

      matcher = s_emailPattern.matcher( page );
      if( matcher.matches() )
      {
        p_account.setEmail( matcher.group( 1 ) );
      }
      else
      {
        logger.warning( "pattern 'email' failed" );
      }

      matcher = s_avatarUrlPattern.matcher( page );
      if( matcher.matches() )
      {
        p_account.setForumAvatarUrl( deproxyfyUrl( matcher.group( 1 ) ) );
      }
      else
      {
        logger.warning( "pattern 'avatar url' failed" );
      }

      matcher = s_jabberPattern.matcher( page );
      if( matcher.matches() )
      {
        String jabberId = matcher.group( 1 );
        if( jabberId != null && !jabberId.isEmpty() )
        {
          p_account.setJabberId( jabberId );
        }
      }
      else
      {
        logger.warning( "pattern 'jabber id' failed" );
      }

      matcher = s_notifModePattern.matcher( page );
      if( matcher.matches() )
      {
        String value = matcher.group( 1 );
        if( value != null )
        {
          // 0 : email
          // 1 : PM
          if( value.equalsIgnoreCase( "0" ) )
          {
            p_account.setAllowMsgFromGame( AllowMessage.Mail );
          }
          else
          // if( value.equalsIgnoreCase( "1" ) )
          {
            p_account.setAllowMsgFromGame( AllowMessage.PM );
          }
          /*else
          {
            p_account.setAllowMsgFromGame( AllowMessage.No );
          }*/

        }
      }
      else
      {
        logger.warning( "pattern 'FIELD_FMG_NOTIF_MODE' failed" );
      }
      
      matcher = s_notifQtyPattern.matcher( page );
      if( matcher.matches() )
      {
        String value = matcher.group( 1 );
        if( value != null )
        {
          // 0 : min
          // 1 : standard
          // 2 : max
          if( value.equalsIgnoreCase( "0" ) )
          {
            p_account.setNotificationQty( NotificationQty.Min );
          }
          else if( value.equalsIgnoreCase( "2" ) )
          {
            p_account.setNotificationQty( NotificationQty.Max );
          }
          else
          {
            p_account.setNotificationQty( NotificationQty.Std );
          }

        }
      }
      else
      {
        logger.warning( "pattern 'FIELD_FMG_NOTIF_QTY' failed" );
      }

      matcher = s_sendEmailPattern.matcher( page );
      if( matcher.matches() )
      {
        String value = matcher.group( 1 );
        if( value != null )
        {
          // 0 : no
          // 1 : by email client
          // 2 : by forms
          if( value.equalsIgnoreCase( "0" ) )
          {
            p_account.setAllowMsgFromPlayer( AllowMessage.PM );
          }
          else
          {
            p_account.setAllowMsgFromPlayer( AllowMessage.Mail );
          }
        }
      }
      else
      {
        logger.warning( "pattern 'viewemail' failed" );
      }



      // backup some forum field to avoid override
      //
      Map<String,String> forumData = new HashMap<String,String>();
      if( p_account.getForumConnectorData() != null 
          && p_account.getForumConnectorData() instanceof HashMap<?,?> )
      {
        forumData = (HashMap<String,String>)p_account.getForumConnectorData();
      }
      

      for( Entry<String, Pattern> entry : s_fieldPatternMap.entrySet() )
      {
        matcher = entry.getValue().matcher( page );
        if( matcher.matches() )
        {
          String data = matcher.group( 1 );
          if( data != null && !data.isEmpty() )
          {
            forumData.put( entry.getKey(), data );
          }
        }
        else
        {
          forumData.remove( entry.getKey() );
          if( entry.getKey().equals( "user_rank" ) )
          {
            logger.fine( "pattern '" + entry.getKey() + "' failed" );
          }
          else
          {
            logger.warning( "pattern '" + entry.getKey() + "' failed" );
          }
        }
      }

       
      p_account.setForumConnectorData( forumData );

    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
      disconnect();
      return false;
    }
    return true;
  }


  @Override
  public boolean sendPMessage(String p_subject, String p_body, String... p_usernames)
  {
    // we need to be connected as admin
    connect();

    try
    {
      URL url = new URL( proxyfyUrl( "http://" + FmpConstant.getForumHost() + "/privmsg?" ) );

      ClientHttpRequest clientPostRequest = null;
      clientPostRequest = new ClientHttpRequest( url );
      for( String username : p_usernames )
      {
        if( username != null )
        {
          clientPostRequest.setParameter( "username[]", username );
        }
      }

      clientPostRequest.setParameter( "subject", p_subject );
      clientPostRequest.setParameter( "message", p_body );
      clientPostRequest.setParameter( "lt", "" );

      clientPostRequest.setParameter( "folder", "inbox" );
      clientPostRequest.setParameter( "mode", "post" );
      clientPostRequest.setParameter( "new_pm_time", "" + (System.currentTimeMillis() / 1000) );
      clientPostRequest.setParameter( "post", "Envoyer" );


      clientPostRequest.setCookie( m_cookieStore.getCookies() );
      clientPostRequest.post();

    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
      disconnect();
      return false;
    }
    return true;
  }


  @Override
  public boolean postNews(String p_threadId, String p_subject, String p_body)
  {
    // we need to be connected as admin
    connect();

    try
    {
      // first request: simply ask for posting page
      // ==========================================
      URL url = new URL( proxyfyUrl( "http://" + FmpConstant.getForumHost() + "/post?f="
          + p_threadId + "&mode=newtopic" ) );
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.GET, FetchOptions.Builder
          .withDefaults().doNotFollowRedirects() );
      request.addHeader( new HTTPHeader( "Host", PROXY_HOST ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost() ) );
      request.addHeader( new HTTPHeader( "Cookie", m_cookieStore.getCookies().toString() ) );

      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );

      // System.out.println( "response code: " + response.getResponseCode() );
      // System.out.println( "final url: " + response.getFinalUrl() );
      for( HTTPHeader header : response.getHeaders() )
      {
        // System.out.println( header.getName() + ": " + header.getValue() );
        if( "Set-Cookie".equalsIgnoreCase( header.getName() ) )
        {
          m_cookieStore.add( header.getValue() );
        }
      }


      // read auth variable used by forum for security
      // =============================================
      String auth1 = null;
      String auth2 = null;
      String page = new String( response.getContent(), getCharset( response ) );

      Matcher matcher = s_addHiddenFieldsPattern.matcher( page );
      if( matcher.matches() )
      {
        auth1 = decrypt( matcher.group( 1 ), Integer.parseInt( matcher.group( 2 ) ) );
        auth2 = decrypt( matcher.group( 3 ), Integer.parseInt( matcher.group( 4 ) ) );
      }


      // second request: post data
      // =========================
      url = new URL( proxyfyUrl( "http://" + FmpConstant.getForumHost() + "/post" ) );

      ClientHttpRequest clientPostRequest = null;
      //clientPostRequest = new ClientHttpRequest( url );
      

      // the following code was test and I didn't succeed to post a news on
      // forum

      URLConnection connection = url.openConnection();
      connection.setDoOutput( true );
      connection.setRequestProperty( "Host", "fullmetalplanete.forum2jeux.com" );
      connection
          .setRequestProperty( "User-Agent",
              "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 GTB7.1" );
      connection.setRequestProperty( "Accept",
          "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
      connection.setRequestProperty( "Accept-Language", "fr-fr" );
      connection.setRequestProperty( "Keep-Alive", "300" );
      connection.setRequestProperty( "Connection", "keep-alive" );

      connection.setRequestProperty( "Referer", "http://fullmetalplanete.forum2jeux.com/post?f=41&mode=newtopic" );
      connection.setRequestProperty( "Cookie", m_cookieStore.toString() );
          
      clientPostRequest = new ClientHttpRequest( connection );
      // clientPostRequest.setCookie( m_cookieStore.getCookies() );
      // clientPostRequest.postCookies();
      
      clientPostRequest.setParameter( "subject", p_subject );
      clientPostRequest.setParameter( "post_icon", "0" );
      clientPostRequest.setParameter( "message", p_body );
      clientPostRequest.setParameter( "lt", "0" );
      clientPostRequest.setParameter( "mode", "newtopic" );
      clientPostRequest.setParameter( "f", p_threadId );
      clientPostRequest.setParameter( "post", "Envoyer" );
      clientPostRequest.setParameter( "notify", "off" );
      clientPostRequest.setParameter( "topictype", "0" );
      // clientPostRequest.setParameter( "topic_calendar_day", "0" );
      // clientPostRequest.setParameter( "topic_calendar_month", "0" );
      // clientPostRequest.setParameter( "topic_calendar_year", "0" );
      // clientPostRequest.setParameter( "topic_calendar_hour", "" );
      // clientPostRequest.setParameter( "topic_calendar_min", "" );
      // clientPostRequest.setParameter( "topic_calendar_duration_day", "" );
      // clientPostRequest.setParameter( "topic_calendar_duration_hour", "" );
      // clientPostRequest.setParameter( "topic_calendar_duration_min", "" );
      // clientPostRequest.setParameter( "create_event", "0" );
      // clientPostRequest.setParameter( "calendar_d", "0" );
      clientPostRequest.setParameter( "poll_title", "" );
      clientPostRequest.setParameter( "poll_option_text", "" );
      clientPostRequest.setParameter( "poll_length", "" );
      clientPostRequest.setParameter( "poll_multiple", "0" );
      clientPostRequest.setParameter( "poll_cancel_vote", "0" );
      if( auth1 != null )
      {
        clientPostRequest.setParameter( "auth[]", auth1 );
      }
      if( auth2 != null )
      {
        clientPostRequest.setParameter( "auth[]", auth2 );
      }

      // clientPostRequest.setCookie( m_cookieStore.getCookies() );
      InputStream is = clientPostRequest.post();

      // read response
      if( is != null )
      {
        char[] buffer = new char[1024];
        InputStreamReader reader = new InputStreamReader( is, "iso-8859-1" );
        int n;
        while( (n = reader.read( buffer )) != -1 )
        {
          System.out.print( buffer/*, 0, n*/);
        }
      }
    } catch( IOException e )
    {
      logger.severe( e.getMessage() );
      disconnect();
      return false;
    }
    return true;
  }


  @Override
  public String getNewsRssUrl(String p_threadId)
  {
    return "http://" + FmpConstant.getForumHost() + "/feed?f=" + p_threadId;
  }


}
