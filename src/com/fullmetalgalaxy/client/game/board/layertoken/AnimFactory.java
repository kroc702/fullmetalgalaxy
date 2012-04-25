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

package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTransfer;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtUnLoad;

/**
 * @author Vincent
 *
 */
public class AnimFactory
{
  public static AnimEvent createAnimEvent(WgtBoardLayerToken p_layerToken, AnEvent p_event)
  {
    if( p_event instanceof EbEvtMove )
    {
      return new AnimMove( p_layerToken, (EbEvtMove)p_event );
    }
    else if( p_event instanceof EbEvtUnLoad )
    {
      return new AnimUnload( p_layerToken, (EbEvtUnLoad)p_event );
    }
    else if( p_event instanceof EbEvtLoad )
    {
      return new AnimLoad( p_layerToken, (EbEvtLoad)p_event );
    }
    else if( p_event instanceof EbEvtTransfer )
    {
      return new AnimTransfer( p_layerToken, (EbEvtTransfer)p_event );
    }
    else if( p_event instanceof EbEvtFire )
    {
      return new AnimFire( p_layerToken, (EbEvtFire)p_event );
    }
    else if( p_event instanceof EbEvtControl )
    {
      return new AnimControl( p_layerToken, (EbEvtControl)p_event );
    }
    return null;
  }
}
