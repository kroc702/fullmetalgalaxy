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

import java.util.List;


/**
 * @author Vincent Legendre
 *
 */
public class EbRegistrationStats extends EbBase
{
  static final long serialVersionUID = 1;

  /** game final points */
  private int m_winningPoints = 0;
  /** game final rank (1 for winner) */
  private int m_gameRank = 0;
  private int m_fireCount = 0;
  private int m_controlCount = 0;
  private int m_oreCount = 0;
  private int m_tokenCount = 0;

  public EbRegistrationStats()
  {
    super();
    init();
  }

  public EbRegistrationStats(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
    m_winningPoints = 0;
    m_gameRank = 0;
    m_fireCount = 0;
    m_controlCount = 0;
    m_oreCount = 0;
    m_tokenCount = 0;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  public static EbRegistrationStats generate(EbRegistration p_registration, EbGame p_game)
  {
    EbRegistrationStats stats = new EbRegistrationStats();
    stats.setOreCount( p_registration.getOreCount() );
    stats.setTokenCount( p_registration.getTokenCount() );
    stats.setWinningPoints( p_registration.getWinningPoint() );

    /*for( AnEvent event : p_game.getLogs() )
    {
      if( event instanceof EbEvtControl )
      {
        EbEvtControl control = (EbEvtControl)event;
        if( control.getRegistration().getId() == p_registration.getId() )
        {
          stats.setControlCount( stats.getControlCount() + 1 );
        }
      }
      else if( event instanceof EbEvtFire )
      {
        EbEvtFire fire = (EbEvtFire)event;
        if( fire.getRegistration().getId() == p_registration.getId() )
        {
          stats.setFireCount( stats.getFireCount() + 1 );
        }
      }
    }*/

    List<EbRegistration> sortedRegistration = p_game.getRegistrationByWinningRank();
    int index = 0;
    while( index < sortedRegistration.size() )
    {
      if( sortedRegistration.get( index ) == p_registration )
      {
        stats.setGameRank( index + 1 );
        break;
      }
      index++;
    }

    return stats;
  }

  // getters / setters
  // -----------------

  /**
   * @return the gameRank
   */
  public int getGameRank()
  {
    return m_gameRank;
  }

  /**
   * @param p_gameRank the gameRank to set
   */
  public void setGameRank(int p_gameRank)
  {
    m_gameRank = p_gameRank;
  }

  /**
   * @return the fireCount
   */
  public int getFireCount()
  {
    return m_fireCount;
  }

  /**
   * @param p_fireCount the fireCount to set
   */
  public void setFireCount(int p_fireCount)
  {
    m_fireCount = p_fireCount;
  }

  /**
   * @return the controlCount
   */
  public int getControlCount()
  {
    return m_controlCount;
  }

  /**
   * @param p_controlCount the controlCount to set
   */
  public void setControlCount(int p_controlCount)
  {
    m_controlCount = p_controlCount;
  }

  /**
   * @return the oreCount
   */
  public int getOreCount()
  {
    return m_oreCount;
  }

  /**
   * @param p_oreCount the oreCount to set
   */
  public void setOreCount(int p_oreCount)
  {
    m_oreCount = p_oreCount;
  }

  /**
   * @return the tokenCount
   */
  public int getTokenCount()
  {
    return m_tokenCount;
  }

  /**
   * @param p_tokenCount the tokenCount to set
   */
  public void setTokenCount(int p_tokenCount)
  {
    m_tokenCount = p_tokenCount;
  }

  /**
   * @return the winningPoints
   */
  public int getWinningPoints()
  {
    return m_winningPoints;
  }

  /**
   * @param p_winningPoints the winningPoints to set
   */
  public void setWinningPoints(int p_winningPoints)
  {
    m_winningPoints = p_winningPoints;
  }



}
