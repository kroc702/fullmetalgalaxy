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
package com.fullmetalgalaxy.model;


import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Kroc
 * 
 */
public interface GameServicesAsync
{

  /**
   * Add a new game.
   * Must have a new name.
   * @param game description of the game.
   * @return id of the created game
   */
  public void saveGame(Game game, AsyncCallback<EbBase> callback);

  /**
   * same as above, but let user set a message about his modifications
   * @param game
   * @param p_modifDesc
   * @return
   * @throws RpcFmpException
   */
  public void saveGame(Game game, String p_modifDesc, AsyncCallback<EbBase> callback);


  /**
   * Get all informations concerning the specific game.
   * @param p_gameId Id of the requested game
   * @return
   */
  public void getModelFmpInit(String p_gameId, AsyncCallback<ModelFmpInit> callback);


  /**
   * get additional game events of a given game.
   * @param p_gameId
   * @return
   * @throws RpcFmpException
   */
  public void getAdditionalGameLog(long p_gameId, AsyncCallback<EbGameLog> callback);

  public void checkUpdate(long p_gameId, AsyncCallback<Void> callback);


  public void runModelUpdate(ModelFmpUpdate p_modelUpdate, AsyncCallback<ModelFmpUpdate> callback);

  /**
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public void getModelFmpUpdate(long p_gameId, AsyncCallback<ModelFmpUpdate> callback);


  /**
   * 
   * @param p_message
   * @throws RpcFmpException
   */
  public void sendChatMessage(ChatMessage p_message, AsyncCallback<Void> callback);


  public void disconnect(Presence p_presence, AsyncCallback<Void> callback);

  public void reconnect(Presence p_presence, AsyncCallback<String> callback);
  
  public void getChatMessage(long p_gameId, AsyncCallback<ChatMessage> callback);

  public void getRoom(long p_gameId, AsyncCallback<PresenceRoom> callback);

  void getUpdate(long p_gameId, long p_myVersion, AsyncCallback<ModelFmpUpdate> callback);


}
