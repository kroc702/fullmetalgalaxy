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
package com.fullmetalgalaxy.client.event;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author vlegendr
 *
 * this event represent any {@link GameEngine} update: this is the legacy event. 
 * (ie the only existing event before the use of eventbus)
 */
public class GameActionEvent extends GwtEvent<GameActionEvent.Handler>
{
  /**
   * Implemented by objects that handle {@link GameActionEvent}.
   */
  public interface Handler extends EventHandler {
    public void onGameEvent(AnEvent p_message);
  }

  /**
   * The event type.
   */
  public static Type<GameActionEvent.Handler> TYPE = new Type<GameActionEvent.Handler>();
  
  private AnEvent m_modelSender = null;
  
  public GameActionEvent(AnEvent p_modelSender)
  {
    m_modelSender = p_modelSender;
  }
  
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<GameActionEvent.Handler> getAssociatedType()
  {
    return TYPE;
  }

  @Override
  protected void dispatch(GameActionEvent.Handler p_handler)
  {
    p_handler.onGameEvent( m_modelSender );
    
  }


}
