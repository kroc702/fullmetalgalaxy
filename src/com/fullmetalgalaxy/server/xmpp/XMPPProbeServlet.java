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
package com.fullmetalgalaxy.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.server.ChannelManager;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.PresenceShow;
import com.google.appengine.api.xmpp.PresenceType;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

/**
 * @author vlegendr
 * handler for /_ah/xmpp/presence/probe/ 
 * request a user's current presence.
 */
public class XMPPProbeServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
  {
    XMPPService xmppService = XMPPServiceFactory.getXMPPService();
    com.google.appengine.api.xmpp.Presence xmppPresence = xmppService.parsePresence(req);

    sendPresence(xmppPresence.getFromJid());
  }
  
  
  public static void sendPresence(JID p_jid)
  {
    PresenceRoom room = ChannelManager.getRoom( 0 );
    PresenceType pt = PresenceType.AVAILABLE;
    int count = room.countPseudo();
    if( count <= 1 )
    {
      pt = PresenceType.UNAVAILABLE;
    }
    XMPPService xmppService = XMPPServiceFactory.getXMPPService();
    xmppService.sendPresence( p_jid, pt, PresenceShow.NONE, "" + count + " joueurs" );
  }
}
