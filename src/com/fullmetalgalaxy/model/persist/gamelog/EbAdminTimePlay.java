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

import java.util.Date;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
public class EbAdminTimePlay extends EbAdmin
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public EbAdminTimePlay()
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

  @Override
  public GameLogType getType()
  {
    return GameLogType.AdminTimePlay;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    // only admin or game creator sould be able to do this
    // TODO how to check my account is admin ?
    if( p_game.isStarted() )
    {
      // no i18n (hmi don't display button in this case)
      throw new RpcFmpException( "la partie est deja en cours" );
    }
    if( p_game.getCurrentNumberOfRegiteredPlayer() < 2 )
    {
      // TODO i18n
      throw new RpcFmpException( "Pour démarrer une partie il faut au moins deux joueurs" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    p_game.setStarted( true );
    if( !p_game.isAsynchron() )
    {
      // every player but me shouldn't have a time constain
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        registration.setEndTurnDate( null );
      }
      EbRegistration myRegistration = getMyRegistration(p_game);
      if( myRegistration != null )
      {
        myRegistration.setEndTurnDate( new Date( System.currentTimeMillis()
            + p_game.getFullTurnDurationInMili() ) );
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += " : Play";
    return str;
  }


  // Bean getter / setter
  // ====================

}
