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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    return getPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);

    if( getToken(p_game).getType() != TokenType.Freighter )
    {
      // not probable error
      throw new RpcFmpException( "Only Freighter can be landed." );
    }
    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getToken(p_game).getType()
          .ordinal(), myRegistration.getColor() );
    }
    // check freighter isn't landing on sea neither montain
    // get the 4 landing hexagon
    AnBoardPosition landingPosition[] = new AnBoardPosition[4];
    landingPosition[0] = getPosition();
    switch( landingPosition[0].getSector() )
    {
    case North:
    case SouthEast:
    case SouthWest:
      landingPosition[1] = landingPosition[0].getNeighbour( Sector.North );
      landingPosition[2] = landingPosition[0].getNeighbour( Sector.SouthEast );
      landingPosition[3] = landingPosition[0].getNeighbour( Sector.SouthWest );
      break;
    case NorthEast:
    case South:
    case NorthWest:
      landingPosition[1] = landingPosition[0].getNeighbour( Sector.NorthEast );
      landingPosition[2] = landingPosition[0].getNeighbour( Sector.South );
      landingPosition[3] = landingPosition[0].getNeighbour( Sector.NorthWest );
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
        throw new RpcFmpException( RpcFmpException.CantLandOn, land.ordinal() );
      }
    }
    // check that freighter isn't landing close to another freighter
    for( EbToken currentToken : p_game.getSetToken() )
    {
      if( (currentToken.getType() == TokenType.Freighter)
          && (currentToken.getLocation() == Location.Board)
          && (currentToken.getId() != getToken(p_game).getId())
          && (landingPosition[0].getRealDistance( currentToken.getPosition() ) < p_game
              .getEbConfigGameVariant().getMinSpaceBetweenFreighter() + 0.1) )
      {
        throw new RpcFmpException( RpcFmpException.CantLandCloser, p_game
            .getEbConfigGameVariant().getMinSpaceBetweenFreighter() );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.moveToken( getToken(p_game), getPosition() );
    getToken(p_game).incVersion();
    // destroy any colorless token in the deployement area
    m_TokenIds = new ArrayList<Long>();
    for( EbToken currentToken : p_game.getSetToken() )
    {
      if( (currentToken.getColor() == EnuColor.None)
          && (currentToken.getLocation() == Location.Board)
          && (getPosition().getHexDistance( currentToken.getPosition() ) <= p_game
              .getEbConfigGameVariant().getDeploymentRadius()) )
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
      landingPosition[1] = landingPosition[0].getNeighbour( Sector.North );
      landingPosition[2] = landingPosition[0].getNeighbour( Sector.SouthEast );
      landingPosition[3] = landingPosition[0].getNeighbour( Sector.SouthWest );
      break;
    case NorthEast:
    case South:
    case NorthWest:
      landingPosition[1] = landingPosition[0].getNeighbour( Sector.NorthEast );
      landingPosition[2] = landingPosition[0].getNeighbour( Sector.South );
      landingPosition[3] = landingPosition[0].getNeighbour( Sector.NorthWest );
    }

    // unload three turrets
    int index = 0;
    Set<EbToken> contains = new HashSet<EbToken>();
    contains.addAll( getToken(p_game).getSetContain() );
    for( EbToken token : contains )
    {
      if( (token.getType() == TokenType.Turret) && (index < 3) )
      {
        index++;
        p_game.moveToken( token, landingPosition[index] );
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

    // reload three turrets
    for( AnBoardPosition position : getToken(p_game).getExtraPositions() )
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
