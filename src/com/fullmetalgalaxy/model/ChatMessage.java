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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 * If a ChatMessage is empty, it's only a keep alive message.
 */
public class ChatMessage implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 201;

  private long m_gameId = 0;

  private String m_text = "";

  private String m_fromPseudo = "";
  private int m_fromPageId = 0;

  private Date m_date = new Date( SharedMethods.currentTimeMillis() );

  public ChatMessage()
  {

  }

  public ChatMessage(long p_gameId, String p_from, String p_text)
  {
    m_gameId = p_gameId;
    m_fromPseudo = p_from;
    m_text = p_text;
  }

  public boolean isEmpty()
  {
    return m_text == null || m_text.isEmpty();
  }
  
  /**
   * @return the gameId
   */
  public long getGameId()
  {
    return m_gameId;
  }

  /**
   * @param p_gameId the gameId to set
   */
  public void setGameId(long p_gameId)
  {
    m_gameId = p_gameId;
  }

  /**
   * @return the text
   */
  public String getText()
  {
    return m_text;
  }

  /**
   * @param p_text the text to set
   */
  public void setText(String p_text)
  {
    m_text = p_text;
  }

  /**
   * @return the fromLogin
   */
  public String getFromPseudo()
  {
    return m_fromPseudo;
  }

  /**
   * @param p_fromLogin the fromLogin to set
   */
  public void setFromPseudo(String p_fromLogin)
  {
    m_fromPseudo = p_fromLogin;
  }

  /**
   * @return the date
   */
  public Date getDate()
  {
    return m_date;
  }

  /**
   * @param p_date the date to set
   */
  public void setDate(Date p_date)
  {
    m_date = p_date;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    // return "[" + getDate().getHours() + ":" + getDate().getMinutes() + "." +
    // getDate().getSeconds()
    // + "] " + getFromLogin() + ": " + getText();
    return getFromPseudo() + ": " + getText();
  }

  /**
   * @return the fromPageId
   */
  public int getFromPageId()
  {
    return m_fromPageId;
  }

  /**
   * @param p_fromPageId the fromPageId to set
   */
  public void setFromPageId(int p_fromPageId)
  {
    m_fromPageId = p_fromPageId;
  }

}
