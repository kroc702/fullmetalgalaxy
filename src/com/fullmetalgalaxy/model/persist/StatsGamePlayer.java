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

import com.fullmetalgalaxy.model.EnuColor;


/**
 * @author Vincent Legendre
 *
 */
public class StatsGamePlayer extends StatsGame
{
  static final long serialVersionUID = 1;

  /** game final rank (1 for winner) */
  private int m_gameRank = 0;
  /** fire action count during game */
  private int m_fireCount = 0;
  /** unit control action count during game */
  private int m_unitControlCount = 0;
  /** freighter control action count during game */
  private int m_freighterControlCount = 0;
  /** unit losed due to opponent control or fire action */
  private int m_losedUnitCount = 0;
  /** players color at beginning of game */
  private int m_initialColor = EnuColor.None;
  /** players color at end of game */
  private int m_finalColor = EnuColor.None;
  /** construct action count during game */
  private int m_constructionCount = 0;
  /** ore count in freighter at end of game */
  private int m_oreCount = 0;
  /** unit count in freighter at end of game */
  private int m_tokenCount = 0;


  public StatsGamePlayer()
  {
    super();
    init();
  }

  public StatsGamePlayer(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
    m_gameRank = 0;
    m_fireCount = 0;
    m_unitControlCount = 0;
    m_freighterControlCount = 0;
    m_oreCount = 0;
    m_tokenCount = 0;
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  public static StatsGamePlayer generate(EbRegistration p_registration, Game p_game)
  {
    StatsGamePlayer stats = new StatsGamePlayer();
    stats.setGame( p_game );

    stats.setOreCount( p_registration.getOreCount(p_game) );
    stats.setTokenCount( p_registration.getTokenCount(p_game) );
    //stats.setWinningPoints( p_registration.getWinningPoint() );

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

  public int getGameRank()
  {
    return m_gameRank;
  }

  public void setGameRank(int p_gameRank)
  {
    m_gameRank = p_gameRank;
  }


  public int getFireCount()
  {
    return m_fireCount;
  }


  public void setFireCount(int p_fireCount)
  {
    m_fireCount = p_fireCount;
  }


  public int getUnitControlCount()
  {
    return m_unitControlCount;
  }


  public void setUnitControlCount(int p_unitControlCount)
  {
    m_unitControlCount = p_unitControlCount;
  }


  public int getFreighterControlCount()
  {
    return m_freighterControlCount;
  }


  public void setFreighterControlCount(int p_freighterControlCount)
  {
    m_freighterControlCount = p_freighterControlCount;
  }


  public int getLosedUnitCount()
  {
    return m_losedUnitCount;
  }


  public void setLosedUnitCount(int p_losedUnitCount)
  {
    m_losedUnitCount = p_losedUnitCount;
  }


  public int getInitialColor()
  {
    return m_initialColor;
  }


  public void setInitialColor(int p_initialColor)
  {
    m_initialColor = p_initialColor;
  }


  public int getFinalColor()
  {
    return m_finalColor;
  }


  public void setFinalColor(int p_finalColor)
  {
    m_finalColor = p_finalColor;
  }


  public int getConstructionCount()
  {
    return m_constructionCount;
  }


  public void setConstructionCount(int p_constructionCount)
  {
    m_constructionCount = p_constructionCount;
  }


  public int getOreCount()
  {
    return m_oreCount;
  }


  public void setOreCount(int p_oreCount)
  {
    m_oreCount = p_oreCount;
  }


  public int getTokenCount()
  {
    return m_tokenCount;
  }


  public void setTokenCount(int p_tokenCount)
  {
    m_tokenCount = p_tokenCount;
  }



}
