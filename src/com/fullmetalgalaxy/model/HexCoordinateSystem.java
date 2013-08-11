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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
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
public class HexCoordinateSystem implements CoordinateSystem<AnBoardPosition>
{

  @Override
  public Collection<AnBoardPosition> getAllNeighbors(AnBoardPosition p_origin)
  {
    Collection<AnBoardPosition> neighbors = new HashSet<AnBoardPosition>();
    for( Sector sector : Sector.values() )
    {
      // TODO remove hex outside of flat map
      neighbors.add( getNeighbor(p_origin, sector) );
    }
    return neighbors;
  }

  @Override
  public AnBoardPosition getNeighbor(AnBoardPosition p_origin, Sector p_sector)
  {
    assert p_origin != null;
    assert p_sector != null;
    
    AnBoardPosition neighbour = new AnBoardPosition( p_origin.getX(), p_origin.getY(), p_sector );

    switch( p_sector )
    {
    case North:
      neighbour.setY( p_origin.getY() - 1 );
      break;
    case NorthEast:
      neighbour.setX( p_origin.getX() + 1 );
      if( p_origin.getX() % 2 == 0 )
      {
        neighbour.setY( p_origin.getY() - 1 );
      }
      break;
    case SouthEast:
      neighbour.setX( p_origin.getX() + 1 );
      if( p_origin.getX() % 2 != 0 )
      {
        neighbour.setY( p_origin.getY() + 1 );
      }
      break;
    case South:
      neighbour.setY( p_origin.getY() + 1 );
      break;
    case SouthWest:
      neighbour.setX( p_origin.getX() - 1 );
      if( p_origin.getX() % 2 != 0 )
      {
        neighbour.setY( p_origin.getY() + 1 );
      }
      break;
    case NorthWest:
      neighbour.setX( p_origin.getX() - 1 );
      if( p_origin.getX() % 2 == 0 )
      {
        neighbour.setY( p_origin.getY() - 1 );
      }
      break;
    default:
      break;
    }
    return neighbour;
  }

  @Override
  public Sector getSector(AnBoardPosition p_origin, AnBoardPosition p_to)
  {
    assert p_origin != null;
    assert p_to != null;

    Sector sector = Sector.North;
    double distance = Double.MAX_VALUE;
    double tmp = 0;

    Sector values[] = Sector.values();
    for( int i = 0; i < values.length; i++ )
    {
      tmp = getStraightDistance( p_to, getNeighbor( p_origin, values[i] ) );
      if( tmp < distance )
      {
        distance = tmp;
        sector = values[i];
      }
    }
    return sector;
  }

  @Override
  public boolean areNeighbor(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    double distance = getStraightDistance( p_A, p_B );
    return (distance < 1.1) && (distance > 0.9);
  }

  @Override
  public double getStraightDistance(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    double fx = (p_B.getX() - p_A.getX()) * 0.8660254037844386;
    double fy = p_B.getY() - p_A.getY();
    if( p_B.getX() % 2 != 0 )
      fy += 0.5;
    if( p_A.getX() % 2 != 0 )
      fy -= 0.5;
    return Math.sqrt( fx * fx + fy * fy );
  }

  @Override
  public int getDiscreteDistance(AnBoardPosition p_A, AnBoardPosition p_B)
  {
    int dy = p_B.getY() - p_A.getY();
    int dx = Math.abs( p_B.getX() - p_A.getX() );
    int a = dx / 2;
    if( ((p_A.getX() % 2 != 0) && (p_B.getX() % 2 == 0) && (dy > 0))
        || ((p_A.getX() % 2 == 0) && (p_B.getX() % 2 != 0) && (dy < 0)) )
    {
      a++;
    }
    dy = Math.abs( dy ) - a;
    if( dy < 0 )
    {
      dy = 0;
    }
    return dx + dy;
  }
  

  /**
   * create a list of board position that draw an hexagon with a given center position and radius 
   * @param p_center
   * @param p_radius
   * @return
   */
  public List<AnBoardPosition> drawHexagon(AnBoardPosition p_center, int p_radius)
  {
    List<AnBoardPosition> list = new ArrayList<AnBoardPosition>();
    p_center = p_center.newInstance();
    p_center.setY( p_center.getY() - p_radius );
    // add every side of hexagon one by one
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.SouthWest );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.South );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.SouthEast );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.NorthEast );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.North );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = getNeighbor( p_center, Sector.NorthWest );
      list.add( p_center );
    }
    return list;
  }
  
  @Override
  public AnBoardPosition normalizePosition(AnBoardPosition p_position)
  {
    return p_position;
  }

}
