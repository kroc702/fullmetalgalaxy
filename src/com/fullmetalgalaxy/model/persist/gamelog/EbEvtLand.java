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

import java.util.ArrayList;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.HexCoordinateSystem;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 * Land a Freighter from Orbit to a board position.
 */
public class EbEvtLand extends AnEventPlay
{
  static final long serialVersionUID = 1;

  /**
   * token list which as been put in graveyard after this action
   */
  private ArrayList<Long> m_TokenIds = null;
  
  /**
   * 
   */
  public EbEvtLand()
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
    m_TokenIds = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtLand;
  }

  @Override
  public boolean canBeParallelHidden()
  {
    return false;
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

    if( getPosition().getX() == -1 )
    {
      // landing position isn't choose yet...
      throw new RpcFmpException("");
    }
    
    if( getToken( p_game ).getType() != TokenType.Freighter
        || getToken( p_game ).getLocation() != Location.Orbit )
    {
      // not probable error
      throw new RpcFmpException( "Only Freighter in orbit can be landed." );
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
    // check freighter isn't landing on sea neither montain
    // get the 4 landing hexagon
    AnBoardPosition landingPosition[] = new AnBoardPosition[6];
    landingPosition[0] = getPosition();
    switch( landingPosition[0].getSector() )
    {
    case North:
    case SouthEast:
    case SouthWest:
      landingPosition[1] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.North );
      landingPosition[2] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.SouthEast );
      landingPosition[3] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.SouthWest );
      break;
    case NorthEast:
    case South:
    case NorthWest:
      landingPosition[1] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.NorthEast );
      landingPosition[2] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.South );
      landingPosition[3] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.NorthWest );
    default:
      // impossible error
      break;
    }
    // check the 4 hexagon
    for( int i = 0; i < 4; i++ )
    {
      LandType land = p_game.getLand( landingPosition[i] );
      if( (land == LandType.None) || (land == LandType.Sea) || (land == LandType.Reef)
          || (land == LandType.Montain) )
      {
        throw new RpcFmpException( errMsg().CantLandOn(
            Messages.getLandString( getAccountId(), land ) ) );
      }
    }
    // check that freighter isn't landing close to another freighter
    for( EbToken currentToken : p_game.getSetToken() )
    {
      if( (currentToken.getType() == TokenType.Freighter)
          && (currentToken.getLocation() == Location.Board)
          && (currentToken.getId() != getToken(p_game).getId())
          && (p_game.getCoordinateSystem().getDiscreteDistance( landingPosition[0], currentToken.getPosition() ) <= FmpConstant.minSpaceBetweenFreighter) )
      {
        throw new RpcFmpException( errMsg().CantLandCloser( FmpConstant.minSpaceBetweenFreighter ) );
      }
    }
    // check that freighter isn't landing too close of map boarder
    if( !p_game.getMapShape().isEWLinked() &&
        (getPosition().getX() < 2 || getPosition().getX() > (p_game.getLandWidth() - 3))  )
    {
      throw new RpcFmpException( errMsg().CantLandTooCloseBorder() );
    }
    if( !p_game.getMapShape().isNSLinked() &&
        (getPosition().getY() < 2 || getPosition().getY() > (p_game.getLandHeight() - 3)) )
    {
      throw new RpcFmpException( errMsg().CantLandTooCloseBorder() );
    }
    // check empty hex near landing position
    HexCoordinateSystem coordinateSystem = p_game.getCoordinateSystem();
    for( int i = 0; i < 6; i++ )
    {
      landingPosition[i] = coordinateSystem.getNeighbor( getPosition(), Sector.getFromOrdinal( i ) );
      landingPosition[i] = coordinateSystem.getNeighbor( landingPosition[i],
          Sector.getFromOrdinal( i ) );
    }
    for( int i = 0; i < 6; i++ )
    {
      if( p_game.getLand( landingPosition[i] ) == LandType.None )
      {
        throw new RpcFmpException( errMsg().CantLandTooCloseBorder() );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.moveToken( getToken(p_game), getPosition() );
    getToken(p_game).incVersion();
    // destroy any colorless token in the deployment area
    m_TokenIds = new ArrayList<Long>();
    for( EbToken currentToken : p_game.getSetToken() )
    {
      if( (currentToken.getColor() == EnuColor.None)
          && (currentToken.getLocation() == Location.Board)
          && (p_game.getCoordinateSystem().getDiscreteDistance( getPosition(), currentToken.getPosition() ) <= FmpConstant.deployementRadius) )
      {
        // destroy this colorless token
        m_TokenIds.add( currentToken.getId() );
        p_game.moveToken( currentToken, Location.Graveyard );
        currentToken.incVersion();
      }
    }


    // add the 3 turrets
    // get the 4 landing hexagon
    AnBoardPosition landingPosition[] = new AnBoardPosition[4];
    landingPosition[0] = getPosition();
    switch( landingPosition[0].getSector() )
    {
    default:
    case North:
    case SouthEast:
    case SouthWest:
      landingPosition[1] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.North );
      landingPosition[2] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.SouthEast );
      landingPosition[3] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.SouthWest );
      break;
    case NorthEast:
    case South:
    case NorthWest:
      landingPosition[1] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.NorthEast );
      landingPosition[2] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.South );
      landingPosition[3] = p_game.getCoordinateSystem().getNeighbor( landingPosition[0], Sector.NorthWest );
    }

    // unload three turrets
    int index = 0;
    if( getToken( p_game ).containToken() )
    {
      for( EbToken token : getToken( p_game ).getCopyContains() )
      {
        if( (token.getType() == TokenType.Turret) && (index < 3) )
        {
          index++;
          p_game.moveToken( token, landingPosition[index] );
          token.incVersion();
        }
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

    // reload three turrets
    for( AnBoardPosition position : getToken(p_game).getExtraPositions(p_game.getCoordinateSystem()) )
    {
      EbToken token = p_game.getToken( position, TokenType.Turret );

      if( token != null )
      {
        p_game.moveToken( token, getToken(p_game) );
        token.decVersion();
      }
    }

    p_game.moveToken( getToken(p_game), Location.Orbit );
    getToken(p_game).decVersion();
    // this update is here only to refresh token display
    p_game.updateLastTokenUpdate( null );

    // put back ore on board.
    if( m_TokenIds != null )
    {
      for( Long id : m_TokenIds )
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
