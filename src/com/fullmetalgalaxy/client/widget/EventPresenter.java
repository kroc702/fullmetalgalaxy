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
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;

/**
 * this class isn't a real widget.
 */
public class EventPresenter
{
  public static String getDetailAsHtml(AnEvent p_event)
  {
    return getIcon( p_event ) + " " + p_event.getType() + "<br/>"
    + "<span class='date'>" + getDate( p_event ) + "</span> par " + getPseudo( p_event );
  }



  public static String getIcon(AnEvent p_event)
  {
    if(p_event instanceof EbEvtTide)
    {
      // we can't determine imediate next tide without replaying event
      // event.getNextTide() is in fact two tide after...
      return Icons.s_instance.tide_unknown().getHTML();
    }

    return "";
  }


  public static String getDate(AnEvent p_event)
  {
    return ClientUtil.formatDateTime( p_event.getLastUpdate() );
  }

  public static String getPseudo(AnEvent p_event)
  {
    if( p_event.isAuto() )
    {
      return "AUTO";
    }
    long accountId = 0;
    if(p_event instanceof AnEventUser)
    {
      accountId = ((AnEventUser)p_event).getAccountId();
    }
    if(p_event instanceof EbEvtPlayerTurn)
    {
      accountId = ((EbEvtPlayerTurn)p_event).getAccountId();
    }
    EbRegistration registration = GameEngine.model().getGame()
        .getRegistrationByIdAccount( accountId );
    if( registration != null && registration.getAccount() != null )
    {
      return registration.getAccount().getPseudo();
    }
    return "???";
  }

}
