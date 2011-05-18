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


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * display the selected token informations
 */
public class WgtContextToken extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();

  /**
   * 
   */
  public WgtContextToken()
  {
    super();
    m_panel.setSize( "100%", "100%" );
    initWidget( m_panel );
  }

  private long m_actionLastUpdate = 0;

  public void redraw()
  {
    assert ModelFmpMain.model() != null;
    EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
    if( actionBuilder.getLastUpdate().getTime() != m_actionLastUpdate )
    {
      assert ModelFmpMain.model() != null;
      m_panel.clear();
      m_actionLastUpdate = actionBuilder.getLastUpdate().getTime();

      Image wgtToken = new Image();
      TokenImages.getTokenImage( actionBuilder.getSelectedToken(), EnuZoom.Medium ).applyTo(
          wgtToken );
      /*wgtToken.setUrl( FmpConstant.getTokenUrl( selectedToken, new EnuZoom( EnuZoom.Medium ) ) );
      wgtToken.setPixelSize( 70, 70 );*/
      wgtToken.setTitle( Messages.getTokenString( 0, actionBuilder.getSelectedToken() ) );

      // m_panel.add( new HTML( "<center>" ) );
      AbsolutePanel absPanel = new AbsolutePanel();
      m_panel.add( absPanel );
      absPanel.setSize( "100%", "100%" );
      absPanel.add( wgtToken, absPanel.getOffsetWidth() / 2 - wgtToken.getWidth() / 2, absPanel
          .getOffsetHeight()
          / 2 - wgtToken.getHeight() / 2 + 30 );
      absPanel.add( new HTML( "<b>" + Messages.getTokenString( 0, actionBuilder.getSelectedToken() )
          + "</b>" ), 0, 0 );
      if( actionBuilder.getSelectedToken().getColor() != EnuColor.None )
      {
        EbRegistration player = ModelFmpMain.model().getGame().getRegistrationByColor(
            actionBuilder.getSelectedToken().getColor() );
        if(player != null && player.haveAccount() )
        {
          absPanel.add( new HTML( player.getAccount().getPseudo() ), 0, 20 );
        }
      }
      
      // display land under token
      AnBoardPosition position = ModelFmpMain.model().getActionBuilder().getSelectedPosition();
      if(position != null)
      {
        String landStr = Messages
            .getLandString( 0, ModelFmpMain.model().getGame().getLand( position ) );
        if( ModelFmpMain.model().getGame().getToken( position, TokenType.Pontoon ) != null )
        {
          landStr += " & " + Messages.getTokenString( 0, TokenType.Pontoon );
        }
        absPanel.add( new HTML( landStr ), 0, 40 );
        
      }

      // display ore count for freither
      EbToken token = ModelFmpMain.model().getActionBuilder().getSelectedToken();
      if( token != null && token.getType() == TokenType.Freighter )
      {
        absPanel.add( new HTML( Messages.getTokenString( 0, TokenType.Ore ) +" : " 
            + token.getContainOre() ), 150, 0 );
      }
      else if( token != null && token.isDestroyer() )
      {
        absPanel.add( new HTML( MAppBoard.s_messages.bullet() + " : " + token.getBulletCount() ),
            150, 0 );
      }
      else if( token != null && token.getType() == TokenType.WeatherHen )
      {
        absPanel.add(
            new HTML( MAppBoard.s_messages.construct() + " : " + token.getBulletCount() ), 150, 0 );
      }
      // m_panel.add( new HTML( "</center>" ) );
    }
  }
}
