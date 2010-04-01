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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.io.Serializable;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Kroc
 *
 */
public class TokenExtraLocation implements Serializable
{
  static final long serialVersionUID = 16;

  private AnBoardPosition m_position = new AnBoardPosition();
  private EbToken m_token = null;


  /**
   * @return the position
   */
  public AnBoardPosition getPosition()
  {
    return m_position;
  }

  /**
   * @param p_position the position to set
   */
  public void setPosition(AnBoardPosition p_position)
  {
    m_position = p_position;
  }

  /**
   * @return the token
   */
  public EbToken getToken()
  {
    return m_token;
  }

  /**
   * @param p_token the token to set
   */
  public void setToken(EbToken p_token)
  {

    m_token = p_token;
  }

}
