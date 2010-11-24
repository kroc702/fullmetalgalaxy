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
package com.fullmetalgalaxy.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbRegistrationStats;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtChangePlayerOrder;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTimeStep;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * TODO create only one dataStore per RPC request
 * @author Vincent
 *
 */
public class ServicesImpl extends RemoteServiceServlet implements Services
{
  public static final long serialVersionUID = 1;
  private final static FmpLogger log = FmpLogger.getLogger( ServicesImpl.class.getName() );
  private static String s_basePath = null;

  /**
   * constructor: 
   */
  public ServicesImpl()
  {
    super();
  }

  /**
   * servlet initialisation
   * initialize SGBD connexion.
   */
  @Override
  public void init() throws ServletException
  {
    super.init();
    if( s_basePath == null )
    {
      s_basePath = getServletContext().getRealPath( "/" );
    }
  }




  public EbBase saveGame(EbGame p_game) throws RpcFmpException
  {
    return saveGame( p_game, null );
  }

  /* (non-Javadoc)
  * @see com.fullmetalgalaxy.model.GameCreationServices#createGame(com.fullmetalgalaxy.model.DbbGame)
  */
  public EbBase saveGame(EbGame p_game, String p_modifDesc) throws RpcFmpException
  {
    if( !isLogged() )
    {
      throw new RpcFmpException(
          "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
    }
    FmgDataStore dataStore = new FmgDataStore();
    if( !p_game.isTrancient() )
    {
      // then add an admin event
      if( p_modifDesc == null || p_modifDesc.trim().length() == 0 )
      {
        p_modifDesc = "Unknown edition event";
      }
      EbAdmin adminEvent = new EbAdmin();
      adminEvent.setGame( p_game );
      adminEvent.setMessage( p_modifDesc );
      EbAccount account = Auth.getUserAccount( getThreadLocalRequest(), getThreadLocalResponse() );
      if( (!Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() ))
          && !(p_game.getAccountCreatorId() != account.getId()) )
      {
        throw new RpcFmpException(
            "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
      }
      adminEvent.setAccountId( account.getId() );
      p_game.addEvent( adminEvent );
    }

    dataStore.save( p_game );
    dataStore.close();


    return p_game.createEbBase();

  }


  static protected EbGame getEbGame(long p_gameId)
  {
    FmgDataStore dataStore = new FmgDataStore();
    EbGame model = dataStore.getGame( p_gameId );
    boolean isUpdated = false;

    if( model == null )
    {
      return null;
    }

    Date lastUpdate = new Date( System.currentTimeMillis() );
    if( model.getLastGameLog() != null )
    {
      lastUpdate = model.getLastGameLog().getLastUpdate();
    }

    try
    {
      isUpdated = updateGame( model );
    } catch( RpcFmpException e )
    {
      log.error( e );
    }

    if( model.getGameType() != GameType.MultiPlayer )
    {
      // model.setLastUpdate( new Date( System.currentTimeMillis() ) );
      model.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    }
    if( isUpdated )
    {
      dataStore.save( model );
      FmpUpdateStatus.broadCastGameUpdate( model );

      // do we need to send an email ?
      ModelFmpUpdate modelUpdate = new ModelFmpUpdate( model, lastUpdate );
      FmpUpdateStatus.loadAllAccounts( modelUpdate.getMapAccounts(), model );
      sendMail( model, modelUpdate );
    }
    dataStore.close();
    return model;
  }

  public static ModelFmpInit sgetModelFmpInit(String p_gameId)
  {
    long gameId = 0;
    try
    {
      gameId = Long.parseLong( p_gameId );
    } catch( NumberFormatException e )
    {
    }
    ModelFmpInit modelInit = new ModelFmpInit();
    EbGame game = null;
    if( gameId == 0 )
    {
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try
      {
        if( s_basePath != null )
        {
          fis = new FileInputStream( new File( s_basePath + p_gameId ) );
          in = new ObjectInputStream( fis );
          modelInit = ModelFmpInit.class.cast( in.readObject() );
          in.close();
          fis.close();
        }
      } catch( Exception ex )
      {
        ex.printStackTrace();
      }
    }
    else
    {
      game = getEbGame( gameId );
      if( game != null )
      {
        modelInit.setGame( game );
        FmpUpdateStatus.loadAllAccounts( modelInit.getMapAccounts(), game );
      }
    }

    return modelInit;
  }


  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException
  {
    ModelFmpInit modelInit = sgetModelFmpInit( p_gameId );

    if( modelInit.getGame() != null && modelInit.getGame().getId() != 0 )
    {
      ModelFmpUpdate modelUpdate = FmpUpdateStatus.getModelUpdate( Auth.getUserPseudo(
          getThreadLocalRequest(), getThreadLocalResponse() ), modelInit.getGame().getId(), null );
      modelInit.setConnectedUsers( modelUpdate.getConnectedUsers() );
    }
    return modelInit;
  }



  private boolean isLogged()
  {
    return Auth.isUserLogged( getThreadLocalRequest(), getThreadLocalResponse() );
  }


  @Override
  public ModelFmpUpdate getModelFmpUpdate(long p_gameId, Date p_lastUpdate) throws RpcFmpException
  {
    assert p_lastUpdate != null;
    assert p_gameId != 0;
    return FmpUpdateStatus.waitForModelUpdate( Auth.getUserPseudo( getThreadLocalRequest(),
        getThreadLocalResponse() ), p_gameId, p_lastUpdate );
  }



  /**
   * eventually send an email to people that need it.
   * This method have to be called after p_action have been successfully ran.
   * @param action
   */
  protected static void sendMail(EbGame p_game, ModelFmpUpdate p_update)
  {
    for( AnEvent action : p_update.getGameEvents() )
    {
      if( (action instanceof EbAdminTimePlay) || (action instanceof EbEvtPlayerTurn) )
      {
        if( p_update.getConnectedUser( p_game.getCurrentPlayerRegistration().getAccountId() ) != null )
        {
          log.fine( "player is connected: we don't need to send an email" );
          return;
        }
        EbAccount currentPlayer = p_update.getMapAccounts().get(
            p_game.getCurrentPlayerRegistration().getAccountId() );
        if( currentPlayer == null )
        {
          log.error( "New turn email couldn't be send" );
          return;
        }
        if( !currentPlayer.isAllowMailFromGame() )
        {
          // player don't want any notification
          log.fine( "player " + currentPlayer.getPseudo() + " don't want any notification" );
          return;
        }
        String subject = "FMG: Notification de tour de jeux sur " + p_game.getName();
        String body = "Bonjour " + currentPlayer.getPseudo() + "\n\n"
            + "Vous pouvez des a present vous connecter a la partie " + p_game.getName()
            + " http://www.fullmetalgalaxy.com/game.jsp?id=" + p_game.getId()
            + " pour jouer votre tour " + p_game.getCurrentTimeStep() + ".\n";
        PMServlet.sendMail( subject, body, currentPlayer.getEmail() );
        return;
      }
    }
  }

  @Override
  public ModelFmpUpdate runEvent(AnEvent p_action, Date p_lastUpdate) throws RpcFmpException
  {
    ModelFmpUpdate modelUpdate = null;
    FmgDataStore dataStore = new FmgDataStore();
    EbGame game = dataStore.getGame( p_action.getIdGame() );

    // security check
    /*if( p_action.getAccountId() != 0L )
    {
      EbAccount account = askForIdentity();
      if( account.getId() != p_action.getAccountId() )
      {
        throw new RpcFmpException( "account " + account.getLogin() + "(" + account.getId()
            + ") thief account " + p_action.getAccountId() );
      }
    }*/

    
    if(p_action.getType() == GameLogType.EvtCancel)
    {
      // cancel action doesn't work in exact same way as other event
      p_action.setLastUpdate( ServerUtil.currentDate() );
      ((EbEvtCancel)p_action).execCancel( game );
      
      updateGame( game );
    }
    else
    {
      updateGame( game );
      p_action.setLastUpdate( ServerUtil.currentDate() );
  
      // execute action
      p_action.checkedExec( game );
      game.addEvent( p_action );
    }
    
    // save all events. This action is required as game->events relation isn't
    // a real bidirectional relation (because of event_index column)
    if( p_action.getType().isEventUser() )
    {
      ((AnEventUser)p_action).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
      if( p_action.getType() == GameLogType.GameJoin )
      {
        EbRegistration registration = game.getRegistrationByIdAccount( ((AnEventUser)p_action)
            .getAccountId() );
        String myPseudo = Auth.getUserPseudo( getThreadLocalRequest(), getThreadLocalResponse() );
        if( registration != null && !myPseudo.equals( registration.getAccountPseudo() ) )
        {
          registration.setAccountPseudo( myPseudo );
        }
      }
    }

    if( (p_action.getType() == GameLogType.EvtPlayerTurn)
        && (game.getCurrentPlayerRegistration().getOrderIndex() == 0) )
    {
      // new turn !
      if( game.getNextTideChangeTimeStep() <= game.getCurrentTimeStep() )
      {
        EbEvtTide eventTide = new EbEvtTide();
        eventTide.setNextTide( Tide.getRandom() );
        eventTide.setGame( game );
        eventTide.checkedExec( game );
        game.addEvent( eventTide );
      }
    }

    dataStore.save( game );

    // Some special cases
    /////////////////////
    if( p_action.getType() == GameLogType.GameJoin )
    {
      // in case of join event, we must load corresponding account
      ModelFmpUpdate updates = FmpUpdateStatus.getModelUpdate( Auth.getUserPseudo(
          getThreadLocalRequest(), getThreadLocalResponse() ), p_action.getIdGame(), null );
      FmpUpdateStatus.loadAllAccounts( updates.getMapAccounts(), game );
      // set pseudo into registration
      EbRegistration registration = game.getRegistrationByIdAccount( ((EbGameJoin)p_action).getAccountId() );
      registration.setAccountPseudo( updates.getMapAccounts().get( ((EbGameJoin)p_action).getAccountId() )
          .getPseudo() );

      FmpUpdateStatus.broadCastGameUpdate( updates );

      if( game.getCurrentNumberOfRegiteredPlayer() == game.getMaxNumberOfPlayer() )
      {
        // if the last player is just connected, automatically launch the game.
        EbAdminTimePlay action = new EbAdminTimePlay();
        action.setAuto( true );
        action.setLastUpdate( ServerUtil.currentDate() );
        action.setAccountId( ((EbGameJoin)p_action).getAccountId() );
        action.setGame( game );
        action.checkedExec( game );
        game.addEvent( action );
      }
    }
    if(game.getCurrentTimeStep() == 0 
        && game.getLastGameLog().getType() == GameLogType.AdminTimePlay
        && !game.isAsynchron() )
    {
      // game is starting
      EbEvtChangePlayerOrder action = new EbEvtChangePlayerOrder();
      action.setLastUpdate( ServerUtil.currentDate() );
      action.initRandomOrder( game );
      action.setGame( game );
      action.checkedExec( game );      
      game.addEvent( action );
    }
    if(game.getCurrentTimeStep() == 1 
        && !game.isAsynchron()
        && game.getLastGameLog().getType() == GameLogType.EvtTide )
    {
      // second turn: everybody should be landed
      EbEvtChangePlayerOrder action = new EbEvtChangePlayerOrder();
      action.setLastUpdate( ServerUtil.currentDate() );
      action.initBoardOrder( game );
      action.setGame( game );
      action.checkedExec( game );      
      game.addEvent( action );
    }
    
    
    dataStore.save( game );
    dataStore.close();

    // comet stuff
    // //////////////
    FmpUpdateStatus.broadCastGameUpdate( game );

    // return model update since the last client version
    if( modelUpdate == null )
    {
      modelUpdate = FmpUpdateStatus.getModelUpdate( Auth.getUserPseudo( getThreadLocalRequest(),
          getThreadLocalResponse() ), game.getId(), p_lastUpdate );
    }

    // do we need to send an email ?
    sendMail( game, modelUpdate );

    return modelUpdate;
  }

  @Override
  public ModelFmpUpdate runAction(ArrayList<AnEventPlay> p_actionList, Date p_lastUpdate)
      throws RpcFmpException
  {
    if( p_actionList.isEmpty() )
    {
      throw new RpcFmpException( "No actions provided" );
    }
    FmgDataStore dataStore = new FmgDataStore();
    EbGame game = null;

    for( AnEvent action : p_actionList )
    {
      if( !action.getType().isEventUser() )
      {
        throw new RpcFmpException( "This method can only play user action" );
      }

      if( game == null )
      {
        game = dataStore.getGame( action.getIdGame() );
      }
      else
      {
        assert game.getId() == action.getIdGame();
      }
      // security check TODO ?
    }
    assert game != null;

    updateGame( game );

    // execute actions
    for( AnEvent action : p_actionList )
    {
      ((AnEventUser)action).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
      action.setLastUpdate( ServerUtil.currentDate() );
      action.checkedExec( game );
      game.addEvent( action );
    }

    dataStore.save( game );
    dataStore.close();

    // comet stuff
    // //////////////
    FmpUpdateStatus.broadCastGameUpdate( game );

    ModelFmpUpdate modelUpdate = FmpUpdateStatus.getModelUpdate( Auth.getUserPseudo(
        getThreadLocalRequest(), getThreadLocalResponse() ), game.getId(), p_lastUpdate );
    // do we need to send an email ?
    sendMail( game, modelUpdate );

    // return model update since the last client version
    return modelUpdate;
  }


  /**
   * update anything on game which can change without user intervention. 
   * ie:<br/>
   * - player actions pts (for asynchron game only)<br/>
   * - tides (for asynchron game only)<br/>
   * - current player's turn. (for turn by turn game only)<br/>
   * - any other defined triggers
   * - set history flag and compute stats
   * @param p_game
   * @return the getResultList of all event which occur during this update.
   */
  static protected boolean updateGame(EbGame p_game) throws RpcFmpException
  {
    assert p_game != null;
    boolean isUpdated = false;
    if( !p_game.isStarted() || p_game.isHistory() || p_game.getGameType() != GameType.MultiPlayer )
    {
      return isUpdated;
    }
    if( !p_game.isFinished() )
    {
      if( p_game.isAsynchron() )
      {
        long currentTimeInMiliSec = System.currentTimeMillis();
        while( (!p_game.isFinished())
            && ((currentTimeInMiliSec - p_game.getLastTimeStepChange().getTime()) > p_game
                .getEbConfigGameTime().getTimeStepDurationInMili()) )
        {
          EbEvtTimeStep event = new EbEvtTimeStep();
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          // p_session.persist( event );
          if( p_game.getNextTideChangeTimeStep() >= p_game.getCurrentTimeStep() )
          {
            EbEvtTide eventTide = new EbEvtTide();
            eventTide.setNextTide( Tide.getRandom() );
            eventTide.setGame( p_game );
            eventTide.checkedExec( p_game );
            p_game.addEvent( eventTide );
            // p_session.persist( eventTide );
          }
          isUpdated = true;
        }
      }
      else if( p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
      {
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate().getTime() < System
                .currentTimeMillis())
            && (p_game.getCurrentTimeStep() != 0) ) // never skip first turn
        {
          // change player's turn
          int oldPlayerOrderIndex = p_game.getCurrentPlayerRegistration().getOrderIndex();
          EbEvtPlayerTurn event = new EbEvtPlayerTurn();
          event.setAuto( true );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          // p_session.persist( event );
          if( p_game.getCurrentPlayerRegistration().getOrderIndex() <= oldPlayerOrderIndex )
          {
            // new turn !
            if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
            {
              EbEvtTide eventTide = new EbEvtTide();
              eventTide.setGame( p_game );
              eventTide.setNextTide( Tide.getRandom() );
              eventTide.checkedExec( p_game );
              // p_session.persist( eventTide );
            }
          }
          isUpdated = true;
        }
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() == null) )
        {
          ModelFmpUpdate updates = FmpUpdateStatus.getModelUpdate( null, p_game.getId(), null );
          if( updates.getConnectedUser( p_game.getCurrentPlayerRegistration().getAccountId() ) != null )
          {
            // current player is connected, update his end turn
            p_game.getCurrentPlayerRegistration().setEndTurnDate(
                new Date( System.currentTimeMillis()
                    + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
            // p_session.persist( p_game.getCurrentPlayerRegistration() );
            updates.getConnectedUser( p_game.getCurrentPlayerRegistration().getAccountId() )
                .setEndTurnDate(
                    new Date( System.currentTimeMillis()
                        + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );

            isUpdated = true;
          }
        }
      }

      // triggers
      p_game.execTriggers();
    }
    else if( !p_game.isHistory() )
    {
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        registration.setStats( EbRegistrationStats.generate( registration, p_game ) );
        // p_session.persist( registration.getStats() );
      }
      p_game.setHistory( true );
      isUpdated = true;
    }

    return isUpdated;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.Services#sendChatMessage(com.fullmetalgalaxy.model.ChatMessage)
   */
  @Override
  public ModelFmpUpdate sendChatMessage(ChatMessage p_message, Date p_lastUpdate)
      throws RpcFmpException
  {
    p_message
        .setFromLogin( Auth.getUserPseudo( getThreadLocalRequest(), getThreadLocalResponse() ) );
    p_message.setDate( ServerUtil.currentDate() );
    ModelFmpUpdate updates = FmpUpdateStatus.getModelUpdate( p_message.getFromLogin(), p_message
        .getGameId(), null );
    updates.addChatMessages( p_message );
    FmpUpdateStatus.broadCastGameUpdate( updates );
    return updates.getNewModelUpdate( p_lastUpdate );
  }



}
