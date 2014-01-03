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

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.BoardFireCover.FdChange;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
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
 *
 */
public class EbEvtLoad extends AnEventPlay
{
  static final long serialVersionUID = 1;

  private int m_oldColor = EnuColor.None;

  /**
   * token list which as been put in graveyard after this action
   */
  private ArrayList<Long> m_TokenIds = null;

  /**
   * 
   */
  public EbEvtLoad()
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
    m_TokenIds = null;
    m_oldColor = EnuColor.None;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtLoad;
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

    // check both token is on board
    if( (getToken(p_game).getLocation() != Location.Board)
        || (getTokenCarrier(p_game).getLocation() != Location.Board) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getToken(p_game) + " can't be moved from location "
          + getToken(p_game).getLocation() );
    }
    // check old position is egal to token position
    if( !getToken(p_game).getPosition().equals( getToken(p_game).getPosition() ) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "bad action" );
    }
    // check that player control both token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken( p_game ).getColor() ) )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }
    if( getToken( p_game ).canBeColored() && getToken( p_game ).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }
    if( (!myRegistration.getEnuColor().isColored( getTokenCarrier( p_game ).getColor() )
        || getTokenCarrier( p_game ).getColor() == EnuColor.None )
        && getTokenCarrier( p_game ).getType() != TokenType.Freighter )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getTokenCarrier( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }

    // check that tokens are neighbor
    if( !getToken(p_game).isNeighbor( p_game.getCoordinateSystem(), getTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( "les deux pions ne sont pas cote a cote" );
    }

    // check this token is allowed to move from this hexagon
    if( !p_game.isTokenTideActive( getToken( p_game ) )
        && (!getToken( p_game ).getType().isOre() || getTokenCarrier( p_game ).getType() != TokenType.Crayfish) )
    {
      throw new RpcFmpException( errMsg()
          .CantMoveOn(
              Messages.getTokenString( getAccountId(), getToken( p_game ) ),
              Messages.getLandString( getAccountId(),
                  p_game.getLand( getToken( p_game ).getPosition() ) ) ) );
    }
    // check that new token carrier can load this token
    if( !p_game.canTokenLoad( getTokenCarrier(p_game), getToken(p_game) ) )
    {
      throw new RpcFmpException( errMsg().CantLoad(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ),
          Messages.getTokenString( getAccountId(), getToken( p_game ) ) ) );
    }
    // check that heap don't goes on mountain
    if(getToken(p_game).getType() == TokenType.Heap && p_game.getLand( getTokenCarrier( p_game ).getPosition() ) == LandType.Montain )
    {
      throw new RpcFmpException( errMsg().CantMoveOn(
          Messages.getTokenString( getAccountId(), getToken( p_game ) ),
          Messages.getLandString( getAccountId(),
              p_game.getLand( getTokenCarrier( p_game ).getPosition() ) ) ) );
    }
    // check tide/fire disable
    if( !p_game.isTokenTideActive( getTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableTide(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ) ) );
    }
    if( !p_game.isTokenFireActive( getTokenCarrier( p_game ) ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableFire(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ),
          Messages.getColorString( getAccountId(),
              p_game.getOpponentFireCover( getTokenCarrier( p_game ) ).getValue() ) ) );
    }
    if( !p_game.isTokenFireActive( getToken( p_game ) ) )
    {
      throw new RpcFmpException( errMsg().CantUnloadDisableFire(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ),
          Messages.getColorString( getAccountId(),
              p_game.getOpponentFireCover( getTokenCarrier( p_game ) ).getValue() ) ) );
    }
    if( getToken( p_game ).getColor() == EnuColor.None
        && p_game.getOpponentFireCover( getMyTeam( p_game ).getColors(p_game.getPreview()),
            getToken( p_game ).getPosition() ).getValue() != EnuColor.None )
    {
      // the colorless token is loaded from opponent fire cover
      throw new RpcFmpException( errMsg().CantUnloadDisableFire(
          Messages.getTokenString( getAccountId(), getTokenCarrier( p_game ) ),
          Messages.getColorString( getAccountId(),
              p_game.getOpponentFireCover( getTokenCarrier( p_game ) ).getValue() ) ) );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    boolean wasFdComputed = isFdComputed();

    // backup for unexec
    setOldColor( getToken(p_game).getColor() );
    setOldPosition( getToken(p_game).getPosition() );
    
    p_game.moveToken( getToken(p_game), getTokenCarrier(p_game) );
    getToken(p_game).incVersion();
    getTokenCarrier(p_game).incVersion();
      
    checkFireDisabling( p_game );

    // if token is a pontoon, check that other pontoon are linked to ground
    m_TokenIds = null ;
    if( getToken(p_game).getType() == TokenType.Pontoon )
    {
      for( AnBoardPosition neighborPosition : p_game.getCoordinateSystem().getAllNeighbors( getOldPosition()) )
      {
        EbToken otherPontoon = p_game.getToken( neighborPosition, TokenType.Pontoon );
        if( otherPontoon != null )
        {
          if( !p_game.isPontoonLinkToGround( otherPontoon ) )
          {
            if(m_TokenIds == null)
            {
              m_TokenIds = p_game.chainRemovePontoon( otherPontoon, getFdRemoved() );
            } else {
              m_TokenIds.addAll( p_game.chainRemovePontoon( otherPontoon, getFdRemoved() ) );
            }
          }
        }
      }
    }

    // then, for all removed pontoon, check fire disabling change
    if( wasFdComputed )
    {
      // save CPU by avoiding recompute fire disabling flags
      p_game.getBoardFireCover().addFireDisabling( getFdAdded() );
      p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
    }
    else if( m_TokenIds != null )
    {
      List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
      List<FireDisabling> fdAdded = new ArrayList<FireDisabling>();
      for( Long idToken : m_TokenIds )
      {
        EbToken token = p_game.getToken( idToken );
        if( token != null && token.getType().isDestroyer() )
        {
          p_game.getBoardFireCover().checkFireDisableFlag( token.getPosition(),
              p_game.getTokenFireLength( token ), FdChange.ENABLE, fdRemoved, fdAdded );
        }
      }
      addFdRemoved( fdRemoved );
      addFdAdded( fdAdded );
      setFdComputed( true );
    }
    else
    {
      setFdComputed( true );
    }
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
    if( getToken(p_game).canBeColored() )
    {
      getToken(p_game).setColor( getOldColor() );
    }
    getTokenCarrier(p_game).decVersion();
    // put back pontoon if there is some
    if( m_TokenIds != null )
    {
      for( Long id : m_TokenIds)
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) && (token.getLocation() == Location.Graveyard) )
        {
          p_game.moveToken( token, token.getPosition() );
          token.decVersion();
        }
      }
    }

    unexecFireDisabling( p_game );
  }

  // TODO merge with EbEvtMove
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
        List<FireDisabling> fd2Remove = new ArrayList<FireDisabling>();
        fd2Remove.addAll( getToken( p_game ).getFireDisablingList() );
        fdRemoved.addAll( getToken( p_game ).getFireDisablingList() );
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


  /**
   * @return the oldColor
   */
  private int getOldColor()
  {
    return m_oldColor;
  }

  /**
   * @param p_oldColor the oldColor to set
   */
  private void setOldColor(int p_oldColor)
  {
    m_oldColor = p_oldColor;
  }

}
