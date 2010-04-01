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
package com.fullmetalgalaxy.client.ressources;



import java.util.Date;


import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.core.client.GWT;

/**
 * this class is used to get a localized string of all class located in rpc package as 
 * GWT localisation can't be ran on server side
 * 
 * @author Vincent Legendre
 *
 */
public class Messages
{
  public static MessagesRpcException s_rpcEx = (MessagesRpcException)GWT
      .create( MessagesRpcException.class );

  public static MessagesRpc s_rpc = (MessagesRpc)GWT.create( MessagesRpc.class );

  public static String getTokenString(EbToken p_token)
  {
    if( p_token.getColor() > EnuColor.None )
    {
      return s_rpc.tokenDescription( getColorString( p_token.getColor() ), getTokenString( p_token
          .getType() ) );
    }
    else
    {
      return getTokenString( p_token.getType() );
    }
  }

  public static String getTokenString(TokenType p_token, int p_color)
  {
    return s_rpc.tokenDescription( getColorString( p_color ), getTokenString( p_token ) );
  }

  private static String getSingleColorString(int p_colorValue)
  {
    switch( p_colorValue )
    {
    case EnuColor.Blue:
      return s_rpc.blue();
    case EnuColor.Cyan:
      return s_rpc.cyan();
    case EnuColor.Grey:
      return s_rpc.grey();
    case EnuColor.Yellow:
      return s_rpc.yellow();
    case EnuColor.Olive:
      return s_rpc.olive();
    case EnuColor.Red:
      return s_rpc.red();
    case EnuColor.Orange:
      return s_rpc.orange();
    case EnuColor.Green:
      return s_rpc.green();
    case EnuColor.Purple:
      return s_rpc.purple();
    case EnuColor.None:
      return s_rpc.colorless();
    case EnuColor.Unknown:
    default:
      return new EnuColor( p_colorValue ).toString();
    }
  }

  public static String getColorString(int p_colorValue)
  {
    if( p_colorValue == EnuColor.Unknown )
    {
      return getSingleColorString( EnuColor.Unknown );
    }

    String str = "";
    boolean isEmpty = true;
    EnuColor color = new EnuColor( p_colorValue );

    if( color.isColored( EnuColor.Blue ) )
    {
      str += getSingleColorString( EnuColor.Blue );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Cyan ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Cyan );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Grey ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Grey );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Yellow ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Yellow );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Olive ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Olive );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Orange ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Orange );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Red ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Red );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Green ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Green );
      isEmpty = false;
    }
    if( color.isColored( EnuColor.Purple ) )
    {
      if( !isEmpty )
        str += " & ";
      str += getSingleColorString( EnuColor.Purple );
      isEmpty = false;
    }

    if( isEmpty )
    {
      str = getSingleColorString( EnuColor.None );
    }
    return str;
  }

  public static String getTideString(Tide p_tideValue)
  {
    switch( p_tideValue )
    {
    case Low:
      return s_rpc.low();
    case Medium:
      return s_rpc.medium();
    case Hight:
      return s_rpc.hight();
    default:
      return p_tideValue.toString();
    }
  }

  public static String getTokenString(TokenType p_tokenValue)
  {
    switch( p_tokenValue )
    {
    case Barge:
      return s_rpc.barge();
    case Freighter:
      return s_rpc.freighter();
    case Tank:
      return s_rpc.tank();
    case Crab:
      return s_rpc.crab();
    case Heap:
      return s_rpc.heap();
    case WeatherHen:
      return s_rpc.weatherHen();
    case Turret:
      return s_rpc.turret();
    case Speedboat:
      return s_rpc.speedboat();
    case Ore:
      return s_rpc.ore();
    case Pontoon:
      return s_rpc.pontoon();
    case None:
    default:
      return p_tokenValue.toString();
    }
  }

  public static String getSectorString(Sector p_sectorValue)
  {
    switch( p_sectorValue )
    {
    case North:
      return s_rpc.north();
    case NorthEast:
      return s_rpc.north_east();
    case SouthEast:
      return s_rpc.south_east();
    case South:
      return s_rpc.south();
    case SouthWest:
      return s_rpc.south_west();
    case NorthWest:
      return s_rpc.north_west();
    default:
      return p_sectorValue.toString();
    }
  }

  public static String getPlanetString(PlanetType p_planet)
  {
    switch( p_planet )
    {
    default:
    case Desert:
      return s_rpc.desert();
    case Grass:
      return s_rpc.grass();
    case Ice:
      return s_rpc.ice();
    case Lava:
      return s_rpc.lava();
    }
  }

  public static String getLandString(LandType p_landValue)
  {
    switch( p_landValue )
    {
    default:
    case None:
      return s_rpc.none();
    case Sea:
      return s_rpc.sea();
    case Reef:
      return s_rpc.reef();
    case Marsh:
      return s_rpc.marsh();
    case Plain:
      return s_rpc.plain();
    case Montain:
      return s_rpc.montain();
    }
  }

  public static String getString(RpcFmpException p_exception)
  {
    switch( p_exception.m_errorCode )
    {
    case RpcFmpException.LogonWrongPassword:
      return s_rpcEx.LogonWrongPassword();
    case RpcFmpException.UnknownGameId:
      return s_rpcEx.UnknownGameId( p_exception.getLong( 0 ) );
    case RpcFmpException.MustBeLogged:
      return s_rpcEx.MustBeLogged();
    case RpcFmpException.NoGameId:
      return s_rpcEx.NoGameId();
    case RpcFmpException.UnknownAccount:
      return s_rpcEx.UnknownAccount();
    case RpcFmpException.MaximumPlayerReached:
      return s_rpcEx.MaximumPlayerReached();
    case RpcFmpException.YouDidntJoinThisGame:
      return s_rpcEx.YouDidntJoinThisGame();
    case RpcFmpException.GameNotStarted:
      return s_rpcEx.GameNotStarted();
    case RpcFmpException.GameFinished:
      return s_rpcEx
          .GameFinished( ClientUtil.formatDateTime( new Date( p_exception.getLong( 0 ) ) ) );
    case RpcFmpException.CantMoveDontControl:
      return s_rpcEx.CantMoveDontControl( getColorString( p_exception.getInt( 0 ) ),
          getColorString( p_exception.getInt( 1 ) ) );
    case RpcFmpException.CantMoveOn:
      return s_rpcEx.CantMoveOn(
          getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 0 ) ) ),
          getLandString( LandType.getFromOrdinal( p_exception.getInt( 1 ) ) ) );
    case RpcFmpException.CantUnloadDontControl:
      return s_rpcEx.CantUnloadDontControl( getColorString( p_exception.getInt( 0 ) ),
          getColorString( p_exception.getInt( 1 ) ) );
    case RpcFmpException.CantUnloadDisableTide:
      return s_rpcEx.CantUnloadDisableTide( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ) );
    case RpcFmpException.CantUnloadDisableFire:
      return s_rpcEx.CantUnloadDisableFire( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ), getColorString( p_exception.getInt( 1 ) ) );
    case RpcFmpException.MustTwoPositionToUnloadBarge:
      return s_rpcEx.MustTwoPositionToUnloadBarge();
    case RpcFmpException.NotEnouthActionPt:
      return s_rpcEx.NotEnouthActionPt();
    case RpcFmpException.CantMoveDisableFire:
      return s_rpcEx.CantMoveDisableFire( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ), getColorString( p_exception.getInt( 1 ) ) );
    case RpcFmpException.CantLoad:
      return s_rpcEx.CantLoad(
          getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 0 ) ) ),
          getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 1 ) ) ) );
    case RpcFmpException.MustControlBothToken:
      return s_rpcEx.MustControlBothToken( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ), getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 1 ) ) ) );
    case RpcFmpException.MustDestroyAllTurrets:
      return s_rpcEx.MustDestroyAllTurrets();
    case RpcFmpException.CantFireOn:
      return s_rpcEx.CantFireOn(
          getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 0 ) ) ),
          getTokenString( TokenType.getFromOrdinal( p_exception.getInt( 1 ) ) ) );
    case RpcFmpException.CantFireDisableTide:
      return s_rpcEx.CantFireDisableTide( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ) );
    case RpcFmpException.CantFireDisableFire:
      return s_rpcEx.CantFireDisableFire( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ), getColorString( p_exception.getInt( 1 ) ) );
    case RpcFmpException.CantMoveAlone:
      return s_rpcEx.CantMoveAlone( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ) ) );
    case RpcFmpException.CantLandOn:
      return s_rpcEx
          .CantLandOn( getLandString( LandType.getFromOrdinal( p_exception.getInt( 0 ) ) ) );
    case RpcFmpException.CantLandCloser:
      return s_rpcEx.CantLandCloser( p_exception.getInt( 0 ) );
    case RpcFmpException.NotYourTurn:
      return s_rpcEx.NotYourTurn();
    case RpcFmpException.TokenWasAlreadyMoved:
      return s_rpcEx.TokenWasAlreadyMoved( getTokenString( TokenType.getFromOrdinal( p_exception
          .getInt( 0 ) ), p_exception.getInt( 1 ) ) );
    case RpcFmpException.CantDestroyFreighter:
      return s_rpcEx.CantDestroyFreighter();
    case RpcFmpException.TwoStepAreNotNeighbour:
      return s_rpcEx.TwoStepAreNotNeighbour();
    case RpcFmpException.LoginAlreadyExist:
      return s_rpcEx.LoginAlreadyExist();
    case 0:
    default:
      return p_exception.toString();
    }
  }
}
