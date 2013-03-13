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

package com.fullmetalgalaxy.client.game.tabmenu;

import com.fullmetalgalaxy.client.widget.EventPresenter;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Vincent
 *
 */
public class TreeItemEvent extends TreeItem
{
  private AnEvent m_event = null;

  /**
   * 
   */
  public TreeItemEvent(AnEvent p_event)
  {
    super( EventPresenter.getIcon( p_event ) + " " + p_event.toString() );
    m_event = p_event;
  }

  public AnEvent getEvent()
  {
    return m_event;
  }
}
