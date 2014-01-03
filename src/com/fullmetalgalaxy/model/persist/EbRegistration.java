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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.googlecode.objectify.annotation.Serialized;


/**
 * @author Kroc
 * represent an association between an account (or user) and a game. so this account become
 * a player for this game
 */
public class EbRegistration extends EbBase
{
  static final long serialVersionUID = 1;

  public EbRegistration()
  {
    super();
    init();
  }

  public EbRegistration(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
    m_color = EnuColor.None;
    m_ptAction = 0;
    m_orderIndex = 0;
    m_originalAccountId = 0;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  @Override
  public String toString()
  {
    return getAccount() + "(" + new EnuColor( getColor() ) + ")";
  }


  // theses data come from database (Game table)
  // ------------------------------------------
  private int m_color = EnuColor.Unknown;
  private int m_OriginalColor = EnuColor.Unknown;
  /** number of weather hen at the last time step change. */
  private int m_workingWeatherHenCount = 0;
  private int m_ptAction = 0;


  private Date m_lastConnexion = new Date();
  @Serialized
  private List<String> m_notifSended = null;
  private long m_originalAccountId = 0;

  @Embedded
  private EbPublicAccount m_account = null;

  /** the following field is a reference to team ID of this player. */
  private long m_teamId = 0;
  /** the teamCache is present to increase some method speed but transient to avoid double persistence.
   * note that if we serialize EbRegistration in future, we will need to add 'transient' keyword, then
   * it should be initialized after received by client side. */
  @Transient
  private EbTeam m_teamCache = null;

  // theses data are moved to EbTeam
  // ===============================
  @Deprecated
  // replaced by EbTeam.fireColor
  protected int m_singleColor = EnuColor.Unknown;
  @Deprecated
  // replaced by EbTeam
  protected int m_orderIndex = 0;
  /** in turn by turn mode this is the end turn date.
   * and in parallel mode, this is the date up to which the board is locked (cf m_lockedPosition) */
  @Deprecated
  // replaced by EbTeam
  protected Date m_endTurnDate = null;
  /** in parallel mode, player lock a board area for a small period */
  @Deprecated
  // replaced by EbTeam
  @Serialized
  protected AnBoardPosition m_lockedPosition = null;
  /**
   * action list that player made during a parallel and hidden turn (ie deployement or take off turn)
   * these actions are seen by player but hidden to others. This event list will be merged with main log
   * at the end of current turn.
   */
  @Deprecated
  // replaced by EbTeam
  @Serialized
  protected List<AnEvent> m_myEvents = new ArrayList<AnEvent>();


  public EbTeam getTeam(GameData p_game)
  {
    if( m_teamCache == null && p_game != null )
    {
      m_teamCache = p_game.getTeam( m_teamId );
    }
    return m_teamCache;
  }

  public void setTeamId(long p_id)
  {
    m_teamId = p_id;
    m_teamCache = null;
  }

  public int getOreCount(Game p_game)
  {
    int count = 0;
    for( EbToken token : p_game.getSetToken() )
    {
      if( (token.getType().isOre()) && (token.getCarrierToken() != null)
          && (token.getCarrierToken().getType() == TokenType.Freighter)
          && getEnuColor().isColored( token.getCarrierToken().getColor() ) )
      {
        count += 1;
      }
    }
    return count;
  }

  public int getTokenCount(Game p_game)
  {
    int count = 0;
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.getEnuColor().isSingleColor() && getEnuColor().isColored( token.getColor() )
          && token.getType() != TokenType.Freighter && token.getType() != TokenType.Turret )
      {
        count++;
      }
    }
    return count;
  }

  /**
   * if game is finished, return final score
   * @param p_game
   * @return
   */
  public int estimateWinningScore(Game p_game)
  {
    int winningPoint = 0;
    if( p_game.isFinished() )
    {
      for( EbToken token : p_game.getSetToken() )
      {
        if( (token.getType() == TokenType.Freighter) && getEnuColor().isColored( token.getColor() )
            && (token.getLocation() == Location.EndGame) )
        {
          winningPoint += token.getWinningPoint();
        }
      }
    }
    else
    {
      for( EbToken token : p_game.getSetToken() )
      {
        if( (token.getColor() != EnuColor.None) && (getEnuColor().isColored( token.getColor() ))
            && (token.getLocation() == Location.Board || token.getLocation() == Location.EndGame) )
        {
          winningPoint += token.getWinningPoint();
        }
      }
    }
    winningPoint -= p_game.getInitialScore();
    return winningPoint;
  }



  /**
   * @return the account
   */
  public boolean haveAccount()
  {
    return getAccount() != null && getAccount().getId() > 0L;
  }



  /**
   * like getActionPt() but after been rounded according to selected time variant.
   * @return
   */
  public int getRoundedActionPt(Game p_game)
  {
    int futurActionPt = getPtAction() / p_game.getEbConfigGameTime().getRoundActionPt();
    futurActionPt *= p_game.getEbConfigGameTime().getRoundActionPt();
    if( futurActionPt > p_game.getEbConfigGameTime().getActionPtMaxReserve() - 15 )
    {
      futurActionPt = p_game.getEbConfigGameTime().getActionPtMaxReserve() - 15;
    }
    return futurActionPt;
  }

  public int getMaxActionPt(Game p_game)
  {
    int freighterCount = getOnBoardFreighterCount( p_game );
    return p_game.getEbConfigGameTime().getActionPtMaxReserve()
        + ((freighterCount - 1) * p_game.getEbConfigGameTime().getActionPtMaxPerExtraShip());
  }


  private static int getDefaultActionInc(Game p_game)
  {
    int timeStep = p_game.getCurrentTimeStep();
    timeStep -= p_game.getEbConfigGameTime().getDeploymentTimeStep();
    int actionInc = p_game.getEbConfigGameTime().getActionPtPerTimeStep();

    if( timeStep <= 0 )
    {
      actionInc = 0;
    }
    else if( timeStep == 1 )
    {
      actionInc = actionInc / 3;
    }
    else if( timeStep == 2 )
    {
      actionInc = (2 * actionInc) / 3;
    }
    return actionInc;
  }


  public int getOnBoardFreighterCount(Game p_game)
  {
    int freighterCount = getEnuColor().getNbColor();
    // after turn 21, we really count number of landed freighter
    if( p_game.getCurrentTimeStep() >= p_game.getEbConfigGameTime().getTakeOffTurns().get( 0 ) )
    {
      freighterCount = 0;
      for( EbToken freighter : p_game.getAllFreighter( getColor() ) )
      {
        if( freighter.getLocation() == Location.Board )
          freighterCount++;
      }
    }
    return freighterCount;
  }

  public int getActionInc(Game p_game)
  {
    int action = 0;
    int freighterCount = getOnBoardFreighterCount( p_game );
    if( freighterCount >= 1 )
    {
      action += getDefaultActionInc( p_game );
      action += (freighterCount - 1) * p_game.getEbConfigGameTime().getActionPtPerExtraShip();
    }
    return action;
  }

  public boolean isNotifSended(String p_msgName)
  {
    if( m_notifSended == null )
    {
      return false;
    }
    return m_notifSended.contains( p_msgName );
  }

  public void clearNotifSended()
  {
    m_notifSended = null;
  }

  public void addNotifSended(String p_msgName)
  {
    if( m_notifSended == null )
    {
      m_notifSended = new ArrayList<String>();
    }
    m_notifSended.add( p_msgName );
  }

  // getters / setters
  // -----------------
  public void setEnuColor(EnuColor p_color)
  {
    m_color = p_color.getValue();
    if( getTeam( null ) != null )
      getTeam( null ).clearColorsCache();
  }

  public EnuColor getEnuColor()
  {
    return new EnuColor( getColor() );
  }

  /**
   * @return the bitfield Color
   */
  public int getColor()
  {
    return m_color;
  }

  /**
   * @return the ptAction
   */
  public int getPtAction()
  {
    return m_ptAction;
  }

  /**
   * @param p_ptAction the ptAction to set
   */
  public void setPtAction(int p_ptAction)
  {
    m_ptAction = p_ptAction;
  }



  /**
   * @param p_color the color to set
   */
  public void setColor(int p_color)
  {
    setEnuColor( new EnuColor( p_color ) );
  }



  /**
   * @return the originalColor
   */
  public int getOriginalColor()
  {
    return m_OriginalColor;
  }

  /**
   * @param p_originalColor the originalColor to set
   */
  public void setOriginalColor(int p_originalColor)
  {
    m_OriginalColor = p_originalColor;
  }





  /**
   * @return the account
   */
  public EbPublicAccount getAccount()
  {
    return m_account;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccount(EbPublicAccount p_account)
  {
    m_account = p_account;
  }


  public int getWorkingWeatherHenCount()
  {
    return m_workingWeatherHenCount;
  }

  public void setWorkingWeatherHenCount(int p_workingWeatherHenCount)
  {
    m_workingWeatherHenCount = p_workingWeatherHenCount;
  }

  /**
   * @return the lastConnexion
   */
  public Date getLastConnexion()
  {
    return m_lastConnexion;
  }

  /**
   * @param p_lastConnexion the lastConnexion to set
   */
  public void updateLastConnexion()
  {
    if( m_lastConnexion == null )
    {
      m_lastConnexion = new Date();
    }
    m_lastConnexion.setTime( SharedMethods.currentTimeMillis() );
  }

  /**
   * @return the isReplacement
   */
  public boolean isReplacement()
  {
    return m_originalAccountId != 0;
  }


  public long getOriginalAccountId()
  {
    return m_originalAccountId;
  }

  public EbPublicAccount getOriginalAccount(Game p_game)
  {
    return p_game.getAccount( getOriginalAccountId() );
  }

  public void setOriginalAccountId(long p_originalAccountId)
  {
    m_originalAccountId = p_originalAccountId;
  }


}
