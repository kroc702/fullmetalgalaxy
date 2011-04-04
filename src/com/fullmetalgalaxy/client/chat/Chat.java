package com.fullmetalgalaxy.client.chat;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ChatService;
import com.fullmetalgalaxy.model.ChatServiceAsync;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.Presence.ClientType;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Chat implements EntryPoint, Window.ClosingHandler
{

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private static final ChatServiceAsync s_chatService = GWT.create( ChatService.class );

  private String m_pseudo = "";
  private int m_pageId = 0;
  private ChatMessage m_lastMsg = new ChatMessage();
  WgtMessages m_wgtMessages = null;
  VerticalPanel m_wgtPresence = new VerticalPanel();

  private String m_channelToken = "";
  private PresenceRoom m_presenceRoom = new PresenceRoom( 0 );


  private AsyncCallback<Void> m_sendMessageCallback = new AsyncCallback<Void>()
  {

    @Override
    public void onFailure(Throwable p_caught)
    {
      m_wgtMessages.addMessage( m_lastMsg.getText() );
      m_wgtMessages.addMessage( "last message failed" );
    }

    @Override
    public void onSuccess(Void p_result)
    {
    }

  };

  private AsyncCallback<Void> m_disconnectCallback = new AsyncCallback<Void>()
  {

    @Override
    public void onFailure(Throwable p_caught)
    {
    }

    @Override
    public void onSuccess(Void p_result)
    {
    }

  };

  private AsyncCallback<String> m_reconnectCallback = new AsyncCallback<String>()
  {
    @Override
    public void onFailure(Throwable p_caught)
    {
      Window.alert( "server (re)connexion error !!!" );
    }
    @Override
    public void onSuccess(String p_result)
    {
      m_channelToken = p_result;
      if( m_channelToken == null || m_channelToken.equalsIgnoreCase( "null" ) )
      {
        m_wgtMessages.addMessage( "server (re)connexion error !!!" );
      }
      else
      {
        ChannelFactory.createChannel( m_channelToken, m_callbackChannel );
      }
    }
  };


  private AsyncCallback<ChatMessage> m_getChatMessageCallback = new AsyncCallback<ChatMessage>()
  {

    @Override
    public void onFailure(Throwable p_caught)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(ChatMessage p_result)
    {
      m_wgtMessages.addMessage( p_result.getFromPseudo() + ": " + p_result.getText() );
    }

  };

  private AsyncCallback<PresenceRoom> m_getPresenceRoomCallback = new AsyncCallback<PresenceRoom>()
  {

    @Override
    public void onFailure(Throwable p_caught)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(PresenceRoom p_result)
    {
      //m_wgtMessages.addMessage( "receive presence list" );
      m_wgtPresence.clear();
      for( Presence presence : p_result )
      {
        m_wgtPresence.add( new Label( presence.getPseudo() ) );
      }
    }

  };

  private ChannelCreatedCallback m_callbackChannel = new ChannelCreatedCallback()
  {
    @Override
    public void onChannelCreated(Channel channel)
    {
      channel.open( m_socketListener );
    }
  };

  private SocketListener m_socketListener = new SocketListener()
  {
    @Override
    public void onOpen()
    {
      // TODO i18n
      m_wgtMessages.addMessage( "ready to chat..." );
    }

    @Override
    public void onMessage(String message)
    {
      Object object = null;
      SerializationStreamFactory factory = GWT.create( ChatService.class );
      try
      {
        // Decode the data
        SerializationStreamReader reader = factory.createStreamReader( message );
        object = reader.readObject();

        if( object instanceof PresenceRoom )
        {
          m_getPresenceRoomCallback.onSuccess( (PresenceRoom)object );
        }
        else if( object instanceof ChatMessage )
        {
          m_getChatMessageCallback.onSuccess( (ChatMessage)object );
        }
        else
        {
          m_wgtMessages.addMessage( message );
        }
      } catch( SerializationException e )
      {
        m_wgtMessages.addMessage( message + "\n" + e.getMessage() );
      }


    }

    @Override
    public void onError(SocketError error)
    {
      // This occur after two hours. in this case, we ask server for a new channel token
      s_chatService.reconnect( getMyPresence(), m_reconnectCallback );
    }

    @Override
    public void onClose()
    {
      // nothing to do
    }
  };
  
  /**
   * create a new instance of my presence
   * @return
   */
  public Presence getMyPresence()
  {
    Presence presence = new Presence( m_pseudo, m_presenceRoom.getGameId(), m_pageId );
    presence.setClientType( ClientType.CHAT );
    return presence;
  }


  public void sendMessage( String p_msg )
  {
    m_lastMsg.setFromPseudo( m_pseudo );
    m_lastMsg.setGameId( m_presenceRoom.getGameId() );
    m_lastMsg.setFromPageId( (int)m_pageId );
    m_lastMsg.setText( p_msg );
    m_lastMsg.getDate().setTime( System.currentTimeMillis() );
    s_chatService.sendMessages( m_lastMsg, m_sendMessageCallback );
  }

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad()
  {
    // init some constant
    String id = ClientUtil.getUrlParameter( "id" );
    long gameId = 0;
    try
    {
      gameId = Long.parseLong( id );
    } catch( Exception e )
    {
      gameId = 0;
    }
    m_presenceRoom.setGameId( gameId );
    m_pseudo = ClientUtil.readGwtPropertyString( "fmp_userpseudo" );
    m_pageId = ClientUtil.readGwtPropertyLong( "fmp_pageid" ).intValue();

    // init interface
    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.setSize( "100%", "100%" );
    m_wgtMessages = new WgtMessages( this );
    hpanel.add( m_wgtMessages );
    hpanel.add( m_wgtPresence );
    RootPanel.get( "wgtmessages" ).add( hpanel );
    // Focus the cursor on the name field when the app loads
    m_wgtMessages.setFocus( true );

    // connect to chat server
    m_channelToken = ClientUtil.readGwtProperty( "fmp_channelToken" );
    m_reconnectCallback.onSuccess( m_channelToken );

    // disconnect if leaving this page
    Window.addWindowClosingHandler( this );

    // read embedded conversation class
    // String strConversation = ClientUtil.readGwtProperty( "fmp_conversation"
    // );
    String strConversation = ClientUtil.getJSString( "conversation" );
    m_socketListener.onMessage( strConversation );
  }

  @Override
  public void onWindowClosing(ClosingEvent p_event)
  {
    s_chatService.disconnect( getMyPresence(), m_disconnectCallback );
  }
}
