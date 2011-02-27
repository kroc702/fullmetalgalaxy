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
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtGameLogs extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();

  /**
   * 
   */
  public WgtGameLogs()
  {
    ScrollPanel panel = new ScrollPanel();
    panel.add( m_panel );
    initWidget( panel );
  }

  public void redraw()
  {
    m_panel.clear();

    for( AnEvent event : ModelFmpMain.model().getGame().getLogs() )
    {
      // display all non admin events
      if( !(event.getType().isEventAdmin()) )
      {
        Label label = new Label( event.toString() );
        m_panel.add( label );
      }
    }
  }

}
