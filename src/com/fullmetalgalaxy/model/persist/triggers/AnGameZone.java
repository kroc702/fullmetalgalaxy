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
package com.fullmetalgalaxy.model.persist.triggers;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbEmbedBase;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 * represent a game zone: graveyard, orbit, or a board zone.
 */
public class AnGameZone extends EbEmbedBase
{
  static final long serialVersionUID = 56;

  /**
   * represent main location
   */
  private Location m_location = Location.Board;
  /**
   * in case location is Board
   */
  private int m_top = -1;
  private int m_left = -1;
  private int m_bottom = -1;
  private int m_right = -1;
  /**
   * in case location is token
   */
  private EbToken m_carrierToken = null;

  /**
   * 
   */
  public AnGameZone()
  {
    // TODO Auto-generated constructor stub
  }

  public boolean contain(EbToken p_token)
  {
    if( p_token.getLocation() != getLocation() )
    {
      return false;
    }
    if( getLocation() == Location.Board )
    {
      AnBoardPosition position = p_token.getPosition();
      if( position.getX() < getLeft() || position.getX() > getRight() || position.getY() < getTop()
          || position.getY() > getBottom() )
      {
        return false;
      }
    }
    else if( getLocation() == Location.Token )
    {
      if( p_token.getCarrierToken() != getCarrierToken() )
      {
        return false;
      }
    }
    return true;
  }

  // getters / setters
  // =================

  /**
   * @return the location
   */
  public Location getLocation()
  {
    return m_location;
  }

  /**
   * @param p_location the location to set
   */
  public void setLocation(Location p_location)
  {
    m_location = p_location;
  }


  /**
   * @return the token
   */
  public EbToken getCarrierToken()
  {
    return m_carrierToken;
  }

  /**
   * @param p_token the token to set
   */
  public void setCarrierToken(EbToken p_token)
  {
    m_carrierToken = p_token;
  }

  /**
   * @return the top
   */
  public int getTop()
  {
    return m_top;
  }

  /**
   * @param p_top the top to set
   */
  public void setTop(int p_top)
  {
    m_top = p_top;
  }

  /**
   * @return the left
   */
  public int getLeft()
  {
    return m_left;
  }

  /**
   * @param p_left the left to set
   */
  public void setLeft(int p_left)
  {
    m_left = p_left;
  }

  /**
   * @return the bottom
   */
  public int getBottom()
  {
    return m_bottom;
  }

  /**
   * @param p_bottom the bottom to set
   */
  public void setBottom(int p_bottom)
  {
    m_bottom = p_bottom;
  }

  /**
   * @return the right
   */
  public int getRight()
  {
    return m_right;
  }

  /**
   * @param p_right the right to set
   */
  public void setRight(int p_right)
  {
    m_right = p_right;
  }



}
