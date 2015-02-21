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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author Legendre Vincent
 * This algo is inspired from AStar algorithm in much simpler.
 */
public class SimplePathFinder implements PathFinder
{
  protected PathGraph m_pathGraph = null;

  protected Stack<com.fullmetalgalaxy.model.pathfinder.PathNode> m_path = null;

  protected Set<com.fullmetalgalaxy.model.pathfinder.PathNode> m_closedPath = null;

  public SimplePathFinder(PathGraph p_pathGraph)
  {
    m_pathGraph = p_pathGraph;
  }

  @Override
  public List<com.fullmetalgalaxy.model.pathfinder.PathNode> findPath(PathNode p_fromNode, PathNode p_toNode)
  {
    return findPath( p_fromNode, p_toNode, null );
  }


  /**
   * return a list of node which represent the shortest path between the two given node.
   * first element is p_fromNode, last element is p_toNode.
   * @param p_fromNode
   * @param p_toNode
   * @return empty if no path was found.
   */
  @Override
  public List<com.fullmetalgalaxy.model.pathfinder.PathNode> findPath(PathNode p_fromNode, PathNode p_toNode,
      PathMobile p_mobile)
  {
    // assert p_fromNode != null;
    // assert p_toNode != null;
    m_path = new Stack<com.fullmetalgalaxy.model.pathfinder.PathNode>();
    m_closedPath = new HashSet<com.fullmetalgalaxy.model.pathfinder.PathNode>();
    m_path.push( p_fromNode );
    PathNode nextNode = getBestNextNode( p_toNode, p_mobile );
    while( !p_fromNode.equals( nextNode ) && !p_toNode.equals( nextNode ) )
    {
      if( nextNode.equals( m_path.peek() ) )
      {
        // no best path was found
        m_closedPath.add( m_path.pop() );
      }
      else
      {
        m_path.push( nextNode );
      }
      nextNode = getBestNextNode( p_toNode, p_mobile );
    }
    if( p_toNode.equals( nextNode ) )
    {
      // a good path was found
      m_path.push( p_toNode );
    }
    else
    {
      // no good path was found
      m_path.clear();
    }
    return m_path;
  }

  /**
   * 
   * @return current node if no best next node is found.
   */
  protected PathNode getBestNextNode(PathNode p_toNode, PathMobile p_mobile)
  {
    PathNode fromNode = (PathNode)m_path.peek();
    // assert fromNode != null;
    Set<com.fullmetalgalaxy.model.pathfinder.PathNode> nodes = m_pathGraph.getAvailableNode( fromNode,
        p_mobile );
    PathNode bestNode = fromNode;
    float bestHeuristic = m_pathGraph.heuristic( bestNode, p_toNode, p_mobile );
    for( Iterator<com.fullmetalgalaxy.model.pathfinder.PathNode> it = nodes.iterator(); it.hasNext(); )
    {
      PathNode pathNode = (PathNode)it.next();
      float heuristic = m_pathGraph.heuristic( pathNode, p_toNode, p_mobile );
      if( (heuristic < bestHeuristic) && (!m_closedPath.contains( pathNode )) )
      {
        bestNode = pathNode;
        bestHeuristic = heuristic;
      }
    }
    return bestNode;
  }
}
