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

package com.fullmetalgalaxy.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.CompanyStatistics;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

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
      newsHtml = "" + getActiveAccount() + " joueurs classés<br/>";
      newsHtml += "" + getCurrentGameCount() + " parties en cours & " + getFinishedGameCount()
          + " terminés<br/>";
      // for debug
      /*newsHtml += "<br/>open: "+getOpenGameCount() + " <br/>";
      newsHtml += "running: "+getRunningGameCount() + " <br/>";
      newsHtml += "finished: "+getFinishedGameCount() + " <br/>";
      newsHtml += "aborted: "+getAbortedGameCount() + " <br/>";
      newsHtml += "deletec: "+getDeletedGameCount() + " <br/>";
      */
      // get best player
      /*newsHtml += "Meilleur joueur:";
      Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);
      accountQuery = accountQuery.order( "-m_currentStats.m_averageNormalizedRank" )
          .filter( "m_currentStats.m_includedInRanking", true )
          .limit( 1 );
      for(EbAccount account : accountQuery.fetch() )
      {
        newsHtml += account.buildHtmlFragment();
      }
      */
      // get best company
      int year = GregorianCalendar.getInstance().get( Calendar.YEAR );
      if( GregorianCalendar.getInstance().get( Calendar.MONTH ) <= 1 )
      {
        year--;
      }
      com.googlecode.objectify.Query<CompanyStatistics> companyList = FmgDataStore.dao()
          .query( CompanyStatistics.class )
          .filter( "m_year", year )
          .order( "-m_profit" ).limit( 3 );
      newsHtml += ("Meilleurs corporations " + year + " :<table width='100%'>");
      for( CompanyStatistics companyStat : companyList )
      {
        if( companyStat.getCompany() != Company.Freelancer )
        {
          newsHtml += ("<tr>");
          newsHtml += ("<td><a href='/oldgameprofile.jsp?corpo=" + companyStat.getCompany()
              + "'><IMG SRC='/images/avatar/" + companyStat.getCompany() + ".jpg' WIDTH=60 HEIGHT=60 BORDER=0/></a></td>");
          newsHtml += ("<td><a href='/oldgameprofile.jsp?corpo=" + companyStat.getCompany()
              + "'><b>" + companyStat.getCompany().getFullName() + "</b></a><br/>");
          newsHtml += ("Bénéfice: " + companyStat.getProfit() + "<br/>");
          newsHtml += ("Rentabilité: " + companyStat.getProfitabilityInPercent() + " %<br/>");
          newsHtml += ("Nb exploitation: " + companyStat.getMiningCount());
          newsHtml += ("</td>");
          newsHtml += ("</tr>");
        }
      }
      newsHtml += ("</table>");
      
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

  
  
  public static int getAccountCount()
  {
    return getInt("AccountCount");
  }

  public static void setAccountCount(int p_accountCount)
  {
    put("AccountCount",p_accountCount);
  }

  public static int incrementAccountCount(int p_toAdd)
  {
    return (int)increment( "AccountCount", p_toAdd );
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


  public static int getFinishedGameCount()
  {
    int count = 0;
    for( ConfigGameTime variant : ConfigGameTime.values() )
    {
      count += getFGameNbConfigGameTime( variant );
    }
    return count;
  }

  public static int getFGameInitiationCount()
  {
    return getInt( "InitiationGameCount" );
  }

  public static void setFGameInitiationCount(int p_currentGameCount)
  {
    put( "InitiationGameCount", p_currentGameCount );
  }

  public static int incrementFGameInitiationCount(int p_toAdd)
  {
    return (int)increment( "InitiationGameCount", p_toAdd );
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




}
