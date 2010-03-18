/**
 * 
 */
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
import com.fullmetalgalaxy.client.home.MAppGameList;
import com.fullmetalgalaxy.client.ressources.MessagesAppMain;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;

/**
 * @author Vincent Legendre
 *
 */
public class AppMain extends AppRoot implements ModelUpdateListener
{
  public static MessagesAppMain s_messages = (MessagesAppMain)GWT.create( MessagesAppMain.class );

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
      ModelFmpMain.model().addAllAccounts( p_result.getMapAccounts() );
      if( model.getConnectedUsers() != null )
      {
        ModelFmpMain.model().m_connectedUsers = model.getConnectedUsers();
      }
      ModelFmpMain.model().load( model.getGame() );
      AppMain.instance().stopLoading();
    }
  };



  public void loadModelFmpBoard(String p_gameId)
  {
    AppMain.instance().startLoading();
    Services.Util.getInstance().getModelFmpInit( p_gameId, loadGameCallback );
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
      if( p_key.equals( MAppGameList.HISTORY_ID ) )
      {
        miniApp = new MAppGameList();
      }
      else if( p_key.equals( MAppGameCreation.HISTORY_ID ) )
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
      defaultHistory.addKey( MAppGameList.HISTORY_ID );
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
