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
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
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
      newsHtml += "Parties: " + (getFinishedGameCount() + getCurrentGameCount()) + " ("
          + getCurrentGameCount() + " en cours)<br/>";
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

  
  // note: style repartition stuff aren't thread safe...
  private static final int STYLE_RATIO_COUNT = 30;
  private static final float STYLE_RATIO_MAX = 4f;
  private static final float STYLE_RATIO_MIN = 0.2f;

  public static void resetStyleRatioRepartition()
  {
    put( "StyleRatioRepartition", new int[STYLE_RATIO_COUNT] );
  }

  private static int[] getStyleRatioRepartition()
  {
    Object obj = get( "StyleRatioRepartition" );
    if( obj instanceof int[] )
    {
      int[] repartition = (int[])obj;
      return repartition;
    }
    return new int[STYLE_RATIO_COUNT];
  }

  private static int styleRatio2Index(float p_styleRatio)
  {
    int index = (int)((p_styleRatio - STYLE_RATIO_MIN) * STYLE_RATIO_COUNT / STYLE_RATIO_MAX);
    if( index < 0 )
      index = 0;
    if( index >= STYLE_RATIO_COUNT )
      index = STYLE_RATIO_COUNT - 1;
    return index;
  }
  
  public static void addStyleRatio(Game p_game)
  {
    int[] repartition = getStyleRatioRepartition();
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      if( registration.getStats() != null )
        repartition[styleRatio2Index( registration.getStats().getStyleRatio() )]++;
    }
    put( "StyleRatioRepartition", repartition );
  }
  
  /**
   * convert a style ratio into one of the three PlayerStyle according to all referenced style ratio
   * @param p_styleRatio
   * @return
   */
  public static PlayerStyle getPlayerStyle(float p_styleRatio)
  {
    int[] repartition = getStyleRatioRepartition();
    int styleCount = 0;
    for( int i = 0; i < STYLE_RATIO_COUNT; i++ )
    {
      styleCount += repartition[i];
    }
    int lowerStyleCount = 0;
    for( int i = 0; i < styleRatio2Index( p_styleRatio ); i++ )
    {
      lowerStyleCount += repartition[i];
    }
    if( lowerStyleCount < styleCount / 3 )
    {
      return PlayerStyle.Pacific;
    }
    if( lowerStyleCount > 2 * styleCount / 3 )
    {
      return PlayerStyle.Aggressive;
    }
    return PlayerStyle.Balanced;
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


  public static double getMaxLevel()
  {
    return getDouble( "MaxLevel" );
  }

  public static void setMaxLevel(double p_accountCount)
  {
    put("MaxLevel",p_accountCount);
  }



  /**
   * sum of game with status Open, Paused, Running
   * @return
   */
  public static int getCurrentGameCount()
  {
    return getInt( "CurrentGameCount" );
  }

  public static void setCurrentGameCount(int p_currentGameCount)
  {
    put( "CurrentGameCount", p_currentGameCount );
  }

  public static int incrementCurrentGameCount(int p_toAdd)
  {
    return (int)increment( "CurrentGameCount", p_toAdd );
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

  public static int getFinishedGameCount()
  {
    int count = 0;
    for( ConfigGameVariant variant : ConfigGameVariant.values() )
    {
      count += getFGameNbConfigGameVariant( variant );
    }
    return count;
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
