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
package com.fullmetalgalaxy.model;

import java.io.Serializable;
import java.util.Date;


/**
 * @author Vincent Legendre
 * This class is the base class of a model. 
 */
public class ModelBase implements Serializable
{
  static final long serialVersionUID = 22;

  /**
   * Date and time of the last update.
   * this is the last server update as a server date. don't compare this date to a client date !
   */
  protected Date m_lastUpdate = new Date( 0 );

  /**
   * 
   */
  public ModelBase()
  {
  }

  /**
   * @return the lastUpdate as a client date.
   * this date is only for widget: do not request an update to the server with this date !
   */
  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  /**
   * @param p_lastUpdate the lastUpdate to set
   */
  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }


}
