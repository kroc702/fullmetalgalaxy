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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;



/**
 * @author Vincent Legendre
 * This moving action is a basic token displacement of one hexagon only.
 */
public class EbEvtMove extends AnEventPlay
{
  static final long serialVersionUID = 1;




  /**
   * 
   */
  public EbEvtMove()
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
    return GameLogType.EvtMove;
  }


  @Override
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    return getOldPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);

    // check token is on board
    if( (getToken(p_game).getLocation() != Location.Board)
        && (getToken(p_game).getLocation() != Location.Token) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getToken(p_game) + " can't be moved from location "
          + getToken(p_game).getLocation() );
    }
    // check old position is egal to token position
    if( !getToken(p_game).getPosition().equals( getOldPosition() ) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "bad action" );
    }
    // check that token is colored
    if( getToken(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( "vous ne pouvez pas déplacer des pions incolores" );
    }
    // check that token is colored
    if( getToken(p_game).getType() == TokenType.Freighter )
    {
      throw new RpcFmpException( "vous ne pouvez pas déplacer votre astronef" );
    }
    // check no hexagon are skipped
    if( !getOldPosition().equals( getNewPosition() )
        && !getOldPosition().isNeighbor( getNewPosition() )
        && ((getToken(p_game).getHexagonSize() == 1) || !getOldPosition().getNeighbour(
            getOldPosition().getSector() ).equals(
            getNewPosition().getNeighbour( getNewPosition().getSector() ) )) )
    {
      // unusual error
      throw new RpcFmpException( RpcFmpException.TwoStepAreNotNeighbour );
    }
    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getToken(p_game).getColor(),
          myRegistration.getColor() );
    }

    // check this token is allowed to move from this hexagon
    if( !p_game.isTokenTideActive( getToken(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveOn, getToken(p_game).getType().ordinal(),
 p_game
          .getLand( getOldPosition() ).ordinal() );
    }
    // check token move to a 'clear' hexagon
    boolean moveToPontoon = false;
    Set<EbToken> tokensOnWay = p_game.getAllToken( getNewPosition() );
    for( EbToken token : tokensOnWay )
    {
      if( (token != getToken(p_game)) && (token.getType() != TokenType.Pontoon) )
      {
        throw new RpcFmpException( "Vous devez déplacer votre pions sur une case libre" );
      }
      if( token.getType() == TokenType.Pontoon )
      {
        if( token.canLoad( getToken(p_game).getType() ) )
        {
          moveToPontoon = true;
        }
        else
        {
          throw new RpcFmpException( "Ce bateau ne peut pas se deplacer sur le ponton" );
        }
      }
    }
    if( !moveToPontoon )
    {
      // check this token is allowed to move to this hexagon
      if( !p_game.canTokenMoveOn( getToken(p_game), getNewPosition() ) )
      {
        throw new RpcFmpException( RpcFmpException.CantMoveOn, getToken(p_game).getType().ordinal(),
            p_game.getLand( getNewPosition() ).ordinal() );
      }
    }
    // check this token is not going from AND to an opponent fire cover
    p_game.getBoardFireCover().decFireCover( getToken(p_game) );
    EnuColor fireCoverColorOld = p_game.getOpponentFireCover( myRegistration.getColor(),
        getOldPosition() );
    EnuColor fireCoverColorNew = p_game.getOpponentFireCover( myRegistration.getColor(),
        getNewPosition() );
    p_game.getBoardFireCover().incFireCover( getToken(p_game) );
    if( (p_game.getLastLog().getType() == GameLogType.EvtMove)
        && (((EbEvtMove)p_game.getLastLog()).getPackedToken().getId() == getPackedToken().getId())
        && (fireCoverColorOld.getValue() != EnuColor.None) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDisableFire, getToken( p_game ).getType()
          .ordinal(), fireCoverColorNew.getValue() );
    }
    if( (fireCoverColorOld.getValue() != EnuColor.None)
        && (fireCoverColorNew.getValue() != EnuColor.None) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDisableFire, getToken(p_game).getType()
          .ordinal(), fireCoverColorNew.getValue() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    // backup for unexec
    setOldPosition( getToken(p_game).getPosition() );
    
    p_game.moveToken( getToken(p_game), getNewPosition() );
    getToken(p_game).incVersion();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    p_game.moveToken( getToken(p_game), getOldPosition() );
    getToken(p_game).decVersion();
  }

}
