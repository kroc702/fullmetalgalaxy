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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;
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

  public final static Logger logger = Logger.getLogger( WebHook.class.getSimpleName() );

  private Game game = null;
  private EbAccount account = null;



  public WebHook(Game p_game, EbAccount p_account)
  {
    super();
    game = p_game;
    account = p_account;
  }

  public void start()
  {
    logger.info( "start processing web hook" );
    QueueFactory.getQueue( "longDBTask" ).add(
        TaskOptions.Builder.withPayload( this ).header( "X-AppEngine-FailFast", "true" ) );
  }

  @Override
  public void run()
  {
    long startTime = System.currentTimeMillis();

    DriverSTAI driverStai = new DriverSTAI();

    try
    {
      ModelFmpInit modelFmpInit = new ModelFmpInit();
      modelFmpInit.setGame( game );

      URL url = new URL( account.getWebHook() );

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      driverStai.saveGame( modelFmpInit, baos );
      String payload = "id," + game.getId() + "\nyou," + game.getRegistrationByIdAccount( account.getId() ).getId()
          + "," + account.getId() + "," + account.getPassword() + "\n\n";
      payload += baos.toString( "UTF-8" );
      HTTPRequest request = new HTTPRequest( url, HTTPMethod.POST, FetchOptions.Builder.withDefaults()
          .doNotFollowRedirects() );
      request.setPayload( payload.getBytes( "UTF-8" ) );

      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch( request );
      ByteArrayInputStream bis = new ByteArrayInputStream(response.getContent());
      
      // assume response is STAI
      ModelFmpUpdate requestUpdate = driverStai.loadGameUpdate( bis, "" + game.getId() );
      GameServicesImpl service = new GameServicesImpl();
      service.runModelUpdate( requestUpdate );

    } catch( Throwable th )
    {
      logger.severe( "webhook fail : account= " + account.getPseudo() + " url=" + account.getWebHook() + "\n"
          + th.getMessage() );
      if( th instanceof RpcFmpException && ((RpcFmpException)th).getCauseEvent() != null )
      {
        logger.severe( ((RpcFmpException)th).getCauseEvent().toString() );
        logger.severe( ((RpcFmpException)th).getCauseEvent().getTransientComment() );
      }
    }
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
