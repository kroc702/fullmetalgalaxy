/**
 * 
 */
package com.fullmetalgalaxy.model.pathfinder;

import java.util.List;

/**
 * @author Vincent Legendre
 *
 */
public interface PathFinder
{
  /**
   * 
   * @param p_fromNode
   * @param p_toNode
   * @param p_mobile may be null
   * @return
   */
  public List<com.fullmetalgalaxy.model.pathfinder.PathNode> findPath(PathNode p_fromNode, PathNode p_toNode,
      PathMobile p_mobile);

  public List<com.fullmetalgalaxy.model.pathfinder.PathNode> findPath(PathNode p_fromNode, PathNode p_toNode);
}
