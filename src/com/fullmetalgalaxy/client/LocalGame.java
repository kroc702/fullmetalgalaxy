/**
 * 
 */
package com.fullmetalgalaxy.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.fullmetalgalaxy.model.persist.triggers.actions.AnAction;
import com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition;
import com.fullmetalgalaxy.model.persist.triggers.conditions.EbCndPuzzleLoad;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Vincent Legendre
 * This class replace standards call to RPC service while playing a puzzle game.
 */
public class LocalGame
{
  /**
   * This method is called after a succesfull load of a local game.
   * @param callback
   */
  public static void loadGame(AsyncCallback<ModelFmpUpdate> callbackUpdates)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    EbGame game = ModelFmpMain.model().getGame();
    for( EbTrigger trigger : game.getTriggers() )
    {
      if( trigger.isEnable() )
      {
        for( AnCondition condition : trigger.getConditions() )
        {
          if( condition instanceof EbCndPuzzleLoad )
          {
            for( AnAction action : trigger.getActions() )
            {
              events.addAll( action.createEvents( game, new ArrayList<Object>() ) );
            }
          }
        }
      }
    }
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
  }

  public static void runEvent(AnEvent p_action, Date p_clientLastUpdate,
      AsyncCallback<ModelFmpUpdate> callbackUpdates)
  {
    EbGame game = ModelFmpMain.model().getGame();
    List<AnEvent> events = new ArrayList<AnEvent>();
    events.add( p_action );
    events.addAll( game.createTriggersEvents() );
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
    // check triggers
    events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    updates = new ModelFmpUpdate();
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
  }

  public static void runAction(ArrayList<AnEventPlay> p_actionList, Date p_clientLastUpdate,
      AsyncCallback<ModelFmpUpdate> callbackUpdates)
  {
    EbGame game = ModelFmpMain.model().getGame();
    ModelFmpUpdate updates = new ModelFmpUpdate();
    List<AnEvent> events = new ArrayList<AnEvent>();
    for( AnEvent eventPlay : p_actionList )
    {
      events.add( eventPlay );
    }
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
    // check triggers
    events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    updates = new ModelFmpUpdate();
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
  }


  /**
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public static void modelUpdate(long p_gameId, Date p_lastVersion,
      AsyncCallback<ModelFmpUpdate> callbackUpdates)
  {
    // check triggers
    EbGame game = ModelFmpMain.model().getGame();
    List<AnEvent> events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setGameEvents( events );
    updates.getFromUpdate().setTime( game.getLastServerUpdate().getTime() );
    updates.getLastUpdate().setTime( System.currentTimeMillis() );
    callbackUpdates.onSuccess( updates );
  }

}
