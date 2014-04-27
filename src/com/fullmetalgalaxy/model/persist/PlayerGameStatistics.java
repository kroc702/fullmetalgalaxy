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

package com.fullmetalgalaxy.model.persist;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventList;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTransfer;
import com.fullmetalgalaxy.model.persist.gamelog.GameEvent;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author vincent legendre
 * 
 * contain statistics about one player on a given game.
 *
 */
@Unindexed
public class PlayerGameStatistics extends EbBase
{
  static final long serialVersionUID = 1;
  
  // information on game
  // ===================
  @Parent
  protected Key<EbGamePreview> m_keyGamePreview = null;
  @Indexed
  private String m_gameName = "";
  /** date of the first landing action. (ie not the creation date) */
  private Date m_gameBeginDate = null;
  /** date of the last take off action */
  @Indexed
  private Date m_gameEndDate = null;
  /** number of team */
  private int m_gameTeamCount = 0;
  /** number of registration */
  private int m_gamePlayerCount = 0;
  /** initial units in hold points.
   * Investment for the whole team. */
  private int m_investment = 0;
  private GameType m_gameType = GameType.Initiation;
  /** Although this status is likely to be history (ie game is finished) */
  private GameStatus m_gameStatus = GameStatus.History;

  private ConfigGameTime m_gameConfigTime = ConfigGameTime.Standard;
  
  @Embedded
  private Set<EbPublicAccount> m_opponentPlayers = new HashSet<EbPublicAccount>();
  @Embedded
  private Set<EbPublicAccount> m_partnerPlayers = new HashSet<EbPublicAccount>();
  private int m_myTeamSize = 0;
  
  // information on player
  // =====================
  /** account that finished the game */
  @Embedded
  @Indexed
  private EbPublicAccount m_account = null;
  
  // information on this playing session
  // ===================================
  
  /** 1 for winner, 2 for second, etc...
   *  Draw are rounded down.
   *  partner players (same team) have same rank.
   *  shall be > 0 and <= m_gameTeamCount
   *  */
  private int m_rank = 0;
  /** units in hold at end of game minus investment.
   * It is the common score to the full team
   *  */
  private int m_score = 0;
  /** number of ore load or transfer into a freighter */
  private int m_oreLoad = 0;
  /** number building action with weather hen */
  private int m_construction = 0;
  /** number of fire action */
  private int m_destruction = 0;
  /** number of freighter capture/control at end of game */
  private int m_freighterCapture = 0;
  /** number of standard units capture/control.
   * don't count control if unit doesn't change team  */
  private int m_unitsCapture = 0;
  
  @Indexed
  private Company m_company = Company.Freelancer;
  /** for turn by turn games only: the average time in second
   * of his turn. between two end turn action */
  private long m_averageReactivityInSec = 0;
  private int m_playerTurnCount = 0;
  /** if not null, player is either a replacement of the above account
   * or was replaced by above account.
   * see m_wasBanned */
  @Embedded
  @Indexed
  private EbPublicAccount m_replacement = null;
  /** if true, player was banned from this game */
  private boolean m_wasBanned = false;
  /** indication on true skill variation with this game */
  @AlsoLoad("m_tsUpdate")
  private double m_tsMeanUpdate = 0;
  private double m_tsSDUpdate = 0;

  
  /**
   * used for serialization policy
   */
  public PlayerGameStatistics()
  {
  }
  
  private void resetStat()
  {
    m_keyGamePreview = null;
    m_gameName = "";
    m_gameBeginDate = null;
    m_gameEndDate = null;
    m_gameTeamCount = 0;
    m_gamePlayerCount = 0;
    m_investment = 0;
    m_gameType = GameType.Initiation;
    m_gameConfigTime = ConfigGameTime.Standard;
    m_opponentPlayers = new HashSet<EbPublicAccount>();
    m_partnerPlayers = new HashSet<EbPublicAccount>();
    m_myTeamSize = 0;
    m_account = null;
    m_rank = 0;
    m_score = 0;
    m_oreLoad = 0;
    m_construction = 0;
    m_destruction = 0;
    m_freighterCapture = 0;
    m_unitsCapture = 0;
    m_company = Company.Freelancer;
    m_averageReactivityInSec = 0;
    m_playerTurnCount = 0;
    m_replacement = null;
    m_wasBanned = false;
    m_tsMeanUpdate = 0;
    m_tsSDUpdate = 0;
  }

  public long getGameId()
  {
    return m_keyGamePreview.getId();
  }

  /**
   * set a lot of statistic for this game/player
   * @param p_game
   * @param p_registration
   */
  public void setStatistics(Game p_game, EbRegistration p_registration)
  {
    if( p_game == null || p_registration == null )
    {
      return;
    }
    resetStat();
    m_keyGamePreview = null;
    m_gameName = p_game.getName();
    m_gameBeginDate = p_game.getCreationDate();
    m_gameEndDate = p_game.getEndDate();
    m_gameTeamCount = p_game.getTeams().size();
    m_gamePlayerCount = p_game.getCurrentNumberOfRegiteredPlayer();
    m_gameType = p_game.getGameType();
    m_gameConfigTime = p_game.getConfigGameTime();
    
    m_account = p_registration.getAccount();
    EbTeam myTeam = p_registration.getTeam( p_game );
    m_myTeamSize = myTeam.getPlayerIds().size();
    m_company = myTeam.getCompany();
    for( EbRegistration registration : myTeam.getPlayers( p_game.getPreview() ))
    {
      if( registration.haveAccount() && registration.getAccount().getId() != m_account.getId() )
      {
        m_partnerPlayers.add( registration.getAccount() );
      }
    }
    for( EbTeam team : p_game.getTeams() )
    {
      if( team.getId() != myTeam.getId() )
      {
        for( EbRegistration registration : team.getPlayers( p_game.getPreview() ) )
        {
          if( registration.haveAccount() )
          {
            m_opponentPlayers.add( registration.getAccount() );
          }
        }
      }
    }
        
    // find rank
    List<EbTeam> sortedTeam = p_game.getTeamByWinningRank();
    int index = 0;
    while( index < sortedTeam.size() )
    {
      if( sortedTeam.get( index ).getId() == myTeam.getId() )
      {
        m_rank = index + 1;
        break;
      }
      index++;
    }

    
    // simple value
    m_replacement = p_registration.getOriginalAccount( p_game );
    m_score = myTeam.estimateWinningScore( p_game );
    m_investment = p_game.getInitialScore() * myTeam.getPlayerIds().size();
        
        
        
    m_currentColor = new EnuColor( p_registration.getOriginalColor() );
    EnuColor finalColor = new EnuColor( p_registration.getColor() );
    m_freighterCapture = finalColor.getNbColor() - m_currentColor.getNbColor();
    
    m_averageReactivityInSec = 0;
    for( GameEvent event : p_game.getLogs() )
    {
      addEvent( p_game, p_registration, event );
    }
    if( m_playerTurnCount > 0 )
    {
      m_averageReactivityInSec /= m_playerTurnCount;
    }

  }

  // these two stat are only used by addEvent method
  @Transient
  private transient EnuColor m_currentColor = new EnuColor();
  @Transient
  private transient Date m_lastOponentTurnDate = null;


  private void addEvent(Game p_game, EbRegistration p_registration, GameEvent p_event)
  {
    // if provided event is a list of them, look inside
    if( p_event instanceof AnEventList )
    {
      for( GameEvent event : ((AnEventList)p_event) )
      {
        addEvent( p_game, p_registration, event );
      }
      return;
    }
    // if an error occur with one action, we don't mind: it's only stats !
    try
    {
      if( p_event instanceof AnEventPlay )
      {

        if( ((AnEventPlay)p_event).getRegistrationId() == p_registration.getId() )
        {
          if( p_event instanceof EbEvtControl )
          {
            m_unitsCapture++;
          }
          else if( p_event instanceof EbEvtFire )
          {
            m_destruction++;
          }
          else if( p_event instanceof EbEvtConstruct )
          {
            m_construction++;
          }
          else if( (p_event instanceof EbEvtLoad)
              && ((EbEvtLoad)p_event).getToken( p_game ).getType().isOre()
              && ((EbEvtLoad)p_event).getTokenCarrier( p_game ).getType() == TokenType.Freighter )
          {
            m_oreLoad++;
          }
          else if( (p_event instanceof EbEvtTransfer)
              && ((EbEvtTransfer)p_event).getToken( p_game ).getType().isOre()
              && ((EbEvtTransfer)p_event).getNewTokenCarrier( p_game ).getType() == TokenType.Freighter )
          {
            m_oreLoad++;
          }
          else if( p_event instanceof EbEvtControlFreighter )
          {
            m_currentColor.addColor( ((EbEvtControlFreighter)p_event).getTokenFreighter( p_game )
                .getColor() );
          }
        }
        else if( p_event instanceof EbEvtControlFreighter
            && m_currentColor.isColored( ((EbEvtControlFreighter)p_event).getTokenFreighter( p_game )
                .getColor() ) )
        {
          m_currentColor.removeColor( ((EbEvtControlFreighter)p_event).getTokenFreighter( p_game )
              .getColor() );
        }
      }
      else if( p_event instanceof EbAdminTimePlay )
      {
        m_lastOponentTurnDate = ((EbAdminTimePlay)p_event).getLastUpdate();
      }
      else if( p_event instanceof EbEvtPlayerTurn )
      {
        if( ((EbEvtPlayerTurn)p_event).getOldPlayerId( p_game ) == p_registration.getId()
            && m_lastOponentTurnDate != null )
        {
          // end turn by given player: compute reactivity
          m_averageReactivityInSec += (((EbEvtPlayerTurn)p_event).getLastUpdate().getTime() - m_lastOponentTurnDate
              .getTime()) / 1000;
          m_playerTurnCount++;
          m_lastOponentTurnDate = null;
        }
        else
        {
          // end turn by another player
          EbTeam team = p_game
              .getRegistration( ((EbEvtPlayerTurn)p_event).getOldPlayerId( p_game ) ).getTeam(
                  p_game );
          if( p_registration.getTeam( p_game ).getId() != team.getId() )
          {
            // end turn by an opponent player !
            m_lastOponentTurnDate = ((EbEvtPlayerTurn)p_event).getLastUpdate();
          }
        }
      }
    } catch( Throwable th )
    {
    }
  }
  
  /**
   * 
   * @return the conservative true skill update (ie Mean - 3 * SD)
   */
  public double getTsUpdate()
  {
    return getTsMeanUpdate() - 3 * getTsSDUpdate();
  }



  /**
   * 1 for winner and 0 for looser.
   * @return [1;0] that represent player rank regardless the number of players.
   */
  public float getNormalizedRank()
  {
    return (getGameTeamCount() - getRank()) * 1f / (getGameTeamCount() - 1);
  }

  /**
   * @return true if player win the game (ie rank == 1)
   */
  public boolean isWinner()
  {
    return getRank() == 1;
  }
  
  /**
   * @return true if player lose all his freighter during the game
   */
  public boolean isLooser()
  {
    return getTeamScore() == -1 * getInvestment();
  }

  /**
   * @return benefit divided by investment [-1;1]
   */
  public float getTeamProfitability()
  {
    return (1f * getTeamScore()) / getInvestment();
  }
  
  /**
   * 
   * @return team profitability divided by team size
   */
  public float getPlayerProfitability()
  {
    return getTeamProfitability() / getMyTeamSize();
  }

  // Getters
  // =======
 
  public Key<EbGamePreview> getKeyGamePreview()
  {
    return m_keyGamePreview;
  }

  public void setKeyGamePreview(Key<EbGamePreview> p_key)
  {
    m_keyGamePreview = p_key;
  }



  public String getGameName()
  {
    return m_gameName;
  }



  public Date getGameBeginDate()
  {
    return m_gameBeginDate;
  }



  public Date getGameEndDate()
  {
    return m_gameEndDate;
  }



  public int getGameTeamCount()
  {
    return m_gameTeamCount;
  }



  public int getGamePlayerCount()
  {
    return m_gamePlayerCount;
  }



  public int getInvestment()
  {
    return m_investment;
  }



  public Set<EbPublicAccount> getOpponentPlayers()
  {
    return m_opponentPlayers;
  }



  public Set<EbPublicAccount> getPartnerPlayers()
  {
    return m_partnerPlayers;
  }



  public EbPublicAccount getAccount()
  {
    return m_account;
  }



  public int getRank()
  {
    return m_rank;
  }



  public int getTeamScore()
  {
    return m_score;
  }

  /**
   * @return team score divided by team size
   */
  public int getPlayerScore()
  {
    return getTeamScore() / getMyTeamSize();
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



  public Company getCompany()
  {
    return m_company;
  }



  public long getAverageReactivityInSec()
  {
    return m_averageReactivityInSec;
  }



  public EbPublicAccount getReplacement()
  {
    return m_replacement;
  }



  public boolean isWasBanned()
  {
    return m_wasBanned;
  }
  
  public GameType getGameType()
  {
    return m_gameType;
  }
  
  public ConfigGameTime getConfigGameTime()
  {
    return m_gameConfigTime;
  }

  public double getTsMeanUpdate()
  {
    return m_tsMeanUpdate;
  }

  public void setTsMeanUpdate(double p_tsMeanUpdate)
  {
    m_tsMeanUpdate = p_tsMeanUpdate;
  }

  public double getTsSDUpdate()
  {
    return m_tsSDUpdate;
  }

  public void setTsSDUpdate(double p_tsSDUpdate)
  {
    m_tsSDUpdate = p_tsSDUpdate;
  }

  public int getMyTeamSize()
  {
    return m_myTeamSize;
  }

  public int getPlayerTurnCount()
  {
    return m_playerTurnCount;
  }

  public GameStatus getGameStatus()
  {
    if( m_gameStatus == null )
    {
      m_gameStatus = GameStatus.History;
    }
    return m_gameStatus;
  }

  public void setGameStatus(GameStatus p_gameStatus)
  {
    m_gameStatus = p_gameStatus;
  }
}
