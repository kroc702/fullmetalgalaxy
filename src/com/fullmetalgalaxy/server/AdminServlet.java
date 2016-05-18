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
package com.fullmetalgalaxy.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.cache.CacheStatistics;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.server.forum.ConectorImpl;
import com.fullmetalgalaxy.server.pm.FmgMessage;

/**
 * @author Vincent
 * 
 * this servlet can perform various action over http interface.
 */
public class AdminServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;
  private final static FmpLogger log = FmpLogger.getLogger( AdminServlet.class.getName() );

  /**
   * 
   */
  public AdminServlet()
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    String strid = null;

    // delete account
    // ==============
    strid = p_req.getParameter( "deleteaccount" );
    if( strid != null )
    {
      FmgDataStore dataStore = new FmgDataStore( false );
      dataStore.delete( EbAccount.class, Long.parseLong( strid ) );
      dataStore.close();
      p_resp.sendRedirect( "/halloffames.jsp" );
    }

    // pull account from forum
    // =======================
    strid = p_req.getParameter( "pullaccount" );
    if( strid != null )
    {
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        if( ServerUtil.forumConnector().pullAccount( account ) )
        {
          ds.put( account );
          p_resp.sendRedirect( "/account.jsp?id="+account.getId() );
        }
        else
        {
          p_resp.getOutputStream().println( "pullAccount failed" );
        }
      }
      else
      {
        p_resp.getOutputStream().println( "account " + strid + " not found" );
      }
      ds.close();
    }

    // push account to forum
    // =======================
    strid = p_req.getParameter( "pushaccount" );
    if( strid != null )
    {
      EbAccount account = FmgDataStore.dao().find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        if( ServerUtil.forumConnector().pushAccount( account ) )
        {
          p_resp.sendRedirect( "/account.jsp?id="+account.getId() );
        }
        else
        {
          p_resp.getOutputStream().println( "pushAccount failed" );
        }
      }
      else
      {
        p_resp.getOutputStream().println( "account " + strid + " not found" );
      }
    }

    // link forum account
    // ==================
    strid = p_req.getParameter( "linkaccount" );
    if( strid != null )
    {
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        String forumId = ServerUtil.forumConnector().getUserId( account.getPseudo() );
        if( forumId == null )
        {
          p_resp.getOutputStream().println(
              "username " + account.getPseudo() + " not found on forum" );
        }
        else
        {
          account.setForumId( forumId );
          account.setIsforumIdConfirmed( true );
          ds.put( account );
          p_resp.sendRedirect( "/account.jsp?id=" + account.getId() );
        }
      }
      else
      {
        p_resp.getOutputStream().println( "account " + strid + " not found" );
      }
      ds.close();
    }

    // send a test private message
    // ===========================
    strid = p_req.getParameter( "testpm" );
    if( strid != null )
    {
      EbAccount account = FmgDataStore.dao().find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        if( new FmgMessage( "test" ).send( account ) )
        {
          p_resp.sendRedirect( "/account.jsp?id=" + account.getId() );
        }
        else
        {
          p_resp.getOutputStream().println( "PM failed" );
        }
      }
    }


    // send a link private message
    // ===========================
    strid = p_req.getParameter( "linkpm" );
    if( strid != null )
    {
      EbAccount account = FmgDataStore.dao().find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        if( new FmgMessage( "linkAccount" ).sendPM( account ) )
        {
          p_resp.sendRedirect( "/account.jsp?id=" + account.getId() );
        }
        else
        {
          p_resp.getOutputStream().println( "PM failed" );
        }
      }
    }


    // create forum account
    // ====================
    strid = p_req.getParameter( "createforumaccount" );
    if( strid != null )
    {
      FmgDataStore ds = new FmgDataStore( false );
      EbAccount account = ds.find( EbAccount.class, Long.parseLong( strid ) );
      if( account != null )
      {
        String forumId = ServerUtil.forumConnector().getUserId( account.getPseudo() );
        if( forumId != null )
        {
          p_resp.getOutputStream().println( "username " + account.getPseudo() + " exist on forum" );
        }
        else
        {
          if( ServerUtil.forumConnector().createAccount( account ) )
          {
            account.setIsforumIdConfirmed( true );
            ds.put( account );
            p_resp.sendRedirect( "/account.jsp?id=" + account.getId() );
          }
          else
          {
            p_resp.getOutputStream().println( "createAccount failed");
          }
        }
      }
      else
      {
        p_resp.getOutputStream().println( "account " + strid + " not found" );
      }
      ds.close();
    }

    // post a new game on forum (test)
    // ===============================
    strid = p_req.getParameter( "forumpostgame" );
    if( strid != null )
    {
      FmgMessage msg = FmgMessage.buildMessage( LocaleFmg.getDefault(), "forumPostGame" );
      msg.putParam( "game_name", "Tutorial" );
      msg.putParam( "game_description",
          "La première partie que vous pouvez faire pour vous familiariser avec les règles (environ 15min)" );
      msg.putParam( "game_url",
          "http://www.fullmetalgalaxy.com/game.jsp?id=/puzzles/tutorial/model.bin" );
      msg = msg.applyParams();

      // ServerUtil.newsConnector().postNews(
      // ConectorImpl.FORUM_GAMES_THREAD_ID, msg.getSubject(),
      // msg.getBody() );
      ServerUtil.newsConnector().postNews( ConectorImpl.FORUM_GAMES_THREAD_ID, "ezrtret", "erztre" );
    }

    // download game
    // =============
    strid = p_req.getParameter( "downloadgame" );
    if( strid != null )
    {
      // load game with standard api
      ModelFmpInit modelInit = GameServicesImpl.sgetModelFmpInit( p_req, p_resp, strid );
      if( modelInit.getGame().getAdditionalEventCount() > 0 )
      {
        // load additional events
        EbGameLog gameLog = GameServicesImpl.sgetAdditionalGameLog( modelInit.getGame().getId() );
        gameLog.getLog().addAll( modelInit.getGame().getLogs() );
        modelInit.getGame().setLogs( gameLog.getLog() );
      }
      if( modelInit != null )
      {
        ObjectOutputStream out = new ObjectOutputStream( p_resp.getOutputStream() );
        out.writeObject( modelInit );
      }
    }

    // delete session from datastore
    // =============================
    strid = p_req.getParameter( "deletesession" );
    if( strid != null )
    {
      // TODO
      p_resp.getOutputStream().println( "TODO" );
    }

    // delete cache
    // ============
    strid = p_req.getParameter( "deletecache" );
    if( strid != null )
    {
      try
      {
        clearCache();
        p_resp.getOutputStream().println( "delete cache Succeed" );
      } catch( CacheException e )
      {
        e.printStackTrace( p_resp.getWriter() );
      }
    }

    // recompute stats
    // ===============
    strid = p_req.getParameter( "recomputestats" );
    if( strid != null )
    {
      int currentGameCount = FmgDataStore.dao().query( EbGamePreview.class )
          .filter( "m_status in",
              new GameStatus[] { GameStatus.Running, GameStatus.Pause, GameStatus.Open } ).count();
      GlobalVars.setCurrentGameCount( currentGameCount );

      RecomputeStats.start();
      p_resp.getOutputStream().println( "aborted & deleted game count can't be recomputed <br/>" );
      p_resp.getOutputStream().println( "recompute stats task launched..." );
    }

    // rebuild game index
    // ==================
    strid = p_req.getParameter( "rebuildstats" );
    if( strid != null )
    {
      RecomputeStats.startRebuildGameIndex();
      p_resp.getOutputStream().println( "rebuild index stats task launched..." );
    }


  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    Map<String, String> params = new HashMap<String, String>();
    ModelFmpInit modelInit = null;

    try
    {
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          params.put( item.getFieldName(), Streams.asString( item.openStream(), "UTF-8" ) );
        }
        else if( item.getFieldName().equalsIgnoreCase( "gamefile" ) )
        {
          ObjectInputStream in = new ObjectInputStream( item.openStream() );
          modelInit = ModelFmpInit.class.cast( in.readObject() );
          in.close();
        }
      }
    } catch( FileUploadException e )
    {
      log.error( e );
    } catch( ClassNotFoundException e2 )
    {
      log.error( e2 );
    }

    // import game from file
    if( modelInit != null )
    {
      // set transient to avoid override data
      modelInit.getGame().setTrancient();

      // search all accounts in database to correct ID
      for( EbRegistration registration : modelInit.getGame().getSetRegistration() )
      {
        if( registration.haveAccount() )
        {
          EbAccount account = FmgDataStore.dao().find( EbAccount.class,
              registration.getAccount().getId() );
          if( account == null )
          {
            // corresponding account from this player doesn't exist in database
            try
            {
              // try to find corresponding pseudo
              account = FmgDataStore.dao().query( EbAccount.class )
                  .filter( "m_compactPseudo ==",
                      ServerUtil.compactTag( registration.getAccount().getPseudo() ) ).get();
            } catch( Exception e )
            {
            }
          }
          registration.setAccount( account );
        }
      }
      
      // then save game
      FmgDataStore dataStore = new FmgDataStore( false );
      dataStore.put( modelInit.getGame() );
      dataStore.close();

    }

  }


  private void clearCache() throws CacheException
  {
    CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
    Cache cache = cacheFactory.createCache( Collections.emptyMap() );
    CacheStatistics stats = cache.getCacheStatistics();
    log.info( "Clearing " + stats.getObjectCount() + " objects in cache" );
    cache.clear();
  }



}
