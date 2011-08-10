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
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;


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
  /** unit loosed due to opponent control or fire action */
  private int m_losedUnitCount = 0;
  /** freighter loosed due to opponent control */
  private int m_losedFreighterCount = 0;
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

  public StatsGamePlayer(Game p_game)
  {
    super( p_game );
    init();
  }

  public StatsGamePlayer(Game p_game, EbRegistration p_registration)
  {
    super( p_game );
    init();

    setPlayer( p_game, p_registration );
  }

  private void init()
  {
    m_gameRank = 0;
    m_fireCount = 0;
    m_unitControlCount = 0;
    m_freighterControlCount = 0;
    m_losedUnitCount = 0;
    m_losedFreighterCount = 0;
    m_initialColor = EnuColor.None;
    m_finalColor = EnuColor.None;
    m_constructionCount = 0;
    m_oreCount = 0;
    m_tokenCount = 0;
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  public void setPlayer(Game p_game, EbRegistration p_registration)
  {
    /*
     * TODO set final score !
     */
    setOreCount( p_registration.getOreCount( p_game ) );
    setTokenCount( p_registration.getTokenCount( p_game ) );
    setFinalColor( p_registration.getColor() );
    
    EnuColor currentColor = new EnuColor(EnuColor.None);
    
    // stats.setWinningPoints( p_registration.getWinningPoint() );

    for( AnEvent event : p_game.getLogs() )
    {
      if( event  instanceof AnEventPlay )
      {
        // TODO getMyRegistration depend of account id...
        // this code will fail in case of replacement
        if( ((AnEventPlay)event).getMyRegistration( p_game ).getId() == p_registration.getId() )
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
            currentColor.addColor( ((EbEvtControlFreighter)event).getTokenFreighter( p_game ).getColor() );
          }
        }
        else if( event instanceof EbEvtControlFreighter
            && currentColor.isColored( ((EbEvtControlFreighter)event).getTokenFreighter( p_game ).getColor() ) )
        {
          setLosedFreighterCount( getLosedFreighterCount() + 1 );
          currentColor.removeColor( ((EbEvtControlFreighter)event).getTokenFreighter( p_game ).getColor() );
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
      else if( (event instanceof EbGameJoin)
          && ((EbGameJoin)event).getMyRegistration( p_game ).getId() == p_registration.getId() )
      {
        // here because original color can change if he lose his original freighter
        setInitialColor( ((EbGameJoin)event).getColor() );
        currentColor.setValue( getInitialColor() );
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



  public PlayerStyle getPlayerStyle()
  {
    int attack = getUnitControlCount() + getFireCount() + getFreighterControlCount() * 2;
    int nbColor = new EnuColor( getFinalColor() ).getNbColor();
    if( nbColor == 0 )
    {
      // oups, player was captured...
      return PlayerStyle.Sheep;
    }
    float mine = getOreCount() / nbColor;
    if( mine == 0 && attack > 0 )
    {
      return PlayerStyle.Aggressive;
    }
    float balance = attack / mine;
    if( balance > 1.9 )
    {
      return PlayerStyle.Aggressive;
    }
    if( balance < 0.5 )
    {
      return PlayerStyle.Pacific;
    }
    return PlayerStyle.Balanced;
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
