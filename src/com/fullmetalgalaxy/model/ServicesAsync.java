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
package com.fullmetalgalaxy.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Kroc
 * 
 */
public interface ServicesAsync
{

  /**
   * simply ask server for user's identity. 
   */
  public void askForIdentity(AsyncCallback<EbAccount> callback);


  /**
   * @param whereClause to select a subset of games.
   * @return an array of all games found.
   */
  public void getGameList(GameFilter p_filter,
      AsyncCallback<List<com.fullmetalgalaxy.model.persist.EbGamePreview>> callback);


  /**
   * Add a new game.
   * Must have a new name.
   * @param game description of the game.
   * @return id of the created game
   */
  public void saveGame(EbGame game, AsyncCallback<EbBase> callback);

  /**
   * same as above, but let user set a message about his modifications
   * @param game
   * @param p_modifDesc
   * @return
   * @throws RpcFmpException
   */
  public void saveGame(EbGame game, String p_modifDesc, AsyncCallback<EbBase> callback);


  /**
   * remove the given game from database
   * @param p_id
   * @throws RpcFmpException
   */
  public void deleteGame(long p_gameId, AsyncCallback<Void> callback);

  /**
   * cancel and history the given game.
   * @param p_id
   * @throws RpcFmpException
   */
  public void cancelGame(long p_gameId, AsyncCallback<Void> callback);

  /**
   * Get all informations concerning the specific game.
   * @param p_gameId Id of the requested game
   * @return
   */
  public void getModelFmpInit(String p_gameId, AsyncCallback<ModelFmpInit> callback);


  public void runEvent(AnEvent p_action, Date p_lastUpdate, AsyncCallback<ModelFmpUpdate> callback);

  public void runAction(ArrayList<AnEventPlay> p_actionList, Date p_lastUpdate,
      AsyncCallback<ModelFmpUpdate> callback);


  /**
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public void getModelFmpUpdate(long p_gameId, Date p_lastUpdate,
      AsyncCallback<ModelFmpUpdate> callback);


  /**
   * 
   * @param p_message
   * @throws RpcFmpException
   */
  public void sendChatMessage(ChatMessage p_message, Date p_lastUpdate,
      AsyncCallback<ModelFmpUpdate> callback);


}
