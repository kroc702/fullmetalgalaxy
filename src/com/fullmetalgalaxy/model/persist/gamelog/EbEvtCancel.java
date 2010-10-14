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
import java.util.List;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.persist.EbGame;

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
  public void check(EbGame p_game) throws RpcFmpException
  {
    // do nothing to let them run in timeline mode as any other actions
  }

  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    // do nothing to let them run in timeline mode as any other actions
  }

  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
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
  public void execCancel(EbGame p_game) throws RpcFmpException
  {
    assert p_game != null;
    assert p_game.getId() == getIdGame();
    if( m_fromActionIndex != p_game.getLogs().size() -1 || m_toActionIndex < 0 )
    {
      throw new RpcFmpException( "this cancel action isn't for this game state" );
    }
    while( m_toActionIndex < p_game.getLogs().size() )
    {
      AnEvent action = p_game.getLastGameLog();
      if( !(action instanceof EbAdmin) )
      {
        // unexec action
        try
        {
          action.unexec( p_game );
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
        }
      }
      p_game.getLogs().remove( p_game.getLogs().size() -1 );
      m_eventsBackup.add( 0, action );
    }
    p_game.addEvent( this );
  }


  @Override
  public void setGame(EbGame p_game)
  {
    super.setGame( p_game );
    if( p_game != null && p_game.getLogs().size() > 0 )
    {
      m_fromActionIndex = p_game.getLogs().size() -1;
    }
  }

  // Bean getter / setter
  // ====================

  public void setToActionIndex(int p_toActionIndex)
  {
    m_toActionIndex = p_toActionIndex;
  }


}
