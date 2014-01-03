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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;

/**
 * @author Vincent
 *
 */
public class AnEventList extends ArrayList<GameEvent> implements GameEvent
{
  private static final long serialVersionUID = 1L;

  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    for( GameEvent event : this )
    {
      event.check( p_game );
    }
  }

  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    for( GameEvent event : this )
    {
      event.exec( p_game );
    }
  }

  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    for( GameEvent event : this )
    {
      event.unexec( p_game );
    }
  }

}
