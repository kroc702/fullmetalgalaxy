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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtTide extends AnEvent
{
  static final long serialVersionUID = 1;

  private ArrayList<Long> m_PontoonIds = null;
  
  private Tide m_oldTide = null;
  private int m_oldTideChange = 0;
  private Tide m_nextTide = null;


  /**
   * 
   */
  public EbEvtTide()
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
    setAuto( true );
    m_PontoonIds = null;
    m_oldTide = null;
    m_nextTide = null;
    m_oldTideChange = 0;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtTide;
  }


  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    assert p_game.getCurrentTide() == getOldTide();
    if( !p_game.isStarted() )
    {
      // TODO i18n
      throw new RpcFmpException( "Cette partie n'est pas demarre" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    Game game = p_game;
    assert game != null;
    
    // backup before changes
    setOldTide( p_game.getCurrentTide() );
    setOldTideChange( p_game.getLastTideChange() );

    game.setCurrentTide( game.getNextTide() );
    game.setNextTide( game.getNextTide2() );
    game.setNextTide2( getNextTide() );
    game.setLastTideChange( game.getCurrentTimeStep() );
    
    // check that all pontoon are still linked to ground
    m_PontoonIds = new ArrayList<Long>();
    if(getOldTide().ordinal() < getNextTide().ordinal() )
    {
      for(EbToken token : p_game.getSetToken())
      {
        if( token.getType() == TokenType.Pontoon 
            && token.getLocation() == Location.Board
            && !p_game.isPontoonLinkToGround( token ) )
        {
          m_PontoonIds.addAll( p_game.chainRemovePontoon( token, getFdRemoved() ) );
        }
      }
    }
    
    execFireDisabling( p_game );
    // TODO we may store tide disable flag to avoid recompute all fire cover
    p_game.getBoardFireCover().reComputeFireCover();

    // update all players weather hen count
    for( EbRegistration registration : game.getSetRegistration() )
    {
      registration.setWorkingWeatherHenCount( p_game.countWorkingWeatherHen( registration
          .getEnuColor() ) );
    }
    
  }

  /**
   * players weather hen count isn't unexec... I don't think it's a big issue
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    Game game = p_game;
    assert game != null;
    game.setNextTide2( game.getNextTide() );
    game.setNextTide( game.getCurrentTide() );
    game.setCurrentTide( getOldTide() );
    game.setLastTideChange( getOldTideChange() );
        
    // put back pontoon if there is some
    if( m_PontoonIds != null )
    {
      for( Long id : m_PontoonIds )
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) && (token.getLocation() == Location.Graveyard) )
        {
          p_game.moveToken( token, token.getPosition() );
          token.decVersion();
        }
      }
    }
    game.setLastTideChange( getOldTideChange() );
    
    unexecFireDisabling( p_game );
    // cf same exec function
    p_game.getBoardFireCover().reComputeFireCover();
  }



  // Bean getter / setter
  // ====================
  /**
   * @return the oldTide
   */
  private Tide getOldTide()
  {
    return m_oldTide;
  }


  /**
   * @param p_oldTide the oldTide to set
   */
  private void setOldTide(Tide p_oldTide)
  {
    m_oldTide = p_oldTide;
  }


  /**
   * @return the oldTideChange
   */
  private int getOldTideChange()
  {
    return m_oldTideChange;
  }


  /**
   * @param p_oldTideChange the oldTideChange to set
   */
  private void setOldTideChange(int p_oldTideChange)
  {
    m_oldTideChange = p_oldTideChange;
  }


  /**
   * @return the nextTide
   */
  public Tide getNextTide()
  {
    return m_nextTide;
  }


  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide(Tide p_nextTide)
  {
    m_nextTide = p_nextTide;
  }

}
