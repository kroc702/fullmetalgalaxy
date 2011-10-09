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

package com.fullmetalgalaxy.server;

import java.util.logging.Logger;

import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.server.EbAccount.NotificationQty;

/**
 * @author Vincent
 * 
 * aim of this class is to send email or private message to player during their game.
 * 
 */
public class GameNotification
{
  public static Logger logger = Logger.getLogger( "GameNotification" );

  /**
   * eventually send an email to people that need it.
   * This method have to be called after p_update have been successfully ran.
   * @param action
   */
  public static void sendMail(Game p_game, ModelFmpUpdate p_update)
  {
    for( AnEvent action : p_update.getGameEvents() )
    {
      if( (action instanceof EbAdminTimePlay) || (action instanceof EbEvtPlayerTurn) )
      {
        if( p_game.getCurrentPlayerRegistration() == null )
        {
          // Parallel mode is starting
          // send email to all players
          FmgMessage msg = new FmgMessage( "paralleleGameStart", p_game );
          if( p_game.getCurrentTimeStep() > 1 )
          {
            msg = new FmgMessage( "paralleleGameUnpause", p_game );
          }
          send2AllPlayers( msg, p_game, NotificationQty.Min );
          return;
        }


        // new turn => email to current player
        send2Player( new FmgMessage( "newTurn", p_game ), p_game,
            p_game.getCurrentPlayerRegistration(), NotificationQty.Min );
        return;
      }
    }
  }


  private static void send2AllPlayers(FmgMessage p_msg, Game p_game, NotificationQty p_level)
  {
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      send2Player( p_msg, p_game, registration, p_level );
    }
  }

  private static void send2Player(FmgMessage p_msg, Game p_game, EbRegistration p_registration,
      NotificationQty p_level)
  {
    EbAccount account = null;
    if( p_registration != null && p_registration.getAccount() != null )
    {
      account = FmgDataStore.dao().get( EbAccount.class, p_registration.getAccount().getId() );
    }
    if( account == null )
    {
      logger.warning( "game " + p_game.getName() + ", notification " + p_msg.getName()
          + " couldn't be send because account wasn't found" );
      return;
    }
    if( account.getNotificationQty().ordinal() < p_level.ordinal() )
    {
      logger.fine( "game " + p_game.getName() + ", notification " + p_msg.getName() + "player "
          + account.getPseudo() + ": don't want this message" );
      return;
    }
    if( ChannelManager.getRoom( p_game.getId() ).isConnected( account.getPseudo() ) )
    {
      logger.fine( "game " + p_game.getName() + ", notification " + p_msg.getName() + "player "
          + account.getPseudo() + " is connected: we don't need to send an email" );
      return;
    }
    // finally send message
    p_msg.send( account );
  }


}
