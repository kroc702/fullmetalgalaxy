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

import java.util.Date;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;


/**
 * @author Vincent Legendre
 * it is the base class to represent any event or action which can be performed on to a game.
 * 'exec()' have to be launch at least once before saving this event. @see unexec()
 */
public class AnEvent extends EbBase
{
  static final long serialVersionUID = 1;

  private Date m_lastUpdate = null;

  // TODO is it really usefull now ?
  private GameLogType m_type = null;

  private Date m_oldUpdate = null;

  private long m_idGame = 0;



  private boolean m_auto = false;

  transient private boolean m_isPersistent = false;



  /**
   * 
   */
  public AnEvent()
  {
    super();
    init();
  }


  @Override
  public boolean isTrancient()
  {
    if( m_isPersistent )
    {
      return false;
    }
    return super.isTrancient();
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    setLastUpdate( new Date( System.currentTimeMillis() ) );
    m_oldUpdate = null;
    m_idGame = 0;
    m_type = null;

  }

  public GameLogType getType()
  {
    if( m_type == null )
    {
      return GameLogType.None;
    }
    else
    {
      return m_type;
    }
  }

  /**
   * used to change timestamp to notify modification
   * we don't really need the original timestamp, but you can set it with 'setOldUpdate()'
   * @return last update minus one second
   */
  protected Date getOldUpdate()
  {
    if( m_oldUpdate != null )
    {
      return m_oldUpdate;
    }
    if( getLastUpdate() == null )
    {
      return null;
    }
    return new Date( getLastUpdate().getTime() - 1000 );
  }

  protected void setOldUpdate(Date p_date)
  {
    m_oldUpdate = p_date;
  }


  /**
   * execute this action
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void exec(EbGame p_game) throws RpcFmpException
  {
    p_game.setVersion( p_game.getVersion() + 1 );
  }

  /**
   * un execute this action. ie undo what exec did.
   * Note that you can't undo an action which isn't executed. This mean that you have to execute 
   * this action to let backup all required information to launch unexec BEFORE savinf it into
   * database.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    p_game.setVersion( p_game.getVersion() - 1 );
  }

  /**
   * a conveniance method which call check() and exec().
   * You don't have to overide it.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public final void checkedExec(EbGame p_game) throws RpcFmpException
  {
    check( p_game );
    exec( p_game );
  }

  /**
   * check this action is allowed.
   * you have to override this method.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void check(EbGame p_game) throws RpcFmpException
  {
    if( p_game == null )
    {
      throw new RpcFmpException( "game [" + getIdGame() + "] was null" );
    }
    if( getIdGame() != p_game.getId() )
    {
      throw new RpcFmpException( "try to apply event from game [" + getIdGame() + "] on game ["
          + p_game.getId() + "]" );
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = getLastUpdate().toString();
    str += " " + getType();
    return str;
  }

  // Bean getter / setter
  // ====================


  /**
   * @return the idGame
   */
  public long getIdGame()
  {
    return m_idGame;
  }

  /**
   * @param p_idGame the idGame to set
   */
  public void setIdGame(long p_idGame)
  {
    m_idGame = p_idGame;
  }


  public void setGame(EbGame p_game)
  {
    m_idGame = p_game.getId();
  }


  /**
   * @return the auto
   */
  public boolean isAuto()
  {
    return m_auto;
  }

  /**
   * @param p_auto the auto to set
   */
  public void setAuto(boolean p_auto)
  {
    m_auto = p_auto;
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
  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }



}
