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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server.forum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.ressources.SharedI18n;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Query;

/**
 * create an html text from an RSS stream.
 * 
 * @author vlegendr
 *
 */
public class Games
{
  private static final int GAMES_ITEM_COUNT = 3;
  private static final String CACHE_NEWS_KEY = "CACHE_GAMES_KEY";
  private static final int CACHE_NEWS_TTL_SEC = 3600; // one hour
  
  
  /**
   * 
   * @return news in an HTML format ready to be included in JSP pages.
   */
  public static String getHtml()
  {
    String newsHtml = String.class.cast( getCache().get( CACHE_NEWS_KEY ) );
    if( newsHtml == null )
    {
      newsHtml = buildNewsHtml();
      getCache().put( CACHE_NEWS_KEY, newsHtml, Expiration.byDeltaSeconds( CACHE_NEWS_TTL_SEC ) );
    }
    
    return newsHtml;
  }
  
  
  /**
   * build news from RSS to an HTML format ready to be included in JSP pages.
   * @return 
   */
  private static String buildNewsHtml()
  {
    String newsHtml = "";
    int gameCount = 0;

    DateFormat dateFormat = new SimpleDateFormat( SharedI18n.getMisc( 0 ).dateFormat() );

    // find recently openened games
    Query<EbGamePreview> query = FmgDataStore.dao().query( EbGamePreview.class )
        .filter( "m_status", GameStatus.Open )
        .filter( "m_configGameTime in", ConfigGameTime.values() )
        .order( "-m_creationDate" ).limit( GAMES_ITEM_COUNT );

    for( EbGamePreview game : query )
    {
      // add a news entry
      newsHtml += "<a href='" + FmpConstant.getBaseUrl() + "/game.jsp?id=" + game.getId()
          + "'><div class='article'><article><span class='date'>"
          + dateFormat.format( game.getCreationDate() )
          // <h4> tag cause graphic glich on IE7
          + "</span><div class='h4'>" + game.getIconsAsHtml() + game.getName()
          + "</div></article></div></a>";
      gameCount++;
    }

    if( gameCount < GAMES_ITEM_COUNT )
    {
      // find recently openened games
      query = FmgDataStore.dao().query( EbGamePreview.class )
          .filter( "m_status in", new GameStatus[] { GameStatus.Running, GameStatus.Pause } )
          .filter( "m_configGameTime in", ConfigGameTime.values() )
          .order( "-m_creationDate" ).limit( GAMES_ITEM_COUNT - gameCount );

      for( EbGamePreview game : query )
      {
        // add a news entry
        newsHtml += "<a href='" + FmpConstant.getBaseUrl() + "/game.jsp?id=" + game.getId()
            + "'><div class='article'><article><span class='date'>"
            + dateFormat.format( game.getCreationDate() )
            // <h4> tag cause graphic glich on IE7
            + "</span><div class='h4'>" + game.getIconsAsHtml() + game.getName()
            + "</div></article></div></a>";
        gameCount++;
      }
    }
    
    return newsHtml;
  }
  
  
  private static MemcacheService s_cache = null;

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
