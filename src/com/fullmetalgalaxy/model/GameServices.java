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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Kroc
 * 
 */
@RemoteServiceRelativePath("Services")
public interface GameServices extends RemoteService
{

  /**
   * create (if id=0) or save a game.
   * @param game description of the game.
   * @return id/lastUpdate of the created/saved game
   */
  public EbBase saveGame(Game game) throws RpcFmpException;

  /**
   * same as above, but let user set a message about his modifications
   * @param game
   * @param p_modifDesc
   * @return id/lastUpdate of the created/saved game
   * @throws RpcFmpException
   */
  public EbBase saveGame(Game game, String p_modifDesc) throws RpcFmpException;


  /**
   * This method shouldn't be used anymore as init is now injected in jsp.
   * 
   * Get all informations concerning the specific game.
   * @param p_gameId Id of the requested game
   * @return
   */
  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException;


  /**
   * get additional game events of a given game.
   * @param p_gameId
   * @return
   * @throws RpcFmpException
   */
  public EbGameLog getAdditionalGameLog(long p_gameId) throws RpcFmpException;


  /**
   * ask server to check update for  the corresponding game.
   * If update are found, they will be send with channel connection.
   * @param p_gameId
   * @throws RpcFmpException
   */
  public void checkUpdate(long p_gameId) throws RpcFmpException;

  /**
   * this method should be used by client as a fallback if channel is not available.
   * in this case, client will poll server for update
   * @param p_gameId
   * @param p_myVersion
   * @return
   * @throws RpcFmpException
   */
  public ModelFmpUpdate getUpdate(long p_gameId, long p_myVersion) throws RpcFmpException;


  public ModelFmpUpdate runModelUpdate(ModelFmpUpdate p_modelUpdate) throws RpcFmpException;

  
  /**
   * This method shouldn't be used anymore as update is now send with channel API.
   * 
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public ModelFmpUpdate getModelFmpUpdate(long p_gameId)
      throws RpcFmpException;


  /**
   * 
   * @param p_message
   * @throws RpcFmpException
   */
  public void sendChatMessage(ChatMessage p_message)
      throws RpcFmpException;

  public void disconnect(Presence p_presence);

  /**
   * return new channel token
   * @param p_presence
   * @return
   */
  public String reconnect(Presence p_presence);
  
  /**
   * This service is only here to serialize a ChatMessage class with RPC.encodeResponseForSuccess
   */
  public ChatMessage getChatMessage(long p_gameId);

  /**
   * return non null PresenceRoom class associated with p_gameId.
   * This service is also here to serialize a PresenceRoom class with RPC.encodeResponseForSuccess
   */
  public PresenceRoom getRoom(long p_gameId);


}
