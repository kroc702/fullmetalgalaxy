/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.client.widget.WgtConfigGameTime;
import com.fullmetalgalaxy.client.widget.WgtConfigGameVariant;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class DlgGameDetail extends DialogBox implements ClickListener, TabListener
{
  private Button m_btnOk = new Button( "OK" );
  private Button m_btnPlay = new Button( "Play" );
  private Button m_btnPause = new Button( "Pause" );
  private Button m_btnEdit = new Button( "Edite" );
  private VerticalPanel m_panel = new VerticalPanel();
  private TabPanel m_tabPanel = new TabPanel();

  private Panel m_generalPanel = new FlowPanel();
  private Panel m_playerPanel = new FlowPanel();
  private WgtConfigGameTime m_wgtConfigTime = (WgtConfigGameTime)GWT
      .create( WgtConfigGameTime.class );
  private WgtConfigGameVariant m_wgtConfigVariant = (WgtConfigGameVariant)GWT
      .create( WgtConfigGameVariant.class );
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

    m_btnOk.addClickListener( this );
    m_btnPlay.addClickListener( this );
    m_btnPause.addClickListener( this );
    m_btnEdit.addClickListener( this );

    m_tabPanel.add( m_generalPanel, "general" );
    m_tabPanel.add( m_playerPanel, "joueurs" );
    m_tabPanel.add( m_wgtConfigTime, "temps" );
    m_tabPanel.add( m_wgtConfigVariant, "variantes" );
    m_wgtLogs.setStyleName( "fmp-log-panel" );
    m_tabPanel.add( m_wgtLogs, "log" );
    m_wgtAdminLogs.setStyleName( "fmp-log-panel" );
    m_tabPanel.add( m_wgtAdminLogs, "admin log" );


    m_tabPanel.setWidth( "100%" );
    m_tabPanel.selectTab( 0 );
    m_tabPanel.setPixelSize( 700, 400 );
    m_tabPanel.addTabListener( this );

    m_panel.add( m_tabPanel );
    m_panel.add( m_btnOk );
    setWidget( m_panel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  public void show()
  {
    super.show();
    EbGame game = ModelFmpMain.model().getGame();

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
            + Messages.getColorString( ModelFmpMain.model().getMyRegistration().getColor() ) ) );
      }
    }

    m_generalPanel.add( new Label( game.getDescription() ) );

    // Display tides
    String htmlTide = MAppBoard.s_messages.tide() + " "
        + BoardIcons.iconTide( game.getCurrentTide() ).getHTML()
        + " ";
    // + Messages.getTideString( game.getCurrentTide() ) + "'> ";
    if( ModelFmpMain.model().haveWeatherHen() )
    {
      htmlTide += BoardIcons.iconTide( game.getNextTide() ).getHTML() + " ";
      // + Messages.getTideString( game.getNextTide() ) + "'>";
      if( game.isAsynchron() )
      {
        htmlTide += " at " + ClientUtil.s_dateTimeFormat.format( game.estimateNextTideChange() );
      }
    }
    else
    {
      htmlTide += Icons.s_instance.tide_unknown().getHTML();
      // + MAppBoard.s_messages.noForecast() + "'> ";
    }
    HTMLPanel tidePanel = new HTMLPanel( htmlTide );
    tidePanel.setWidth( "100%" );
    m_generalPanel.add( tidePanel );

    // display start game date
    m_generalPanel.add( new Label( MAppBoard.s_messages.gameCreation( ClientUtil.s_dateTimeFormat
        .format( game.getCreationDate() ) )
        + " par " + ModelFmpMain.model().getAccount( game.getAccountCreatorId() ).getPseudo() ) );

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

    // edit button
    m_generalPanel.add( m_btnEdit );
    m_generalPanel.add( new HTML( "<a href='/admin/Servlet?downloadgame=" + game.getId()
        + "'>download</a>" ) );


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
        m_generalPanel.add( m_btnPause );
      }
      else
      {
        m_generalPanel.add( new Label( "Partie en pause" ) );
        m_generalPanel.add( m_btnPlay );
      }
    }

    // set player informations
    // -----------------------
    m_playerPanel.clear();
    m_playerPanel.add( getPlayerWidget() );

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

    m_playerPanel.clear();
    m_playerPanel.add( new Label( MAppBoard.s_messages.xPlayers( ModelFmpMain.model().getGame()
        .getSetRegistration().size() ) ) );

    // get player order
    List<EbRegistration> sortedRegistration = ModelFmpMain.model().getGame()
        .getRegistrationByPlayerOrder();


    Grid m_playerGrid = new Grid( sortedRegistration.size() + 1, 8 );
    m_playerGrid.setStyleName( "fmp-array" );

    m_playerGrid.setText( 0, 0, "" ); // avatar
    m_playerGrid.setText( 0, 1, "login" );
    m_playerGrid.setText( 0, 2, "couleur(s)" );
    m_playerGrid.setText( 0, 3, "pt d'action" );
    m_playerGrid.setText( 0, 4, "pt de victoire" );
    m_playerGrid.setText( 0, 5, "" ); // must play before
    m_playerGrid.setText( 0, 6, "" ); // messages
    m_playerGrid.setText( 0, 7, "" ); // email
    m_playerGrid.getRowFormatter().addStyleName( 0, "fmp-home-gameline-caption" );

    int index = 0;
    for( EbRegistration registration : sortedRegistration )
    {
      index++;

      // display avatar
      if( registration.haveAccount() )
      {
        m_playerGrid.setHTML( index, 0, "<IMG SRC='"
            + ModelFmpMain.model().getAccount( registration.getAccountId() ).getAvatarUrl()
            + "' WIDTH=60 HEIGHT=60 BORDER=0 />" );
      }

      // display login
      String login = "???";
      if( registration.haveAccount() )
      {
        login = ModelFmpMain.model().getAccount( registration.getAccountId() ).getPseudo();
      }
      if( (!ModelFmpMain.model().getGame().isAsynchron())
          && (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == registration) )
      {
        m_playerGrid.setHTML( index, 1, "<b>" + login + "</b>" );
      }
      else
      {
        m_playerGrid.setHTML( index, 1, login );
      }

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
            + Messages.getColorString( color.getValue() ) + "'> ";
      }
      m_playerGrid.setHTML( index, 2, htmlColors );

      // display action points
      m_playerGrid.setText( index, 3, "" + registration.getPtAction() );

      // display Wining points
      m_playerGrid.setText( index, 4, "" + registration.getWinningPoint() );

      // display 'must play before'
      if( (!ModelFmpMain.model().getGame().isAsynchron())
          && (registration.getEndTurnDate() != null) )
      {
        m_playerGrid.setText( 0, 5, "doit jouer avant" );
        m_playerGrid.setText( index, 5, ClientUtil.s_dateTimeFormat.format( registration
            .getEndTurnDate() ) );
      }

      // display email
      if( (registration.haveAccount()) )
      {
        String htmlMail = "<a target='_blank' href='/privatemsg.jsp?id="
            + registration.getAccountId()
            + "'><img src='" + "/images/css/icon_pm.gif' border=0 alt='PM'></a>";
        m_playerGrid.setHTML( index, 7, htmlMail );
      }
    }

    m_playerPanel.add( m_playerGrid );
    return m_playerPanel;
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnOk )
    {
      hide();
    }
    else if( p_sender == m_btnPause )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePause( ModelFmpMain.model().getMyAccountId() );
      gameLog.setGame( ModelFmpMain.model().getGame() );
      ModelFmpMain.model().runSingleAction( gameLog );
    }
    else if( p_sender == m_btnPlay )
    {
      AnEvent gameLog = GameLogFactory.newAdminTimePlay( ModelFmpMain.model().getMyAccountId() );
      gameLog.setGame( ModelFmpMain.model().getGame() );
      ModelFmpMain.model().runSingleAction( gameLog );
    }
    else if( p_sender == m_btnEdit )
    {
      AppMain.instance().gotoEditGame( ModelFmpMain.model().getGame().getId() );
      hide();
    }

  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.TabListener#onBeforeTabSelected(com.google.gwt.user.client.ui.SourcesTabEvents, int)
   */
  public boolean onBeforeTabSelected(SourcesTabEvents p_sender, int p_tabIndex)
  {
    return true;
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.TabListener#onTabSelected(com.google.gwt.user.client.ui.SourcesTabEvents, int)
   */
  public void onTabSelected(SourcesTabEvents p_sender, int p_tabIndex)
  {
    switch( p_tabIndex )
    {
    case 0: // general
      break;
    case 1: // players
      break;
    case 2: // time
      m_wgtConfigTime.attachBean( ModelFmpMain.model().getGame().getEbConfigGameTime() );
      m_wgtConfigTime.setReadOnly( true );
      break;
    case 3: // variant
      m_wgtConfigVariant.attachBean( ModelFmpMain.model().getGame().getEbConfigGameVariant() );
      m_wgtConfigVariant.setReadOnly( true );
      break;
    case 4: // logs
      m_wgtLogs.redraw();
      break;
    case 5: // admin logs
      m_wgtAdminLogs.redraw();
      break;
    }

  }



}
