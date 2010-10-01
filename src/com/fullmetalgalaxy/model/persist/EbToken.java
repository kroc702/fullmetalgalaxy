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
package com.fullmetalgalaxy.model.persist;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;



/**
 * @author Vincent Legendre
 * this class represent any token in game.
 */
public class EbToken extends EbBase
{
  static final long serialVersionUID = 12;

  private TokenType m_type = TokenType.None;
  private int m_color = EnuColor.None;
  private Location m_location = Location.ToBeConstructed;
  private AnBoardPosition m_position = new AnBoardPosition();
  private int m_bulletCount = 0;
  private boolean m_fireDisabled = false;

  private EbGame m_game = null;

  private long m_carrierTokenId = 0;

  /**
   * list of all Token local id (see m_localId) this token actually contain 
   * (empty if this token doesn't contain any other token)
   */
  private Set<Long> m_setContain = null;


  /**
   * 
   */
  public EbToken()
  {
    super();
    init();
  }

  public EbToken(EbBase p_base)
  {
    super( p_base );
    init();
  }


  /**
   * 
   */
  public EbToken(TokenType p_type)
  {
    this();
    setType( p_type );
    setBulletCount( getMaxBulletCount() );
  }

  private void init()
  {
    m_type = TokenType.Ore;
    m_position = new AnBoardPosition();
    m_color = EnuColor.None;
    m_location = Location.ToBeConstructed;
    m_bulletCount = 0;
    m_carrierTokenId = 0;
    m_game = null;
    m_setContain = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /**
   * @return the "z-index" style property of the image of this token.
   * This value is also used to determine with token to select in case of several
   * token on the same hexagon.
   */
  // @Transient
  public int getZIndex()
  {
    return getZIndex( getType(), getPosition().getSector() ) + getPosition().getY() * 2
        + getPosition().getX() % 2;
  }

  public static int getZIndex(TokenType p_tokenType, Sector p_sector)
  {
    switch( p_tokenType )
    {
    case Pontoon:
      return 0;
    case Freighter:
      if( (p_sector == Sector.North) || (p_sector == Sector.SouthEast)
          || (p_sector == Sector.SouthWest) )
        return 1;
      else
        return 0;
    case Ore:
    case Barge:
    case Crab:
    case WeatherHen:
    case Speedboat:
    case Tank:
    case Heap:
      return 1;
    case Turret:
      return 4;
    default:
      return 10;
    }
  }

  /**
   * offset height in pixel to display token image in tactic zoom.
   * it represent the land height.
   * @return
   */
  // @Transient
  public int getLandPixOffset()
  {
    switch( getType() )
    {
    default:
    case Freighter:
    case Turret:
      return 0;
    case Ore:
      return EbGame.getLandPixOffset( getGame().getLand( getPosition() ) );
    case Barge:
    case Crab:
    case WeatherHen:
    case Speedboat:
    case Tank:
    case Heap:
    case Pontoon:
      return getGame().getLandPixOffset( getPosition() );
    }
  }

  /**
   * @return true if this token can have a specific color (ie if not a pontoon nor ore)
   */
  // @Transient
  public boolean canBeColored()
  {
    return canBeColored( getType() );
  }

  public static boolean canBeColored(TokenType p_type)
  {
    switch( p_type )
    {
    case Turret:
    case Barge:
    case WeatherHen:
    case Crab:
    case Freighter:
    case Speedboat:
    case Tank:
    case Heap:
      return true;
    case Pontoon:
    case Ore:
    default:
      return false;
    }
  }


  /**
   * @return the size it take inside another token (don't take in account token inside him)
   */
  // @Transient
  public int getLoadingSize()
  {
    switch( getType() )
    {
    case Freighter:
      return 1000;
    case Barge:
      return 4;
    case WeatherHen:
    case Crab:
      return 2;
    case Pontoon:
    case Speedboat:
    case Tank:
    case Heap:
    case Ore:
    case Turret:
    default:
      return 1;
    }
  }

  /**
   * @return the size it take inside another token
   */
  // @Transient
  public int getLoadingCapability()
  {
    switch( getType() )
    {
    case Freighter:
      return 1000;
    case Barge:
      return 4;
    case WeatherHen:
      return 1;
    case Crab:
      return 2;
    case Pontoon:
      return 10;
    case Speedboat:
    case Tank:
    case Heap:
    case Ore:
    case Turret:
    default:
      return 0;
    }
  }

  /**
   * 
   * @return the maximum number of bullet according to the token type
   */
  // @Transient
  public int getMaxBulletCount()
  {
    switch( getType() )
    {
    case Turret:
      return 10;
    case Speedboat:
    case Tank:
    case Heap:
      return 2;
    case Freighter:
    case Barge:
    case WeatherHen:
    case Crab:
    case Pontoon:
    case Ore:
    default:
      return 0;
    }
  }

  // @Transient
  public boolean isDestroyer()
  {
    switch( getType() )
    {
    case Turret:
    case Speedboat:
    case Tank:
    case Heap:
      return true;
    case Freighter:
    case Barge:
    case WeatherHen:
    case Crab:
    case Pontoon:
    case Ore:
    default:
      return false;
    }
  }

  /**
   * @param p_token
   * @return true if the two token have at least one neighbor position.
   */
  // @Transient
  public boolean isNeighbor(EbToken p_token)
  {
    if( (getLocation() != Location.Board) || (p_token.getLocation() != Location.Board) )
    {
      return false;
    }
    if( isNeighbor( p_token.getPosition() ) )
    {
      return true;
    }
    for( AnBoardPosition otherPosition : p_token.getExtraPositions() )
    {
      if( isNeighbor( otherPosition ) )
      {
        return true;
      }
    }
    return false;
  }

  // @Transient
  public ArrayList<EbToken> getNeighborTokens()
  {
    ArrayList<EbToken> neighbor = new ArrayList<EbToken>();
    for( EbToken token : getGame().getSetToken() )
    {
      if( token.isNeighbor( this ) )
      {
        neighbor.add( token );
      }
    }
    return neighbor;
  }

  /**
   * 
   * @return true if an opponent token is a neighbor of this token.
   */
  // @Transient
  public boolean haveOponentNeighbor()
  {
    // first determine the token owner color
    EnuColor tokenOwnerColor = getGame().getTokenOwnerColor( this );
    for( EbToken token : getGame().getSetToken() )
    {
      if( (token.canBeColored()) && (!tokenOwnerColor.isColored( token.getColor() ))
          && (token.isNeighbor( this )) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * @param 
   * @return true if the token have at least one neighbor position with p_position.
   */
  // @Transient
  public boolean isNeighbor(AnBoardPosition p_position)
  {
    if( getPosition().isNeighbor( p_position ) )
    {
      return true;
    }
    for( AnBoardPosition myPosition : getExtraPositions() )
    {
      if( myPosition.isNeighbor( p_position ) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * determine if this token can load the given token.
   * don't check already loaded token.
   * @param p_tokenType the token type value we want to load
   * @return
   */
  // @Transient
  public boolean canLoad(TokenType p_tokenType)
  {
    switch( getType() )
    {
    case Freighter:
      return true;
    case Barge:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Crab)
          || (p_tokenType == TokenType.Heap) || (p_tokenType == TokenType.WeatherHen)
          || (p_tokenType == TokenType.Ore) || (p_tokenType == TokenType.Pontoon) )
      {
        return true;
      }
      return false;
    case Crab:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Heap)
          || (p_tokenType == TokenType.Ore) || (p_tokenType == TokenType.Pontoon) )
      {
        return true;
      }
      return false;
    case WeatherHen:
      if( p_tokenType == TokenType.Ore )
      {
        return true;
      }
      return false;
    case Pontoon:
      if( (p_tokenType == TokenType.Tank) || (p_tokenType == TokenType.Heap)
          || (p_tokenType == TokenType.Ore) || (p_tokenType == TokenType.Crab)
          || (p_tokenType == TokenType.WeatherHen) )
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

  /**
   * determine is this token is allowed to move on this kind of land
   * @param p_land must be in [None,Sea,Plain,Montain]
   * @return
   */
  public boolean canMoveOn(LandType p_land)
  {
    if( p_land == LandType.None )
    {
      return false;
    }

    switch( getType() )
    {
    case Barge:
    case Speedboat:
      if( p_land != LandType.Sea )
      {
        return false;
      }
      break;
    case Heap:
      if( p_land != LandType.Plain )
      {
        return false;
      }
      break;
    case Crab:
      for( EbToken token : getSetContain() )
      {
        if( (token.getType() == TokenType.Heap) && (p_land == LandType.Montain) )
        {
          return false;
        }
      }
    case Tank:
    case WeatherHen:
    case Ore:
      if( p_land == LandType.Sea )
      {
        return false;
      }
      break;
    case Pontoon:
      return true;
    case Freighter:
    case Turret:
    default:
      return false;
    }
    return true;
  }


  /**
   * used for path finder
   * @param p_position
   * @return
   */
  public boolean canMoveOn(EbRegistration p_player, AnBoardPosition p_position)
  {
    assert p_player != null;
    assert p_position != null;
    EbGame game = getGame();
    // check that no token is already on this hexagon
    EbToken newTokenOnWay = game.getToken( p_position );
    EnuColor myColor = p_player.getEnuColor();

    // if newTokenOnWay == this, this mean that barge head want to move on barge tail: this is allowed
    if( newTokenOnWay != null && newTokenOnWay != this)
    {
      if( newTokenOnWay.getType() == TokenType.Pontoon )
      {
        return game.canTokenLoad( newTokenOnWay, this );
      }
      // disable load/unload action for path finder to search path
      return false;
      // // enter in newTokenOnWay
      // if( newTokenOnWay.getType() == TokenType.Turret )
      // {
      // // we never want to enter into the turret, but into the freighter
      // newTokenOnWay = game.getToken( p_position, TokenType.Freighter );
      // assert newTokenOnWay != null;
      // }
      // if( !game.canTokenLoad( newTokenOnWay, this ) )
      // {
      // return false;
      // }
      // if( !game.isTokenTideActive( newTokenOnWay ) )
      // {
      // return false;
      // }
      // if( !game.isTokenFireActive( myColor, newTokenOnWay ) )
      // {
      // return false;
      // }
      // if( !myColor.isColored( newTokenOnWay.getColor() ) )
      // {
      // if( newTokenOnWay.getType() != TokenType.Freighter )
      // {
      // return false;
      // }
      // else
      // {
      // // player want enter into a freighter he don't own: check presence of
      // // turret
      // ArrayList<com.fullmetalgalaxy.model.persist.AnBoardPosition> extraPositions =
      // newTokenOnWay
      // .getExtraPositions();
      // for( Iterator<com.fullmetalgalaxy.model.persist.AnBoardPosition> it =
      // extraPositions.iterator(); it
      // .hasNext(); )
      // {
      // EbToken turret = game.getToken( (AnBoardPosition)it.next(),
      // TokenType.Turret );
      // if( turret != null )
      // {
      // return false;
      // }
      // }
      // }
      // }
    }
    else
    {
      // check this token is not under an opponent fire cover
      EnuColor fireCoverColor = game.getOpponentFireCover( myColor.getValue(), p_position );
      if( fireCoverColor.getValue() != EnuColor.None )
      {
        return false;
      }
      // check this token is allowed to move on this hexagon
      // TODO maybe check with isTokenTideActive ?

      // determine, according to current tide, if the new position is sea,
      // plain or montain
      LandType land = game.getLand( p_position ).getLandValue( game.getCurrentTide() );
      EbToken tokenPontoon = game.getToken( p_position, TokenType.Pontoon );
      if( (tokenPontoon != null) && !(tokenPontoon.canLoad( getType() )) )
      {
        return false;
      }
      // check this token is allowed to move on this hexagon
      if( canMoveOn( land ) == false )
      {
        return false;
      }
      // if last position is also a land, check that the token is colored
      // (not a minerais neither a pontoon)
      if( ((game.getToken( getPosition() ) == null) || (game.getToken( getPosition() ).getColor() == EnuColor.None))
          && (getColor() == EnuColor.None) )
      {
        return false;
      }
    }
    return true;
  }

  /**
   * @return the size in hexagon of the token
   */
  // @Transient
  public int getHexagonSize()
  {
    switch( getType() )
    {
    case Freighter:
      return 4;
    case Barge:
      return 2;
    default:
      return 1;
    }
  }

  /**
   * 
   * @param p_token
   * @return all board positions of a given token 
   */
  // @Transient
  public ArrayList<com.fullmetalgalaxy.model.persist.AnBoardPosition> getExtraPositions()
  {
    ArrayList<com.fullmetalgalaxy.model.persist.AnBoardPosition> list = new ArrayList<com.fullmetalgalaxy.model.persist.AnBoardPosition>();

    if( (getLocation() == Location.Board) && (getPosition().getX() >= 0)
        && (getPosition().getY() >= 0) )
    {
      // token is on board
      switch( getType() )
      {
      case Freighter:
        switch( getPosition().getSector() )
        {
        case North:
        case SouthEast:
        case SouthWest:
          list.add( getPosition().getNeighbour( Sector.North ) );
          list.add( getPosition().getNeighbour( Sector.SouthEast ) );
          list.add( getPosition().getNeighbour( Sector.SouthWest ) );
          break;
        case NorthEast:
        case South:
        case NorthWest:
          list.add( getPosition().getNeighbour( Sector.NorthEast ) );
          list.add( getPosition().getNeighbour( Sector.South ) );
          list.add( getPosition().getNeighbour( Sector.NorthWest ) );
        default:
          break;
        }
        break;

      case Barge:
        list.add( getPosition().getNeighbour( getPosition().getSector() ) );
        break;

      default:
        // other token are on a single hexagon: do nothing
        break;
      }
    }
    return list;
  }

  /**
   * this method is used for eclipse debuger
   */
  @Override
  public String toString()
  {
    String str = "";
    if( getColor() > EnuColor.None )
    {
      str += getEnuColor().toString() + " ";
    }
    return str + getType().toString();
  }


  public void loadToken(EbToken p_token)
  {
    if( p_token.getCarrierToken() != null )
    {
      // a token can be carried by only one carrier at a time
      p_token.getCarrierToken().unloadToken( p_token );
    }
    p_token.setCarrierToken( (EbToken)this );
    p_token.setLocation( Location.Token );
    p_token.setPosition( new AnBoardPosition( -1, -1 ) );
    assert p_token.getId() != 0;
    if( m_setContain == null )
    {
      m_setContain = new HashSet<Long>();
    }
    m_setContain.add( p_token.getId() );
  }

  public void unloadToken(EbToken p_token)
  {
    assert m_setContain != null;
    m_setContain.remove( p_token.getId() );
    p_token.setCarrierToken( null );
    // p_token.setLocation( Location.Board );
  }



  // @Transient
  public EnuColor getEnuColor()
  {
    return new EnuColor( getColor() );
  }

  public void setEnuColor(EnuColor p_color)
  {
    m_color = p_color.getValue();
  }


  // getters / setters
  // -----------------
  /**
   * @return the type
   */
  public TokenType getType()
  {
    return m_type;
  }

  /**
   * @param p_type the type to set
   */
  public void setType(TokenType p_type)
  {
    m_type = p_type;
  }

  /**
   * @return the idColor
   */
  public int getColor()
  {
    return m_color;
  }

  /**
   * @param p_idColor the idColor to set
   */
  public void setColor(int p_color)
  {
    setEnuColor( new EnuColor( p_color ) );
  }

  /**
   * return a set of all contained token.
   * compute this set from the set of local id. 
   * @return the contain
   */
  public Set<EbToken> getSetContain()
  {
    Set<EbToken> setContain = new HashSet<EbToken>();
    EbGame game = getGame();
    if( (m_setContain == null) || (game == null) )
    {
      return setContain;
    }
    for( Long localId : m_setContain )
    {
      setContain.add( game.getToken( localId ) );
    }
    return setContain;
  }


  /**
   * @return the location
   */
  public Location getLocation()
  {
    return m_location;
  }

  /**
   * @param p_location the location to set
   */
  public void setLocation(Location p_location)
  {
    m_location = p_location;
  }

  /**
   * @return the game
   */
  public EbGame getGame()
  {
    return m_game;
  }

  /**
   * @param p_game the game to set
   */
  public void setGame(EbGame p_game)
  {
    m_game = p_game;
  }

  /**
   * @return the carrierToken
   */
  public EbToken getCarrierToken()
  {
    if( m_carrierTokenId == 0 || getGame() == null )
    {
      return null;
    }
    return getGame().getToken( m_carrierTokenId );
  }

  /**
   * @param p_carrierToken the carrierToken to set
   */
  public void setCarrierToken(EbToken p_carrierToken)
  {
    if( p_carrierToken == null )
    {
      m_carrierTokenId = 0;
    }
    else
    {
      m_carrierTokenId = p_carrierToken.getId();
    }
  }

  /**
   * @return the position
   */
  public AnBoardPosition getPosition()
  {
    return m_position;
  }

  /**
   * @param p_position the position to set
   */
  protected void setPosition(AnBoardPosition p_position)
  {
    m_position = p_position;
  }


  /**
   * @return the bulletCount
   */
  public int getBulletCount()
  {
    return m_bulletCount;
  }


  /**
   * @param p_bulletCount the bulletCount to set
   */
  public void setBulletCount(int p_bulletCount)
  {
    m_bulletCount = p_bulletCount;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbBase#setLastUpdate(java.util.Date)
   */
  /*@Override
  public void setLastUpdate(Date p_lastUpdate)
  {
    super.setLastUpdate( p_lastUpdate );
    if( getGame() != null )
    {
      getGame().updateLastTokenUpdate( getLastUpdate() );
    }
  }*/

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbBase#setTrancient()
   */

  /**
   * @return the fireDisabled
   */
  public boolean isFireDisabled()
  {
    return m_fireDisabled;
  }

  /**
   * @param p_fireDisabled the fireDisabled to set
   */
  public void setFireDisabled(boolean p_fireDisabled)
  {
    m_fireDisabled = p_fireDisabled;
  }



}