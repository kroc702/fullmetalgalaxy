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
package com.fullmetalgalaxy.client.home;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MiniApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class MAppHomeMenu implements MiniApp
{
  public static final String HISTORY_ID = "homemenu";
  public static MessagesAppHome s_messages = (MessagesAppHome)GWT.create( MessagesAppHome.class );

  private Command m_cmdCreateGame = new Command()
  {
    public void execute()
    {
      if( ModelFmpMain.model().isLogged() )
      {
        AppMain.instance().gotoCreateGame();
      }
      else
      {
        Window.alert( s_messages.errorMustBeLogged() );
      }
    }
  };


  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  public void hide()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  public void show(HistoryState p_state)
  {
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getWidget()
   */
  public Widget getTopWidget()
  {
    return null;
  }

}
