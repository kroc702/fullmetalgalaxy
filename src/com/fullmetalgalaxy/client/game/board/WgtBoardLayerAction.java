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
package com.fullmetalgalaxy.client.game.board;

import java.util.ArrayList;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtConstruct;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtDeployment;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtUnLoad;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerAction extends WgtBoardLayerBase
{
  protected ImagePool m_images = new ImagePool( this );

  /**
   * last update of the currently displayed action
   */
  protected long m_actionLastUpdate = 0;


  /**
   * 
   */
  public WgtBoardLayerAction()
  {
    super();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    EventsPlayBuilder action = GameEngine.model().getActionBuilder();
    if( action.getLastUpdate().getTime() != m_actionLastUpdate || p_forceRedraw )
    {
      redrawAction();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    redrawAction();
  }


  private void drawImage(AbstractImagePrototype p_image, AnBoardPosition p_position)
  {
    Image image = m_images.getNextImage();
    p_image.applyTo( image );
    DOM.setStyleAttribute( image.getElement(), "zIndex", "1000" );
    image.removeStyleName( "transparent50" );
    setWidgetHexPosition( image, p_position );
  }

  private void drawTransparentImage(AbstractImagePrototype p_image, AnBoardPosition p_position)
  {
    Image image = m_images.getNextImage();
    p_image.applyTo( image );
    DOM.setStyleAttribute( image.getElement(), "zIndex", "1000" );
    image.addStyleName( "transparent50" );
    setWidgetHexPosition( image, p_position );
  }

  

  private void drawFoot(AnBoardPosition p_position)
  {
    drawImage( BoardIcons.arrow( getZoom().getValue(), p_position.getSector() ), p_position );
  }

  private void drawTransparentToken(TokenType p_type, EnuColor p_color, AnBoardPosition p_position)
  {
    AbstractImagePrototype image = null;
    if( p_type.canBeColored(  ) )
    {
      image = AbstractImagePrototype.create( TokenImages.getTokenImage( p_color, getZoom()
          .getValue(), p_type, p_position.getSector() ) );
    } else {
      image = AbstractImagePrototype.create( TokenImages.getTokenImage(
          new EnuColor( EnuColor.None ), getZoom().getValue(), p_type, p_position.getSector() ) );
    }
    drawTransparentImage( image, p_position );
  }

  private void drawTransparentToken(EbToken p_token, AnBoardPosition p_position)
  {
    drawTransparentToken( p_token.getType(), p_token.getEnuColor(), p_position );
  }

  private void drawFireAction(EbEvtFire p_fireAction)
  {
    assert p_fireAction != null;
    if( p_fireAction.getTokenDestroyer2( GameEngine.model().getGame() ) != null )
    {
      Image image = m_images.getNextImage();
      BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( image );
      DOM.setStyleAttribute( image.getElement(), "zIndex", "1" );
      setWidgetHexPosition( image, p_fireAction.getTokenDestroyer2( GameEngine.model().getGame() )
          .getPosition() );
    }
    if( p_fireAction.getTokenTarget( GameEngine.model().getGame() ) != null )
    {
      Image image = m_images.getNextImage();
      BoardIcons.target( getZoom().getValue() ).applyTo( image );
      DOM.setStyleAttribute( image.getElement(), "zIndex", "1000" );
      setWidgetHexPosition( image, p_fireAction.getTokenTarget( GameEngine.model().getGame() ).getPosition() );
    }
  }

  private void drawControlAction(EbEvtControl p_fireAction)
  {
    assert p_fireAction != null;
    if( p_fireAction.getTokenDestroyer2( GameEngine.model().getGame() ) != null )
    {
      Image image = m_images.getNextImage();
      BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( image );
      DOM.setStyleAttribute( image.getElement(), "zIndex", "1" );
      setWidgetHexPosition( image, p_fireAction.getTokenDestroyer2( GameEngine.model().getGame() )
          .getPosition() );
    }
    if( p_fireAction.getTokenTarget( GameEngine.model().getGame() ) != null )
    {
      Image image = m_images.getNextImage();
      BoardIcons.target_control( getZoom().getValue() ).applyTo( image );
      DOM.setStyleAttribute( image.getElement(), "zIndex", "1000" );
      setWidgetHexPosition( image, p_fireAction.getTokenTarget( GameEngine.model().getGame() ).getPosition() );
    }
  }

  /**
   * redraw the full action layer.  
   */
  protected void redrawAction()
  {
    m_actionLastUpdate = GameEngine.model().getActionBuilder().getLastUpdate().getTime();
    ArrayList<AnEventPlay> actionList = GameEngine.model().getActionList();
    m_images.resetImageIndex();
    int actionCount = actionList.size();
    int index = 0;
    AnEvent firstAction = null;
    if( actionCount > 0 )
    {
      firstAction = actionList.get( 0 );
    }
    while( index < actionCount )
    {
      AnEvent action = actionList.get( index );
      AnEvent nextAction = GameEngine.model().getActionBuilder().getAction( index + 1 );
      AnEvent previousAction = GameEngine.model().getActionBuilder().getAction( index - 1 );
      if( action.getType() == GameLogType.EvtMove )
      {
        if( (nextAction == null)
            || ((nextAction.getType() == GameLogType.EvtLoad) 
                && (((EbEvtLoad)nextAction).getToken(  GameEngine.model().getGame() ).getType().isOre())
                && (firstAction != null)
                && (firstAction.getType() != GameLogType.EvtConstruct) ) )
        {
          if( (firstAction != null) && (firstAction.getType() == GameLogType.EvtConstruct) )
          {
            drawTransparentToken( ((EbEvtConstruct)firstAction).getConstructType(),
                  ((EbEvtConstruct)firstAction).getTokenCarrier( GameEngine.model().getGame() )
                      .getEnuColor(),
                  ((EbEvtMove)action).getNewPosition() );
          }
          else
          {
            drawTransparentToken( ((EbEvtMove)action).getToken( GameEngine.model().getGame() ),
                ((EbEvtMove)action)
                .getNewPosition() );
          }
        }
        else
        {
          // as barge as two hexagons, one foot shouldn't be displayed
          if( GameEngine.model().getActionBuilder().getSelectedToken().getHexagonSize() == 2 )
          {
            if( GameEngine.model().getActionBuilder().getSelectedToken().getPosition().equals(
                GameEngine.model().getActionBuilder().getSelectedPosition() ) )
            {
              // barge was selected by tail
              AnEvent nextNextAction = GameEngine.model().getActionBuilder().getAction(
                  index + 2 );
              if( (nextNextAction != null) && !(nextNextAction.getType() == GameLogType.EvtLoad) )
              {
                drawFoot( ((EbEvtMove)action).getNewPosition() );
              }
            }
            else
            {
              // barge was selected by head
              if( (previousAction != null) && !(previousAction.getType() == GameLogType.EvtUnLoad) )
              {
                drawFoot( ((EbEvtMove)action).getNewPosition() );
              }
            }
          }
          else
          {
            drawFoot( ((EbEvtMove)action).getNewPosition() );
          }
        }
      }
      else if( action.getType() == GameLogType.EvtLoad )
      {
        // hardly nothing to display as the transparent token is displayed with
        // the potential next unload action.
        if( (nextAction != null) && (nextAction.getType() == GameLogType.EvtUnLoad) )
        {
          // the loaded token will be strait unloaded
          AnBoardPosition position = ((AnEventPlay)action).getTokenCarrier(
              GameEngine.model().getGame() ).getPosition().newInstance();
          position.setSector( ((AnEventPlay)action).getToken( GameEngine.model().getGame() )
              .getPosition()
              .getNeighbourSector(
              position ) );
          drawFoot( position );
        }
        else if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtMove || previousAction.getType() == GameLogType.EvtUnLoad)
            && (((AnEventPlay)action).getToken( GameEngine.model().getGame() ).getType().isOre())
            && (firstAction != null)
            && (firstAction.getType() != GameLogType.EvtConstruct) )
        {
          drawTransparentToken( ((AnEventPlay)action).getToken( GameEngine.model().getGame() ),
              ((AnEventPlay)previousAction).getNewPosition() );
        }
        else if( GameEngine.model().getActionBuilder().getSelectedAction() == null )
        {
          drawTransparentToken( ((AnEventPlay)action).getToken( GameEngine.model().getGame() ),
              ((AnEventPlay)action).getTokenCarrier( GameEngine.model().getGame() ).getPosition() );
        }

      }
      else if( action.getType() == GameLogType.EvtTransfer)
      {
        // hardly nothing to display as the transparent token is displayed with
        // the potential next unload action.
        if( (nextAction != null) && (nextAction.getType() == GameLogType.EvtUnLoad) )
        {
          // the loaded token will be strait unloaded
          AnBoardPosition position = ((AnEventPlay)action).getNewTokenCarrier(
              GameEngine.model().getGame() ).getPosition().newInstance();
          position.setSector( ((AnEventPlay)action).getTokenCarrier( GameEngine.model().getGame() ).getPosition()
              .getNeighbourSector( position ) );
          drawFoot( position );
        }
        else if( (previousAction != null) && (previousAction.getType() == GameLogType.EvtMove || previousAction.getType() == GameLogType.EvtUnLoad)
            && (((AnEventPlay)action).getToken( GameEngine.model().getGame() ).getType().isOre())
            && (firstAction != null)
            && (firstAction.getType() != GameLogType.EvtConstruct) )
        {
          drawTransparentToken( ((AnEventPlay)action).getToken( GameEngine.model().getGame() ),
              ((AnEventPlay)previousAction).getNewPosition() );
        }
        else if( GameEngine.model().getActionBuilder().getSelectedAction() == null )
        {
          drawTransparentToken( ((AnEventPlay)action).getToken( GameEngine.model().getGame() ),
              ((AnEventPlay)action).getTokenCarrier( GameEngine.model().getGame() ).getPosition() );
        }

      }
      else if( action.getType() == GameLogType.EvtUnLoad )
      {
        if( (nextAction != null) && (nextAction.getType() == GameLogType.EvtMove || nextAction.getType() == GameLogType.EvtLoad) )
        {
          drawFoot( ((EbEvtUnLoad)action).getNewPosition() );
        }
        else
        {
          if( (firstAction != null) && (firstAction.getType() == GameLogType.EvtConstruct) )
          {
            drawTransparentToken( ((EbEvtConstruct)firstAction).getConstructType(),
                  ((EbEvtConstruct)firstAction).getTokenCarrier( GameEngine.model().getGame() )
                      .getEnuColor(),
                  ((EbEvtUnLoad)action).getNewPosition() );
          }
          else
          {
            drawTransparentToken( ((EbEvtUnLoad)action).getToken( GameEngine.model().getGame() ),
                ((EbEvtUnLoad)action)
                .getNewPosition() );
          }
        }
      }
      else if( action.getType() == GameLogType.EvtFire )
      {
        drawFireAction( (EbEvtFire)action );
      }
      else if( action.getType() == GameLogType.EvtControl )
      {
        drawControlAction( (EbEvtControl)action );
      }
      index++;
    }
    if( GameEngine.model().getActionBuilder().getSelectedAction() != null )
    {
      AnEventPlay action = GameEngine.model().getActionBuilder().getSelectedAction();
      if( action instanceof EbEvtConstruct )
      {
        assert ((EbEvtConstruct)action).getTokenCarrier( GameEngine.model().getGame() ) != null;
        drawTransparentToken( ((EbEvtConstruct)action).getConstructType(),
              ((EbEvtConstruct)action).getTokenCarrier( GameEngine.model().getGame() )
                  .getEnuColor(), ((EbEvtConstruct)action).getTokenCarrier(
                  GameEngine.model().getGame() ).getPosition() );
      }
      else if( action instanceof EbEvtUnLoad )
      {
        if( ((EbEvtUnLoad)action).getToken( GameEngine.model().getGame() ) != null )
        {
          AnBoardPosition position = GameEngine.model().getActionBuilder().getSelectedPosition();
          if( ((EbEvtUnLoad)action).getNewPosition() != null )
          {
            position.setSector( position
                .getNeighbourSector( ((EbEvtUnLoad)action).getNewPosition() ) );
          }
          TokenType tokenType = action.getToken( GameEngine.model().getGame() ).getType();
          if( firstAction != null && firstAction.getType() == GameLogType.EvtConstruct)
          {
            tokenType = ((EbEvtConstruct)firstAction).getConstructType();
          }
          drawTransparentToken( tokenType,
              ((EbEvtUnLoad)action).getTokenCarrier(GameEngine.model().getGame()).getEnuColor(),
              position );
        }
      }
      else if( action instanceof EbEvtFire )
      {
        drawFireAction( (EbEvtFire)action );
      }
      else if( action instanceof EbEvtControl )
      {
        drawControlAction( (EbEvtControl)action );
      }
      else if( action instanceof EbEvtLand || action instanceof EbEvtDeployment )
      {
        if( ((AnEventPlay)action).getPosition() != null && ((AnEventPlay)action).getPosition().getX() >= 0 )
        {
          drawTransparentToken( ((AnEventPlay)action).getToken( GameEngine.model().getGame() ),
              ((AnEventPlay)action).getPosition() );
          if( action instanceof EbEvtLand )
          {
            drawTransparentImage( BoardIcons.deployment3( getZoom().getValue() ), ((AnEventPlay)action).getPosition() );
          }
        }
      }
    }
    m_images.hideOtherImage();
  }


}
