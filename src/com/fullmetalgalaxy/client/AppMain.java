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


import com.fullmetalgalaxy.client.chat.ChatEngine;
import com.fullmetalgalaxy.client.chat.MAppChat;
import com.fullmetalgalaxy.client.chat.MAppLittlePresences;
import com.fullmetalgalaxy.client.chat.MAppPresences;
import com.fullmetalgalaxy.client.creation.MAppGameCreation;
import com.fullmetalgalaxy.client.event.ChannelMessageEventHandler;
import com.fullmetalgalaxy.client.event.ChannelMessageHandlerCollection;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.event.SourcesChannelMessageEvents;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.game.board.MAppStatusBar;
import com.fullmetalgalaxy.client.game.context.MAppContext;
import com.fullmetalgalaxy.client.game.tabmenu.MAppTabMenu;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameServicesAsync;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Vincent Legendre
 *
 */
public class AppMain extends AppRoot implements SourcesChannelMessageEvents, Window.ClosingHandler
{
  private static AppMain s_instance = null;
  private static final int WATCHDOG_PERIOD_MS = 1000 * 60 * 3; // 3 min
  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private static final GameServicesAsync s_gameService = GWT.create( GameServices.class );

  public static GameServicesAsync getRpcService()
  {
    return s_gameService;
  }
  
  public static AppMain instance()
  {
    return s_instance;
  }
  
  protected EbPublicAccount m_myAccount = new EbPublicAccount();

  protected boolean m_myAccountAdmin = false;

  /**
   * @see ModelFmpInit.m_channelToken
   */
  private String m_channelToken = null;
  private long m_gameId = 0;
  private int m_pageId = 0;
  private boolean m_isChannelConnected = false;

  private ChannelMessageHandlerCollection m_channelMessageHandlerCollection = new ChannelMessageHandlerCollection();
  
  public boolean m_isReloadTimerScheduled = false;
  private Timer m_reloadTimer = new Timer()
  {
    @Override
    public void run()
    {
      ClientUtil.reload();
      m_isReloadTimerScheduled = false;
    }
  };

  private Timer m_channelWatchDogTimer = new Timer()
  {
    @Override
    public void run()
    {
      // send a keep alive message.
      // server should answer with an empty chat message.
      ChatMessage msg = new ChatMessage( m_gameId, getMyPresence().getPseudo(), null );
      AppMain.getRpcService().sendChatMessage( msg, m_dummyCallback );
      scheduleReloadTimer();
    }
  };

private ChatEngine m_chatEngine = null;


  /**
   * 
   */
  public AppMain()
  {
    super();
    s_instance = this;
    loadAccountInfoFromPage();

    // disconnect if leaving this page
    Window.addWindowClosingHandler( this );
  }

  private void loadAccountInfoFromPage()
  {
    getMyAccount().setPseudo( ClientUtil.readGwtPropertyString( "fmp_userpseudo" ) );
    getMyAccount().setId( ClientUtil.readGwtPropertyLong( "fmp_userid" ) );
    getMyAccount().setCurrentLevel( ClientUtil.readGwtPropertyLong( "fmp_userlevel" ).intValue() );
    m_myAccountAdmin = ClientUtil.readGwtPropertyBoolean( "fmp_useradmin" );
    m_pageId = ClientUtil.readGwtPropertyLong( "fmp_pageid" ).intValue();
    m_gameId = ClientUtil.readGwtPropertyLong( "fmp_gameid" ).longValue();
    
    m_channelToken = ClientUtil.readGwtProperty( "fmp_channelToken" );
    if( m_channelToken != null && !m_channelToken.equalsIgnoreCase( "null" ) )
    {
      m_reconnectCallback.onSuccess( m_channelToken );
    }
  }


  /**
   * create a new instance of my presence
   * @return
   */
  public Presence getMyPresence()
  {
    Presence presence = new Presence( AppMain.instance().getMyAccount().getPseudo(), m_gameId,
        AppMain.instance().getPageId() );
    //presence.setClientType( ClientType.GAME );
    //presence.setClientType( ClientType.CHAT );
    return presence;
  }

  public EbPublicAccount getMyAccount()
  {
    return m_myAccount;
  }

  public boolean iAmAdmin()
  {
    return m_myAccountAdmin;
  }

  private AsyncCallback<Void> m_dummyCallback = new AsyncCallback<Void>()
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
        Window.alert( "server (re)connexion error !!!" );
      }
      else
      {
        ChannelFactory.createChannel( m_channelToken, m_callbackChannel );
      }
    }
  };

  private ChannelCreatedCallback m_callbackChannel = new ChannelCreatedCallback()
  {
    @Override
    public void onChannelCreated(Channel channel)
    {
      channel.open( new SocketListener()
      {
        @Override
        public void onOpen()
        {
          //MAppMessagesStack.s_instance.showMessage( "onOpen" );
          
          // send an empty chat message to check channel is working
          ChatMessage message = new ChatMessage();
          message.setGameId( m_gameId );
          message.setFromPageId( getPageId() );
          message.setFromPseudo( AppMain.instance().getMyAccount().getPseudo() );
          AppMain.getRpcService().sendChatMessage( message, m_dummyCallback );
        }

        @Override
        public void onMessage(String message)
        {
          //MAppMessagesStack.s_instance.showMessage( "onMessage" );
          
          // we receive a message from channel: we won't need to reload page
          cancelReloadTimer();
          // also reshedule channel watchdog
          if( isChannelConnected() )
          {
              m_channelWatchDogTimer.cancel();
          }
          m_channelWatchDogTimer.schedule( WATCHDOG_PERIOD_MS );

          // We could set this flag in onOpen callback
          // but on some browser this doesn't reflect reality
          m_isChannelConnected = true;

          Object object = deserialize( message );
          
          // TODO use a dedicated class instead of this empty ChatMessage for keep alive msg
          if( object instanceof ChatMessage )
          {
            ChatMessage p_msg = (ChatMessage)object;
            if( p_msg.isEmpty() 
                && !getMyAccount().getPseudo().equalsIgnoreCase( p_msg.getFromPseudo() ))
            {
              // empty message: server ask if we are still connected
              ChatMessage keepAliveMessage = new ChatMessage();
              keepAliveMessage.setGameId( GameEngine.model().getGame().getId() );
              keepAliveMessage.setFromPageId( AppMain.instance().getPageId() );
              keepAliveMessage.setFromPseudo( AppMain.instance().getMyAccount().getPseudo() );
              AppMain.getRpcService().sendChatMessage( keepAliveMessage, m_dummyCallback );
            }
          }
          
          m_channelMessageHandlerCollection.fireEventChanelMessage( object );

        }

        @Override
        public void onError(SocketError error)
        {
          m_isChannelConnected = false;
          MAppMessagesStack.s_instance.showWarning( "Error: " + error.getDescription() );
        }

        @Override
        public void onClose()
        {
          //MAppMessagesStack.s_instance.showWarning( "onClose" );
          m_isChannelConnected = false;
          // This occur after two hours. in this case, we ask server for a new
          // channel token
          AppMain.getRpcService().reconnect( getMyPresence(), m_reconnectCallback );
        }
      } );
    }
  };


  private Object deserialize(String p_serial)
  {
    Object object = null;
    try
    {
      // Decode game data
      SerializationStreamFactory factory = GWT.create( GameServices.class );
      SerializationStreamReader reader = factory.createStreamReader( p_serial );
      object = reader.readObject();
      if( object != null )
      {
        return object;
      }
    } catch( Exception e )
    {
      AppRoot.logger.severe( e.getMessage() );
    }
    return object;
  }





  /* (non-Javadoc)
   * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
   */
  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
    
    // start engines, ie none graphical components
    if(RootPanel.get(GameEngine.HISTORY_ID) != null )
    {
      new GameEngine().onModuleLoad();
    }
    if(RootPanel.get(ChatEngine.HISTORY_ID) != null )
    {
      m_chatEngine = new ChatEngine();
      m_chatEngine.onModuleLoad();
    }
    
    // call all entry point involve in this application
    if( RootPanel.get(MAppContext.HISTORY_ID) != null )
    {
      new MAppContext().onModuleLoad();
    }
    if( RootPanel.get(MAppGameCreation.HISTORY_ID) != null )
    {
      new MAppGameCreation().onModuleLoad();
    }
    if( RootPanel.get(MAppStatusBar.HISTORY_ID) != null )
    {
      new MAppStatusBar().onModuleLoad();
    }
    if( RootPanel.get(MAppMessagesStack.HISTORY_ID) != null )
    {
      new MAppMessagesStack().onModuleLoad();
    }
    if( RootPanel.get(MAppBoard.HISTORY_ID) != null )
    {
      new MAppBoard().onModuleLoad();
    }
    if( RootPanel.get(MAppTabMenu.HISTORY_ID) != null )
    {
      new MAppTabMenu().onModuleLoad();
    }
    if( RootPanel.get(MAppLittlePresences.HISTORY_ID) != null )
    {
      new MAppLittlePresences().onModuleLoad();
    }
    if( RootPanel.get(MAppPresences.HISTORY_ID) != null )
    {
      new MAppPresences().onModuleLoad();
    }
    if( RootPanel.get(MAppChat.HISTORY_ID) != null )
    {
      new MAppChat().onModuleLoad();
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  public boolean isChannelConnected()
  {
    return m_isChannelConnected;
  }

  /**
   * @return the channelToken
   */
  protected String getChannelToken()
  {
    return m_channelToken;
  }


  /**
   * @return the pageId
   */
  public int getPageId()
  {
    return m_pageId;
  }
  
  public boolean isUserConnected(String p_pseudo)
  {
    if( m_chatEngine != null && p_pseudo != null )
    {
      return m_chatEngine.isUserConnected( p_pseudo );
    }
    return false;
  }

  public PresenceRoom getPresenceRoom()
  {
    if( m_chatEngine != null )
    {
      return m_chatEngine.getPresenceRoom();
    }
    return new PresenceRoom(0);
  }


  @Override
  public void addChannelMessageEventHandler(Class<?> p_class,
      ChannelMessageEventHandler p_handler)
  {
    m_channelMessageHandlerCollection.addChannelMessageEventHandler( p_class, p_handler );
  }

  @Override
  public void removeChannelMessageEventHandler(Class<?> p_class,
      ChannelMessageEventHandler p_handler)
  {
    m_channelMessageHandlerCollection.removeChannelMessageEventHandler( p_class, p_handler );
  }
  
  /**
   * This method should be called if we are waiting for Channel Message.
   * If we don't receive it after a short time period, page will be reloaded.
   */
  public void scheduleReloadTimer()
  {
    // reload page if no response after 10 seconds
    m_reloadTimer.schedule( 10000 );
    m_isReloadTimerScheduled = true;
  }
  
  public void cancelReloadTimer()
  {
    if( m_isReloadTimerScheduled )
    {
      m_reloadTimer.cancel();
      m_isReloadTimerScheduled = false;
    }
  }
  
  @Override
  public void onWindowClosing(ClosingEvent p_event)
  {
    AppMain.getRpcService().disconnect( getMyPresence(), m_dummyCallback );
  }
  
  
}
