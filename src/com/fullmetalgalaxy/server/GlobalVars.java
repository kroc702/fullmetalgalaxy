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

import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
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
        newsHtml += account.buildHtmlFragment();
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
    return (int)increment( "AbortedGameCount", p_toAdd );
  }


  public static int getFinishedGameCount()
  {
    return getInt( "FinishedGameCount" );
  }

  public static void setFinishedGameCount(int p_accountCount)
  {
    put( "FinishedGameCount", p_accountCount );
  }

  /**
   * TODO remove this stat as it is included in FGameNbConfigGameTime
   * @param p_toAdd
   * @return
   */
  public static int incrementFinishedGameCount(int p_toAdd)
  {
    return (int)increment( "FinishedGameCount", p_toAdd );
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
    return (int)increment( "OpenGameCount", p_toAdd );
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
    return (int)increment( "RunningGameCount", p_toAdd );
  }


  public static int getDeletedGameCount()
  {
    return getInt( "DeletedGameCount" );
  }

  public static int incrementDeletedGameCount(int p_toAdd)
  {
    return (int)increment( "DeletedGameCount", p_toAdd );
  }

  // =================================
  // finished games stats
  // =================================
  public static int getFGameNbPlayer()
  {
    return getInt( "FGameNbPlayer" );
  }
  public static void setFGameNbPlayer(int p_value)
  {
    put( "FGameNbPlayer", p_value );
  }

  public static int incrementFGameNbPlayer(int p_value)
  {
    return (int)increment( "FGameNbPlayer", p_value );
  }

  private static String getVarName(ConfigGameTime p_config)
  {
    switch( p_config )
    {
    case Standard:
      return "FGameNbTimeStandard";
    case QuickAsynch:
      return "FGameNbTimeQuickAsynch";
    case QuickTurnBased:
      return "FGameNbTimeQuickTurnBased";
    case StandardAsynch:
      return "FGameNbTimeStandardAsynch";
    case Custom:
    default:
      return "FGameNbTimeCustom";
    }
  }

  public static int getFGameNbConfigGameTime(ConfigGameTime p_config)
  {
    return getInt( getVarName( p_config ) );
  }

  public static void setFGameNbConfigGameTime(ConfigGameTime p_config, int p_value)
  {
    put( getVarName( p_config ), p_value );
  }

  public static int incrementFGameNbConfigGameTime(ConfigGameTime p_config, int p_value)
  {
    return (int)increment( getVarName( p_config ), p_value );
  }

  private static String getVarName(ConfigGameVariant p_config)
  {
    switch( p_config )
    {
    case Standard:
    default:
      return "FGameNbVariantStandard";
    }
  }

  public static int getFGameNbConfigGameVariant(ConfigGameVariant p_config)
  {
    return getInt( getVarName( p_config ) );
  }

  public static void setFGameNbConfigGameVariant(ConfigGameVariant p_config, int p_value)
  {
    put( getVarName( p_config ), p_value );
  }
  public static int incrementFGameNbConfigGameVariant(ConfigGameVariant p_config, int p_value)
  {
    return (int)increment( getVarName( p_config ), p_value );
  }



  public static long getFGameNbOfHexagon()
  {
    return getLong( "FGameNbOfHexagon" );
  }

  public static void setFGameNbOfHexagon(long p_value)
  {
    put( "FGameNbOfHexagon", p_value );
  }

  public static long incrementFGameNbOfHexagon(int p_value)
  {
    return increment( "FGameNbOfHexagon", p_value );
  }


  public static int getFGameFmpScore()
  {
    return getInt( "FGameFmpScore" );
  }

  public static void setFGameFmpScore(int p_value)
  {
    put( "FGameFmpScore", p_value );
  }

  public static int incrementFGameFmpScore(int p_value)
  {
    return (int)increment( "FGameFmpScore", p_value );
  }

  public static int getFGameFireCount()
  {
    return getInt( "FGameFireCount" );
  }

  public static void setFGameFireCount(int p_value)
  {
    put( "FGameFireCount", p_value );
  }

  public static int incrementFGameFireCount(int p_value)
  {
    return (int)increment( "FGameFireCount", p_value );
  }

  public static int getFGameUnitControlCount()
  {
    return getInt( "FGameUnitControlCount" );
  }

  public static void setFGameUnitControlCount(int p_value)
  {
    put( "FGameUnitControlCount", p_value );
  }

  public static int incrementFGameUnitControlCount(int p_value)
  {
    return (int)increment( "FGameUnitControlCount", p_value );
  }

  public static int getFGameFreighterControlCount()
  {
    return getInt( "FGameFreighterControlCount" );
  }

  public static void setFGameFreighterControlCount(int p_value)
  {
    put( "FGameFreighterControlCount", p_value );
  }

  public static int incrementFGameFreighterControlCount(int p_value)
  {
    return (int)increment( "FGameFreighterControlCount", p_value );
  }


  public static int getFGameConstructionCount()
  {
    return getInt( "FGameConstructionCount" );
  }

  public static void setFGameConstructionCount(int p_value)
  {
    put( "FGameConstructionCount", p_value );
  }

  public static int incrementFGameConstructionCount(int p_value)
  {
    return (int)increment( "FGameConstructionCount", p_value );
  }

  public static int getFGameOreCount()
  {
    return getInt( "FGameOreCount" );
  }

  public static void setFGameOreCount(int p_value)
  {
    put( "FGameOreCount", p_value );
  }

  public static int incrementFGameOreCount(int p_value)
  {
    return (int)increment( "FGameOreCount", p_value );
  }

  public static int getFGameTokenCount()
  {
    return getInt( "FGameTokenCount" );
  }

  public static void setFGameTokenCount(int p_value)
  {
    put( "FGameTokenCount", p_value );
  }

  public static int incrementFGameTokenCount(int p_value)
  {
    return (int)increment( "FGameTokenCount", p_value );
  }



}
