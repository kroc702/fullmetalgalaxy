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
import com.fullmetalgalaxy.model.persist.EbGame;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtTide extends AnEvent
{
  static final long serialVersionUID = 1;



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
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtTide;
  }


  @Override
  public void check(EbGame p_game) throws RpcFmpException
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
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbGame game = p_game;
    assert game != null;
    game.setCurrentTide( game.getNextTide() );
    game.setNextTide( getNextTide() );
    game.setLastTideChange( game.getCurrentTimeStep() );
    game.invalidateFireCover();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbGame game = p_game;
    assert game != null;
    game.setNextTide( game.getCurrentTide() );
    game.setCurrentTide( getOldTide() );
    game.invalidateFireCover();
    game.setLastTideChange( getOldTideChange() );
  }



  @Override
  public void setGame(EbGame p_game)
  {
    assert p_game != null;
    setIdGame( p_game.getId() );

    setOldTide( p_game.getCurrentTide() );
    setOldTideChange( p_game.getLastTideChange() );

  }


  // Bean getter / setter
  // ====================

}
