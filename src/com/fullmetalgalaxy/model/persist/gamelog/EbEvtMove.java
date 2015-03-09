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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.BoardFireCover.FdChange;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.FireDisabling;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;




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
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    if(getOldPosition() != null)
    {
      return getOldPosition();
    }
    return getToken(p_game).getPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
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
    if( getToken( p_game ).getType() == TokenType.Teleporter )
    {
      throw new RpcFmpException( "Le teleporter ne se deplace pas seul" );
    }
    // check no hexagon are skipped
    AnBoardPosition tokenPosition = getToken(p_game).getPosition();
    if( !tokenPosition.equals( getNewPosition() )
        && !p_game.getCoordinateSystem().areNeighbor( tokenPosition, getNewPosition() )
        && ((getToken(p_game).getHexagonSize() == 1) || !p_game.getCoordinateSystem().getNeighbor( tokenPosition,
            tokenPosition.getSector() ).equals(
            p_game.getCoordinateSystem().getNeighbor( getNewPosition(), getNewPosition().getSector() ) )) )
    {
      // unusual error: no i18n
      throw new RpcFmpException( "You must select all moving step without any gap between them." );
    }
    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }

    // check this token is allowed to move from this hexagon
    if( !p_game.isTokenTideActive( getToken(p_game) ) )
    {
      throw new RpcFmpException( errMsg().CantMoveOn(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getLandString( getAccountId(), p_game.getLand( tokenPosition ) ) ) );
    }
    // check token move to a 'clear' hexagon
    boolean moveToPontoon = false;
    Set<EbToken> tokensOnWay = p_game.getAllToken( getNewPosition() );
    for( EbToken token : tokensOnWay )
    {
      if( (token != getToken( p_game )) && (token.getType() != TokenType.Pontoon)
          && (token.getType() != TokenType.Sluice) )
      {
        // TODO i18n
        throw new RpcFmpException( "Vous devez déplacer votre pions sur une case libre" );
      }
      if( token.getType() == TokenType.Pontoon || token.getType() == TokenType.Sluice )
      {
        if( token.canLoad( getToken(p_game).getType() ) )
        {
          moveToPontoon = true;
        }
        else
        {
          // TODO i18n
          throw new RpcFmpException( Messages.getTokenString( 0, getToken( p_game ) )
              + " ne peut pas se deplacer sur le "
              + Messages.getTokenString( 0, token ) );
        }
      }
    }
    if( !moveToPontoon )
    {
      // check this token is allowed to move to this hexagon
      // note that any token are allowed to voluntary stuck themself into reef of marsh
      if( !p_game.canTokenMoveOn( getToken( p_game ), getNewPosition() ) )
      {
        throw new RpcFmpException( errMsg().CantMoveOn(
            Messages.getTokenString( getAccountId(), getToken( p_game ) ),
            Messages.getLandString( getAccountId(), p_game.getLand( getNewPosition() ) ) ) );
      }
      
    }

    // check this token is not going from AND to an opponent fire cover
    // Not sure about necessity of the following test.
    EnuColor fireCoverColorNew = p_game.getOpponentFireCover( myRegistration.getTeam( p_game ).getColors(p_game.getPreview()),
        getNewPosition() );
    if( getToken( p_game ).isFireDisabled() && fireCoverColorNew.getValue() != EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDisableFire(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getColorString( getAccountId(), fireCoverColorNew.getValue() ) ) );
    }
    EnuColor fireCoverColorOld = p_game.getOpponentFireCover( getToken( p_game ) );

    exec( p_game );
    fireCoverColorNew = p_game.getOpponentFireCover( getToken( p_game ) );
    unexec( p_game );
    if( (fireCoverColorOld.getValue() != EnuColor.None)
        && (p_game.getLastLog() != null)
        && (p_game.getLastLog().getType() == GameLogType.EvtMove)
        && (((EbEvtMove)p_game.getLastLog()).getPackedToken().getId() == getPackedToken().getId()) )
    {
      throw new RpcFmpException( errMsg().CantMoveDisableFire(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getColorString( getAccountId(), fireCoverColorOld.getValue() ) ) );
    }
    if( (fireCoverColorOld.getValue() != EnuColor.None)
        && (fireCoverColorNew.getValue() != EnuColor.None) )
    {
      throw new RpcFmpException( errMsg().CantMoveDisableFire(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getColorString( getAccountId(), fireCoverColorNew.getValue() ) ) );
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
    setOldPosition( getToken(p_game).getPosition() );

    p_game.moveToken( getToken(p_game), getNewPosition() );
    checkFireDisabling( p_game );
    getToken(p_game).incVersion();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    p_game.moveToken( getToken(p_game), getOldPosition() );
    getToken(p_game).decVersion();

    unexecFireDisabling( p_game );
  }


  private void checkFireDisabling(Game p_game)
  {
    if( isFdComputed() )
    {
      // save CPU by avoiding recompute fire disabling flags
      p_game.getBoardFireCover().addFireDisabling( getFdAdded() );
      p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
    }
    else
    {
      int fireRange = p_game.getTokenFireLength( getToken( p_game ) );
      List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
      List<FireDisabling> fdAdded = new ArrayList<FireDisabling>();

      if( getToken( p_game ).isFireDisabling() )
      {
        // lock current moving destroyer and remove all his fire disabling
        // action
        // to allow his target to defend themself
        p_game.getBoardFireCover().decFireCover( getToken( p_game ) );
        AbstractCollection<FireDisabling> fd2Remove = new ArrayList<FireDisabling>();
        for( FireDisabling fd : getToken( p_game ).getFireDisablingList() )
        {
          fd2Remove.add( fd );
          if( fd.getDestroyer1Id() != getToken( p_game ).getId() )
          {
            for( FireDisabling fd2 : fd.getDestroyer1( p_game ).getFireDisablingList() )
            {
              if( fd2.getTargetId() != fd.getTargetId()
                  && p_game.canTokenFireOn( fd.getTarget( p_game ), fd2.getDestroyer2( p_game ) ) )
              {
                // may be a small bug: we shouldn't removed fd2 if fd2 is older than fd...
                fd2Remove.add( fd2 );
              }
            }
          }
          else if( fd.getDestroyer2Id() != getToken( p_game ).getId() )
          {
            for( FireDisabling fd2 : fd.getDestroyer2( p_game ).getFireDisablingList() )
            {
              if( fd2.getTargetId() != fd.getTargetId()
                  && p_game.canTokenFireOn( fd.getTarget( p_game ), fd2.getDestroyer1( p_game ) ) )
              {
                fd2Remove.add( fd2 );
              }
            }
          }
        }

        fdRemoved.addAll( fd2Remove );
        p_game.getBoardFireCover().removeFireDisabling( fd2Remove );
        // check if old target can disable unit
        for( FireDisabling fd : fd2Remove )
        {
          EbToken target = fd.getTarget( p_game );
          if( !target.isFireDisabled() )
          {
            p_game.getBoardFireCover().checkFireDisableFlag( target.getPosition(),
                p_game.getTokenFireLength( target ),
                FdChange.fromDestroyerFireDisableStatus( target.isFireDisabled() ), fdRemoved,
                fdAdded );
          }
        }
        // now check if old target can be re-disable with other destroyer
        for( FireDisabling fd : fd2Remove )
        {
          EbToken target = fd.getTarget( p_game );
          if( !target.isFireDisabled() )
          {
            p_game.getBoardFireCover().recursiveCheckFireDisableFlag( target, FdChange.DISABLE,
                fdRemoved, fdAdded );
          }
        }

        // p_game.getBoardFireCover().checkFireDisableFlag( getToken( p_game
        // ).getPosition(),
        // fireRange - 1, fdRemoved, fdAdded );
        p_game.getBoardFireCover().incFireCover( getToken( p_game ) );
      }
      else if( getToken( p_game ).isFireDisabled() )
      {
        // if token was fire disabled, the only allowed moved is to leave a fire
        // cover...
        List<FireDisabling> fd2Remove = new ArrayList<FireDisabling>();
        fd2Remove.addAll( getToken( p_game ).getFireDisablingList() );
        fdRemoved.addAll( getToken( p_game ).getFireDisablingList() );
        p_game.getBoardFireCover().removeFireDisabling( fd2Remove );
      }

      // fire range +1 to look for all tokens that may be impacted (ie enter or
      // leave fire cover)
      p_game.getBoardFireCover().checkFireDisableFlag( getToken( p_game ).getPosition(),
          fireRange + 1,
          FdChange.fromDestroyerFireDisableStatus( getToken( p_game ).isFireDisabled() ),
          fdRemoved, fdAdded );

      p_game.getBoardFireCover().cleanFireDisableCollection( fdRemoved, fdAdded );

      addFdRemoved( fdRemoved );
      addFdAdded( fdAdded );
      setFdComputed( true );
    }
  }


}
