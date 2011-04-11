package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("chat")
public interface ChatService extends RemoteService
{

  public void sendChatMessage(ChatMessage p_msg) throws IllegalArgumentException;

  public ChatMessage getChatMessage(long p_gameId);

  public PresenceRoom getRoom(long p_gameId);

  public void disconnect(Presence p_presence);

  public String reconnect(Presence p_presence);
}
