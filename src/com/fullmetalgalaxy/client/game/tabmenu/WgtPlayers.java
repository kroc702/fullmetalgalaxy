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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.game.tabmenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.chat.DlgChatInput;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminBan;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vlegendr
 *
 */
public class WgtPlayers extends Composite implements ClickHandler
{
  private Map<Widget, EbRegistration> m_banButtons = new HashMap<Widget, EbRegistration>();
  private Map<Widget, EbRegistration> m_skipTurnButtons = new HashMap<Widget, EbRegistration>();
  private Panel m_playerPanel = new FlowPanel();
  
  private Button m_btnChat = new Button( "chat" );
  
  public WgtPlayers()
  {
    super();
    
    m_btnChat.addClickHandler( this );
    initPlayerPanel();
    ScrollPanel m_scrollPanel = new ScrollPanel();
    m_scrollPanel.setStyleName( "fmp-players-panel" );
    m_scrollPanel.add( m_playerPanel );
    initWidget( m_scrollPanel );
  }
  
  private void initPlayerPanel()
  {
    m_banButtons.clear();
    m_skipTurnButtons.clear();
    m_playerPanel.clear();
    int playerCount = GameEngine.model().getGame().getSetRegistration().size();
    String strPlayerCount = ""+playerCount;
    if( playerCount != GameEngine.model().getGame().getMaxNumberOfPlayer() )
    {
      strPlayerCount += "/"+GameEngine.model().getGame().getMaxNumberOfPlayer();
    }
    String strLabel = MAppBoard.s_messages.xPlayers( strPlayerCount );
    if( GameEngine.model().getGame().getMaxTeamAllowed() > 0 )
    {
      String strTeamCount = ""+GameEngine.model().getGame().getTeams().size();
      if( GameEngine.model().getGame().getTeams().size() != GameEngine.model().getGame().getMaxTeamAllowed() )
      {
        strTeamCount += "/"+GameEngine.model().getGame().getMaxTeamAllowed();
      }
      strLabel += " - " + MAppBoard.s_messages.xTeams( strTeamCount );
    }
    m_playerPanel.add( new Label( strLabel ) );

    // message to all link
    long pseudoList[] = new long[playerCount];
    int i = 0;
    for( EbRegistration registration : GameEngine.model().getGame().getSetRegistration() )
    {
      if( registration.getAccount() != null )
      {
        pseudoList[i] = registration.getAccount().getId();
      }
      else
      {
        pseudoList[i] = 0;
      }
      i++;
    }
    m_playerPanel.add( new HTML( "<a href='"
        + EbPublicAccount.getEMailUrl( GameEngine.model().getGame().getName(), pseudoList )
        + "' >Envoyer un message à tous</a>" ) );

    // get player order
    List<EbTeam> sortedTeam = GameEngine.model().getGame().getTeamByPlayOrder();


    Grid m_playerGrid = new Grid( GameEngine.model().getGame().getSetRegistration().size() + 1, 9 );
    m_playerGrid.setStyleName( "fmp-array" );

    m_playerGrid.setText( 0, 0, "" ); // team avatar
    m_playerGrid.setText( 0, 1, "" ); // avatar
    m_playerGrid.setText( 0, 2, "login" );
    m_playerGrid.setText( 0, 3, "couleur(s)" );
    m_playerGrid.setText( 0, 4, "pt d'action" );
    m_playerGrid.setHTML( 0, 5, "pt de victoire<br/>(estimation)" );
    m_playerGrid.setText( 0, 6, "" ); // must play before
    m_playerGrid.setText( 0, 7, "" ); // ban
    m_playerGrid.setText( 0, 8, "" ); // skip turn
    m_playerGrid.getRowFormatter().addStyleName( 0, "fmp-home-gameline-caption" );

    int index = 0;
    for( EbTeam team : sortedTeam )
      for( EbRegistration registration : team
          .getPlayers( GameEngine.model().getGame().getPreview() ) )
      {
        index++;

        String html = "";

        // display team avatar
        if( team.getCompany() != Company.Freelancer )
        {
          m_playerGrid
              .setHTML( index, 0, "<IMG SRC='/images/avatar/" + team.getCompany()
                  + ".jpg' WIDTH=60 HEIGHT=60 BORDER=0 title='" + team.getCompany().getFullName()
                  + "'/>" );
        }
        
        if( registration.haveAccount() )
        {
          // display avatar
          m_playerGrid.setHTML( index, 1, "<IMG SRC='" + registration.getAccount().getAvatarUrl()
              + "' WIDTH=60 HEIGHT=60 BORDER=0 />" );

          // display login
          // if player is connected, display in bold font
          if( AppMain.instance().isUserConnected( registration.getAccount().getPseudo() ) )
          {
            html += "<b>";
          }
          String login = registration.getAccount().getPseudo();
          html += "<a href='" + registration.getAccount().getProfileUrl() + "' target='_blank'>"
              + login + "</a>";
          if( AppMain.instance().isUserConnected( registration.getAccount().getPseudo() ) )
          {
            html += "</b>";
          }
        }
        else
        {
          // display avatar
          m_playerGrid.setHTML( index, 1,
              "<IMG SRC='/images/avatar/avatar-default.jpg' WIDTH=60 HEIGHT=60 BORDER=0 />" );
          // display login
          html = "???";
        }

        // display email messages
        if( registration.getAccount() != null )
        {
          html += " <a target='_blank' href='"
              + registration.getAccount().getEMailUrl( GameEngine.model().getGame().getName() )
              + "'><img src='/images/css/icon_pm.gif' border=0 alt='PM' /></a> ";
        }

        if( GameEngine.model().getGame().getCurrentPlayerIds().contains( registration.getId() ) )
        {
          html += AbstractImagePrototype.create( Icons.s_instance.action16() ).getHTML();
        }
        if( registration.haveAccount() )
        {
          html += "<br/><img src='" + registration.getAccount().getGradUrl()
              + "' border=0 alt='GRAD'/>";
        }
        if( registration.isReplacement() )
        {
          EbPublicAccount resigned = registration.getOriginalAccount( GameEngine.model().getGame() );
          html += "<br/><small>remplace&nbsp;<a href='" + resigned.getProfileUrl()
              + "' target='_blank'>" + resigned.getPseudo() + "</a></small>";
        }
        m_playerGrid.setHTML( index, 2, html );

        // display all colors
        EnuColor color = registration.getEnuColor();
        int colorIndex = 0;
        String htmlColors = "";
        for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
        {
          if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
          {
            htmlColors += BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
                .getHTML();
          }
        }
        if( color.getValue() == EnuColor.None )
        {
          htmlColors += " <IMG SRC='images/board/icon.gif' WIDTH=16 HEIGHT=16 BORDER=0 TITLE='"
              + Messages.getColorString( 0, color.getValue() ) + "'> ";
        }
        m_playerGrid.setHTML( index, 3, htmlColors );

        // display action points
        m_playerGrid
            .setText(
                index,
                4,
                ""
                    + registration.getPtAction()
                    + "/"
                    + (GameEngine.model().getGame().getEbConfigGameTime().getActionPtMaxReserve() + ((registration
                        .getEnuColor().getNbColor() - 1) * GameEngine.model().getGame()
                        .getEbConfigGameTime().getActionPtMaxPerExtraShip())) );

        // display Wining points
        m_playerGrid.setText( index, 5,
            "" + team.estimateWinningScore( GameEngine.model().getGame() ) );

        // display admin button
        if( (GameEngine.model().getGame().getAccountCreator() != null && AppMain.instance()
            .getMyAccount().getId() == GameEngine.model().getGame().getAccountCreator().getId())
            || AppMain.instance().iAmAdmin() )
        {
          if( registration.haveAccount() )
          {
            // display ban button
            Image banImage = new Image();
            banImage.setUrl( "/images/icons/ban.gif" );
            banImage.setAltText( "BAN" );
            banImage.setTitle( "Banir un joueur de cette partie" );
            banImage.addClickHandler( this );
            m_playerGrid.setWidget( index, 7, banImage );
            m_banButtons.put( banImage, registration );
          }

          // display endTurn button
          if( (GameEngine.model().getGame().getCurrentPlayerIds().contains( registration.getId() )) )
          {
            PushButton btnSkipTurn = new PushButton( new Image( Icons.s_instance.endTurn32() ) );
            btnSkipTurn.setTitle( MAppBoard.s_messages.endTurn() );
            btnSkipTurn.setStyleName( "fmp-PushButton32" );
            btnSkipTurn.addClickHandler( this );
            m_playerGrid.setWidget( index, 8, btnSkipTurn );
            m_skipTurnButtons.put( btnSkipTurn, registration );
          }

        }
        
      }

    m_playerPanel.add( m_playerGrid );
    
    
    // come from old WgtContextPlayers
    //
    Game game = GameEngine.model().getGame();
    if( (game.getGameType() == GameType.MultiPlayer || game.getGameType() == GameType.Initiation) )
    {
      VerticalPanel vpanel = new VerticalPanel();

      // other connected User
      vpanel.add( new Label( "Visiteur(s) :" ) );
      for( Presence user : AppMain.instance().getPresenceRoom() )
      {
        if( !contain( GameEngine.model().getGame().getSetRegistration(), user.getPseudo() ) )
        {
          HTML html = new HTML( "<b>" + user.getPseudo() + "</b>" );
          html.setWidth( "100%" );
          vpanel.add( html );
        }
      }
      if( (game.getGameType() == GameType.MultiPlayer || game.getGameType() == GameType.Initiation) )
      {
        vpanel.add( m_btnChat );
        vpanel.add( new HTML("<a href='/chat.jsp?id="+game.getId()+"' target='_blank'><img src='/images/icon_new_window.gif'/></a>") );
      }
      m_playerPanel.add( vpanel );
    }
  }

  private boolean contain(Set<EbRegistration> p_players, String p_pseudo)
  {
    for( EbRegistration player : p_players )
    {
      if( player != null && player.haveAccount()
          && player.getAccount().getPseudo().equalsIgnoreCase( p_pseudo ) )
      {
        return true;
      }
    }
    return false;
  }




  

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnChat )
    {
      DlgChatInput.showDialog();
    }
    else if( m_skipTurnButtons.get( p_event.getSource() ) != null )
    {
      EbRegistration registration = m_skipTurnButtons.get( p_event.getSource() );
      String playerName = Messages.getColorString( 0,
          registration.getTeam( GameEngine.model().getGame() ).getFireColor() );
      if( registration.getAccount() != null )
      {
         playerName = registration.getAccount().getPseudo();
      }
      if( Window.confirm( "Voulez-vous réellement sauter le tour de "
          + playerName
          + ", il lui reste "+registration.getPtAction()+" points d'action.") )
      {
        EbEvtPlayerTurn action = new EbEvtPlayerTurn();
        action.setGame( GameEngine.model().getGame() );
        if( registration.getAccount() != null )
        {
          action.setAccountId( registration.getAccount().getId() );
        }
        action.setOldPlayerId( registration.getId() );
        // ok itsn't an automatic action, but with this trick I can track of the guy which
        // end this turn and pass through action checking
        action.setAuto( true );
        GameEngine.model().runSingleAction( action );
      }
    }
    else if( m_banButtons.get( p_event.getSource() ) != null )
    {
      // want to ban player
      EbRegistration registration = m_banButtons.get( p_event.getSource() );
      if( Window.confirm( "Voulez-vous réellement banir " + registration.getAccount().getPseudo()
          + " de la partie " + GameEngine.model().getGame().getName() ) )
      {
        EbAdminBan gameLog = new EbAdminBan();
        gameLog.setAccountId( AppMain.instance().getMyAccount().getId() );
        gameLog.setRegistrationId( registration.getId() );
        gameLog.setGame( GameEngine.model().getGame() );
        GameEngine.model().runSingleAction( gameLog );
      }
    }
  }


}
