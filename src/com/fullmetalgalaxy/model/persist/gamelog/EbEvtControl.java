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
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtControl extends AnEventPlay
{
  static final long serialVersionUID = 1;

  private int m_oldColor = EnuColor.None;


  /**
   * 
   */
  public EbEvtControl()
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
    m_oldColor = EnuColor.None;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtControl;
  }


  @Override
  public AnBoardPosition getSelectedPosition(EbGame p_game)
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
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);

    // check that player control destroyers
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer1(p_game).getColor() )
        || getTokenDestroyer1(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenDestroyer1(p_game)
          .getColor(), getMyRegistration(p_game).getColor() );
    }
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer2(p_game).getColor() )
        || getTokenDestroyer2(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenDestroyer2(p_game)
          .getColor(), getMyRegistration(p_game).getColor() );
    }

    // check that two token are destroyer
    if( !getTokenDestroyer1( p_game ).isDestroyer() || !getTokenDestroyer2( p_game ).isDestroyer() )
    {
      throw new RpcFmpException(
          "Il vous faut deux destructeurs pour controler un vehicule adverse" );
    }

    // check the first destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer1(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantFireDisableTide, getTokenDestroyer1(p_game)
          .getType().ordinal() );
    }
    // check the second destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer2(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantFireDisableTide, getTokenDestroyer2(p_game)
          .getType().ordinal() );
    }

    // check first, second destroyer and target are not under opponents fires
    EnuColor fireCover = p_game.getBoardFireCover()
        .getFireCover( getTokenTarget(p_game).getPosition() );
    fireCover.removeColor( getMyRegistration(p_game).getOriginalColor() );
    if( getTokenDestroyer1(p_game).isFireDisabled() || getTokenDestroyer2(p_game).isFireDisabled()
        || fireCover.getValue() != EnuColor.None )
    {
      throw new RpcFmpException(
          "Pour qu'un control soit possible, il faut qu'aucun des trois pions ne soit sous zone de feu adverse" );
    }


    if( !getTokenDestroyer1(p_game).isNeighbor( getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer1(p_game) + " n'est pas au contact de "
          + getTokenTarget(p_game) );
    }
    if( !getTokenDestroyer2(p_game).isNeighbor( getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer2(p_game) + " n'est pas au contact de "
          + getTokenTarget(p_game) );
    }

    // check that target isn't freighter
    if( getTokenTarget(p_game).getType() == TokenType.Freighter )
    {
      throw new RpcFmpException(
          "les astronefs ne peuvent etres control� de cette facon. Vous devez d�truire toute les tourelles puis entrer dedans" );
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
    setOldColor( getTokenTarget( p_game).getColor() );
    
    p_game.changeTokenColor( getTokenTarget(p_game), getTokenDestroyer1(p_game).getColor() );
    getTokenTarget(p_game).incVersion();
    for( EbToken token : getTokenTarget(p_game).getSetContain() )
    {
      if( token.canBeColored() )
      {
        token.incVersion();
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

    getTokenTarget(p_game).decVersion();
    p_game.changeTokenColor( getTokenTarget(p_game), getOldColor() );
    for( EbToken token : getTokenTarget(p_game).getSetContain() )
    {
      if( token.canBeColored() )
      {
        token.decVersion();
      }
    }
  }

  /**
   * @param p_game game to apply event
    * @return the tokenTarget
   */
  public EbToken getTokenTarget(EbGame p_game)
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
