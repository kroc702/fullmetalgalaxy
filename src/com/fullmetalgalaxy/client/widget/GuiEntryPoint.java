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
package com.fullmetalgalaxy.client.widget;



import java.util.logging.Level;

import com.fullmetalgalaxy.client.AppRoot;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * this class is an entrypoint to be used as one component of main application
 * and a composite widget that can be inserted inside html page.
 * 
 * in fact we can extend this class instead standard composite to have an EntryPoint
 */
public abstract class GuiEntryPoint extends Composite implements EntryPoint
{

  /**
   * 
   */
  public GuiEntryPoint()
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
   */
  @Override
  public void onModuleLoad()
  {
    RootPanel panel = RootPanel.get( getHistoryId() );
    if( panel != null && getTopWidget() != null && AppRoot.instance().getHistoryState().containsKey( getHistoryId() ))
    {
      panel.setVisible( true );
      if( panel.getWidgetCount() == 0 )
      {
        panel.add( getTopWidget() );
      }
      //show( AppRoot.instance().getHistoryState() );
    }
    else
    {
      AppRoot.logger.log( Level.WARNING, "couldn't display mini app " + getHistoryId() );
    }
  }
  
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  public Widget getTopWidget()
  {
    return this;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  public void hide()
  {
    super.setVisible( false );
  }



  public abstract String getHistoryId();

}
