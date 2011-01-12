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
package com.fullmetalgalaxy.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


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
  private EbGame m_game = null;

  /**
   * contain all destroyer on board token which should be fire enable/disable/dec as it's during
   * one of his movement.
   */
  private Set<EbToken> m_lockedToken = new HashSet<EbToken>();
  
  
  /**
   * 
   */
  public BoardFireCover()
  {
  }

  /**
   * 
   */
  public BoardFireCover(EbGame p_game)
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
  public byte getFireCover(int p_x, int p_y, EnuColor p_color)
  {
    if( m_fireCover == null )
    {
      reComputeFireCover();
    }
    if( (p_x < 0) || (p_y < 0) || (p_x >= m_fireCover.length) || (p_y >= m_fireCover[0].length)
        || (!p_color.isSingleColor()) )
    {
      return 0;
    }
    return m_fireCover[p_x][p_y][p_color.getColorIndex()];
  }


  public byte getDisabledFireCover(int p_x, int p_y, EbRegistration p_registration)
  {
    if( m_disabledFireCover == null )
    {
      reComputeFireCover();
    }
    if( (p_x < 0) || (p_y < 0) || (p_x >= m_disabledFireCover.length)
        || (p_y >= m_disabledFireCover[0].length) )
    {
      return 0;
    }
    EnuColor color = new EnuColor( p_registration.getOriginalColor() );
    return m_disabledFireCover[p_x][p_y][color.getColorIndex()];
  }


  public EnuColor getFireCover(AnBoardPosition p_position)
  {
    return getFireCover( p_position.getX(), p_position.getY() );
  }


  /**
   * 
   * @param p_token
   * @return
   * @deprecated
   */
  private EnuColor getFireCover(int p_x, int p_y)
  {
    EnuColor cover = new EnuColor( EnuColor.None );
    for( int iColor = 0; iColor < EnuColor.getTotalNumberOfColor(); iColor++ )
    {
      EnuColor currentColor = EnuColor.getColorFromIndex( iColor );
      if( getFireCover( p_x, p_y, currentColor ) >= 2 )
      {
        cover.addColor( currentColor );
      }
    }
    return cover;
  }

  protected byte getFireCover(int p_x, int p_y, int p_colorValue)
  {
    if( (p_colorValue < 0) || (p_colorValue >= EnuColor.getTotalNumberOfColor()) )
    {
      return 0;
    }
    return getFireCover( p_x, p_y, new EnuColor( p_colorValue ) );
  }


  protected void incFireCover(int p_x, int p_y, EnuColor p_color, byte[][][] p_fireCover)
  {
    if( (p_fireCover == null) || (p_x < 0) || (p_y < 0) || (!p_color.isSingleColor()) )
    {
      return;
    }
    if( (p_x >= p_fireCover.length) || (p_y >= p_fireCover[0].length) )
    {
      // the fire cover array is too small: enlarge it !
      byte[][][] newFireCover = new byte[Math.max( p_x + 10, p_fireCover.length )][Math.max(
          p_y + 10, p_fireCover[0].length )][EnuColor.getTotalNumberOfColor()];
      for( int ix = 0; ix < p_fireCover.length; ix++ )
      {
        for( int iy = 0; iy < p_fireCover[0].length; iy++ )
        {
          for( int ic = 0; ic < EnuColor.getTotalNumberOfColor(); ic++ )
          {
            newFireCover[ix][iy][ic] = p_fireCover[ix][iy][ic];
          }
        }
      }
      p_fireCover = newFireCover;
    }
    p_fireCover[p_x][p_y][p_color.getColorIndex()]++;

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
        color.setValue( tokenOwner.getOriginalColor() );
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
    if( (m_fireCover == null) || (!p_token.isDestroyer())
        || p_token.getLocation() != Location.Board )
    {
      m_lockedToken.remove( p_token );
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
          incFireCover( ix, iy, color, fireCover );
        }
      }
    }

    m_lockedToken.remove( p_token );
  }


  /**
   * 
   * @param p_x
   * @param p_y
   * @param p_color
   * @param p_fireCover
   * @param p_disableFireCover if true, don't search for other destroyer to fire enable
   */
  protected void decFireCover(int p_x, int p_y, EnuColor p_color, byte[][][] p_fireCover)
  {
    if( (p_fireCover == null) || (p_x < 0) || (p_y < 0) || (!p_color.isSingleColor()) )
    {
      return;
    }
    if( (p_x >= p_fireCover.length) || (p_y >= p_fireCover[0].length) )
    {
      // the fire cover array is too small: enlarge it !
      byte[][][] newFireCover = new byte[Math.max( p_x + 10, p_fireCover.length )][Math.max(
          p_y + 10, p_fireCover[0].length )][EnuColor.getTotalNumberOfColor()];
      for( int ix = 0; ix < p_fireCover.length; ix++ )
      {
        for( int iy = 0; iy < p_fireCover[0].length; iy++ )
        {
          for( int ic = 0; ic < EnuColor.getTotalNumberOfColor(); ic++ )
          {
            newFireCover[ix][iy][ic] = p_fireCover[ix][iy][ic];
          }
        }
      }
      p_fireCover = newFireCover;
    }

    p_fireCover[p_x][p_y][p_color.getColorIndex()]--;
  }

  public void decFireCover(EbToken p_token)
  {
    if( m_lockedToken.contains( p_token ) )
    {
      RpcUtil.logDebug( "token " + p_token + " is already locked !!!" );
    }
    else
    {
      m_lockedToken.add( p_token );
    }
    if( (m_fireCover == null) || (!p_token.isDestroyer())
        || p_token.getLocation() != Location.Board )
    {
      return;
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
          decFireCover( ix, iy, color, fireCover );
        }
      }
    }
  }

  /**
   * Check for a specific token if his fire disable flag isn't an error...
   * not sure where theses errors may come from...
   * In this case, it can correct flag.
   * @param p_token
   * @return true if there where an error in flag disable
   */
  public boolean checkFireDisableFlag(EbToken p_token)
  {
    if( (p_token.getLocation() != Location.Board)
        || (!p_token.canBeColored())
        || (p_token.getType() == TokenType.Freighter)
        || (p_token.getType() == TokenType.Turret) )
    {
      // all theses token can't be fire disabled
      return false;
    }
    if( m_fireCover == null )
    {
      reComputeFireCover();
    }
    EnuColor color = m_game.getOpponentFireCover( p_token );
    if( p_token.isFireDisabled() && color.getValue() == EnuColor.None )
    {
      // RpcUtil.logDebug( "token " + p_token +
      // " has an error on his fire disbale flag" );
      decFireCover( p_token );
      p_token.setFireDisabled( false );
      incFireCover( p_token );
      return true;
    }
    else if( !p_token.isFireDisabled() && color.getValue() != EnuColor.None )
    {
      // RpcUtil.logDebug( "token " + p_token +
      // " has an error on his fire disbale flag" );
      decFireCover( p_token );
      p_token.setFireDisabled( true );
      incFireCover( p_token );
      return true;
    }
    return false;
  }

// TODO 

  // EbGame a une liste de fireDisabling et chaque pion engagé dans une
  // neutralisation ont une liste aussi
  // un pion ne peut être neutralisé que par un seul couple du même proprio
  // le checkFireDisableFlag est récursive (ou en plusieurs passe) et renvoie
  // une nouvelle liste (complète ou simple mise à jour ?)
  // faire aussi une version pour tous les pions pour recompute et
  // EvtChangeTide.
  // Attention aux contrôles d'astronefs si une neutralisation implique 2
  // neutralisant de couleur différente

  public void invalidateFireCover()
  {
    m_fireCover = null;
    m_disabledFireCover = null;
    m_lockedToken.clear();
  }

  protected void reComputeFireCover()
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
      checkFireDisableFlag( token );
    }
  }


}
