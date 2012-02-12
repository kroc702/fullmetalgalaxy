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
package com.fullmetalgalaxy.client.game.tabmenu;

import java.util.Date;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.WgtGameTime;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogFactory;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author Vincent Legendre
 * TODO i18n
 */
public class WgtGameInfo extends Composite implements ClickHandler
{
  private Button m_btnPlay = new Button( "Play" );
  private Button m_btnPause = new Button( "Pause" );
  private Button m_btnEdit = new Button( "Edite" );
  private Button m_btnAbort = new Button( "Annuler la partie" );
  private ToggleButton m_btnRecordEvent = new ToggleButton( "Enregistrer" );
  private ToggleButton m_btnGrid = new ToggleButton( "Grille" );
  private ToggleButton m_btnAtmosphere = new ToggleButton( "Atmosphere" );
  private ToggleButton m_btnCustomMap = new ToggleButton( "Affichage carte custom" );

  private Panel m_generalPanel = new FlowPanel();
  

  /**
   * 
   */
  public WgtGameInfo()
  {
    super();

    m_btnPlay.addClickHandler( this );
    m_btnPause.addClickHandler( this );
    m_btnEdit.addClickHandler( this );
    m_btnAbort.addClickHandler( this );
    m_btnRecordEvent.addClickHandler( this );
    m_btnGrid.addClickHandler( this );
    m_btnAtmosphere.addClickHandler( this );
    m_btnCustomMap.addClickHandler( this );
    
    initGeneralPanel();
    initWidget( m_generalPanel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  private void initGeneralPanel()
  {
    Game game = GameEngine.model().getGame();

    // set general informations
    // ------------------------
    m_generalPanel.clear();

    if( game.getGameType() == GameType.Puzzle )
    {
      m_generalPanel.add( new Label( "Partie en mode 'Puzzle'" ) );
      if( GameEngine.model().getMyRegistration() != null )
      {
        m_generalPanel.add( new Label( ", vous controllez les pions "
            + Messages.getColorString( 0, GameEngine.model().getMyRegistration().getColor() ) ) );
      }
    }

    m_generalPanel.add( new Label( game.getDescription() ) );

    // Display tides
    String htmlTide = MAppBoard.s_messages.tide() + " "
        + BoardIcons.iconTide( game.getCurrentTide() ).getHTML()
        + " ";
    // + Messages.getTideString( game.getCurrentTide() ) + "'> ";
    if( (GameEngine.model().getMyRegistration() != null)
        && (GameEngine.model().getMyRegistration().getWorkingWeatherHenCount() > 0) )
    {
      htmlTide += BoardIcons.iconTide( game.getNextTide() ).getHTML() + " ";
      // + Messages.getTideString( game.getNextTide() ) + "'>";
    }
    else
    {
      htmlTide += Icons.s_instance.tide_unknown().getHTML();
      // + MAppBoard.s_messages.noForecast() + "'> ";
    }
    if( game.isParallel() )
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

    if( GameEngine.model().isJoined() )
    {
      EbRegistration registration = GameEngine.model().getMyRegistration();
      if( game.isParallel() )
      {
        // Display next action point increments
        Date nextActionIncrement = game.estimateTimeStepDate( game.getCurrentTimeStep() + 1 );
        m_generalPanel.add( new Label( MAppBoard.s_messages.nextActionPt(
            EbConfigGameTime.getActionInc( game, registration ),
            ClientUtil.s_dateTimeFormat.format( nextActionIncrement ) ) ) );
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
    m_btnGrid.setDown( GameEngine.model().isGridDisplayed() );
    // atmosphere button
    m_generalPanel.add( m_btnAtmosphere );
    m_btnAtmosphere.setDown( GameEngine.model().isAtmosphereDisplayed() );
    // standard display button
    if( game.getMapUri() != null )
    {
      m_generalPanel.add( m_btnCustomMap );
      m_btnCustomMap.setDown( GameEngine.model().isCustomMapDisplayed() );
    }

    // display end game date
    if( !game.isParallel() )
    {
      m_generalPanel.add( new Label( MAppBoard.s_messages.turn() + " " + game.getCurrentTimeStep()
          + "/" + game.getEbConfigGameTime().getTotalTimeStep() ) );
    }
    m_generalPanel.add( new Label( MAppBoard.s_messages.gameFinishAt( ClientUtil.s_dateTimeFormat
        .format( game.estimateEndingDate() ) ) ) );

    if( game.getGameType() == GameType.MultiPlayer )
    {
      switch( game.getStatus() )
      {
      case Open:
        m_generalPanel.add( new Label( "Partie en pause et ouverte aux inscriptions" ) );
        break;
      case Aborted:
        m_generalPanel.add( new Label( "Partie annulée" ) );
        break;
      case Pause:
        m_generalPanel.add( new Label( "Partie en pause" ) );
        break;
      case History:
        m_generalPanel.add( new Label( "Partie archivée" ) );
        break;
      case Puzzle:
        m_generalPanel.add( new Label( "Partie solo" ) );
        break;
      case Running:
        m_generalPanel.add( new Label( "Partie en cours" ) );
        break;
      default:
        m_generalPanel.add( new Label( "Cette partie est dans un état inconnu" ) );
        break;
      }
    }

    if( (game.getAccountCreator() != null &&
        AppMain.instance().getMyAccount().getId() == game.getAccountCreator().getId() )
        || AppMain.instance().iAmAdmin() )
    {
      // play / pause button
      if( game.getGameType() == GameType.MultiPlayer )
      {
        if( game.getStatus() == GameStatus.Running )
        {
          m_generalPanel.add( m_btnPause );
        }
        else if( game.getStatus() == GameStatus.Open || game.getStatus() == GameStatus.Pause )
        {
          m_generalPanel.add( m_btnPlay );
        }
      }
      // edit button
      m_generalPanel.add( m_btnEdit );
      m_generalPanel.add( m_btnAbort );
    }

    if( AppMain.instance().iAmAdmin() )
    {
      // download button
      m_generalPanel.add( new HTML( "<a href='/admin/Servlet?downloadgame=" + game.getId()
          + "'>download</a>" ) );
      // record events
      m_generalPanel.add( m_btnRecordEvent );
    }

    m_generalPanel.add( new HTML( "<br/>") );
    WgtGameTime wgtGameTime = new WgtGameTime();
    wgtGameTime.setReadOnly( true );
    m_generalPanel.add( wgtGameTime );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnGrid )
    {
      GameEngine.model().setGridDisplayed( m_btnGrid.isDown() );
    }
    else if( p_event.getSource() == m_btnAtmosphere )
    {
      GameEngine.model().setAtmosphereDisplayed( m_btnAtmosphere.isDown() );
    }
    else if( p_event.getSource() == m_btnCustomMap )
    {
      GameEngine.model().setCustomMapDisplayed( m_btnCustomMap.isDown() );
    }
    else if( p_event.getSource() == m_btnPause )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePause( AppMain.instance().getMyAccount()
          .getId() );
      gameLog.setGame( GameEngine.model().getGame() );
      GameEngine.model().runSingleAction( gameLog );
    }
    else if( p_event.getSource() == m_btnPlay )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePlay( AppMain.instance().getMyAccount()
          .getId() );
      gameLog.setGame( GameEngine.model().getGame() );
      GameEngine.model().runSingleAction( gameLog );
    }
    else if( p_event.getSource() == m_btnEdit )
    {
      ClientUtil.gotoUrl( "/editgame.jsp?id="+ GameEngine.model().getGame().getId() );
    }
    else if( p_event.getSource() == m_btnAbort )
    {
      // TODO i18n
      if( Window.confirm( "êtes vous certain de vouloir annuler cette partie ?" ) )
      {
        AnEvent gameLog = GameLogFactory.newAdminAbort( AppMain.instance().getMyAccount().getId() );
        gameLog.setGame( GameEngine.model().getGame() );
        GameEngine.model().runSingleAction( gameLog );
      }
    }
    else if( p_event.getSource() == m_btnRecordEvent )
    {
      if( m_btnRecordEvent.isDown() )
      {
        GameEngine.model().getGame()
            .setMessage( EventsPlayBuilder.GAME_MESSAGE_RECORDING_TAG + "\n" );
      }
      GameEngine.model().getActionBuilder().setRecordMode( m_btnRecordEvent.isDown() );
    }
   }





}
