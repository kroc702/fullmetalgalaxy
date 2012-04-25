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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog ask player to choose his color.
 */

public class DlgJoinChooseColor extends DialogBox implements ClickHandler
{
  // UI
  private Map<Image, Integer> m_icons = new HashMap<Image, Integer>();
  private Button m_btnCancel = new Button( "Cancel" );
  private Panel m_panel = new FlowPanel();

  private static DlgJoinChooseColor s_dlg = null;

  public static DlgJoinChooseColor instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgJoinChooseColor();
    }
    return s_dlg;
  }

  /**
   * 
   */
  public DlgJoinChooseColor()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "Choisissez votre couleur" );

    m_btnCancel.addClickHandler( this );



    setWidget( m_panel );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnCancel )
    {
      this.hide();
      return;
    }

    int color = m_icons.get( p_event.getSource() );

    EbGameJoin action = new EbGameJoin();
    action.setGame( GameEngine.model().getGame() );
    action.setAccountId( AppMain.instance().getMyAccount().getId() );
    action.setAccount( AppMain.instance().getMyAccount() );
    action.setColor( color );
    GameEngine.model().runSingleAction( action );

    this.hide();
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    m_panel.clear();

    // configure color selector
    Set<EnuColor> freeColors = null;
    if( GameEngine.model().getGame().getSetRegistration().size() >= GameEngine.model().getGame()
        .getMaxNumberOfPlayer() )
    {
      freeColors = GameEngine.model().getGame().getFreeRegistrationColors();
    }
    else
    {
      freeColors = GameEngine.model().getGame().getFreePlayersColors();
    }
    for( EnuColor color : freeColors )
    {
      Image image = new Image();
      BoardIcons.icon64( color.getValue() ).applyTo( image );
      image.setTitle( Messages.getColorString( 0, color.getValue() ) );
      image.addStyleName( "fmp-button" );
      image.addClickHandler( this );
      m_icons.put( image, color.getValue() );
      m_panel.add( image );
    }

    m_panel.add( m_btnCancel );


    super.show();
  }
}
