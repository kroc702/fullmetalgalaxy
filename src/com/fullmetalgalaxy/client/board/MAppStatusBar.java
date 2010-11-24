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


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.creation.MAppGameCreation;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.fonts.ImageFont;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class MAppStatusBar extends MApp
{
  public static final String HISTORY_ID = "status";

  protected HorizontalPanel m_panel = new HorizontalPanel();
  protected Image m_icon = Icons.s_instance.desert_planet_medium().createImage();
  protected HTML m_title = new HTML();
  private String m_strTitle = "";

  protected WgtPlayerInfo m_playerInfo = new WgtPlayerInfo();
  protected WgtTimeInfo m_timeInfo = new WgtTimeInfo();

  /**
   * 
   */
  public MAppStatusBar()
  {
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    m_panel.setWidth( "100%" );
    m_panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

    VerticalPanel vpanel = new VerticalPanel();
    vpanel.setSize( "100%", "40px" );
    vpanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    vpanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    vpanel.add( m_playerInfo );
    // vpanel.setCellWidth( m_playerInfo, "200px" );
    vpanel.add( m_timeInfo );

    m_panel.add( vpanel );
    m_panel.setCellWidth( vpanel, "280px" );
    m_panel.add( m_icon );
    m_panel.setCellWidth( m_icon, "40px" );
    m_panel.add( m_title );
    m_panel.setCellHorizontalAlignment( m_title, HasHorizontalAlignment.ALIGN_LEFT );
    initWidget( m_panel );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  private void setTitleStatus(String p_title)
  {
    if( m_strTitle.equals( p_title ) )
    {
      return;
    }
    m_strTitle = p_title;
    m_title.setHTML( ImageFont.getHTML( ImageFont.s_FontTitleBundle, m_strTitle ) );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MApp#show(com.fullmetalgalaxy.client.HistoryState)
   */
  @Override
  public void show(HistoryState p_state)
  {
    super.show( p_state );

    if( p_state.containsKey( MAppBoard.HISTORY_ID ) )
    {
      setTitleStatus( ModelFmpMain.model().getGame().getName() );
    }
    else if( p_state.containsKey( MAppGameCreation.HISTORY_ID ) )
    {
      // TODO i18n
      setTitleStatus( "Nouvelle exploitation" );
    }
    else
    {
      setTitleStatus( "browse" );
    }
  }


}
