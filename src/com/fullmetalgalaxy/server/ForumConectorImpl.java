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
package com.fullmetalgalaxy.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.myjavatools.web.ClientHttpRequest;


/**
 * TODO removed many hard coded constant and password !!!
 * 
 * I used firefox livehttpheaders extension to help constructing this
 */ 
public class ForumConectorImpl implements ForumConector
{
  private final static FmpLogger log = FmpLogger.getLogger( ForumConectorImpl.class.getName() );
  private static String COOKIE_SID = "fa_" + FmpConstant.getForumHost().replace( '.', '_' ) + "_sid";
  private static String FORUM_USERNAME = "";
  private static String FORUM_PASS = "";
  
  private FmgCookieStore m_cookieStore = new FmgCookieStore();
  
  

  private static Properties s_forumConfig = new Properties();

  static
  {
    // try retrieve data from file
    try
    {
      s_forumConfig.load( ForumConectorImpl.class.getResourceAsStream( "forumaccount.properties" ) );
      FORUM_USERNAME = s_forumConfig.getProperty( "username" );
      FORUM_PASS = s_forumConfig.getProperty( "password" );
    } catch( Exception e )
    {
      log.error( e );
    }
  }


  protected boolean isConnected()
  {
    return (m_cookieStore.getCookie( COOKIE_SID ) != null);
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
      url = new URL( "http://" + FmpConstant.getForumHost() + "/login" );
    
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, 
          FetchOptions.Builder.withDefaults().doNotFollowRedirects() ); 
      request.setPayload( ("username=" + FORUM_USERNAME + "&password=" + FORUM_PASS 
          + "&redirect=&query=&login=Connexion").getBytes( "UTF-8" ) );
      request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
      //request.addHeader( new HTTPHeader( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 (CK-Ifremer) Firefox/3.0 ( .NET CLR 3.5.30729; .NET4.0E)" ) );
      //request.addHeader( new HTTPHeader( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" ) );
      //request.addHeader( new HTTPHeader( "Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3" ) );
      ///request.addHeader( new HTTPHeader( "Accept-Encoding", "gzip,deflate" ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      //request.addHeader( new HTTPHeader( "Keep-Alive", "300" ) );
      //request.addHeader( new HTTPHeader( "Connection", "keep-alive" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/login" ) );
      //request.addHeader( new HTTPHeader( "Cookie", "extendedview=; __utma=197449400.1261798819.1301581035.1301581035.1301581035.1; __utmz=197449400.1301581035.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _csuid=X8b6019a8aa1ad" ) );
      //request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );
  
      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );
    
      //System.out.println( "response code: "+ response.getResponseCode() );
      //System.out.println( "final url: "+ response.getFinalUrl() );
      for( HTTPHeader header : response.getHeaders() )
      {
        //System.out.println( header.getName() + ": " + header.getValue() );
        if( "Set-Cookie".equalsIgnoreCase( header.getName() ) )
        {
          m_cookieStore.add( header.getValue() );
        }
      }
    } catch( IOException e )
    {
      log.error( e );
    }
  }


  @Override
  public String getUserId(String p_pseudo)
  {
    // we don't need to be connected
    
    Pattern pattern = Pattern.compile(".*<td class=\"row1\" .* href=\"/u(.*)\"><span style=\"color:#E.....\"><strong>"
        + Pattern.quote(p_pseudo) + "</strong></span></a></span></td>.*", Pattern.CASE_INSENSITIVE);
    
    try {
      URL url = new URL( "http://" + FmpConstant.getForumHost() + "/memberlist?username="
          + URLEncoder.encode( p_pseudo, "UTF-8" ) );
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      
      StringBuffer page = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        page.append( line );
      }
      reader.close();

      Matcher matcher = pattern.matcher( page);
      if( matcher.matches() )
      {
        return matcher.group( 1 );
      }
      
    } catch (IOException e) {
      log.error( e );
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
      char code = (char)Integer.parseInt( ""+s.charAt(n) );
      //System.out.println( "code="+(int)code );
      String s1 = s.substring(0,n);
      s1 += s.substring(n+1, s.length());
      //System.out.println( s );
      //System.out.println( s1 );
      s1 = URLDecoder.decode( s1, "UTF-8" );
      // not sure why this... 
      // maybe javascript escape don't behave the same as URLDecoder
      s1 = s1.replace( ' ', (char)('%'+code) );
      //System.out.println( s1 );
      
      StringBuffer t = new StringBuffer();
      
      for(int i=0; i<s1.length(); i++)
      {
        t.append( (char)(s1.charAt(i)-code) );
      }
      
      decrypted = URLDecoder.decode(t.toString(), "UTF-8");
      //System.out.println( t.toString() );
      //System.out.println();
      //System.out.println("===========================");
    } catch( UnsupportedEncodingException e )
    {
      log.error( e );
    }
    return decrypted;
  }

 

  @Override
  public void createAccount(EbAccount p_account)
  {
    // we don't need to be connected
    
    // first request: send username, email and password
    // ================================================
    FmgCookieStore cookieStore = new FmgCookieStore();
    
    try
    {
      URL url = new URL( "http://" + FmpConstant.getForumHost() + "/register?agreed=true&step=2" );
      String payload = "username="+p_account.getPseudo()
                      +"&email="+p_account.getEmail()
                      +"&password="+p_account.getPassword()
                      +"&submit=Enregistrer";
      payload = URLEncoder.encode( payload, "UTF-8" );
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder.withDefaults().doNotFollowRedirects() ); 
      request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/register?agreed=true&step=2" ) );
      request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );
      request.setPayload( payload.getBytes("UTF-8") );
      
      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );
      
      //System.out.println( "response code: "+ response.getResponseCode() );
      //System.out.println( "final url: "+ response.getFinalUrl() );
      for( HTTPHeader header : response.getHeaders() )
      {
        //System.out.println( header.getName() + ": " + header.getValue() );
        if( "Set-Cookie".equalsIgnoreCase( header.getName() ) )
        {
          cookieStore.add( header.getValue() );
        }
      }
      
      // read auth variable used by forum for security
      // =============================================
      Pattern pattern = Pattern.compile( ".*<input type=\"hidden\" name=\"confirm_pass\" value=(.+) />"
          +".*addHiddenFields\\('form_confirm', \\{'auth\\[\\]':\\[\\['(.+)',(.+)\\],\\['(.+)',(.+)\\]\\]\\}\\);\\}.*" );
      String confirm_pass = "";
      String auth1 = "";
      String auth2 = "";
      String page = new String( response.getContent(), "UTF-8" );
      Matcher matcher = pattern.matcher( page);
      if( matcher.matches() )
      {
        confirm_pass = matcher.group( 1 );
        auth1 = decrypt( matcher.group( 2 ) , Integer.parseInt(matcher.group( 3 )) );
        auth2 = decrypt( matcher.group( 4 ) , Integer.parseInt(matcher.group( 5 )) );
      }
        
      
      // second request: confirm password
      // ================================
      payload = "auth[]=" + auth1
      		    +"&auth[]=" + auth2
      		    +"&password_confirm=" + p_account.getPassword()
      		    +"&username=" +p_account.getPseudo()
      		    +"&email=" +p_account.getEmail()
      		    +"&password=" + p_account.getPassword()
      		    +"&confirm_pass=" + confirm_pass
      		    +"&submit=Enregistrer";
      payload = URLEncoder.encode( payload, "UTF-8" );
      // in theory we should parse response to get second url. but it's the same.
      request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder.withDefaults().doNotFollowRedirects() ); 
      request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
      request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
      request.addHeader( new HTTPHeader( "Referer", "http://" + FmpConstant.getForumHost()
          + "/register?agreed=true&step=2" ) );
      request.addHeader( new HTTPHeader( "Content-Type", "application/x-www-form-urlencoded" ) );
      request.setPayload( payload.getBytes("UTF-8") );
      
      response = URLFetchServiceFactory.getURLFetchService().fetch( request );
      
    } catch( Exception e )
    {
      log.error( e );
    }
  }


  @Override
  public void pushAccount(EbAccount p_account)
  {
    // we need to be connected as admin
    connect();
    
    try
    {
      URL url = new URL( "http://" + FmpConstant.getForumHost()
          + "/admin/index.forum?part=users_groups&sub=users&mode=edit&u="
          + p_account.getForumId()+"&extended_admin=1&sid="+m_cookieStore.getCookie( COOKIE_SID ) );

      ClientHttpRequest clientPostRequest = null;
      clientPostRequest = new ClientHttpRequest( url );
      //clientPostRequest.setParameter( "username_edit", "kroc" );
      //clientPostRequest.setParameter( "email", "vincent.legendre@gmail.com" );
      //clientPostRequest.setParameter( "password", "" );
      //clientPostRequest.setParameter( "password_confirm", "" );
      
      // a list of many parameters
      // TODO
      clientPostRequest.setParameter( "profile_field_2_-20", "test FMG 3 !!!" );
      
      
      clientPostRequest.setParameter( "submit", "Enregistrer" );
      clientPostRequest.setParameter( "mode", "save" );
      clientPostRequest.setParameter( "agreed", "true" );
      clientPostRequest.setParameter( "id", p_account.getForumId() );
      
      clientPostRequest.setCookie( m_cookieStore.getCookies() );
      clientPostRequest.post();
    } catch( IOException e )
    {
      log.error( e );
    }
  }


  @Override
  public void pullAccount(EbAccount p_account)
  {
    // this light version don't have to be connected

    if( p_account.getForumId() == null )
    {
      // no forum account
      return;
    }

    try
    {
      // we should have all we want on this page
      URL url = new URL( p_account.getProfileUrl() );
      BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );

      StringBuffer page = new StringBuffer();
      String line;
      while( (line = reader.readLine()) != null )
      {
        page.append( line );
      }
      reader.close();

      Pattern pattern = Pattern
          .compile( ".*<span class=\"gen\">Avatar:......</span></td><td width=\"80.\"><b><span class=\"gen\"><img src=\"(.*)\" alt=\"\" /></span></b>.*" );
      // Pattern pattern = Pattern.compile( ".*" );
      Matcher matcher = pattern.matcher( page );
      if( matcher.matches() )
      {
        String avatarUrl = matcher.group( 1 );
        if( avatarUrl != null )
        {
          p_account.setForumAvatarUrl( avatarUrl );
        }
      }

    } catch( IOException e )
    {
      log.error( e );
    }
  }

  /*
    @Override
    public void pullAccount(EbAccount p_account)
    {
      // we need to be connected as admin
      connect();
      
      try
      {
        // we should have all we want on this page
        URL url = new URL( "http://" + FmpConstant.getForumHost()
            + "/admin/index.forum?part=users_groups&sub=users&mode=edit&u="
            + p_account.getForumId()+"&extended_admin=1&sid="+m_cookieStore.getCookie( COOKIE_SID ) );
        
        HTTPRequest request = new HTTPRequest( url ); 
        request.addHeader( new HTTPHeader( "Host", FmpConstant.getForumHost() ) );
        request.addHeader( new HTTPHeader( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" ) );
        request.addHeader( new HTTPHeader( "Cookie", m_cookieStore.getCookies().toString() ) );
        
        HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );
        Pattern pattern = Pattern.compile( ".*" );
        String page = new String( response.getContent(), "UTF-8" );
        Matcher matcher = pattern.matcher( page);
        if( matcher.matches() )
        {
          // TODO
        }
        
      } catch( IOException e )
      {
        log.error( e );
      }
    }
  */

  @Override
  public void sendPMessage( String p_subject, String p_body, String ... p_usernames )
  {
    // we need to be connected as admin
    connect();
    
    try
    {
      URL url = new URL( "http://" + FmpConstant.getForumHost() + "/privmsg?" );
      
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
      clientPostRequest.setParameter( "new_pm_time", ""+(System.currentTimeMillis()/1000) );
      clientPostRequest.setParameter( "post", "Envoyer" );

      
      clientPostRequest.setCookie( m_cookieStore.getCookies() );
      clientPostRequest.post();
      
    } catch( IOException e )
    {
      log.error( e );
    } 
  }


}
