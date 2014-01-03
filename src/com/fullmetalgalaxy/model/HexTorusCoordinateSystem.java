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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;

/**
 * @author Kroc
 *
 */
public class HexTorusCoordinateSystem extends HexCoordinateSystem
{
  private MapShape m_mapShape = MapShape.Flat;
  private int m_width = Integer.MAX_VALUE;
  private int m_height = Integer.MAX_VALUE;
  
  public HexTorusCoordinateSystem(MapShape p_mapShape, int p_width, int p_height )
  {
    super();
    if( p_mapShape != null )
    {
      m_mapShape = p_mapShape;
    }
    m_width = p_width;
    m_height = p_height;
  }
  
  /**
   * if position is outside of map AND map shape is border less, center position on map.
   * @param p_position
   * @return
   */
  @Override
  public AnBoardPosition normalizePosition(AnBoardPosition p_position)
  {
    if( m_mapShape.isEWLinked() )
    {
      if( p_position.getX() >= m_width ) p_position.setX( p_position.getX() - m_width );
      if( p_position.getX() < 0 ) p_position.setX( p_position.getX() + m_width );
    }
    if( m_mapShape.isNSLinked() )
    {
      if( p_position.getY() >= m_height ) p_position.setY( p_position.getY() - m_height );
      if( p_position.getY() < 0 ) p_position.setY( p_position.getY() + m_height );
    }
    return p_position;
  }
  
  /**
   * build a new B position that is similar (but not normalized as above method)
   * but as close as possible from A
   * @param p_A
   * @param p_B
   * @return
   */
  private AnBoardPosition getClosestB(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    p_B = new AnBoardPosition( p_B );
    if( m_mapShape.isEWLinked() )
    {
      if( Math.abs( p_B.getX() + m_width - p_A.getX() ) < Math.abs( p_B.getX() - p_A.getX() ) )
      {
        p_B.setX( p_B.getX() + m_width );
      } else if( Math.abs( p_B.getX() - m_width - p_A.getX() ) < Math.abs( p_B.getX() - p_A.getX() ) )
      {
        p_B.setX( p_B.getX() - m_width );
      } 
    }
    if( m_mapShape.isNSLinked() )
    {
      if( Math.abs( p_B.getY() + m_height - p_A.getY() ) < Math.abs( p_B.getY() - p_A.getY() ) )
      {
        p_B.setY( p_B.getY() + m_height );
      } else if( Math.abs( p_B.getY() - m_height - p_A.getY() ) < Math.abs( p_B.getY() - p_A.getY() ) )
      {
        p_B.setY( p_B.getY() - m_height );
      } 
    }
    return p_B;
  }

  @Override
  public Collection<AnBoardPosition> getAllNeighbors(AnBoardPosition p_origin)
  {
    Collection<AnBoardPosition> neighbors = new HashSet<AnBoardPosition>();
    for( Sector sector : Sector.values() )
    {
      // TODO remove hex outside of flat map
      neighbors.add( normalizePosition(getNeighbor(p_origin, sector)) );
    }
    return neighbors;
  }

  @Override
  public AnBoardPosition getNeighbor(AnBoardPosition p_origin, Sector p_sector)
  {
    return normalizePosition( super.getNeighbor( p_origin, p_sector ));
  }

  @Override
  public Sector getSector(AnBoardPosition p_origin, AnBoardPosition p_to)
  {
    return super.getSector(p_origin, getClosestB(p_origin,p_to));
  }

  @Override
  public boolean areNeighbor(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    return super.areNeighbor(p_A, getClosestB(p_A,p_B));
  }

  @Override
  public double getStraightDistance(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    return super.getStraightDistance(p_A, getClosestB(p_A,p_B));
  }

  @Override
  public int getDiscreteDistance(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    return super.getDiscreteDistance(p_A, getClosestB(p_A,p_B));
  }
  

  /**
   * create a list of board position that draw an hexagon with a given center position and radius 
   * @param p_center
   * @param p_radius
   * @return
   */
  @Override
  public List<AnBoardPosition> drawHexagon(AnBoardPosition p_center, int p_radius)
  {
    List<AnBoardPosition> listTmp = super.drawHexagon(p_center, p_radius);
    List<AnBoardPosition> list = new ArrayList<AnBoardPosition>();
    for( AnBoardPosition position : listTmp )
    {
      list.add( normalizePosition( position ) );
    }
    return list;
  }
}
