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
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ClientUtil;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerAtmosphere extends WgtBoardLayerBase implements EventPreview
{

  /**
   * 
   */
  public WgtBoardLayerAtmosphere()
  {
    setStyleName( "fmp-atmosphere" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  public boolean onEventPreview(Event p_event)
  {
    // TODO Auto-generated method stub
    return false;
  }


  private static int s_firstGridRuleIndex = createGridRules();

  public static int createGridRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-atmosphere",
        "{background: url(images/board/atmosphere/clear.png);}" ) - 1;
    return oldLength;
  }


}
