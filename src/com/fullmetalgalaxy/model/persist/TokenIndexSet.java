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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.fullmetalgalaxy.model.Location;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 *
 */
public class TokenIndexSet implements IsSerializable
{
  static final long serialVersionUID = 224;

  private Set<EbToken> m_tokenSet = null;

  private Map<AnBoardPosition, Set<EbToken>> m_positionIndex = null;
  private Map<Long, EbToken> m_idIndex = null;


  public TokenIndexSet()
  {
    super();
  }

  /**
   * 
   */
  public TokenIndexSet(Set<EbToken> p_set)
  {
    m_tokenSet = p_set;
    rebuildIndex();
  }

  protected void setTokenSet(Set<EbToken> p_tokenSet)
  {
    m_tokenSet = p_tokenSet;
    rebuildIndex();
  }

  private void rebuildIndex()
  {
    assert m_tokenSet != null;
    m_positionIndex = new HashMap<AnBoardPosition, Set<EbToken>>();
    m_idIndex = new HashMap<Long, EbToken>();
    for( EbToken token : m_tokenSet )
    {
      addToken( token );
    }
  }

  public Set<EbToken> getAllToken(AnBoardPosition p_position)
  {
	assert m_positionIndex != null;
    return m_positionIndex.get( p_position );
  }

  public EbToken getToken(long p_id)
  {
	assert m_idIndex != null;  
    return m_idIndex.get( p_id );
  }

  public void setPosition(EbToken p_token, Location p_location)
  {
    assert p_location != Location.Board;
    if( m_positionIndex != null && p_token.getLocation() == Location.Board )
    {
      m_positionIndex.get( p_token.getPosition() ).remove( p_token );
      for( AnBoardPosition position : p_token.getExtraPositions() )
      {
        m_positionIndex.get( position ).remove( p_token );
      }
    }
    p_token.setLocation( p_location );
    // p_token.setPosition( new AnBoardPosition( -1, -1 ) );
  }

  public void setPosition(EbToken p_token, AnBoardPosition p_position)
  {
    if( m_positionIndex != null && p_token.getLocation() == Location.Board )
    {
      m_positionIndex.get( p_token.getPosition() ).remove( p_token );
      for( AnBoardPosition position : p_token.getExtraPositions() )
      {
        m_positionIndex.get( position ).remove( p_token );
      }
    }
    p_token.setPosition( p_position );
    p_token.setLocation( Location.Board );
    if( m_positionIndex != null )
    {
      addTokenPosition( p_token, p_token.getPosition() );
      for( AnBoardPosition position : p_token.getExtraPositions() )
      {
        addTokenPosition( p_token, position );
      }
    }
  }

  /**
   * add this token to created indexes.
   * @param p_token
   */
  public void addToken(EbToken p_token)
  {
    assert m_positionIndex != null;
    assert m_idIndex != null;
    m_idIndex.put( p_token.getId(), p_token );
    if( p_token.getLocation() != Location.Board )
    {
      return;
    }
    addTokenPosition( p_token, p_token.getPosition() );
    for( AnBoardPosition position : p_token.getExtraPositions() )
    {
      addTokenPosition( p_token, position );
    }
  }

  protected void addTokenPosition(EbToken p_token, AnBoardPosition p_position)
  {
    if( m_positionIndex.get( p_position ) == null )
    {
      m_positionIndex.put( p_position, new HashSet<EbToken>() );
    }
    m_positionIndex.get( p_position ).add( p_token );
  }
}
