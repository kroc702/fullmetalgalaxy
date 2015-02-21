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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;

/**
 * @author Vincent Legendre
 *
 */
public class EbEvtChangePlayerOrder extends AnEvent
{
  static final long serialVersionUID = 1;

  /** registration term is a legacy: it is the team order */
  private ArrayList<Long> m_oldRegistrationOrder = null;
  private ArrayList<Long> m_newRegistrationOrder = null;
  
  /**
   * 
   */
  public EbEvtChangePlayerOrder()
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
    m_oldRegistrationOrder = null;
    m_newRegistrationOrder = new ArrayList<Long>();
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtChangePlayerOrder;
  }

  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( getNewTeamOrder().size() != p_game.getTeams().size() )
    {
      throw new RpcFmpException( "EbEvtChangePlayerOrder isn't well configured." );
    }
  }
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    // backup for unexec
    List<EbTeam> sortedTeams = p_game.getTeamByPlayOrder();
    m_oldRegistrationOrder = new ArrayList<Long>();
    for( EbTeam team : sortedTeams )
    {
      m_oldRegistrationOrder.add( team.getId() );
    }
    
    int orderIndex = 0;
    for(long idTeam : getNewTeamOrder())
    {
      p_game.getTeam( idTeam ).setOrderIndex( orderIndex );
      orderIndex++;
    }

    p_game.getCurrentPlayerIds().clear();
    if( p_game.isTimeStepParallelHidden( p_game.getCurrentTimeStep() ) )
    {
      // a change player order occur after landing, next turn is deployment
      // and all player can play simultaneously
      for( EbTeam team : sortedTeams )
      {
        p_game.getCurrentPlayerIds().addAll( team.getPlayerIds() );
      }
    }
    else
    {
      // only first player can play
      p_game.getCurrentPlayerIds().addAll( p_game.getTeamByOrderIndex( 0 ).getPlayerIds() );
    }
  }
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    assert m_oldRegistrationOrder != null;
    
    int orderIndex = 0;
    long lastPlayerId = 0;
    for(long idTeam : m_oldRegistrationOrder)
    {
      p_game.getTeam( idTeam ).setOrderIndex( orderIndex );
      orderIndex++;
      lastPlayerId = idTeam;
    }
    p_game.getCurrentPlayerIds().clear();
    p_game.getCurrentPlayerIds().add( lastPlayerId );
  }

  public ArrayList<Long> getNewTeamOrder()
  {
    return m_newRegistrationOrder;
  }


  /**
   * Establish a new registration order at random
   * @param p_game
   */
  public void initRandomOrder(Game p_game)
  {
    getNewTeamOrder().clear();
    for( EbTeam team : p_game.getTeams() )
    {
      getNewTeamOrder().add( RpcUtil.random( getNewTeamOrder().size()+1 ), team.getId() );
    }
  }
  
  /**
   * Establish a new registration order according to the followings rules:
   * - first player chosed at random
   * - then in clockwise order on board
   * @param p_game
   */
  public void initBoardOrder(Game p_game)
  {
    int endAngle = 3;
    getNewTeamOrder().clear();
    // compute an angle for each player
    Map<Double, EbTeam> thetaTeamMap = new HashMap<Double, EbTeam>();
    for( EbTeam team : p_game.getTeams() )
    {
      EbRegistration registration = p_game.getRegistration( team.getPlayerIds().get( 0 ) );
      EbToken token = p_game.getFreighter( registration );
      if(token != null && token.getLocation() == Location.Board)
      {
        thetaTeamMap.put(
            Math.atan2( token.getPosition().getY() - (p_game.getLandHeight() / 2), token
                .getPosition().getX() - (p_game.getLandWidth() / 2) ), team );
      } else {
        // freighter isn't on board, it will play at the end.
        thetaTeamMap.put( endAngle * Math.PI, team );
        endAngle++;
      }
    }
    
    // determine order according angle
    Double theta = 999D;
    while(!thetaTeamMap.isEmpty())
    {
      for( Map.Entry<Double, EbTeam> entry : thetaTeamMap.entrySet() )
      {
        if(theta > entry.getKey())
        {
          theta = entry.getKey();
        }
      }
      getNewTeamOrder().add( thetaTeamMap.get( theta ).getId() );
      thetaTeamMap.remove( theta );
      theta = 999D;
    }
  }
  
}
