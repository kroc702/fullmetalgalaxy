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
package com.fullmetalgalaxy.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.server.ChannelManager;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

/**
 * @author vlegendr
 * handler for /_ah/xmpp/presence/unavailable/ 
 * signal that the user is unavailable.
 */
public class XMPPUnavailableServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
  {
    XMPPService xmppService = XMPPServiceFactory.getXMPPService();
    com.google.appengine.api.xmpp.Presence xmppPresence = xmppService.parsePresence(req);

    String pseudo = FmgDataStore.getPseudoFromJid( xmppPresence.getFromJid().getId() );
    
    // TODO well pseudo is different from JID !
    // we should request datastore
    Presence fmgPresence = new Presence( pseudo, 0, 0 );
    fmgPresence.setJabberId( xmppPresence.getFromJid().getId() );
    ChannelManager.disconnect( fmgPresence );
  }
}
