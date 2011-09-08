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

package com.fullmetalgalaxy.client.chat;

import java.util.HashSet;
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.event.ChannelMessageEventHandler;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class MAppPresences extends GuiEntryPoint implements ChannelMessageEventHandler
{
  public static final String HISTORY_ID = "Presences";

  private Panel m_panel = new VerticalPanel();
  
  
  public MAppPresences()
  {
    initWidget( m_panel );
  }
  
  

  
  @Override
  protected void onLoad()
  {
    super.onLoad();
    onChannelMessage( AppMain.instance().getPresenceRoom() );
    AppMain.instance().addChannelMessageEventHandler( PresenceRoom.class, this );
  }

  @Override
  protected void onUnload()
  {
    super.onUnload();
    AppMain.instance().removeChannelMessageEventHandler( PresenceRoom.class, this );
  }



  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }


  @Override
  public void onChannelMessage(Object p_message)
  {
    // redraw widget according to new presenceRoom
    PresenceRoom newPresenceRoom = (PresenceRoom)p_message;
    m_panel.clear();
    Set<String> pseudoList = new HashSet<String>();
    for( Presence presence : newPresenceRoom )
    {
      if( !pseudoList.contains( presence.getPseudo() ) )
      {
        pseudoList.add( presence.getPseudo() );
        Panel panel = new HorizontalPanel();
        Image image = new Image( presence.getAvatarUrl() );
        image.setPixelSize( 32, 32 );
        image.setAltText( presence.getPseudo() );
        image.setTitle( presence.getPseudo() );
        panel.add( image );
        panel.add( new Label(presence.getPseudo()) );
        m_panel.add( panel );
      }
    }
  }


}
