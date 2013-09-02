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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model.persist;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author Vincent Legendre
 *
 * contain several games statistics of a given account.
 * Only GameType.MultiPlayer are taken in account.
 */
public class AccountStatistics implements Serializable, IsSerializable
{
  private static final long serialVersionUID = 1L;
  
  
  /**
   * it's the sum of all his scores. We can call this: total profit.
   */
  private int m_score = 0;

  /** used to make request on database */
  private boolean m_includedInRanking = false;

  private float m_averageProfitability = 0;
  
  private float m_averageNormalizedRank = 0;
  /** only for ConfigGameTime.Standard (slow turn by turn) */
  private long m_averageReactivityInSec = 0;
  
  // the following statistic likely have to be divided by m_finshedGameCount
  // to be compared
  /** number of ore load or transfer into a freighter */
  @Unindexed
  private int m_oreLoad = 0;
  /** number building action with weather hen */
  @Unindexed
  private int m_construction = 0;
  /** number of fire action */
  @Unindexed
  private int m_destruction = 0;
  /** number of freighter capture/control */
  @Unindexed
  private int m_freighterCapture = 0;
  /** number of standard units capture/control.
   * don't count control if unit doesn't change team  */
  @Unindexed
  private int m_unitsCapture = 0;
  
  
  /**
   * quantity of different account meet in his finished game
   */
  private int m_opponentPlayerCount = 0;
  @Serialized
  private Set<EbPublicAccount> m_opponentPlayers = new HashSet<EbPublicAccount>();
  
  private int m_finshedGameCount = 0;

  private int m_victoryCount = 0;
  @Unindexed
  private int m_losedCount = 0;
  @Unindexed
  private Date m_firstGameDate = null;
  @Unindexed
  private Date m_lastGameDate = null;
  
  
  /**
   * used for serialization policy
   */
  public AccountStatistics()
  {
  }
  
  
  public AccountStatistics(Iterable<PlayerGameStatistics> p_statistics)
  {
    for(PlayerGameStatistics stat : p_statistics)
    {
      addStatistic( stat );
    }
  }
  
  public void addStatistic(PlayerGameStatistics p_statistic)
  {
    if( p_statistic.getGameType() != GameType.MultiPlayer ) return;

    if( m_firstGameDate == null || m_firstGameDate.after( p_statistic.getGameEndDate() ) )
    {
      m_firstGameDate = p_statistic.getGameEndDate();
    }
    if( m_lastGameDate == null || m_lastGameDate.before( p_statistic.getGameEndDate() ) )
    {
      m_lastGameDate = p_statistic.getGameEndDate();
    }

    m_averageProfitability *= m_finshedGameCount;
    m_averageNormalizedRank *= m_finshedGameCount;

    m_score += p_statistic.getScore();
    m_averageProfitability += p_statistic.getProfitability();
    m_averageNormalizedRank += p_statistic.getNormalizedRank();
    if( p_statistic.getConfigGameTime() == ConfigGameTime.Standard
        && p_statistic.getReplacement() == null )
    {
      m_averageReactivityInSec *= m_finshedGameCount;
      m_averageReactivityInSec += p_statistic.getAverageReactivityInSec();
    }
    m_oreLoad += p_statistic.getOreLoad();
    m_construction += p_statistic.getConstruction();
    m_destruction += p_statistic.getDestruction();
    m_freighterCapture += p_statistic.getFreighterCapture();
    m_unitsCapture += p_statistic.getUnitsCapture();
    for( EbPublicAccount account : p_statistic.getOpponentPlayers() )
    {
      if( !m_opponentPlayers.contains( account ) )
      {
        m_opponentPlayerCount++;
        m_opponentPlayers.add( account );
      }
    }
    m_finshedGameCount++;
    if( p_statistic.isWinner() ) m_victoryCount++;
    if( p_statistic.isLooser() ) m_losedCount++;      

    if( m_finshedGameCount > 0 )
    {
      m_averageProfitability /= m_finshedGameCount;
      m_averageNormalizedRank /= m_finshedGameCount;
      if( p_statistic.getConfigGameTime() == ConfigGameTime.Standard
          && p_statistic.getReplacement() == null )
      {
        m_averageReactivityInSec /= m_finshedGameCount;
      }
    }

    // is this account included or excluded from ranking ?
    if( m_opponentPlayerCount > 3 && m_finshedGameCount > 2 )
    {
      m_includedInRanking = true;
    }
    else
    {
      m_includedInRanking = false;
    }
  }
  
  
  
  
  /**
   * proposed by ludomaniak.
   * we may want to save this statistic to make query on it.
   * @return
   */
  public float getLevel()
  {
    int victoryPt = getVictoryCount()*2 - getLosedCount();
    if( victoryPt < 0 ) victoryPt = 0;
    return (1f*victoryPt)/getFinshedGameCount();
  }

  public int getAverageProfitabilityInPercent()
  {
    return Math.round( getAverageProfitability() * 100 );
  }

  public int getAverageNormalizedRankInPercent()
  {
    return Math.round( getAverageNormalizedRank() * 100 );
  }

  // Getters
  // =======


  public int getScore()
  {
    return m_score;
  }


  public float getAverageProfitability()
  {
    return m_averageProfitability;
  }


  public float getAverageNormalizedRank()
  {
    return m_averageNormalizedRank;
  }


  public long getAverageReactivityInSec()
  {
    return m_averageReactivityInSec;
  }


  public int getOreLoad()
  {
    return m_oreLoad;
  }


  public int getConstruction()
  {
    return m_construction;
  }


  public int getDestruction()
  {
    return m_destruction;
  }


  public int getFreighterCapture()
  {
    return m_freighterCapture;
  }


  public int getUnitsCapture()
  {
    return m_unitsCapture;
  }


  public int getOpponentPlayerCount()
  {
    return m_opponentPlayerCount;
  }


  public int getFinshedGameCount()
  {
    return m_finshedGameCount;
  }


  public int getVictoryCount()
  {
    return m_victoryCount;
  }

  public int getLosedCount()
  {
    return m_losedCount;
  }


  public Set<EbPublicAccount> getOpponentPlayers()
  {
    return m_opponentPlayers;
  }


  public boolean isIncludedInRanking()
  {
    return m_includedInRanking;
  }


  public Date getFirstGameDate()
  {
    return m_firstGameDate;
  }


  public Date getLastGameDate()
  {
    return m_lastGameDate;
  }

  
  
}
