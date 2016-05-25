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



import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 * Land a Freighter from Orbit to a board position.
 */
public class EbEvtDeployment extends AnEventPlay
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public EbEvtDeployment()
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
    setCost( 0 );
  }

  @Override
  public String toString()
  {
    String str = super.toString();
    if( m_token != null )
    {
      str += m_token;
    }
    return str;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtDeployment;
  }


  @Override
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    return getPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);

    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ), this );
    }

    // check token is contained by a landed freighter
    EbToken freighter = getToken( p_game ).getCarrierToken();
    if( freighter == null || freighter.getType() != TokenType.Freighter
        || freighter.getLocation() != Location.Board )
    {
      // no i18n
      throw new RpcFmpException( "Your " + getToken( p_game )
          + " should be located in a landed freighter", this );
    }

    // check that, in turn by turn, player don't wan't to deploy too early
    if( !p_game.isParallel() && p_game.getEbConfigGameTime().getDeploymentTimeStep() != p_game.getCurrentTimeStep() )
    {
      throw new RpcFmpException( errMsg().mustWaitToDeploy( p_game.getEbConfigGameTime().getDeploymentTimeStep()), this );
    }
    
    // check that, player don't wan't to deploy too late
    if( p_game.getEbConfigGameTime().getDeploymentTimeStep() < p_game.getCurrentTimeStep() )
    {
      // no i18n
      throw new RpcFmpException( "Too late to deploy your units", this );
    }

    if( !p_game.canDeployUnit( getMyRegistration( p_game ) ) )
    {
      // no i18n
      throw new RpcFmpException( "You can't deploy your units after your first move", this );
    }
    
    // check token isn't deployed too far from his freighter
    if( getToken( p_game ).getHexagonSize() == 2 )
    {
      // barge must have at least one hex inside deployment area
      if( p_game.getCoordinateSystem().getDiscreteDistance( freighter.getPosition(), getPosition() ) > FmpConstant.deployementRadius
          && p_game.getCoordinateSystem().getDiscreteDistance( freighter.getPosition(),
              p_game.getCoordinateSystem().getNeighbor( getPosition(), getPosition().getSector() ) ) > FmpConstant.deployementRadius )
      {
        throw new RpcFmpException( errMsg().cantDeployTooFar( FmpConstant.deployementRadius ), this );
      }
    } else if( p_game.getCoordinateSystem().getDiscreteDistance( freighter.getPosition(), getPosition() ) > FmpConstant.deployementRadius )
    {
      throw new RpcFmpException( errMsg().cantDeployTooFar( FmpConstant.deployementRadius ), this );
    }

    // check token move to a 'clear' hexagon
    EbToken tokensOnWay = p_game.getToken( getPosition() );
    if( tokensOnWay != null )
    {
      // check that new token carrier can load this token
      if( !p_game.canTokenLoad( tokensOnWay, getToken( p_game ) ) )
      {
        throw new RpcFmpException( errMsg().CantLoad(
            Messages.getTokenString( getAccountId(), tokensOnWay ),
            Messages.getTokenString( getAccountId(), getToken( p_game ) ) ), this );
      }
    }
    else
    {
      // check this token is allowed to move to this hexagon
      if( !p_game.canTokenMoveOn( getToken( p_game ), getPosition() ) )
      {
        throw new RpcFmpException( errMsg().CantMoveOn(
            Messages.getTokenString( getAccountId(), getToken( p_game ) ),
            Messages.getLandString( getAccountId(), p_game.getLand( getPosition() ) ) ), this );
      }

      // for pontoon, check they are linked to the ground
      if( getToken( p_game ).getType() == TokenType.Pontoon
          && !p_game.isPontoonLinkToGround( getPosition() ) )
      {
        throw new RpcFmpException( errMsg().cantDeployPotoonInSea(), this );
      }
    }

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    // backup for unexec
    setTokenCarrier( getToken( p_game ).getCarrierToken() );

    EbToken tokensOnWay = p_game.getToken( getPosition() );
    if( tokensOnWay != null && tokensOnWay.getType() != TokenType.Pontoon
        && tokensOnWay.getType() != TokenType.Sluice )
    {
      p_game.moveToken( getToken( p_game ), tokensOnWay );
      tokensOnWay.incVersion();
    }
    else
    {
      p_game.moveToken( getToken( p_game ), getPosition() );
    }
    getToken(p_game).incVersion();

    execFireDisabling( p_game, getPosition() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    if( getTokenCarrier( p_game ) == null )
    {
      throw new RpcFmpException( "can't cancel deployement: old carrier isn't known", this );
    }
    super.unexec(p_game);

    if( getToken( p_game ).getCarrierToken() != null )
    {
      getToken( p_game ).getCarrierToken().decVersion();
    }
    p_game.moveToken( getToken( p_game ), getTokenCarrier( p_game ) );
    getToken(p_game).decVersion();
    // this update is here only to refresh token display
    p_game.updateLastTokenUpdate( null );

    unexecFireDisabling( p_game );
  }



}
