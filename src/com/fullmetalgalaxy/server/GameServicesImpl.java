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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;

/**
 * TODO create only one dataStore per RPC request
 * @author Vincent
 *
 */
public class GameServicesImpl extends RemoteServiceServlet implements GameServices
{
  public static final long serialVersionUID = 1;
  private final static FmpLogger log = FmpLogger.getLogger( GameServicesImpl.class.getName() );

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
    ServerUtil.setBasePath( getServletContext().getRealPath( "/" ) );
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
      // no i18n
      throw new RpcFmpException( "You must be logged for this action" );
    }
    FmgDataStore dataStore = new FmgDataStore(false);
    EbAccount account = Auth.getUserAccount( getThreadLocalRequest(), getThreadLocalResponse() );

    // should we construct minimap image ?
    if( p_game.getMinimapUri() == null )
    {
      FmgDataStore.storeMinimap( p_game );
    }

    boolean isNewlyCreated = true;
    if( !p_game.isTrancient() )
    {
      isNewlyCreated = false;
      // then add an admin event
      if( p_modifDesc == null || p_modifDesc.trim().length() == 0 )
      {
        p_modifDesc = "Unknown edition event";
      }
      EbAdmin adminEvent = new EbAdmin();
      adminEvent.setGame( p_game );
      adminEvent.setMessage( p_modifDesc );

      if( !Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() )
          && (p_game.getAccountCreator().getId() != account.getId() || (p_game.getCurrentTimeStep() >= 2 && p_game
              .getGameType() != GameType.Initiation)) )
      {
        // TODO i18n
        throw new RpcFmpException(
            "seul l'admin peut modifier la partie après l'atterissage des joueurs" );
      }
      adminEvent.setAccountId( account.getId() );
      p_game.addEvent( adminEvent );
    }

    dataStore.put( p_game );
    dataStore.close();

    if( isNewlyCreated )
    {
      // game is just created
      GameWorkflow.gameOpen( p_game );
    }

    return p_game.createEbBase();
  }


  /**
   * load a game from database and check if any update is required.
   * if update are required, their are broadcasted to clients.
   * @param p_gameId
   * @return the returned game can be saved after any update
   */
  static protected Game getEbGame(long p_gameId)
  {
    FmgDataStore dataStore = new FmgDataStore(false);
    Game model = dataStore.getGame( p_gameId );
    boolean isGameUpdated = false;
    if( model == null )
    {
      return null;
    }


    // check that registration event log are not corrupted
    if( !model.isTimeStepParallelHidden( model.getCurrentTimeStep() ) )
    {
      for( EbRegistration registration : model.getSetRegistration() )
      {
        if( !registration.getMyEvents().isEmpty() )
        {
          registration.clearMyEvents();
          isGameUpdated = true;
        }
      }
    }

    // anything to update ?
    GameStatus oldStatus = model.getStatus();
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
    modelUpdate.setGameId( model.getId() );
    modelUpdate.setFromVersion( model.getVersion() );

    ArrayList<AnEvent> events = GameWorkflow.checkUpdate( model );

    if( !events.isEmpty() || oldStatus != model.getStatus() )
    {
      // some was necessary
      if( !events.isEmpty() )
      {
        modelUpdate.setGameEvents( events );
        modelUpdate.setToVersion( model.getVersion() );

        // do we need to send an email ?
        GameNotification.sendMail( model, modelUpdate );
      }
      isGameUpdated = true;
    }
    if( isGameUpdated )
    {
      dataStore.put( model );
      try
      {
        // this action may lead to a ConcurrentModificationException
        // in this case, it simply mean that several client ask for an update
        // at same time
        dataStore.close();

        // so we broadcast change only once
        modelUpdate.setToVersion( model.getVersion() );
        ChannelManager.broadcast( ChannelManager.getRoom( model.getId() ), modelUpdate );
      } catch( ConcurrentModificationException e )
      {
      }
    }

    if( model.getGameType() == GameType.Puzzle )
    {
      model.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    }
    return model;
  }


  public static ModelFmpInit sgetModelFmpInit(HttpServletRequest p_request,
      HttpServletResponse p_response, String p_gameId)
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
    if( gameId == 0 && ServerUtil.getBasePath() != null )
    {
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try
      {
        String localizedUrl = I18n.localizeUrl( p_request, p_response, p_gameId );
        fis = new FileInputStream( new File( ServerUtil.getBasePath() + localizedUrl ) );
        in = new ObjectInputStream( fis );
        modelInit = ModelFmpInit.class.cast( in.readObject() );
        in.close();
        fis.close();
      } catch( Exception ex )
      {
        log.warning( ex.getMessage() );
      }
      // if the localized game wasn't found, search with standard url
      if( in == null )
      {
        try
        {
          fis = new FileInputStream( new File( ServerUtil.getBasePath() + p_gameId ) );
          in = new ObjectInputStream( fis );
          modelInit = ModelFmpInit.class.cast( in.readObject() );
          in.close();
          fis.close();
        } catch( Exception ex )
        {
          log.error( ex );
        }
      }
      if( modelInit != null && modelInit.getGame() != null )
      {
        // call onLoad callback as Objectify does for backward compatibility
        modelInit.getGame().getPreview().onLoad();
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
    ModelFmpInit modelInit = sgetModelFmpInit( getThreadLocalRequest(), getThreadLocalResponse(),
        p_gameId );

    if( modelInit.getGame() != null && modelInit.getGame().getId() != 0 )
    {
      // ModelFmpUpdate modelUpdate = FmpUpdateStatus.getModelUpdate(
      // Auth.getUserPseudo(
      // getThreadLocalRequest(), getThreadLocalResponse() ),
      // modelInit.getGame().getId(), null );
      modelInit.setPresenceRoom( ChannelManager.getRoom( modelInit.getGame().getId() ) );
    }
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




  @Override
  public ModelFmpUpdate runModelUpdate(ModelFmpUpdate p_modelUpdate) throws RpcFmpException
  {
    if( p_modelUpdate.getGameEvents().isEmpty() )
    {
      // no i18n as unusual
      throw new RpcFmpException( "No actions provided" );
    }
    FmgDataStore dataStore = new FmgDataStore(false);
    Game game = dataStore.getGame( p_modelUpdate.getGameId() );
    if( game == null )
    {
      // no i18n as unusual
      throw new RpcFmpException( "run action on unknown game: "+p_modelUpdate.getGameId());
    }
    if( game.getVersion() != p_modelUpdate.getFromVersion() )
    {
      // TODO should we try to apply this update ?
      throw new RpcFmpException( "Send action on wrong game version" );
    }
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
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate( game );
    modelUpdate.setGameEvents( GameWorkflow.checkUpdate( game ) );

    // execute actions
    try
    {
      // an automatic update before run event ?
      modelUpdate.getGameEvents().addAll( GameWorkflow.checkUpdate( game, p_modelUpdate.getGameEvents() ) );
      
      // then run all provided event
      for( AnEvent event : p_modelUpdate.getGameEvents() )
      {
        event.setTrancient();
        EbRegistration registration = null;
        if( event instanceof AnEventUser )
        {
          registration = ((AnEventUser)event).getMyRegistration( game );
          if( registration != null )
          {
            registration.updateLastConnexion();
            registration.clearNotifSended();
          }
          else if( (event instanceof EbEvtMessage)
              && (game.getAccount( ((AnEventUser)event).getAccountId() ) == null) )
          {
            // add our account info into this game
            game.addAccount( new EbPublicAccount( FmgDataStore.dao().get( EbAccount.class,
                ((AnEventUser)event).getAccountId() ) ) );
          }
        }
        event.setLastUpdate( ServerUtil.currentDate() );
        event.setGameVersion( game.getVersion() );
        
        // execute action
        if(event.getType() == GameLogType.EvtCancel)
        {
          // cancel action doesn't work in exact same way as other event
          ((EbEvtCancel)event).execCancel( game );
        }
        else if( registration != null && game.isTimeStepParallelHidden( game.getCurrentTimeStep() ) )
        {
          for( AnEvent myevent : registration.getMyEvents() )
          {
            myevent.exec( game );
          }
          event.check( game );
          for( int i = registration.getMyEvents().size() - 1; i >= 0; i-- )
          {
            AnEvent myevent = registration.getMyEvents().get( i );
            myevent.unexec( game );
          }
          registration.addMyEvent( event );
        }
        else
        {
          event.checkedExec( game );
          game.addEvent( event );

        }
      }
      modelUpdate.getGameEvents().addAll( p_modelUpdate.getGameEvents() );
      // another automatic update after running event ?
      modelUpdate.getGameEvents().addAll( GameWorkflow.checkUpdate( game ) );
      
    } catch( RpcFmpException e )
    {
      dataStore.rollback();
      throw e;
    }

    // do we need to send an email ?
    GameNotification.sendMail( game, modelUpdate );

    // and save game
    dataStore.put( game );
    dataStore.close();

    // broadcast changes to all clients
    modelUpdate.setToVersion( game.getVersion() );
    modelUpdate.setClientID( p_modelUpdate.getClientID() );
    ChannelManager.broadcast( ChannelManager.getRoom( game.getId() ), modelUpdate );

    return modelUpdate;
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


  public static EbGameLog sgetAdditionalGameLog(long p_gameId)
  {
    EbGameLog gameLog = new EbGameLog();
    Key<EbGamePreview> keyPreview = new Key<EbGamePreview>( EbGamePreview.class, p_gameId );
    Iterable<EbGameLog> logs = FmgDataStore.dao().query( EbGameLog.class ).order( "m_index" )
        .ancestor( keyPreview );
    for( EbGameLog log : logs )
    {
      gameLog.getLog().addAll( log.getLog() );
    }
    return gameLog;
  }

  @Override
  public EbGameLog getAdditionalGameLog(long p_gameId) throws RpcFmpException
  {
    return sgetAdditionalGameLog( p_gameId );
  }


  @Override
  public ModelFmpUpdate getUpdate(long p_gameId, long p_myVersion) throws RpcFmpException
  {
    Game game = getEbGame( p_gameId );

    ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
    modelUpdate.setGameId( game.getId() );
    modelUpdate.setFromVersion( p_myVersion );
    modelUpdate.setToVersion( game.getVersion() );

    if( game.getVersion() > p_myVersion )
    {
      // get events from main event list
      if( !game.getLogs().isEmpty() )
      {
        int firstEventIndex = game.getLogs().size();
        while( firstEventIndex > 0
            && game.getLogs().get( firstEventIndex - 1 ).getGameVersion() >= p_myVersion )
        {
          firstEventIndex--;
        }

        for( int iEvent = firstEventIndex; iEvent < game.getLogs().size(); iEvent++ )
        {
          modelUpdate.getGameEvents().add( game.getLogs().get( iEvent ) );
        }
      }

      // get events from registration event lists
      for( EbRegistration registration : game.getSetRegistration() )
      {
        for( AnEvent event : registration.getMyEvents() )
        {
          if( event.getGameVersion() > p_myVersion )
          {
            modelUpdate.getGameEvents().add( event );
          }
        }
      }
    }
   
    return modelUpdate;
  }

}
