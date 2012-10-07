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

package com.fullmetalgalaxy.server.forum;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.fullmetalgalaxy.server.syndication.Article;
import com.fullmetalgalaxy.server.syndication.ArticleFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * create an html text from an RSS stream.
 * 
 * @author Vincent Legendre
 *
 */
public class News
{
  private static final int NEWS_FULLITEM_COUNT = 1;
  private static final int NEWS_ITEM_COUNT = 3;
  private static final String CACHE_NEWS_KEY = "CACHE_NEWS_KEY";
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
    int itemCount = 0;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out;
    try
    {
      out = new PrintStream( baos, false, "UTF-8" );
    } catch( UnsupportedEncodingException e1 )
    {
      out = new PrintStream( baos );
    }
    for( Article article : ArticleFactory.createArticles() )
    {
      article.writePreviewAsHtml( itemCount < NEWS_FULLITEM_COUNT, out );

      // and stop if we've got enough
      itemCount++;
      if( itemCount >= NEWS_ITEM_COUNT)
      {
        break;
      }
    }
    try
    {
      return baos.toString( "UTF-8" );
    } catch( UnsupportedEncodingException e )
    {
      e.printStackTrace();
      return baos.toString();
    }
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
