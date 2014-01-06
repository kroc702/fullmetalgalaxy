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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.BoardFireCover.FdChange;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.FireDisabling;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 * it is the base class to represent any event or action which can be performed on to a game.
 * 'exec()' have to be launch at least once before saving this event. @see unexec()
 * 
 */
public class AnEvent extends EbBase implements GameEvent
{
  static final long serialVersionUID = 1;

  /** the date when server receive or create this event */
  private Date m_lastUpdate = null;
  /** game version on server, when it receive or create this event */
  private long m_gameVersion = 0;
  /** game id on which this action shall be run */
  private long m_idGame = 0;


  /**
   * a backup of all fire disable flag that have been changed by this action
   */
  private List<FireDisabling> m_fdRemoved = new ArrayList<FireDisabling>();
  private List<FireDisabling> m_fdAdded = new ArrayList<FireDisabling>();
  /** this flag is here to avoid recompute fire disabling action again and again */
  private boolean m_isFdComputed = false;

  
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
    setLastUpdate( new Date() );
    m_idGame = 0;

    m_fdRemoved = null;
    m_fdAdded = null;
    m_isFdComputed = false;
  }

  public GameLogType getType()
  {
    return GameLogType.None;
  }

  /**
   * Whether this even can be store in parallel event list local to a team.
   */
  public boolean canBeParallelHidden()
  {
    return false;
  }

  /**
   * execute this action
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
  }

  /**
   * un execute this action. ie undo what exec did.
   * Note that you can't undo an action which isn't executed. This mean that you have to execute 
   * this action to let backup all required information to launch unexec BEFORE savinf it into
   * database.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
  }

  /**
   * a conveniance method which call check() and exec().
   * You don't have to overide it.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public final void checkedExec(Game p_game) throws RpcFmpException
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
  @Override
  public void check(Game p_game) throws RpcFmpException
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
    String str = "" + getType();
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

  public void setGame(Game p_game)
  {
    m_idGame = p_game.getId();
    m_gameVersion = p_game.getVersion();
  }


  public long getGameVersion()
  {
    return m_gameVersion;
  }


  public void setGameVersion(long p_gameVersion)
  {
    m_gameVersion = p_gameVersion;
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

  /**
   * @return the fdRemoved
   */
  protected List<FireDisabling> getFdRemoved()
  {
    return m_fdRemoved;
  }


  /**
   * @param p_fdRemoved the fdRemoved to set
   */
  protected void setFdRemoved(List<FireDisabling> p_fdRemoved)
  {
    m_fdRemoved = p_fdRemoved;
  }

  protected void addFdRemoved(List<FireDisabling> p_fdRemoved)
  {
    if( p_fdRemoved == null || p_fdRemoved.isEmpty() )
    {
      return;
    }
    if( m_fdRemoved == null )
    {
      setFdRemoved( new ArrayList<FireDisabling>() );
    }
    m_fdRemoved.addAll( p_fdRemoved );
  }

  /*protected void addFdRemoved(FireDisabling p_fdRemoved)
  {
    if( p_fdRemoved == null )
    {
      return;
    }
    if( m_fdRemoved == null )
    {
      m_fdRemoved = new ArrayList<FireDisabling>();
    }
    m_fdRemoved.add( p_fdRemoved );
  }*/


  /**
   * @return the fdAdded
   */
  protected List<FireDisabling> getFdAdded()
  {
    return m_fdAdded;
  }


  /**
   * @param p_fdAdded the fdAdded to set
   */
  protected void setFdAdded(List<FireDisabling> p_fdAdded)
  {
    m_fdAdded = p_fdAdded;
  }

  protected void addFdAdded(List<FireDisabling> p_fdAdded)
  {
    if( p_fdAdded == null || p_fdAdded.isEmpty() )
    {
      return;
    }
    if( m_fdAdded == null )
    {
      setFdAdded( new ArrayList<FireDisabling>() );
    }
    m_fdAdded.addAll( p_fdAdded );
  }


  /**
   * @return the isFdComputed
   */
  protected boolean isFdComputed()
  {
    return m_isFdComputed;
  }


  /**
   * @param p_isFdComputed the isFdComputed to set
   */
  protected void setFdComputed(boolean p_isFdComputed)
  {
    m_isFdComputed = p_isFdComputed;
  }


  /**
   */
  protected void unexecFireDisabling(Game p_game)
  {
    assert isFdComputed();
    p_game.getBoardFireCover().addFireDisabling( getFdRemoved() );
    p_game.getBoardFireCover().removeFireDisabling( getFdAdded() );

  }

  protected void execFireDisabling(Game p_game, AnBoardPosition p_position)
  {
    if( isFdComputed() )
    {
      // save CPU by avoiding recompute fire disabling flags
      p_game.getBoardFireCover().addFireDisabling( getFdAdded() );
      p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
    }
    else
    {
      // TODO we may avoid useless computing if token wasn't a destroyer !
      int fireRange = 3; // as it's the maximum fire length...
      List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
      List<FireDisabling> fdAdded = new ArrayList<FireDisabling>();

      // fire range to look for all tokens that may be impacted
      p_game.getBoardFireCover().checkFireDisableFlag( p_position, fireRange, FdChange.ALL,
          fdRemoved, fdAdded );

      p_game.getBoardFireCover().cleanFireDisableCollection( fdRemoved, fdAdded );

      addFdRemoved( fdRemoved );
      addFdAdded( fdAdded );
      setFdComputed( true );
    }
  }

  /**
   * recompute fire cover and check for all token his fire disabled flag
   * @param p_game
   */
  protected void execFireDisabling(Game p_game)
  {
    if( isFdComputed() )
    {
      // save CPU by avoiding recompute fire disabling flags
      p_game.getBoardFireCover().addFireDisabling( getFdAdded() );
      p_game.getBoardFireCover().removeFireDisabling( getFdRemoved() );
    }
    else
    {
      List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
      List<FireDisabling> fdAdded = new ArrayList<FireDisabling>();
      p_game.getBoardFireCover().reComputeFireCover( fdRemoved, fdAdded );
      addFdRemoved( fdRemoved );
      addFdAdded( fdAdded );
      setFdComputed( true );
    }
  }


}
