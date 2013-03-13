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
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 *
 */
public class EbAdmin extends AnEventUser
{
  static final long serialVersionUID = 1;

  private String m_message = null;

  /**
   * 
   */
  public EbAdmin()
  {
    super();
    init();
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_message = null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    throw new RpcFmpException( "les actions d'administrations ne peuvent être défaite" );
  }

  // Bean getter / setter
  // ====================
  /**
   * if message start with './', '/' or 'http://', message is a web page url
   * @return the message
   */
  public String getMessage()
  {
    return m_message;
  }

  /**
   * @param p_message the message to set
   */
  public void setMessage(String p_message)
  {
    m_message = p_message;
  }

}
