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
/**
 * 
 */
package com.fullmetalgalaxy.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.Presence.ClientType;
import com.fullmetalgalaxy.server.ChannelManager;
import com.google.appengine.api.xmpp.Subscription;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

/**
 * @author vlegendr
 * handler for /_ah/xmpp/subscription/unsubscribe/ 
 * signal that the user is unsubscribing from the application's presence.
 */
public class XMPPUnsuscribeServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
  {
    XMPPService xmppService = XMPPServiceFactory.getXMPPService();
    Subscription sub = xmppService.parseSubscription(req);

    // Split the XMPP address (e.g., user@gmail.com)
    // from the resource (e.g., gmail.CD6EBC4A)
    String fromJID = sub.getFromJid().getId().split("/")[0];

    
    // TODO well pseudo is different from JID !
    // we should request datastore
    Presence fmgPresence = new Presence( fromJID, 0, 0 );
    fmgPresence.setClientType( ClientType.XMPP );
    ChannelManager.disconnect( fmgPresence );
  }
}
