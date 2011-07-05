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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.BindedWgtConfigGameVariant;
import com.fullmetalgalaxy.client.widget.WgtConfigGameTime;
import com.fullmetalgalaxy.client.widget.WgtConfigGameVariant;
import com.fullmetalgalaxy.client.widget.WgtConstructReserve;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminBan;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogFactory;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * TODO i18n
 */
public class DlgGameDetail extends DialogBox implements ClickHandler, SelectionHandler<Integer>
{
  private Button m_btnOk = new Button( "OK" );
  private Button m_btnPlay = new Button( "Play" );
  private Button m_btnPause = new Button( "Pause" );
  private Button m_btnEdit = new Button( "Edite" );
  private Map<Widget, EbRegistration> m_banButtons = new HashMap<Widget, EbRegistration>();
  private Image m_btnSkipTurn = Icons.s_instance.endTurn32().createImage();
  private ToggleButton m_btnGrid = new ToggleButton( "Grille" );
  private ToggleButton m_btnAtmosphere = new ToggleButton( "Atmosphere" );
  private ToggleButton m_btnCustomMap = new ToggleButton( "Affichage carte custom" );
  private VerticalPanel m_panel = new VerticalPanel();
  private TabPanel m_tabPanel = new TabPanel();

  private Panel m_generalPanel = new FlowPanel();
  private Panel m_playerPanel = new FlowPanel();
  private WgtConstructReserve m_wgtReserve = new WgtConstructReserve();
  private WgtConfigGameTime m_wgtConfigTime = new WgtConfigGameTime();
  private WgtConfigGameVariant m_wgtConfigVariant = new BindedWgtConfigGameVariant();
  private WgtGameLogs m_wgtLogs = new WgtGameLogs();
  private WgtGameAdminLogs m_wgtAdminLogs = new WgtGameAdminLogs();


  /**
   * 
   */
  public DlgGameDetail()
  {
    // auto hide / modal
    super( true, true );

    // Set the dialog box's caption.
    setText( "Game detail" );

    m_btnOk.addClickHandler( this );
    m_btnPlay.addClickHandler( this );
    m_btnPause.addClickHandler( this );
    m_btnEdit.addClickHandler( this );
    m_btnGrid.addClickHandler( this );
    m_btnAtmosphere.addClickHandler( this );
    m_btnCustomMap.addClickHandler( this );
    m_btnSkipTurn.setTitle( "Fin de tour" );
    m_btnSkipTurn.setStyleName( "fmp-button" );
    m_btnSkipTurn.addClickHandler( this );
    
    m_tabPanel.add( m_generalPanel, "general" );
    m_tabPanel.add( m_playerPanel, "joueurs" );
    m_wgtReserve.setStyleName( "fmp-log-panel" );
    m_wgtReserve.setSize( "650px", "400px" );
    m_tabPanel.add( m_wgtReserve, "reserve" );
    
    m_wgtConfigTime.setReadOnly( true );
    m_tabPanel.add( m_wgtConfigTime, "temps" );
    m_tabPanel.add( m_wgtConfigVariant, "variantes" );
    m_wgtLogs.setStyleName( "fmp-log-panel" );
    m_wgtLogs.setSize( "650px", "400px" );
    m_tabPanel.add( m_wgtLogs, "log" );
    m_wgtAdminLogs.setStyleName( "fmp-log-panel" );
    m_wgtAdminLogs.setSize( "650px", "400px" );
    m_tabPanel.add( m_wgtAdminLogs, "admin log" );


    m_tabPanel.setWidth( "100%" );
    m_tabPanel.selectTab( 0 );
    m_tabPanel.setPixelSize( 700, 400 );
    m_tabPanel.addSelectionHandler( this );

    m_panel.add( m_tabPanel );
    m_panel.add( m_btnOk );
    setWidget( m_panel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    super.show();
    Game game = ModelFmpMain.model().getGame();

    // Set the dialog box's caption.
    setText( game.getName() );

    // set general informations
    // ------------------------
    m_generalPanel.clear();

    if( game.getGameType() == GameType.Puzzle )
    {
      m_generalPanel.add( new Label( "Partie en mode 'Puzzle'" ) );
      if( ModelFmpMain.model().getMyRegistration() != null )
      {
        m_generalPanel.add( new Label( ", vous controllez les pions "
            + Messages.getColorString( 0, ModelFmpMain.model().getMyRegistration().getColor() ) ) );
      }
    }

    m_generalPanel.add( new Label( game.getDescription() ) );

    // Display tides
    String htmlTide = MAppBoard.s_messages.tide() + " "
        + BoardIcons.iconTide( game.getCurrentTide() ).getHTML()
        + " ";
    // + Messages.getTideString( game.getCurrentTide() ) + "'> ";
    if( (ModelFmpMain.model().getMyRegistration() != null)
        && (ModelFmpMain.model().getMyRegistration().getWorkingWeatherHenCount() > 0) )
    {
      htmlTide += BoardIcons.iconTide( game.getNextTide() ).getHTML() + " ";
      // + Messages.getTideString( game.getNextTide() ) + "'>";
    }
    else
    {
      htmlTide += Icons.s_instance.tide_unknown().getHTML();
      // + MAppBoard.s_messages.noForecast() + "'> ";
    }
    if( game.isAsynchron() )
    {
      htmlTide += " at " + ClientUtil.s_dateTimeFormat.format( game.estimateNextTideChange() );
    }

    HTMLPanel tidePanel = new HTMLPanel( htmlTide );
    tidePanel.setWidth( "100%" );
    m_generalPanel.add( tidePanel );

    // display start game date
    if( game.getGameType() == GameType.MultiPlayer )
    {
      m_generalPanel.add( new HTML( MAppBoard.s_messages.gameCreation( ClientUtil.s_dateTimeFormat
        .format( game.getCreationDate() ) )
        + " par <a href='/profile.jsp?id="
        + (game.getAccountCreator() == null ? "0" : game.getAccountCreator().getId())
        + "' target='_blank'>"
          + (game.getAccountCreator() == null ? "???" : game.getAccountCreator().getPseudo())
          + "</a>" ) );
      // TODO i18n
      m_generalPanel
          .add( new HTML(
              "Reporter un problème à <a href='mailto:admin@fullmetalgalaxy.com'>admin@fullmetalgalaxy.com</a>" ) );
    }

    if( ModelFmpMain.model().isJoined() )
    {
      EbRegistration registration = ModelFmpMain.model().getMyRegistration();
      if( game.isAsynchron() )
      {
        // Display next action point increments
        Date nextActionIncrement = game.estimateTimeStepDate( game.getCurrentTimeStep() + 1 );
        m_generalPanel.add( new Label( MAppBoard.s_messages.nextActionPt( game
            .getEbConfigGameTime().getActionPtPerTimeStep(), ClientUtil.s_dateTimeFormat
            .format( nextActionIncrement ) ) ) );
      }
      else if( registration.getEndTurnDate() != null )
      {
        if( registration.getId() == game.getCurrentPlayerRegistration().getId() )
        {
          m_generalPanel.add( new Label( "Fin du tour: "
              + ClientUtil.s_dateTimeFormat.format( registration.getEndTurnDate() ) ) );
        }
        else
        {
          m_generalPanel.add( new Label( "Prochain tour avant: "
              + ClientUtil.s_dateTimeFormat.format( registration.getEndTurnDate() ) ) );
        }
      }
    }

    // grid button
    m_generalPanel.add( m_btnGrid );
    m_btnGrid.setDown( ModelFmpMain.model().isGridDisplayed() );
    // atmosphere button
    m_generalPanel.add( m_btnAtmosphere );
    m_btnAtmosphere.setDown( ModelFmpMain.model().isAtmosphereDisplayed() );
    // standard display button
    if( game.getMapUri() != null )
    {
      m_generalPanel.add( m_btnCustomMap );
      m_btnCustomMap.setDown( ModelFmpMain.model().isCustomMapDisplayed() );
    }

    // display end game date
    if( !game.isAsynchron() )
    {
      m_generalPanel.add( new Label( MAppBoard.s_messages.turn() + " " + game.getCurrentTimeStep()
          + "/" + game.getEbConfigGameTime().getTotalTimeStep() ) );
    }
    m_generalPanel.add( new Label( MAppBoard.s_messages.gameFinishAt( ClientUtil.s_dateTimeFormat
        .format( game.estimateEndingDate() ) ) ) );

    if( game.getGameType() == GameType.MultiPlayer )
    {
      if( game.isStarted() )
      {
        m_generalPanel.add( new Label( "Partie en cours" ) );
      }
      else
      {
        m_generalPanel.add( new Label( "Partie en pause" ) );
      }
    }

    if( (game.getAccountCreator() != null &&
        ModelFmpMain.model().getMyAccount().getId() == game.getAccountCreator().getId() )
        || ModelFmpMain.model().iAmAdmin() )
    {
      // play / pause button
      if( game.getGameType() == GameType.MultiPlayer )
      {
        if( game.isStarted() )
        {
          m_generalPanel.add( m_btnPause );
        }
        else
        {
          m_generalPanel.add( m_btnPlay );
        }
      }
      // edit button
      m_generalPanel.add( m_btnEdit );
    }

    if( ModelFmpMain.model().iAmAdmin() )
    {
      // download button
      m_generalPanel.add( new HTML( "<a href='/admin/Servlet?downloadgame=" + game.getId()
          + "'>download</a>" ) );
    }

    // set player informations
    // -----------------------
    m_playerPanel.clear();
    m_playerPanel.add( getPlayerWidget() );

    
    // redraw reserve
    // --------------
    m_wgtReserve.redraw();
    
    // display winner !
    /*if( ModelFmpMain.model().getGame().isFinished() )
    {
      Image image = new Image( "winner.jpg" );
      image.setWidth( "100%" );
      m_playerPanel.add( image );
      m_playerPanel.add( new HTMLPanel( "<center><big><b>" + winnerLogin + "</b></big></center>" ) );
    }*/

  }

  private Widget getPlayerWidget()
  {
    Panel m_playerPanel = new FlowPanel();
    m_banButtons.clear();

    m_playerPanel.clear();
    int PlayerCount = ModelFmpMain.model().getGame().getSetRegistration().size();
    m_playerPanel.add( new Label( MAppBoard.s_messages.xPlayers( PlayerCount ) ) );

    // message to all link
    String pseudoList[] = new String[PlayerCount];
    int i = 0;
    for( EbRegistration registration : ModelFmpMain.model().getGame().getSetRegistration() )
    {
      pseudoList[i] = registration.getAccount().getPseudo();
      i++;
    }
    m_playerPanel.add( new HTML( "<a href='"
        + EbPublicAccount
            .getPMUrl( "[FMG] " + ModelFmpMain.model().getGame().getName(), pseudoList )
        + "' >Envoyer un message à tous</a>" ) );

    // get player order
    List<EbRegistration> sortedRegistration = ModelFmpMain.model().getGame()
        .getRegistrationByPlayerOrder();


    Grid m_playerGrid = new Grid( sortedRegistration.size() + 1, 9 );
    m_playerGrid.setStyleName( "fmp-array" );

    m_playerGrid.setText( 0, 0, "" ); // avatar
    m_playerGrid.setText( 0, 1, "login" );
    m_playerGrid.setText( 0, 2, "couleur(s)" );
    m_playerGrid.setText( 0, 3, "pt d'action" );
    m_playerGrid.setHTML( 0, 4, "pt de victoire<br/>(estimation)" );
    m_playerGrid.setText( 0, 5, "" ); // must play before
    m_playerGrid.setText( 0, 6, "" ); // messages
    m_playerGrid.setText( 0, 7, "" ); // ban
    m_playerGrid.setText( 0, 8, "" ); // skip turn
    m_playerGrid.getRowFormatter().addStyleName( 0, "fmp-home-gameline-caption" );

    int index = 0;
    for( EbRegistration registration : sortedRegistration )
    {
      index++;

      // display avatar
      if( registration.getAccount() != null )
      {
        m_playerGrid.setHTML( index, 0, "<IMG SRC='" + registration.getAccount().getAvatarUrl()
            + "' WIDTH=60 HEIGHT=60 BORDER=0 />" );
      }

      // display login
      String login = registration.getAccount().getPseudo();
      String html = "<a href='" + registration.getAccount().getProfileUrl()
          + "' target='_blank'>" + login
          + "</a>";
      if( ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == registration )
      {
        html += Icons.s_instance.action16().getHTML();
      }
      m_playerGrid.setHTML( index, 1, html );

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
      m_playerGrid.setHTML( index, 2, htmlColors );

      // display action points
      m_playerGrid
          .setText(
              index,
              3,
              ""  + registration.getPtAction()
                  + "/"
                  + (ModelFmpMain.model().getGame().getEbConfigGameVariant()
                      .getActionPtMaxReserve() + ((registration.getEnuColor().getNbColor() - 1) * ModelFmpMain
                      .model().getGame().getEbConfigGameVariant().getActionPtMaxPerExtraShip())) );

      // display Wining points
      m_playerGrid.setText( index, 4, "" + registration.estimateWinningScore(ModelFmpMain.model().getGame()) );

      // display 'must play before'
      if( (!ModelFmpMain.model().getGame().isAsynchron())
          && (registration.getEndTurnDate() != null) )
      {
        m_playerGrid.setText( 0, 5, "doit jouer avant" );
        m_playerGrid.setText( index, 5, ClientUtil.s_dateTimeFormat.format( registration
            .getEndTurnDate() ) );
      }

      // display email messages
      String htmlMail = "<a target='_blank' href='/privatemsg.jsp?id="
          + registration.getAccount().getId() + "'><img src='"
          + "/images/css/icon_pm.gif' border=0 alt='PM'></a>";
      m_playerGrid.setHTML( index, 6, htmlMail );

      // display admin button
      if( (ModelFmpMain.model().getGame().getAccountCreator() != null
          && ModelFmpMain.model().getMyAccount().getId() == ModelFmpMain.model().getGame()
          .getAccountCreator().getId()) || ModelFmpMain.model().iAmAdmin() ) 
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
        if( (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == registration) )
        {
          m_playerGrid.setWidget( index, 8, m_btnSkipTurn );
        }
        
      }
      
    }

    m_playerPanel.add( m_playerGrid );
    return m_playerPanel;
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnOk )
    {
      hide();
    }
    else if( p_event.getSource() == m_btnGrid )
    {
      ModelFmpMain.model().setGridDisplayed( m_btnGrid.isDown() );
    }
    else if( p_event.getSource() == m_btnAtmosphere )
    {
      ModelFmpMain.model().setAtmosphereDisplayed( m_btnAtmosphere.isDown() );
    }
    else if( p_event.getSource() == m_btnCustomMap )
    {
      ModelFmpMain.model().setCustomMapDisplayed( m_btnCustomMap.isDown() );
    }
    else if( p_event.getSource() == m_btnPause )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePause( ModelFmpMain.model().getMyAccount()
          .getId() );
      gameLog.setGame( ModelFmpMain.model().getGame() );
      ModelFmpMain.model().runSingleAction( gameLog );
    }
    else if( p_event.getSource() == m_btnPlay )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePlay( ModelFmpMain.model().getMyAccount()
          .getId() );
      gameLog.setGame( ModelFmpMain.model().getGame() );
      ModelFmpMain.model().runSingleAction( gameLog );
    }
    else if( p_event.getSource() == m_btnEdit )
    {
      AppMain.instance().gotoEditGame( ModelFmpMain.model().getGame().getId() );
      hide();
    }
    else if( p_event.getSource() == m_btnSkipTurn )
    {
      EbRegistration registration = ModelFmpMain.model().getGame().getCurrentPlayerRegistration();
      if( Window.confirm( "Voulez-vous réellement sauter le tour de "
          + registration.getAccount().getPseudo()
          + ", il lui reste "+registration.getPtAction()+" points d'action.") )
      {
        EbEvtPlayerTurn action = new EbEvtPlayerTurn();
        action.setGame( ModelFmpMain.model().getGame() );
        action.setAccountId( ModelFmpMain.model().getMyAccount().getId() );
        // ok itsn't an automatic action, but with this trick I can track of the guy which
        // end this turn and pass through action checking
        action.setAuto( true );
        ModelFmpMain.model().runSingleAction( action );
      }
    }
    else if( m_banButtons.get( p_event.getSource() ) != null )
    {
      // want to ban player
      EbRegistration registration = m_banButtons.get( p_event.getSource() );
      if( Window.confirm( "Voulez-vous réellement banir " + registration.getAccount().getPseudo()
          + " de la partie " + ModelFmpMain.model().getGame().getName() ) )
      {
        EbAdminBan gameLog = new EbAdminBan();
        gameLog.setAccountId( ModelFmpMain.model().getMyAccount().getId() );
        gameLog.setRegistrationId( registration.getId() );
        gameLog.setGame( ModelFmpMain.model().getGame() );
        ModelFmpMain.model().runSingleAction( gameLog );
      }
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(com.google.gwt.event.logical.shared.SelectionEvent)
   */
  @Override
  public void onSelection(SelectionEvent<Integer> p_event)
  {
    switch( p_event.getSelectedItem() )
    {
    case 0: // general
      break;
    case 1: // players
      break;
    case 2: // reserve 
      m_wgtReserve.redraw();
      break;
    case 3: // time
      m_wgtConfigTime.attachBean( ModelFmpMain.model().getGame().getEbConfigGameTime() );
      m_wgtConfigTime.setReadOnly( true );
      break;
    case 4: // variant
      m_wgtConfigVariant.attachBean( ModelFmpMain.model().getGame().getEbConfigGameVariant() );
      m_wgtConfigVariant.setReadOnly( true );
      break;
    case 5: // logs
      m_wgtLogs.redraw();
      break;
    case 6: // admin logs
      m_wgtAdminLogs.redraw();
      break;
    }
  }



}
