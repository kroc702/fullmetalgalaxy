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

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtLoad extends AnEventPlay
{
  static final long serialVersionUID = 1;


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
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtLoad;
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
    assert getOldColor() == getToken(p_game).getColor();

    // check both token is on board
    if( (getToken(p_game).getLocation() != Location.Board)
        || (getTokenCarrier(p_game).getLocation() != Location.Board) )
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
    // check that player control both token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getToken(p_game).getColor(),
          myRegistration.getColor() );
    }
    if( getToken(p_game).canBeColored() && getToken(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getToken(p_game).getColor(),
          myRegistration.getColor() );
    }
    if( !myRegistration.getEnuColor().isColored( getTokenCarrier(p_game).getColor() )
        || getTokenCarrier(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenCarrier(p_game).getColor(),
          myRegistration.getColor() );
    }

    // check that tokens are neighbor
    if( !getToken(p_game).isNeighbor( getTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( "les deux pions ne sont pas cote a cote" );
    }

    // check this token is allowed to move from this hexagon
    if( !p_game.isTokenTideActive( getToken(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveOn, getToken(p_game).getType().ordinal(),
 p_game
          .getLand( getOldPosition() ).ordinal() );
    }
    // check that new token carrier can load this token
    if( !p_game.canTokenLoad( getTokenCarrier(p_game), getToken(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantLoad, getTokenCarrier(p_game).getType().ordinal(),
          getToken(p_game).getType().ordinal() );
    }
    // check that heap don't goes on mountain
    if(getToken(p_game).getType() == TokenType.Heap && p_game.getLand( getTokenCarrier( p_game ).getPosition() ) == LandType.Montain )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveOn, getToken(p_game).getType().ordinal(),
          p_game.getLand( getTokenCarrier( p_game ).getPosition() ).ordinal() );
    }
    // check tide/fire disable
    if( !p_game.isTokenTideActive( getTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantUnloadDisableTide, getTokenCarrier(p_game).getType()
          .ordinal() );
    }
    if( !p_game.isTokenFireActive( myRegistration.getEnuColor(), getTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantUnloadDisableFire, getTokenCarrier(p_game).getType()
          .ordinal(), p_game.getOpponentFireCover( getTokenCarrier(p_game) ).getValue() );
    }
    if( !p_game.isTokenFireActive( myRegistration.getEnuColor(), getToken(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantUnloadDisableFire, getToken(p_game).getType()
          .ordinal(), p_game.getOpponentFireCover( getToken(p_game) ).getValue() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.moveToken( getToken(p_game), getTokenCarrier(p_game) );
    getToken(p_game).incVersion();
    getTokenCarrier(p_game).incVersion();
    // if token is a pontoon, check that other pontoon are linked to ground
    setMiscTokenIds( null );
    if( getToken(p_game).getType() == TokenType.Pontoon )
    {
      for( Sector sector : Sector.values() )
      {
        EbToken otherPontoon = p_game.getToken( getOldPosition().getNeighbour( sector ),
            TokenType.Pontoon );
        if( otherPontoon != null )
        {
          if( !p_game.isPontoonLinkToGround( otherPontoon ) )
          {
            chainRemovePontoon( p_game, otherPontoon );
          }
        }
      }
    }
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
    if( getToken(p_game).canBeColored() )
    {
      getToken(p_game).setColor( getOldColor() );
    }
    getTokenCarrier(p_game).decVersion();
    // put back pontoon if there is some
    if( getMiscTokenIds() != null )
    {
      for( Long id : getMiscTokenIds() )
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) && (token.getLocation() == Location.Graveyard) )
        {
          p_game.moveToken( token, token.getPosition() );
          token.decVersion();
        }
      }
    }
  }




}
