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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.status;


import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Vincent Legendre
 *
 */
public class MAppStatusBar extends GuiEntryPoint implements ModelUpdateEvent.Handler
{
  public static final String HISTORY_ID = "status";

  protected Panel m_panel = new HorizontalPanel();

  // protected WgtPlayerInfo m_playerInfo = new WgtPlayerInfo();
  protected WgtGameStatus m_gameInfo = new WgtGameStatus();

  /**
   * 
   */
  public MAppStatusBar()
  {
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
    // m_panel.setWidth( "100%" );
    // m_panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    // m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

    redraw();
    m_panel.setStyleName( "fmp-status" );
    initWidget( m_panel );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  private void redraw()
  {
    m_panel.clear();
    m_panel.add( m_gameInfo );
    for( EbTeam team : GameEngine.model().getGame().getTeamByPlayOrder() )
      for( EbRegistration registration : team
          .getPlayers( GameEngine.model().getGame().getPreview() ) )
      {
        if( registration != null && registration.getColor() != EnuColor.None )
        {
          m_panel.add( new WgtPlayerInfo( registration ) );
        }
      }
  }

  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    // redraw everything after any model update
    //
    redraw();
  }


}
