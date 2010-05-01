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

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.ConnectedUser;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */

public class WgtContextPlayers extends Composite implements ClickHandler
{
  private Panel m_playerPanel = new VerticalPanel();
  private Button m_btnChat = new Button( "chat" );

  private DlgChatInput m_dlgChat = null;


  /**
   * 
   */
  public WgtContextPlayers(DlgChatInput p_dlgChat)
  {
    super();
    assert p_dlgChat != null;
    m_dlgChat = p_dlgChat;
    m_btnChat.addClickHandler( this );
    initWidget( m_playerPanel );
  }

  public void redraw()
  {
    assert ModelFmpMain.model() != null;
    EbGame game = ModelFmpMain.model().getGame();

    // set player informations
    // -----------------------
    m_playerPanel.clear();
    m_playerPanel.add( new Label( MAppBoard.s_messages.xPlayers( ModelFmpMain.model().getGame()
        .getCurrentNumberOfRegiteredPlayer() ) ) );

    // get player order
    List<EbRegistration> sortedRegistration = new ArrayList<EbRegistration>();
    if( !ModelFmpMain.model().getGame().isAsynchron() )
    {
      for( int index = 0; index < ModelFmpMain.model().getGame().getSetRegistration().size(); index++ )
      {
        sortedRegistration
            .add( ModelFmpMain.model().getGame().getRegistrationByOrderIndex( index ) );
      }
    }
    else
    {
      sortedRegistration.addAll( ModelFmpMain.model().getGame().getSetRegistration() );
    }

    for( EbRegistration player : sortedRegistration )
    {
      if( player.haveAccount() )
      {
        addPlayer( player, ModelFmpMain.model().isUserConnected( player.getAccountId() ) );
      }
      else
      {
        addPlayer( player, false );
      }
    }

    // other connected User
    m_playerPanel.add( new Label( "Visiteur(s) :" ) );
    for( ConnectedUser user : ModelFmpMain.model().getConnectedUsers() )
    {
      if( !contain( sortedRegistration, user.getPseudo() ) )
      {
        addVisitor( user.getPseudo() );
      }
    }
    if( game.getGameType() == GameType.MultiPlayer )
    {
      m_playerPanel.add( m_btnChat );
    }
  }

  private boolean contain(List<EbRegistration> p_players, String p_pseudo)
  {
    for( EbRegistration player : p_players )
    {
      if( player.haveAccount()
          && ModelFmpMain.model().getAccount( player.getAccountId() ).getPseudo().equalsIgnoreCase(
              p_pseudo ) )
      {
        return true;
      }
    }
    return false;
  }

  private void addPlayer(EbRegistration p_player, boolean p_isConnected)
  {
    String htmlPlayers = "";

    // display all colors
    EnuColor color = p_player.getEnuColor();
    int colorIndex = 0;
    for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
    {
      if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
      {
        htmlPlayers += BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
            .getHTML();
      }
    }
    if( color.getValue() == EnuColor.None )
    {
      htmlPlayers += " <IMG SRC='images/board/icon.gif' WIDTH=16 HEIGHT=16 BORDER=0 TITLE='"
          + Messages.getColorString( color.getValue() ) + "'> ";
    }
    htmlPlayers += " ";
    // display name
    if( p_isConnected )
    {
      htmlPlayers += "<b>";
    }
    if( p_player.haveAccount() )
    {
      htmlPlayers += ModelFmpMain.model().getAccount( p_player.getAccountId() ).getPseudo();
    }
    else
    {
      htmlPlayers += "free";
    }
    if( p_isConnected )
    {
      htmlPlayers += "</b>";
    }
    // display current turn
    if( (!p_player.getGame().isAsynchron())
        && (p_player.getGame().getCurrentPlayerRegistration() == p_player) )
    {
      htmlPlayers += Icons.s_instance.action16().getHTML();
    }
    m_playerPanel.add( new HTML( htmlPlayers ) );
  }

  private void addVisitor(String p_pseudo)
  {
    HTML html = new HTML( "<b>" + p_pseudo + "</b>" );
    html.setWidth( "100%" );
    m_playerPanel.add( html );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnChat )
    {
      m_dlgChat.center();
      m_dlgChat.show();
    }

  }



}
