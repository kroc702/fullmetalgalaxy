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

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent
 *
 * all event that modify game data should implement the following interface
 */
public interface GameEvent extends java.io.Serializable, IsSerializable
{
  /**
   * check this action is allowed.
   * you have to override this method.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void check(Game p_game) throws RpcFmpException;

  /**
   * execute this action
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void exec(Game p_game) throws RpcFmpException;

  /**
   * un execute this action. ie undo what exec did.
   * Note that you can't undo an action which isn't executed. This mean that you have to execute 
   * this action to let backup all required information to launch unexec BEFORE savinf it into
   * database.
   * @param p_game game to apply event
   * @throws RpcFmpException
   */
  public void unexec(Game p_game) throws RpcFmpException;

}
