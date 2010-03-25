package com.fullmetalgalaxy.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
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




  public EbAccount askForIdentity() throws RpcFmpException
  {
    if( !isLogged() )
    {
      throw new RpcFmpException( RpcFmpException.LogonWrongPassword );
    }
    EbAccount account = Auth.getUserAccount( getThreadLocalRequest(), getThreadLocalResponse() );

    return account;
  }



  /**
   * @see com.fullmetalgalaxy.model.Services#getGameList(java.lang.String)
   */
  public List<com.fullmetalgalaxy.model.persist.EbGamePreview> getGameList(GameFilter p_filter)
      throws RpcFmpException
  {
    return FmgDataStore.getGamePreviewList();
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
    // if( !isLogged() ||
    // (!p_game.getAccountCreator().getLogin().equalsIgnoreCase( getRemoteUser()
    // ))
    // && !Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() )
    // )
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
      EbAccount account = askForIdentity();
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

    // save initial state
    /*try
    {
      // get a game step and save it.
      session = FmgDataStore.beginTransaction();
      GameInit gameInit = new GameInit( p_game );
      session.persist( gameInit );
      FmgDataStore.commit( session );
    } catch( Exception e )
    {
      FmgDataStore.rollback( session );
    }

    GameStep gameStep = new GameStep( p_game, dateBeforeCommit );
    gameStep.setIdRegistration( 0 );
    gameStep.setGreatEvent( EnuGreatEvent.GameCreation );
    saveGameStep( gameStep );
    */
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

    if( modelInit.getGame().getId() != 0 )
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



  public ModelFmpUpdate getModelFmpUpdate(long p_gameId, Date p_lastUpdate) throws RpcFmpException
  {
    assert p_lastUpdate != null;
    assert p_gameId != 0;
    return FmpUpdateStatus.waitForModelUpdate( Auth.getUserPseudo( getThreadLocalRequest(),
        getThreadLocalResponse() ), p_gameId, p_lastUpdate );
  }


  private void convertThrowable(Throwable p_th) throws RpcFmpException
  {
    if( p_th instanceof RpcFmpException )
    {
      throw (RpcFmpException)p_th;
    }
    if( p_th instanceof Exception )
    {
      throw new RpcFmpException( ((Exception)p_th).getMessage() );
    }
    String message = "Unexpected error: " + p_th.toString();
    if( p_th.getMessage() != null )
    {
      message += "\n" + p_th.getMessage();
    }
    if( p_th.getStackTrace() != null )
    {
      for( int i = 0; i < p_th.getStackTrace().length; i++ )
      {
        message += "\n" + p_th.getStackTrace()[i].toString();
      }
    }
    throw new RpcFmpException( message );
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
        String body = "Bonjour " + currentPlayer.getPseudo()
            + "\n\n"
            + "Vous pouvez des a present vous connecter a la partie " + p_game.getName()
            + " http://www.fullmetalgalaxy.com/game.jsp?id=" + p_game.getId()
            + " pour jouer votre tour " + p_game.getCurrentTimeStep() + ".\n";
        PMServlet.sendMail( subject, body, currentPlayer.getEmail() );
        return;
      }
    }
  }

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

    updateGame( game );
    p_action.setLastUpdate( ServerUtil.currentDate() );
    game.addEvent( p_action );

    // execute action
    p_action.check(game);
    p_action.exec(game);

    // save all events. This action is required as game->events relation isn't
    // a real bidirectional relation (because of event_index column)
    if( p_action.getType().isEventUser() )
    {
      ((AnEventUser)p_action).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
    }

    if( (p_action.getType() == GameLogType.EvtPlayerTurn)
        && (game.getCurrentPlayerRegistration().getOrderIndex() == 0) )
    {
      // new turn !
      if( game.getNextTideChangeTimeStep() <= game.getCurrentTimeStep() )
      {
        EbEvtTide eventTide = new EbEvtTide();
        game.addEvent( eventTide );
        eventTide.setNextTide( Tide.getRandom() );
        eventTide.checkedExec( game );
      }
    }

    dataStore.save( game );

    // if the last player is just connected, automatically launch the game.
    if( (p_action.getType() == GameLogType.GameJoin)
        && (game.getCurrentNumberOfRegiteredPlayer() == game
            .getMaxNumberOfPlayer()) )
    {
      EbAdminTimePlay action = new EbAdminTimePlay();
      action.setAuto( true );
      action.setLastUpdate( ServerUtil.currentDate() );
      action.setAccountId( ((EbGameJoin)p_action).getAccountId() );
      game.addEvent( action );
      action.checkedExec( game );
    }
    dataStore.save( game );
    dataStore.close();

    if( p_action.getType() == GameLogType.GameJoin )
    {
      // in case of join event, we must load corresponding account
      ModelFmpUpdate updates = FmpUpdateStatus.getModelUpdate( Auth.getUserPseudo(
          getThreadLocalRequest(), getThreadLocalResponse() ), p_action.getIdGame(), null );
      FmpUpdateStatus.loadAllAccounts( updates.getMapAccounts(), game );
      FmpUpdateStatus.broadCastGameUpdate( updates );
    }

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
    if( !p_game.isStarted() || p_game.isFinished() || p_game.getGameType() != GameType.MultiPlayer )
    {
      return isUpdated;
    }
    if( p_game.isAsynchron() )
    {
      long currentTimeInMiliSec = System.currentTimeMillis();
      while( (!p_game.isFinished())
          && ((currentTimeInMiliSec - p_game.getLastTimeStepChange().getTime()) > p_game
              .getEbConfigGameTime().getTimeStepDurationInMili()) )
      {
        EbEvtTimeStep event = new EbEvtTimeStep();
        p_game.addEvent( event );
        event.checkedExec(p_game);
        // p_session.persist( event );
        if( p_game.getNextTideChangeTimeStep() >= p_game.getCurrentTimeStep() )
        {
          EbEvtTide eventTide = new EbEvtTide();
          eventTide.setNextTide( Tide.getRandom() );
          p_game.addEvent( eventTide );
          eventTide.checkedExec(p_game);
          // p_session.persist( eventTide );
        }
        isUpdated = true;
      }
    }
    else
    {
      while( (!p_game.isFinished())
          /* TODO turn skiping is deactivated in Standard time */
          && (p_game.getConfigGameTime() != ConfigGameTime.Standard)
          && (p_game.getCurrentPlayerRegistration() != null)
          && (p_game.getCurrentPlayerRegistration().getEndTurnDate() != null)
          && (p_game.getCurrentPlayerRegistration().getEndTurnDate().getTime() < System
              .currentTimeMillis()) )
      {
        // change player's turn
        int oldPlayerOrderIndex = p_game.getCurrentPlayerRegistration().getOrderIndex();
        EbEvtPlayerTurn event = new EbEvtPlayerTurn();
        event.setAuto( true );
        p_game.addEvent( event );
        event.checkedExec(p_game);
        // p_session.persist( event );
        if( p_game.getCurrentPlayerRegistration().getOrderIndex() <= oldPlayerOrderIndex )
        {
          // new turn !
          if( p_game.getNextTideChangeTimeStep() <= p_game.getCurrentTimeStep() )
          {
            EbEvtTide eventTide = new EbEvtTide();
            eventTide.setGame( p_game );
            eventTide.setNextTide( Tide.getRandom() );
            eventTide.checkedExec(p_game);
            // p_session.persist( eventTide );
          }
        }
        isUpdated = true;
      }
      ModelFmpUpdate updates = FmpUpdateStatus.getModelUpdate( null, p_game.getId(), null );
      if( (p_game.getCurrentPlayerRegistration() != null)
          && (updates.getConnectedUser( p_game.getCurrentPlayerRegistration().getAccountId() ) != null)
          && ((p_game.getCurrentPlayerRegistration().getEndTurnDate() == null) || (p_game
              .getCurrentPlayerRegistration().getEndTurnDate().getTime() > System
              .currentTimeMillis()
              + p_game.getEbConfigGameTime().getTimeStepDurationInMili())) )
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

        // as we update current players end turn date, we may need to update
        // next players end turn date
        EbRegistration currentPlayerRegistration = p_game.getCurrentPlayerRegistration();
        int index = currentPlayerRegistration.getOrderIndex();
        EbRegistration nextPlayerRegistration = p_game.getNextPlayerRegistration( index );
        while( (nextPlayerRegistration != p_game.getCurrentPlayerRegistration())
            && (updates.getConnectedUser( nextPlayerRegistration.getAccountId() ) != null)
            && ((nextPlayerRegistration.getEndTurnDate() == null) || (nextPlayerRegistration
                .getEndTurnDate().getTime() > System.currentTimeMillis()
                + p_game.getEbConfigGameTime().getTimeStepDurationInMili())) )
        {
          // next player is also connected, update his end turn
          nextPlayerRegistration.setEndTurnDate( new Date( currentPlayerRegistration
              .getEndTurnDate().getTime()
              + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
          // p_session.persist( nextPlayerRegistration );
          updates.getConnectedUser( nextPlayerRegistration.getAccountId() )
              .setEndTurnDate(
                  new Date( currentPlayerRegistration.getEndTurnDate().getTime()
                      + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
          // let see next player
          currentPlayerRegistration = nextPlayerRegistration;
          index = currentPlayerRegistration.getOrderIndex();
          nextPlayerRegistration = p_game.getNextPlayerRegistration( index );
        }
        FmpUpdateStatus.broadCastGameUpdate( updates );
        isUpdated = true;
      }

      /* I was using getRemoteUser (replaced by getConnectedUser)
            if( (p_game.getCurrentPlayerRegistration() != null)
                && (isLogged())
                && (getRemoteUser()
                    .equals( p_game.getCurrentPlayerRegistration().getAccount().getLogin() ))
                && ((p_game.getCurrentPlayerRegistration().getEndTurnDate() == null) || (p_game
                    .getCurrentPlayerRegistration().getEndTurnDate().getTime() > System
                    .currentTimeMillis()
                    + p_game.getEbConfigGameTime().getTimeStepDurationInMili())) )
            {
              // player current's turn !
              // update end turn
              p_game.getCurrentPlayerRegistration().setEndTurnDate(
                  new Date( System.currentTimeMillis()
                      + p_game.getEbConfigGameTime().getTimeStepDurationInMili() ) );
              p_session.persist( p_game.getCurrentPlayerRegistration() );
            }
      */
    }

    if( !p_game.isFinished() )
    {
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

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.Services#cancelGame(long)
   */
  public void cancelGame(long p_gameId) throws RpcFmpException
  {
    FmgDataStore dataStore = new FmgDataStore();
    EbGame game = dataStore.getGame( p_gameId );
    if( !isLogged()
        || (game.getAccountCreatorId() != Auth.getUserAccount( getThreadLocalRequest(),
            getThreadLocalResponse() ).getId())
        && !Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() ) )
    {
      throw new RpcFmpException(
          "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
    }
    game.setHistory( true );

    dataStore.close();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.Services#deleteGame(long)
   */
  public void deleteGame(long p_gameId) throws RpcFmpException
  {
    if( !isLogged() || !getThreadLocalRequest().isUserInRole( "admin" ) )
    {
      throw new RpcFmpException(
          "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
    }
    FmgDataStore dataStore = new FmgDataStore();
    dataStore.deleteGame( p_gameId );
    dataStore.close();
  }



}
