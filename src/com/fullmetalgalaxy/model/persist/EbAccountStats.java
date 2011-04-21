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
package com.fullmetalgalaxy.model.persist;

import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 *
 */
public class EbAccountStats extends EbBase
{
  static final long serialVersionUID = 1;

  public enum Status implements IsSerializable
  {
    /** game is still running */
    Running, 
    /** game is finished */
    Finished,
    /** game was canceled */
    Canceled,
    /** account was banned from this game */
    Banned,
    /** account create this game, but didn't play it */
    CreatorOnly,
    /** periodic erosion */
    Erosion;
  }
  
  // game short description
  private long m_gameId = 0;
  private String m_gameName = null;
  private int m_numberOfPlayer = 0;
  private ConfigGameTime m_configGameTime = null;
  private ConfigGameVariant m_configGameVariant = null;
  
  private boolean m_isCreator = false;
  
  /** Statistic last update:<br/>
   * - if status is Running or CreatorOnly, game creation date<br/>
   * - if status is Finished or Cancel, game end date<br/>
   * - if status is Banned, player ban date<br/>
   *  */
  private Date m_lastUpdate = new Date();
  /** current account status for this game */
  private Status m_status = Status.Running;
  
  /** game final score, or erosion value
   * may differ from oreCount + tokenCount as ore may have different value.
   * More important, this score depend of other players level !
   *
   * finalScore = (fmpScore - 20)*(sum(otherLevel)/(myLevel*otherPlayerCount))^sign(fmpScore) 
   *
   * for winner, we also add the sum of other players bonus
   * */
  private int m_finalScore = 0;
  /** game final rank (1 for winner) */
  private int m_gameRank = 0;
  /** fire action count during game */
  private int m_fireCount = 0;
  /** unit control action count during game */
  private int m_unitControlCount = 0;
  /** freighter control action count during game */
  private int m_freighterControlCount = 0;
  /** unit losed due to opponent control or fire action */
  private int m_losedUnitCount = 0;
  /** players color at beginning of game */
  private int m_initialColor = EnuColor.None;
  /** players color at end of game */
  private int m_finalColor = EnuColor.None;
  /** construct action count during game */
  private int m_constructionCount = 0;
  /** ore count in freighter at end of game */
  private int m_oreCount = 0;
  /** unit count in freighter at end of game */
  private int m_tokenCount = 0;


  public EbAccountStats()
  {
    super();
    init();
  }

  public EbAccountStats(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
    m_gameRank = 0;
    m_fireCount = 0;
    m_unitControlCount = 0;
    m_freighterControlCount = 0;
    m_oreCount = 0;
    m_tokenCount = 0;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  public static EbAccountStats generate(EbRegistration p_registration, Game p_game)
  {
    EbAccountStats stats = new EbAccountStats();
    stats.setOreCount( p_registration.getOreCount(p_game) );
    stats.setTokenCount( p_registration.getTokenCount(p_game) );
    //stats.setWinningPoints( p_registration.getWinningPoint() );

    /*for( AnEvent event : p_game.getLogs() )
    {
      if( event instanceof EbEvtControl )
      {
        EbEvtControl control = (EbEvtControl)event;
        if( control.getRegistration().getId() == p_registration.getId() )
        {
          stats.setControlCount( stats.getControlCount() + 1 );
        }
      }
      else if( event instanceof EbEvtFire )
      {
        EbEvtFire fire = (EbEvtFire)event;
        if( fire.getRegistration().getId() == p_registration.getId() )
        {
          stats.setFireCount( stats.getFireCount() + 1 );
        }
      }
    }*/

    List<EbRegistration> sortedRegistration = p_game.getRegistrationByWinningRank();
    int index = 0;
    while( index < sortedRegistration.size() )
    {
      if( sortedRegistration.get( index ) == p_registration )
      {
        stats.setGameRank( index + 1 );
        break;
      }
      index++;
    }

    return stats;
  }

  // getters / setters
  // -----------------

  
  public long getGameId()
  {
    return m_gameId;
  }

  public void setGameId(long p_gameId)
  {
    m_gameId = p_gameId;
  }

  public String getGameName()
  {
    return m_gameName;
  }

  public void setGameName(String p_gameName)
  {
    m_gameName = p_gameName;
  }

  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }

  public Status getStatus()
  {
    return m_status;
  }

  public void setStatus(Status p_status)
  {
    m_status = p_status;
  }

  public int getGameRank()
  {
    return m_gameRank;
  }

  public void setGameRank(int p_gameRank)
  {
    m_gameRank = p_gameRank;
  }

  public int getFireCount()
  {
    return m_fireCount;
  }

  public void setFireCount(int p_fireCount)
  {
    m_fireCount = p_fireCount;
  }

  public int getUnitControlCount()
  {
    return m_unitControlCount;
  }

  public void setUnitControlCount(int p_unitControlCount)
  {
    m_unitControlCount = p_unitControlCount;
  }

  public int getFreighterControlCount()
  {
    return m_freighterControlCount;
  }

  public void setFreighterControlCount(int p_freighterControlCount)
  {
    m_freighterControlCount = p_freighterControlCount;
  }

  public int getLosedUnitCount()
  {
    return m_losedUnitCount;
  }

  public void setLosedUnitCount(int p_losedUnitCount)
  {
    m_losedUnitCount = p_losedUnitCount;
  }

  public int getInitialColor()
  {
    return m_initialColor;
  }

  public void setInitialColor(int p_initialColor)
  {
    m_initialColor = p_initialColor;
  }

  public int getFinalColor()
  {
    return m_finalColor;
  }

  public void setFinalColor(int p_finalColor)
  {
    m_finalColor = p_finalColor;
  }

  public int getConstructionCount()
  {
    return m_constructionCount;
  }

  public void setConstructionCount(int p_constructionCount)
  {
    m_constructionCount = p_constructionCount;
  }

  public int getOreCount()
  {
    return m_oreCount;
  }

  public void setOreCount(int p_oreCount)
  {
    m_oreCount = p_oreCount;
  }

  public int getTokenCount()
  {
    return m_tokenCount;
  }

  public void setTokenCount(int p_tokenCount)
  {
    m_tokenCount = p_tokenCount;
  }

  public int getNumberOfPlayer()
  {
    return m_numberOfPlayer;
  }

  public void setNumberOfPlayer(int p_numberOfPlayer)
  {
    m_numberOfPlayer = p_numberOfPlayer;
  }

  public ConfigGameTime getConfigGameTime()
  {
    return m_configGameTime;
  }

  public void setConfigGameTime(ConfigGameTime p_configGameTime)
  {
    m_configGameTime = p_configGameTime;
  }

  public ConfigGameVariant getConfigGameVariant()
  {
    return m_configGameVariant;
  }

  public void setConfigGameVariant(ConfigGameVariant p_configGameVariant)
  {
    m_configGameVariant = p_configGameVariant;
  }

  public boolean isCreator()
  {
    return m_isCreator;
  }

  public void setCreator(boolean p_isCreator)
  {
    m_isCreator = p_isCreator;
  }

  public int getFinalScore()
  {
    return m_finalScore;
  }

  public void setFinalScore(int p_finalScore)
  {
    m_finalScore = p_finalScore;
  }



}
