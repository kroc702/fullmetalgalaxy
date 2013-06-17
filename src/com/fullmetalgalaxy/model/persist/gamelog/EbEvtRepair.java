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



import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtRepair extends AnEventPlay
{
  static final long serialVersionUID = 1;


  /**
   * 
   */
  public EbEvtRepair()
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
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtRepair;
  }


  @Override
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    return getPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    EbToken freighter = p_game.getToken( getPosition(), TokenType.Freighter );
    EbToken turret = p_game.getToken( getPosition(), TokenType.Turret );
    if( (freighter == null) || (turret != null) )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "you can repair only destroyed turret" );
    }
    // check he don't repair center freighter
    if( freighter.getPosition().equals( getPosition() ) )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "you can repair only destroyed turret" );
    }
    if( freighter.getBulletCount() <= 0 )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "you can't repair any more turrets" );
    }
    EnuColor fireCoverColor = p_game.getOpponentFireCover( getMyTeam( p_game ).getColors(p_game.getPreview()),
        getPosition() );
    if( fireCoverColor.getValue() != EnuColor.None )
    {
      throw new RpcFmpException( errMsg().cantRepairTurretFireCover() );
    }
    // check that no other turret construction occur on this hex since last
    // control
    int iback = 0;
    AnEvent event = p_game.getLastLog( iback );
    int maxIBack = p_game.getLogs().size() - 10;
    while( event != null && iback < maxIBack )
    {
      if( event instanceof EbEvtControlFreighter
          && ((EbEvtControlFreighter)event).getTokenFreighter( p_game ).getId() == freighter
              .getId() )
      {
        break;
      }
      if( event instanceof EbEvtRepair && ((EbEvtRepair)event).getPosition().equals( getPosition() ) )
      {
        throw new RpcFmpException( errMsg().cantRepairTurretTwice() );
      }
      iback++;
      event = p_game.getLastLog( iback );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbToken freighter = p_game.getToken( getPosition(), TokenType.Freighter );
    EbToken turret = new EbToken();
    turret.setType( TokenType.Turret );
    turret.setColor( freighter.getColor() );
    p_game.addToken( turret );
    p_game.moveToken( turret, getPosition() );
    turret.getPosition().setSector( freighter.getPosition().getNeighbourSector( getPosition() ) );
    turret.incVersion();
    freighter.setBulletCount( freighter.getBulletCount() - 1 );

    execFireDisabling( p_game, getPosition() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbToken freighter = p_game.getToken( getPosition(), TokenType.Freighter );
    EbToken turret = p_game.getToken( getPosition(), TokenType.Turret );
    turret.setLocation( Location.Graveyard );
    turret.decVersion();
    freighter.setBulletCount( freighter.getBulletCount() + 1 );

    unexecFireDisabling( p_game );
  }



}
