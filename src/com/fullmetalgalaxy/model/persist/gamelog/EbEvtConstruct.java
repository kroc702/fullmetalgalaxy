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
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtConstruct extends AnEventPlay
{
  static final long serialVersionUID = 1;


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
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtConstruct;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
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
      throw new RpcFmpException( "le pion de matiere premiere doit etre un minerai" );
    }
    // check that tokencarrier is a weather hen
    if( getTokenCarrier(p_game).getType() != TokenType.WeatherHen )
    {
      throw new RpcFmpException( "seul les pondeuses meteo peuvent construire des pions" );
    }
    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getTokenCarrier(p_game).getColor() )
        || getTokenCarrier(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenCarrier(p_game).getColor(),
          myRegistration.getColor() );
    }
    // check that player have one more action point to unload that token
    if( myRegistration.getPtAction() <= 0 )
    {
      // no i18n
      throw new RpcFmpException( "player must have one more action point to unload that token" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    getToken(p_game).setType( getConstructType() );
    if( getToken(p_game).canBeColored() )
    {
      getToken(p_game).setColor( getTokenCarrier(p_game).getColor() );
    }
    getToken(p_game).setBulletCount( getToken(p_game).getMaxBulletCount() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    getToken(p_game).setType( TokenType.Ore );
    getToken(p_game).setColor( EnuColor.None );
    getToken(p_game).setBulletCount( getToken(p_game).getMaxBulletCount() );
  }


}