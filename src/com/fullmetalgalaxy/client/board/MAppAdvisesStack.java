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

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * display in game message, error and chat messages from other players
 */

public class MAppAdvisesStack extends MApp implements ClickHandler
{
  public static final String HISTORY_ID = "advises";

  private Map<Widget, EbEvtMessage> s_advisesMap = new HashMap<Widget, EbEvtMessage>();
  private HorizontalPanel m_panel = new HorizontalPanel();

  public static MAppAdvisesStack s_instance = null;

  /**
   * 
   */
  public MAppAdvisesStack()
  {
    super();
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    initWidget( m_panel );
    s_instance = this;
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  @Override
  public void show(HistoryState p_state)
  {
    super.show( p_state );
    m_panel.clear();
    s_advisesMap.clear();
    for( AnEvent event : ModelFmpMain.model().getGame().getLogs() )
    {
      if( event.getType() == GameLogType.EvtMessage )
      {
        Label label = new Label( "?" );
        label.setTitle( ((EbEvtMessage)event).getTitle() );
        label.setStyleName( "fmp-btn-advises" );
        label.addClickHandler( this );
        m_panel.insert( label, 0 );
        s_advisesMap.put( label, (EbEvtMessage)event );
      }
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(ClickEvent p_event)
  {
    EbEvtMessage message = s_advisesMap.get( p_event.getSource() );
    if( message != null )
    {
      DlgMessageEvent dlgMsg = new DlgMessageEvent( message );
      dlgMsg.center();
      dlgMsg.show();
    }
  }


}
