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

import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 * move a token from a carrier token to a board position
 */
public class EbEvtUnLoad extends AnEventPlay
{
  static final long serialVersionUID = 1;


  /**
   * 
   */
  public EbEvtUnLoad()
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
    setCost( 1 );
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtUnLoad;
  }


  @Override
  // @Transient
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    if( getTokenCarrier(p_game) != null )
    {
      return getTokenCarrier(p_game).getPosition();
    }
    else
    {
      return getNewPosition();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    // check both token is on board
    if( (getToken(p_game).getLocation() != Location.Token)
        || (getTokenCarrier(p_game).getLocation() != Location.Board) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getToken(p_game) + " can't be moved from location "
          + getToken(p_game).getLocation(), this );
    }
    // check that player control both token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ), this );
    }
    if( getToken(p_game).canBeColored() && getToken(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ), this );
    }
    if( (!myRegistration.getEnuColor().isColored( getTokenCarrier( p_game ).getColor() )
        || getTokenCarrier(p_game).getColor() == EnuColor.None )
        && getTokenCarrier( p_game ).getType() != TokenType.Freighter
        && getTokenCarrier( p_game ).getType() != TokenType.Warp )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getTokenCarrier( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ), this );
    }
    // check no hexagon are skipped
    if( !getTokenCarrier( p_game ).isNeighbor( p_game.getCoordinateSystem(), getNewPosition() ) )
    {
      // unusual error: no i18n
      throw new RpcFmpException( "You must select all moving step without any gap between them.", this );
    }
    // check that carrier isn't tide deactivate
    if( !p_game.isTokenTideActive( getToken( p_game ).getCarrierToken() ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableTide(
          Messages.getTokenString( getAccountId(), getToken( p_game ).getCarrierToken() ) ), this );
    }
    // check that carrier isn't fire deactivate
    if( !p_game.isTokenFireActive( getToken( p_game )
        .getCarrierToken() ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableFire(
          Messages.getTokenString( getAccountId(), getToken( p_game ).getCarrierToken() ),
          Messages.getColorString( getAccountId(), p_game.getOpponentFireCover( getToken( p_game ).getCarrierToken() )
              .getValue() ) ), this );
    }

    // check token move to a 'clear' hexagon
    boolean moveToPontoon = false;
    Set<EbToken> tokensOnWay = p_game.getAllToken( getNewPosition() );
    if( getToken( p_game ).getHexagonSize() == 2 )
    {
      tokensOnWay.addAll( p_game.getAllToken( p_game.getCoordinateSystem().getNeighbor(
          getNewPosition(), getNewPosition().getSector() ) ) );
    }
    for( EbToken token : tokensOnWay )
    {
      if( (token != getToken( p_game )) )
      {
        if( (token.getType() != TokenType.Pontoon) && (token.getType() != TokenType.Sluice)
            && (token.getType() != TokenType.Ore2Generator) && (token.getType() != TokenType.Ore3Generator) )
        {
          throw new RpcFmpException( "Vous devez déplacer votre pions sur une case libre", this );
        }
        else
        {
          if( token.canLoad( getToken( p_game ).getType() ) )
          {
            moveToPontoon = true;
          }
          else
          {
            throw new RpcFmpException( "Cette unité ne peut pas ce deplacer sur le "
                + token.getType(), this );
          }
        }
      }
    }
    if( !moveToPontoon )
    {
      // check this token is allowed to move to this hexagon
      if( !p_game.canTokenMoveOn( getToken( p_game ), getNewPosition() ) )
      {
        throw new RpcFmpException( errMsg().CantMoveOn(
            Messages.getTokenString( getAccountId(), getToken( p_game ) ),
            Messages.getLandString( getAccountId(), p_game.getLand( getNewPosition() ) ) ), this );
      }
    }
    // check this token is not going to an opponent fire cover
    EnuColor fireCoverColor = p_game.getOpponentFireCover( getMyTeam(p_game).getColors(p_game.getPreview()),
        getNewPosition() );
    if( fireCoverColor.getValue() != EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDisableFire(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getColorString( getAccountId(), fireCoverColor.getValue() ) ), this );
    }
    if( !p_game.isTokenFireActive( getTokenCarrier( p_game ) ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableFire(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ),
          Messages.getColorString( getAccountId(),
              p_game.getOpponentFireCover( getTokenCarrier( p_game ) ).getValue() ) ), this );
    }
    // if a pontoon, check it is linked to ground
    if( (getToken(p_game).getType() == TokenType.Pontoon)
        && (!p_game.isPontoonLinkToGround( getNewPosition() )) )
    {
      throw new RpcFmpException( "les pontons doivent etres relie a la terre", this );
    }

    // check that token don't leave from a destroyed turret
    if( getTokenCarrier(p_game).getType() == TokenType.Freighter )
    {
      boolean turretFound = false;
      for( AnBoardPosition neighborPosition : p_game.getCoordinateSystem().getAllNeighbors( getNewPosition() ) )
      {
        EbToken token = p_game.getToken( neighborPosition, TokenType.Turret );
        if( token != null )
        {
          turretFound = true;
        }
      }
      if( !turretFound )
      {
        throw new RpcFmpException( "Vous ne pouvez pas décharger par un pod detruit.", this );
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
    p_game.moveToken( getToken(p_game), getNewPosition() );
    getToken(p_game).incVersion();
    getTokenCarrier(p_game).incVersion();

    execFireDisabling( p_game, getToken( p_game ).getPosition() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    if( getTokenCarrier( p_game ).getType() == TokenType.Teleporter )
    {
      List<EbToken> freighters = p_game.getAllFreighter( getTokenCarrier( p_game ).getColor() );
      if( freighters.size() > 0 )
      {
        p_game.moveToken( getToken( p_game ), freighters.get( 0 ) );
        freighters.get( 0 ).decVersion();
      }
    }
    else if( getTokenCarrier( p_game ).getType() == TokenType.Warp )
    {
      p_game.moveToken( getToken( p_game ), p_game.getMainWarp() );
    }
    else
    {
      p_game.moveToken( getToken( p_game ), getTokenCarrier( p_game ) );
    }
    getToken(p_game).decVersion();
    getTokenCarrier( p_game ).decVersion();

    unexecFireDisabling( p_game );
  }

  // Bean getter / setter
  // ====================
  @Override
  public void setToken(EbToken p_token)
  {
    super.setToken( p_token );
    setCost( 1 );
    if( p_token.getType() == TokenType.Ore0 )
    {
      setCost( 0 );
    }
    if( p_token.getType() == TokenType.Teleporter )
    {
      setCost( 4 );
    }

  }

}
