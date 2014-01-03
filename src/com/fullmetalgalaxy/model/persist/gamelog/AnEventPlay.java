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
package com.fullmetalgalaxy.model.persist.gamelog;



import java.util.Date;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 *
 */
public class AnEventPlay extends AnEventUser
{
  static final long serialVersionUID = 1;

  private long m_registrationId = 0L;
  /**
   * action point cost of this action.
   */
  private int m_cost = 0;

  private AnBoardPosition m_position = null;
  private AnBoardPosition m_newPosition = null;
  private AnBoardPosition m_oldPosition = null;

  private EbBase m_packedToken = null;
  private EbBase m_packedTokenCarrier = null;
  private EbBase m_packedNewTokenCarrier = null;
  private EbBase m_packedTokenDestroyer1 = null;
  private EbBase m_packedTokenDestroyer2 = null;



  public AnEventPlay()
  {
    super();
    init();
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_registrationId = 0L;
    m_cost = 0;
    m_newPosition = null;
    m_packedToken = null;
    m_packedTokenCarrier = null;

    m_packedNewTokenCarrier = null;

    m_position = null;

    m_oldPosition = null;

    m_packedTokenDestroyer1 = null;
    m_packedTokenDestroyer2 = null;
  }

  /**
   * @param p_game 
   * @return a board position where action is done or null if not relevant.
   */
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    return null;
  }

  /**
   * set player AND account
   * @param p_registration
   */
  public void setRegistration(EbRegistration p_registration)
  {
    if( p_registration == null )
    {
      m_registrationId = 0;
      return;
    }
    m_registrationId = p_registration.getId();
    if( p_registration.getAccount() == null )
    {
      setAccountId( 0 );
    }
    else
    {
      setAccountId( p_registration.getAccount().getId() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( isAuto() )
    {
      // if event is auto generated, assume everything are correct
      return;
    }
    EbRegistration myRegistration = getMyRegistration( p_game );
    if( myRegistration == null )
    {
      // no i18n ?
      throw new RpcFmpException( "you didn't join this game." );
    }
    if( myRegistration.getPtAction() < getCost() && p_game.getGameType() != GameType.Practice )
    {
      throw new RpcFmpException( errMsg().NotEnouthActionPt() );
    }
    if( (!p_game.isParallel() || (p_game.getCurrentTimeStep() <= 1))
        && (!p_game.getCurrentPlayerIds().contains( myRegistration.getId() ))
        && p_game.getGameType() != GameType.Practice )
    {
      throw new RpcFmpException( errMsg().NotYourTurn() );
    }
    if( (p_game.getStatus() == GameStatus.Open || p_game.getStatus() == GameStatus.Pause)
        && p_game.getGameType() != GameType.Practice )
    {
      throw new RpcFmpException( errMsg().gameNotStarted() );
    }

    if( p_game.isParallel()
        && p_game.getCurrentTimeStep() > p_game.getEbConfigGameTime().getDeploymentTimeStep() )
    {
      EbTeam team = p_game.getOtherTeamBoardLocked( myRegistration,
          getLockedPosition(), SharedMethods.currentTimeMillis() );
      if( team != null )
      {
        throw new RpcFmpException( errMsg().boardLocked() );
      }
    }
  }

  private AnBoardPosition getLockedPosition()
  {
    AnBoardPosition position = getPosition();
    if( position == null )
    {
      position = getNewPosition();
    }
    if( position == null )
    {
      position = getOldPosition();
    }
    return position;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbRegistration registration = getMyRegistration(p_game);
    if( registration != null )
    {
      registration.setPtAction( registration.getPtAction() - getCost() );
      // registration.setLastUpdate( getLastUpdate() );
      if( p_game.isParallel() )
      {
        registration.getTeam(p_game).setLockedPosition( getLockedPosition() );
        registration.getTeam(p_game).setEndTurnDate(
            new Date( SharedMethods.currentTimeMillis()
            + p_game.getEbConfigGameTime().getLockGameInMillis() ) );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbRegistration registration = getMyRegistration(p_game);
    if( registration != null )
    {
      registration.setPtAction( registration.getPtAction() + getCost() );
      // registration.setLastUpdate( getOldUpdate() );
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += getCost() + "pt : ";
    return str;
  }

  @Override
  public EbRegistration getMyRegistration(Game p_game)
  {
    EbRegistration registration = p_game.getRegistration( getRegistrationId() );
    if( registration != null )
    {
      return registration;
    }
    return super.getMyRegistration( p_game );
  }

  public EbTeam getMyTeam(Game p_game)
  {
    return getMyRegistration( p_game ).getTeam( p_game );
  }

  // Bean getter / setter
  // ====================
  /**
   * @return the cost
   */
  public int getCost()
  {
    return m_cost;
  }

  /**
   * @param p_cost the cost to set
   */
  public void setCost(int p_cost)
  {
    m_cost = p_cost;
  }

  /**
   * @return the newPosition
   */
  public AnBoardPosition getNewPosition()
  {
    return m_newPosition;
  }

  /**
   * @param p_newPosition the newPosition to set
   */
  public void setNewPosition(AnBoardPosition p_newPosition)
  {
    m_newPosition = p_newPosition;
  }

  /**
   * @return the packedToken
   */
  protected EbBase getPackedToken()
  {
    return m_packedToken;
  }


  /**
   * @return the packedTokenCarrier
   */
  protected EbBase getPackedTokenCarrier()
  {
    return m_packedTokenCarrier;
  }


  public AnBoardPosition getOldPosition()
  {
    return m_oldPosition;
  }

  /**
   * @param p_oldPosition the oldPosition to set
   */
  protected void setOldPosition(AnBoardPosition p_oldPosition)
  {
    m_oldPosition = p_oldPosition;
  }

  /**
   * @return the position
   */
  public AnBoardPosition getPosition()
  {
    return m_position;
  }

  /**
   * @param p_position the position to set
   */
  public void setPosition(AnBoardPosition p_position)
  {
    m_position = p_position;
  }


  /**
   * @return the packedNewTokenCarrier
   */
  protected EbBase getPackedNewTokenCarrier()
  {
    return m_packedNewTokenCarrier;
  }

  /**
   * @return the packedTokenDestroyer1
   */
  protected EbBase getPackedTokenDestroyer1()
  {
    return m_packedTokenDestroyer1;
  }

  /**
   * @return the packedTokenDestroyer2
   */
  protected EbBase getPackedTokenDestroyer2()
  {
    return m_packedTokenDestroyer2;
  }

  /**
   * @return the registrationId
   */
  public long getRegistrationId()
  {
    return m_registrationId;
  }



  // cache to avoid researching again and again
  // and to implement getter
  // ===========================================
  transient protected EbToken m_token = null;
  transient private EbToken m_tokenCarrier = null;
  transient private EbToken m_newTokenCarrier = null;
  transient private EbToken m_tokenDestroyer1 = null;
  transient private EbToken m_tokenDestroyer2 = null;

  /**
   * @param p_game game to apply event
   * @return the token
   */
  public EbToken getToken(Game p_game)
  {
    if( m_token == null && getPackedToken() != null )
    {
      m_token = p_game.getToken( getPackedToken().getId() );
    }
    return m_token;
  }

  /**
   * @param p_game game to apply event
   * @return the tokenCarrier
   */
  public EbToken getTokenCarrier(Game p_game)
  {
    if( m_tokenCarrier == null && getPackedTokenCarrier() != null )
    {
      m_tokenCarrier = p_game.getToken( getPackedTokenCarrier().getId() );
    }
    return m_tokenCarrier;
  }

  /**
   * @param p_game game to apply event
   * @return the newTokenCarrier
   */
  public EbToken getNewTokenCarrier(Game p_game)
  {
    if( m_newTokenCarrier == null && getPackedNewTokenCarrier() != null )
    {
      m_newTokenCarrier = p_game.getToken( getPackedNewTokenCarrier().getId() );
    }
    return m_newTokenCarrier;
  }

  /**
   * @param p_game game to apply event
   * @return the tokenDestroyer1
   */
  public EbToken getTokenDestroyer1(Game p_game)
  {
    if( m_tokenDestroyer1 == null && getPackedTokenDestroyer1() != null )
    {
      m_tokenDestroyer1 = p_game.getToken( getPackedTokenDestroyer1().getId() );
    }
    return m_tokenDestroyer1;
  }

  /**
   * @param p_game game to apply event
   * @return the tokenDestroyer2
   */
  public EbToken getTokenDestroyer2(Game p_game)
  {
    if( m_tokenDestroyer2 == null && getPackedTokenDestroyer2() != null )
    {
      m_tokenDestroyer2 = p_game.getToken( getPackedTokenDestroyer2().getId() );
    }
    return m_tokenDestroyer2;
  }


  /**
   * @param p_token the token to set
   */
  public void setToken(EbToken p_token)
  {
    if( p_token != null )
    {
      m_packedToken = p_token.createEbBase();
    }
    else
    {
      m_packedToken = null;
    }
    m_token = p_token;
  }

  /**
   * @param p_tokenCarrier the tokenCarrier to set
   */
  public void setTokenCarrier(EbToken p_tokenCarrier)
  {
    if( p_tokenCarrier != null )
    {
      m_packedTokenCarrier = p_tokenCarrier.createEbBase();
    }
    else
    {
      m_packedTokenCarrier = null;
    }
    m_tokenCarrier = p_tokenCarrier;
  }

  /**
   * @param p_tokenDestroyer1 the tokenDestroyer1 to set
   */
  public void setTokenDestroyer1(EbToken p_tokenDestroyer1)
  {
    if( p_tokenDestroyer1 != null )
    {
      m_packedTokenDestroyer1 = p_tokenDestroyer1.createEbBase();
    } else {
      m_packedTokenDestroyer1 = null;
    }
    m_tokenDestroyer1 = p_tokenDestroyer1;
  }

  /**
   * @param p_tokenDestroyer2 the tokenDestroyer2 to set
   */
  public void setTokenDestroyer2(EbToken p_tokenDestroyer2)
  {
    if( p_tokenDestroyer2 != null )
    {
      m_packedTokenDestroyer2 = p_tokenDestroyer2.createEbBase();
    } else {
      m_packedTokenDestroyer2 = null;
    }
    m_tokenDestroyer2 = p_tokenDestroyer2;
  }

  /**
   * @param p_tokenDestroyer2 the tokenDestroyer2 to set
   */
  public void setNewTokenCarrier(EbToken p_newTokenCarrier)
  {
    if( p_newTokenCarrier != null )
    {
      m_packedNewTokenCarrier = p_newTokenCarrier.createEbBase();
    } else {
      m_packedNewTokenCarrier = null;
    }
    m_newTokenCarrier = p_newTokenCarrier;
  }



}
