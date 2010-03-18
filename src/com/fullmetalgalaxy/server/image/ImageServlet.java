package com.fullmetalgalaxy.server.image;

/*
 * ImageServlet
 *
 * Copyright (c) 2000 Ken McCrary, All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * KEN MCCRARY MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. KEN MCCRARY
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.server.CacheKey;
import com.fullmetalgalaxy.server.ServicesImpl;
import com.fullmetalgalaxy.server.CacheKey.CacheKeyType;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


/**
 *  Simple servlet to use with Image I/O producer
 *  QueryString should be the name of a class
 *  implementing ImageProducer
 */
public class ImageServlet extends HttpServlet
{
  static final long serialVersionUID = 555;
  private final static int CACHE_IMAGES_TTL_SEC = 7 * 24 * 3600; // one week
  // private final static int CACHE_TTL_BEFORE_RENEW_SEC = 24 * 3600; // 24h

  // private MiniMapProducer m_miniMapProducer = null;
  private static MemcacheService s_cache = null;



  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
      ServletException
  {
    try
    {
      String gameid = request.getParameter( "minimap" );

      String type = createMinimap( response.getOutputStream(), gameid );

      response.setContentType( type );
    } catch( Exception e )
    {
      throw new ServletException( e );
    }
  }

  public String createMinimap(OutputStream p_stream, String p_gameId) throws IOException
  {
    // get param
    long gameId = 0;
    if( p_gameId != null )
    {
      try
      {
        gameId = Long.parseLong( p_gameId );
      } catch( NumberFormatException e )
      {
      }
    }
    CacheKey key = null;
    if( gameId == 0 )
    {
      key = new CacheKey( CacheKeyType.Image, p_gameId );
    }
    else
    {
      key = new CacheKey( CacheKeyType.Image, gameId );
    }

    // load image from cache
    byte[] data = (byte[])getCache().get( key );
    if( data == null )
    {
      // cache is empty, then load game from datastore
      FmgDataStore dataStore = new FmgDataStore();
      EbGame model = null;
      if( gameId != 0 )
      {
        model = dataStore.getGame( gameId );
      }
      else if( p_gameId != null && !p_gameId.equals( "0" ) )
      {
        ModelFmpInit modelInit = ServicesImpl.sgetModelFmpInit( p_gameId );
        model = modelInit.getGame();
      }

      // create image as raw data from game
      MiniMapProducer miniMapProducer = new MiniMapProducer(
          getServletContext().getRealPath( "/" ), model );
      data = miniMapProducer.getImage();

      // put raw data to cache for future reuse
      getCache().put( key, data, Expiration.byDeltaSeconds( CACHE_IMAGES_TTL_SEC ) );
    }

    // finally write data to stream
    assert data != null;
    p_stream.write( data );

    return MiniMapProducer.getImageType();
  }

  /**
   * @return the s_cache
   */
  private static MemcacheService getCache()
  {
    if( s_cache == null )
    {
      s_cache = MemcacheServiceFactory.getMemcacheService();
    }
    return s_cache;
  }



}
