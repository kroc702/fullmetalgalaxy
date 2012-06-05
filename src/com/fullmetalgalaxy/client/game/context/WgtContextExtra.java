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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kroc
 * display information about the current action.
 */

public class WgtContextExtra extends WgtView implements ClickHandler
{
  // UI
  private FocusPanel m_focusPanel = new FocusPanel();
  private VerticalPanel m_vPanel = new VerticalPanel();
  private HorizontalPanel m_hPanel = new HorizontalPanel();
  private HTML m_lblTitle = new HTML();

  // ref on model
  Map<Widget, EbToken> m_wgtTokenLink = new HashMap<Widget, EbToken>();

  /**
   * 
   */
  public WgtContextExtra()
  {
    super();

    m_lblTitle.setStyleName( "fmp-context-extra-title" );
    m_vPanel.add( m_lblTitle );
    m_vPanel.add( m_hPanel );

    initWidget( m_focusPanel );


    // subscribe all needed models update event
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );

    // Give the overall composite a style name.
    setStyleName( "WgtActionInfo" );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    try
    {
      EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
      EbToken token = m_wgtTokenLink.get( p_event.getSource() );

      assert token != null;

      actionBuilder.userTokenClick( token );
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( e.getLocalizedMessage() );
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  protected void redraw()
  {
    m_hPanel.clear();
    m_focusPanel.clear();
    m_wgtTokenLink.clear();
    GameEngine model = GameEngine.model();
    if( (model == null) )
    {
      return;
    }
    if( GameEngine.model().getGame().isFinished() )
    {
      return;
    }
    EventsPlayBuilder action = model.getActionBuilder();
    EbToken mainToken = action.getSelectedToken();

    if( (!action.isBoardTokenSelected()) && (!action.isActionsPending())
        && (action.getSelectedAction() == null) )
    {
      // so, no token is selected: find ship in orbit !
      Game game = model.getGame();
      if( model.getGame().getGameType() == GameType.Puzzle
          || game.getCurrentTimeStep() <= game.getEbConfigGameTime()
              .getDeploymentTimeStep() || game.getStatus() == GameStatus.Open
          || game.getStatus() == GameStatus.Pause )
      {
        Set<EbToken> list = GameEngine.model().getGame().getSetToken();
        boolean isTitleDisplayed = false;
        for( Iterator<com.fullmetalgalaxy.model.persist.EbToken> it = list.iterator(); it.hasNext(); )
        {
          EbToken token = (EbToken)it.next();
  
          if( token.getLocation() == Location.Orbit
              && GameEngine.model().getGame().getRegistrationByColor( token.getColor() ).haveAccount() )
          {
            if( !isTitleDisplayed )
            {
              isTitleDisplayed = true;
              m_lblTitle.setText( MAppBoard.s_messages.inOrbit() );
            }
            // this token is in orbit !
            // and an account is associated with it
            addToken( token );
          }
        }
      }
    }
    else if( action.getSelectedAction() != null
        && (action.getSelectedAction().getType() == GameLogType.EvtLand || action
            .getSelectedAction().getType() == GameLogType.EvtDeployment) )
    {
      assert action.getSelectedToken() != null;
      EbToken token = (EbToken)action.getSelectedToken();
      if( action.getSelectedAction().getType() == GameLogType.EvtLand )
      {
        m_lblTitle.setHTML( MAppBoard.s_messages.landing() );
      }
      else
      {
        m_lblTitle.setHTML( MAppBoard.s_messages.deployment( Messages.getTokenString( 0,
            token.getType() ) ) );
      }
      addToken( token, token.getPosition().getSector() );
    }
    else if( action.isBoardTokenSelected() && mainToken.containToken() )
    {
      m_lblTitle.setText( MAppBoard.s_messages.contain() );

      // Add list of token contained by the selected token
      // and won't be unload during the preparing action
      for( EbToken token : mainToken.getContains() )
      {
        if( !action.containUnload( token )
            && (!token.getType().isOre() || mainToken.getType() != TokenType.Freighter) )
        {
          addToken( token );
        }
      }

      if( (mainToken.getType() == TokenType.WeatherHen)
          && !(action.getSelectedAction() instanceof EbEvtConstruct) )
      {
        // Add list of token that can be constructed
        //
        EbToken ore = mainToken.getCopyContains().iterator().next();
        m_lblTitle.setText( MAppBoard.s_messages.construct() );

        EbConfigGameVariant variant = model.getGame().getEbConfigGameVariant();
        for( Entry<TokenType, Integer> entry : variant.getConstructReserve().entrySet() )
        {
          if( variant.canConstruct( entry.getKey() ) )
          {
            EbToken fakeToken = new EbToken( entry.getKey() );
            fakeToken.setId( ore.getId() );
            if( entry.getKey().canBeColored(  ) )
            {
              fakeToken.setColor( mainToken.getColor() );
            }
            fakeToken.setVersion( ore.getVersion() );
            fakeToken.setLocation( Location.ToBeConstructed );
            fakeToken.setCarrierToken( mainToken );
            addToken( fakeToken );
          }
        }
      }
    }
    if( m_hPanel.getWidgetCount() > 0 )
    {
      m_focusPanel.add( m_vPanel );
    }
  }

  private void addToken(EbToken p_token)
  {
    addToken( p_token, Sector.North );
  }

  private void addToken(EbToken p_token, Sector p_sectorValue)
  {
    FlowPanel panelToken = new FlowPanel();
    Image wgtToken = new Image();
    HTML label = new HTML( "" );
    if( p_token.getType() == TokenType.Freighter )
    {
      EbRegistration registration = GameEngine.model().getGame().getRegistrationByColor(
          p_token.getColor() );
      if( registration.haveAccount() )
      {
        label.setHTML( registration.getAccount().getPseudo() );
      }
      else
      {
        label.setHTML( "???" );
      }
    }
    else
    {
      String lblStr = Messages.getTokenString( 0, p_token.getType() );
      if( (p_token.getType().getMaxBulletCount() > 0) && (p_token.getBulletCount() != p_token.getType().getMaxBulletCount()))
      {
        if( p_token.getType().getMaxBulletCount() - p_token.getBulletCount() >= 2 )
          lblStr += "<br/>xx";
        else
          lblStr += "<br/>x";
      }
      label.setHTML( lblStr );
    }
    TokenImages.getTokenImage( p_token.getEnuColor(), EnuZoom.Small, p_token.getType(),
        p_sectorValue ).applyTo( wgtToken );
    wgtToken.addClickHandler( this );
    wgtToken.setTitle( Messages.getTokenString( 0, p_token ) );
    m_wgtTokenLink.put( wgtToken, p_token );
    panelToken.add( wgtToken );
    panelToken.add( label );
    m_hPanel.add( panelToken );
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(GameEngine p_ModelSender)
  {
    // TODO optimisation: redraw only if required
    redraw();
  }


}
