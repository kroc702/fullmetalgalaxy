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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 * increment one time step.
 */
public class EbEvtTimeStep extends AnEvent
{
  static final long serialVersionUID = 1;

  private Date m_oldTimeStepChange = null;
  private Set<EbToken> oreToRemoveWhileUnexec = null;

  /**
   * 
   */
  public EbEvtTimeStep()
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
    m_oldTimeStepChange = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtTimeStep;
  }


  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    assert p_game.getLastTimeStepChange().equals( getOldTimeStepChange() );
    if( p_game.getStatus() != GameStatus.Running )
    {
      // no i18n
      throw new RpcFmpException( "game not started" );
    }
    if( p_game.isFinished() )
    {
      // no i18n
      throw new RpcFmpException( "game is finished" );
    }
    if( getOldTimeStepChange() != null
        && !getOldTimeStepChange().equals( p_game.getLastTimeStepChange() ) )
    {
      // no i18n as it shoudln't occur
      throw new RpcFmpException( "EvtTimeStep have incoherant old time step change" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    Game game = p_game;
    assert game != null;

    // first backup data
    if( getOldTimeStepChange() == null )
    {
      setOldTimeStepChange( p_game.getLastTimeStepChange() );
    }

    game.setLastTimeStepChange( new Date( getOldTimeStepChange().getTime()
        + game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
    game.setCurrentTimeStep( game.getCurrentTimeStep() + 1 );
    if( p_game.isParallel() )
    {
      // update all players action point
      for( EbRegistration registration : game.getSetRegistration() )
      {
        int action = registration.getPtAction();
        action += registration.getActionInc( game );
        if( (action > FmpConstant.maximumActionPtWithoutLanding)
            && (!game.isLanded( registration.getEnuColor() )) )
        {
          action = FmpConstant.maximumActionPtWithoutLanding;
        }
        if( action > registration.getMaxActionPt( p_game ) )
        {
          action = registration.getMaxActionPt( p_game );
        }
        registration.setPtAction( action );
      }
    }
    // update all tokens bullets count
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.getType() != TokenType.Freighter
          && token.getColor() != EnuColor.None
          && token.getBulletCount() < token.getType().getMaxBulletCount() )
      {
        int multipleBulletIncrement = 1;
        if( token.getType().getMaxBulletCount() > 2 )
        {
          multipleBulletIncrement = (token.getType().getMaxBulletCount() / 2);
        }
        token.setBulletCount( token.getBulletCount()
            + multipleBulletIncrement * game.getEbConfigGameTime().getBulletCountIncrement() );
        if( token.getBulletCount() > token.getType().getMaxBulletCount() )
        {
          token.setBulletCount( token.getType().getMaxBulletCount() );
        }
      }
      if( (token.getType() == TokenType.Ore2Generator || token.getType() == TokenType.Ore3Generator)
          && token.getLocation() == Location.Board )
      {
        if( game.getAllToken( token.getPosition() ).size() >= 2 )
        {
          token.setBulletCount( 0 );
        } else if( token.getBulletCount() >= 2 ) {
          // create new ore token !
          token.setBulletCount( 0 );
          EbToken oreToken = new EbToken( TokenType.Ore );
          if( token.getType() == TokenType.Ore3Generator )
          {
            oreToken.setType( TokenType.Ore3 );
          }
          game.moveToken( oreToken, token.getPosition() );
          addOreToRemoveWhileUnexec( oreToken );
        } else {
          token.setBulletCount( token.getBulletCount()+game.getEbConfigGameTime().getBulletCountIncrement() );
        }

      }
    }

    if( oreToRemoveWhileUnexec != null )
    {
      for( EbToken ore : oreToRemoveWhileUnexec )
      {
        game.addToken( ore );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    Game game = p_game;
    assert game != null;
    game.setLastTimeStepChange( getOldTimeStepChange() );
    game.setCurrentTimeStep( game.getCurrentTimeStep() - 1 );
    if( p_game.isParallel() )
    {
      // update all players action point
      for( EbRegistration registration : game.getSetRegistration() )
      {
        int action = registration.getPtAction();
        int nbColor = registration.getEnuColor().getNbColor();
        if( nbColor >= 1 )
        {
          action -= game.getEbConfigGameTime().getActionPtPerTimeStep();
          action -= (nbColor - 1) * game.getEbConfigGameTime().getActionPtPerExtraShip();
        }
        if( action < 0 )
        {
          action = 0;
        }
        registration.setPtAction( action );
      }
    }

    if( oreToRemoveWhileUnexec != null )
    {
      for( EbToken ore : oreToRemoveWhileUnexec )
      {
        for( EbToken token : game.getAllToken( ore.getPosition() ) )
        {
          if( token.getType() == TokenType.Ore2Generator
              || token.getType() == TokenType.Ore3Generator )
          {
            token.setBulletCount( 2 );
          }
        }
      }
      p_game.getSetToken().removeAll( oreToRemoveWhileUnexec );
    }
  }


  private void addOreToRemoveWhileUnexec(EbToken ore)
  {
    if( oreToRemoveWhileUnexec == null )
    {
      oreToRemoveWhileUnexec = new HashSet<EbToken>();
    }
    oreToRemoveWhileUnexec.add( ore );
  }

  // Bean getter / setter
  // ====================

  /**
   * @return the oldTimeStepChange
   */
  private Date getOldTimeStepChange()
  {
    return m_oldTimeStepChange;
  }


  /**
   * @param p_oldTimeStepChange the oldTimeStepChange to set
   */
  private void setOldTimeStepChange(Date p_oldTimeStepChange)
  {
    m_oldTimeStepChange = p_oldTimeStepChange;
  }

}
