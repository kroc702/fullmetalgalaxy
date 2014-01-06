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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.event.ChannelMessageEventHandler;
import com.fullmetalgalaxy.client.event.GameActionEvent;
import com.fullmetalgalaxy.client.event.GameLoadEvent;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.board.DlgJoinGame;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.game.board.layertoken.AnimEvent;
import com.fullmetalgalaxy.client.game.board.layertoken.AnimFactory;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.HexCoordinateSystem;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;


/**
 * @author Vincent Legendre
 *
 */
public class GameEngine implements EntryPoint, ChannelMessageEventHandler
{
  public static final String HISTORY_ID = "GameEngine";
  public static Logger logger = Logger.getLogger( "GameEngine" );
  private static GameEngine s_ModelFmpMain = null;
  /** polling method is used as a fallback is channel is disconnected */
  private static final int EVENTS_POLLING_PERIOD_MS = 1000 * 30; // 30 sec

  /**
   * @return a unique instance of the model on client side
   */
  public static GameEngine model()
  {
    return s_ModelFmpMain;
  }

  public static Game game()
  {
    return GameEngine.model().getGame();
  }
  
  public static HexCoordinateSystem coordinateSystem()
  {
    return GameEngine.model().getGame().getCoordinateSystem();
  }
  
  protected Game m_game = new Game();


  protected EventsPlayBuilder m_actionBuilder = new EventsPlayBuilder();


  // interface
  private boolean m_isGridDisplayed = false;

  private boolean m_isFireCoverDisplayed = false;

  private EnuZoom m_zoomDisplayed = new EnuZoom( EnuZoom.Medium );
  // cloud layer
  private boolean m_isAtmosphereDisplayed = true;
  // standard land layer or custom map image
  private boolean m_isCustomMapDisplayed = false;

  /**
   * if set, user can't do anything else:
   * - navigate in past actions
   * - exiting this mode
   * - if game is puzzle or standard (turn by turn, no time limit) validate to cancel some actions
   */
  private boolean m_isTimeLineMode = false;
  /** correspond to currently displayed action index in time line mode
   * this index doesn't take into account additional event (ie not loaded but present in database)
   *  */
  private int m_currentActionIndex = 0;
  /** game currentTimeStep at the moment we start time line mode */
  private int m_lastTurnPlayed = 0;
  

  private int m_successiveRpcErrorCount = 0;


  /**
   * 
   */
  public GameEngine()
  {
    s_ModelFmpMain = this;
  }


  private FmpCallback<ModelFmpInit> loadGameCallback = new FmpCallback<ModelFmpInit>()
  {
    @Override
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      ModelFmpInit model = (ModelFmpInit)p_result;
      if( model==null )
      {
        Window.alert( MAppBoard.s_messages.unknownGame() );
        return;
      }
      else
      {
        if( model.getPresenceRoom() != null )
        {
          //ModelFmpMain.model().m_connectedUsers = model.getPresenceRoom();
        }
        m_game = model.getGame();
        getActionBuilder().setGame( getGame() );
        getActionBuilder().setMyAccount( AppMain.instance().getMyAccount() );
        // play my events (for parallel hidden turns)
        try
        {
          if( getMyRegistration() != null )
          {
            for( AnEvent event : getMyRegistration().getTeam( getGame() ).getMyEvents() )
            {
              event.exec( getGame() );
            }
          }
        } catch( Throwable e )
        {
          // no i18n
          RpcUtil.logError( "error ", e );
          Window.alert( "unexpected error : " + e );
        }

        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
        AppRoot.getEventBus().fireEvent( new GameLoadEvent( m_game ) );
        if( m_game.getGameType() == GameType.MultiPlayer
            || m_game.getGameType() == GameType.Initiation )
        {
          AppMain.instance().addChannelMessageEventHandler( ModelFmpUpdate.class, GameEngine.model() );
          // this timer poll server for update if channel isn't connected
          m_eventsPollingTimer.scheduleRepeating( EVENTS_POLLING_PERIOD_MS );
        }
        else
        {
          LocalGame.loadGame( GameEngine.model() );
        }
        AppMain.instance().stopLoading();
        
        // if current user is the game creator and game need player, ask him with a dialog
        //
        if( m_game.getStatus() == GameStatus.Open
            && AppMain.instance().getMyAccount() != null
            && AppMain.instance().getMyAccount().getId() == m_game.getAccountCreator().getId() 
            && getMyRegistration() == null )
        {
          DlgJoinGame.instance().show();
          DlgJoinGame.instance().center();
        }
      }
    }
  };

  private Timer m_eventsPollingTimer = new Timer()
  {
    @Override
    public void run()
    {
      // don't ask for update if we are waiting for response !
      if( !AppMain.instance().isChannelConnected()
          && m_lastModelUpdateClientID == 0
          && (getGame().getGameType() == GameType.MultiPlayer || getGame().getGameType() == GameType.Initiation) )
      {
        AppMain.getRpcService().getUpdate( getGame().getId(), getGame().getVersion(),
            m_callbackEvents );

      }
    }
  };

  public EbRegistration getMyRegistration()
  {
    if( getGame().getGameType() == GameType.Puzzle )
    {
      return getGame().getRegistration( getGame().getCurrentPlayerIds().get( 0 ) );
    }
    if( !isLogged() )
    {
      return null;
    }
    for( Iterator<EbRegistration> it = getGame().getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.haveAccount() 
          && registration.getAccount().getId() == AppMain.instance().getMyAccount().getId() )
      {
        return registration;
      }
    }
    return null;
  }


  /**
   * This method build a short string that represent current hmi option
   * (ie grid, atmosphere, zoom, fire cover)
   * This compact amount of data to be saved in cookies (and then send to server).
   * @return
   */
  private String buildHMIFlags()
  {
    StringBuffer flags = new StringBuffer();
    flags.append( isGridDisplayed() ? 'G' : 'g' );
    flags.append( isAtmosphereDisplayed() ? 'A' : 'a' );
    flags.append( getZoomDisplayed().getValue() == EnuZoom.Medium ? 'Z' : 'z' );
    flags.append( isFireCoverDisplayed() ? 'F' : 'f' );
    return flags.toString();
  }

  private void applyHMIFlags(String p_flags)
  {
    if( p_flags == null )
    {
      return;
    }
    for( int i = 0; i < p_flags.length(); i++ )
    {
      switch( p_flags.charAt( i ) )
      {
      case 'G':
        setGridDisplayed( true );
        break;
      case 'g':
        setGridDisplayed( false );
        break;
      case 'A':
        setAtmosphereDisplayed( true );
        break;
      case 'a':
        setAtmosphereDisplayed( false );
        break;
      case 'Z':
        setZoomDisplayed( EnuZoom.Medium );
        break;
      case 'z':
        setZoomDisplayed( EnuZoom.Small );
        break;
      case 'F':
        setFireCoverDisplayed( true );
        break;
      case 'f':
        setFireCoverDisplayed( false );
        break;
      default:
        // do nothing
      }
    }
  }

  /**
   * This method save HMI option in cookies to be restored later
   */
  private void backupHMIFlags()
  {
    Cookies.setCookie( "HMIFlags", buildHMIFlags(), new Date( Long.MAX_VALUE ) );
  }

  private void restoreHMIFlags()
  {
    applyHMIFlags( Cookies.getCookie( "HMIFlags" ) );
  }


  public boolean isLogged()
  {
    return AppMain.instance().getMyAccount().getId() != 0;
  }


  public boolean isJoined()
  {
    return getMyRegistration() != null;
  }

  /**
   * @return the action
   */
  public ArrayList<AnEventPlay> getActionList()
  {
    return getActionBuilder().getActionList();
  }

  public void reinitGame()
  {
    m_game = new Game();
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  public Game getGame()
  {
    return m_game;
  }



  /** last model update client ID that was send without any response.
   * if 0: no action was send to server. */
  private int m_lastModelUpdateClientID = 0;


  private FmpCallback<ModelFmpUpdate> m_callbackEvents = new FmpCallback<ModelFmpUpdate>()
  {
    @Override
    public void onSuccess(ModelFmpUpdate p_result)
    {
      super.onSuccess( p_result );
      m_successiveRpcErrorCount = 0;
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().clear();
      AppMain.fireEventChannelMessage( p_result );
    }

    @Override
    public void onFailure(Throwable p_caught)
    {
      m_successiveRpcErrorCount++;
      //super.onFailure( p_caught );
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      // maybe the action failed because the model isn't up to date
      if( m_successiveRpcErrorCount <= 2 && AppMain.instance().isChannelConnected() )
      {
        if( p_caught instanceof RpcFmpException )
        {
          MAppMessagesStack.s_instance
.showWarning( ((RpcFmpException)p_caught)
              .getLocalizedMessage() );
        }
      }
      else
      {
        // too much successive error: reload page
        ClientUtil.reload();
      }
    }
  };



  protected void receiveModelUpdate(ModelFmpUpdate p_result)
  {
    if( p_result == null || getGame() == null )
    {
      // this shouldn't occur anymore !
      return;
    }

    try
    {
      if( (getGame().getGameType() == GameType.MultiPlayer || getGame().getGameType() == GameType.Initiation)
          && getGame().getVersion() >= p_result.getToVersion() )
      {
        // assume we can discard this update !
        return;
      }

      if( getGame().getVersion() < p_result.getFromVersion()
          || (getGame().getVersion() != p_result.getFromVersion() && getGame().getVersion() < p_result
              .getToVersion()) )
      {
        Window.alert( "Error: receive incoherant model update (" + p_result.getFromVersion()
            + " expected " + getGame().getVersion() + "). reload page" );
        ClientUtil.reload();
        return;
      }

      if( p_result.getClientID() == m_lastModelUpdateClientID )
      {
        // this update correspond to the last action request
        // and we receive response from channel before RCP !
        AppMain.instance().stopLoading();
        getActionBuilder().clear();
      }


      if( p_result.getAccountId() == AppMain.instance().getMyAccount().getId() )
      {
        getActionBuilder().clear();
      }
      getGame().setVersion( p_result.getToVersion() );

      // handle game events first
      //
      boolean isNewPlayerTurn = false;
      List<AnEvent> events = p_result.getGameEvents();
      for( AnEvent event : events )
      {
        // if we receive and end turn event after an hidden parallel time step
        // we need to unexec my private event logs
        if( event instanceof EbEvtPlayerTurn && getGame().getCurrentPlayerIds().size() == 1 )
        {
          isNewPlayerTurn = true;
          if( getGame().isTimeStepParallelHidden( getGame().getCurrentTimeStep() )
              && getMyRegistration() != null
              && !getMyRegistration().getTeam( getGame() ).getMyEvents().isEmpty() )
          {
            for( int i = getMyRegistration().getTeam( getGame() ).getMyEvents().size() - 1; i >= 0; i-- )
            {
              getMyRegistration().getTeam( getGame() ).getMyEvents().get( i ).unexec( getGame() );
            }
          }
        }


        if( event.getType() == GameLogType.EvtCancel )
        {
          ((EbEvtCancel)event).execCancel( getGame() );
        }
        else if( event instanceof AnEventUser
            && event.canBeParallelHidden()
            && getGame().isTimeStepParallelHidden( getGame().getCurrentTimeStep() )
            && ((AnEventUser)event).getMyRegistration( getGame() ) != null )
        {
          EbTeam myTeam = ((AnEventUser)event).getMyRegistration( getGame() ).getTeam( getGame() );
          myTeam.addMyEvent( event );
          if( myTeam == getMyRegistration().getTeam( getGame() ) )
          {
            event.exec( getGame() );
            getGame().updateLastTokenUpdate( null );
            AppRoot.getEventBus().fireEvent( new GameActionEvent( event ) );
          }
        }
        else
        {
          event.exec( getGame() );
          getGame().addEvent( event );
          getGame().updateLastTokenUpdate( null );
          AppRoot.getEventBus().fireEvent( new GameActionEvent( event ) );
        }
        
      }

      // assume that if we receive an update, something has changed !
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );

      if( isNewPlayerTurn
          && getMyRegistration() != null
          && (getGame().getCurrentPlayerIds().size() == 0 || getGame().getCurrentPlayerIds()
              .contains( getMyRegistration().getId() )) )
      {
        Window.alert( MAppBoard.s_messages.yourTurnToPlay() );
      }
    } catch( Throwable e )
    {
      // no i18n
      RpcUtil.logError( "error ", e );
      Window.alert( "unexpected error : " + e );
    }
  }


  @Override
  public void onChannelMessage(Object p_message)
  {
    if( p_message instanceof ModelFmpUpdate)
    {
      receiveModelUpdate( (ModelFmpUpdate)p_message );
    }
  }
  



  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void runSingleAction(AnEvent p_action)
  {
    if( m_lastModelUpdateClientID != 0 )
    {
      // no i18n as HMI is lock, so it shouldn't occur
      Window.alert( "An action is already send to server... wait for server response." );
      return;
    }
    AppMain.instance().startLoading();
    m_lastModelUpdateClientID = Random.nextInt();
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate( getGame() );
    modelUpdate.setClientID( m_lastModelUpdateClientID );

    try
    {
      if( !GameEngine.model().isLogged()
          && (getGame().getGameType() == GameType.MultiPlayer || getGame().getGameType() == GameType.Initiation) )
      {
        // no i18n as HMI won't allow that. so unusual
        throw new RpcFmpException( "You must be logged to do this action" );
      }
      // do not check player is logged to let him join action
      // action.check();
      if( getGame().getGameType() == GameType.MultiPlayer
          || getGame().getGameType() == GameType.Initiation )
      {
        AppMain.instance().scheduleCheckChannelTimer();
        modelUpdate.setFromPageId( AppMain.instance().getPageId() );
        modelUpdate.setFromPseudo( AppMain.instance().getMyAccount().getPseudo() );
        modelUpdate.setGameEvents( new ArrayList<AnEvent>() );
        modelUpdate.getGameEvents().add( p_action );
        AppMain.getRpcService().runModelUpdate( modelUpdate, m_callbackEvents );
      }
      else
      {
        LocalGame.runEvent( p_action, m_callbackEvents, this );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( ex.getLocalizedMessage() );
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
    }
  }

  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void runCurrentAction()
  {
    if( m_lastModelUpdateClientID != 0 )
    {
      Window.alert( "Une action a déjà été envoyé au serveur... sans réponse pour l'instant" );
      return;
    }
    AppMain.instance().startLoading();
    m_lastModelUpdateClientID = Random.nextInt();
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate( getGame() );
    modelUpdate.setClientID( m_lastModelUpdateClientID );

    try
    {
      if( !GameEngine.model().isJoined() )
      {
        // no i18n ?
        throw new RpcFmpException( "you didn't join this game." );
      }
      // action.check();
      getActionBuilder().unexec();
      if( getGame().getGameType() == GameType.MultiPlayer
          || getGame().getGameType() == GameType.Initiation )
      {
        AppMain.instance().scheduleCheckChannelTimer();
        // then send request
        modelUpdate.setFromPageId( AppMain.instance().getPageId() );
        modelUpdate.setFromPseudo( AppMain.instance().getMyAccount().getPseudo() );
        modelUpdate.setGameEvents( new ArrayList<AnEvent>() );
        modelUpdate.getGameEvents().addAll( getActionBuilder().getActionList() );
        AppMain.getRpcService().runModelUpdate( modelUpdate, m_callbackEvents );
      }
      else
      {
        LocalGame.runAction( getActionBuilder().getActionList(), m_callbackEvents, this );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( ex.getLocalizedMessage() );
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_lastModelUpdateClientID = 0;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    }
  }





  /**
   * @return the actionBuilder
   */
  public EventsPlayBuilder getActionBuilder()
  {
    return m_actionBuilder;
  }

  /**
   * @return the isGridDisplayed
   */
  public boolean isGridDisplayed()
  {
    return m_isGridDisplayed;
  }

  /**
   * @param p_isGridDisplayed the isGridDisplayed to set
   */
  public void setGridDisplayed(boolean p_isGridDisplayed)
  {
    m_isGridDisplayed = p_isGridDisplayed;
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    backupHMIFlags();
  }

  /**
   * @return the isAtmosphereDisplayed
   */
  public boolean isAtmosphereDisplayed()
  {
    return m_isAtmosphereDisplayed;
  }

  /**
   * @param p_isAtmosphereDisplayed the isAtmosphereDisplayed to set
   */
  public void setAtmosphereDisplayed(boolean p_isAtmosphereDisplayed)
  {
    m_isAtmosphereDisplayed = p_isAtmosphereDisplayed;
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    backupHMIFlags();
  }

  /**
   * @return the isStandardLandDisplayed
   */
  public boolean isCustomMapDisplayed()
  {
    return m_isCustomMapDisplayed;
  }

  /**
   * @param p_isCustomMapDisplayed the isStandardLandDisplayed to set
   */
  public void setCustomMapDisplayed(boolean p_isCustomMapDisplayed)
  {
    m_isCustomMapDisplayed = p_isCustomMapDisplayed;
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  /**
   * @return the isFireCoverDisplayed
   */
  public boolean isFireCoverDisplayed()
  {
    return m_isFireCoverDisplayed;
  }

  /**
   * @param p_isFireCoverDisplayed the isFireCoverDisplayed to set
   */
  public void setFireCoverDisplayed(boolean p_isFireCoverDisplayed)
  {
    m_isFireCoverDisplayed = p_isFireCoverDisplayed;
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    backupHMIFlags();
  }

  /**
   * @return the zoomValueDisplayed
   */
  public EnuZoom getZoomDisplayed()
  {
    return m_zoomDisplayed;
  }

  /**
   * @param p_zoomValueDisplayed the zoomValueDisplayed to set
   */
  public void setZoomDisplayed(int p_zoomValueDisplayed)
  {
    setZoomDisplayed( new EnuZoom( p_zoomValueDisplayed ) );
  }

  public void setZoomDisplayed(EnuZoom p_zoomDisplayed)
  {
    m_zoomDisplayed = p_zoomDisplayed;
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    backupHMIFlags();
  }


  /**
   * @return the isTimeLineMode
   */
  public boolean isTimeLineMode()
  {
    return m_isTimeLineMode;
  }

  /**
   * @param p_isTimeLineMode the isTimeLineMode to set
   */
  public void setTimeLineMode(boolean p_isTimeLineMode)
  {
    getActionBuilder().clear();
    if( m_isTimeLineMode == p_isTimeLineMode )
    {
      return;
    }
    getActionBuilder().setReadOnly( p_isTimeLineMode );
    m_isTimeLineMode = p_isTimeLineMode;
    m_lastTurnPlayed = getGame().getCurrentTimeStep();
    if( !p_isTimeLineMode )
    {
      timePlay( 99999 );
    }
    m_currentActionIndex = getGame().getLogs().size();
    if( getMyRegistration() != null )
    {
      m_currentActionIndex += getMyRegistration().getTeam( getGame() ).getMyEvents().size();
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  /**
   * @return return the last time step played. ie the highest time step in this game
   */
  public int getLastTurnPlayed()
  {
    if( !isTimeLineMode() )
    {
      return getGame().getCurrentTimeStep();
    }
    return m_lastTurnPlayed;
  }

  /**
   * in time line mode, play several events backward
   * @param p_actionCount
   */
  public void timeBack(int p_actionCount)
  {
    if( p_actionCount > m_currentActionIndex )
    {
      GameEngine.model().loadAdditionalEvents();
      return;
    }



    List<AnEvent> logs = getGame().getLogs();
    while( (m_currentActionIndex > 0) && (p_actionCount > 0) )
    {
      m_currentActionIndex--;

      AnEvent action = null;
      if( m_currentActionIndex >= logs.size() && getMyRegistration() != null )
      {
        action = getMyRegistration().getTeam( getGame() ).getMyEvents()
            .get( m_currentActionIndex - logs.size() );
      }
      else
      {
        action = logs.get( m_currentActionIndex );
      }

      if( action != null && !(action instanceof EbAdmin)
          && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // unexec action
        try
        {
          action.unexec( getGame() );
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
          Window.alert( "unexpected error : " + e );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
          return;
        }
        // don't count automatic action as one action to play
        if( !action.isAuto() )
        {
          p_actionCount--;
        }
        // if previous action is EvtConstruct, then unexec too
        action = null;
        if( m_currentActionIndex >= logs.size() + 1 && getMyRegistration() != null )
        {
          action = getMyRegistration().getTeam( getGame() ).getMyEvents()
              .get( m_currentActionIndex - logs.size() - 1 );
        }
        else if( m_currentActionIndex > 0 )
        {
          action = logs.get( m_currentActionIndex - 1 );
        }
        if( action != null && action.getType() == GameLogType.EvtConstruct )
        {
          p_actionCount++;
        }
      }
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  /**
   * in time mode, play several events
   * @param p_actionCount
   */
  public void timePlay(int p_actionCount)
  {
    List<AnEvent> logs = getGame().getLogs();
    boolean execAnimation = p_actionCount == 1;
    int totalEventCount = logs.size();
    if( GameEngine.model().getMyRegistration() != null )
    {
      totalEventCount += GameEngine.model().getMyRegistration().getTeam( getGame() ).getMyEvents()
          .size();
    }
    while( (m_currentActionIndex < totalEventCount) && (p_actionCount > 0) )
    {
      AnEvent action = null;
      if( m_currentActionIndex >= logs.size() && getMyRegistration() != null )
      {
        action = getMyRegistration().getTeam( getGame() ).getMyEvents()
            .get( m_currentActionIndex - logs.size() );
      }
      else
      {
        action = logs.get( m_currentActionIndex );
      }

      if( !(action instanceof EbAdmin) 
          && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // exec action
        try
        {
          action.exec( getGame() );
        } catch( RpcFmpException e )
        {
          logger.severe( e.getMessage() );
          Window.alert( "unexpected error : " + e );
          return;
        }
        // don't count automatic action as one action to play
        if( !action.isAuto() && action.getType() != GameLogType.EvtConstruct )
        {
          p_actionCount--;
        }
        // if next action is automatic, then exec too
        if( m_currentActionIndex<logs.size()-1 
            && logs.get( m_currentActionIndex+1 ).isAuto() )
        {
          p_actionCount++;
        }
        if( !(action instanceof EbEvtMessage) && execAnimation )
        {
          AppRoot.getEventBus().fireEvent( new GameActionEvent( action ) );
        }
      }
      m_currentActionIndex++;
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  /**
   * in time mode, play events up to the given one
   * @param p_actionCount
   */
  public void timePlay(AnEvent p_event)
  {
    if( !isTimeLineMode() || p_event == null || p_event.getIdGame() != getGame().getId() )
    {
      logger.warning( "time play command but wrong mode or wrong params" );
      return;
    }
    AnEvent currentEvent = getCurrentAction();
    boolean timeForward = false;
    if( currentEvent != null
        && currentEvent.getLastUpdate().getTime() < p_event.getLastUpdate().getTime() )
    {
      timeForward = true;
    }
    try
    {
      while( currentEvent != p_event )
      {
        // we assume playing event in same way during the whole method
        // this insure us not going in endless loop
        if( timeForward )
        {
          currentEvent.exec( getGame() );
          m_currentActionIndex++;
          currentEvent = getCurrentAction();
        }
        else
        {
          m_currentActionIndex--;
          currentEvent = getCurrentAction();
          if( !(currentEvent instanceof EbAdmin) && !(currentEvent instanceof EbGameJoin)
              && !(currentEvent instanceof EbEvtCancel) )
          {
            currentEvent.unexec( getGame() );
          }
        }
        if( m_currentActionIndex < 0 || m_currentActionIndex >= getGame().getLogs().size() )
        {
          logger.severe( "time play command but given event wasn't found" );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
          return;
        }
      }
    } catch( RpcFmpException e )
    {
      logger.severe( e.getMessage() );
      Window.alert( "unexpected error : " + e );
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
  }

  private boolean m_isAnimationPlaying = false;

  public boolean isAnimationPlaying()
  {
    return m_isAnimationPlaying;
  }

  public void startPlayAnimation()
  {
    if( isTimeLineMode() )
    {
      m_isAnimationPlaying = true;
      m_animationTimer.schedule( 1 );
    }
  }

  public void stopPlayAnimation()
  {
    m_isAnimationPlaying = false;
    m_animationTimer.cancel();
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
  }

  /**
   * in time line mode this timer exec all games events with animation
   */
  private Timer m_animationTimer = new Timer()
  {
    @Override
    public void run()
    {
      if( !isTimeLineMode() )
      {
        stopPlayAnimation();
        return;
      }
      AnEvent action = getCurrentAction();
      if( action == null )
      {
        stopPlayAnimation();
        return;
      }
      if( !(action instanceof EbAdmin) && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // exec action
        try
        {
          action.exec( getGame() );
        } catch( RpcFmpException e )
        {
          logger.severe( e.getMessage() );
          Window.alert( "unexpected error : " + e );
          stopPlayAnimation();
          return;
        }
        if( !(action instanceof EbEvtMessage) )
        {
          AppRoot.getEventBus().fireEvent( new GameActionEvent( action ) );
        }
      }
      m_currentActionIndex++;
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );

      // Schedule next event execution
      AnimEvent anim = AnimFactory.createAnimEvent( null, action );
      if( anim != null )
      {
        m_animationTimer.schedule( anim.getDurration() );
      }
      else
      {
        m_animationTimer.schedule( 1 );
      }
    }
  };


  public int getCurrentActionIndex()
  {
    return m_currentActionIndex;
  }

  public AnEvent getCurrentAction()
  {
    if( !model().isTimeLineMode() )
    {
      return getGame().getLastGameLog();
    }
    if( m_currentActionIndex < getGame().getLogs().size() )
    {
      return getGame().getLogs().get( m_currentActionIndex );
    }
    if( getMyRegistration() != null
        && m_currentActionIndex < getGame().getLogs().size()
            + getMyRegistration().getTeam( getGame() ).getMyEvents().size() )
    {
      return getMyRegistration().getTeam( getGame() ).getMyEvents()
          .get(
          m_currentActionIndex - getGame().getLogs().size() );
    }
    return null;
  }

  /**
   * User is allowed to cancel action if in puzzle or turn by turn on several day.
   * He must also be in his own turn.
   * @return true if user is allowed to cancel action up to 'getCurrentActionIndex()'
   */
  public boolean canCancelAction()
  {
    if( getGame().getGameType()==GameType.Puzzle )
    {
      return true;
    }
    if( getMyRegistration() == null )
    {
      return false;
    }
    // for training game, creator can always cancel game event
    if( getGame().getGameType() == GameType.Initiation
        && getMyRegistration().getAccount().getId() == getGame().getAccountCreator().getId() )
    {
      return true;
    }
    if( m_lastTurnPlayed != getGame().getCurrentTimeStep() )
    {
      return false;
    }
    if( getGame().isFinished() )
    {
      return false;
    }
    if( getGame().getConfigGameTime() == ConfigGameTime.StandardAsynch )
    {
      AnEvent event = null;
      for( int i = m_currentActionIndex; i < getGame().getLogs().size(); i++ )
      {
        event = getGame().getLogs().get( i );
        if( event == null || !(event instanceof AnEventPlay)
            || ((AnEventPlay)event).getAccountId() != AppMain.instance().getMyAccount().getId() )
        {
          return false;
        }
      }
      return true;
    }
    if( getGame().getCurrentPlayerIds().contains( getMyRegistration().getId() ) )
    {
      return true;
    }
    return false;
  }

  @Override
  public void onModuleLoad()
  {
    AppMain.instance().startLoading();
    restoreHMIFlags();

    String strModel = ClientUtil.getJSString( "fmp_model" );
    if( strModel != null )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( GameServices.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strModel );
        Object object = reader.readObject();
        if( object instanceof ModelFmpInit )
        {
          loadGameCallback.onSuccess( (ModelFmpInit)object );
        }
      } catch( SerializationException e )
      {
        AppRoot.logger.log( Level.WARNING, e.getMessage() );
      }
    }
    
    if( AppMain.instance().isLoading() )
    {
      // well, model init wasn't found in jsp => ask it with standard RPC call
      String gameId = ClientUtil.getUrlParameter( "id" ); 
      if( gameId != null )
      {
        AppMain.getRpcService().getModelFmpInit( gameId, loadGameCallback );
      }
      else
      {
        // load an empty game
        AppMain.instance().stopLoading();
      }
    }
  }




  /**
   * if game have additional events that are not contained by current model
   * load them with gwt RPC
   */
  public void loadAdditionalEvents()
  {
    if( getGame().getAdditionalEventCount() > 0 )
    {
      // callback
      FmpCallback<EbGameLog> callbackLoadAdditionalEvents = new FmpCallback<EbGameLog>()
      {
        @Override
        public void onSuccess(EbGameLog p_gameLog)
        {
          AppMain.instance().stopLoading();
          getGame().setAdditionalEventCount(
              getGame().getAdditionalEventCount() - p_gameLog.getLog().size() );
          m_currentActionIndex += p_gameLog.getLog().size();
          p_gameLog.getLog().addAll( getGame().getLogs() );
          getGame().setLogs( p_gameLog.getLog() );
          AppMain.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
        }
      };

      AppMain.instance().startLoading();
      AppMain.getRpcService().getAdditionalGameLog( getGame().getId(),
          callbackLoadAdditionalEvents );
    }
  }






}
