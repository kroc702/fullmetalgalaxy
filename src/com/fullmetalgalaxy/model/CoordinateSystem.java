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

import java.util.Collection;

/**
 * @author Kroc
 *  
 * A coordinate system have several utility method to make computation
 * on coordinate.
 * several coordinate system may be implemented in future.
 * 
 * TODO Sector should be an abstract interface instead of this 6 values enum
 */
public interface CoordinateSystem<C extends Coordinate>
{

  /**
   * @return return coordinate of all direct neighbor coordinate
   */
  public Collection<C> getAllNeighbors(C p_origin);

  /**
   * @return return coordinate of direct neighbor or a copy of this if p_sector is unknown
   */
  public C getNeighbor(C p_origin, Sector p_sector);

  /**
   * @return the sector of a close position (it doesn't have to be direct neighbor)
   */
  public Sector getSector(C p_origin, C p_to);

  /**
   * This method have less meaning if coordinate are continuous instead of discrete.
   * @param p_position
   * @return return true if the given position is a direct neighbor.
   */
  public boolean areNeighbor(C p_A, C p_B);

  /**
   * @return real distance as if we measure it with a ruler. units are still boxes or hex 
   */
  public double getStraightDistance(C p_A, C p_B);

  /**
   * @return distance as integer value. It may differ from straight distance if
   * coordinate system don't allow Straight movement due to map discretization
   */
  public int getDiscreteDistance(C p_A, C p_B);

  /**
   * if position is outside of map AND map shape is border less, center position on map.
   * this method is allowed to modify input coordinate object
   * @param p_position that may have huge or negative value
   * @return smallest positive value that represent the same position
   */
  public C normalizePosition(C p_position);

}
