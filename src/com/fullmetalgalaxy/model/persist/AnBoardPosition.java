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
package com.fullmetalgalaxy.model.persist;

import com.fullmetalgalaxy.model.Coordinate;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.pathfinder.PathNode;


/**
 * 
 */

/**
 * @author vincent legendre
 * This class describe a position on an hexagonal board.
 */
public class AnBoardPosition extends AnPair implements PathNode, Coordinate
{
  static final long serialVersionUID = 16;

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



}
