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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.WgtGameTime;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog inform player that other players are real
 * and ask password if game is private 
 */

public class DlgJoinGame extends DialogBox implements ClickHandler
{
  // UI
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Panel m_panel = new VerticalPanel();

  private TextBox m_txtPassword = new TextBox();

  private static DlgJoinGame s_dlg = null;

  public static DlgJoinGame instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgJoinGame();
    }
    return s_dlg;
  }


  /**
   * 
   */
  public DlgJoinGame()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( MAppBoard.s_messages.joinTitle() );

    m_btnCancel.addClickHandler( this );
    m_btnOk.addClickHandler( this );


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
    else if( p_event.getSource() == m_btnOk )
    {
      if( GameEngine.model().getGame().isPasswordProtected()
          && !m_txtPassword.getText().equals( GameEngine.model().getGame().getPassword() ) )
      {
        Window.alert( MAppBoard.s_messages.pleaseCheckGamePasword() );
        return;
      }
      this.hide();

      if( !FmpConstant.getDefaultInitialHolds().equals(
          GameEngine.model().getGame().getInitialHolds() )
          || !FmpConstant.getDefaultReserve( GameEngine.model().getGame().getMaxNumberOfPlayer() )
              .equals( GameEngine.model().getGame().getConstructReserve() ) )
      {
        // not default variant... display a little message to player
        DlgJoinDisplayVariant.instance().show();
        DlgJoinDisplayVariant.instance().center();
      }
      else
      {
        DlgJoinChooseColor.instance().show();
        DlgJoinChooseColor.instance().center();
      }
      return;
    }

  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    m_panel.clear();

    m_panel.add( new HTML( ClientUtil.formatUserMessage( GameEngine.model().getGame()
        .getDescription() ) ) );
    m_panel.add( new HTML( "<hr/>" ) );
    
    WgtGameTime wgtGameTime = new WgtGameTime();
    wgtGameTime.setReadOnly( true );
    m_panel.add( wgtGameTime );
    m_panel.add( new HTML( "<hr/>" ) );


    if( GameEngine.model().getGame().isPasswordProtected() )
    {
      HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.add( new Label( MAppBoard.s_messages.pasword() ) );
      hPanel.add( m_txtPassword );
      m_panel.add( hPanel );
    }
    else
    {
      m_panel.add( new HTML( MAppBoard.s_messages.joinWarning() ) );
    }

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( m_btnCancel );
    hPanel.add( m_btnOk );
    m_panel.add( hPanel );

    super.show();
  }

}
