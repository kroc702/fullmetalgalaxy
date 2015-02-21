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
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author vincent legendre
 *
 */
public enum TokenType implements java.io.Serializable, IsSerializable
{
  /**
   * Theses values are store as is in data base.
   */
  Freighter, Turret, Barge, Speedboat, Tank, Heap, Crab, WeatherHen, Pontoon, Ore, None,
 Ore0, Ore3, Ore5, Crayfish, Sluice, Hovertank, Tarask, Destroyer;

  public boolean canBeColored()
  {
    switch( this )
    {
    case Turret:
    case Barge:
    case WeatherHen:
    case Crab:
    case Freighter:
    case Speedboat:
    case Tank:
    case Heap:
    case Crayfish:
    case Hovertank:
    case Tarask:
    case Destroyer:
      return true;
    case Pontoon:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    case Sluice:
    default:
      return false;
    }
  }

  public boolean isOre()
  {
    switch( this )
    {
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
      return true;
    default:
    return false;
    }
  }
  
  public int getWinningPoint()
  {
    switch( this )
    {
    case Ore0:
      return 0;
    case Ore:
      return 2;
    case Ore3:
      return 3;
    case Ore5:
      return 5;
    default:
      return 1;
    }
  }

  public boolean isDestroyer()
  {
    switch( this )
    {
    case Turret:
    case Speedboat:
    case Tank:
    case Heap:
    case Hovertank:
    case Tarask:
    case Destroyer:
      return true;
    case Freighter:
    case Barge:
    case WeatherHen:
    case Crab:
    case Pontoon:
    case Sluice:
    case Crayfish:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    default:
      return false;
    }
  }
  

  /**
   * @return the size it take inside another token (don't take in account token inside him)
   */
  public int getLoadingSize()
  {
    switch( this )
    {
    case Freighter:
      return 10;
    case Barge:
    case Destroyer:
      return 6;
    case WeatherHen:
    case Crab:
    case Crayfish:
    case Tarask:
      return 2;
    case Pontoon:
    case Speedboat:
    case Tank:
    case Heap:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    case Sluice:
    case Hovertank:
    case Turret:
    default:
      return 1;
    }
  }

  /**
   * @return the size it take inside another token
   */
  public int getLoadingCapability()
  {
    switch( this )
    {
    case Freighter:
      return 1000;
    case Barge:
      return 4;
    case WeatherHen:
      return 1;
    case Crab:
    case Crayfish:
      return 2;
    case Pontoon:
    case Sluice:
      return 10;
    case Speedboat:
    case Tank:
    case Heap:
    case Hovertank:
    case Tarask:
    case Destroyer:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    case Turret:
    default:
      return 0;
    }
  }

  /**
   * 
   * @return the maximum number of bullet according to the token type
   */
  public int getMaxBulletCount()
  {
    switch( this )
    {
    case Turret:
      return 10;
    case Destroyer:
      return 6;
    case Freighter:
      return 3;
    case Speedboat:
    case Tank:
    case Heap:
    case WeatherHen:
    case Tarask:
    case Hovertank:
      return 2;
    case Barge:
    case Crab:
    case Pontoon:
    case Ore:
    default:
      return 0;
    }
  }

  
  
  public int getZIndex(Sector p_sector)
  {
    switch( this )
    {
    case Pontoon:
    case Sluice:
      return 0;
    case Freighter:
      if( (p_sector == Sector.North) || (p_sector == Sector.SouthEast)
          || (p_sector == Sector.SouthWest) )
        return 3;
      else
        return 2;
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    case Barge:
    case Crab:
    case WeatherHen:
    case Speedboat:
    case Tank:
    case Heap:
    case Crayfish:
    case Hovertank:
    case Tarask:
    case Destroyer:
      return 3;
    case Turret:
      return 6;
    default:
      return 10;
    }
  }

  /**
   * determine if this token can load the given token.
   * don't check already loaded token.
   * @param p_tokenType the token type value we want to load
   * @return
   */
  public boolean canLoad(TokenType p_tokenType)
  {
    switch( this )
    {
    case Freighter:
      return true;
    case Barge:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Crab)
          || (p_tokenType == TokenType.Heap) || (p_tokenType == TokenType.WeatherHen)
          || (p_tokenType.isOre())
          || (p_tokenType == TokenType.Sluice) || (p_tokenType == TokenType.Pontoon)
          || (p_tokenType == TokenType.Crayfish) )
      {
        return true;
      }
      return false;
    case Crab:
    case Crayfish:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Heap)
          || (p_tokenType.isOre()) 
          || (p_tokenType == TokenType.Sluice) || (p_tokenType == TokenType.Pontoon) )
      {
        return true;
      }
      return false;
    case WeatherHen:
      if( (p_tokenType.isOre()) )
      {
        return true;
      }
      return false;
    case Pontoon:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Heap)
          || (p_tokenType.isOre())
          || (p_tokenType == TokenType.Crab)
          || (p_tokenType == TokenType.WeatherHen) )
      {
        return true;
      }
      return false;
    case Sluice:
      if( (p_tokenType == TokenType.Speedboat) || (p_tokenType == TokenType.Barge)
          || (p_tokenType == TokenType.Crayfish) || (p_tokenType == TokenType.Tarask)
          || (p_tokenType == TokenType.Destroyer) )
      {
        return true;
      }
      return false;
    case Speedboat:
    case Tank:
    case Heap:
    case Ore:
    case Turret:
    default:
      return false;
    }
  }


}
