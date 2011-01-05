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
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 *
 */

public class WgtBoardLayerToken extends WgtBoardLayerBase implements LoadHandler
{
  private HashMap<EbToken, TokenWidget> m_tokenMap = new HashMap<EbToken, TokenWidget>();

  /**
   * contain all loaded color. it's used to display the loading picture if at least 
   * one image isn't loaded yet.
   */
  // private Set m_loadedColor = new HashSet();
  /**
   * 
   */
  public WgtBoardLayerToken()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#redraw(int, int, int, int)
   */
  @Override
  public void redraw()
  {
    super.redraw();

    EbGame game = ModelFmpMain.model().getGame();
    Set<EbToken> tokenList = game.getSetToken();

    // little optimisation to avoid using isHexVisible for each token...
    AnPair pixPositionLeftTop = new AnPair( m_left, m_top );
    AnPair pixPositionRightBotom = new AnPair( m_right, m_botom );
    AnPair hexPositionLeftTop = convertPixPositionToHexPosition( pixPositionLeftTop );
    hexPositionLeftTop.setX( hexPositionLeftTop.getX() - 2 );
    hexPositionLeftTop.setY( hexPositionLeftTop.getY() - 2 );
    AnPair hexPositionRightBotom = convertPixPositionToHexPosition( pixPositionRightBotom );
    hexPositionRightBotom.setX( hexPositionRightBotom.getX() + 2 );
    hexPositionRightBotom.setY( hexPositionRightBotom.getY() + 2 );

    for( EbToken token : tokenList )
    {
      if( token.getLocation() != Location.Board )
      {
        // not visible token, but it may still need an update
        updateTokenWidget( token, false );
      }
      else if( (token.getPosition().getX() > hexPositionLeftTop.getX())
          && (token.getPosition().getY() > hexPositionLeftTop.getY())
          && (token.getPosition().getX() < hexPositionRightBotom.getX())
          && (token.getPosition().getY() < hexPositionRightBotom.getY()) )
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
    for( EbToken token : m_tokenMap.keySet() )
    {
      if( token.getGame() == null )
      {
        // token was removed from the game, get rid of it
        TokenWidget tokenWidget = m_tokenMap.get( token );
        tokenWidget.setVisible( false );
        remove( tokenWidget.getTokenImage() );
        remove( tokenWidget.getIconWarningImage() );
        m_tokenMap.remove( token );
      }
    }
  }


  /**
   * return the timed html corresponding to any given token.
   * If the widget isn't found in cache (ie in m_tokenMap) or if the cache is too old,
   * it create and add it.
   * @param p_token
   * @return
   */
  private void updateTokenWidget(EbToken p_token, boolean p_force)
  {
    assert p_token != null;
    TokenWidget tokenWidget = (TokenWidget)m_tokenMap.get( p_token );
    if( p_token.getLocation() != Location.Board )
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
      tokenWidget = new TokenWidget();
      m_tokenMap.put( p_token, tokenWidget );
    }
    if( (tokenWidget.isUpdateRequired( p_token )) || (p_force == true) )
    {
      // update is needed !

      // token is on board: display it !
      EbGame game = ModelFmpMain.model().getGame();
      add( tokenWidget.getTokenImage() );
      tokenWidget.getTokenImage().setVisible( true );
      int landPixOffset = 0;
      if( getZoom().getValue() == EnuZoom.Medium )
      {
        landPixOffset = p_token.getLandPixOffset();
      }
      TokenImages.getTokenImage( p_token, getZoom().getValue() ).applyTo(
          tokenWidget.getTokenImage() );

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

      EbToken nearTank = null;

      // if token is under opponents fire cover, display a warning icon
      if( p_token.isFireDisabled() )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.disable_fire( getZoom()
            .getValue() ), p_token, landPixOffset );
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
      if( ((nearTank = game.getTankCheating( p_token )) != null)
          && (nearTank.getId() > p_token.getId()) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.warning( getZoom()
              .getValue() ), p_token, landPixOffset );
      }
      else
      // display a load count image ?
      if( (p_token.getType() != TokenType.Ore)
          && (p_token.getType() != TokenType.Freighter) && (p_token.getType() != TokenType.Turret)
          && (p_token.getType() != TokenType.Pontoon) && (p_token.getSetContain().size() > 0) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.iconLoad( getZoom().getValue(),
            p_token.getContainSize() ), p_token, landPixOffset );
      }
      else
      // display a bullet count image ?
      if( (p_token.getType() != TokenType.Ore)
          && (p_token.getType() != TokenType.Freighter) && (p_token.getType() != TokenType.Turret)
          && (p_token.getType() != TokenType.Pontoon) && (p_token.getBulletCount() > 0) )
      {
        addWarningImage( tokenWidget.getIconWarningImage(), BoardIcons.iconBullet( getZoom().getValue(),
            p_token.getBulletCount() ), p_token, landPixOffset );
      }
      else
      {
        remove( tokenWidget.getIconWarningImage() );
      }
      // last touch...
      tokenWidget.setLastTokenDrawn( p_token );
    }
  }

  private void addWarningImage(Image p_image, AbstractImagePrototype p_absImage, EbToken p_token,
      int p_landPixOffset)
  {
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
    EbGame game = ModelFmpMain.model().getGame();
    int pxW = game.getLandPixWidth( getZoom() );
    int pxH = game.getLandPixHeight( getZoom() );
    setPixelSize( pxW, pxH );
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
    EbGame game = ModelFmpMain.model().getGame();
    if( m_lastGameId != game.getId() || p_forceRedraw )
    {
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_lastTideValue = game.getCurrentTide();
      m_lastGameId = game.getId();
      int pxW = game.getLandPixWidth( getZoom() );
      int pxH = game.getLandPixHeight( getZoom() );
      setPixelSize( pxW, pxH );
      clearTokenMap();
      redraw();
    }
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
