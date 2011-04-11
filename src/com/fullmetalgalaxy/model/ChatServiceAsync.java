package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ChatService</code>.
 */
public interface ChatServiceAsync
{
  void sendChatMessage(ChatMessage p_msg, AsyncCallback<Void> callback)
      throws IllegalArgumentException;

  public void getChatMessage(long p_gameId, AsyncCallback<ChatMessage> callback);

  public void getRoom(long p_gameId, AsyncCallback<PresenceRoom> callback);

  public void disconnect(Presence p_prensence, AsyncCallback<Void> callback);
  
  public void reconnect(Presence p_presence, AsyncCallback<String> callback);
}
