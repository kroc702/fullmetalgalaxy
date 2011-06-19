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

import javax.persistence.Id;

/**
 * Store all statistics related to the entire game or site
 * 
 * @author vlegendr
 *
 */
public class EbGlobalStat
{
  private static final long ID = 1;
  
  private static EbGlobalStat s_instance = null;
  
  @Id
  private Long m_id = ID;
  
  private int m_accountCount = 0;
  private int m_activeAccountCount = 0;
  private int m_maxLevel = 1;

  private int m_gameCount = 0;
  private int m_openGameCount = 0;
  private int m_currentGameCount = 0;
  
  
  public EbGlobalStat()
  {
    
  }
  
  
  public static EbGlobalStat instance()
  {
    if( s_instance == null )
    {
      s_instance = FmgDataStore.dao().get( EbGlobalStat.class, ID );
    }
    if( s_instance == null )
    {
      s_instance = new EbGlobalStat();
    }
    return s_instance;
  }
  
  public void save()
  {
    FmgDataStore ds = new FmgDataStore( false );
    ds.put( this );
    ds.close();
  }

  // getter / setter
  // ===============

  public int getAccountCount()
  {
    return m_accountCount;
  }


  public void setAccountCount(int p_accountCount)
  {
    m_accountCount = p_accountCount;
  }


  public int getActiveAccountCount()
  {
    return m_activeAccountCount;
  }


  public void setActiveAccountCount(int p_activeAccountCount)
  {
    m_activeAccountCount = p_activeAccountCount;
  }


  public int getMaxLevel()
  {
    return m_maxLevel;
  }


  public void setMaxLevel(int p_maxLevel)
  {
    m_maxLevel = p_maxLevel;
  }


  public int getGameCount()
  {
    return m_gameCount;
  }


  public void setGameCount(int p_gameCount)
  {
    m_gameCount = p_gameCount;
  }


  public int getOpenGameCount()
  {
    return m_openGameCount;
  }


  public void setOpenGameCount(int p_openGameCount)
  {
    m_openGameCount = p_openGameCount;
  }


  public int getCurrentGameCount()
  {
    return m_currentGameCount;
  }


  public void setCurrentGameCount(int p_currentGameCount)
  {
    m_currentGameCount = p_currentGameCount;
  }

}
