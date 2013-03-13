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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.server.ChannelManager;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

/**
 * @author vlegendr
 *
 */
public class XMPPMessageServlet extends HttpServlet
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public XMPPMessageServlet()
  {
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
  {
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    Message message = xmpp.parseMessage( req );

    // Split the XMPP address (e.g., user@gmail.com)
    // from the resource (e.g., gmail.CD6EBC4A)
    String jid = message.getFromJid().getId();
    String pseudo = FmgDataStore.getPseudoFromJid( jid );
    String body = message.getBody();
    ChatMessage chatMessage = new ChatMessage( 0, pseudo, body );

    PresenceRoom room = ChannelManager.getRoom( 0 );
    Presence presence = room.getPresence( pseudo, 0 );
    if( presence == null )
    {
      // presence of the sender wasn't found in room...
      presence = new Presence( pseudo, 0, 0 );
      presence.setJabberId( jid );
      ChannelManager.connect( presence );
    }

    ChannelManager.broadcast( room, chatMessage );
  }

  
  public static boolean sendXmppMessage(String p_jabberId, ChatMessage p_chatMessage)
  {
    boolean messageSent = false;
    if( p_jabberId != null )
    {
      JID jid = new JID(p_jabberId);
      String msgBody = "[" + p_chatMessage.getFromPseudo() + "] " + p_chatMessage.getText();
      Message msg = new MessageBuilder()
                        .withRecipientJids(jid)
                        .withBody(msgBody)
                        .build();
              
      XMPPService xmpp = XMPPServiceFactory.getXMPPService();
      SendResponse status = xmpp.sendMessage(msg);
      messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
    }
    return messageSent;
  }

  public static boolean isPresent(String p_jabberId)
  {
    if( p_jabberId == null )
    {
      return false;
    }
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    return xmpp.getPresence( new JID( p_jabberId ) ).isAvailable();
  }

}
