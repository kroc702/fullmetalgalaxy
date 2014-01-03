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
package com.fullmetalgalaxy.model.persist.triggers.actions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;


/**
 * @author Vincent Legendre
 * This action create a message event.
 */
public class EbActMessage extends AnAction
{
  static final long serialVersionUID = 123;

  private String m_message = "";


  /**
   * 
   */
  public EbActMessage()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbActMessage(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
    m_message = "";
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.actions.AnAction#exec(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public List<AnEvent> createEvents(Game p_game, List<Object> p_params)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    if( !getMessage().trim().equals( "" ) )
    {
      EbEvtMessage msg = new EbEvtMessage();
      msg.setAuto( true );
      msg.setGame( p_game );
      msg.setMessage( getMessage() );
      events.add( msg );
    }
    return events;
  }

  /**
   * if message start with './' or 'http://', message is a web page url
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
