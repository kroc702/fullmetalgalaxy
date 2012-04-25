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
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.pathfinder.PathNode;


/**
 * 
 */

/**
 * @author vincent legendre
 * This class describe a position on an hexagonal board.
 */
public class AnBoardPosition extends AnPair implements PathNode
{
  static final long serialVersionUID = 16;

  @Enumerated(EnumType.ORDINAL)
  private Sector sector = Sector.North;

  public AnBoardPosition()
  {
    super();
  }

  public AnBoardPosition(AnBoardPosition p_position)
  {
    super( p_position );
    setSector( p_position.getSector() );
  }

  public AnBoardPosition(int p_x, int p_y)
  {
    super( p_x, p_y );
  }

  public AnBoardPosition(int p_x, int p_y, Sector p_sector)
  {
    super( p_x, p_y );
    sector = p_sector;
  }

  /**
   * 
   * @param p_sector
   * @return return board position of neighbour or a copy of this if p_sector is unknown
   */
  public AnBoardPosition getNeighbour(Sector p_sector)
  {
    AnBoardPosition neighbour = new AnBoardPosition( getX(), getY(), p_sector );

    switch( p_sector )
    {
    case North:
      neighbour.setY( getY() - 1 );
      break;
    case NorthEast:
      neighbour.setX( getX() + 1 );
      if( getX() % 2 == 0 )
      {
        neighbour.setY( getY() - 1 );
      }
      break;
    case SouthEast:
      neighbour.setX( getX() + 1 );
      if( getX() % 2 != 0 )
      {
        neighbour.setY( getY() + 1 );
      }
      break;
    case South:
      neighbour.setY( getY() + 1 );
      break;
    case SouthWest:
      neighbour.setX( getX() - 1 );
      if( getX() % 2 != 0 )
      {
        neighbour.setY( getY() + 1 );
      }
      break;
    case NorthWest:
      neighbour.setX( getX() - 1 );
      if( getX() % 2 == 0 )
      {
        neighbour.setY( getY() - 1 );
      }
      break;
    default:
      break;
    }
    return neighbour;
  }

  /**
   * 
   * @param p_position
   * @return the sector of a Neighbour position
   */
  public Sector getNeighbourSector(AnBoardPosition p_position)
  {
    assert p_position != null;

    Sector sector = Sector.North;
    double distance = Double.MAX_VALUE;
    double tmp = 0;

    Sector values[] = Sector.values();
    for( int i = 0; i < values.length; i++ )
    {
      tmp = p_position.getRealDistance( getNeighbour( values[i] ) );
      if( tmp < distance )
      {
        distance = tmp;
        sector = values[i];
      }
    }
    return sector;
  }

  /**
   * 
   * @param p_position
   * @return return true if the given position is a neighbour hexagon
   */
  public boolean isNeighbor(AnBoardPosition p_position)
  {
    double distance = getRealDistance( p_position );
    return (distance < 1.1) && (distance > 0.9);
  }

  /**
   * @return la distance r�el (pas en nb de case) entre les coordon�es 'ix' et 'iy'
   */
  public double getRealDistance(AnBoardPosition p_position)
  {
    double fx = (p_position.getX() - getX()) * 0.8660254037844386;
    double fy = p_position.getY() - getY();
    if( p_position.getX() % 2 != 0 )
      fy += 0.5;
    if( getX() % 2 != 0 )
      fy -= 0.5;
    return Math.sqrt( fx * fx + fy * fy );
  }

  public int getHexDistance(AnBoardPosition p_position)
  {
    int dy = p_position.getY() - getY();
    int dx = Math.abs( p_position.getX() - getX() );
    int a = dx / 2;
    if( ((getX() % 2 != 0) && (p_position.getX() % 2 == 0) && (dy > 0))
        || ((getX() % 2 == 0) && (p_position.getX() % 2 != 0) && (dy < 0)) )
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
   * @return the sector
   */
  public Sector getSector()
  {
    return sector;
  }

  /**
   * @param p_sector the sector to set
   */
  public void setSector(Sector p_sector)
  {
    sector = p_sector;
  }

  /**
   * for debugging purpose only
   */
  @Override
  public String toString()
  {
    String str = "[";
    str += Integer.toString( getX() );
    str += ",";
    str += Integer.toString( getY() );
    str += ";";
    str += getSector().toString();
    str += "]";
    return str;
  }

  public AnBoardPosition newInstance()
  {
    return new AnBoardPosition( this );
  }

  /**
   * create a list of board position that draw an hexagon with a given center position and radius 
   * @param p_center
   * @param p_radius
   * @return
   */
  public static List<AnBoardPosition> drawHexagon(AnBoardPosition p_center, int p_radius)
  {
    List<AnBoardPosition> list = new ArrayList<AnBoardPosition>();
    p_center = p_center.newInstance();
    p_center.setY( p_center.getY() - p_radius );
    // add every side of hexagon one by one
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.SouthWest );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.South );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.SouthEast );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.NorthEast );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.North );
      list.add( p_center );
    }
    for( int i = 0; i < p_radius; i++ )
    {
      p_center = p_center.getNeighbour( Sector.NorthWest );
      list.add( p_center );
    }
    return list;
  }

}
