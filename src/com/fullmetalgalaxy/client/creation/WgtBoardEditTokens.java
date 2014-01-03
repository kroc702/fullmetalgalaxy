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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.BoardConvert;
import com.fullmetalgalaxy.client.game.board.BoardLayerCollection;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerLand;
import com.fullmetalgalaxy.client.game.board.layertoken.WgtBoardLayerToken;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardEditTokens extends FocusPanel implements MouseListener, ScrollListener
{
  // ui
  AbsolutePanel m_panel = new AbsolutePanel();

  WgtBoardLayerLand m_layerLand = new WgtBoardLayerLand();
  WgtBoardLayerToken m_layerToken = new WgtBoardLayerToken();

  BoardLayerCollection m_layerCollection = new BoardLayerCollection();

  // model
  private EnuColor m_color = new EnuColor( EnuColor.None );
  private TokenType m_tokenType = TokenType.Ore;
  private Sector m_sector = Sector.North;

  /**
   * 
   */
  public WgtBoardEditTokens()
  {
    super();
    m_layerCollection.addLayer( m_layerLand );
    m_layerCollection.addLayer( m_layerToken );

    m_panel.add( m_layerCollection, 0, 0 );

    m_layerCollection.setZoom( getZoom() );
    addMouseListener( this );
    setWidget( m_panel );
  }



  protected EnuZoom getZoom()
  {
    return GameEngine.model().getZoomDisplayed();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseDown(Widget p_sender, int p_x, int p_y)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseEnter(Widget p_sender)
  {
    // m_layerSelect.setHexagonHightVisible( true );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseLeave(Widget p_sender)
  {
    // m_layerSelect.setHexagonHightVisible( false );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoard#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(Widget p_sender, int p_x, int p_y)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    AnBoardPosition position = BoardConvert.convertPixPositionToHexPosition( new AnPair( p_x,
        p_y ), getZoom(), new AnPair(0,0) );
    Game game = GameEngine.model().getGame();

    if( DOM.eventGetButton( DOM.eventGetCurrentEvent() ) == Event.BUTTON_LEFT )
    {
      EbToken oldToken = game.getToken( position );

      EbToken token = new EbToken();
      token.setType( getTokenType() );
      token.setEnuColor( getColor() );
      token.setBulletCount( token.getType().getMaxBulletCount() );
      game.addToken( token );
      position.setSector( getSector() );

      if( oldToken != null )
      {
        try
        {
          if( token.getType() == TokenType.Turret && oldToken.getType() == TokenType.Freighter )
          {
            token.setColor( oldToken.getColor() );
            game.moveToken( token, position );
          }
          else if( game.canTokenLoad( oldToken, token ) )
          {
            if( oldToken.getType() == TokenType.Pontoon )
            {
              game.moveToken( token, position );
            }
            else
            {
              game.moveToken( token, oldToken );
            }
          }
          else if( token.getType() != TokenType.Turret )
          {
            game.moveToken( oldToken, Location.Graveyard );
            game.moveToken( token, position );
          }
        } catch( RpcFmpException e )
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      else if( token.getType() != TokenType.Turret )
      {
        game.moveToken( token, position );
      }
    }
    else
    {
      try
      {
        EbToken token = game.getToken( position );
        if( token != null )
        {
          game.moveToken( token, Location.Graveyard );
        }
      } catch( RpcFmpException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ScrollListener#onScroll(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onScroll(Widget p_widget, int p_scrollLeft, int p_scrollTop)
  {
    m_layerCollection.redraw( p_scrollLeft, p_scrollTop, p_scrollLeft + p_widget.getOffsetWidth(),
        p_scrollTop + p_widget.getOffsetHeight() );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseMove(Widget p_sender, int p_x, int p_y)
  {
    // nothing to do
  }


  private int m_oldHashLand = 0;

  private int hashLand(Game p_game)
  {
    int hash = 0;
    for( byte b : p_game.getLands() )
    {
      hash += b;
    }
    hash += p_game.getPlanetType().ordinal();
    return hash;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoard#notifyModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  public void notifyModelUpdate(GameEngine p_ctrModelSender)
  {
    if( hashLand( GameEngine.model().getGame() ) != m_oldHashLand )
    {
      // land change: redraw all
      m_oldHashLand = hashLand( GameEngine.model().getGame() );
      // force redraw
      m_layerLand.onModelChange( true );
      m_layerToken.onModelChange( true );
    }
    else
    {
      m_layerToken.onModelChange();
    }
  }

  public void onTabSelected()
  {
    m_layerCollection.show();
    m_layerCollection.cropDisplay( 0, 0, GameEngine.game().getLandWidth(), GameEngine.game()
        .getLandHeight() );
    // FF fix.
    m_panel.setPixelSize( m_layerCollection.asWidget().getOffsetWidth(), 
        m_layerCollection.asWidget().getOffsetHeight() );
  }
  
  /**
   * @return the color
   */
  public EnuColor getColor()
  {
    return m_color;
  }

  /**
   * @param p_color the color to set
   */
  public void setColor(EnuColor p_color)
  {
    m_color = p_color;
  }

  /**
   * @return the tokenType
   */
  public TokenType getTokenType()
  {
    return m_tokenType;
  }

  /**
   * @param p_tokenType the tokenType to set
   */
  public void setTokenType(TokenType p_tokenType)
  {
    m_tokenType = p_tokenType;
  }

  /**
   * @return the sector
   */
  public Sector getSector()
  {
    return m_sector;
  }

  /**
   * @param p_sector the sector to set
   */
  public void setSector(Sector p_sector)
  {
    m_sector = p_sector;
  }


}
