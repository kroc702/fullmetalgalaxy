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
package com.fullmetalgalaxy.model.persist;

import java.util.Date;

import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 *
 */
public class StatsGame extends EbAccountStats
{
  static final long serialVersionUID = 1;

  public enum Status implements IsSerializable
  {
    /** game is still running */
    Running,
    /** game is finished */
    Finished,
    /** game was canceled */
    Canceled,
    /** account was banned from this game */
    Banned;
  }

  // game short description
  private long m_gameId = 0;
  private String m_gameName = null;
  private int m_numberOfPlayer = 0;
  private int m_numberOfHexagon = 0;
  private ConfigGameTime m_configGameTime = null;
  private ConfigGameVariant m_configGameVariant = null;
  private Date m_gameCreation = new Date();
  
  /** current account status for this game */
  private Status m_status = Status.Running;
  private boolean m_isCreator = false;
  


  public StatsGame()
  {
    super();
    init();
  }

  public StatsGame(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
    m_gameId = 0;
    m_gameName = null;
    m_numberOfPlayer = 0;
    m_numberOfHexagon = 0;
    m_configGameTime = null;
    m_configGameVariant = null;
    m_gameCreation = new Date();

    m_isCreator = false;
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  public void setGame(Game p_game)
  {
    m_gameId = p_game.getId();
    m_gameName = p_game.getName();
    m_numberOfPlayer = p_game.getCurrentNumberOfRegiteredPlayer();
    m_numberOfHexagon = p_game.getNumberOfHexagon();
    m_configGameTime = p_game.getConfigGameTime();
    m_configGameVariant = p_game.getConfigGameVariant();
    m_gameCreation = p_game.getCreationDate();
  }

  // getters / setters
  // -----------------

  
  public long getGameId()
  {
    return m_gameId;
  }


  public void setGameId(long p_gameId)
  {
    m_gameId = p_gameId;
  }


  public String getGameName()
  {
    return m_gameName;
  }


  public void setGameName(String p_gameName)
  {
    m_gameName = p_gameName;
  }


  public int getNumberOfPlayer()
  {
    return m_numberOfPlayer;
  }


  public void setNumberOfPlayer(int p_numberOfPlayer)
  {
    m_numberOfPlayer = p_numberOfPlayer;
  }


  public ConfigGameTime getConfigGameTime()
  {
    return m_configGameTime;
  }


  public void setConfigGameTime(ConfigGameTime p_configGameTime)
  {
    m_configGameTime = p_configGameTime;
  }


  public ConfigGameVariant getConfigGameVariant()
  {
    return m_configGameVariant;
  }


  public void setConfigGameVariant(ConfigGameVariant p_configGameVariant)
  {
    m_configGameVariant = p_configGameVariant;
  }


  public boolean isCreator()
  {
    return m_isCreator;
  }


  public void setCreator(boolean p_isCreator)
  {
    m_isCreator = p_isCreator;
  }

  /**
   * @return the gameCreation
   */
  public Date getGameCreation()
  {
    return m_gameCreation;
  }

  /**
   * @param p_gameCreation the gameCreation to set
   */
  public void setGameCreation(Date p_gameCreation)
  {
    m_gameCreation = p_gameCreation;
  }

  /**
   * @return the numberOfHexagon
   */
  public int getNumberOfHexagon()
  {
    return m_numberOfHexagon;
  }

  /**
   * @param p_numberOfHexagon the numberOfHexagon to set
   */
  public void setNumberOfHexagon(int p_numberOfHexagon)
  {
    m_numberOfHexagon = p_numberOfHexagon;
  }

  /**
   * @return the status
   */
  public Status getStatus()
  {
    return m_status;
  }

  /**
   * @param p_status the status to set
   */
  public void setStatus(Status p_status)
  {
    m_status = p_status;
  }



}
