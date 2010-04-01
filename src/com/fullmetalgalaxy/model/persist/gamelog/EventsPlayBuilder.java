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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameEventStack;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Mobile;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.pathfinder.PathFinder;
import com.fullmetalgalaxy.model.pathfinder.PathMobile;
import com.fullmetalgalaxy.model.pathfinder.PathNode;
import com.fullmetalgalaxy.model.pathfinder.SimplePathFinder;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EventsPlayBuilder implements GameEventStack
{
  private ArrayList<AnEventPlay> m_actionList = new ArrayList<AnEventPlay>();
  // theses informations are used while building a new action
  private AnEventPlay m_selectedAction = null;
  private EbToken m_selectedToken = null;
  private AnBoardPosition m_selectedPosition = null;

  private AnBoardPosition m_lastClick = null;

  private Date m_lastUpdate = new Date( System.currentTimeMillis() );

  private long m_accountId = 0L;
  private EbGame m_game = null;

  /**
   * true if the action list is executed.
   */
  private boolean m_isExecuted = false;

  /**
   * 
   */
  public EventsPlayBuilder()
  {

  }

  public void clear() throws RpcFmpException
  {
    RpcUtil.logDebug( "clear action " );
    if( m_isExecuted )
    {
      unexec();
    }
    m_actionList.clear();
    unselectToken();
    m_lastClick = null;
  }

  public void exec() throws RpcFmpException
  {
    if( m_isExecuted )
      return;
    for( AnEvent action : getActionList() )
    {
      action.exec( m_game );
    }
    m_isExecuted = true;
  }

  public void checkedExec() throws RpcFmpException
  {
    if( m_isExecuted )
      return;
    for( AnEvent action : getActionList() )
    {
      action.checkedExec( m_game );
    }
    m_isExecuted = true;
  }

  public void unexec() throws RpcFmpException
  {
    if( !m_isExecuted )
      return;
    int actionIndex = getActionList().size();
    while( actionIndex > 0 )
    {
      actionIndex--;
      getAction( actionIndex ).unexec( m_game );
    }
    m_isExecuted = false;
  }


  public boolean isTokenSelected()
  {
    return m_selectedToken != null;
  }

  public boolean isBoardTokenSelected()
  {
    return m_selectedToken != null && m_selectedToken.getLocation() == Location.Board;
  }

  public ArrayList<AnEventPlay> getActionList()
  {
    return m_actionList;
  }

  public AnEventPlay getAction(int p_index)
  {
    if( (p_index < 0) || (p_index >= getActionList().size()) )
    {
      return null;
    }
    return getActionList().get( p_index );
  }

  public int getCost()
  {
    int cost = 0;
    for( AnEventPlay action : getActionList() )
    {
      cost += action.getCost();
    }
    return cost;
  }

  public boolean isRunnable()
  {
    boolean isRunnable = true;
    try
    {
      check();
    } catch( RpcFmpException e )
    {
      isRunnable = false;
    }
    return isRunnable;
  }

  public void check() throws RpcFmpException
  {
    if( (getSelectedAction() != null) && (getSelectedAction() instanceof EbEvtLand) )
    {
      getSelectedAction().check( m_game );
      return;
    }
    if( getActionList().size() == 0 )
    {
      throw new RpcFmpException();
    }
    boolean wasExecuted = isExecuted();
    try
    {
      if( !wasExecuted )
      {
        exec();
      }

      if( (getLastAction().getType() == GameLogType.EvtMove) && (getSelectedToken() != null)
          && (getSelectedToken().getLocation() == Location.Board) )
      {
        getGame().getBoardFireCover().decFireCover( getSelectedToken() );
        EnuColor fireCoverColor = getGame().getOpponentFireCover( getSelectedToken() );
        getGame().getBoardFireCover().incFireCover( getSelectedToken() );
        if( fireCoverColor.getValue() != EnuColor.None )
        {
          throw new RpcFmpException(
              "un mouvement ne peut se terminer dans une zone de feu adverse. Vous pouvez quand meme effectuer un tir" );
        }
      }

    } finally
    {
      if( !wasExecuted )
      {
        unexec();
      }
    }
  }

  /**
   * 
   * @param p_token
   * @return true if p_token is unloaded by at least one unload action
   */
  public boolean containUnload(EbToken p_token)
  {
    if( getSelectedAction() != null )
    {
      if( getSelectedAction() instanceof EbEvtUnLoad )
      {
        EbEvtUnLoad unload = (EbEvtUnLoad)getSelectedAction();
        if( unload.getToken( m_game ) == p_token )
        {
          return true;
        }
      }
      if( getSelectedAction() instanceof EbEvtConstruct )
      {
        // only weather hen can construct and it contain only one token
        return true;
      }
    }
    for( AnEvent action : getActionList() )
    {
      if( action.getType() == GameLogType.EvtUnLoad )
      {
        EbEvtUnLoad unload = (EbEvtUnLoad)action;
        // if( (unload.getToken() != null) && (unload.getToken().equals( p_token
        // )) )
        if( unload.getToken( m_game ) == p_token )
        {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isActionsPending()
  {
    return getActionList().size() != 0;
  }

  protected AnEvent getLastAction()
  {
    return getAction( getActionList().size() - 1 );
  }

  public AnEvent getLastGameLog()
  {
    return getLastAction();
  }

  /**
   * @return the lastUpdate
   */
  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  /**
   * @param p_lastUpdate the lastUpdate to set
   */
  protected void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }

  /**
   * @return the selectedAction
   */
  public AnEventPlay getSelectedAction()
  {
    return m_selectedAction;
  }

  /**
   * @param p_selectedAction the selectedAction to set
   */
  protected void setSelectedAction(AnEventPlay p_selectedAction)
  {
    m_selectedAction = p_selectedAction;
  }

  /**
   * @return the selectedToken
   */
  public EbToken getSelectedToken()
  {
    return m_selectedToken;
  }

  /**
   * @param p_selectedToken the selectedToken to set
   */
  protected void setSelectedToken(EbToken p_selectedToken)
  {
    m_selectedToken = p_selectedToken;
  }

  /**
   * @return the selectedPosition
   */
  public AnBoardPosition getSelectedPosition()
  {
    return m_selectedPosition;
  }

  /**
   * @param p_selectedPosition the selectedPosition to set
   */
  protected void setSelectedPosition(AnBoardPosition p_selectedPosition)
  {
    m_selectedPosition = p_selectedPosition;
  }

  public AnBoardPosition getLastUserClick()
  {
    return m_lastClick;
  }

  protected void setLastUserClick(AnBoardPosition p_position)
  {
    m_lastClick = p_position;
  }


  protected EbRegistration getMyRegistration()
  {
    if( getGame().getGameType() == GameType.MultiPlayer )
    {
      return getGame().getRegistrationByIdAccount( getAccountId() );
    }
    else
    {
      return getGame().getCurrentPlayerRegistration();
    }
  }

  /**
   * This method is part of the user building action API.
   * if user click on board.
   * @param p_position
   * @return true if action has changed
   */
  public EventBuilderMsg userBoardClick(AnBoardPosition p_position) throws RpcFmpException
  {
    EventBuilderMsg isUpdated = EventBuilderMsg.None;
    RpcUtil.logDebug( "user click board " + p_position );
    assert p_position != null;
    assert getGame() != null;
    if( p_position.equals( getLastUserClick() ) )
    {
      if( isRunnable() )
      {
        userOk();
      }
      else
      {
        clear();
        isUpdated = EventBuilderMsg.Updated;
      }
      return isUpdated;
    }

    setLastUserClick( p_position );
    if( getMyRegistration() == null )
    {
      // user isn't registred to this game
      clear();
      isUpdated = EventBuilderMsg.Updated;
    }

    AnEvent previousAction = getLastAction();
    exec();
    try
    {
      EbToken token = getGame().getToken( p_position );
      if( (!isBoardTokenSelected()) && (token != null) && (getSelectedAction() == null) )
      {
        // nothing is selected but user click on a token
        selectBoardToken( p_position );
        isUpdated = EventBuilderMsg.Updated;
      }
      else if( isBoardTokenSelected() )
      {
        // a token was already selected
        if( (token != null) && (token.getType() == TokenType.Pontoon)
            && !(getSelectedAction() instanceof EbEvtFire) )
        {
          // pontoon don't really load other token
          if( (getSelectedAction() != null) && (getSelectedAction() instanceof EbEvtUnLoad)
              && token.canLoad( ((EbEvtUnLoad)getSelectedAction()).getToken( m_game ).getType() ) )
          {
            token = null;
          }
          else if( token.canLoad( getSelectedToken().getType() ) )
          {
            token = null;
          }
        }
        // if token is a turret and selected action is unload, select freighter
        // instead
        if( (token != null) && (token.getType() == TokenType.Turret)
            && (getSelectedAction() instanceof EbEvtUnLoad) )
        {
          token = getGame().getToken( token.getPosition(), TokenType.Freighter );
        }


        if( token == null )
        {
          // user don't click on a token
          if( getSelectedAction() == null )
          {
            if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtUnLoad) )
            {
              EbEvtUnLoad previousUnload = (EbEvtUnLoad)previousAction;
              // so user want to move the just unloaded token, not the carrier
              // token
              selectBoardToken( previousUnload.getToken( m_game ), previousUnload
                  .getSelectedPosition( m_game ) );

              if( (getSelectedToken().getHexagonSize() == 2)
                  && (p_position.isNeighbor( previousUnload.getNewPosition() )) )
              {
                // in fact user want to turn his unloaded barge.
                previousUnload.unexec( m_game );
                previousUnload.getNewPosition().setSector(
                    previousUnload.getNewPosition().getNeighbourSector( p_position ) );
                previousUnload.exec( m_game );
                isUpdated = EventBuilderMsg.Updated;
              }

            }

            if( isUpdated == EventBuilderMsg.None )
            {
              // move previously selected token to that position
              if( moveSelectedTo( p_position ) )
              {
                isUpdated = EventBuilderMsg.Updated;
              }
            }

            if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtUnLoad) )
            {
              setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
            }
          }
          else if( getSelectedAction() instanceof EbEvtConstruct )
          {
            // construct action...
            AnBoardPosition closePosition = getSelectedPosition().getNeighbour(
                getSelectedPosition().getNeighbourSector( p_position ) );

            // first add a construct action
            EbEvtConstruct actionConstruct = (EbEvtConstruct)getSelectedAction();
            actionConstruct.setGame( m_game );
            actionConstruct.setAccountId( getAccountId() );
            actionAdd( actionConstruct );

            // then unload token
            userTokenClick( actionConstruct.getToken( m_game ) );
            exec();
            // we never construct barge token.
            // assume that constructed token has only one hexagon
            actionUnloadSelected( closePosition );

            if( !closePosition.equals( p_position ) )
            {
              setSelectedAction( null );
              setSelectedPosition( closePosition );
              setSelectedToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ) );
              moveSelectedTo( p_position );
              setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
            }
            isUpdated = EventBuilderMsg.Updated;
          }
          else if( getSelectedAction() instanceof EbEvtUnLoad )
          {
            // an unload action...
            AnBoardPosition closePosition = getSelectedPosition().getNeighbour(
                getSelectedPosition().getNeighbourSector( p_position ) );

            AnBoardPosition closePosition2 = null;
            if( ((EbEvtUnLoad)getSelectedAction()).getToken( m_game ).getHexagonSize() == 2 )
            {
              // if token is a barge, we have to use two position to determine
              // the unload final position
              if( ((EbEvtUnLoad)getSelectedAction()).getNewPosition() != null )
              {
                // the second position we where waiting for
                closePosition = ((EbEvtUnLoad)getSelectedAction()).getNewPosition();
                closePosition.setSector( closePosition.getNeighbourSector( p_position ) );
                actionUnloadSelected( closePosition );
              }
              else
              {
                if( !closePosition.equals( p_position ) )
                {
                  closePosition2 = closePosition.getNeighbour( closePosition
                      .getNeighbourSector( p_position ) );
                  closePosition.setSector( closePosition.getNeighbourSector( closePosition2 ) );
                }
                if( getGame().canTokenMoveOn(
                    ((EbEvtUnLoad)getSelectedAction()).getToken( m_game ),
                    closePosition.getNeighbour( closePosition.getSector() ) ) )
                {
                  actionUnloadSelected( closePosition );
                }
                else
                {
                  // in fact user click on the first position
                  // we are waiting for the second
                  ((EbEvtUnLoad)getSelectedAction()).setNewPosition( closePosition );
                }
              }
            }
            else
            {
              actionUnloadSelected( closePosition );
            }
            if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtLoad) )
            {
              // they where other action before this unload:
              // reselect the original selected token
              selectBoardToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ), getAction( 0 )
                  .getSelectedPosition( m_game ) );
            }
            if( closePosition2 != null )
            {
              if( !closePosition2.equals( p_position ) )
              {
                setSelectedAction( null );
                setSelectedPosition( closePosition2 );
                setSelectedToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ) );
                moveSelectedTo( p_position );
                setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
              }
            }
            else if( !closePosition.equals( p_position ) )
            {
              setSelectedAction( null );
              setSelectedPosition( closePosition );
              setSelectedToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ) );
              moveSelectedTo( p_position );
              setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
            }
            isUpdated = EventBuilderMsg.Updated;
          }
          if( isUpdated == EventBuilderMsg.None )
          {
            // user click somewhere, but no action was found: clear/unselect
            clear();
            isUpdated = EventBuilderMsg.Updated;
          }
        }
        else
        {
          // user click on another token
          if( getSelectedAction() == null )
          {
            boolean isPathFound = true;
            /*AnBoardPosition closePosition = p_position.getNeighbour( p_position
                .getNeighbourSector( getSelectedPosition() ) );
            if( !closePosition.equals( getSelectedPosition() ) )*/
            AnBoardPosition closePosition = p_position.getNeighbour( p_position
                .getNeighbourSector( getSelectedToken().getPosition() ) );
            if( !closePosition.equals( getSelectedToken().getPosition() ) )
            {
              isPathFound = moveSelectedTo( closePosition );
            }
            if( token.getType() == TokenType.Turret )
            {
              token = getGame().getToken( p_position, TokenType.Freighter );
              assert token != null;
            }
            if( isPathFound )
            {
              actionLoadSelected( token, p_position );
            }
            else
            {
              clear();
              selectBoardToken( token, p_position );
            }
            isUpdated = EventBuilderMsg.Updated;
          }
          else if( getSelectedAction() instanceof EbEvtFire )
          {
            // a fire action...
            if( ((EbEvtFire)getSelectedAction()).getTokenDestroyer2( m_game ) == null )
            {
              // select second destroyer
              ((EbEvtFire)getSelectedAction()).setTokenDestroyer2( token );
              isUpdated = EventBuilderMsg.Updated;
            }
            else
            {
              // select target and add action
              actionFire( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
          }
          else if( getSelectedAction() instanceof EbEvtControl )
          {
            // a control action...
            if( ((EbEvtControl)getSelectedAction()).getTokenDestroyer2( m_game ) == null )
            {
              // select second destroyer
              ((EbEvtControl)getSelectedAction()).setTokenDestroyer2( token );
              isUpdated = EventBuilderMsg.Updated;
            }
            else
            {
              // select target and add action
              actionControl( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
          }
          else if( getSelectedAction() instanceof EbEvtUnLoad )
          {
            // an unload action... which may be a transfer
            if( token.isNeighbor( ((EbEvtUnLoad)getSelectedAction()).getTokenCarrier( m_game ) ) )
            {
              // it is a transfer !
              actionTransferSelected( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
            else
            {
              AnBoardPosition closeUnloadPosition = getSelectedPosition().getNeighbour(
                  getSelectedPosition().getNeighbourSector( p_position ) );
              AnBoardPosition closeLoadPosition = p_position.getNeighbour( p_position
                  .getNeighbourSector( getSelectedPosition() ) );
              actionUnloadSelected( closeUnloadPosition );
              if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtLoad) )
              {
                // they where other action before this unload:
                // reselect the original selected token
                selectBoardToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ), getAction( 0 )
                    .getSelectedPosition( m_game ) );
              }
              if( !closeLoadPosition.equals( closeUnloadPosition ) )
              {
                setSelectedAction( null );
                setSelectedPosition( closeUnloadPosition );
                setSelectedToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ) );
                moveSelectedTo( closeLoadPosition );
                setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
              }
              if( token.getType() == TokenType.Turret )
              {
                token = getGame().getToken( p_position, TokenType.Freighter );
                assert token != null;
              }
              actionLoadSelected( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
          }
          else if( getSelectedAction() instanceof EbEvtConstruct )
          {
            // Construct action... possibly followed by a transfer
            // first add a construct action
            EbEvtConstruct actionConstruct = (EbEvtConstruct)getSelectedAction();
            actionConstruct.setGame( getGame() );
            actionConstruct.setAccountId( getAccountId() );
            actionAdd( actionConstruct );
            // then unload token
            userTokenClick( actionConstruct.getToken( m_game ) );
            exec();
            // then move constructed token
            if( token.isNeighbor( ((EbEvtUnLoad)getSelectedAction()).getTokenCarrier( m_game ) ) )
            {
              // it is a transfer !
              actionTransferSelected( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
            else
            {
              AnBoardPosition closeUnloadPosition = getSelectedPosition().getNeighbour(
                  getSelectedPosition().getNeighbourSector( p_position ) );
              AnBoardPosition closeLoadPosition = p_position.getNeighbour( p_position
                  .getNeighbourSector( getSelectedPosition() ) );
              actionUnloadSelected( closeUnloadPosition );
              if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtLoad) )
              {
                // they where other action before this unload:
                // reselect the original selected token
                selectBoardToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ), getAction( 0 )
                    .getSelectedPosition( m_game ) );
              }
              if( !closeLoadPosition.equals( closeUnloadPosition ) )
              {
                setSelectedAction( null );
                setSelectedPosition( closeUnloadPosition );
                setSelectedToken( ((EbEvtUnLoad)getLastAction()).getToken( m_game ) );
                moveSelectedTo( closeLoadPosition );
                setSelectedPosition( getAction( 0 ).getSelectedPosition( m_game ) );
              }
              if( token.getType() == TokenType.Turret )
              {
                token = getGame().getToken( p_position, TokenType.Freighter );
                assert token != null;
              }
              actionLoadSelected( token, p_position );
              isUpdated = EventBuilderMsg.Updated;
            }
          }
        }
      }
      else if( getSelectedAction() instanceof EbEvtLand )
      {
        EbEvtLand action = (EbEvtLand)getSelectedAction();
        // p_position.setSector( action.getPosition().getSector() );
        action.getPosition().setX( p_position.getX() );
        action.getPosition().setY( p_position.getY() );
        setLastUpdate( new Date( System.currentTimeMillis() ) );
        isUpdated = EventBuilderMsg.Updated;
      }
      else if( isActionsPending() )
      {
        clear();
        isUpdated = EventBuilderMsg.Updated;
      }
    } finally
    {
      unexec();
    }
    if( isUpdated == EventBuilderMsg.Updated )
    {
      setLastUpdate( new Date( System.currentTimeMillis() ) );
    }
    return isUpdated;
  }

  /**
   * This method is part of the user building action API.
   * is user click on a token which ISN'T on board.
   * @param p_token
   * @return
   * @throws RpcFmpException
   */
  public boolean userTokenClick(EbToken p_token) throws RpcFmpException
  {
    RpcUtil.logDebug( "user click token " + p_token );
    // assert p_token.getLocation() != Location.Board;
    boolean isUpdated = false;
    exec();
    try
    {
      if( (p_token.getLocation() == Location.Orbit) && (p_token.getType() == TokenType.Freighter) )
      {
        // player select a freighter in orbit
        if( getSelectedAction() instanceof EbEvtLand )
        {
          // player want to turn his ship before landing !
          EbEvtLand action = (EbEvtLand)getSelectedAction();
          action.getPosition().setSector( action.getPosition().getSector().getNext() );
          p_token.getPosition().setSector( action.getPosition().getSector() );
          isUpdated = true;
        }
        else
        {
          // ship in orbit: prepare to land
          clear();
          EbEvtLand action = new EbEvtLand();
          action.setGame( getGame() );
          action.setAccountId( getAccountId() );
          action.setPosition( new AnBoardPosition() );
          p_token.getPosition().setSector( action.getPosition().getSector() );
          action.setToken( p_token );
          setSelectedAction( action );
          setSelectedToken( p_token );
          isUpdated = true;
        }
      }
      else if( p_token.getLocation() == Location.Token )
      {
        assert isBoardTokenSelected();
        assert getSelectedToken() == p_token.getCarrierToken();
        if( getLastAction() != null
            && (getLastAction().getType() == GameLogType.EvtLoad || getLastAction().getType() == GameLogType.EvtTransfer)
            && getLastAction().getToken( getGame() ).canBeColored() )
        {
          AnBoardPosition selectedPosition = getSelectedPosition();
          clear();
          userBoardClick( selectedPosition );
        }
        // a token inside another token: prepare to unload !
        EbEvtUnLoad action = new EbEvtUnLoad();
        action.setGame( getGame() );
        action.setTokenCarrier( getSelectedToken() );
        action.setToken( p_token );
        setSelectedAction( action );
        isUpdated = true;
      }
      else if( p_token.getLocation() == Location.ToBeConstructed )
      {
        assert isBoardTokenSelected();
        assert getSelectedToken().getType() == TokenType.WeatherHen;
        EbEvtConstruct action = new EbEvtConstruct();
        action.setGame( getGame() );
        action.setTokenCarrier( getSelectedToken() );
        assert getGame().getToken( p_token.getId() ) != null;
        action.setToken( getGame().getToken( p_token.getId() ) );
        action.setConstructType( p_token.getType() );
        setSelectedAction( action );
        isUpdated = true;
      }
    } finally
    {
      unexec();
    }
    if( isUpdated )
    {
      setLastUpdate( new Date( System.currentTimeMillis() ) );
      // ModelFmpMain.model().notifyModelUpdate();
    }
    return isUpdated;
  }

  /**
   * Warning: don't check anymore if action is runnable or not.
   * @throws RpcFmpException
   */
  public void userOk() throws RpcFmpException
  {
    // RpcUtil.logDebug( "user click OK " );
    if( getSelectedAction() instanceof EbEvtLand )
    {
      actionAdd( getSelectedAction() );
      m_isExecuted = true;
      unexec();
    }
  }

  public void userCancel() throws RpcFmpException
  {
    // RpcUtil.logDebug( "user click cancel " );
    clear();
  }

  public void cancel()
  {
    try
    {
      userCancel();
    } catch( RpcFmpException e )
    {
    }
  }

  public EventBuilderMsg userAction(GameLogType p_type) throws RpcFmpException
  {
    RpcUtil.logDebug( "user click action " + p_type );
    EventBuilderMsg isUpdated = EventBuilderMsg.None;

    if( p_type == GameLogType.EvtRepair )
    {
      assert isBoardTokenSelected();
      EbEvtRepair action = new EbEvtRepair();
      action.setGame( getGame() );
      action.setAccountId( getAccountId() );
      action.setPosition( getSelectedPosition() );
      // ModelFmpMain.model().runSingleAction( gameLog );
      setSelectedAction( action );
      isUpdated = EventBuilderMsg.MustRun;
    }
    else if( p_type == GameLogType.EvtTakeOff )
    {
      assert isBoardTokenSelected();
      EbEvtTakeOff action = new EbEvtTakeOff();
      action.setGame( getGame() );
      action.setAccountId( getAccountId() );
      action.setToken( getSelectedToken() );
      action.setOldPosition( getSelectedToken().getPosition() );
      // ModelFmpMain.model().runSingleAction( gameLog );
      setSelectedAction( action );
      isUpdated = EventBuilderMsg.MustRun;
    }
    else if( p_type == GameLogType.EvtFire )
    {
      assert isBoardTokenSelected();
      EbEvtFire action = new EbEvtFire();
      action.setGame( getGame() );
      action.setAccountId( getAccountId() );
      EbToken token = getSelectedToken();
      if( token.getType() == TokenType.Freighter )
      {
        token = getGame().getToken( getSelectedPosition(), TokenType.Turret );
        if( token == null )
        {
          token = getSelectedToken();
        }
      }
      assert getGame().getTokenFireLength( token ) > 0;
      setSelectedToken( token );
      action.setTokenDestroyer1( token );
      setSelectedAction( action );
      isUpdated = EventBuilderMsg.Updated;
    }
    else if( p_type == GameLogType.EvtControl )
    {
      assert isBoardTokenSelected();
      assert getGame().getTokenFireLength( getSelectedToken() ) > 0;
      EbEvtControl action = new EbEvtControl();
      action.setGame( getGame() );
      action.setAccountId( getAccountId() );
      action.setTokenDestroyer1( getSelectedToken() );
      setSelectedAction( action );
      isUpdated = EventBuilderMsg.Updated;
    }

    if( isUpdated == EventBuilderMsg.Updated )
    {
      setLastUserClick( (AnBoardPosition)null );
      // ModelFmpMain.model().notifyModelUpdate();
    }
    return isUpdated;
  }


  /**
   * replace current selected token
   * @param p_token
   * @param p_position
   */
  protected void selectBoardToken(AnBoardPosition p_position)
  {
    if( p_position == null )
    {
      unselectToken();
    }
    EbToken token = getGame().getToken( p_position );
    if( token == null )
    {
      unselectToken();
    }
    else if( token.getType() == TokenType.Turret )
    {
      // we usually don't want to select turret but freighter
      token = getGame().getToken( p_position, TokenType.Freighter );
      assert token != null;
    }
    selectBoardToken( token, p_position );
  }

  /**
   * replace current selected token
   * @param p_token
   * @param p_position
   */
  protected void selectBoardToken(EbToken p_token, AnBoardPosition p_position)
  {
    RpcUtil.logDebug( "user select board token " + p_token + " " + p_position );
    assert p_token != null;
    assert p_position != null;
    assert p_token.getLocation() == Location.Board;
    setSelectedToken( p_token );
    setSelectedPosition( p_position );
    setSelectedAction( null );
    setLastUpdate( new Date( System.currentTimeMillis() ) );
  }

  protected void selectLoadToken(EbToken p_token)
  {
    RpcUtil.logDebug( "user select load token " + p_token );
    // a board token have to be selected first
    assert m_selectedToken != null;
    assert m_selectedPosition != null;
    assert m_selectedToken.getLocation() == Location.Board;
    assert p_token != null;
    assert p_token.getLocation() == Location.Token;
    EbEvtUnLoad action = new EbEvtUnLoad();
    action.setGame( m_selectedToken.getGame() );
    action.setToken( p_token );
    action.setTokenCarrier( m_selectedToken );
    setSelectedAction( action );
    setLastUpdate( new Date( System.currentTimeMillis() ) );
  }

  public void unselectToken()
  {
    RpcUtil.logDebug( "user unselect token " );
    m_selectedToken = null;
    m_selectedPosition = null;
    m_selectedAction = null;
    setLastUpdate( new Date( System.currentTimeMillis() ) );
  }


  protected void actionAddUnchecked(AnEventPlay p_action) throws RpcFmpException
  {
    p_action.exec( m_game );
    getActionList().add( p_action );
    m_selectedAction = null;
    // if a board token is selected, keep it
    if( (m_selectedToken == null) || (m_selectedPosition == null) )
    {
      m_selectedToken = null;
      m_selectedPosition = null;
    }
    setLastUpdate( new Date( System.currentTimeMillis() ) );
  }

  protected void actionAdd(AnEventPlay p_action) throws RpcFmpException
  {
    assert p_action != null;

    p_action.check( m_game );
    actionAddUnchecked( p_action );
  }


  protected void actionControl(EbToken p_tokenTarget, AnBoardPosition p_position)
      throws RpcFmpException
  {
    assert m_selectedToken != null;
    // assert m_selectedPosition != null;
    assert m_selectedToken.getLocation() == Location.Board;
    assert getSelectedAction() instanceof EbEvtControl;
    EbEvtControl action = (EbEvtControl)getSelectedAction();
    RpcUtil.logDebug( "user control with " + action.getTokenDestroyer1( m_game ) + " and "
        + action.getTokenDestroyer2( m_game ) + " onto " + p_tokenTarget + " " + p_position );
    action.setTokenTarget( p_tokenTarget );
    action.setOldColor( p_tokenTarget.getColor() );
    actionAdd( action );
    setSelectedAction( null );
  }

  protected void actionFire(EbToken p_tokenTarget, AnBoardPosition p_position)
      throws RpcFmpException
  {
    assert m_selectedToken != null;
    // assert m_selectedPosition != null;
    assert m_selectedToken.getLocation() == Location.Board;
    assert getSelectedAction() instanceof EbEvtFire;
    EbEvtFire action = (EbEvtFire)getSelectedAction();
    RpcUtil.logDebug( "user fire with " + action.getTokenDestroyer1( m_game ) + " and "
        + action.getTokenDestroyer2( m_game ) + " onto " + p_tokenTarget + " " + p_position );
    action.setTokenTarget( p_tokenTarget );
    action.setOldPosition( p_tokenTarget.getPosition() );
    actionAdd( action );
    setSelectedAction( null );
  }

  protected void actionMoveSelected(AnBoardPosition p_position) throws RpcFmpException
  {
    RpcUtil.logDebug( "user move token " + getSelectedToken() + " to " + p_position );
    assert m_selectedToken != null;
    assert m_selectedToken.getLocation() == Location.Board;
    assert getSelectedToken().isNeighbor( p_position );

    if( getSelectedToken().getPosition().equals( p_position )
        || getSelectedToken().getExtraPositions().contains( p_position ) )
    {
      return;
    }
    if( getSelectedToken().getHexagonSize() == 2 )
    {
      AnBoardPosition currentPosition = getSelectedToken().getPosition().newInstance();
      boolean isSelectedByHead = true;
      unexec();
      if( getSelectedToken().getPosition().equals( getSelectedPosition() ) )
      {
        // barge was selected by tail
        isSelectedByHead = false;
      }
      exec();
      if( isSelectedByHead )
      {
        if( p_position.isNeighbor( getSelectedToken().getPosition() ) )
        {
          // simply turn barge
          currentPosition.setSector( currentPosition.getNeighbourSector( p_position ) );
          p_position = currentPosition;
        }
        else
        {
          AnBoardPosition newPosition = currentPosition.getNeighbour( currentPosition.getSector() );
          newPosition.setSector( newPosition.getNeighbourSector( p_position ) );
          p_position = newPosition;
        }
      }
      else
      {
        if( p_position.isNeighbor( getSelectedToken().getExtraPositions().get( 0 ) ) )
        {
          // simply turn barge
          p_position.setSector( p_position.getNeighbourSector( getSelectedToken()
              .getExtraPositions().get( 0 ) ) );
        }
        else
        {
          p_position.setSector( p_position.getNeighbourSector( currentPosition ) );
        }
      }
    }
    else
    {
      p_position.setSector( getSelectedToken().getPosition().getNeighbourSector( p_position ) );
    }
    EbEvtMove action = new EbEvtMove();
    action.setGame( getGame() );
    action.setAccountId( getAccountId() );
    action.setToken( getSelectedToken() );
    action.setOldPosition( getSelectedToken().getPosition() );
    action.setNewPosition( p_position );
    actionAdd( action );
    setSelectedAction( null );
  }

  protected void actionTransferSelected(EbToken p_newTokenCarrier, AnBoardPosition p_position)
      throws RpcFmpException
  {
    assert m_selectedAction != null;
    assert m_selectedAction instanceof EbEvtUnLoad;
    EbEvtUnLoad unload = (EbEvtUnLoad)getSelectedAction();
    assert unload != null;
    setSelectedAction( null );
    EbEvtTransfer action = new EbEvtTransfer();
    action.setGame( getGame() );
    action.setAccountId( getAccountId() );
    action.setToken( unload.getToken( m_game ) );
    action.setTokenCarrier( unload.getTokenCarrier( m_game ) );
    action.setNewTokenCarrier( p_newTokenCarrier );

    AnEvent lastAction = getLastAction();
    if( (lastAction == null) && (getGame().getLogs().size() > 0) )
    {
      lastAction = getGame().getLogs().get( getGame().getLogs().size() - 1 );
    }
    // this code was to set unload cost to 1 for any vehicle number
    if( (lastAction != null)
        && (lastAction.getType() == GameLogType.EvtUnLoad)
        && (((EbEvtUnLoad)lastAction).getTokenCarrier( m_game ) == action.getTokenCarrier( m_game ))
        && (((EbEvtUnLoad)lastAction).getToken( m_game ) == action.getNewTokenCarrier( m_game )) )
    {
      action.setCost( 0 );
    }
    if( (lastAction != null)
        && (lastAction.getType() == GameLogType.EvtTransfer)
        && (((EbEvtTransfer)lastAction).getCost() == 0)
        && (((EbEvtTransfer)lastAction).getTokenCarrier( m_game ) == action
            .getTokenCarrier( m_game ))
        && (((EbEvtTransfer)lastAction).getNewTokenCarrier( m_game ) == action
            .getNewTokenCarrier( m_game )) )
    {
      action.setCost( 0 );
    }

    // test if user wan't enter into a freighter he don't own to take the
    // control
    if( (action.getNewTokenCarrier( m_game ).getType() == TokenType.Freighter)
        && (!action.getMyRegistration( m_game ).getEnuColor().isColored(
            action.getNewTokenCarrier( m_game ).getColor() )) )
    {
      actionControlFreighter( action.getToken( m_game ), action.getNewTokenCarrier( m_game ) );
    }

    RpcUtil.logDebug( "user transfert token " + action.getToken( m_game ) + " from "
        + action.getTokenCarrier( m_game ) + " to " + action.getTokenCarrier( m_game ) );
    actionAdd( action );
    // keep the first token selected to be ready to unload it
    selectBoardToken( p_newTokenCarrier, p_position );
    EbEvtUnLoad actionUnload = new EbEvtUnLoad();
    actionUnload.setGame( getGame() );
    actionUnload.setTokenCarrier( p_newTokenCarrier );
    actionUnload.setToken( unload.getToken( m_game ) );
    setSelectedAction( actionUnload );
  }

  protected void actionUnloadSelected(AnBoardPosition p_position) throws RpcFmpException
  {
    assert m_selectedAction != null;
    assert m_selectedAction instanceof EbEvtUnLoad;
    EbEvtUnLoad action = (EbEvtUnLoad)getSelectedAction();
    RpcUtil.logDebug( "user unload token " + action.getToken( m_game ) + " from "
        + action.getTokenCarrier( m_game ) + " to " + p_position );
    action.setAccountId( getAccountId() );
    action.setGame( getGame() );
    action.setNewPosition( p_position );
    AnEvent lastAction = getLastAction();
    if( (lastAction == null) && (getGame().getLogs().size() > 0) )
    {
      lastAction = getGame().getLogs().get( getGame().getLogs().size() - 1 );
    }
    /* this code was to set unload cost to 1 for any vehicle number
    if( (lastAction != null)
        && (lastAction.getType() == GameLogType.EvtUnLoad)
        && (((EbEvtUnLoad)lastAction).getTokenCarrier( m_game ) == action.getTokenCarrier( m_game )) )
    {
      action.setCost( 0 );
    }
    if( (lastAction != null)
        && (lastAction.getType() == GameLogType.EvtTransfer)
        && (((EbEvtTransfer)lastAction).getTokenCarrier( m_game ) == action
            .getTokenCarrier( m_game )) )
    {
      action.setCost( 0 );
    }*/
    
    actionAdd( action );
    // setSelectedToken( getSelectedToken() );
    // setSelectedPosition( p_position );
    setSelectedAction( null );
  }

  protected void actionControlFreighter(EbToken p_token, EbToken p_tokenFreighter)
      throws RpcFmpException
  {
    EbEvtControlFreighter action = new EbEvtControlFreighter();
    action.setAccountId( getAccountId() );
    action.setGame( getGame() );
    action.setToken( p_token );
    action.setTokenCarrier( p_tokenFreighter );
    action.setCost( -1 * getGame().getEbConfigGameTime().getActionPtPerExtraShip() );
    for( EbRegistration registration : getGame().getSetRegistration() )
    {
      EnuColor color = registration.getEnuColor();
      if( color.isColored( p_tokenFreighter.getColor() ) )
      {
        action.setOldRegistration( registration );
      }
    }
    assert action.getOldRegistration( getGame() ) != null;
    actionAdd( action );
  }


  protected void actionLoadSelected(EbToken p_tokenCarrier, AnBoardPosition p_position)
      throws RpcFmpException
  {
    assert isBoardTokenSelected();
    RpcUtil.logDebug( "user load token " + getSelectedToken() + " from "
        + getSelectedToken().getPosition() + " to " + p_tokenCarrier );
    EbEvtLoad action = new EbEvtLoad();
    action.setAccountId( getAccountId() );
    action.setGame( getGame() );
    if( p_tokenCarrier.getType() == TokenType.Ore )
    {
      // user want to load something into an ore...
      // he probably want the reverse !
      action.setOldPosition( p_tokenCarrier.getPosition() );
      action.setTokenCarrier( getSelectedToken() );
      action.setToken( p_tokenCarrier );
      actionAdd( action );
      return;
    }
    action.setOldColor( getSelectedToken().getColor() );
    action.setOldPosition( getSelectedToken().getPosition() );
    action.setTokenCarrier( p_tokenCarrier );
    action.setToken( getSelectedToken() );

    // test if user wan't enter into a freighter he don't own to take the
    // control
    if( (action.getTokenCarrier( m_game ).getType() == TokenType.Freighter)
        && (!action.getMyRegistration( m_game ).getEnuColor().isColored(
            action.getTokenCarrier( m_game ).getColor() )) )
    {
      actionControlFreighter( action.getToken( m_game ), action.getTokenCarrier( m_game ) );
    }

    actionAdd( action );
    // unload the content of action.getToken() to action.getTokenCarrier()
    for( EbToken tokenContent : action.getToken( m_game ).getSetContain() )
    {
      EbEvtTransfer transfer = new EbEvtTransfer();
      transfer.setAccountId( getAccountId() );
      transfer.setGame( getGame() );
      transfer.setToken( tokenContent );
      transfer.setTokenCarrier( tokenContent.getCarrierToken() );
      transfer.setNewTokenCarrier( p_tokenCarrier );
      transfer.setCost( 0 );
      actionAdd( transfer );
    }
    // keep the first token selected to be ready to unload it
    EbToken token = getSelectedToken();
    selectBoardToken( p_tokenCarrier, p_position );
    EbEvtUnLoad actionUnload = new EbEvtUnLoad();
    actionUnload.setGame( getGame() );
    actionUnload.setTokenCarrier( p_tokenCarrier );
    actionUnload.setToken( token );
    setSelectedAction( actionUnload );
  }



  /**
   * 
   * @param p_position
   * @return true if a path  was found
   */
  protected boolean moveSelectedTo(AnBoardPosition p_position) throws RpcFmpException
  {
    RpcUtil.logDebug( "user move token " + getSelectedToken() + " from "
        + getSelectedToken().getPosition() + " to " + p_position );
    assert getSelectedToken() != null;
    assert getSelectedToken().getLocation() == Location.Board;
    assert getGame() != null;
    assert (getAccountId() != 0) || (getGame().getGameType() != GameType.MultiPlayer);

    if( getSelectedToken().isNeighbor( p_position ) )
    {
      actionMoveSelected( p_position );
      return true;
    }
    EbGame game = getSelectedToken().getGame();
    EbRegistration registration = getMyRegistration();

    int actionPt = registration.getPtAction() - getCost();
    PathMobile mobile = new Mobile( registration, getSelectedToken() );
    // small optimisation
    // it's working only because heuristic is optimistic
    float heuristic = game.heuristic( getSelectedToken().getPosition(), p_position, mobile );
    if( (heuristic > actionPt) || (heuristic > 9)
        || (!getSelectedToken().canMoveOn( registration, p_position )) )
    {
      // we know that no path will be found !
      return false;
    }
    game.resetPathGraph();
    PathFinder finder = new SimplePathFinder( game );
    List<com.fullmetalgalaxy.model.pathfinder.PathNode> path = finder.findPath( getSelectedToken()
        .getPosition(), p_position, mobile );
    if( path.isEmpty() || (path.size() - 2 > actionPt) )
    {
      // no path was found
      return false;
    }
    else
    {
      // add and check path.
      Iterator<PathNode> it = path.iterator();
      // skip first position has it's the token position
      if( it.hasNext() )
      {
        it.next();
      }
      while( it.hasNext() )
      {
        PathNode node = it.next();
        actionMoveSelected( ((AnBoardPosition)node) );
      }
    }
    return true;
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
  public void setAccountId(long p_accountId)
  {
    m_accountId = p_accountId;
  }

  /**
   * @return the game
   */
  public EbGame getGame()
  {
    return m_game;
  }

  /**
   * @param m_game the game to set
   */
  public void setGame(EbGame p_game)
  {
    if( m_game != null )
    {
      m_game.setGameEventStack( m_game );
    }
    m_game = p_game;
    m_game.setGameEventStack( this );
  }

  /**
   * @return the isExecuted
   */
  public boolean isExecuted()
  {
    return m_isExecuted;
  }


}
