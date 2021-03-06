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
 *  Copyright 2010 to 2016 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.pm.FmgMessage;
import com.fullmetalgalaxy.server.serialization.DriverSTAI;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * @author Vincent
 * 
 * this class send a end turn message to a given url and may use the response to play.
 */
public class WebHook implements DeferredTask
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static Logger logger = Logger.getLogger( WebHook.class.getName() );

  private long gameId = 0;
  private long accountId = 0;
  private int retryCount = 0;
  private long startTimeMillis = 0;
  private long startDelayMillis = 0;

  public String staiExtraStatements = null;


  public WebHook(long p_gameId, long p_accountId)
  {
    super();
    gameId = p_gameId;
    accountId = p_accountId;
  }

  public void start()
  {
    logger.info( "start processing web hook" );
    if( retryCount > 0 )
    {
      logger.info( "retry count = " + retryCount );
    }
    startTimeMillis = System.currentTimeMillis();
    QueueFactory.getDefaultQueue()
        .add(
        TaskOptions.Builder.withPayload( this ).header( "X-AppEngine-FailFast", "true" ) );
  }

  @Override
  public void run()
  {
    // wait before performing webhook request
    long waitingTime = System.currentTimeMillis() - startTimeMillis;
    if( waitingTime < startDelayMillis )
    {
      try
      {
        Thread.sleep( startDelayMillis - waitingTime );
      } catch( InterruptedException e )
      {
      }
      QueueFactory.getDefaultQueue().add(
          TaskOptions.Builder.withPayload( this ).header( "X-AppEngine-FailFast", "true" ) );
      return;
    }


    startTimeMillis = System.currentTimeMillis();
    startDelayMillis = 0;

    DriverSTAI driverStai = new DriverSTAI();
    HTTPResponse response = null;
    String payload = null;

    Game game = null;
    EbAccount account = null;
    try
    {
      game = FmgDataStore.dao().getGame( gameId );
      account = FmgDataStore.dao().get( EbAccount.class, accountId );
    } catch( Throwable th )
    {
      logger.log( Level.SEVERE, "unable to read game and account", th );
      return;
    }

    try
    {
      ModelFmpInit modelFmpInit = new ModelFmpInit();
      modelFmpInit.setGame( game );

      URL url = new URL( account.getWebHook() );

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      driverStai.saveGame( modelFmpInit, baos );
      payload = "id," + game.getId() + "\nyou," + game.getRegistrationByIdAccount( account.getId() ).getId() + ","
          + account.getId() + "," + account.getPseudo() + "," + account.getPassword() + "\nwebhookAnswerInResponse\n";
      if( staiExtraStatements != null )
      {
        payload += staiExtraStatements;
        payload += "\n";
      }
      payload += "\n";
      payload += baos.toString( "UTF-8" );
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder.withDefaults()
          .doNotFollowRedirects() );
      request.setHeader( new HTTPHeader( "Content-Type", "text/plain; charset=utf-8" ) );
      request.getFetchOptions().disallowTruncate();
      request.getFetchOptions().doNotFollowRedirects();
      request.getFetchOptions().doNotValidateCertificate();
      // maximum allowed by https://cloud.google.com/appengine/docs/java/outbound-requests#request_timeouts
      request.getFetchOptions().setDeadline( 60.0 );
      request.setPayload( payload.getBytes( "UTF-8" ) );

      
      response = URLFetchServiceFactory.getURLFetchService().fetch( request );
      
      
      if( response.getResponseCode() != 200 )
      {
        throw new Exception( "http post response status : " + response.getResponseCode() );
      }
      ByteArrayInputStream bis = new ByteArrayInputStream(response.getContent());
      
      // assume response is STAI
      ModelFmpUpdate requestUpdate = driverStai.loadGameUpdate( bis, "" + game.getId() );
      if( !requestUpdate.getGameEvents().isEmpty() )
      {
        GameServicesImpl service = new GameServicesImpl();
        service.runModelUpdate( requestUpdate );
      }
      else
      {
        logger.info( "web hook response didn't contain any action" );
      }

    } catch( Throwable th )
    {
      logger.log( Level.SEVERE, "webhook fail : account= " + account.getPseudo() + " url=" + account.getWebHook(), th );
      if( th instanceof RpcFmpException && ((RpcFmpException)th).getCauseEvent() != null )
      {
        logger.severe( ((RpcFmpException)th).getCauseEvent().toString() );
        logger.severe( ((RpcFmpException)th).getCauseEvent().getTransientComment() );
      }
      retryCount++;
      if( retryCount < 3 )
      {
        // wait between 2 and 10 seconds before performing a retry
        startDelayMillis = Math.round( 1000 * (2 + Math.random() * 8) );
        staiExtraStatements = "retry," + retryCount + "," + startDelayMillis + ", " + th.getMessage();
        this.start();
      }
      else
      {
        // send an error report to account
        StringBuffer body = new StringBuffer();
        body.append( "game= " + game.getName() + "\n" );
        body.append( "game url= http://www.fullmetalgalaxy.com/game.jsp?id=" + game.getId() + "\n" );
        body.append( "account= " + account.getPseudo() + "\n" );
        body.append( "webhook url= " + account.getWebHook() + "\n" );
        body.append( "webhook failed " + retryCount + " times\n" );
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy.MM.dd 'at' HH:mm:ss z" );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        body.append( "last request time= " + dateFormat.format( new Date( startTimeMillis ) ) + "\n" );
        body.append( "last request duration= " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " sec\n" );
        body.append( "last error= " + th.getMessage() + "\n" );
        if( th instanceof RpcFmpException && ((RpcFmpException)th).getCauseEvent() != null )
        {
          body.append( ((RpcFmpException)th).getCauseEvent().toString() );
          body.append( "\n" );
          body.append( ((RpcFmpException)th).getCauseEvent().getTransientComment() );
          body.append( "\n" );
        }
        body.append( "retry webhook= http://www.fullmetalgalaxy.com/AccountServlet?account=" + account.getId()
            + "&retrywebhook=" + game.getId() + "\n" );
        body.append( "--------------------------\n" );
        if( payload != null )
        {
          body.append( "POST data=\n" );
          body.append( payload );
          body.append( "--------------------------\n" );
        }
        if( response != null )
        {
          body.append( "last response code= " + response.getResponseCode() + "\n" );
          body.append( "last response content=\n" );
          try
          {
            body.append( new String( response.getContent(), getCharset( response ) ) );
            body.append( "\n" );
          } catch( UnsupportedEncodingException e )
          {
            logger.severe( e.getMessage() );
          }
          body.append( "--------------------------\n" );
        }
        FmgMessage errorMessage = new FmgMessage( "WebHook failed", body.toString() );
        errorMessage.sendEMail( account );
      }
    }
  }

  public void setStartDelayMillis(long p_startDelayMillis)
  {
    startDelayMillis = p_startDelayMillis;
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



}
