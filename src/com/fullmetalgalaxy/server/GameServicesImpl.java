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
package com.fullmetalgalaxy.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtChangePlayerOrder;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTimeStep;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.server.image.BlobstoreCache;
import com.fullmetalgalaxy.server.image.MiniMapProducer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * TODO create only one dataStore per RPC request
 * @author Vincent
 *
 */
public class GameServicesImpl extends RemoteServiceServlet implements GameServices
{
  public static final long serialVersionUID = 1;
  private final static FmpLogger log = FmpLogger.getLogger( GameServicesImpl.class.getName() );
  protected static String s_basePath = null;

  /**
   * constructor: 
   */
  public GameServicesImpl()
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




  @Override
  public EbBase saveGame(Game p_game) throws RpcFmpException
  {
    return saveGame( p_game, null );
  }

  /* (non-Javadoc)
  * @see com.fullmetalgalaxy.model.GameCreationServices#createGame(com.fullmetalgalaxy.model.DbbGame)
  */
  @Override
  public EbBase saveGame(Game p_game, String p_modifDesc) throws RpcFmpException
  {
    if( !isLogged() )
    {
      throw new RpcFmpException(
          "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
    }
    FmgDataStore dataStore = new FmgDataStore(false);
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
      if( (!Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() )) )
      {
        throw new RpcFmpException(
            "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
      }
      adminEvent.setAccountId( account.getId() );
      p_game.addEvent( adminEvent );
    }

    dataStore.put( p_game );
    dataStore.close();

    // should we construct minimap image ?
    if( p_game.getMinimapUri() == null )
    {
      MiniMapProducer miniMapProducer = new MiniMapProducer( s_basePath, p_game );
      byte[] data = miniMapProducer.getImage();
      BlobstoreCache.storeMinimap( p_game.getId(), new ByteArrayInputStream( data ) );
    }


    return p_game.createEbBase();
  }


  static protected Game getEbGame(long p_gameId)
  {
    FmgDataStore dataStore = new FmgDataStore(false);
    Game model = dataStore.getGame( p_gameId );
    if( model == null )
    {
      return null;
    }


    // anything to update ?
    try
    {
      ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
      modelUpdate.setGameId( model.getId() );
      modelUpdate.setFromVersion( model.getVersion() );

      ArrayList<AnEvent> events = updateGame( model );

      if( !events.isEmpty() )
      {
        modelUpdate.setGameEvents( events );

        dataStore.put( model );
        modelUpdate.setToVersion( model.getVersion() );

        ChannelManager.broadcast( ChannelManager.getRoom( model.getId() ), modelUpdate );

        // do we need to send an email ?
        sendMail( model, modelUpdate );
      }
    } catch( RpcFmpException e )
    {
      log.error( e );
    }


    dataStore.close();

    if( model.getGameType() != GameType.MultiPlayer )
    {
      model.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    }
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
    Game game = null;
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
        // FmpUpdateStatus.loadAllAccounts( modelInit.getMapAccounts(), game );
      }
    }

    return modelInit;
  }


  @Override
  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException
  {
    ModelFmpInit modelInit = sgetModelFmpInit( p_gameId );

    if( modelInit.getGame() != null && modelInit.getGame().getId() != 0 )
    {
      // ModelFmpUpdate modelUpdate = FmpUpdateStatus.getModelUpdate(
      // Auth.getUserPseudo(
      // getThreadLocalRequest(), getThreadLocalResponse() ),
      // modelInit.getGame().getId(), null );
    }
    modelInit.setPresenceRoom( ChannelManager.getRoom( modelInit.getGame().getId() ) );
    return modelInit;
  }



  private boolean isLogged()
  {
    return Auth.isUserLogged( getThreadLocalRequest(), getThreadLocalResponse() );
  }

  /**
   * This service is only here to serialize a ModelFmpUpdate class with RPC.encodeResponseForSuccess
   */
  @Override
  public ModelFmpUpdate getModelFmpUpdate(long p_gameId) throws RpcFmpException
  {
    assert p_gameId != 0;
    // return FmpUpdateStatus.waitForModelUpdate( Auth.getUserPseudo(
    // getThreadLocalRequest(),
    // getThreadLocalResponse() ), p_gameId, p_lastUpdate );
    return null;
  }



  /**
   * eventually send an email to people that need it.
   * This method have to be called after p_action have been successfully ran.
   * @param action
   */
  protected static void sendMail(Game p_game, ModelFmpUpdate p_update)
  {
    for( AnEvent action : p_update.getGameEvents() )
    {
      if( (action instanceof EbAdminTimePlay) || (action instanceof EbEvtPlayerTurn) )
      {
        EbAccount currentPlayer = null;
        if( p_game.getCurrentPlayerRegistration() != null )
        {
          FmgDataStore dataStore = new FmgDataStore(true);
          currentPlayer = dataStore.get( EbAccount.class, p_game.getCurrentPlayerRegistration()
              .getAccount().getId() );
        }
        if( currentPlayer == null )
        {
          // TODO send email to all players: it's an asynchron game
          log.error( "New turn email couldn't be send" );
          return;
        }
        if( ChannelManager.getRoom( p_game.getId() ).isConnected( currentPlayer.getPseudo() ) )
        {
          log.fine( "player is connected: we don't need to send an email" );
          return;
        }
        if( !currentPlayer.isAllowMailFromGame() || !currentPlayer.haveEmail() )
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




  // TODO merge these two method and use a modelUpdate to get pageID
  @Override
  public void runEvent(AnEvent p_action) throws RpcFmpException
  {
    // System.out.println( p_action );
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
    FmgDataStore dataStore = new FmgDataStore(false);
    Game game = dataStore.getGame( p_action.getIdGame() );
    int oldTimeStep = game.getCurrentTimeStep();

    p_action.setLastUpdate( ServerUtil.currentDate() );
    modelUpdate.setFromVersion( game.getVersion() );
    modelUpdate.setGameId( game.getId() );


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

    if( (p_action.getType() == GameLogType.EvtPlayerTurn)
        && (game.getCurrentTimeStep() == game.getEbConfigGameTime().getTotalTimeStep()) )
    {
      // end turn action in the last turn...
      // check that his freighter take off, if not, take off automatically
      for( EbToken token : game.getSetToken() )
      {
        if( (token.getType() == TokenType.Freighter)
            && (game.getCurrentPlayerRegistration().getEnuColor().isColored( token.getColor()))
            && (token.getLocation() == Location.Board) )
        {
          // automatic take off for this freighter
          EbEvtTakeOff eventTakeOff = new EbEvtTakeOff();
          eventTakeOff.setGame( game );
          eventTakeOff.setToken( token );
          eventTakeOff.setAccountId( game.getRegistrationByColor( token.getColor() ).getAccount().getId() );
          eventTakeOff.setAuto( true );
          eventTakeOff.checkedExec( game );
          game.addEvent( eventTakeOff );
          modelUpdate.getGameEvents().add( eventTakeOff );
        }
      }
    }
    
    // real action
    //
    if(p_action.getType() == GameLogType.EvtCancel)
    {
      // cancel action doesn't work in exact same way as other event
      ((EbEvtCancel)p_action).execCancel( game );
      
      modelUpdate.getGameEvents().add( p_action );
      modelUpdate.getGameEvents().addAll( updateGame( game ) );
    }
    else
    {
      modelUpdate.getGameEvents().addAll( updateGame( game ) );
      modelUpdate.getGameEvents().add( p_action );

      // execute action
      p_action.checkedExec( game );
      game.addEvent( p_action );
    }
    
    // save all events. This action is required as game->events relation isn't
    // a real bidirectional relation (because of event_index column)
    if( p_action.getType().isEventUser() )
    {
      ((AnEventUser)p_action).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
    }

    if( (p_action.getType() == GameLogType.EvtPlayerTurn)
        && (oldTimeStep != game.getCurrentTimeStep()) )
    {
      // new turn !
      if( game.getNextTideChangeTimeStep() <= game.getCurrentTimeStep() )
      {
        EbEvtTide eventTide = new EbEvtTide();
        eventTide.setNextTide( Tide.getRandom() );
        eventTide.setGame( game );
        eventTide.checkedExec( game );
        game.addEvent( eventTide );
        modelUpdate.getGameEvents().add( eventTide );
      }
    }

    dataStore.put( game );

    // Some special cases
    /////////////////////
    if( p_action.getType() == GameLogType.GameJoin )
    {
      if( game.getCurrentNumberOfRegiteredPlayer() == game.getMaxNumberOfPlayer() )
      {
        // TODO we may prefer starting game not in live mode only (slow game only)
        // if the last player is just connected, automatically launch the game.
        EbAdminTimePlay action = new EbAdminTimePlay();
        action.setAuto( true );
        action.setLastUpdate( ServerUtil.currentDate() );
        action.setAccountId( ((EbGameJoin)p_action).getAccountId() );
        action.setGame( game );
        action.checkedExec( game );
        game.addEvent( action );
        modelUpdate.getGameEvents().add( action );
      }

      if(game.getCurrentTimeStep() == 0 
          && game.getLastGameLog().getType() == GameLogType.AdminTimePlay
          && game.getFreighter( game.getRegistrationByOrderIndex( 0 ) ).getLocation() == Location.Orbit )
      {
        // game is starting
        EbEvtChangePlayerOrder action = new EbEvtChangePlayerOrder();
        action.setLastUpdate( ServerUtil.currentDate() );
        action.initRandomOrder( game );
        action.setGame( game );
        action.checkedExec( game );      
        game.addEvent( action );
        modelUpdate.getGameEvents().add( action );
      }
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
      modelUpdate.getGameEvents().add( action );
    }
    
    dataStore.put( game );
    dataStore.close();
    modelUpdate.setToVersion( game.getVersion() );

    // do we need to send an email ?
    sendMail( game, modelUpdate );

    // broadcast changes to all clients
    ChannelManager.broadcast( ChannelManager.getRoom( game.getId() ), modelUpdate );
  }

  @Override
  public void runAction(ArrayList<AnEventPlay> p_actionList) throws RpcFmpException
  {
    if( p_actionList.isEmpty() )
    {
      throw new RpcFmpException( "No actions provided" );
    }
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
    FmgDataStore dataStore = new FmgDataStore(false);
    Game game = null;
    AnEvent lastAction = null;
    RpcFmpException exception = null;

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

    modelUpdate.setGameId( game.getId() );
    modelUpdate.setFromVersion( game.getVersion() );
    modelUpdate.setGameEvents( updateGame( game ) );

    // execute actions
    try
    {
      for( AnEvent action : p_actionList )
      {
        ((AnEventUser)action).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
        action.setLastUpdate( ServerUtil.currentDate() );
        action.checkedExec( game );
        game.addEvent( action );

        lastAction = action;
      }
      modelUpdate.getGameEvents().addAll( p_actionList );
    } catch( RpcFmpException e )
    {
      exception = e;
    }


    // Some special cases
    // ///////////////////
    if( game.isAsynchron() && lastAction != null && lastAction.getType() == GameLogType.EvtLand )
    {
      // a player is just landed and game is parallel: next player
      EbEvtPlayerTurn action = new EbEvtPlayerTurn();
      action.setLastUpdate( ServerUtil.currentDate() );
      action.setAccountId( ((EbEvtLand)lastAction).getAccountId() );
      action.setGame( game );
      action.checkedExec( game );
      game.addEvent( action );
      modelUpdate.getGameEvents().add( action );
    }


    dataStore.put( game );
    dataStore.close();
    modelUpdate.setToVersion( game.getVersion() );

    // do we need to send an email ?
    sendMail( game, modelUpdate );

    // broadcast changes to all clients
    ChannelManager.broadcast( ChannelManager.getRoom( game.getId() ), modelUpdate );

    if( exception != null )
    {
      throw exception;
    }
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
  static protected ArrayList<AnEvent> updateGame(Game p_game) throws RpcFmpException
  {
    assert p_game != null;
    ArrayList<AnEvent> events = new ArrayList<AnEvent>();

    // in some case, game is never updated
    if( !p_game.isStarted() || p_game.isHistory() || p_game.getGameType() != GameType.MultiPlayer
        || p_game.getCurrentTimeStep() == 0 )
    {
      return events;
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
          // automatic take off for all freighter just before end game
          if( p_game.getCurrentTimeStep() > p_game.getEbConfigGameTime().getTotalTimeStep() )
          {
            for( EbToken token : p_game.getSetToken() )
            {
              if( (token.getType() == TokenType.Freighter)
                  && (token.getLocation() == Location.Board) )
              {
                // automatic take off for this freighter
                EbEvtTakeOff eventTakeOff = new EbEvtTakeOff();
                eventTakeOff.setGame( p_game );
                eventTakeOff.setToken( token );
                eventTakeOff.setAccountId( p_game.getRegistrationByColor( token.getColor() )
                    .getAccount().getId() );
                eventTakeOff.setAuto( true );
                eventTakeOff.checkedExec( p_game );
                p_game.addEvent( eventTakeOff );
                events.add( eventTakeOff );
              }
            }
          }
          // new time step
          EbEvtTimeStep event = new EbEvtTimeStep();
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          events.add( event );
          if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
          {
            // next tide
            EbEvtTide eventTide = new EbEvtTide();
            eventTide.setNextTide( Tide.getRandom() );
            eventTide.setGame( p_game );
            eventTide.checkedExec( p_game );
            p_game.addEvent( eventTide );
            events.add( eventTide );
          }
        }
      }
      else if( p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
      {
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate().getTime() 
                    <= System.currentTimeMillis())
            && (p_game.getCurrentTimeStep() != 0) ) // never skip first turn
        {
          // change player's turn
          int oldPlayerOrderIndex = p_game.getCurrentPlayerRegistration().getOrderIndex();
          EbEvtPlayerTurn event = new EbEvtPlayerTurn();
          event.setAuto( true );
          event.setGame( p_game );
          event.checkedExec( p_game );
          p_game.addEvent( event );
          events.add( event );
          if( p_game.getCurrentPlayerRegistration().getOrderIndex() <= oldPlayerOrderIndex )
          {
            // new turn !
            if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
            {
              // next tide
              EbEvtTide eventTide = new EbEvtTide();
              eventTide.setGame( p_game );
              eventTide.setNextTide( Tide.getRandom() );
              eventTide.checkedExec( p_game );
              p_game.addEvent( eventTide );
              events.add( eventTide );
            }
          }
        }
        /* TODO
        if( (p_game.getCurrentPlayerRegistration() != null)
            && (p_game.getCurrentPlayerRegistration().getEndTurnDate() == null) )
        {
          ModelFmpUpdate updates = null;// FmpUpdateStatus.getModelUpdate( null,
                                        // p_game.getId(), null );
          if( updates.isUserConnected( p_game.getCurrentPlayerRegistration().getAccountPseudo() ) )
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
        }*/
      }

      // triggers
      p_game.execTriggers();
    }
    else if( !p_game.isHistory() )
    {
      /*for( EbRegistration registration : p_game.getSetRegistration() )
      {
        registration.setStats( EbAccountStats.generate( registration, p_game ) );
      }*/
      p_game.setHistory( true );
    }

    return events;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.GameServices#sendChatMessage(com.fullmetalgalaxy.model.ChatMessage)
   */
  @Override
  public void sendChatMessage(ChatMessage p_message) throws RpcFmpException
  {
    // we could check pseudo to detect cheater...
    //p_message
    //    .setFromPseudo( Auth.getUserPseudo( getThreadLocalRequest(), getThreadLocalResponse() ) );
    p_message.setDate( ServerUtil.currentDate() );
    
    PresenceRoom room = ChannelManager.getRoom( p_message.getGameId() );
    ChannelManager.broadcast( room, p_message );
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

  @Override
  public void checkUpdate(long p_gameId) throws RpcFmpException
  {
    getEbGame( p_gameId );
  }

}
