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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.FireDisabling;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 * manage fire cover of all token on board.
 * TODO change m_fireCover array (shouldn't by color, but by registration)
 */
public class BoardFireCover implements Serializable
{
  static final long serialVersionUID = 1;

  // x, y, color index
  private byte m_fireCover[][][] = null;
  /**
   * same as above but for disabled tokens
   */
  private byte m_disabledFireCover[][][] = null;
  private Game m_game = null;

  /**
   * contain all destroyer on board token which should be fire enable/disable/dec as it's during
   * one of his movement.
   * Should allow duplicate elements
   */
  private List<Long> m_lockedToken = new ArrayList<Long>();
  
  
  /**
   * 
   */
  public BoardFireCover()
  {
  }

  /**
   * 
   */
  public BoardFireCover(Game p_game)
  {
    assert p_game != null;
    m_game = p_game;
  }

  /**
   * 
   * @param p_x
   * @param p_y
   * @param p_color must be a single color
   * @return
   */
  public byte getFireCover(AnBoardPosition p_position, EnuColor p_color)
  {
    if( m_fireCover == null )
    {
      reComputeFireCover();
    }
    p_position = m_game.getCoordinateSystem().normalizePosition( p_position );
    if( (p_position.getX() < 0) || (p_position.getY() < 0)
        || (p_position.getX() >= m_fireCover.length)
        || (p_position.getY() >= m_fireCover[0].length)
        || (!p_color.isSingleColor()) )
    {
      return 0;
    }
    return m_fireCover[p_position.getX()][p_position.getY()][p_color.getColorIndex()];
  }


  public byte getDisabledFireCover(AnBoardPosition p_position, EnuColor p_color)
  {
    if( m_disabledFireCover == null )
    {
      reComputeFireCover();
    }
    AnBoardPosition position = m_game.getCoordinateSystem().normalizePosition( p_position );
    if( (position.getX() < 0) || (position.getY() < 0) || (position.getX() >= m_fireCover.length)
        || (position.getY() >= m_fireCover[0].length) || (!p_color.isSingleColor()) )
    {
      return 0;
    }
    return m_disabledFireCover[position.getX()][position.getY()][p_color.getColorIndex()];
  }

  public byte getDisabledFireCover(int p_x, int p_y, EnuColor p_color)
  {
    return getDisabledFireCover( new AnBoardPosition( p_x, p_y ), p_color );
  }


  public byte getDisabledFireCover(int p_x, int p_y, EbTeam p_team)
  {
    return getDisabledFireCover( p_x, p_y, new EnuColor( p_team.getFireColor() ) );
  }


  public EnuColor getFireCover(AnBoardPosition p_position)
  {
    EnuColor cover = new EnuColor( EnuColor.None );
    for( int iColor = 0; iColor < EnuColor.getTotalNumberOfColor(); iColor++ )
    {
      EnuColor currentColor = EnuColor.getColorFromIndex( iColor );
      if( getFireCover( p_position, currentColor ) >= 2 )
      {
        cover.addColor( currentColor );
      }
    }
    return cover;
  }




  protected void incFireCover(AnBoardPosition p_position, EnuColor p_color, byte[][][] p_fireCover)
  {
    p_position = m_game.getCoordinateSystem().normalizePosition( p_position );
    if( (p_fireCover == null) || (p_position.getX() < 0) || (p_position.getY() < 0)
        || (!p_color.isSingleColor()) || (p_position.getX() >= p_fireCover.length)
        || (p_position.getY() >= p_fireCover[0].length) )
    {
      return;
    }
    p_fireCover[p_position.getX()][p_position.getY()][p_color.getColorIndex()]++;

  }

  /**
   * compute color where to add his fire cover
   * @param p_token
   * @return
   */
  private EnuColor getFireCoverColor(EbToken p_token)
  {
    EnuColor color = new EnuColor( EnuColor.None );
    if( p_token.getColor() != EnuColor.None )
    {
      EbRegistration tokenOwner = m_game.getRegistrationByColor( p_token.getColor() );
      if( tokenOwner != null )
      {
        color.setValue( tokenOwner.getTeam( m_game ).getFireColor() );
      }
      else
      {
        color.setValue( p_token.getColor() );
      }
    }
    return color;
  }

  public void incFireCover(EbToken p_token)
  {
    assert p_token != null;
    m_lockedToken.remove( p_token.getId() );
    if( (!p_token.getType().isDestroyer()) || p_token.getLocation() != Location.Board )
    {
      return;
    }
    if( m_lockedToken.contains( p_token.getId() ) )
    {
      RpcUtil.logDebug( "token " + p_token + " is still locked !!!" );
      return;
    }
    if( m_fireCover == null )
    {
      reComputeFireCover();
      return;
    }

    // destroyer range
    int fireRange = m_game.getTokenFireLength( p_token );

    // compute color where to add his fire cover
    EnuColor color = getFireCoverColor( p_token );

    // compute fire cover to use
    byte[][][] fireCover = m_fireCover;
    if( m_game.isTokenFireCoverDisabled( p_token ) )
    {
      fireCover = m_disabledFireCover;
    }

    AnBoardPosition position = p_token.getPosition();
    for( int ix = position.getX() - fireRange; ix < position.getX() + fireRange + 1; ix++ )
    {
      for( int iy = position.getY() - fireRange; iy < position.getY() + fireRange + 1; iy++ )
      {
        if( m_game.canTokenFireOn( p_token, new AnBoardPosition( ix, iy ) ) )
        {
          incFireCover( new AnBoardPosition( ix, iy ), color, fireCover );
        }
      }
    }
  }


  /**
   * 
   * @param p_x
   * @param p_y
   * @param p_color
   * @param p_fireCover
   * @param p_disableFireCover if true, don't search for other destroyer to fire enable
   */
  protected void decFireCover(AnBoardPosition p_position, EnuColor p_color, byte[][][] p_fireCover)
  {
    p_position = m_game.getCoordinateSystem().normalizePosition( p_position );
    if( (p_fireCover == null) || (p_position.getX() < 0) || (p_position.getY() < 0)
        || (!p_color.isSingleColor()) || (p_position.getX() >= p_fireCover.length)
        || (p_position.getY() >= p_fireCover[0].length) )
    {
      return;
    }
    p_fireCover[p_position.getX()][p_position.getY()][p_color.getColorIndex()]--;
  }

  public void decFireCover(EbToken p_token)
  {
    if( m_lockedToken.contains( p_token.getId() ) )
    {
      RpcUtil.logDebug( "token " + p_token + " is already locked !!!" );
      m_lockedToken.add( p_token.getId() );
      return;
    }
    m_lockedToken.add( p_token.getId() );
    if( (!p_token.getType().isDestroyer()) || p_token.getLocation() != Location.Board )
    {
      return;
    }
    if( m_fireCover == null )
    {
      reComputeFireCover();
    }

    // destroyer fire range
    int fireRange = m_game.getTokenFireLength( p_token );

    // compute color where to add his fire cover
    EnuColor color = getFireCoverColor( p_token );

    // compute fire cover to use
    byte[][][] fireCover = m_fireCover;
    if( m_game.isTokenFireCoverDisabled( p_token ) )
    {
      fireCover = m_disabledFireCover;
    }

    AnBoardPosition position = p_token.getPosition();
    for( int ix = position.getX() - fireRange; ix < position.getX() + fireRange + 1; ix++ )
    {
      for( int iy = position.getY() - fireRange; iy < position.getY() + fireRange + 1; iy++ )
      {
        if( m_game.canTokenFireOn( p_token, new AnBoardPosition( ix, iy ) ) )
        {
          decFireCover( new AnBoardPosition( ix, iy ), color, fireCover );
        }
      }
    }
  }



  /**
   * Check for a specific token if his fire disable flag isn't an error...
   * In this case, it can correct flag.
   * @param p_token
   * @return true if there where an error in flag disable
   */
  protected boolean checkFireDisableFlag(EbToken p_token, FdChange p_fdChange,
      Collection<FireDisabling> p_fdRemoved,
      Collection<FireDisabling> p_fdAdded)
  {
    assert p_token != null;
    boolean isFdUpdated = false;
    if( (p_token.getLocation() != Location.Board)
        || (p_token.getColor() == EnuColor.None)
        || (p_token.getType() == TokenType.Freighter)
        || (p_token.getType() == TokenType.Turret) )
    {
      // all theses token can't be fire disabled
      return isFdUpdated;
    }
    if( m_fireCover == null )
    {
      reComputeFireCover();
    }
    // TODO allow two neutralizations with 4 tanks. recursive method ?
    //
    EnuColor color = m_game.getOpponentFireCover( p_token );
    if( p_token.isFireDisabled() && p_fdChange != FdChange.DISABLE )
    {
      // we can't use the list version of removeFireDisabling because of
      // ConcurrentModificationException
      List<FireDisabling> fd2Removed = new ArrayList<FireDisabling>();
      for( FireDisabling fd : p_token.getFireDisablingList() )
      {
        if( !m_game.canTokenFireOn( fd.getDestroyer1( m_game ), p_token )
            || !m_game.canTokenFireOn( fd.getDestroyer2( m_game ), p_token )
            || m_game.isTokenFireCoverDisabled( fd.getDestroyer1( m_game ) )
            || m_game.isTokenFireCoverDisabled( fd.getDestroyer2( m_game ) )
            || !m_game.isTokenTideActive( fd.getDestroyer1( m_game ) )
            || !m_game.isTokenTideActive( fd.getDestroyer2( m_game ) )
            || m_game.getTokenTeamColors( fd.getDestroyer1( m_game ) ).equals(
                m_game.getTokenTeamColors( p_token ) )
            || m_lockedToken.contains( fd.getDestroyer1Id() )
            || m_lockedToken.contains( fd.getDestroyer2Id() ) )
        {
          // one of his destroyer can't really fire on target: remove this FireDisabling
          fd2Removed.add( fd );
          isFdUpdated = true;
        }
      }
      p_fdRemoved.addAll( fd2Removed );
      removeFireDisabling( fd2Removed );
    }

    if( !p_token.isFireDisabled() && p_fdChange != FdChange.ENABLE
        && color.getValue() != EnuColor.None )
    {
      decFireCover( p_token );
      // now construct a fire disabling instance
      FireDisabling fd = new FireDisabling();
      fd.setTarget( p_token );
      // look for destroyer unit that create this fire zone...
      // ok this method may be somewhat inefficient
      // Note that 3 is the maximum fire length
      AnBoardPosition position = p_token.getPosition();
      for( int ix = position.getX() - 3; ix < position.getX() + 4; ix++ )
      {
        for( int iy = position.getY() - 3; iy < position.getY() + 4; iy++ )
        {
          EbToken otherToken = m_game.getToken( new AnBoardPosition( ix, iy ) );
          if( otherToken != null )
          {
            EnuColor tokenTeamColor = m_game.getTokenTeamColors( otherToken );

            if( tokenTeamColor.isColored( color )
              && !m_game.isTokenFireCoverDisabled( otherToken )
                && m_game.canTokenFireOn( otherToken, p_token )
              && !m_lockedToken.contains( otherToken.getId() ) )
            {
              if( fd.getDestroyer1( m_game ) == null )
              {
                fd.setDestroyer1( otherToken );
              }
              else if( fd.getDestroyer2( m_game ) == null )
              {
                fd.setDestroyer2( otherToken );
                break;
              }
              else
              {
                break;
              }
            }
          }
        }
      }

      if( fd.getDestroyer1( m_game ) != null && fd.getDestroyer2( m_game ) != null )
      {
        p_fdAdded.add( fd );
        p_token.addFireDisabling( fd );
        fd.getDestroyer1( m_game ).addFireDisabling( fd );
        fd.getDestroyer2( m_game ).addFireDisabling( fd );
      }
      else
      {
        RpcUtil
            .logError( "BUG: a token should be set as fire disabled, but we didn't found destroyer" );
      }
      incFireCover( p_token );
      isFdUpdated = true;
    }
    return isFdUpdated;
  }

  /**
   * similar to 'checkFireDisableFlag' but recursively check token that may be impacted.
   * @param p_token
   * @param p_fdRemoved
   * @param p_fdAdded
   * @return
   */
  public boolean recursiveCheckFireDisableFlag(EbToken p_token, FdChange p_fdChange,
      Collection<FireDisabling> p_fdRemoved, Collection<FireDisabling> p_fdAdded)
  {
    boolean isFd = p_token.isFireDisabled();
    boolean isFdChanged = checkFireDisableFlag( p_token, p_fdChange, p_fdRemoved, p_fdAdded );
    if( isFd != p_token.isFireDisabled() )
    {
      isFdChanged |= checkFireDisableFlag( p_token.getPosition(),
          m_game.getTokenFireLength( p_token ),
          FdChange.fromDestroyerFireDisableStatus( p_token.isFireDisabled() ), p_fdRemoved,
          p_fdAdded );
    }
    return isFdChanged;
  }


  /**
   * Check fire disable flag of all token in an area around p_position BUT NOT at p_position
   * AND NOT token that are controlled by same player.
   * Note that despite p_radius parameter, area ISN'T round but is square.
   * @param p_position
   * @param p_radius
   * @param p_fdRemoved
   * @param p_fdAdded
   * @return
   */
  public boolean checkFireDisableFlag(AnBoardPosition p_position, int p_radius,
      FdChange p_fdChange,
      Collection<FireDisabling> p_fdRemoved, Collection<FireDisabling> p_fdAdded)
  {
    assert m_game != null;
    boolean isFdChanged = false;
    EbToken token = m_game.getToken( p_position );
    EnuColor teamColor = new EnuColor( EnuColor.None );
    if( token != null )
    {
      teamColor = m_game.getTokenTeamColors( token );
    }
    for( int ix = p_position.getX() - p_radius; ix <= p_position.getX() + p_radius; ix++ )
    {
      for( int iy = p_position.getY() - p_radius; iy <= p_position.getY() + p_radius; iy++ )
      {
        if( ix != p_position.getX() || iy != p_position.getY() )
        {
          token = m_game.getToken( new AnBoardPosition( ix, iy ) );
          if( token != null && !teamColor.contain( token.getColor() ) )
          {
            isFdChanged |= recursiveCheckFireDisableFlag( token, p_fdChange, p_fdRemoved, p_fdAdded );
          }
        }
      }
    }
    return isFdChanged;
  }


  /**
   * Used to filter CheckFireDisableFlag result
   * @author Vincent
   *
   */
  public enum FdChange
  {
    ALL, DISABLE, ENABLE;

    public static FdChange fromDestroyerFireDisableStatus(boolean p_isFireDisable)
    {
      if( p_isFireDisable )
      {
        return ENABLE;
      }
      return DISABLE;
    }
  }

  /**
   * If the two collection contain the same FireDisabling class, removed them both
   * @param p_fdRemoved
   * @param p_fdAdded
   * @return
   */
  public void cleanFireDisableCollection(Collection<FireDisabling> p_fdRemoved,
      Collection<FireDisabling> p_fdAdded)
  {
    Collection<FireDisabling> fd2Remove = new ArrayList<FireDisabling>();
    for( FireDisabling fd : p_fdRemoved )
    {
      if( p_fdAdded.contains( fd ) )
      {
        fd2Remove.add( fd );
      }
    }
    p_fdRemoved.removeAll( fd2Remove );
    p_fdAdded.removeAll( fd2Remove );
  }


  /**
   * Warning: this method don't check if the old target is neutralizing other token !
   * @param p_fireDisabling
   */
  public void removeFireDisabling(FireDisabling p_fireDisabling)
  {
    assert p_fireDisabling != null;
    assert p_fireDisabling.getTarget( m_game ) != null;
    assert p_fireDisabling.getDestroyer1( m_game ) != null;
    assert p_fireDisabling.getDestroyer2( m_game ) != null;
    decFireCover( p_fireDisabling.getTarget( m_game ) );
    p_fireDisabling.getTarget( m_game ).removeFireDisabling( p_fireDisabling );
    p_fireDisabling.getDestroyer1( m_game ).removeFireDisabling( p_fireDisabling );
    p_fireDisabling.getDestroyer2( m_game ).removeFireDisabling( p_fireDisabling );
    incFireCover( p_fireDisabling.getTarget( m_game ) );
  }

  /**
   * Warning: this method don't check if the old target is neutralizing other token !
   * @param p_fireDisabling
   */
  public void removeFireDisabling(Collection<FireDisabling> p_fireDisabling)
  {
    if( p_fireDisabling == null )
    {
      return;
    }
    for( FireDisabling fd : p_fireDisabling )
    {
      removeFireDisabling( fd );
    }
  }

  /**
   * Warning: this method don't check if the old target is neutralizing other token !
   * @param p_fireDisabling
   */
  public void addFireDisabling(FireDisabling p_fireDisabling)
  {
    assert p_fireDisabling != null;
    assert p_fireDisabling.getTarget( m_game ) != null;
    assert p_fireDisabling.getDestroyer1( m_game ) != null;
    assert p_fireDisabling.getDestroyer2( m_game ) != null;
    decFireCover( p_fireDisabling.getTarget( m_game ) );
    p_fireDisabling.getTarget( m_game ).addFireDisabling( p_fireDisabling );
    p_fireDisabling.getDestroyer1( m_game ).addFireDisabling( p_fireDisabling );
    p_fireDisabling.getDestroyer2( m_game ).addFireDisabling( p_fireDisabling );
    incFireCover( p_fireDisabling.getTarget( m_game ) );
  }

  /**
   * Warning: this method don't check if the old target is neutralizing other token !
   * @param p_fireDisabling
   */
  public void addFireDisabling(Collection<FireDisabling> p_fireDisabling)
  {
    if( p_fireDisabling == null )
    {
      return;
    }
    for( FireDisabling fd : p_fireDisabling )
    {
      addFireDisabling( fd );
    }
  }

// TODO 

  // le checkFireDisableFlag est récursive (ou en plusieurs passe) et renvoie
  // une nouvelle liste (complète ou simple mise à jour ?)
//  faire aussi une version pour tous les pions pour recompute et EvtChangeTide. 

  // Attention aux contrôles d'astronefs si une neutralisation implique 2
  // neutralisant de couleur différente

  public void invalidateFireCover()
  {
    m_fireCover = null;
    m_disabledFireCover = null;
    m_lockedToken.clear();
  }

  public void reComputeFireCover()
  {
    Collection<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
    Collection<FireDisabling> fdAdded = new ArrayList<FireDisabling>();
    reComputeFireCover( fdRemoved, fdAdded );
    if( !fdRemoved.isEmpty() || !fdAdded.isEmpty() )
    {
      RpcUtil.logError( "#### fire disable flag was wrong !" );
    }
  }

  public void reComputeFireCover(Collection<FireDisabling> p_fdRemoved,
      Collection<FireDisabling> p_fdAdded)
  {
    // first clear fire cover
    m_fireCover = new byte[m_game.getLandWidth()][m_game.getLandHeight()][EnuColor
        .getTotalNumberOfColor()];
    m_disabledFireCover = new byte[m_game.getLandWidth()][m_game.getLandHeight()][EnuColor
        .getTotalNumberOfColor()];
    m_lockedToken.clear();

    // compute fire cover
    for( EbToken token : m_game.getSetToken() )
    {
      incFireCover( token );
    }

    // some token may have to be fire enabled/disabled
    for( EbToken token : m_game.getSetToken() )
    {
      checkFireDisableFlag( token, FdChange.ALL, p_fdRemoved, p_fdAdded );
    }
    cleanFireDisableCollection( p_fdRemoved, p_fdAdded );
  }


}
