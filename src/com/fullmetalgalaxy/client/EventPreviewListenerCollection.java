/**
 * 
 */
package com.fullmetalgalaxy.client;

import java.util.ArrayList;
import java.util.Iterator;


import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;

/**
 * @author Vincent Legendre
 * A helper class for implementers of the SourcesPreviewEvents interface. This subclass of ArrayList assumes that
 * all objects added to it will be of type EventPreview.
 */
public class EventPreviewListenerCollection extends ArrayList<EventPreview>
{
  static final long serialVersionUID = 15;


  /**
   * 
   */
  public EventPreviewListenerCollection()
  {
  }


  public boolean fireEventPreview(Event p_event)
  {
    EventPreview listener = null;
    for( Iterator<EventPreview> it = iterator(); it.hasNext(); )
    {
      listener = (EventPreview)it.next();
      try
      {
        if( listener.onEventPreview( p_event ) == false )
        {
          // stop fire preview event, and cancel it
          return false;
        }
      } catch( Exception e )
      {
        RpcUtil.logError( "the listener " + listener.toString()
            + " bug while notify a modefireEventPreview", e );
      }
    }
    // do not cancel event
    return true;
  }


}
