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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.pathfinder;

import java.util.Set;

/**
 * @author Legendre Vincent
 * represent the graph for Astar algorithm.
 */
public interface PathGraph
{
  /**
   * an heuristic about the 'goodness' of current node.
   * it's usually the distance between the two nodes.
   * @param p_fromNode
   * @param p_toNode
   * @param p_mobile may be null
   * @return the lower it is, the better is p_currentNode
   */
  public float heuristic(PathNode p_fromNode, PathNode p_toNode, PathMobile p_mobile);

  /**
   * For any given node, it return a list of all possible nodes the algo can search.
   * @param p_fromNode
   * @param p_mobile may be null
   * @return a Set of Node
   */
  public Set<com.fullmetalgalaxy.model.pathfinder.PathNode> getAvailableNode(PathNode p_fromNode,
      PathMobile p_mobile);
}
