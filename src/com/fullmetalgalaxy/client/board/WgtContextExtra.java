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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kroc
 * display information about the current action.
 */

public class WgtContextExtra extends WgtView implements ClickHandler
{
  // UI
  FocusPanel m_focusPanel = new FocusPanel();
  Panel m_panel = new HorizontalPanel();

  // ref on model
  Map<Widget, EbToken> m_wgtTokenLink = new HashMap<Widget, EbToken>();

  /**
   * 
   */
  public WgtContextExtra()
  {
    super();

    m_focusPanel.add( m_panel );
    initWidget( m_focusPanel );


    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

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
      EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
      EbToken token = m_wgtTokenLink.get( p_event.getSource() );

      assert token != null;

      actionBuilder.userTokenClick( token );
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( Messages.getString( e ) );
    }
    ModelFmpMain.model().notifyModelUpdate();
  }

  protected void redraw()
  {
    m_panel.clear();
    m_wgtTokenLink.clear();
    ModelFmpMain model = ModelFmpMain.model();
    if( (model == null) )
    {
      return;
    }
    if( ModelFmpMain.model().getGame().isFinished() )
    {
      return;
    }

    EventsPlayBuilder action = ModelFmpMain.model().getActionBuilder();
    EbToken mainToken = action.getSelectedToken();

    if( (!action.isBoardTokenSelected()) && (!action.isActionsPending())
        && (action.getSelectedAction() == null) )
    {
      // so, no token is selected: find ship in orbit !
      Set<EbToken> list = ModelFmpMain.model().getGame().getSetToken();
      boolean isTitleDisplayed = false;
      for( Iterator<com.fullmetalgalaxy.model.persist.EbToken> it = list.iterator(); it.hasNext(); )
      {
        EbToken token = (EbToken)it.next();
        if( token.getLocation() == Location.Orbit )
        {
          if( !isTitleDisplayed )
          {
            isTitleDisplayed = true;
            m_panel.add( new Label( MAppBoard.s_messages.inOrbit() ) );
          }
          // this token is in orbit !
          addToken( token );
        }
      }
    }
    else if( action.getSelectedAction() instanceof EbEvtLand )
    {
      m_panel.add( new HTML( MAppBoard.s_messages.landing() ) );
      assert action.getSelectedToken() != null;
      EbToken token = (EbToken)action.getSelectedToken();
      addToken( token, token.getPosition().getSector() );
    }
    else if( action.isBoardTokenSelected() )
    {
      if( !mainToken.getSetContain().isEmpty() )
      {
        m_panel.add( new Label( MAppBoard.s_messages.contain() ) );
      }
      // Add list of token contained by the selected token 
      // and won't be unload during the preparing action
      for( EbToken token : mainToken.getSetContain() )
      {
        if( !action.containUnload( token )
            && (token.getType() != TokenType.Ore || mainToken.getType() != TokenType.Freighter) )
        {
          addToken( token );
        }
      }
      if( (mainToken.getType() == TokenType.WeatherHen) && (!mainToken.getSetContain().isEmpty())
          && !(action.getSelectedAction() instanceof EbEvtConstruct) )
      {
        // Add list of token that can be constructed
        //
        EbToken ore = mainToken.getSetContain().iterator().next();
        m_panel.add( new Label( MAppBoard.s_messages.construct() ) );

        EbConfigGameVariant variant = mainToken.getGame().getEbConfigGameVariant();
        for(Entry<TokenType,Integer> entry : variant.getConstructReserve().entrySet() )
        {
          if( variant.canConstruct( entry.getKey() ) )
          {
            EbToken fakeToken = new EbToken( entry.getKey() );
            fakeToken.setId( ore.getId() );
            if( EbToken.canBeColored( entry.getKey()) )
            {
              fakeToken.setColor( mainToken.getColor() );
            }
            fakeToken.setVersion( ore.getVersion() );
            fakeToken.setLocation( Location.ToBeConstructed );
            fakeToken.setGame( mainToken.getGame() );
            fakeToken.setCarrierToken( mainToken );
            addToken( fakeToken );
          }
        }
        
      }
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
    Label label = new Label( "" );
    if( p_token.getType() == TokenType.Freighter )
    {
      EbRegistration registration = ModelFmpMain.model().getGame().getRegistrationByColor(
          p_token.getColor() );
      if( registration.haveAccount() )
      {
        label.setText( ModelFmpMain.model().getAccount( registration.getAccountId() ).getPseudo() );
      }
      else
      {
        label.setText( "unknown" );
      }
    }
    else
    {
      label.setText( Messages.getTokenString( p_token.getType() ) );
    }
    TokenImages.getTokenImage( p_token.getEnuColor(), EnuZoom.Small, p_token.getType(),
        p_sectorValue ).applyTo( wgtToken );
    wgtToken.addClickHandler( this );
    wgtToken.setTitle( Messages.getTokenString( p_token ) );
    m_wgtTokenLink.put( wgtToken, p_token );
    panelToken.add( wgtToken );
    panelToken.add( label );
    m_panel.add( panelToken );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  @Override
  public void notifyHmiUpdate()
  {
    redraw();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    // TODO optimisation: redraw only if required
    redraw();
  }


}
