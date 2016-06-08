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

import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog inform player that other players are real
 * and ask password if game is private 
 */

public class DlgAIJoinGame extends DialogBox implements ClickHandler
{
  // UI
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Panel m_panel = new VerticalPanel();
  private ListBox listAI = new ListBox();
  IntegerBox intAPBonus = new IntegerBox();


  private static DlgAIJoinGame s_dlg = null;

  public static DlgAIJoinGame instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgAIJoinGame();
    }
    return s_dlg;
  }


  /**
   * 
   */
  public DlgAIJoinGame()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( MAppBoard.s_messages.joinTitle() );

    // TODO ask server to get this list
    listAI.addItem( "stai", "5180826044071936" );
    listAI.addItem( "killerai", "5087341249036288" );
    listAI.addItem( "niceai", "5148254354276352" );
    // localhost account
    // listAI.addItem( "test", "5629499534213120" );
    listAI.setItemSelected( 0, true );
    listAI.setVisibleItemCount( 3 );

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
      EbPublicAccount account = new EbPublicAccount();
      account.setId( Long.parseLong( listAI.getValue( listAI.getSelectedIndex() ) ) );
      account.setPseudo( listAI.getItemText( listAI.getSelectedIndex() ) );
      account.setAI( true );
      EbGameJoin joinEvent = new EbGameJoin();
      joinEvent.setAccountId( account.getId() );
      joinEvent.setAccount( account );
      joinEvent.setActionPointBonus( intAPBonus.getValue() );

      this.hide();

      DlgJoinChooseColor.instance().setJoinEvent( joinEvent );
      DlgJoinChooseColor.instance().show();
      DlgJoinChooseColor.instance().center();
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

    m_panel.add( new HTML( "Add an automatic player<hr/>Action point bonus:" ) );

    intAPBonus.setValue( 0 );
    m_panel.add( intAPBonus );

    m_panel.add( new HTML( "Select avaliable player" ) );

    m_panel.add( listAI );
    


    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( m_btnCancel );
    hPanel.add( m_btnOk );
    m_panel.add( hPanel );

    super.show();
  }

}
