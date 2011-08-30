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

  /**
   * This service is only here to serialize a ChatMessage class with RPC.encodeResponseForSuccess
   */
  public ChatMessage getChatMessage(long p_gameId);

  /**
   * return non null PresenceRoom class associated with p_gameId.
   * This service is also here to serialize a PresenceRoom class with RPC.encodeResponseForSuccess
   */
  public PresenceRoom getRoom(long p_gameId);

  public void disconnect(Presence p_presence);

  public String reconnect(Presence p_presence);
}
