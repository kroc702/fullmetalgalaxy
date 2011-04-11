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
package com.fullmetalgalaxy.client.chat;

import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class WgtPresences extends VerticalPanel
{
  private Set<String> m_pseudoList = new HashSet<String>();
  
  /**
   * 
   */
  public WgtPresences()
  {
    super();
  }

  public void setPresenceRoom(PresenceRoom p_room)
  {
    clear();
    m_pseudoList.clear();
    for( Presence presence : p_room )
    {
      if( !m_pseudoList.contains( presence.getPseudo() ) )
      {
        m_pseudoList.add( presence.getPseudo() );
        add( new Label( presence.getPseudo() ) );
      }
    }
  }
}
