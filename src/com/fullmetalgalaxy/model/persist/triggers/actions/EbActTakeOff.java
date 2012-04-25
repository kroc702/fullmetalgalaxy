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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.triggers.actions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;


/**
 * @author Vincent Legendre
 * This action create a message event.
 */
public class EbActTakeOff extends AnAction
{
  static final long serialVersionUID = 123;



  /**
   * 
   */
  public EbActTakeOff()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbActTakeOff(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
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
    EbToken freighter = null;
    if( (p_params.size() >= 1) && (p_params.get( 0 ) instanceof EbToken) )
    {
      freighter = (EbToken)p_params.get( 0 );
      if( (freighter.getType() == TokenType.Freighter)
          && (freighter.getLocation() == Location.Board) )
      {
        EbEvtTakeOff event = new EbEvtTakeOff();
        event.setGame( p_game );
        event.setCost( 0 );
        event.setToken( freighter );
        event.setBackInOrbit( true );
        event.setRegistration( null );
        event.setAuto( true );
        events.add( event );
      }
    }
    return events;
  }



}
