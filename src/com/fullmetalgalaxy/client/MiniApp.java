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
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * classes which implements this interface may be added to any part of the root panel. 
 * it should only use the AppMain static interface.
 */
public interface MiniApp
{
  /**
   * the widget which can be added to the root panel.
   * @return
   */
  public Widget getTopWidget();

  /**
   * show all dialog widget, eventually add/modify the AppMain menu.
   * when this method is called, the 'topwidget' is already added to the root panel.
   * @param p_state contain all new applications status.
   */
  public void show(HistoryState p_state);

  /**
   * hide all dialog widgets, remove any menu it create.
   * This method is usually called before removing the top widget from root panel.
   */
  public void hide();

  public String getHistoryId();
}
