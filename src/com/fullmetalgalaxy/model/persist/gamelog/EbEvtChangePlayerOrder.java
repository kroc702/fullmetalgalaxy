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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;

/**
 * @author Vincent Legendre
 *
 */
public class EbEvtChangePlayerOrder extends AnEvent
{
  static final long serialVersionUID = 1;

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
    if(getNewRegistrationOrder().size() != p_game.getSetRegistration().size())
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
    List<EbRegistration> sortedRegistration = p_game.getRegistrationByPlayerOrder();
    m_oldRegistrationOrder = new ArrayList<Long>();
    for(EbRegistration registration : sortedRegistration)
    {
      m_oldRegistrationOrder.add( registration.getId() );
    }
    
    int orderIndex = 0;
    for(long idRegistration : getNewRegistrationOrder())
    {
      p_game.getRegistration( idRegistration ).setOrderIndex( orderIndex );
      orderIndex++;
    }
    p_game.setCurrentPlayerRegistration( p_game.getRegistrationByOrderIndex( 0 ) );
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
    for(long idRegistration : m_oldRegistrationOrder)
    {
      p_game.getRegistration( idRegistration ).setOrderIndex( orderIndex );
      orderIndex++;
    }
  }

  public ArrayList<Long> getNewRegistrationOrder()
  {
    return m_newRegistrationOrder;
  }


  /**
   * Establish a new registration order at random
   * @param p_game
   */
  public void initRandomOrder(Game p_game)
  {
    getNewRegistrationOrder().clear();
    for(EbRegistration registration : p_game.getSetRegistration() )
    {
      getNewRegistrationOrder().add( RpcUtil.random( getNewRegistrationOrder().size()+1 ), registration.getId() );
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
    getNewRegistrationOrder().clear();
    // compute an angle for each player
    Map<Double,EbRegistration> thetaRegistrationMap = new HashMap<Double,EbRegistration>();
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      EbToken token = p_game.getFreighter( registration );
      if(token != null && token.getLocation() == Location.Board)
      {
        thetaRegistrationMap.put( Math.atan2( token.getPosition().getY()-p_game.getLandHeight()/2 ,
            token.getPosition().getX()-p_game.getLandWidth()/2), registration );
      } else {
        // freighter isn't on board, it will play at the end.
        thetaRegistrationMap.put( 3*Math.PI, registration );
      }
    }
    
    // determine order according angle
    Double theta = 999D;
    while(!thetaRegistrationMap.isEmpty())
    {
      for(Map.Entry<Double,EbRegistration> entry : thetaRegistrationMap.entrySet() )
      {
        if(theta > entry.getKey())
        {
          theta = entry.getKey();
        }
      }
      getNewRegistrationOrder().add( thetaRegistrationMap.get( theta ).getId() );
      thetaRegistrationMap.remove( theta );
      theta = 999D;
    }
  }
  
}
