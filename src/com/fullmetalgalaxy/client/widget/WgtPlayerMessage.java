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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Vincent
 *
 */
public class WgtPlayerMessage extends Composite
{
  private Panel m_panel = new SimplePanel();

  /**
   * 
   */
  public WgtPlayerMessage(Game p_game, EbEvtMessage p_message)
  {
    super();
    EbPublicAccount account = p_game.getAccount( p_message.getAccountId() );
    String avatarUrl = "/images/avatar/avatar-default.jpg";
    String pseudo = "???";
    if( account != null )
    {
      pseudo = account.getPseudo();
      avatarUrl = account.getAvatarUrl();
    }

    String html = "<div class='article'><span class='date'>";
    html += ClientUtil.formatDateTime( p_message.getLastUpdate() );
    html += "</span><div style='float:left;'><img src='";
    html += avatarUrl;
    html += "' height='40px'/></div>";
    html += "<div class='h4'>";
    html += pseudo;
    html += "</div>";
    html += ClientUtil.formatUserMessage( p_message.getMessage() );
    html += "</div>";

    m_panel.add( new HTML( html ) );
    initWidget( m_panel );
  }

}
