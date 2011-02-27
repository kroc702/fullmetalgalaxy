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
package com.fullmetalgalaxy.model.persist.triggers.actions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;


/**
 * @author Vincent Legendre
 * This action create a message event.
 */
public class EbActChangeTide extends AnAction
{
  static final long serialVersionUID = 123;

  private Tide m_nextTide = null;


  /**
   * 
   */
  public EbActChangeTide()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbActChangeTide(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
    m_nextTide = null;
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
  public List<AnEvent> createEvents(EbGame p_game, List<Object> p_params)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    Tide nextTide = getNextTide();
    if( nextTide == null )
    {
      nextTide = Tide.getRandom();
    }
    EbEvtTide event = new EbEvtTide();
    event.setGame( p_game );
    event.setNextTide( nextTide );
    events.add( event );

    return events;
  }

  /**
   * @return the nextTide
   */
  public Tide getNextTide()
  {
    return m_nextTide;
  }

  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide(Tide p_nextTide)
  {
    m_nextTide = p_nextTide;
  }



}
