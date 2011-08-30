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

package com.fullmetalgalaxy.client.creation;

import com.fullmetalgalaxy.client.widget.WgtGameHeaderInfo;
import com.fullmetalgalaxy.client.widget.WgtGameTime;
import com.fullmetalgalaxy.client.widget.WgtMapSize;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class WgtGeneralInfo extends Composite
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();
  private WgtGameHeaderInfo m_headerInfo = new WgtGameHeaderInfo();
  private WgtMapSize m_mapSize = new WgtMapSize();
  private WgtGameTime m_time = new WgtGameTime();
  
  
  public WgtGeneralInfo()
  {
    super();
    m_panel.add( m_headerInfo );
    m_panel.add( m_mapSize );
    m_panel.add( m_time );
    
    initWidget( m_panel );
  }
}
