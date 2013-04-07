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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.fullmetalgalaxy.model.GameEventStack;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.MapSize;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * @author Kroc
 *
 */

public class MAppGameCreation extends GuiEntryPoint implements ClickHandler,
    ModelUpdateEvent.Handler
{
  public static final String HISTORY_ID = "new";

  public static final String s_TokenIdGame = "idGame";


  public static MessagesAppGameCreation s_messages = (MessagesAppGameCreation)GWT
      .create( MessagesAppGameCreation.class );

  // model controller
  private boolean m_isLandGenerated = false;
  private boolean m_isOreGenerated = false;

  // UI
  private FlowPanel m_panel = new FlowPanel();
  private TabPanel m_tabPanel = new TabPanel();
  private WgtGeneralInfo m_simpleForm = new WgtGeneralInfo();
  private WgtEditLand m_wgtEditLand = new WgtEditLand();
  private WgtEditTokens m_wgtEditTokens = new WgtEditTokens();
  private WgtEditReserve m_wgtEditReserve = new WgtEditReserve();
  private WgtEditAdmin m_wgtEditAdmin = new WgtEditAdmin();
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
    m_tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>()
    {
      @Override
      public void onBeforeSelection(BeforeSelectionEvent<Integer> p_event)
      {
        if( p_event.getItem() == 4 // tab admin
            && !AppMain.instance().iAmAdmin() )
        {
          Window.alert( "Vous n'avez pas les droits pour cet onglet" );
            p_event.cancel();
        }
      }
    } );
    m_tabPanel.addSelectionHandler( new SelectionHandler<Integer>()
    {
      @Override
      public void onSelection(SelectionEvent<Integer> p_event)
      {
     // Let the user know what they just did.
        switch( p_event.getSelectedItem() )
        {
        case 0:
          break;
        case 1: // map
          m_wgtEditLand.setPixelSize( m_tabPanel.getOffsetWidth(), m_tabPanel.getOffsetHeight() - 20 );
          if( m_isLandGenerated == false )
          {
            GameGenerator.generLands();
            m_isLandGenerated = true;
          }
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
          break;
        case 2: // tokens
          m_wgtEditTokens.setPixelSize( m_tabPanel.getOffsetWidth(), m_tabPanel.getOffsetHeight() - 20 );
          if( m_isOreGenerated == false )
          {
            if( m_isLandGenerated == false )
            {
              GameGenerator.generLands();
              m_isLandGenerated = true;
            }
            GameGenerator.populateOres();
            m_isOreGenerated = true;
          }
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
          break;
        case 3: // réserve
          m_wgtEditReserve.onTabSelected();
          break;
        case 4: // Extra
          m_wgtEditAdmin.onTabSelected();
          break;
        default:
          break;
        }
      }
    });

    m_tabPanel.add( m_wgtEditLand, s_messages.map() );
    m_tabPanel.add( m_wgtEditTokens, s_messages.tokens() );
    m_tabPanel.add( m_wgtEditReserve, s_messages.reserve() );
    m_tabPanel.add( m_wgtEditAdmin, "Admin" );

    m_panel.setSize( "100%", "100%" );
    m_panel.add( m_tabPanel );
    m_panel.add( m_btnCreateGame );
    m_panel.add( m_btnCancel );

    initWidget( m_panel );

    AppMain.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );
  }

  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  public void createGame()
  {
    Game game = GameEngine.model().getGame();
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

    game.setAccountCreator( AppMain.instance().getMyAccount() );

    
    if( GameEngine.model().getGame().isTrancient() )
    {
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
    }

    // (3) Create an asynchronous callback to handle the result.
    FmpCallback<EbBase> callback = new FmpCallback<EbBase>()
    {
      @Override
      public void onSuccess(EbBase p_result)
      {
        super.onSuccess( p_result );
        // load newly created game to show it
        GameEngine.model().getGame().updateFrom( p_result );
        GameEngine.model().getActionBuilder().setGame( GameEngine.model().getGame() );
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
    AppMain.getRpcService().saveGame( GameEngine.model().getGame(), callback );

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
        GameEngine.model().getGame().updateFrom( p_result );
        GameEngine.model().getActionBuilder().setGame( GameEngine.model().getGame() );
        ClientUtil.gotoUrl( "/game.jsp?id="+ p_result.getId() );
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
    Game game = GameEngine.model().getGame();
    GameEventStack stack = game.getGameEventStack();
    game.setGameEventStack( game ); // as stack may be client specific class
    AppMain.getRpcService().saveGame( GameEngine.model().getGame(), comment, callback );
    game.setGameEventStack( stack );
  }

  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnCreateGame )
    {
      AppMain.instance().startLoading();
      if( GameEngine.model().getGame().isTrancient() )
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
      GameEngine.model().getGame().reinit();
      History.back();
    }
  }



  /**
   * initialize game to defaults parameters
   */
  private void initNewGame()
  {
    Game game = GameEngine.model().getGame();
    game.setAccountCreator( AppMain.instance().getMyAccount() );

    game.setConfigGameTime( ConfigGameTime.Standard );
    game.setMaxNumberOfPlayer( 4 );
    GameGenerator.setSize( MapSize.Medium );
    GameGenerator.clearLand( LandType.Plain );

    m_isLandGenerated = false;
    m_isOreGenerated = false;
  }




  @Override
  public void onModelUpdate(GameEngine p_modelSender)
  {
    if( !GameEngine.model().isLogged() )
    {
      Window.alert( "Pour éditer une partie vous devez etre loggé" );
      ClientUtil.gotoUrl( "/" );
      return;
    }

    if( !GameEngine.model().getGame().isTrancient() )
    {
      m_isLandGenerated = true;
      m_isOreGenerated = true;
    }
    // TODO check we don't have to do anything
    /*
    if( !AppRoot.instance().getHistoryState().containsKey( s_TokenIdGame ) 
        || AppRoot.instance().getHistoryState().getLong( s_TokenIdGame ) == 0 )
    {
      // create a new game
      p_modelSender.reinitGame();
      initNewGame();
    }
    else
    {
      // game is loaded... nothing to do !
    }*/
  }


}
