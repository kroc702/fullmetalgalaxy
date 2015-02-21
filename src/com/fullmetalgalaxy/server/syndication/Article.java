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

package com.fullmetalgalaxy.server.syndication;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.fullmetalgalaxy.model.ressources.SharedI18n;
import com.fullmetalgalaxy.server.LocaleFmg;

/**
 * @author Vincent Legendre
 *
 * A single article regardless where it where loaded from.
 * May be stored in database.
 */
public class Article implements Comparable<Article>
{
  private static final int MAX_PREVIEW_SIZE = 190;

  
  
  private String m_title = null;
  private String m_link = null;
  private String m_body = null;
  private Date m_pubDate = null;
  
  private LocaleFmg m_locale = null;
  private String m_author = null;
  private String m_iconUrl = null;
  private Set<String> m_tags = null; 
  
  @Override
  public int compareTo(Article p_arg0)
  {
    return -1 * m_pubDate.compareTo( p_arg0.getPubDate() );
  }
 
  /**
   * build a preview in html language
   * @param p_withDescription if true, add the beginning of article
   * @return
   */
  public void writePreviewAsHtml(boolean p_withDescription, PrintStream p_out)
  {
    DateFormat dateFormat = new SimpleDateFormat( SharedI18n.getMisc( 0 ).dateFormat() );
    
    String description = getBody();

    if( !p_withDescription )
    {
      description = "";
    }
    else
    {
      // cut description
      int charIndex = 0;
      int charCount = 0;
      while( charCount < MAX_PREVIEW_SIZE )
      {
        int index = description.indexOf( "<img", charIndex );
        if( index < 0 )
        {
          charIndex += MAX_PREVIEW_SIZE - charCount;
          charCount = MAX_PREVIEW_SIZE;
        }
        else
        {
          charIndex = description.indexOf( '>', index );
          charCount += index + 5;
        }
      }
      if( description.length() > charIndex )
      {
        description = description.substring( 0, charIndex ) + " ...";
      }
      description = "<p>" + description + "</p>";
    }

    // add a news entry
    p_out.println( "<a href='" + getLink() + "'><div class='article'><article><span class='date'>"
    + dateFormat.format( getPubDate() )
     // <h4> tag cause graphic glich on IE7
        + "</span><div class='h4'>" + getTitle() + "</div>" + description + "</article></div></a>" );
  }
  
  public void writeRssItem(PrintStream p_out)
  {
    
  }

  
  // getters and setters
  // ===================
  public String getTitle()
  {
    return m_title;
  }
  public void setTitle(String p_title)
  {
    m_title = p_title;
  }
  public String getLink()
  {
    return m_link;
  }
  public void setLink(String p_link)
  {
    m_link = p_link;
  }
  public String getBody()
  {
    return m_body;
  }
  public void setBody(String p_body)
  {
    m_body = p_body;
  }
  public Date getPubDate()
  {
    return m_pubDate;
  }
  public void setPubDate(Date p_pubDate)
  {
    m_pubDate = p_pubDate;
  }
  public LocaleFmg getLocale()
  {
    return m_locale;
  }
  public void setLocale(LocaleFmg p_locale)
  {
    m_locale = p_locale;
  }
  public String getAuthor()
  {
    return m_author;
  }
  public void setAuthor(String p_author)
  {
    m_author = p_author;
  }
  public String getIconUrl()
  {
    return m_iconUrl;
  }
  public void setIconUrl(String p_iconUrl)
  {
    m_iconUrl = p_iconUrl;
  }
  
 
  
  
}
