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
package com.fullmetalgalaxy.model.persist;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

  private long m_version = 0;
  private TokenType m_type = TokenType.None;
  private int m_color = EnuColor.None;
  private Location m_location = Location.ToBeConstructed;
  private AnBoardPosition m_position = new AnBoardPosition();
  /**
   * due to legacy, we must keep bullet count as integer.
   * So, it represent 1/10th of a bullet.
   */
  private int m_bulletCount = 0;

  private List<FireDisabling> m_listFireDisabling = null;

  /**
   * TODO remove, as it is here only for backward serialization compatibility
   */
  private long m_carrierTokenId = 0;
  private EbToken m_carrierToken = null;

  /**
   * list of all Token local id (see m_localId) this token actually contain 
   * (empty if this token doesn't contain any other token)
   * 
   * TODO remove this set, as it is here only for backward serialization compatibility
   */
  private Set<Long> m_setContain = null;

  /**
   * list of all Token this token actually contain 
   * (empty if this token doesn't contain any other token)
   */
  private Set<EbToken> m_setContainToken = null;


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
    setBulletCount( getType().getMaxBulletCount() );
  }

  private void init()
  {
    m_type = TokenType.Ore;
    m_position = new AnBoardPosition();
    m_color = EnuColor.None;
    m_location = Location.ToBeConstructed;
    m_bulletCount = 0;
    m_carrierTokenId = 0;
    m_carrierToken = null;
    m_setContain = null;
    m_setContainToken = null;
    m_listFireDisabling = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  /**
   * TODO remove, as it is here only for backward serialization compatibility
   * @param p_game
   */
  public void convertTokenId2Token(Game p_game)
  {
    if( m_carrierToken == null && m_carrierTokenId != 0 )
    {
      m_carrierToken = p_game.getToken( m_carrierTokenId );
    }
    if( m_setContainToken == null && m_setContain != null )
    {
      if( !m_setContain.isEmpty() )
      {
        m_setContainToken = new HashSet<EbToken>();
        for( Long tokenId : m_setContain )
        {
          m_setContainToken.add( p_game.getToken( tokenId ) );
        }
      }
      m_setContain = null;
    }
  }

  /**
   * @param p_token
   * @return all winning point contained by p_token
   */
  public int getWinningPoint()
  {
    int winningPoint = getType().getWinningPoint(  );
    if( containToken() )
    {
      for( EbToken token : getContains() )
      {
        winningPoint += token.getWinningPoint();
      }
    }
    return winningPoint;
  }

  /**
   * @return the "z-index" style property of the image of this token.
   * This value is also used to determine with token to select in case of several
   * token on the same hexagon.
   */
  public int getZIndex()
  {
    if( getLocation() == Location.Graveyard )
    {
      return 0;
    }
    return getType().getZIndex( getPosition().getSector() ) + getPosition().getY() * 2
        + getPosition().getX() % 2;
  }

  /**
   * offset height in pixel to display token image in tactic zoom.
   * it represent the land height.
   * @return
   */  
  public int getLandPixOffset(Game p_game)
  {
    switch( getType() )
    {
    default:
    case Freighter:
    case Turret:
      return 0;
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
    case Crayfish:
      return Game.getLandPixOffset( p_game.getLand( getPosition() ) );
    case Barge:
    case Crab:
    case WeatherHen:
    case Speedboat:
    case Tank:
    case Heap:
    case Pontoon:
    case Sluice:
    case Hovertank:
    case Tarask:
      return p_game.getLandPixOffset( getPosition() );
    }
  }

  /**
   * @return true if this token can have a specific color (ie if not a pontoon nor ore)
   */
  public boolean canBeColored()
  {
    return getType().canBeColored(  );
  }

  /**
   * 
   * @return the size of all his loaded token take inside another token.
   */
  public int getContainSize()
  {
    int loadingSize = 0;
    if( containToken() )
    {
      for( EbToken token : getContains() )
      {
        loadingSize += token.getType().getLoadingSize();
      }
    }
    return loadingSize;
  }

  /**
   * 
   * @return contained ore count
   */
  public int getContainOre()
  {
    int loadingSize = 0;
    if( containToken() )
    {
      for( EbToken token : getContains() )
      {
        if( token.getType().isOre() && token.getType() != TokenType.Ore0 )
        {
          loadingSize += 1;
        }
      }
    }
    return loadingSize;
  }



  /**
   * @param p_token
   * @return the size of p_token and all his loaded token take inside another token.
   */
  public int getFullLoadingSize()
  {
    return getType().getLoadingSize() + getContainSize();
  }

  

  /**
   * @param p_token
   * @return true if the two token have at least one neighbor position.
   */
  
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

  
  public ArrayList<EbToken> getNeighborTokens(Game p_game)
  {
    ArrayList<EbToken> neighbor = new ArrayList<EbToken>();
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.isNeighbor( this ) )
      {
        neighbor.add( token );
      }
    }
    return neighbor;
  }

  /**
   * Not used anymore
   * @return true if an opponent token is a neighbor of this token.
   */
  public boolean haveOponentNeighbor(Game p_game)
  {
    // first determine the token owner color
    EnuColor tokenOwnerColor = p_game.getTokenOwnerColor( this );
    // TODO it's not optimal... we should look onto the six neighbor hexagons
    for( EbToken token : p_game.getSetToken() )
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
   * @return true if token have a unit next to p_position he can control (may be different color but same owner)
   */
  public boolean canControlNeighbor(Game p_game, AnBoardPosition p_position)
  {
    if( p_position == null || getLocation() != Location.Board || !canBeColored() )
    {
      return false;
    }
    boolean isDestroyer = getType().isDestroyer();
    if( getType() == TokenType.Freighter && p_game.getToken( p_position, TokenType.Turret ) != null )
    {
      isDestroyer = true;
    }
    // first determine the token color
    EnuColor tokenColor = getEnuColor();
    for( Sector sector : Sector.values() )
    {
      AnBoardPosition position = p_position.getNeighbour( sector );
      for( EbToken token : p_game.getAllToken( position ) )
      {
        if( (token.canBeColored()) && (tokenColor.getValue() != token.getColor())
            && (token.isNeighbor( this ))
            && ( isDestroyer || token.getType().isDestroyer() ) )
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @bug in this method, opponent only mean different color.
   * @return true if at least one opponent destroyer can fire on this token
   */
  public boolean canBeATarget(Game p_game)
  {
    AnBoardPosition position = getPosition();
    for( int ix = position.getX() - 3; ix < position.getX() + 4; ix++ )
    {
      for( int iy = position.getY() - 3; iy < position.getY() + 4; iy++ )
      {
        EbToken otherToken = p_game.getToken( new AnBoardPosition( ix, iy ) );
        if( otherToken != null )
        {
          if( otherToken.getColor() != getColor()
 && !p_game.isTokenFireCoverDisabled( otherToken )
              && p_game.canTokenFireOn( otherToken, this ) )
          {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param 
   * @return true if the token have at least one neighbor position with p_position.
   */
  
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
  public boolean canLoad(TokenType p_tokenType)
  {
    return getType().canLoad( p_tokenType );
  }

  /**
   * determine is this token is allowed to move on this kind of land
   * always true if p_land is reef or marsh
   * @param p_land 
   * @return
   */
  public boolean canMoveOn(Game p_game, LandType p_land)
  {
    if( p_land == LandType.None )
    {
      return false;
    }

    switch( getType() )
    {
    case Barge:
    case Speedboat:
    case Crayfish:
    case Tarask:
      if( p_land == LandType.Montain || p_land == LandType.Plain )
      {
        return false;
      }
      break;
    case Hovertank:
      if( p_land == LandType.Montain )
      {
        return false;
      }
      break;
    case Heap:
    case Sluice:
      if( p_land == LandType.Montain || p_land == LandType.Sea )
      {
        return false;
      }
      break;
    case Crab:
      if( p_land == LandType.Montain && containToken() )
      {
        for( EbToken token : getContains() )
        {
          if( token.getType() == TokenType.Heap )
          {
            return false;
          }
        }
      }
    case Tank:
    case WeatherHen:
      if( p_land == LandType.Sea )
      {
        return false;
      }
      break;
    case Pontoon:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
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
  public boolean canMoveOn(Game p_game, EbRegistration p_player, AnBoardPosition p_position)
  {
    assert p_player != null;
    assert p_position != null;
    // check that no token is already on this hexagon
    EbToken newTokenOnWay = p_game.getToken( p_position );
    EnuColor myColor = p_player.getEnuColor();

    // if newTokenOnWay == this, this mean that barge head want to move on barge tail: this is allowed
    if( newTokenOnWay != null && newTokenOnWay != this)
    {
      if( (newTokenOnWay.getType() == TokenType.Pontoon) || (newTokenOnWay.getType() == TokenType.Sluice) )
      {
        return p_game.canTokenLoad( newTokenOnWay, this );
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
      EnuColor fireCoverColor = p_game.getOpponentFireCover( myColor.getValue(), p_position );
      if( fireCoverColor.getValue() != EnuColor.None )
      {
        return false;
      }
      // check this token is allowed to move on this hexagon
      // TODO maybe check with isTokenTideActive ?

      // determine, according to current tide, if the new position is sea,
      // plain or montain
      LandType land = p_game.getLand( p_position ).getLandValue( p_game.getCurrentTide() );
      EbToken tokenPontoon = p_game.getToken( p_position, TokenType.Pontoon );
      if( tokenPontoon == null )
      {
        tokenPontoon = p_game.getToken( p_position, TokenType.Sluice );
      }
      if( (tokenPontoon != null) && !(tokenPontoon.canLoad( getType() )) )
      {
        return false;
      }
      // check this token is allowed to move on this hexagon
      if( canMoveOn( p_game, land ) == false )
      {
        return false;
      }
      // if last position is also a land, check that the token is colored
      // (not a minerais neither a pontoon)
      if( ((p_game.getToken( getPosition() ) == null) || (p_game.getToken( getPosition() )
          .getColor() == EnuColor.None))
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
   * this method is used for eclipse debugger
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
    assert p_token != null;
    if( p_token.getCarrierToken() != null )
    {
      // a token can be carried by only one carrier at a time
      p_token.getCarrierToken().unloadToken( p_token );
    }
    p_token.setCarrierToken( (EbToken)this );
    p_token.setLocation( Location.Token );
    p_token.setPosition( new AnBoardPosition( -1, -1 ) );
    assert p_token.getId() != 0;
    if( getContains() == null )
    {
      m_setContainToken = new HashSet<EbToken>();
    }
    getContains().add( p_token );
  }

  public void unloadToken(EbToken p_token)
  {
    assert p_token != null;
    assert containToken();
    getContains().remove( p_token );
    p_token.setCarrierToken( null );
    if( getContains().isEmpty() )
    {
      m_setContainToken = null;
    }
  }



  
  public EnuColor getEnuColor()
  {
    return new EnuColor( getColor() );
  }

  public void setEnuColor(EnuColor p_color)
  {
    m_color = p_color.getValue();
  }

  public boolean containToken()
  {
    return getContains() != null;
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
   * 
   * This version, compare to getContains, make a copy
   * of the set to allow function that use it to modify
   * token content while iterating on this set.
   * 
   * @return a copy of token set contain (never null)
   */
  public Set<EbToken> getCopyContains()
  {
    Set<EbToken> set = new HashSet<EbToken>();
    if( getContains() != null )
    {
      set.addAll( getContains() );
    }
    return set;
  }

  /**
   * return a set of all contained token.
   * @return the contain token set or null
   */
  public Set<EbToken> getContains()
  {
    return m_setContainToken;
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
   * @return the carrierToken
   */
  public EbToken getCarrierToken()
  {
    return m_carrierToken;
  }

  /**
   * @param p_carrierToken the carrierToken to set
   */
  public void setCarrierToken(EbToken p_carrierToken)
  {
    m_carrierToken = p_carrierToken;
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
  public float getBulletCount()
  {
    return (m_bulletCount/10f);
  }


  /**
   * @param p_bulletCount the bulletCount to set
   */
  public void setBulletCount(float p_bulletCount)
  {
    m_bulletCount = (int)Math.round(p_bulletCount*10);
    if( m_bulletCount < 0 )
    {
      m_bulletCount = 0;
    }
    if( m_bulletCount > getType().getMaxBulletCount()*10 )
    {
      m_bulletCount = getType().getMaxBulletCount()*10;
    }
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
    if( m_listFireDisabling == null )
    {
      return false;
    }
    for( FireDisabling fd : m_listFireDisabling )
    {
      if( fd.getTargetId() == getId() )
      {
        return true;
      }
    }
    return false;
  }

  public boolean isFireDisabling()
  {
    if( m_listFireDisabling == null )
    {
      return false;
    }
    for( FireDisabling fd : m_listFireDisabling )
    {
      if( fd.getDestroyer1Id() == getId() )
      {
        return true;
      }
      if( fd.getDestroyer2Id() == getId() )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Warning: unless you know what your are doing, don't use it !
   * Use same function provided in EbGame to update fire cover. 
   * @param p_fireDisabled the fireDisabled to set
   */
  public void addFireDisabling(FireDisabling p_fireDisabling)
  {
    if( p_fireDisabling != null )
    {
      if( m_listFireDisabling == null )
      {
        m_listFireDisabling = new ArrayList<FireDisabling>();
      }
      if( !m_listFireDisabling.contains( p_fireDisabling ) )
      {
        m_listFireDisabling.add( p_fireDisabling );
      }
    }
  }

  /**
   * Should be called only by BoardFireCover class
   * @param p_fireDisabling
   */
  public void removeFireDisabling(FireDisabling p_fireDisabling)
  {
    if( m_listFireDisabling != null && p_fireDisabling != null )
    {
      m_listFireDisabling.remove( p_fireDisabling );
      if( m_listFireDisabling.isEmpty() )
      {
        m_listFireDisabling = null;
      }
    }
  }

  /**
   * Should be called only by BoardFireCover class
  */
  public void clearFireDisabling()
  {
    m_listFireDisabling = null;
  }

  /**
   * Should be called only by BoardFireCover class
   */
  public List<FireDisabling> getFireDisablingList()
  {
    return m_listFireDisabling;
  }

  /**
   * @return the version
   */
  public long getVersion()
  {
    return m_version;
  }

  /**
   * @param p_version the version to set
   */
  public void setVersion(long p_version)
  {
    m_version = p_version;
  }


  public void incVersion()
  {
    m_version++;
  }

  public void decVersion()
  {
    m_version--;
  }


}
