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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;






/**
 * An entity bean pair class mapped onto x,y
 *
 * 
 */
public class AnPair extends EbEmbedBase
{
  static final long serialVersionUID = 21;

  // these two properties don't have the 'm_' prefix because hibernate has some
  // trouble to find getter while a superclass is mapped as CollectionOfElements

  private int x = -1;
  private int y = -1;


  /**
   */
  public AnPair()
  {
    super();
  }

  /**
   * @param p_anPair
   */
  public AnPair(AnPair p_anPair)
  {
    super();
    x = p_anPair.getX();
    y = p_anPair.getY();
  }

  /**
   * @param p_x
   * @param p_y
   */
  public AnPair(int p_x, int p_y)
  {
    super();
    x = p_x;
    y = p_y;
  }

  public void setXY(int p_x, int p_y)
  {
    setX( p_x );
    setY( p_y );
  }

  /**
   * @return the x
   */
  public int getX()
  {
    return x;
  }

  /**
   * @param p_x the x to set
   */
  public void setX(int p_x)
  {
    x = p_x;
  }

  /**
   * @return the y
   */
  public int getY()
  {
    return y;
  }

  /**
   * @param p_y the y to set
   */
  public void setY(int p_y)
  {
    y = p_y;
  }

  public boolean equals(AnPair other)
  {
    if( this == other )
      return true;
    if( other == null )
      return false;
    if( x == other.x && y == other.y )
      return true;
    else
      return false;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    AnPair other = null;
    try
    {
      other = (AnPair)obj;
    } catch( Throwable th )
    {
      return super.equals( obj );
    }
    return equals( other );
  }

}
