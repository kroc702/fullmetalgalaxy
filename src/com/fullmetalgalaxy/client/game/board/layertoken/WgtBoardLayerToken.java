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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board.layertoken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.GameActionEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerBase;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 *
 */

public class WgtBoardLayerToken extends WgtBoardLayerBase implements LoadHandler,
    GameActionEvent.Handler
{
  static
  {
    TokenImages.loadAllBundle();
  }

  private HashMap<EbToken, TokenWidget> m_tokenMap = new HashMap<EbToken, TokenWidget>();

  /**
   * 
   */
  public WgtBoardLayerToken()
  {
    AppRoot.getEventBus().addHandler( GameActionEvent.TYPE, this );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#redraw(int, int, int, int)
   */
  @Override
  public void redraw()
  {
    super.redraw();

    Game game = GameEngine.model().getGame();
    Set<EbToken> tokenList = game.getSetToken();

    for( EbToken token : tokenList )
    {
      if( token.getLocation() != Location.Board )
      {
        // not visible token, but it may still need an update
        updateTokenWidget( token, false );
      }
      else if( isHexVisible( token.getPosition() ) )
      {
        // for each visible token...
        updateTokenWidget( token, false );
      }
    }

  }

  /**
   * remove token which where removed from game.
   */
  public void cleanToken()
  {
    List<EbToken> token2Remove = new ArrayList<EbToken>();
    for( EbToken token : m_tokenMap.keySet() )
    {
      if( GameEngine.model().getGame().getToken( token.getId() ) == null )
      {
        // token was removed from the game, get rid of it
        TokenWidget tokenWidget = m_tokenMap.get( token );
        tokenWidget.setVisible( false );
        remove( tokenWidget.getTokenImage() );
        remove( tokenWidget.getIconWarningImage() );
        token2Remove.add( token );
      }
    }
    for( EbToken token : token2Remove )
    {
      m_tokenMap.remove( token );
    }
  }


  /**
   * return the timed html corresponding to any given token.
   * If the widget isn't found in cache (ie in m_tokenMap) or if the cache is too old,
   * it create and add it.
   * @param p_token
   * @return
   */
  public void updateTokenWidget(EbToken p_token, boolean p_force)
  {
    assert p_token != null;
    Game game = GameEngine.model().getGame();

    TokenWidget tokenWidget = (TokenWidget)m_tokenMap.get( p_token );
    if( (p_token.getLocation() != Location.Board && p_token.getLocation() != Location.Graveyard)
        || (p_token.getLocation() == Location.Graveyard && (p_token.getColor() == EnuColor.None || game
            .getLand( p_token.getPosition() ) == LandType.Sea)) )
    {
      if( tokenWidget != null )
      {
        // token isn't on board but have a widget: hide it !
        tokenWidget.setVisible( false );
        tokenWidget.setLastTokenDrawn( p_token );
      }
      return;
    }
    if( tokenWidget == null )
    {
      tokenWidget = getTokenWidget( p_token, true );
    }
    if( (tokenWidget.isUpdateRequired( p_token )) || (p_force == true) )
    {
      // update is needed !

      // token is on board: display it !
      add( tokenWidget.getTokenImage() );
      tokenWidget.getTokenImage().setVisible( true );
      int landPixOffset = 0;
      if( getZoom().getValue() == EnuZoom.Medium )
      {
        landPixOffset = p_token.getLandPixOffset( game );
      }
      tokenWidget.setTokenImage( TokenImages.getTokenImage( p_token, getZoom().getValue() ) );

      setWidgetHexPosition( tokenWidget.getTokenImage(), p_token.getPosition(), landPixOffset );
      DOM.setStyleAttribute( tokenWidget.getTokenImage().getElement(), "zIndex", Integer
          .toString( p_token.getZIndex() ) );

      // this is to handle the loading status
      /*if( !m_loadedColor.contains( p_token.getEnuColor() ) )
      {
        AppMain.instance().startLoading();
        m_loadedColor.add( p_token.getEnuColor() );
        tokenWidget.getTokenImage().addLoadListener( this );
      }*/

      if( p_token.getLocation() == Location.Graveyard )
      {
        // no warning to display for wreck
        remove( tokenWidget.getIconWarningImage() );
      }
      else
      // if token is under opponents fire cover, display a warning icon
      if( p_token.isFireDisabled() )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.disable_fire( getZoom()
            .getValue() ), p_token, landPixOffset );
      }
      else
      // if token disabling other token, display icon
      if( p_token.isFireDisabling() )
      {
        addWarningImage( tokenWidget.getIconWarningImage(),
            BoardIcons.disabling_fire( getZoom().getValue() ), p_token, landPixOffset );
      }
      else
      // if the token isn't active (ie under water for land unit)
      // then display a warning icon
      if( !game.isTokenTideActive( p_token ) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.disable_water( getZoom()
            .getValue() ), p_token, landPixOffset );
      }
      else
      // if two tank are neighbor on two montains, display a warning icon
      if( game.isTankCheating( p_token ) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.warning( getZoom()
              .getValue() ), p_token, landPixOffset );
      }
      else
      // display a load count image ?
      if( (!p_token.getType().isOre())
          && (p_token.getType() != TokenType.Freighter) && (p_token.getType() != TokenType.Turret)
          && (p_token.getType() != TokenType.Pontoon) && (p_token.containToken()) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.iconLoad( getZoom().getValue(),
            p_token.getContainSize() ), p_token, landPixOffset );
      }
      else
      // display a bullet count image ?
      if( (p_token.getType().getMaxBulletCount() > 0)
          && (p_token.getType() != TokenType.Turret) && (p_token.getType() != TokenType.Freighter) 
          && (p_token.getBulletCount() != p_token.getType().getMaxBulletCount()) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.iconBullet( getZoom().getValue(),
            (int)Math.ceil(p_token.getType().getMaxBulletCount() - p_token.getBulletCount()) ), p_token, landPixOffset );
      }
      else
      // display a deployment image ?
      if( (p_token.getType() == TokenType.Freighter)
          && (game.getCurrentTimeStep() <= game.getEbConfigGameTime().getDeploymentTimeStep()) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.deployment3( getZoom().getValue() ) , p_token, 0 );
      }
      else
      {
        remove( tokenWidget.getIconWarningImage() );
      }
      // last touch...
      tokenWidget.setLastTokenDrawn( p_token );
    }
  }

  protected void addWarningImage(Image p_image, ImageResource p_absImage, EbToken p_token,
      int p_landPixOffset)
  {
    addWarningImage( p_image, AbstractImagePrototype.create( p_absImage ), p_token, p_landPixOffset );
  }

  protected void addWarningImage(Image p_image, AbstractImagePrototype p_absImage, EbToken p_token,
      int p_landPixOffset)
  {
    if( p_absImage == null )
    {
      p_image.setVisible( false );
      return;
    }
    add( p_image );
    p_image.setVisible( true );
    p_absImage.applyTo( p_image );
    DOM.setStyleAttribute( p_image.getElement(), "zIndex", Integer.toString( p_token.getZIndex() ) );
    setWidgetHexPosition( p_image, p_token.getPosition(), p_landPixOffset );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.LoadHandler#onLoad(com.google.gwt.event.dom.client.LoadEvent)
   */
  @Override
  public void onLoad(LoadEvent p_event)
  {
    AppMain.instance().stopLoading();
  }

  /**
   * reset lastUpdate off all TimedHtml
   */
  private void invalidateTokenMap()
  {
    for( TokenWidget timedHtml : m_tokenMap.values() )
    {
      timedHtml.invalidate();
      remove( timedHtml.getIconWarningImage() );
      remove( timedHtml.getTokenImage() );
    }
  }

  private void clearTokenMap()
  {
    for( TokenWidget timedHtml : m_tokenMap.values() )
    {
      remove( timedHtml.getIconWarningImage() );
      remove( timedHtml.getTokenImage() );
    }
    m_tokenMap.clear();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    invalidateTokenMap();
    redraw();
  }

  private long m_tokenLastUpdate = 0;
  private Tide m_lastTideValue = Tide.Unknown;
  private long m_lastGameId = 0;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    Game game = GameEngine.model().getGame();
    if( m_lastGameId != game.getId() || p_forceRedraw )
    {
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_lastTideValue = game.getCurrentTide();
      m_lastGameId = game.getId();
      resetPixelSize();
      clearTokenMap();
      redraw();
    }
    if( m_tokenLastUpdate != game.getLastTokenUpdate().getTime() )
    {
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_lastTideValue = game.getCurrentTide();
      redraw();
    }
  }



  @Override
  public void onGameEvent(AnEvent p_event)
  {
    super.onModelChange( false );
    Game game = GameEngine.model().getGame();
    AnimEvent animation = AnimFactory.createAnimEvent( this, p_event );
    if( animation != null )
    {
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      addAnimation( animation );
    }
    else
    {
      if( m_tokenLastUpdate != game.getLastTokenUpdate().getTime() )
      {
        m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
        m_lastTideValue = game.getCurrentTide();
        redraw();
      }
      if( m_lastTideValue != game.getCurrentTide() )
      {
        m_lastTideValue = game.getCurrentTide();
        invalidateTokenMap();
        redraw();
      }
    }
  }

  public TokenWidget getTokenWidget(EbToken p_token)
  {
    return m_tokenMap.get( p_token );
  }

  public TokenWidget getTokenWidget(EbToken p_token, boolean p_newIfNeeded)
  {
    TokenWidget tokenWidget = m_tokenMap.get( p_token );
    if( tokenWidget == null && p_newIfNeeded )
    {
      tokenWidget = new TokenWidget();
      m_tokenMap.put( p_token, tokenWidget );
    }
    return tokenWidget;
  }



  private List<AnimEvent> m_animations = new LinkedList<AnimEvent>();
  private boolean isAnimationRunning = false;

  private void addAnimation(AnimEvent p_animation)
  {
    m_animations.add( p_animation );
    if( !isAnimationRunning )
    {
      isAnimationRunning = true;
      m_animations.get( 0 ).run();
    }
  }

  public void nextAnimation()
  {
    if( m_animations.isEmpty() )
    {
      return;
    }
    m_animations.remove( 0 );
    if( !m_animations.isEmpty() )
    {
      m_animations.get( 0 ).run();
    }
    else
    {
      isAnimationRunning = false;
      redraw();
    }
  }

  /**
   * 
   * @param p_token
   * @return true if given token is visible by user
   */
  public boolean isVisible(EbToken p_token)
  {
    if( p_token.getLocation() != Location.Board )
    {
      return false;
    }
    return isHexVisible( p_token.getPosition() );
  }


  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    super.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );
    invalidateTokenMap();
    redraw();
  }

}
