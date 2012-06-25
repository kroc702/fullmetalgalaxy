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

import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog ask player to choose his color.
 */

public class DlgJoinChooseColor extends DialogBox
{
  // UI
  private ListBox m_colorSelection = new ListBox();
  private Image m_preview = new Image("/images/board/icon.gif");

  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Panel m_panel = new VerticalPanel();

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

    // add color list widget
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
      m_colorSelection.addItem( Messages.getColorString( 0, color.getValue() ), ""+color.getValue() );
    }
    m_colorSelection.setSelectedIndex( -1 );
    m_colorSelection.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        int colorValue = Integer.parseInt( m_colorSelection.getValue( m_colorSelection.getSelectedIndex() ));
        EnuColor color = new EnuColor(colorValue);
        m_preview.setUrl( "/images/board/" + color.toString() + "/preview.jpg" );
        m_btnOk.setEnabled( true );
      }
    } );
    Panel hpanel = new HorizontalPanel();
    hpanel.add( m_colorSelection );
    hpanel.add( m_preview );
    m_panel.add( hpanel );

    // add buttons
    hpanel = new HorizontalPanel();
    // add cancel button
    m_btnCancel.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        hide();
      }
    } );
    hpanel.add( m_btnCancel );

    // add OK button
    m_btnOk.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        EnuColor color = EnuColor.getColorFromIndex( m_colorSelection.getSelectedIndex() );
        EbGameJoin action = new EbGameJoin();
        action.setGame( GameEngine.model().getGame() );
        action.setAccountId( AppMain.instance().getMyAccount().getId() );
        action.setAccount( AppMain.instance().getMyAccount() );
        action.setColor( color.getValue() );
        GameEngine.model().runSingleAction( action );
        hide();
      }
    } );
    m_btnOk.setEnabled( false );
    hpanel.add( m_btnOk );
    m_panel.add( hpanel );

    setWidget( m_panel );
  }

}
