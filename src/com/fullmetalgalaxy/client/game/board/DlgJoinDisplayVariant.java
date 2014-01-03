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
package com.fullmetalgalaxy.client.game.board;

import java.util.Map;
import java.util.Map.Entry;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog warn player for little rules variation
 */

public class DlgJoinDisplayVariant extends DialogBox
{
  // UI
  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Panel m_panel = new VerticalPanel();

  private static DlgJoinDisplayVariant s_dlg = null;

  public static DlgJoinDisplayVariant instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgJoinDisplayVariant();
    }
    return s_dlg;
  }

  /**
   * 
   */
  public DlgJoinDisplayVariant()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( MAppBoard.s_messages.variantTitle() );

    // display common construction reserve
    m_panel.add( new HTML( "<b>" + MAppBoard.s_messages.commonConstructReserve() + "</b>" ) );
    m_panel.add( createTokenList( GameEngine.model().getGame().getConstructReserve() ) );
    
    // display initial hold
    m_panel.add( new HTML( "<b>" + MAppBoard.s_messages.initialHold() + "</b>" ) );
    m_panel.add( createTokenList( GameEngine.model().getGame().getInitialHolds() ) );

    // add buttons
    HorizontalPanel hpanel = new HorizontalPanel();
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
        hide();
        DlgJoinChooseColor.instance().show();
        DlgJoinChooseColor.instance().center();
      }
    } );
    hpanel.add( m_btnOk );
    m_panel.add( hpanel );

    setWidget( m_panel );
  }


  private Widget createTokenList(Map<TokenType, Integer> p_tokenList)
  {
    FlowPanel panel = new FlowPanel();

    for( Entry<TokenType, Integer> entry : p_tokenList.entrySet() )
    {
      if( entry.getValue() > 0 )
      {
        panel.add( new Label( " " + Messages.getTokenString( 0, entry.getKey() ) + ": "
            + entry.getValue() ) );
      }
    }

    return panel;
  }

}
