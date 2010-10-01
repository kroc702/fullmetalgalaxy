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
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *  move a token from a carrier token to another carrier token
 */
public class EbEvtTransfer extends AnEventPlay
{
  static final long serialVersionUID = 1;



  /**
   * 
   */
  public EbEvtTransfer()
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
    return GameLogType.EvtTransfer;
  }


  @Override
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    if( getTokenCarrier(p_game) != null )
    {
      return getTokenCarrier(p_game).getPosition();
    }
    else
    {
      return super.getSelectedPosition(p_game);
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    // check new token carrier is on board
    if( (getToken(p_game).getLocation() != Location.Token)
        || (getNewTokenCarrier(p_game).getLocation() != Location.Board) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getToken(p_game) + " can't be moved from "
          + getTokenCarrier(p_game) + " to " + getNewTokenCarrier(p_game) );
    }
    // check that player control both token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getToken(p_game).getColor(),
          myRegistration.getColor() );
    }
    if( !myRegistration.getEnuColor().isColored( getTokenCarrier(p_game).getColor() )
        || getTokenCarrier(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenCarrier(p_game)
          .getColor(), myRegistration.getColor() );
    }
    if( !myRegistration.getEnuColor().isColored( getNewTokenCarrier(p_game).getColor() )
        || getNewTokenCarrier(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getNewTokenCarrier(p_game)
          .getColor(), myRegistration.getColor() );
    }
    // check that tokens are neighbor
    if( getTokenCarrier(p_game).getLocation() == Location.Board )
    {
      if( !getTokenCarrier(p_game).isNeighbor( getNewTokenCarrier(p_game) )
          && getTokenCarrier(p_game).getCarrierToken() != getNewTokenCarrier(p_game) )
      {
        throw new RpcFmpException( "les deux pions " + getTokenCarrier(p_game) + " et "
            + getNewTokenCarrier(p_game) + " ne sont pas voisins: le transfert est impossible." );
      }
    }
    else
    {
      if( getTokenCarrier(p_game).getCarrierToken() != getNewTokenCarrier(p_game) )
      {
        throw new RpcFmpException( "les deux pions " + getTokenCarrier(p_game) + " et "
            + getNewTokenCarrier(p_game) + " ne sont pas voisins: le transfert est impossible." );
      }
    }
    // check that new token carrier can load this token
    if( !p_game.canTokenLoad( getNewTokenCarrier(p_game), getToken(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantLoad,
          getNewTokenCarrier(p_game).getType().ordinal(), getToken(p_game).getType().ordinal() );
    }
    // check that heap don't goes on mountain
    if(getToken(p_game).getType() == TokenType.Heap && p_game.getLand( getNewTokenCarrier( p_game ).getPosition() ) == LandType.Montain )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveOn, getToken(p_game).getType().ordinal(),
          p_game.getLand( getNewTokenCarrier( p_game ).getPosition() ).ordinal() );
    }
    // check tide/fire disable
    if( !p_game.isTokenTideActive( getNewTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantUnloadDisableTide, getTokenCarrier(p_game).getType()
          .ordinal() );
    }
    if( !p_game.isTokenFireActive( myRegistration.getEnuColor(), getNewTokenCarrier(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantUnloadDisableFire, getTokenCarrier(p_game).getType()
          .ordinal(), p_game.getOpponentFireCover( getTokenCarrier(p_game) ).getValue() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.moveToken( getToken(p_game), getNewTokenCarrier(p_game) );
    getToken(p_game).incVersion();
    getTokenCarrier(p_game).incVersion();
    getNewTokenCarrier(p_game).incVersion();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    p_game.moveToken( getToken(p_game), getTokenCarrier(p_game) );
    getToken(p_game).decVersion();
    getTokenCarrier(p_game).decVersion();
    getNewTokenCarrier(p_game).decVersion();
  }





}