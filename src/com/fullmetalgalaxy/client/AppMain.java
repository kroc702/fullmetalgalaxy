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
package com.fullmetalgalaxy.client;

import java.util.logging.Level;

import com.fullmetalgalaxy.client.creation.MAppGameCreation;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.game.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.game.board.MAppStatusBar;
import com.fullmetalgalaxy.client.game.context.MAppContext;
import com.fullmetalgalaxy.client.game.tabmenu.MAppTabMenu;
import com.fullmetalgalaxy.model.ChatService;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;

/**
 * @author Vincent Legendre
 *
 */
public class AppMain extends AppRoot 
{
 
  /**
   * 
   */
  public AppMain()
  {
    super();

  }


  private FmpCallback<ModelFmpInit> loadGameCallback = new FmpCallback<ModelFmpInit>()
  {
    @Override
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      ModelFmpInit model = (ModelFmpInit)p_result;
      if( model.getPresenceRoom() != null )
      {
        ModelFmpMain.model().m_connectedUsers = model.getPresenceRoom();
      }
      ModelFmpMain.model().load( model.getGame() );
      AppMain.instance().stopLoading();
    }
  };



  public void loadModelFmpBoard()
  {
    AppMain.instance().startLoading();
    
    String strModel = ClientUtil.getJSString( "fmp_model" );
    if( strModel != null )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( GameServices.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strModel );
        Object object = reader.readObject();
        if( object instanceof ModelFmpInit )
        {
          loadGameCallback.onSuccess( (ModelFmpInit)object );
        }
      } catch( SerializationException e )
      {
        AppRoot.logger.log( Level.WARNING, e.getMessage() );
      }
    }
    
    String strRoom = ClientUtil.getJSString( "fmp_room" );
    if( strRoom != null )
    {
      try
      {
        SerializationStreamFactory factory = GWT.create( ChatService.class );
        SerializationStreamReader reader;
        reader = factory.createStreamReader( strRoom );
        Object object = reader.readObject();
        if( object instanceof PresenceRoom )
        {
          ModelFmpMain.model().receivePresenceRoom( (PresenceRoom)object );
        }
      } catch( SerializationException e )
      {
        AppRoot.logger.log( Level.WARNING, e.getMessage() );
      }
    }


    if( AppMain.instance().isLoading() )
    {
      // well, model init wasn't found in jsp => ask it with standard RPC call
      String gameId = ClientUtil.getUrlParameter( "id" ); 
      if( gameId != null )
      {
        GameServices.Util.getInstance().getModelFmpInit( gameId, loadGameCallback );
      }
      else
      {
        // load an empty game
        ModelFmpMain.model().load( new Game() );
        AppMain.instance().stopLoading();
      }
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
   */
  @Override
  public void onModuleLoad()
  {
    super.onModuleLoad();
    
    // load model from jsp
    loadModelFmpBoard();
    
    // call all entry point involve in this application
    new MAppContext().onModuleLoad();
    new MAppGameCreation().onModuleLoad();
    new MAppStatusBar().onModuleLoad();
    new MAppMessagesStack().onModuleLoad();
    new MAppBoard().onModuleLoad();
    new MAppTabMenu().onModuleLoad();
    
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(ModelFmpMain.model()) );
  }


}
