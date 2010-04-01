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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
// @MappedSuperclass
public class AnEventPlay extends AnEventUser
{
  static final long serialVersionUID = 1;



  public AnEventPlay()
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

  }

  /**
   * @param p_game TODO
   * @return a board position where action is done or null if not relevant.
   */
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    return null;
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( isAuto() )
    {
      // if event is auto generated, assume everything are correct
      return;
    }
    EbRegistration registration = getMyRegistration(p_game);
    if( registration == null )
    {
      throw new RpcFmpException( RpcFmpException.YouDidntJoinThisGame );
    }
    if( registration.getPtAction() < getCost() )
    {
      throw new RpcFmpException( RpcFmpException.NotEnouthActionPt );
    }
    if( (!p_game.isAsynchron()) && (p_game.getCurrentPlayerRegistration() != registration) )
    {
      throw new RpcFmpException( RpcFmpException.NotYourTurn );
    }
    if( !p_game.isStarted() )
    {
      // TODO i18n
      throw new RpcFmpException( "Cette partie n'est pas demarre" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbRegistration registration = getMyRegistration(p_game);
    if( registration != null )
    {
      registration.setPtAction( registration.getPtAction() - getCost() );
      // registration.setLastUpdate( getLastUpdate() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbRegistration registration = getMyRegistration(p_game);
    if( registration != null )
    {
      registration.setPtAction( registration.getPtAction() + getCost() );
      // registration.setLastUpdate( getOldUpdate() );
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    String name = getClass().getName();
    if( name.startsWith( "com.fullmetalgalaxy.model.persist.gamelog.EbEvt" ) )
    {
      name = name.substring( 37 );
    }
    str += " : " + name;
    return str;
  }


  // Bean getter / setter
  // ====================


}
