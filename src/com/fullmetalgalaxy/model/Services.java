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
package com.fullmetalgalaxy.model;


import java.util.ArrayList;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author Kroc
 * 
 */
public interface Services extends RemoteService
{

  public static final String SERVICE_URI = "Services";

  public static class Util
  {

    public static ServicesAsync getInstance()
    {
      ServicesAsync instance = (ServicesAsync)GWT.create( Services.class );
      ServiceDefTarget target = (ServiceDefTarget)instance;
      target.setServiceEntryPoint( GWT.getModuleBaseURL() + SERVICE_URI );
      // AppMain.instance().startLoading();
      return instance;
    }
  }


  /**
   * create (if id=0) or save a game.
   * @param game description of the game.
   * @return id/lastUpdate of the created/saved game
   */
  public EbBase saveGame(EbGame game) throws RpcFmpException;

  /**
   * same as above, but let user set a message about his modifications
   * @param game
   * @param p_modifDesc
   * @return id/lastUpdate of the created/saved game
   * @throws RpcFmpException
   */
  public EbBase saveGame(EbGame game, String p_modifDesc) throws RpcFmpException;


  /**
   * This method shouldn't be used anymore as init is now injected in jsp.
   * 
   * Get all informations concerning the specific game.
   * @param p_gameId Id of the requested game
   * @return
   */
  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException;



  /**
   * TODO we should merge runAction and runEvent and take in parameters a ModelFmpUpdate
   * @param p_action
   * @throws RpcFmpException
   */
  public void runEvent(AnEvent p_action) throws RpcFmpException;

  public void runAction(ArrayList<AnEventPlay> p_actionList)
      throws RpcFmpException;


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
}
