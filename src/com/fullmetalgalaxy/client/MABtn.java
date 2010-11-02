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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public abstract class MABtn implements MiniApp
{
  RootPanel panel = RootPanel.get( getHistoryId() );

  public MABtn()
  {

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getHistoryId()
   */
  @Override
  public abstract String getHistoryId();

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return panel;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  @Override
  public void hide()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show(com.fullmetalgalaxy.client.HistoryState)
   */
  @Override
  public void show(HistoryState p_state)
  {
  }

}
