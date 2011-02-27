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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MiniApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.widget.BindedWgtGameInfo;
import com.fullmetalgalaxy.client.widget.WgtBean;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameEventStack;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kroc
 *
 */

public class MAppGameCreation extends Composite implements MiniApp, ClickHandler, ChangeListener,
    TabListener
{
  public static final String HISTORY_ID = "new";

  public static final String s_TokenIdGame = "idGame";


  private static MessagesAppGameCreation s_messages = (MessagesAppGameCreation)GWT
      .create( MessagesAppGameCreation.class );

  // model controller
  private boolean m_isLandGenerated = false;
  private boolean m_isOreGenerated = false;

  // UI
  private FlowPanel m_panel = new FlowPanel();
  private TabPanel m_tabPanel = new TabPanel();
  private WgtBean m_simpleForm = new BindedWgtGameInfo();
  private WgtEditLand m_wgtEditLand = new WgtEditLand();
  private WgtEditTokens m_wgtEditTokens = new WgtEditTokens();
  private WgtEditForces m_wgtEditForces = new WgtEditForces();
  private WgtEditTriggers m_wgtEditTriggers = new WgtEditTriggers();
  private WgtEditExtra m_wgtEditExtra = new WgtEditExtra();
  private Button m_btnCreateGame = new Button( s_messages.createGame() );
  private Button m_btnCancel = new Button( s_messages.cancel() );

  public MAppGameCreation()
  {
    m_btnCreateGame.addClickHandler( this );
    m_btnCancel.addClickHandler( this );


    // m_form.setBean( ModelFmpMain.model().getGame() );

    m_tabPanel.add( m_simpleForm, s_messages.simpleCreation() );
    m_tabPanel.setSize( "100%", "90%" );
    m_tabPanel.selectTab( 0 );
    m_tabPanel.addTabListener( this );

    // m_tabPanel.add( m_form, s_messages.hardWay() );

    m_tabPanel.add( m_wgtEditLand, s_messages.map() );
    m_tabPanel.add( m_wgtEditTokens, "Pions" );
    m_tabPanel.add( m_wgtEditForces, "Forces" );
    m_tabPanel.add( m_wgtEditTriggers, "Triggers" );
    m_tabPanel.add( m_wgtEditExtra, "Extra" );

    m_panel.setSize( "100%", "100%" );
    m_panel.add( m_tabPanel );
    m_panel.add( m_btnCreateGame );
    m_panel.add( m_btnCancel );

    initWidget( m_panel );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  public void createGame()
  {
    EbGame game = ModelFmpMain.model().getGame();
    if( game.getName().compareTo( "" ) == 0 )
    {
      Window.alert( s_messages.errorName() );
      return;
    }
    if( (game.getEbConfigGameTime().estimateTotalActionPoint() < 250)
        || (game.getEbConfigGameTime().estimateTotalActionPoint() > 450) )
    {
      Window.alert( s_messages.errorActionPt() );
      return;
    }
    if( (game.getLandWidth() > 70) || (game.getLandHeight() > 50) )
    {
      Window.alert( s_messages.errorMapTooLarge( 70, 50 ) );
      return;
    }
    if( game.getMaxNumberOfPlayer() < 2 )
    {
      Window.alert( "Il faut au moins deux joueurs pour créer une partie" );
      return;
    }

    game.setAccountCreatorId( ModelFmpMain.model().getMyAccountId() );

    // tune configuration
    if(game.getConfigGameVariant() != null)
    {
      // standard variant configuration: construct quantity have to be multiply by player count
      game.setConfigGameVariant( game.getConfigGameVariant() );
      game.getEbConfigGameVariant().multiplyConstructQty( game.getMaxNumberOfPlayer() );
    }
    
    // lands creation
    if( m_isLandGenerated == false )
    {
      GameGenerator.generLands();
    }
    if( m_isOreGenerated == false )
    {
      GameGenerator.populateOres();
    }
    GameGenerator.cleanToken();

    // (3) Create an asynchronous callback to handle the result.
    FmpCallback<EbBase> callback = new FmpCallback<EbBase>()
    {
      @Override
      public void onSuccess(EbBase p_result)
      {
        super.onSuccess( p_result );
        // load newly created game to show it
        ModelFmpMain.model().getGame().updateFrom( p_result );
        ModelFmpMain.model().getActionBuilder().setGame( ModelFmpMain.model().getGame() );
        // this was in the old time where we had only one html page for
        // everything
        // AppMain.instance().gotoGame( p_result.getId() );
        // MAppMessagesStack.s_instance.showMessage(
        // s_messages.gameCreationSuccess() );
        ClientUtil.gotoUrl( "/game.jsp?id=" + p_result.getId() );
      }

      @Override
      public void onFailure(Throwable p_caught)
      {
        super.onFailure( p_caught );
        Window.alert( p_caught.getMessage() );
      }
    };

    // (4) Make the call. Control flow will continue immediately and later
    // 'callback' will be invoked when the RPC completes.
    Services.Util.getInstance().saveGame( ModelFmpMain.model().getGame(), callback );

  }

  public void saveGame()
  {
    String comment = Window.prompt( "Un commentaire pour cette modif ?", "" );

    // (3) Create an asynchronous callback to handle the result.
    FmpCallback<EbBase> callback = new FmpCallback<EbBase>()
    {
      @Override
      public void onSuccess(EbBase p_result)
      {
        super.onSuccess( p_result );
        // load newly created game to show it
        ModelFmpMain.model().getGame().updateFrom( p_result );
        ModelFmpMain.model().getActionBuilder().setGame( ModelFmpMain.model().getGame() );
        AppMain.instance().gotoGame( p_result.getId() );
        MAppMessagesStack.s_instance.showMessage( "Modif sauvegardes" );
      }

      @Override
      public void onFailure(Throwable p_caught)
      {
        super.onFailure( p_caught );
        Window.alert( p_caught.getMessage() );
      }
    };

    // (4) Make the call. Control flow will continue immediately and later
    // 'callback' will be invoked when the RPC completes.
    EbGame game = ModelFmpMain.model().getGame();
    GameEventStack stack = game.getGameEventStack();
    game.setGameEventStack( game ); // as stack may be client specific class
    Services.Util.getInstance().saveGame( ModelFmpMain.model().getGame(), comment, callback );
    game.setGameEventStack( stack );
  }

  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnCreateGame )
    {
      if( ModelFmpMain.model().getGame().isTrancient() )
      {
        createGame();
      }
      else
      {
        saveGame();
      }
    }
    else if( p_event.getSource() == m_btnCancel )
    {
      ModelFmpMain.model().getGame().reinit();
      History.back();
    }
  }

  /**
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onChange(Widget p_sender)
  {
    // m_form.initFromBean();
  }

  /**
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return this;
  }


  /**
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  @Override
  public void hide()
  {
    // nothing to do ...
  }

  /**
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  @Override
  public void show(HistoryState p_state)
  {
    if( !ModelFmpMain.model().isLogged() )
    {
      Window.alert( "Pour éditer une partie vous devez etre loggé" );
      AppMain.instance().gotoHome();
      return;
    }

    if( !p_state.containsKey( s_TokenIdGame ) || p_state.getLong( s_TokenIdGame ) == 0 )
    {
      // create a new game
      ModelFmpMain.model().reinitGame();
      initNewGame();
    }
    else
    {
      if( ModelFmpMain.model().getGame().getId() != p_state.getLong( s_TokenIdGame ) )
      {
        // load game before
        AppMain.instance().loadModelFmpBoard( p_state.getString( s_TokenIdGame ) );
      }
      else
      {
        // game is loaded... nothing to do !
      }
    }

    ModelFmpMain.model().setZoomDisplayed( EnuZoom.Small );
    m_simpleForm.attachBean( ModelFmpMain.model().getGame() );
  }

  /**
   * initialize game to defaults parameters
   */
  private void initNewGame()
  {
    EbGame game = ModelFmpMain.model().getGame();
    game.setAccountCreatorId( ModelFmpMain.model().getMyAccountId() );

    /*    ModelFmpMain.model().getGame().setTotalTimeStep( 120 );
        ModelFmpMain.model().getGame().setRegistrationEndDate(
            new Date( System.currentTimeMillis() + 2021760000 ) );
        ModelFmpMain.model().getGame().setStartingDate( new Date( System.currentTimeMillis() ) );
        ModelFmpMain.model().getGame().setTimeStepDurationInSec( 17280 );
        ModelFmpMain.model().getGame().setActionPtPerTimeStep( 3 );
        ModelFmpMain.model().getGame().setActionPtPerExtraShip( 1 );
        ModelFmpMain.model().getGame().setTideChangeFrequency( 5 );
        ModelFmpMain.model().getGame().setMinNumberOfPlayer( 2 );
    */
    game.setConfigGameTime( ConfigGameTime.Standard );
    game.setConfigGameVariant( ConfigGameVariant.Standard );
    game.setMaxNumberOfPlayer( 4 );
    GameGenerator.setSize( MapSize.Medium );
    GameGenerator.clearLand( LandType.Plain );

    m_isLandGenerated = false;
    m_isOreGenerated = false;
  }

  @Override
  public void onTabSelected(SourcesTabEvents p_sender, int p_tabIndex)
  {
    // Let the user know what they just did.
    switch( p_tabIndex )
    {
    case 0:
      m_simpleForm.attachBean( ModelFmpMain.model().getGame() );
      // m_simpleForm.initFromBean();
      break;
    case 1: // map
      m_wgtEditLand.setPixelSize( m_tabPanel.getOffsetWidth(), m_tabPanel.getOffsetHeight() - 20 );
      m_isLandGenerated = true;
      ModelFmpMain.model().fireModelUpdate();
      break;
    case 2: // tokens
      m_wgtEditTokens.setPixelSize( m_tabPanel.getOffsetWidth(), m_tabPanel.getOffsetHeight() - 20 );
      m_isOreGenerated = true;
      ModelFmpMain.model().fireModelUpdate();
      break;
    case 3: // forces
      m_wgtEditForces.refreshRegistrationList();
      break;
    case 4: // triggers
      m_wgtEditTriggers.refreshTriggerList();
      break;
    case 5: // Extra
      break;
    default:
      break;
    }
  }

  @Override
  public boolean onBeforeTabSelected(SourcesTabEvents p_sender, int p_tabIndex)
  {
    switch( p_tabIndex )
    {
    case 0: // general
    case 1: // map
      // allows click
      return true;
    case 2: // tokens
    case 3: // forces
    case 4: // triggers
    case 5: // Extra
    default:
      if( ModelFmpMain.model().iAmAdmin() )
      {
        return true;
      }
      Window.alert( "Vous n'avez pas les droits pour cet onglet" );
      return false;
    }
  }


}
