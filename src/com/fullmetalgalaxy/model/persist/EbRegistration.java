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

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.TokenType;


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
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }


  // theses data come from database (Game table)
  // ------------------------------------------
  private int m_color = EnuColor.Unknown;
  private int m_OriginalColor = EnuColor.Unknown;
  private int m_ptAction = 0;
  private int m_orderIndex = 0;
  private int m_turretsToRepair = 0;
  /** number of weather hen at the last time step change. */
  private int m_workingWeatherHenCount = 0;
  private Date m_endTurnDate = null;


  // this id/pseudo are kept only for backward compatibility with older game
  public long m_accountId = 0L;
  public String m_accountPseudo = null;
  private EbPublicAccount m_account = null;


  public int getOreCount(Game p_game)
  {
    int count = 0;
    for( EbToken token : p_game.getSetToken() )
    {
      if( (token.getType() == TokenType.Ore) && (token.getCarrierToken() != null)
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

  public int estimateWinningScore(Game p_game)
  {
    int winningPoint = 0;
    for( EbToken token : p_game.getSetToken() )
    {
      if( (token.getColor() != EnuColor.None) && (getEnuColor().isColored( token.getColor() )) 
          && (token.getLocation() == Location.Board || token.getLocation() == Location.EndGame) )
      {
        winningPoint += token.getWinningPoint();
      }
    }
    winningPoint -= p_game.getEbConfigGameVariant().getInitialScore();
    return winningPoint;
  }


  public int getWinningScore(Game p_game)
  {
    int winningPoint = 0;
    for( EbToken token : p_game.getSetToken() )
    {
      if( (token.getType() == TokenType.Freighter) && getEnuColor().isColored( token.getColor() )
          && (token.getLocation() == Location.EndGame) )
      {
        winningPoint += token.getWinningPoint();
      }
    }
    winningPoint -= p_game.getEbConfigGameVariant().getInitialScore();
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
    return futurActionPt;
  }

  // getters / setters
  // -----------------
  public void setEnuColor(EnuColor p_color)
  {
    m_color = p_color.getValue();
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
   * @return the orderIndex
   */
  public int getOrderIndex()
  {
    return m_orderIndex;
  }

  /**
   * @param p_orderIndex the orderIndex to set
   */
  public void setOrderIndex(int p_orderIndex)
  {
    m_orderIndex = p_orderIndex;
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
   * @return the turretsToRepair
   */
  public int getTurretsToRepair()
  {
    return m_turretsToRepair;
  }

  /**
   * @param p_turretsToRepair the turretsToRepair to set
   */
  public void setTurretsToRepair(int p_turretsToRepair)
  {
    m_turretsToRepair = p_turretsToRepair;
  }

  /**
   * @return the endTurnDate
   */
  public Date getEndTurnDate()
  {
    return m_endTurnDate;
  }

  /**
   * Warning, it's recommended to use EbGame.setEndTurnDate instead of this one.
   * @param p_endTurnDate the endTurnDate to set
   */
  public void setEndTurnDate(Date p_endTurnDate)
  {
    m_endTurnDate = p_endTurnDate;
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


}
