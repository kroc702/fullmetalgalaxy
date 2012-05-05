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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * @author Vincent Legendre
 *
 * Build Article from RSS feed
 */
public class ArticleFactory
{
  private static final DateFormat s_pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

  /**
   * concat several url into on single article list
   * @return
   */
  public static List<Article> createArticles()
  {
    
  }
  
  
  /**
   * build a list of article that come from a single url
   * @param p_url
   * @return
   */
  public static List<Article> createArticles(String p_url)
  {
    List<Article> listArticle = new ArrayList<Article>();
    for( Element item : fetchRssItem( p_url ) )
    {
      // TODO add thread info like author, icon, locale
      listArticle.add( createArticleFromRss( item ) );
    }
    return listArticle;
  }
  
  public static void writeRss(List<Article> p_articles, PrintStream p_out)
  {
    
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
    
    // read relevent informations
    // title, link, description, pubDate
    article.setTitle( p_item.getChild("title").getText() );
    article.setLink( p_item.getChild("link").getText() );
    article.setBody( p_item.getChild("description").getText() );
    Date pubDate = new Date();
    try
    {
      pubDate = s_pubDateFormat.parse(p_item.getChild("pubDate").getText());
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
