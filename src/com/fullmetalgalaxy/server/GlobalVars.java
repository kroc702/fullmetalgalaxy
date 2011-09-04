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

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Query;

/**
 * @author vlegendr
 *
 */
public class GlobalVars extends GlobalVarBase
{
  private static final String CACHE_STATS_KEY = "CACHE_STATS_KEY";
  private static final int CACHE_STATS_TTL_SEC = 3600; // one hour

  /**
   * 
   * @return stats for index page in an HTML format ready to be included in JSP pages.
   */
  public static String getStatsHtml()
  {
    String newsHtml = String.class.cast( getCache().get( CACHE_STATS_KEY ) );
    if( newsHtml == null )
    {
      newsHtml = "Joueurs: "+getAccountCount() +" ("+getActiveAccount()+" actifs)<br/>";
      newsHtml += "Parties: "+getTotalGameCount() + " ("+(getOpenGameCount() + getRunningGameCount()) +" en cours)<br/>";
      // for debug
      /*newsHtml += "<br/>open: "+getOpenGameCount() + " <br/>";
      newsHtml += "running: "+getRunningGameCount() + " <br/>";
      newsHtml += "finished: "+getFinishedGameCount() + " <br/>";
      newsHtml += "aborted: "+getAbortedGameCount() + " <br/>";
      newsHtml += "deletec: "+getDeletedGameCount() + " <br/>";
      */
      // get best player
      newsHtml += "Meilleur joueur:";
      Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);
      accountQuery = accountQuery.order( "-m_currentLevel" ).limit( 1 );
      for(EbAccount account : accountQuery.fetch() )
      {
        newsHtml += "<a href='"+account.getProfileUrl()+"'><table width='100%'><tr>";
        newsHtml += "<td><img src='"+account.getAvatarUrl()+"' height='40px'/></td>";
        newsHtml += "<td>"+account.getPseudo()+"<br/><img src='"+account.getGradUrl()+"'/></td>";
        newsHtml += "<td>"+account.getCurrentLevel()+" Pts</td>";
        newsHtml += "</tr></table></a>";
      }
      
      getCache().put( CACHE_STATS_KEY, newsHtml, Expiration.byDeltaSeconds( CACHE_STATS_TTL_SEC ) );
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

  
  public static int getTotalGameCount()
  {
    return getOpenGameCount() + getRunningGameCount() + getFinishedGameCount() + getAbortedGameCount();
  }
  
  
  
  
  
  
  public static int getAccountCount()
  {
    return getInt("AccountCount");
  }

  public static void setAccountCount(int p_accountCount)
  {
    put("AccountCount",p_accountCount);
  }


  public static int getActiveAccount()
  {
    return getInt( "ActiveAccount" );
  }

  public static void setActiveAccount(int p_accountCount)
  {
    put( "ActiveAccount", p_accountCount );
  }


  public static int getActiveOpenGameCount()
  {
    return getInt("ActiveOpenGameCount");
  }

  public static void setActiveOpenGameCount(int p_accountCount)
  {
    put("ActiveOpenGameCount",p_accountCount);
  }


  public static int getMaxLevel()
  {
    return getInt("MaxLevel");
  }

  public static void setMaxLevel(int p_accountCount)
  {
    put("MaxLevel",p_accountCount);
  }


  public static int getAbortedGameCount()
  {
    return getInt( "AbortedGameCount" );
  }

  public static void setAbortedGameCount(int p_accountCount)
  {
    put( "AbortedGameCount", p_accountCount );
  }

  public static int incrementAbortedGameCount(int p_toAdd)
  {
    return increment( "AbortedGameCount", p_toAdd );
  }


  public static int getFinishedGameCount()
  {
    return getInt( "FinishedGameCount" );
  }

  public static void setFinishedGameCount(int p_accountCount)
  {
    put( "FinishedGameCount", p_accountCount );
  }

  public static int incrementFinishedGameCount(int p_toAdd)
  {
    return increment( "FinishedGameCount", p_toAdd );
  }


  public static int getOpenGameCount()
  {
    return getInt("OpenGameCount");
  }

  public static void setOpenGameCount(int p_accountCount)
  {
    put("OpenGameCount",p_accountCount);
  }

  public static int incrementOpenGameCount(int p_toAdd)
  {
    return increment( "OpenGameCount", p_toAdd );
  }


  public static int getRunningGameCount()
  {
    return getInt( "RunningGameCount" );
  }

  public static void setRunningGameCount(int p_accountCount)
  {
    put( "RunningGameCount", p_accountCount );
  }

  public static int incrementRunningGameCount(int p_toAdd)
  {
    return increment( "RunningGameCount", p_toAdd );
  }


  public static int getDeletedGameCount()
  {
    return getInt( "DeletedGameCount" );
  }

  public static int incrementDeletedGameCount(int p_toAdd)
  {
    return increment( "DeletedGameCount", p_toAdd );
  }


}
