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
package com.fullmetalgalaxy.client.event;

import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author vlegendr
 *
 * 
 */
public class GameLoadEvent extends GwtEvent<GameLoadEvent.Handler>
{
  /**
   * Implemented by objects that handle {@link GameLoadEvent}.
   */
  public interface Handler extends EventHandler {
    public void onGameLoad(Game p_game);
  }

  /**
   * The event type.
   */
  public static Type<GameLoadEvent.Handler> TYPE = new Type<GameLoadEvent.Handler>();
  
  private Game m_game = null;
  
  public GameLoadEvent(Game p_game)
  {
    m_game = p_game;
  }
  
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<GameLoadEvent.Handler> getAssociatedType()
  {
    return TYPE;
  }

  @Override
  protected void dispatch(GameLoadEvent.Handler p_handler)
  {
    if( m_game != null )
    {
      p_handler.onGameLoad( m_game );
    }
    
  }


}
