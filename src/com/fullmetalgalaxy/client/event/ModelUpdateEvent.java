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

import com.fullmetalgalaxy.client.game.GameEngine;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author vlegendr
 *
 * this event represent any {@link GameEngine} update: this is the legacy event. 
 * (ie the only existing event before the use of eventbus)
 */
public class ModelUpdateEvent extends GwtEvent<ModelUpdateEvent.Handler>
{
  /**
   * Implemented by objects that handle {@link ModelUpdateEvent}.
   */
  public interface Handler extends EventHandler {
    public void onModelUpdate(GameEngine p_modelSender);
  }

  /**
   * The event type.
   */
  public static Type<ModelUpdateEvent.Handler> TYPE = new Type<ModelUpdateEvent.Handler>();
  
  private GameEngine m_modelSender = null;
  
  public ModelUpdateEvent(GameEngine p_modelSender)
  {
    m_modelSender = p_modelSender;
  }
  
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<ModelUpdateEvent.Handler> getAssociatedType()
  {
    return TYPE;
  }

  @Override
  protected void dispatch(ModelUpdateEvent.Handler p_handler)
  {
    p_handler.onModelUpdate( m_modelSender );
    
  }


}
