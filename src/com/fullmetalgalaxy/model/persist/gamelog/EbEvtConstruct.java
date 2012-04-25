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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtConstruct extends AnEventPlay
{
  static final long serialVersionUID = 1;

  private TokenType m_constructType = TokenType.None;

  /**
   * 
   */
  public EbEvtConstruct()
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
    m_constructType = TokenType.None;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtConstruct;
  }

  @Override
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
    // check token is on board
    if( (getTokenCarrier(p_game).getLocation() != Location.Board)
        && (getTokenCarrier(p_game).getLocation() != Location.Token) )
    {
      // not probable error (no i18n)
      throw new RpcFmpException( "token " + getTokenCarrier(p_game)
          + " must be on board to construct a token" );
    }
    // check that token is an ore
    if( getToken(p_game).getType() != TokenType.Ore )
    {
      // no i18n
      throw new RpcFmpException( "you need an ore to construct" );
    }
    // check that tokencarrier is a weather hen
    if( getTokenCarrier(p_game).getType() != TokenType.WeatherHen )
    {
      // no i18n
      throw new RpcFmpException( "only weather hen can construct units" );
    }
    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getTokenCarrier(p_game).getColor() )
        || getTokenCarrier(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getTokenCarrier( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }
    // check that player have one more action point to unload that token
    if( myRegistration.getPtAction() <= 0 )
    {
      // no i18n
      throw new RpcFmpException( "player must have one more action point to unload that token" );
    }
    // check token construct reserve
    if( !p_game.getEbConfigGameVariant().canConstruct( getConstructType() ) )
    {
      // no i18n
      throw new RpcFmpException( getConstructType().toString() + " construct reserve is empty" );
    }
    // Check bullet count: wheather hen can't construct more than 2 unit per
    // turn
    if( getTokenCarrier( p_game ).getBulletCount() < 1 )
    {
      throw new RpcFmpException( errMsg().tooManyConstruction() );
    }
    // Check wheather hen do not construct two similar unit during same turn
    int reverseIndex = p_game.getLogs().size() - 1;
    while( getTokenCarrier( p_game ).getBulletCount() < 2 && reverseIndex > 0 )
    {
      AnEvent event = p_game.getLogs().get( reverseIndex );
      if( event.getType() == GameLogType.EvtTimeStep
          || event.getType() == GameLogType.EvtPlayerTurn )
      {
        break;
      }
      if( event.getType() == GameLogType.EvtConstruct
          && getConstructType() != TokenType.Tank
          && ((EbEvtConstruct)event).getConstructType() == getConstructType()
          && ((EbEvtConstruct)event).getPackedTokenCarrier().getId() == getPackedTokenCarrier()
              .getId() )
      {
        throw new RpcFmpException( errMsg().tooManyConstruction() );
      }
      reverseIndex--;
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    getTokenCarrier( p_game ).setBulletCount( getTokenCarrier( p_game ).getBulletCount() - 1 );
    getToken(p_game).setType( getConstructType() );
    if( getToken(p_game).canBeColored() )
    {
      getToken(p_game).setColor( getTokenCarrier(p_game).getColor() );
    }
    getToken(p_game).setBulletCount( getToken(p_game).getMaxBulletCount() );
    p_game.getEbConfigGameVariant().decConstructQty( getConstructType() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    getTokenCarrier( p_game ).setBulletCount( getTokenCarrier( p_game ).getBulletCount() + 1 );
    getToken(p_game).setType( TokenType.Ore );
    getToken(p_game).setColor( EnuColor.None );
    getToken(p_game).setBulletCount( getToken(p_game).getMaxBulletCount() );
    p_game.getEbConfigGameVariant().incConstructQty( getConstructType() );
  }

  /**
   * @return the p_constructType
   */
  public TokenType getConstructType()
  {
    return m_constructType;
  }

  /**
   * @param p_type the p_constructType to set
   */
  public void setConstructType(TokenType p_type)
  {
    m_constructType = p_type;
  }

}
