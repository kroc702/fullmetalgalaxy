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

package com.fullmetalgalaxy.server.xmpp;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.server.ChannelManager;

/**
 * @author vlegendr
 * return true if message should be only send back to original sender.
 */
public class ChatCommand
{
  public static boolean process(ChatMessage p_msg)
  {
    if( p_msg.isEmpty() )
    {
      return false;
    }
    
    // help
    // ====
    if( p_msg.getText().startsWith( "/?" ) )
    {
      p_msg.setText( "/?\t: This help message\n" +
      		"/user\t: Connected users list\n" +
      		"/juron\t: Haddock capitains language\n" +
      		"/pipo\t: A crazy tip\n" );
      return true;
    }
    // connected users list
    // ====================
    else if( p_msg.getText().startsWith("/user") )
    {
      PresenceRoom room = ChannelManager.getRoom( p_msg.getGameId() );
      String newMsg = "";
      for( Presence p : room )
      {
        newMsg += p.getPseudo()+" ["+p.getClientType().toString()+"]\n"; 
      }
      p_msg.setText( newMsg );
      return true;
    }
    // Haddock capitains language
    // ==========================
    else if( p_msg.getText().startsWith("/juron") )
    {
      p_msg.setText( Pipotron.pipo( "MilleSabords" ) );
    }
    // pipo tips
    // =========
    else if( p_msg.getText().startsWith("/pipo") )
    {
      p_msg.setText( Pipotron.pipo( "pipo" ) );
    }
    return false;
  }
}
