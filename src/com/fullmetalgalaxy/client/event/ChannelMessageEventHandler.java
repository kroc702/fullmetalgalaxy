package com.fullmetalgalaxy.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author vlegendr
 * 
 * 
 */ 
public interface ChannelMessageEventHandler extends EventHandler
{
  // TODO find a way to type Object in signature
  public void onChannelMessage( Object p_message);
}
