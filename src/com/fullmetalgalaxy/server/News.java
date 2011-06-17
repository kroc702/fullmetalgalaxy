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
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * create an html text from an RSS stream.
 * 
 * @author vlegendr
 *
 */
public class News
{
  private static final String NEWS_RSS_URL = "http://fmptest.forumgratuit.fr/feed?f=3";
  private static final int NEWS_ITEM_COUNT = 5;
  private static final String CACHE_NEWS_KEY = "CACHE_NEWS_KEY";
  private static final int CACHE_NEWS_TTL_SEC = 3600; // one hour
  
  private static final DateFormat s_pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

 
  
  
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
    
    int itemCount = 0;
    for( Element item : getNewsItem() )
    {
      // for each item:
      // read relevent informations
      // title, link, description, pubDate
      String title = item.getChild("title").getText();
      String link = item.getChild("link").getText();
      String description = item.getChild("description").getText();
      Date pubDate = new Date();
      try
      {
        pubDate = s_pubDateFormat.parse(item.getChild("pubDate").getText());
      } catch( ParseException e )
      {
        e.printStackTrace();
      }

      // add a news entry
      newsHtml += "<p><a href="+link+">"+title+"</a><br/>"+description+"</p>";
        
      // and stop if we've got enough
      itemCount++;
      if( itemCount >= NEWS_ITEM_COUNT)
      {
        break;
      }
    }
    
    return newsHtml;
  }
  

  @SuppressWarnings("unchecked")
  private static List<Element> getNewsItem()
  {
    List<Element> listEtudiants = new ArrayList<Element>();
      
    org.jdom.Document document;
    //On crée une instance de SAXBuilder
    SAXBuilder sxb = new SAXBuilder();
    try
    {
      URL url = new URL( NEWS_RSS_URL );
       //On crée un nouveau document JDOM avec en argument le fichier XML
       //Le parsing est terminé ;)
       document = sxb.build( url.openStream() );
  
      //On initialise un nouvel élément racine avec l'élément racine du document.
      Element racine = document.getRootElement();
  
      //On crée une List contenant tous les noeuds "etudiant" de l'Element racine
      listEtudiants = racine.getChild("channel").getChildren("item");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return listEtudiants;
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
