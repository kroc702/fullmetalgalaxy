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
package com.fullmetalgalaxy.model.ressources;



import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;

/**
 * this class is used to get a localized string of all class located in rpc package as 
 * GWT localisation can't be ran on server side
 * 
 * @author Vincent Legendre
 *
 */
public class Messages
{

  public static String getTokenString(long p_accountId, EbToken p_token)
  {
    if( p_token.getColor() > EnuColor.None )
    {
      return SharedI18n.getMessages( p_accountId ).tokenDescription(
          getColorString( p_accountId, p_token.getColor() ),
          getTokenString( p_accountId, p_token.getType() ) );
    }
    else
    {
      return getTokenString( p_accountId, p_token.getType() );
    }
  }


  private static String getSingleColorString(long p_accountId, int p_colorValue)
  {
    switch( p_colorValue )
    {
    case EnuColor.Blue:
      return SharedI18n.getMessages( p_accountId ).blue();
    case EnuColor.Cyan:
      return SharedI18n.getMessages( p_accountId ).cyan();
    case EnuColor.Grey:
      return SharedI18n.getMessages( p_accountId ).grey();
    case EnuColor.Yellow:
      return SharedI18n.getMessages( p_accountId ).yellow();
    case EnuColor.Olive:
      return SharedI18n.getMessages( p_accountId ).olive();
    case EnuColor.Red:
      return SharedI18n.getMessages( p_accountId ).red();
    case EnuColor.Orange:
      return SharedI18n.getMessages( p_accountId ).orange();
    case EnuColor.Green:
      return SharedI18n.getMessages( p_accountId ).green();
    case EnuColor.Purple:
      return SharedI18n.getMessages( p_accountId ).purple();
    case EnuColor.None:
      return SharedI18n.getMessages( p_accountId ).colorless();
    case EnuColor.Unknown:
    default:
      return new EnuColor( p_colorValue ).toString();
    }
  }

  public static String getColorString(long p_accountId, int p_colorValue)
  {
    if( p_colorValue == EnuColor.Unknown )
    {
      return getSingleColorString( p_accountId, EnuColor.Unknown );
    }

    String str = "";
    EnuColor color = new EnuColor( p_colorValue );

    for( int iColor = 1; iColor < EnuColor.getMaxColorValue(); iColor *= 2 )
    {
      if( color.isColored( iColor ) )
      {
        if( !str.isEmpty() )
          str += " & ";
        str += getSingleColorString( p_accountId, iColor );
      }
    }

    if( str.isEmpty() )
    {
      str = getSingleColorString( p_accountId, EnuColor.None );
    }
    return str;
  }

  public static String getTideString(long p_accountId, Tide p_tideValue)
  {
    switch( p_tideValue )
    {
    case Low:
      return SharedI18n.getMessages( p_accountId ).low();
    case Medium:
      return SharedI18n.getMessages( p_accountId ).medium();
    case Hight:
      return SharedI18n.getMessages( p_accountId ).hight();
    default:
      return p_tideValue.toString();
    }
  }

  public static String getTokenString(long p_accountId, TokenType p_tokenValue)
  {
    switch( p_tokenValue )
    {
    case Barge:
      return SharedI18n.getMessages( p_accountId ).barge();
    case Freighter:
      return SharedI18n.getMessages( p_accountId ).freighter();
    case Tank:
      return SharedI18n.getMessages( p_accountId ).tank();
    case Crab:
      return SharedI18n.getMessages( p_accountId ).crab();
    case Heap:
      return SharedI18n.getMessages( p_accountId ).heap();
    case WeatherHen:
      return SharedI18n.getMessages( p_accountId ).weatherHen();
    case Turret:
      return SharedI18n.getMessages( p_accountId ).turret();
    case Speedboat:
      return SharedI18n.getMessages( p_accountId ).speedboat();
    case Ore:
      return SharedI18n.getMessages( p_accountId ).ore();
    case Pontoon:
      return SharedI18n.getMessages( p_accountId ).pontoon();
    case None:
    default:
      return p_tokenValue.toString();
    }
  }

  public static String getSectorString(long p_accountId, Sector p_sectorValue)
  {
    switch( p_sectorValue )
    {
    case North:
      return SharedI18n.getMessages( p_accountId ).north();
    case NorthEast:
      return SharedI18n.getMessages( p_accountId ).north_east();
    case SouthEast:
      return SharedI18n.getMessages( p_accountId ).south_east();
    case South:
      return SharedI18n.getMessages( p_accountId ).south();
    case SouthWest:
      return SharedI18n.getMessages( p_accountId ).south_west();
    case NorthWest:
      return SharedI18n.getMessages( p_accountId ).north_west();
    default:
      return p_sectorValue.toString();
    }
  }

  public static String getPlanetString(long p_accountId, PlanetType p_planet)
  {
    switch( p_planet )
    {
    default:
    case Desert:
      return SharedI18n.getMessages( p_accountId ).desert();
    case Grass:
      return SharedI18n.getMessages( p_accountId ).grass();
    case Ice:
      return SharedI18n.getMessages( p_accountId ).ice();
    case Lava:
      return SharedI18n.getMessages( p_accountId ).lava();
    }
  }

  public static String getLandString(long p_accountId, LandType p_landValue)
  {
    switch( p_landValue )
    {
    default:
    case None:
      return SharedI18n.getMessages( p_accountId ).none();
    case Sea:
      return SharedI18n.getMessages( p_accountId ).sea();
    case Reef:
      return SharedI18n.getMessages( p_accountId ).reef();
    case Marsh:
      return SharedI18n.getMessages( p_accountId ).marsh();
    case Plain:
      return SharedI18n.getMessages( p_accountId ).plain();
    case Montain:
      return SharedI18n.getMessages( p_accountId ).montain();
    }
  }


}
