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

package com.fullmetalgalaxy.server.syndication;

import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.fullmetalgalaxy.server.LocaleFmg;


/**
 * @author Vincent Legendre
 *
 * Build Article from RSS feed
 */
public class ArticleFactory
{
  private static final DateFormat s_pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
  private static final DateFormat s_dcDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH );
  
  public static Logger logger = Logger.getLogger( "ArticleFactory" );

  private static Properties s_articlesProperties = new Properties();

  static
  {
    // try retrieve data from file
    try
    {
      s_articlesProperties.load( ArticleFactory.class.getResourceAsStream( "articles.properties" ) );
    } catch( Exception e )
    {
      logger.severe( e.getMessage() );
    }
  }

  /**
   * concat several url into on single article list
   * @return
   */
  public static List<Article> createArticles()
  {
    ArrayList<Article> listArticle = new ArrayList<Article>();
    int rssIndex = 0;
    
    // read all rss url
    String rssUrl = s_articlesProperties.getProperty( "rssUrl_" + rssIndex );
    while( rssUrl != null )
    {
      LocaleFmg locale = LocaleFmg.fromString( s_articlesProperties.getProperty( "locale_"
          + rssIndex ) );
      String author = s_articlesProperties.getProperty( "author_" + rssIndex );
      String iconUrl = s_articlesProperties.getProperty( "iconUrl_" + rssIndex );
      List<Article> tmpList = createArticles( rssUrl );
      for( Article article : tmpList )
      {
        article.setLocale( locale );
        article.setAuthor( author );
        article.setIconUrl( iconUrl );
      }
      listArticle.addAll( tmpList );

      // next rss
      rssIndex++;
      rssUrl = s_articlesProperties.getProperty( "rssUrl_" + rssIndex );
    }

    // sort according to date
    Collections.sort( listArticle );

    return listArticle;
  }
  
  
  /**
   * build a list of article that come from a single url
   * @param p_url
   * @return
   */
  private static List<Article> createArticles(String p_url)
  {
    List<Article> listArticle = new ArrayList<Article>();
    for( Element item : fetchRssItem( p_url ) )
    {
      listArticle.add( createArticleFromRss( item ) );
    }
    return listArticle;
  }
  
  public static void writeRss(List<Article> p_articles, PrintStream p_out)
  {
    // TODO all
  }
  
  
  /**
   * build an Article from a sax element found in an RSS file.
   * output Article won't be fully filled: it miss some information like author...
   * @param p_item
   * @return
   */
  private static Article createArticleFromRss(Element p_item)
  {
    Article article = new Article();
    if( p_item == null )
      return article;
    
    // read relevent informations
    // title, link, description, pubDate
    if( p_item.getChild( "title" ) != null )
      article.setTitle( p_item.getChild( "title" ).getText() );
    if( p_item.getChild( "link" ) != null )
      article.setLink( p_item.getChild( "link" ).getText() );
    if( p_item.getChild( "description" ) != null )
      article.setBody( p_item.getChild( "description" ).getText() );
    Date pubDate = new Date( 0 );
    try
    {
      if( p_item.getChild( "pubDate" ) != null )
        pubDate = s_pubDateFormat.parse( p_item.getChild( "pubDate" ).getText() );
      else if( p_item
          .getChild( "date", Namespace.getNamespace( "http://purl.org/dc/elements/1.1/" ) ) != null )
        pubDate = s_dcDateFormat.parse( p_item.getChild( "date",
            Namespace.getNamespace( "http://purl.org/dc/elements/1.1/" ) ).getText() );
    } catch( ParseException e )
    {
      e.printStackTrace();
    } catch( IllegalArgumentException e )
    {
      e.printStackTrace();
    }
    article.setPubDate( pubDate );
    return article;
  }

  
  @SuppressWarnings("unchecked")
  private static List<Element> fetchRssItem(String p_url)
  {
    List<Element> listItem = new ArrayList<Element>();
      
    org.jdom.Document document;
    // On crée une instance de SAXBuilder
    SAXBuilder sxb = new SAXBuilder();
    try
    {
      URL url = new URL( p_url );
      // On crée un nouveau document JDOM avec en argument le fichier XML
      // Le parsing est terminé ;)
       document = sxb.build( url.openStream() );
  
      // On initialise un nouvel élément racine avec l'élément racine du
      // document.
      Element racine = document.getRootElement();
  
      // On crée une List contenant tous les noeuds "etudiant" de l'Element
      // racine
      listItem = racine.getChild("channel").getChildren("item");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return listItem;
  }


}
