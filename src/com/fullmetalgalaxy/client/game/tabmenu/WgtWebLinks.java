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

package com.fullmetalgalaxy.client.game.tabmenu;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author vlegendr
 *
 */
public class WgtWebLinks extends HTML
{
  public WgtWebLinks()
  {
    super();
    setHTML( "<P ALIGN=CENTER><A HREF='/'>Accueil</A></P>"
        + "<P ALIGN=CENTER><A HREF='/presentation.jsp'>Pr&eacute;sentation</A></P>"
        + "<P ALIGN=CENTER><A HREF='/gamelist.jsp'>Parties en cours</A></P>"
        + "<P ALIGN=CENTER><A HREF='http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy'>Forum</A></P>"
        + "<P ALIGN=CENTER><A HREF='/chat.jsp'>Chat</A></P>"
        + "<P ALIGN=CENTER><A HREF='/help/'>Aides de jeu</A></P>"
        + "<P ALIGN=CENTER><A HREF='/halloffames.jsp'>Joueurs</A></P>"
        + "<P ALIGN=CENTER><A HREF='/historique.jsp'>Background</A></P>"
        + "<P ALIGN=CENTER><A HREF='/liens.jsp'>Liens</A></P>" );
  }
}
