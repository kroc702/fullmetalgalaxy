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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;

import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Vincent Legendre
 *
 */
public class StatsPlayer implements java.io.Serializable, IsSerializable
{
  static final long serialVersionUID = 1;

  /** game final score
   * may differ from oreCount + tokenCount as ore may have different value.
   * */
  private int m_finalScore = 0;


  /** game final rank (1 for winner) */
  private int m_gameRank = 0;
  /** fire action count during game */
  private int m_fireCount = 0;
  /** unit control action count during game */
  private int m_unitControlCount = 0;
  /** freighter control action count during game */
  private int m_freighterControlCount = 0;
  /** unit loosed due to opponent control or fire action */
  private int m_losedUnitCount = 0;
  /** freighter loosed due to opponent control */
  private int m_losedFreighterCount = 0;
  /** construct action count during game */
  private int m_constructionCount = 0;
  /** ore count in freighter at end of game */
  private int m_oreCount = 0;
  /** unit count in freighter at end of game */
  private int m_tokenCount = 0;


  /**
   * used for serialization policy
   */
  protected StatsPlayer()
  {
  }


  /**
   * set a lot of statistic for this game/player
   * @param p_game
   * @param p_registration
   */
  public StatsPlayer(Game p_game, EbRegistration p_registration)
  {
    if( p_registration == null )
    {
      return;
    }
    setOreCount( p_registration.getOreCount( p_game ) );
    setTokenCount( p_registration.getTokenCount( p_game ) );
    setFinalScore( p_registration.estimateWinningScore( p_game ) );
    
    EnuColor currentColor = new EnuColor(EnuColor.None);
    

    for( AnEvent event : p_game.getLogs() )
    {
      // if an error occur with one action, we don't mind: it's only stats !
      try
      {
        if( event instanceof AnEventPlay )
        {
          EbRegistration eventRegistration = ((AnEventPlay)event).getMyRegistration( p_game );
          if( eventRegistration == null )
          {
            // TODO getMyRegistration depend of account id...
            // in case of replacement eventRegistration will be null !
          }
          else if( eventRegistration.getId() == p_registration.getId() )
          {
            if( event instanceof EbEvtControl )
            {
              setUnitControlCount( getUnitControlCount() + 1 );
            }
            else if( event instanceof EbEvtFire )
            {
              setFireCount( getFireCount() + 1 );
            }
            else if( event instanceof EbEvtConstruct )
            {
              setConstructionCount( getConstructionCount() + 1 );
            }
            else if( event instanceof EbEvtControlFreighter )
            {
              setFreighterControlCount( getFreighterControlCount() + 1 );
              currentColor.addColor( ((EbEvtControlFreighter)event).getTokenFreighter( p_game )
                  .getColor() );
            }
          }
          else if( event instanceof EbEvtControlFreighter
              && currentColor.isColored( ((EbEvtControlFreighter)event).getTokenFreighter( p_game )
                  .getColor() ) )
          {
            setLosedFreighterCount( getLosedFreighterCount() + 1 );
            currentColor.removeColor( ((EbEvtControlFreighter)event).getTokenFreighter( p_game )
                .getColor() );
          }
          else if( event instanceof EbEvtControl
              && currentColor.isColored( ((EbEvtControl)event).getTokenTarget( p_game ).getColor() ) )
          {
            setLosedUnitCount( getLosedUnitCount() + 1 );
          }
          else if( event instanceof EbEvtFire
              && currentColor.isColored( ((EbEvtFire)event).getTokenTarget( p_game ).getColor() ) )
          {
            setLosedUnitCount( getLosedUnitCount() + 1 );
          }
        }
      } catch( Throwable th )
      {
      }
    }

    List<EbRegistration> sortedRegistration = p_game.getRegistrationByWinningRank();
    int index = 0;
    while( index < sortedRegistration.size() )
    {
      if( sortedRegistration.get( index ) == p_registration )
      {
        setGameRank( index + 1 );
        break;
      }
      index++;
    }

  }

  /**
   * compute a style ratio that can be used to determine player's style.
   * @return
   */
  public float getStyleRatio()
  {
    int finalFreighterCount = 1 + getFreighterControlCount() - getLosedFreighterCount();
    if( finalFreighterCount <= 0 )
      finalFreighterCount = 1;
    // +1 are here to avoid / zero
    return (getFireCount() + getUnitControlCount() + 4 * getFreighterControlCount()
        + getConstructionCount() + 1)
        / (getOreCount() / finalFreighterCount + getLosedUnitCount() + 1);
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


  /**
   * @return the losedFreighterCount
   */
  public int getLosedFreighterCount()
  {
    return m_losedFreighterCount;
  }

  /**
   * @param p_losedFreighterCount the losedFreighterCount to set
   */
  public void setLosedFreighterCount(int p_losedFreighterCount)
  {
    m_losedFreighterCount = p_losedFreighterCount;
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

  public int getFinalScore()
  {
    return m_finalScore;
  }

  public void setFinalScore(int p_score)
  {
    m_finalScore = p_score;
  }


}
