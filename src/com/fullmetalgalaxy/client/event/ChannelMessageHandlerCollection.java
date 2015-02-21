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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.MAppMessagesStack;

/**
 * @author Vincent Legendre
 * A helper class for implementers of the SourcesChannelMessageEvents interface. This subclass of ArrayList assumes that
 * all objects added to it will be of type EventPreview.
 */

public class ChannelMessageHandlerCollection implements SourcesChannelMessageEvents
{
  static final long serialVersionUID = 15;

  private Map<Class<?>, ArrayList<ChannelMessageEventHandler> > m_handlerMap = new HashMap<Class<?>, ArrayList<ChannelMessageEventHandler> >();
  
  /**
   * 
   */
  public ChannelMessageHandlerCollection()
  {
  }


  public void fireEventChanelMessage(Object p_message)
  {
    //MAppMessagesStack.s_instance.showMessage( "fireEventChanelMessage "+p_message );
    if( p_message == null )
    {
      return;
    }
    ArrayList<ChannelMessageEventHandler> handlerList = m_handlerMap.get( p_message.getClass() );
    if( handlerList != null )
    {
      for( ChannelMessageEventHandler handler : handlerList)
      {
        if( handler != null )
        {
          try
          {
            //MAppMessagesStack.s_instance.showMessage( "before onChannelMessage " );
            handler.onChannelMessage( p_message );
            //MAppMessagesStack.s_instance.showMessage( "after onChannelMessage " );
          } catch( Exception e )
          {
            AppRoot.logger.log( Level.WARNING, "the listener " + handler.toString()
                + " bug while notify a ChannelMessageEvent", e );
          }
        }
      }
    }
    //MAppMessagesStack.s_instance.showMessage( "end fireEventChanelMessage" );
  }


  @Override
  public void addChannelMessageEventHandler(Class<?> p_class, ChannelMessageEventHandler p_handler)
  {
    //assert p_handler instanceof ChannelMessageEventHandler<p_class>;
    ArrayList<ChannelMessageEventHandler> handlerList = m_handlerMap.get( p_class );
    if( handlerList == null )
    {
      handlerList = new ArrayList<ChannelMessageEventHandler>();
      m_handlerMap.put( p_class, handlerList );
    }
    handlerList.add( p_handler );
  }


  @Override
  public void removeChannelMessageEventHandler(Class<?> p_class,
      ChannelMessageEventHandler p_handler)
  {
    ArrayList<ChannelMessageEventHandler> handlerList = m_handlerMap.get( p_class );
    if( handlerList != null )
    {
      handlerList.remove( p_handler );
    }
  }


}
