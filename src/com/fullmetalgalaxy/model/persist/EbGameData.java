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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * these data plus preview data contain all data needed to represent and play a game.
 * This class is used only to be stored into data base.
 * As we don't need to query on these data, it is unindexed and serialized.
 * 
 * @author Vincent Legendre
 */
@Unindexed
public class EbGameData extends EbBase
{
  static final long serialVersionUID = 11;

  @Parent
  protected Key<EbGamePreview> m_preview;

  protected Tide m_currentTide = Tide.Medium;
  protected Tide m_nextTide = Tide.Medium;
  protected Tide m_nextTide2 = Tide.Medium;
  /** this average level is computed as follow: +1 for high tide and -1 for low tide */
  protected int m_averageTideLevel = 0;
  protected int m_lastTideChange = 0;
  protected Date m_lastTimeStepChange = new Date( SharedMethods.currentTimeMillis() );
  protected ArrayList<Integer> m_takeOffTurns = null;
  protected String m_mapUri = null;
  protected String m_messages = null;
  /**
  * Land description. It's a two dimension array of landWitdh * landHeight
  */
  protected byte[] m_lands = new byte[0];

  @Serialized
  protected Set<com.fullmetalgalaxy.model.persist.EbToken> m_setToken = new HashSet<com.fullmetalgalaxy.model.persist.EbToken>();

  /**
   * if m_setGameLog isn't complete, this value contain the number of event stored in a separate entity.
   */
  @Unindexed
  protected int m_additionalEventCount = 0;
  /**
   * key of all additional game log.
   */
  @Serialized
  protected List<Long> m_additionalGameLog = null;
  // TODO convert this AnEvent list to a GameEvent list
  @Serialized
  protected List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> m_setGameLog = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();
  @Serialized
  protected List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> m_triggers = new ArrayList<com.fullmetalgalaxy.model.persist.triggers.EbTrigger>();
  @Serialized
  protected Set<EbPublicAccount> m_accounts = null;
  @Serialized
  protected Map<TokenType, Integer> m_constructReserve = new HashMap<TokenType, Integer>();
  @Serialized
  protected Map<TokenType, Integer> m_initialHolds = null;

  @Unindexed
  protected long m_nextLocalId = 0L;

  /**
   * 
   */
  public EbGameData()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbGameData(EbBase p_base)
  {
    super( p_base );
  }
  protected void init()
  {
    m_currentTide = Tide.Medium;
    m_nextTide = Tide.Medium;
    m_nextTide2 = Tide.Medium;
    m_averageTideLevel = 0;
    m_lastTideChange = 0;
    m_lands = new byte[0];
    m_lastTimeStepChange = new Date( SharedMethods.currentTimeMillis() );
    m_takeOffTurns = null;
    m_mapUri = null;
    m_messages = null;
    m_setToken = new HashSet<com.fullmetalgalaxy.model.persist.EbToken>();
    m_setGameLog = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();
    m_triggers = new ArrayList<com.fullmetalgalaxy.model.persist.triggers.EbTrigger>();
    m_nextLocalId = 0L;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  
  public void setKeyPreview( Key<EbGamePreview> p_preview )
  {
    m_preview = p_preview;
  }
  
  
  /**
   * use this to generate any id local to this game.
   * @return
   */
  public long getNextLocalId()
  {
    m_nextLocalId++;
    return m_nextLocalId;
  }


  public Map<TokenType, Integer> getConstructReserve()
  {
    return m_constructReserve;
  }


  public Map<TokenType, Integer> getInitialHolds()
  {
    if( m_initialHolds == null )
    {
      return FmpConstant.getDefaultInitialHolds();
    }
    return m_initialHolds;
  }

  public void setInitialHoldsQty(TokenType p_type, int p_qty)
  {
    if( m_initialHolds == null )
    {
      m_initialHolds = FmpConstant.getDefaultInitialHolds();
    }
    m_initialHolds.put( p_type, p_qty );
  }

  /**
   * score sum of all token in initial hold 
   * @return
   */
  public int getInitialScore()
  {
    int score = 1; // one for freighter
    for(Entry<TokenType, Integer> entry: getInitialHolds().entrySet())
    {
      score += entry.getKey().getWinningPoint() * entry.getValue();
    }
    return score;
  }
  
  /**
   * Don't use this method directly, it's for hibernate and h4gwt use only
   * @return the lands
   * @WgtHidden
   */
  public byte[] getLands()
  {
    return m_lands;
  }

  /**
   * Don't use this method directly, it's for hibernate and h4gwt use only
   * @param p_lands the lands to set
   */
  public void setLands(byte[] p_lands)
  {
    m_lands = p_lands;
  }



  /**
   * @return the setToken
   * @WgtHidden
   */
  public Set<com.fullmetalgalaxy.model.persist.EbToken> getSetToken()
  {
    return m_setToken;
  }

  /**
   * @param p_setToken the setToken to set
   */
  public void setSetToken(Set<com.fullmetalgalaxy.model.persist.EbToken> p_setToken)
  {
    m_setToken = p_setToken;
  }




  /**
   * @return the currentTide
   * @WgtHidden
   */
  public Tide getCurrentTide()
  {
    return m_currentTide;
  }

  /**
   * @param p_currentTide the currentTide to set
   */
  public void setCurrentTide(Tide p_currentTide)
  {
    m_currentTide = p_currentTide;
  }



  /**
   * @return the nextTide
   * @WgtHidden
   */
  public Tide getNextTide()
  {
    if( m_nextTide == null )
    {
      m_nextTide = Tide.getRandom( getAverageTideLevel() );
      setAverageTideLevel( getAverageTideLevel() + m_nextTide.getLevel() );
    }
    return m_nextTide;
  }

  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide(Tide p_nextTide)
  {
    m_nextTide = p_nextTide;
  }


  /**
   * @return the nextTide
   * @WgtHidden
   */
  public Tide getNextTide2()
  {
    if( m_nextTide2 == null )
    {
      m_nextTide2 = Tide.getRandom( getAverageTideLevel() );
      setAverageTideLevel( getAverageTideLevel() + m_nextTide2.getLevel() );
    }
    return m_nextTide2;
  }

  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide2(Tide p_nextTide)
  {
    m_nextTide2 = p_nextTide;
  }




  /**
   * @return the lastTideChange
   */
  public int getLastTideChange()
  {
    return m_lastTideChange;
  }

  /**
   * @param p_lastTideChange the lastTideChange to set
   */
  public void setLastTideChange(int p_lastTideChange)
  {
    m_lastTideChange = p_lastTideChange;
  }

  /**
   * @return the lastTimeStepChange
   */
  public Date getLastTimeStepChange()
  {
    return m_lastTimeStepChange;
  }

  /**
   * @param p_lastTimeStepChange the lastTimeStepChange to set
   */
  public void setLastTimeStepChange(Date p_lastTimeStepChange)
  {
    m_lastTimeStepChange = p_lastTimeStepChange;
  }


  /**
   * @return the setActionLog
   */
  public List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> getLogs()
  {
    return m_setGameLog;
  }

  /**
   * @param p_setActionLog the setActionLog to set
   */
  public void setLogs(List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> p_setActionLog)
  {
    m_setGameLog = p_setActionLog;
  }

  /**
   * use getAllowedTakeOffTurns() instead
   * @return the takeOffTurns
   */
  public ArrayList<Integer> getTakeOffTurns()
  {
    return m_takeOffTurns;
  }

  /**
   * @param p_takeOffTurns the takeOffTurns to set
   */
  public void setTakeOffTurns(ArrayList<Integer> p_takeOffTurns)
  {
    m_takeOffTurns = p_takeOffTurns;
  }

  /**
   * @return the triggers
   */
  public List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> getTriggers()
  {
    return m_triggers;
  }

  /**
   * @param p_triggers the triggers to set
   */
  public void setTriggers(List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> p_triggers)
  {
    m_triggers = p_triggers;
  }


  /**
   * @return the mapUri
   */
  public String getMapUri()
  {
    return m_mapUri;
  }

  /**
   * @param p_mapUri the mapUri to set
   */
  public void setMapUri(String p_mapUri)
  {
    m_mapUri = p_mapUri;
  }

  public String getMessage()
  {
    return m_messages;
  }

  public void setMessage(String p_messages)
  {
    m_messages = p_messages;
  }



  /**
   * @param p_otherAccounts the otherAccounts to set
   */
  public void addAccount(EbPublicAccount p_otherAccount)
  {
    if( p_otherAccount == null )
      return;
    if( m_accounts == null )
    {
      m_accounts = new HashSet<EbPublicAccount>();
    }
    m_accounts.add( p_otherAccount );
  }

  public EbPublicAccount getAccount(long p_accountId)
  {
    if( m_accounts == null )
    {
      return null;
    }
    for( EbPublicAccount account : m_accounts )
    {
      if( account.getId() == p_accountId )
      {
        return account;
      }
    }
    return null;
  }


  /**
   * if message start with './', '/' or 'http://', message is a web page url
   * @return
   */
  public boolean isMessageWebUrl()
  {
    return getMessage() != null && ( getMessage().startsWith( "./" ) || getMessage().startsWith( "/" )
        || getMessage().startsWith( "http://" ) );
  }

  public int getAdditionalEventCount()
  {
    return m_additionalEventCount;
  }

  public void setAdditionalEventCount(int p_additionalEventCount)
  {
    m_additionalEventCount = p_additionalEventCount;
  }

  public List<Long> getAdditionalGameLog()
  {
    if( m_additionalGameLog == null )
    {
      m_additionalGameLog = new ArrayList<Long>();
    }
    return m_additionalGameLog;
  }

  public void setAdditionalGameLog(List<Long> p_additionalGameLog)
  {
    m_additionalGameLog = p_additionalGameLog;
  }

  public int getAverageTideLevel()
  {
    return m_averageTideLevel;
  }

  public void setAverageTideLevel(int p_averageTideLevel)
  {
    m_averageTideLevel = p_averageTideLevel;
  }



}
