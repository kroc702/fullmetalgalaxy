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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.BoardFireCover.FdChange;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.FireDisabling;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtFire extends AnEventPlay
{
  static final long serialVersionUID = 1;

  /**
   * token list which as been put in graveyard after this action
   */
  private ArrayList<Long> m_TokenIds = null;


  /**
   * 
   */
  public EbEvtFire()
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
    setCost( 2 );
    m_TokenIds = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtFire;
  }


  @Override
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    if( getTokenDestroyer1(p_game) != null )
    {
      return getTokenDestroyer1(p_game).getPosition();
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);

    // check that player control destroyers
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer1(p_game).getColor() )
        || getTokenDestroyer1(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getTokenDestroyer1( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), getMyRegistration( p_game ).getColor() ) ) );
    }
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer2(p_game).getColor() )
        || getTokenDestroyer2(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getTokenDestroyer2( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), getMyRegistration( p_game ).getColor() ) ) );
    }
    // check that player control destroyers
    if( (getTokenTarget( p_game ).getColor() != EnuColor.None)
        && (getMyRegistration(p_game).getEnuColor().isColored( getTokenTarget(p_game).getColor() )) )
    {
      throw new RpcFmpException( errMsg().cantDestroyYourUnits() );
    }

    // check the first destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer1(p_game) ) )
    {
      throw new RpcFmpException( errMsg().CantFireDisableTide(
          Messages.getTokenString( getAccountId(), getTokenDestroyer1( p_game ) ) ) );
    }
    // check the second destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer2(p_game) ) )
    {
      throw new RpcFmpException( errMsg().CantFireDisableTide(
          Messages.getTokenString( getAccountId(), getTokenDestroyer2( p_game ) ) ) );
    }

    if( ((!(p_game.getLastLog() instanceof EbEvtMove)) || (((EbEvtMove)p_game.getLastLog())
        .getToken( p_game ) != getTokenDestroyer1( p_game )))
        && ((!(p_game.getLastLog() instanceof EbEvtFire)) || (((EbEvtFire)p_game.getLastLog())
            .getTokenDestroyer1( p_game ) != getTokenDestroyer1( p_game ))) )
    {
      // check the first destroyer is not fire deactivated
      if( !p_game.isTokenFireActive( getTokenDestroyer1( p_game ) ) )
      {
        throw new RpcFmpException( errMsg().CantFireDisableFire(
            Messages.getTokenString( getAccountId(), getTokenDestroyer1( p_game ) ),
            Messages.getColorString( getAccountId(),
                p_game.getOpponentFireCover( getTokenDestroyer1( p_game ) ).getValue() ) ) );
      }
    }
    if( getTokenDestroyer2( p_game ).isFireDisabled() )
    {
      throw new RpcFmpException( errMsg().CantFireDisableFire(
          Messages.getTokenString( getAccountId(), getTokenDestroyer2( p_game ) ),
          Messages.getColorString( getAccountId(),
              p_game.getOpponentFireCover( getTokenDestroyer2( p_game ) ).getValue() ) ) );
    }

    if( !p_game.canTokenFireOn( getTokenDestroyer1(p_game), getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( errMsg().cantFireOn( Messages.getTokenString( getAccountId(), getTokenDestroyer1( p_game )),
          Messages.getTokenString( getAccountId(), getTokenTarget( p_game )) ));
    }
    if( !p_game.canTokenFireOn( getTokenDestroyer2(p_game), getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( errMsg().cantFireOn( Messages.getTokenString( getAccountId(), getTokenDestroyer2( p_game )),
          Messages.getTokenString( getAccountId(), getTokenTarget( p_game )) ));
    }

    if( getTokenDestroyer1( p_game ).getBulletCount() < 1
        && getTokenDestroyer1( p_game ).getType() != TokenType.Turret )
    {
      throw new RpcFmpException( errMsg().noMoreAmo(
          Messages.getTokenString( getAccountId(), getTokenDestroyer1( p_game ) ) ) );
    }
    if( getTokenDestroyer2( p_game ).getBulletCount() < 1
        && getTokenDestroyer2( p_game ).getType() != TokenType.Turret )
    {
      throw new RpcFmpException( errMsg().noMoreAmo(
          Messages.getTokenString( getAccountId(), getTokenDestroyer2( p_game ) ) ) );
    }

    // check that two destroyer are different
    if( getTokenDestroyer1( p_game ).getId() == getTokenDestroyer2( p_game ).getId() )
    {
      // no i18n as unusual
      throw new RpcFmpException( "the two destroyer must be different" );
    }

    // check that target isn't freighter
    if( getTokenTarget(p_game).getType() == TokenType.Freighter )
    {
      throw new RpcFmpException( errMsg().CantDestroyFreighter() );
    }

    // check that game isn't in parallel hidden phase
    if( p_game.isTimeStepParallelHidden( p_game.getCurrentTimeStep() ) )
    {
      throw new RpcFmpException( errMsg().CantAttackInParallelHiddenPhase() );
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
    setOldPosition( getTokenTarget(p_game).getPosition() );
    
    getTokenDestroyer1(p_game).setBulletCount( getTokenDestroyer1(p_game).getBulletCount() - 1 );
    getTokenDestroyer1(p_game).incVersion();
    getTokenDestroyer2(p_game).setBulletCount( getTokenDestroyer2(p_game).getBulletCount() - 1 );
    getTokenDestroyer2(p_game).incVersion();
    
    AnBoardPosition position = getTokenTarget(p_game).getPosition();
    if( getTokenTarget(p_game).getType() != TokenType.Pontoon )
    {
      // target isn't a pontoon: simply move it to graveyard
      if( !isFdComputed() )
      {
        addFdRemoved( getTokenTarget( p_game ).getFireDisablingList() );
        p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
      }
      p_game.moveToken( getTokenTarget(p_game), Location.Graveyard );
      getTokenTarget(p_game).incVersion();
      //if( !isFdComputed() )
      {
        // if it was a destroyer, it may disabling other token: check that
        execFireDisabling( p_game, position );
      }
    }
    EbToken pontoon = p_game.getToken( position, TokenType.Pontoon );
    if( pontoon != null )
    {
      // these is still a pontoon here, remove it from board
      m_TokenIds = new ArrayList<Long>();
      m_TokenIds.add( pontoon.getId() );
      p_game.moveToken( pontoon, Location.Graveyard );
      pontoon.incVersion();

      // check that other pontoon are linked to ground and remove all theses
      for( Sector sector : Sector.values() )
      {
        EbToken otherPontoon = p_game.getToken( getOldPosition().getNeighbour( sector ),
            TokenType.Pontoon );
        if( otherPontoon != null && !p_game.isPontoonLinkToGround( otherPontoon ) )
        {
          List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
          m_TokenIds.addAll( p_game.chainRemovePontoon( otherPontoon, fdRemoved ) );
          addFdRemoved( fdRemoved );
        }
      }

      // then, for all removed pontoon, check fire disabling change
      if( wasFdComputed )
      {
        // save CPU by avoiding recompute fire disabling flags
        //p_game.getBoardFireCover().addFireDisabling( getFdAdded() );
        //p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
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
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    getTokenDestroyer1(p_game).setBulletCount( getTokenDestroyer1(p_game).getBulletCount() + 1 );
    getTokenDestroyer1(p_game).decVersion();
    getTokenDestroyer2(p_game).setBulletCount( getTokenDestroyer2(p_game).getBulletCount() + 1 );
    getTokenDestroyer2(p_game).decVersion();

    if( getTokenIds() != null )
    {
      // we destroy a pontoon
      // put back other pontoon/token if there is some
      for( Long id : getTokenIds() )
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) && (token.getLocation() == Location.Graveyard) )
        {
          p_game.moveToken( token, token.getPosition() );
          token.decVersion();
        }
      }
    }
    if( getTokenTarget(p_game).getType() != TokenType.Pontoon )
    {
      // target wasn't a pontoon: put it back from graveyard
      p_game.moveToken( getTokenTarget(p_game), getOldPosition() );
      getTokenTarget(p_game).decVersion();   
    }

    unexecFireDisabling( p_game );
  }

  private ArrayList<Long> getTokenIds()
  {
    return m_TokenIds;
  }


  
  /**
   * @param p_game game to apply event
    * @return the tokenTarget
   */
  public EbToken getTokenTarget(Game p_game)
  {
    return getToken( p_game );
  }


  /**
   * @param p_game game to apply event
    * @return the tokenTarget
   */
  public void setTokenTarget(EbToken p_token)
  {
    setToken( p_token );
  }

}
