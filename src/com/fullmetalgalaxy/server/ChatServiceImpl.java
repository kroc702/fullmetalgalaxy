package com.fullmetalgalaxy.server;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ChatService;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ChatServiceImpl extends RemoteServiceServlet implements ChatService
{
  @SuppressWarnings("unused")
  private final static FmpLogger log = FmpLogger.getLogger( ChatServiceImpl.class.getName() );

  



  @Override
  public void sendMessages(ChatMessage p_msg) throws IllegalArgumentException
  {
    // we could check pseudo to detect cheater...
    //p_msg.setFromPseudo( Auth.getUserPseudo( getThreadLocalRequest(), getThreadLocalResponse() ) );
    p_msg.setDate( ServerUtil.currentDate() );

    PresenceRoom room = ChannelManager.getRoom( p_msg.getGameId() );
    ChannelManager.broadcast( room, p_msg );
  }

  /**
   * This service is only here to serialize a ChatMessage class with RPC.encodeResponseForSuccess
   */
  @Override
  public ChatMessage getChatMessage(long p_gameId)
  {
    return new ChatMessage();
  }

  /**
   * return non null PresenceRoom class associated with p_gameId.
   * This service is also here to serialize a PresenceRoom class with RPC.encodeResponseForSuccess
   */
  @Override
  public PresenceRoom getRoom(long p_gameId)
  {
    return ChannelManager.getRoom( p_gameId );
  }




  @Override
  public void disconnect(Presence p_presence)
  {
    ChannelManager.disconnect( p_presence );
  }

  @Override
  public String reconnect(Presence p_presence)
  {
    return ChannelManager.connect( p_presence );
  }
}
