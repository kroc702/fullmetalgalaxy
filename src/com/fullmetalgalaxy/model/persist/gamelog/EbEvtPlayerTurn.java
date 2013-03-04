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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.MessagesRpcException;
import com.fullmetalgalaxy.model.ressources.SharedI18n;


/**
 * @author Vincent Legendre
 * change currents player turns and update action point for next player.
 */
public class EbEvtPlayerTurn extends AnEvent
{
  static final long serialVersionUID = 1;

  /** player that make this action (may be old player, admin or game creator, or automatic) */
  private long m_accountId = 0L;
  private int m_oldActionPt = 0;
  private Date m_endTurnDate = null;

  // data used by timeline mode
  private short m_oldTurn = 0;
  private short m_newTurn = 0;
  /** ie EbRegistration ID */
  private long m_oldPlayerId = 0;
  private int m_oldCurrentPlayersCount = 0;

  /**
   * 
   */
  public EbEvtPlayerTurn()
  {
    super();
    init();
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_accountId = 0;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtPlayerTurn;
  }

  /**
   * if player id isn't set, this method will read it from account id
   * @param p_game
   * @return
   */
  private long getOldPlayerId(Game p_game)
  {
    if( m_oldPlayerId <= 0 )
    {
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        if( p_game.getCurrentPlayerIds().contains( registration.getId() )
            && registration.getAccount().getId() == getAccountId() )
        {
          m_oldPlayerId = registration.getId();
        }
      }
    }
    return m_oldPlayerId;
  }

  public void setOldPlayerId(long p_oldPlayerId)
  {
    m_oldPlayerId = p_oldPlayerId;
  }

  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( isAuto() )
    {
      // no check !
      return;
    }
    if( p_game.getStatus() != GameStatus.Running )
    {
      throw new RpcFmpException( errMsg().gameNotStarted() );
    }
    if( p_game.isFinished() )
    {
      // no i18n
      throw new RpcFmpException( "This game is finished" );
    }
    if( p_game.isParallel() && p_game.getCurrentTimeStep() > 1 )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "You can't end your turn in parallele mode" );
    }
    if( p_game.getCurrentPlayerIds().isEmpty() )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "You can't end your turn: no current player" );
    }
    EbRegistration myRegistration = p_game.getRegistration( getOldPlayerId( p_game ) );
    if( myRegistration == null )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "Not your turn" );
    }
    if( p_game.getCurrentTimeStep() <= p_game.getEbConfigGameTime().getDeploymentTimeStep() )
    {
      EbToken freighter = p_game.getFreighter( myRegistration );
      if( freighter != null && freighter.getLocation() == Location.Orbit )
      {
        throw new RpcFmpException( errMsg().mustLandFreighter() );
      }
    }
    // check that current player have no cheating tank (ie two tank on same
    // montain)
    if( !isAuto() )
    {
      EnuColor playerColor = myRegistration.getEnuColor();
      for( EbToken token : p_game.getSetToken() )
      {
        if( token.getColor() != EnuColor.None && playerColor.isColored( token.getColor() )
            && p_game.getTankCheating( token ) != null )
        {
          throw new RpcFmpException( errMsg().cantEndTurnTwoTankMontain() );
        }
      }
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    Game game = p_game;
    assert game != null;
    
    // round down players action point
    EbRegistration currentPlayerRegistration = p_game.getRegistration( getOldPlayerId( p_game ) );
    assert currentPlayerRegistration != null;
    // backup for unexec
    m_oldActionPt = currentPlayerRegistration.getPtAction();
    currentPlayerRegistration.setPtAction( currentPlayerRegistration.getRoundedActionPt(p_game) );
    m_oldTurn = (short)game.getCurrentTimeStep();
    m_newTurn = m_oldTurn;
    m_oldCurrentPlayersCount = game.getCurrentPlayerIds().size();


    if( game.getCurrentPlayerIds().size() > 1 )
    {
      // several player are playing at the same time.
      // so this action only end turn of one single player
      game.getCurrentPlayerIds().remove( (Long)getOldPlayerId( p_game ) );
    }
    else
    {
      // assume there is one and only one current player

      // next player
      EbRegistration nextPlayerRegistration = null;
      if( game.isTimeStepParallelHidden( m_oldTurn ) )
      {
        nextPlayerRegistration = game.getRegistrationByOrderIndex( 0 );
      }
      else
      {
        nextPlayerRegistration = game.getNextPlayerRegistration( currentPlayerRegistration
            .getOrderIndex() );
      }

      if( nextPlayerRegistration.getOrderIndex() <= currentPlayerRegistration.getOrderIndex() )
      {
        // next turn !
        m_newTurn++;
        if( game.isTimeStepParallelHidden( m_oldTurn ) )
        {
          // play in main event stack all events in registrations
          playRegistrationEvents( p_game );

          // compute next turn
          while( game.isTimeStepParallelHidden( m_newTurn ) )
            m_newTurn++;
        }
        game.setCurrentTimeStep( m_newTurn );
        if( game.isParallel() )
        {
          // this is the real start time for parallele game
          // as player may took a while to land
          // start parallel mode
          p_game.setLastTimeStepChange( new Date() );
        }
      }

      // reset all end turn date
      for( EbRegistration player : game.getSetRegistration() )
      {
        player.setEndTurnDate( null );
      }
      game.getCurrentPlayerIds().clear();


      if( game.isTimeStepParallelHidden( game.getCurrentTimeStep() )
          || (game.isParallel() && game.getCurrentTimeStep() > 1) )
      {
        // all players become current players !
        // except those without any landed freighter
        for( EbRegistration registration : game.getSetRegistration() )
        {
          if( registration.getOnBoardFreighterCount( game ) > 0 )
          {
            addCurrentPlayer( game, registration );
          }
        }
      }
      else
      {
        addCurrentPlayer( game, nextPlayerRegistration );
      }
    }
  }

  private void addCurrentPlayer(Game p_game, EbRegistration nextPlayerRegistration)
  {
    // update all his tokens bullets count
    EnuColor nextPlayerColor = nextPlayerRegistration.getEnuColor();
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.getType() != TokenType.Freighter
          && token.getBulletCount() < token.getType().getMaxBulletCount()
          && nextPlayerColor.isColored( token.getColor() ) )
      {
        token.setBulletCount( token.getBulletCount()
            + p_game.getEbConfigGameTime().getBulletCountIncrement() );
      }
    }

    // parallel hidden phase correspond to deployment or take off phase:
    // don't add action points
    if( !p_game.isTimeStepParallelHidden( p_game.getCurrentTimeStep() ) )
    {
      int actionInc = nextPlayerRegistration.getActionInc( p_game );
      int actionPt = nextPlayerRegistration.getPtAction() + actionInc;
      if( actionPt > nextPlayerRegistration.getMaxActionPt( p_game ) )
      {
        actionPt = nextPlayerRegistration.getMaxActionPt( p_game );
      }
      nextPlayerRegistration.setPtAction( actionPt );

      // set End turn date for future current player
      if( p_game.getCurrentTimeStep() > 1
          && p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
      {
        if( m_endTurnDate == null )
        {
          m_endTurnDate = new Date( System.currentTimeMillis()
              + p_game.getEbConfigGameTime().getTimeStepDurationInMili() );
        }
        nextPlayerRegistration.setEndTurnDate( m_endTurnDate );
      }
    }
    p_game.getCurrentPlayerIds().add( nextPlayerRegistration.getId() );

  }

  private void playRegistrationEvents(Game p_game)
  {
    List<EbRegistration> players = p_game.getRegistrationByPlayerOrder();
    int eventIndex[] = new int[players.size()];
    boolean morePrivateEvent = true;
    while( morePrivateEvent )
    {
      morePrivateEvent = false;
      for( int playerIndex = 0; playerIndex < players.size(); playerIndex++ )
      {
        boolean eventAdded = false;
        while( !eventAdded
            && eventIndex[playerIndex] < players.get( playerIndex ).getMyEvents().size() )
        {
          morePrivateEvent = true;
          try
          {
            AnEvent event = players.get( playerIndex ).getMyEvents().get( eventIndex[playerIndex] );
            event.checkedExec( p_game );
            p_game.addEvent( event );
            eventAdded = true;
          } catch( RpcFmpException e )
          {
          }
          eventIndex[playerIndex]++;
        }
      }
    }
    // remove all private events
    for( EbRegistration player : players )
    {
      player.clearMyEvents();
    }
  }

  /**
   * note that this method do not set the player's end turn date.
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    assert p_game != null;

    if( m_oldCurrentPlayersCount > 1 )
    {
      p_game.getCurrentPlayerIds().add( (Long)getOldPlayerId( p_game ) );
    }
    else
    {
      // remove action point to all current players
      for( long currentPlayerId : p_game.getCurrentPlayerIds() )
      {
        EbRegistration registration = p_game.getRegistration( currentPlayerId );
        int actionInc = registration.getActionInc( p_game );
        int actionPt = registration.getPtAction() - actionInc;
        if( actionPt < 0 )
        {
          actionPt = 0;
        }
        registration.setPtAction( actionPt );
      }

      // previous player
      EbRegistration previousPlayer = p_game.getRegistration( getOldPlayerId( p_game ) );
      previousPlayer.setPtAction( m_oldActionPt );
      p_game.getCurrentPlayerIds().clear();
      p_game.getCurrentPlayerIds().add( previousPlayer.getId() );

      p_game.setCurrentTimeStep( m_oldTurn );
    }
  }

  protected MessagesRpcException errMsg()
  {
    return SharedI18n.getMessagesError( getAccountId() );
  }


  /**
   * @return the account
   */
  public long getAccountId()
  {
    return m_accountId;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccountId(long p_id)
  {
    m_accountId = p_id;
  }

  public short getOldTurn()
  {
    return m_oldTurn;
  }

  public short getNewTurn()
  {
    return m_newTurn;
  }



}
