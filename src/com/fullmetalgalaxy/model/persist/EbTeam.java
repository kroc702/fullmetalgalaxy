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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.googlecode.objectify.annotation.Serialized;

/**
 * @author Vincent
 *
 */
public class EbTeam extends EbBase
{
  private static final long serialVersionUID = 1L;

  private List<Long> m_playerIds = new ArrayList<Long>();

  private Company m_company = Company.Freelancer;

  /** the single color of common fire cover */
  private int m_fireColor = EnuColor.Unknown;
  /** all colors controlled at least by on player of the team */
  @Transient
  private transient int m_colors = EnuColor.Unknown;

  private int m_orderIndex = 0;
  /** in turn by turn mode this is the end turn date.
   * and in parallel mode, this is the date up to which the board is locked (cf m_lockedPosition) */
  private Date m_endTurnDate = null;
  /** in parallel mode, player lock a board area for a small period */
  @Serialized
  private AnBoardPosition m_lockedPosition = null;

  /**
   * action list that player made during a parallel and hidden turn (ie deployement or take off turn)
   * these actions are seen by player but hidden to others. This event list will be merged with main log
   * at the end of current turn.
   */
  @Serialized
  protected List<AnEvent> m_myEvents = null;

  @Serialized
  private StatsPlayer m_stats = null;



  public int getOnBoardFreighterCount(Game p_game)
  {
    EnuColor colors = new EnuColor( getColors( p_game.getPreview() ) );
    int freighterCount = colors.getNbColor();
    // after turn 21, we really count number of landed freighter
    if( p_game.getCurrentTimeStep() >= p_game.getEbConfigGameTime().getTakeOffTurns().get( 0 ) )
    {
      freighterCount = 0;
      for( EbToken freighter : p_game.getAllFreighter( colors.getValue() ) )
      {
        if( freighter.getLocation() == Location.Board )
          freighterCount++;
      }
    }
    return freighterCount;
  }

  /**
   * 
   */
  public void clearColorsCache()
  {
    m_colors = EnuColor.Unknown;
  }

  /**
   * if colors cache is empty, compute the sum of all players colors
   * @param p_game
   * @return
   */
  public int getColors(EbGamePreview p_game)
  {
    if( m_colors == EnuColor.Unknown )
    {
      for( EbRegistration registration : getPlayers( p_game ) )
      {
        m_colors = EnuColor.addColor( m_colors, registration.getColor() );
      }
    }
    return m_colors;
  }

  public List<Long> getPlayerIds()
  {
    return m_playerIds;
  }

  public List<EbRegistration> getPlayers(EbGamePreview p_game)
  {
    List<EbRegistration> registrationList = new ArrayList<EbRegistration>();
    for( long playerId : m_playerIds )
    {
      registrationList.add( p_game.getRegistration( playerId ) );
    }
    return registrationList;
  }

  public int estimateWinningScore(Game p_game)
  {
    int winningScore = 0;
    for( EbRegistration registration : getPlayers( p_game.getPreview() ) )
    {
      winningScore += registration.estimateWinningScore( p_game );
    }
    return winningScore;
  }


  public Company getCompany()
  {
    return m_company;
  }

  public void setCompany(Company p_company)
  {
    m_company = p_company;
  }

  public int getFireColor()
  {
    return m_fireColor;
  }

  public void setFireColor(int p_fireColor)
  {
    m_fireColor = p_fireColor;
  }

  public int getOrderIndex()
  {
    return m_orderIndex;
  }

  public void setOrderIndex(int p_orderIndex)
  {
    m_orderIndex = p_orderIndex;
  }

  public Date getEndTurnDate()
  {
    return m_endTurnDate;
  }

  public void setEndTurnDate(Date p_endTurnDate)
  {
    m_endTurnDate = p_endTurnDate;
  }

  public AnBoardPosition getLockedPosition()
  {
    return m_lockedPosition;
  }

  public void setLockedPosition(AnBoardPosition p_lockedPosition)
  {
    m_lockedPosition = p_lockedPosition;
  }

  /**
   * don't use this method to add event !
   * @return a read only list
   */
  public List<AnEvent> getMyEvents()
  {
    if( m_myEvents == null )
    {
      return new ArrayList<AnEvent>();
    }
    return m_myEvents;
  }

  public void setMyEvents(List<AnEvent> p_myEvents)
  {
    m_myEvents = p_myEvents;
  }

  public void clearMyEvents()
  {
    m_myEvents = null;
  }

  public void addMyEvent(AnEvent p_action)
  {
    if( m_myEvents == null )
    {
      m_myEvents = new ArrayList<AnEvent>();
    }
    if( !m_myEvents.contains( p_action ) )
    {
      m_myEvents.add( p_action );
    }
  }


  public boolean haveAccount(EbGamePreview p_game)
  {
    for( EbRegistration registration : getPlayers( p_game ) )
    {
      if( registration.haveAccount() )
        return true;
    }
    return false;
  }



}
