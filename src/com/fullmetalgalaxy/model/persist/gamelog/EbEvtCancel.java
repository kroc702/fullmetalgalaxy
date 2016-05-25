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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.persist.Game;

/**
 * @author vlegendr
 * This event is used to cancel, when allowed, several action on top of game log event.
 */
public class EbEvtCancel extends AnEventUser
{
  private static final long serialVersionUID = -6246885948647990623L;

  /** should be the last event +1 in game log (ie game.getLogs().size()) */
  private int m_fromActionIndex = 0;
  /** the last event to remove/cancel from game log */
  private int m_toActionIndex = 0;
  /** a backup of all canceled events */
  private List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> m_eventsBackup = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();
  
  /**
   * 
   */
  public EbEvtCancel()
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
  }
  
  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtCancel;
  }

  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    // do nothing to let them run in timeline mode as any other actions
  }

  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    // do nothing to let them run in timeline mode as any other actions
  }

  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    // do nothing to let them run in timeline mode as any other actions
  }

  /**
   * this method differ from exec as:
   * - it won't be called during standard exec/unexec process
   * - it should be called while not registered by p_game (ie not in game log)
   * - it will unexec and removed some action and then registred itself
   * @param p_game
   */
  public void execCancel(Game p_game) throws RpcFmpException
  {
    assert p_game != null;
    assert p_game.getId() == getIdGame();
    boolean isTimeStepCanceled = false;
    long timeSinceLastTimeStepChange = SharedMethods.currentTimeMillis()
        - p_game.getLastTimeStepChange().getTime();
    if( timeSinceLastTimeStepChange > p_game.getEbConfigGameTime().getTimeStepDurationInMili() )
    {
      timeSinceLastTimeStepChange = 0;
    }
    int totalEventCount = p_game.getLogs().size() + p_game.getAdditionalEventCount();
    int toActionIndex = m_toActionIndex - p_game.getAdditionalEventCount();
    if( getMyRegistration( p_game ) != null )
    {
      totalEventCount += getMyRegistration( p_game ).getTeam(p_game).getMyEvents().size();
    }
    if( m_fromActionIndex != totalEventCount - 1 || toActionIndex < 0 )
    {
      throw new RpcFmpException( "this cancel action isn't for this game state", this );
    }

    List<AnEvent> eventLogs = p_game.getLogs();
    if( getMyRegistration( p_game ) != null && toActionIndex >= p_game.getLogs().size() )
    {
      toActionIndex -= p_game.getLogs().size();
      eventLogs = getMyRegistration( p_game ).getTeam(p_game).getMyEvents();
    }
    while( toActionIndex < eventLogs.size() )
    {
      AnEvent action = eventLogs.get( eventLogs.size() - 1 );
      if( !(action instanceof EbAdmin) )
      {
        // unexec action
        try
        {
          action.unexec( p_game );

          if( action instanceof EbEvtTimeStep )
          {
            isTimeStepCanceled = true;
          }
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
        }
      }
      eventLogs.remove( eventLogs.size() - 1 );
      m_eventsBackup.add( 0, action );
    }
    // this is to avoid timestep replay right after the cancel action.
    if( p_game.isParallel() && isTimeStepCanceled )
    {
      p_game.setLastTimeStepChange( new Date( SharedMethods.currentTimeMillis()
          - timeSinceLastTimeStepChange ) );
    }
    p_game.addEvent( this );
  }


  // Bean getter / setter
  // ====================

  public void setToActionIndex(int p_toActionIndex)
  {
    m_toActionIndex = p_toActionIndex;
  }


  public void setFromActionIndex(int p_fromActionIndex)
  {
    m_fromActionIndex = p_fromActionIndex;
  }

  public int getFromActionIndex()
  {
    return m_fromActionIndex;
  }

  public int getToActionIndex()
  {
    return m_toActionIndex;
  }


}
