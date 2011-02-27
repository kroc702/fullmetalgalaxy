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
package com.fullmetalgalaxy.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author vincent
 *
 */
public class ConnectedUser implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 203;

  private long m_id = 0;
  private String m_pseudo = "";
  private Date m_lastConnexion = new Date( System.currentTimeMillis() );
  private Date m_endTurnDate = null;

  public ConnectedUser()
  {
  }

  public ConnectedUser(String p_pseudo, Date p_endTurn)
  {
    m_pseudo = p_pseudo;
    m_endTurnDate = p_endTurn;
  }

  public String getPseudo()
  {
    return m_pseudo;
  }

  public void setPseudo(String p_pseudo)
  {
    m_pseudo = p_pseudo;
  }

  public Date getEndTurnDate()
  {
    return m_endTurnDate;
  }

  public void setEndTurnDate(Date p_endTurnDate)
  {
    m_endTurnDate = p_endTurnDate;
  }

  public Date getLastConnexion()
  {
    return m_lastConnexion;
  }

  public void setLastConnexion(Date p_lastConnexion)
  {
    m_lastConnexion = p_lastConnexion;
  }

  /**
   * @return the id
   */
  public long getId()
  {
    return m_id;
  }

  /**
   * @param p_id the id to set
   */
  public void setId(long p_id)
  {
    m_id = p_id;
  }


}
