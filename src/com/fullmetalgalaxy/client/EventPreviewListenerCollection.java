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
package com.fullmetalgalaxy.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * @author Vincent Legendre
 * A helper class for implementers of the SourcesPreviewEvents interface. This subclass of ArrayList assumes that
 * all objects added to it will be of type EventPreview.
 */

public class EventPreviewListenerCollection extends ArrayList<NativePreviewHandler>
{
  static final long serialVersionUID = 15;


  /**
   * 
   */
  public EventPreviewListenerCollection()
  {
  }


  public void fireEventPreview(NativePreviewEvent p_event)
  {
    NativePreviewHandler listener = null;
    for( Iterator<NativePreviewHandler> it = iterator(); it.hasNext(); )
    {
      listener = (NativePreviewHandler)it.next();
      try
      {
        listener.onPreviewNativeEvent( p_event );
      } catch( Exception e )
      {
        RpcUtil.logError( "the listener " + listener.toString()
            + " bug while notify a modefireEventPreview", e );
      }
    }
  }


}
