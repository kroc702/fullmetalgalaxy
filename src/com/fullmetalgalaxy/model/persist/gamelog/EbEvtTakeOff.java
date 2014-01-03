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
 *
 */
public class EbEvtTakeOff extends AnEventPlay
{
  static final long serialVersionUID = 1;

  private boolean m_backInOrbit = false;

  /**
   * token list which are colorless after this action
   */
  private ArrayList<Long> m_TokenIds = null;
  
  
  /**
   * 
   */
  public EbEvtTakeOff()
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
    m_backInOrbit = false;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtTakeOff;
  }


  @Override
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    // check that token is a freighter
    if( getToken(p_game).getType() != TokenType.Freighter )
    {
      // no i18n
      throw new RpcFmpException( "only freighter can take off" );
    }
    // check that take off is allowed this turn
    if( !p_game.getAllowedTakeOffTurns().contains( p_game.getCurrentTimeStep() ) )
    {
      // no i18n
      throw new RpcFmpException( "Take off isn't allowed this turn" );
    }
    // check token is on board
    if( (getToken(p_game).getLocation() != Location.Board)
        && (getToken(p_game).getLocation() != Location.Token) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getToken(p_game) + " can't be moved from location "
          + getToken(p_game).getLocation() );
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
    
    // reload three turrets
    for( AnBoardPosition position : getToken(p_game).getExtraPositions(p_game.getCoordinateSystem()) )
    {
      EbToken token = p_game.getToken( position, TokenType.Turret );

      if( token != null )
      {
        p_game.moveToken( token, getToken(p_game) );
        token.incVersion();
      }
    }

    if( isBackInOrbit() )
    {
      p_game.moveToken( getToken(p_game), Location.Orbit );
    }
    else
    {
      p_game.moveToken( getToken(p_game), Location.EndGame );
    }
    getToken(p_game).incVersion();

    // all tokens on board become colorless
    m_TokenIds = new ArrayList<Long>();
    for( EbToken token : p_game.getSetToken() )
    {
      if( (token.getLocation() == Location.Board) && (token.getColor() == getToken(p_game).getColor()) )
      {
        m_TokenIds.add( token.getId() );
        p_game.changeTokenColor( token, EnuColor.None );
        token.incVersion();
      }
    }

    execFireDisabling( p_game );

    // this update is here only to refresh token display
    p_game.updateLastTokenUpdate( null );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    p_game.moveToken( getToken(p_game), Location.Board );
    getToken(p_game).decVersion();

    // unload three turrets
    // TODO turrets are not put back at the same place.
    int index = 0;
    if( getToken( p_game ).containToken() )
    {
      for( EbToken token : getToken( p_game ).getCopyContains() )
      {
        if( (token.getType() == TokenType.Turret) && (index < 3) )
        {
          p_game.moveToken( token, getToken( p_game ).getExtraPositions(p_game.getCoordinateSystem()).get( index ) );
          token.decVersion();
          index++;
        }
      }
    }
    
    // put back color on token
    if( m_TokenIds != null )
    {
      int freitherColor = getToken( p_game ).getColor();
      for( Long id : m_TokenIds )
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) )
        {
          p_game.changeTokenColor( token, freitherColor );
          token.decVersion();
        }
      }
    }

    unexecFireDisabling( p_game );
  }


  /**
   * @return the backInOrbit
   */
  public boolean isBackInOrbit()
  {
    return m_backInOrbit;
  }

  /**
   * @param p_backInOrbit the backInOrbit to set
   */
  public void setBackInOrbit(boolean p_backInOrbit)
  {
    m_backInOrbit = p_backInOrbit;
  }


}
