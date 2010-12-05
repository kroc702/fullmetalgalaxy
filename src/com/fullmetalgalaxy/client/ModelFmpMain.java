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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.client.board.DlgMessageEvent;
import com.fullmetalgalaxy.client.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ConnectedUser;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.ModelUpdateListenerCollection;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * @author Vincent Legendre
 *
 */
public class ModelFmpMain implements SourceModelUpdateEvents
{
  private static ModelFmpMain s_ModelFmpMain = new ModelFmpMain();

  /**
   * @return a unique instance of the model on client side
   */
  public static ModelFmpMain model()
  {
    return s_ModelFmpMain;
  }

  protected String m_gameId = null;
  protected EbGame m_game = new EbGame();

  /**
   * the list of all widget which will be refreshed after any model change. 
   */
  protected ModelUpdateListenerCollection m_listenerCollection = new ModelUpdateListenerCollection();

  protected GameFilter m_gameFilter = new GameFilter();

  protected EventsPlayBuilder m_actionBuilder = new EventsPlayBuilder();

  protected String m_myAccountLogin = null;
  protected String m_myAccountPseudo = null;
  protected long m_myAccountId = -1L;
  protected boolean m_myAccountAdmin = false;

  protected Date m_lastServerUpdate = new Date();

  // connected players (or any other peoples)
  protected Set<ConnectedUser> m_connectedUsers = new HashSet<ConnectedUser>();
  private Map<Long, EbAccount> m_accounts = null;


  // interface
  private boolean m_isGridDisplayed = false;

  private boolean m_isFireCoverDisplayed = false;

  private EnuZoom m_zoomDisplayed = new EnuZoom( EnuZoom.Medium );
  // cloud layer
  private boolean m_isAtmosphereDisplayed = true;
  // standard land layer or custom map image
  private boolean m_isCustomMapDisplayed = false;

  /**
   * minimap or players connections informations
   */
  private boolean m_isMiniMapDisplayed = true;

  /**
   * if set, user can't do anything else:
   * - navigate in past actions
   * - exiting this mode
   * - if game is puzzle or standard (turn by turn, no time limit) validate to cancel some actions
   */
  private boolean m_isTimeLineMode = false;
  private int m_currentActionIndex = 0;
  /** game currentTimeStep at the moment we start time line mode */
  private int m_lastTurnPlayed = 0;
  
  private boolean m_isModelUpdatePending = false;


  private int m_successiveRpcErrorCount = 0;


  protected Map<Long, EbAccount> getAccounts()
  {
    if( m_accounts == null )
    {
      m_accounts = new HashMap<Long, EbAccount>();
    }
    return m_accounts;
  }
  
  public EbAccount getAccount(long p_id)
  {
    
    EbAccount account = getAccounts().get( p_id );
    if( account == null )
    {
      account = new EbAccount();
      account.setLogin( "Unknown" );
    }
    return account;
  }
  
  public EbAccount getAccount(String p_pseudo)
  {
    for(Map.Entry<Long, EbAccount> account : getAccounts().entrySet())
    {
      if(account.getValue().getPseudo() != null && account.getValue().getPseudo().equals( p_pseudo ))
      {
        return account.getValue();
      }
    }
    return null;
  }
  
  /**
   * 
   * @param p_accounts
   * @return true if at least one account was added
   */
  public boolean addAllAccounts(Map<Long, EbAccount> p_accounts)
  {
    boolean added = false;
    // add all new accounts
    //
    if( m_accounts == null )
    {
      m_accounts = p_accounts;
      added = true;
    }
    else
    {
      for( Map.Entry<Long, EbAccount> entry : p_accounts.entrySet() )
      {
        if( !m_accounts.containsKey( entry.getKey() ) )
        {
          m_accounts.put( entry.getKey(), entry.getValue() );
          added = true;
        }
      }
    }
    return added;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.SourceModelUpdateEvents#subscribeModelUpdateEvent(com.fullmetalgalaxy.client.ModelUpdateListener)
   */
  @Override
  public void subscribeModelUpdateEvent(ModelUpdateListener p_listener)
  {
    m_listenerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.SourceModelUpdateEvents#removeModelUpdateEvent(com.fullmetalgalaxy.model.ModelUpdateListener)
   */
  @Override
  public void removeModelUpdateEvent(ModelUpdateListener p_listener)
  {
    m_listenerCollection.remove( p_listener );
  }

  /**
   * This method call 'notifyModelUpdate()' of all childs and father controller.
   * You should call this method every time the model associated with this controller is updated.
   * the sub method should first call 'super.notifyModelUpdate()'
   */
  public void notifyModelUpdate()
  {
    // setLastUpdate( new Date() );
    fireModelUpdate();
  }

  public void fireModelUpdate()
  {
    m_listenerCollection.fireModelUpdate( this );
  }

  /**
   * 
   */
  public ModelFmpMain()
  {
    getActionBuilder().setAccountId( getMyAccountId() );
  }

  public void load(EbGame p_model)
  {
    if( p_model==null )
    {
      // TODO i18n
      Window.alert( "Partie non trouve...\nVerifier l'url, mais il ce peut qu'elle ai ete suprime" );
      return;
    }
    m_updatePeriodInMS = 0;
    m_game = p_model;
    getActionBuilder().setGame( getGame() );
    m_lastServerUpdate = getGame().getLastServerUpdate();
    notifyModelUpdate();
    if( p_model.getGameType() != GameType.MultiPlayer )
    {
      LocalGame.loadGame( m_callbackFmpUpdate );
    }
    scheduleUpdateTimer( true );
  }

  /**
   * @return the gameFilter
   */
  public GameFilter getGameFilter()
  {
    return m_gameFilter;
  }

  public void setGameFilter(GameFilter p_filter)
  {
    m_gameFilter = p_filter;
  }

  public EbRegistration getMyRegistration()
  {
    if( getGame().getGameType() == GameType.Puzzle )
    {
      return getGame().getCurrentPlayerRegistration();
    }
    if( !isLogged() )
    {
      return null;
    }
    for( Iterator<EbRegistration> it = getGame().getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.getAccountId() == getMyAccountId() )
      {
        return registration;
      }
    }
    return null;
  }


  private void loadAccountInfoFromPage()
  {
    RootPanel panel = RootPanel.get( "fmp_userlogin" );
    if( panel == null )
    {
      m_myAccountLogin = "";
    }
    else
    {
      m_myAccountLogin = DOM.getElementAttribute( panel.getElement(), "content" );
    }
    panel = RootPanel.get( "fmp_userpseudo" );
    if( panel == null )
    {
      m_myAccountPseudo = "";
    }
    else
    {
      m_myAccountPseudo = DOM.getElementAttribute( panel.getElement(), "content" );
    }
    panel = RootPanel.get( "fmp_userid" );
    if( panel == null )
    {
      m_myAccountId = 0;
    }
    else
    {
      m_myAccountId = Long.parseLong( DOM.getElementAttribute( panel.getElement(), "content" ) );
    }
    panel = RootPanel.get( "fmp_useradmin" );
    if( panel == null )
    {
      m_myAccountAdmin = false;
    }
    else
    {
      m_myAccountAdmin = true;
    }

  }

  /**
   * @return the myAccount
   */
  public long getMyAccountId()
  {
    if( m_myAccountId == -1 )
    {
      loadAccountInfoFromPage();
    }
    return m_myAccountId;
  }

  public boolean iAmAdmin()
  {
    if( m_myAccountId == -1 )
    {
      loadAccountInfoFromPage();
    }
    return m_myAccountAdmin;
  }

  public String getMyPseudo()
  {
    if( m_myAccountPseudo == null )
    {
      loadAccountInfoFromPage();
    }
    return m_myAccountPseudo;
  }

  public String getMyLogin()
  {
    if( m_myAccountLogin == null )
    {
      loadAccountInfoFromPage();
    }
    return m_myAccountLogin;
  }



  public boolean isLogged()
  {
    return getMyAccountId() != 0;
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
    m_game = new EbGame();
    m_gameId = null;
    notifyModelUpdate();
  }

  public EbGame getGame()
  {
    return m_game;
  }

  public void setGameId(String p_id)
  {
    m_gameId = p_id;
  }

  public String getGameId()
  {
    if( m_gameId == null )
    {
      return "" + getGame().getId();
    }
    return m_gameId;
  }


  private boolean m_isActionPending = false;

  private FmpCallback<ModelFmpUpdate> m_callbackEvents = new FmpCallback<ModelFmpUpdate>()
  {
    @Override
    public void onSuccess(ModelFmpUpdate p_result)
    {
      super.onSuccess( p_result );
      m_successiveRpcErrorCount = 0;
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().clear();
      m_callbackFmpUpdate.onSuccess( p_result );
    }

    @Override
    public void onFailure(Throwable p_caught)
    {
      m_successiveRpcErrorCount++;
      //super.onFailure( p_caught );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
      m_callbackFmpUpdate.onFailure( p_caught );
      // maybe the action failed because the model isn't up to date
      if( m_successiveRpcErrorCount < 2 )
      {
        serverUpdate();
      }
      else
      {
        scheduleUpdateTimer( true );
      }
    }
  };

  public FmpCallback<ModelFmpUpdate> getCallbackEvents()
  {
    return m_callbackEvents;
  }

  private FmpCallback<ModelFmpUpdate> m_callbackFmpUpdate = new FmpCallback<ModelFmpUpdate>()
  {
    @Override
    public void onSuccess(ModelFmpUpdate p_result)
    {
      boolean isActive = false;
      m_successiveRpcErrorCount = 0;
      m_isModelUpdatePending = false;

      if( p_result == null )
      {
        if( m_connectedUsers != null && m_connectedUsers.size() > 1 )
        {
          isActive = true;
        }
        scheduleUpdateTimer( isActive );
        return;
      }
      if( m_isActionPending )
      {
        RpcUtil.logDebug( "model update while action is pending, ignore it." );
        return;
      }

      try
      {
        super.onSuccess( p_result );

        if( getLastServerUpdate().after( p_result.getFromUpdate() ) )
        {
          // this is probably because we send an action while a get update was
          // pending
          RpcUtil.logDebug( "model update 'from' is after 'lastUpdate', ignore it..." );
          scheduleUpdateTimer( isActive );
          return;
        }

        // add all new accounts
        //
        addAllAccounts( p_result.getMapAccounts() );
        if( p_result.getMapAccounts().size() > 1 )
        {
          isActive = true;
        }

        // handle game events first
        //
        List<AnEvent> events = p_result.getGameEvents();
        for( AnEvent event : events )
        {
          if( event.getType() == GameLogType.EvtMessage )
          {
            DlgMessageEvent dlgMsg = new DlgMessageEvent( (EbEvtMessage)event );
            dlgMsg.center();
            dlgMsg.show();
          }
          if( getGame() != null )
          {
            if( event.getType() == GameLogType.EvtCancel )
            {
              ((EbEvtCancel)event).execCancel( getGame() );
            }
            
            event.exec( getGame() );
            // getGame().getLastUpdate().setTime(
            // event.getLastUpdate().getTime() );
            if( event.getType() != GameLogType.EvtCancel )
            {
              getGame().addEvent( event );
            }
            getGame().getLastServerUpdate().setTime( event.getLastUpdate().getTime() );
            getGame().updateLastTokenUpdate( null );
          }
          isActive = true;
        }

        // handle chat messages
        //
        if( p_result.getChatMessages() != null )
        {
          for( ChatMessage message : p_result.getChatMessages() )
          {
            MAppMessagesStack.s_instance.showMessage( message.getFromLogin() + " : "
                + message.getText() );
            isActive = true;
          }
        }

        // handle connected player
        // 
        if( p_result.getConnectedUsers() != null )
        {
          m_connectedUsers = p_result.getConnectedUsers();
          if( m_connectedUsers.size() > 1 )
          {
            isActive = true;
          }
          for( ConnectedUser connectedUser : m_connectedUsers )
          {
            if( connectedUser.getEndTurnDate() != null )
            {
              // update end turn date
              EbRegistration registration = getGame().getRegistrationByIdAccount(
                  getAccount( connectedUser.getPseudo() ).getId() );
              if( registration != null )
              {
                registration.setEndTurnDate( connectedUser.getEndTurnDate() );
              }
            }
          }
        }

        // last but not least... refresh general last server update
        //
        getLastServerUpdate().setTime( p_result.getLastUpdate().getTime() );

        // if( !updates.getGameEvents().isEmpty() ||
        // !updates.getChatMessages().isEmpty() )
        // {
        // assume that if we receive an update, something has changed !
        ModelFmpMain.model().fireModelUpdate();
        // }
      } catch( Throwable e )
      {
        RpcUtil.logError( "error ", e );
        Window.alert( "unexpected error : " + e );
      }
      // we just receive a model update, schedule next update later
      scheduleUpdateTimer( isActive );
    }

    @Override
    public void onFailure(Throwable p_caught)
    {
      m_isModelUpdatePending = false;
      m_successiveRpcErrorCount++;
      super.onFailure( p_caught );
      // maybe the action failed because the model isn't up to date
      if( m_successiveRpcErrorCount < 2 )
      {
        serverUpdate();
      }
      else
      {
        scheduleUpdateTimer( true );
      }
    }
  };


  public Date getLastServerUpdate()
  {
    return m_lastServerUpdate;
  }

  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void runSingleAction(AnEvent p_action)
  {
    if( m_isActionPending )
    {
      Window.alert( "Une action a déjà été envoyé au serveur... sans réponse pour l'instant" );
      return;
    }
    m_isActionPending = true;
    m_updateTimer.cancel();
    AppMain.instance().startLoading();

    try
    {
      if( !ModelFmpMain.model().isLogged() && getGame().getGameType() == GameType.MultiPlayer )
      {
        // TODO i18n ???
        throw new RpcFmpException( "You must be logged to do this action" );
      }
      // do not check player is logged to let him join action
      // action.check();
      cancelUpdateTimer();
      if( getGame().getGameType() == GameType.MultiPlayer )
      {
        Services.Util.getInstance().runEvent( p_action, getLastServerUpdate(), m_callbackEvents );
      }
      else
      {
        LocalGame.runEvent( p_action, getGame().getLastServerUpdate(), m_callbackEvents );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( Messages.getString( ex ) );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_isActionPending = false;
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
    if( m_isActionPending )
    {
      Window.alert( "Une action a d�j� �t� envoy� au serveur... sans r�ponse pour l'instant" );
      return;
    }
    m_isActionPending = true;
    m_updateTimer.cancel();
    AppMain.instance().startLoading();

    try
    {
      if( !ModelFmpMain.model().isJoined() )
      {
        // no i18n ?
        throw new RpcFmpException( "you didn't join this game." );
      }
      // action.check();
      cancelUpdateTimer();
      getActionBuilder().unexec();
      if( getGame().getGameType() == GameType.MultiPlayer )
      {
        Services.Util.getInstance().runAction( getActionBuilder().getActionList(),
            getLastServerUpdate(), m_callbackEvents );
      }
      else
      {
        LocalGame.runAction( getActionBuilder().getActionList(), getGame().getLastServerUpdate(),
            m_callbackEvents );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( Messages.getString( ex ) );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      scheduleUpdateTimer( true );
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
      scheduleUpdateTimer( true );
    }
  }

  public boolean isModelUpdatePending()
  {
    return m_isModelUpdatePending;
  }

  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void endTurn()
  {
    EbEvtPlayerTurn action = new EbEvtPlayerTurn();
    action.setAccountId( model().getMyAccountId() );
    action.setGame( model().getGame() );
    runSingleAction( action );
  }


  private int m_updatePeriodInMS = 0;

  private int getUpdatePeriodInMS()
  {
    if( ModelFmpMain.model().getGame().getGameType() == GameType.Puzzle )
    {
      return FmpConstant.localResfreshingPeriod * 1000;
    }
    // since comet stuff
    // return 4000;
    if( m_updatePeriodInMS <= 0 )
    {
      // compute period in second
      m_updatePeriodInMS = ModelFmpMain.model().getGame().getEbConfigGameTime()
          .getTimeStepDurationInSec()
          / ModelFmpMain.model().getGame().getEbConfigGameTime().getActionPtPerTimeStep();
      if( m_updatePeriodInMS < FmpConstant.minimumResfreshingPeriod )
      {
        m_updatePeriodInMS = FmpConstant.minimumResfreshingPeriod;
      }
      if( m_updatePeriodInMS > FmpConstant.maximumResfreshingPeriod
          || ModelFmpMain.model().getGame().getEbConfigGameTime().getTimeStepDurationInSec() == 0 )
      {
        m_updatePeriodInMS = FmpConstant.maximumResfreshingPeriod;
      }
      if( getGame().isFinished() )
      {
        m_updatePeriodInMS = FmpConstant.maximumResfreshingPeriod;
      }
      // then transform in milisecond
      m_updatePeriodInMS *= 1000;
    }
    return m_updatePeriodInMS;
  }

  /**
   * 
   * after this call, the timer is scheduled to refresh permanently
   */
  public void setUpdatePeriod2Minimum()
  {
    m_updatePeriodInMS = FmpConstant.minimumResfreshingPeriod * 1000;
    cancelUpdateTimer();
    m_updateTimer.run();
  }

  public boolean isUpdatePeriod2Minimum()
  {
    return m_updatePeriodInMS == FmpConstant.minimumResfreshingPeriod * 1000;
  }

  /**
   * Create a new timer that calls Window.alert().
   */
  private Timer m_updateTimer = new Timer()
  {
    @Override
    public void run()
    {
      cancelUpdateTimer();
      serverUpdate();
    }
  };


  protected void scheduleUpdateTimer(boolean p_isActive)
  {
    cancelUpdateTimer();
    if( (getGame().getCurrentPlayerRegistration() != null)
        && (getGame().getCurrentPlayerRegistration().getEndTurnDate() != null)
        && (getGame().getCurrentPlayerRegistration().getEndTurnDate().getTime()
            - System.currentTimeMillis() > 0)
        && (getGame().getCurrentPlayerRegistration().getEndTurnDate().getTime()
            - System.currentTimeMillis() < getUpdatePeriodInMS()) )
    {
      m_updateTimer.schedule( (int)(getGame().getCurrentPlayerRegistration().getEndTurnDate()
          .getTime() - System.currentTimeMillis()) );
    }
    else
    // if( p_isActive )
    {
      m_updateTimer.schedule( getUpdatePeriodInMS() );
    }
    /*else
    {
      m_updateTimer.schedule( FmpConstant.inactiveResfreshingPeriod * 1000 );
    }*/
  }

  private void cancelUpdateTimer()
  {
    m_updateTimer.cancel();
  }


  /**
   * rpc call to update the model from server.
   * after this call, the timer is scheduled to refresh permanently
   */
  protected void serverUpdate()
  {
    cancelUpdateTimer();
    // if player just send an action to server, cancel this update as it will
    // come with action response
    if( m_isActionPending )
    {
      return;
    }
    // if player is constructing an action, wait
    if( !getActionList().isEmpty() )
    {
      m_updateTimer.schedule( 4 * 1000 );
      return;
    }
    // then call the right service
    if( getGame().getGameType() == GameType.MultiPlayer )
    {
      Services.Util.getInstance().getModelFmpUpdate( ModelFmpMain.model().getGame().getId(),
          getLastServerUpdate(), m_callbackFmpUpdate );
    }
    else
    {
      LocalGame.modelUpdate( ModelFmpMain.model().getGame().getId(), getLastServerUpdate(),
          m_callbackFmpUpdate );
    }
    m_isModelUpdatePending = true;
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
    fireModelUpdate();
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
    fireModelUpdate();
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
    fireModelUpdate();
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
    fireModelUpdate();
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
    m_zoomDisplayed = new EnuZoom( p_zoomValueDisplayed );
    fireModelUpdate();
  }

  public void setZoomDisplayed(EnuZoom p_zoomDisplayed)
  {
    m_zoomDisplayed = p_zoomDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isMiniMapDisplayed
   */
  public boolean isMiniMapDisplayed()
  {
    return m_isMiniMapDisplayed;
  }

  /**
   * @param p_isMiniMapDisplayed the isMiniMapDisplayed to set
   */
  public void setMiniMapDisplayed(boolean p_isMiniMapDisplayed)
  {
    m_isMiniMapDisplayed = p_isMiniMapDisplayed;
    fireModelUpdate();
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
    m_isTimeLineMode = p_isTimeLineMode;
    m_lastTurnPlayed = getGame().getCurrentTimeStep();
    if( !p_isTimeLineMode )
    {
      timePlay( 99999 );
    }
    m_currentActionIndex = getGame().getLogs().size();
    fireModelUpdate();
  }

  public void timeBack(int p_actionCount)
  {
    List<AnEvent> logs = getGame().getLogs();
    while( (m_currentActionIndex > 0) && (p_actionCount > 0) )
    {
      m_currentActionIndex--;
      AnEvent action = logs.get( m_currentActionIndex );
      if( !(action instanceof EbAdmin) 
          && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // unexec action
        try
        {
          logs.get( m_currentActionIndex ).unexec( getGame() );
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
          Window.alert( "unexpected error : " + e );
          fireModelUpdate();
          return;
        }
        // don't count automatic action as one action to play
        if( !action.isAuto() )
        {
          p_actionCount--;
        }
        // if previous action is EvtConstruct, then unexec too
        if( m_currentActionIndex>0 
            && logs.get( m_currentActionIndex-1 ).getType() == GameLogType.EvtConstruct )
        {
          p_actionCount++;
        }
      }
    }
    fireModelUpdate();
  }


  public void timePlay(int p_actionCount)
  {
    List<AnEvent> logs = getGame().getLogs();
    while( (m_currentActionIndex < logs.size()) && (p_actionCount > 0) )
    {
      AnEvent action = logs.get( m_currentActionIndex );
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
          RpcUtil.logError( "error ", e );
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
      }
      m_currentActionIndex++;
    }
    fireModelUpdate();
  }

  public int getCurrentActionIndex()
  {
    return m_currentActionIndex;
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
    if( iAmAdmin() )
    {
      return true;
    }
    if( getMyRegistration() == null )
    {
      return false;
    }
    if( getGame().getConfigGameTime()!=ConfigGameTime.Standard )
    {
      return false;
    }
    if( m_lastTurnPlayed != getGame().getCurrentTimeStep() )
    {
      return false;
    }
    if( getMyRegistration() != getGame().getCurrentPlayerRegistration())
    {
      return false;
    }
    return true;
  }
  
  /**
   * @return the connectedPlayer
   */
  public Set<ConnectedUser> getConnectedUsers()
  {
    return m_connectedUsers;
  }

  public boolean isUserConnected(String p_pseudo)
  {
    assert p_pseudo != null;
    for( ConnectedUser connectedUser : getConnectedUsers() )
    {
      if( p_pseudo.equals( connectedUser.getPseudo() ) )
      {
        return true;
      }
    }
    return false;
  }

  public boolean isUserConnected(long p_accountId)
  {
    return isUserConnected( model().getAccount( p_accountId ).getPseudo() );
  }


}
