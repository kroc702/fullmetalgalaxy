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
package com.fullmetalgalaxy.model.persist.gamelog;



/**
 * @author Vincent Legendre
 *
 */
public class EbEvtMessage extends AnEvent
{
  static final long serialVersionUID = 1;

  private String m_message = null;
  private String m_title = null;

  /**
   * 
   */
  public EbEvtMessage()
  {
    super();
    this.init();
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
    m_title = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtMessage;
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

  /**
   * @return the title
   */
  public String getTitle()
  {
    return m_title;
  }

  /**
   * @param p_title the title to set
   */
  public void setTitle(String p_title)
  {
    m_title = p_title;
  }



}
