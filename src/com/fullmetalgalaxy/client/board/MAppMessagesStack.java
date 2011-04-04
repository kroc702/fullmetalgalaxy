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

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * display in game message, error and chat messages from other players
 */
public class MAppMessagesStack extends MApp
{
  public static final String HISTORY_ID = "messages";

  private Map<Widget, PopupTimer> s_messagesPanels = new HashMap<Widget, PopupTimer>();
  private VerticalPanel m_panel = new VerticalPanel();

  public static MAppMessagesStack s_instance = new MAppMessagesStack();

  /**
   * 
   */
  public MAppMessagesStack()
  {
    super();

    initWidget( m_panel );
    s_instance = this;
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  class PopupTimer extends Timer
  {
    private Widget m_wdg = null;

    public PopupTimer(Widget wdg)
    {
      m_wdg = wdg;
    }

    @Override
    public void run()
    {
      if( m_wdg == null )
      {
        return;
      }
      m_wdg.removeFromParent();
      s_messagesPanels.remove( m_wdg );
    }
  }



  public void showWarning(String p_msg)
  {
    Panel panel = new HorizontalPanel();
    Image image = Icons.s_instance.cancel32().createImage();
    panel.add( image );
    panel.add( new Label( p_msg ) );
    showMessage( panel );
  }


  public void showMessage(String p_msg)
  {
    showMessage( new Label( p_msg ) );
  }


  public void showMessage(Widget p_wgt)
  {
    if( p_wgt == null )
    {
      return;
    }
    
    PopupTimer timer = s_messagesPanels.get( p_wgt );
    if( timer == null )
    {
      p_wgt.setWidth( "100%" );
      timer = new PopupTimer( p_wgt );
      s_messagesPanels.put( p_wgt, timer );
      m_panel.add( p_wgt );
    }
    else
    {
      timer.cancel();
    }
    timer.schedule( FmpConstant.clientMessagesLivePeriod * 1000 );
  }



}
