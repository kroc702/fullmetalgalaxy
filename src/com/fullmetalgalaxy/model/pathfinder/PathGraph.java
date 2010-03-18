/**
 * 
 */
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
