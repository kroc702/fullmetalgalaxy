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

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.board.MAppAdvisesStack;
import com.fullmetalgalaxy.client.board.MAppBoard;
import com.fullmetalgalaxy.client.board.MAppContext;
import com.fullmetalgalaxy.client.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.board.MAppStatusBar;
import com.fullmetalgalaxy.client.board.MAppSwitchMenu;
import com.fullmetalgalaxy.client.creation.MAppGameCreation;
import com.fullmetalgalaxy.model.ChatService;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;

/**
 * @author Vincent Legendre
 *
 */
public class AppMain extends AppRoot implements ModelUpdateListener
{
  private static AppMain m_instance = null;



  /**
   * 
   */
  public AppMain()
  {
    super();
    if( m_instance == null )
    {
      m_instance = this;
    }
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

  }

  /**
   * 
   * @return the first instance of AppMain
   */
  public static AppMain instance()
  {
    return m_instance;
  }

  private FmpCallback<ModelFmpInit> loadGameCallback = new FmpCallback<ModelFmpInit>()
  {
    @Override
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      ModelFmpInit model = (ModelFmpInit)p_result;
      if( model.getPresenceRoom() != null )
      {
        ModelFmpMain.model().m_connectedUsers = model.getPresenceRoom();
      }
      ModelFmpMain.model().load( model.getGame() );
      AppMain.instance().stopLoading();
    }
  };



  public void loadModelFmpBoard(String p_gameId)
  {
    AppMain.instance().startLoading();
    
    String strModel = ClientUtil.getJSString( "fmp_model" );
    if( strModel != null )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( Services.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strModel );
        Object object = reader.readObject();
        if( object instanceof ModelFmpInit )
        {
          loadGameCallback.onSuccess( (ModelFmpInit)object );
        }
      } catch( SerializationException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    String strRoom = ClientUtil.getJSString( "fmp_room" );
    if( strRoom != null )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( ChatService.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strRoom );
        Object object = reader.readObject();
        if( object instanceof PresenceRoom )
        {
          ModelFmpMain.model().receivePresenceRoom( (PresenceRoom)object );
        }
      } catch( SerializationException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }


    if( AppMain.instance().isLoading() )
    {
      // well, model init wasn't found in jsp => ask it with standard RPC call
      Services.Util.getInstance().getModelFmpInit( p_gameId, loadGameCallback );
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
   */
  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.model.ModelBase)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    // DbbAccountPreview account = ModelFmpMain.model().getMyAccount();
  }


  /**
   * Called when the browser window is resized.
   */
  @Override
  public void onWindowResized(int p_width, int p_height)
  {
    super.onWindowResized( p_width, p_height );
  }



  private static Map<String, MiniApp> s_mappMap = new HashMap<String, MiniApp>();

  @Override
  protected MiniApp getMApp(String p_key)
  {
    assert p_key != null;
    MiniApp miniApp = s_mappMap.get( p_key );
    if( miniApp == null )
    {
      if( p_key.equals( MAppGameCreation.HISTORY_ID ) )
      {
        miniApp = new MAppGameCreation();
      }
      else if( p_key.equals( MAppStatusBar.HISTORY_ID ) )
      {
        miniApp = new MAppStatusBar();
      }
      else if( p_key.equals( MAppContext.HISTORY_ID ) )
      {
        miniApp = new MAppContext();
      }
      else if( p_key.equals( MAppSwitchMenu.HISTORY_ID ) )
      {
        miniApp = new MAppSwitchMenu();
      }
      else if( p_key.equals( MAppMessagesStack.HISTORY_ID ) )
      {
        miniApp = new MAppMessagesStack();
      }
      else if( p_key.equals( MAppAdvisesStack.HISTORY_ID ) )
      {
        miniApp = new MAppAdvisesStack();
      }
      else if( p_key.equals( MAppBoard.HISTORY_ID ) )
      {
        miniApp = new MAppBoard();
      }
      s_mappMap.put( p_key, miniApp );
    }
    return miniApp;
  }

  @Override
  public HistoryState getDefaultHistoryState()
  {

    HistoryState defaultHistory = super.getDefaultHistoryState();
    if( defaultHistory.isEmpty() )
    {
      defaultHistory = getEmptyHistoryState();
    }
    return defaultHistory;
  }


  public static HistoryState getEmptyHistoryState()
  {
    HistoryState history = new HistoryState();
    history.addKey( MAppStatusBar.HISTORY_ID );
    return history;
  }

  public static HistoryState getNewGameHistoryState()
  {
    HistoryState history = getEmptyHistoryState();
    history.addKey( MAppGameCreation.HISTORY_ID );
    return history;
  }

  public static HistoryState getGameHistoryState()
  {
    HistoryState history = getEmptyHistoryState();
    history.addKey( MAppBoard.HISTORY_ID );
    history.addKey( MAppContext.HISTORY_ID );
    history.addKey( MAppSwitchMenu.HISTORY_ID );
    history.addKey( MAppMessagesStack.HISTORY_ID );
    history.addKey( MAppAdvisesStack.HISTORY_ID );
    return history;
  }

  public void gotoCreateGame()
  {
    History.newItem( getNewGameHistoryState().toString() );
  }

  public void gotoAccountDirectory()
  {
    /*resetMiniApp();
    if( m_mAppAccountDirectory == null )
    {
      m_mAppAccountDirectory = new MAppAccountDirectory();
    }*/
    // addMiniApp( m_mAppAccountDirectory, DockPanel.CENTER );
  }

  public void gotoMyAccount()
  {
    /*if( ModelFmpMain.model().getMyAccount() != null )
    {
      resetMiniApp();
      if( m_mAppCreateAccount == null )
      {
        m_mAppCreateAccount = new MAppCreateAccount();
      }
      m_mAppCreateAccount.setAccount( ModelFmpMain.model().getMyAccount() );
      // addMiniApp( m_mAppCreateAccount );
    }*/
  }

  public void gotoHome()
  {
    History.newItem( getDefaultHistoryState().toString() );
  }

  public void gotoGame(long p_idGame)
  {
    HistoryState history = getGameHistoryState();
    history.setLong( MAppBoard.s_TokenIdGame, p_idGame );
    History.newItem( history.toString() );
  }

  public void gotoEditGame(long p_idGame)
  {
    HistoryState history = getNewGameHistoryState();
    history.setLong( MAppGameCreation.s_TokenIdGame, p_idGame );
    History.newItem( history.toString() );
  }
}
