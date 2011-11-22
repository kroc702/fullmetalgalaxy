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
package com.fullmetalgalaxy.client.game.board;


import java.util.ArrayList;
import java.util.Date;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */
public class WgtPlayerInfo extends WgtView
{
  HorizontalPanel m_panel = new HorizontalPanel();
  HorizontalPanel m_panelTide = new HorizontalPanel();
  Image m_iconAction = Icons.s_instance.action16().createImage();
  HTML m_lblAction = new HTML( "&nbsp;: 0  " );
  Image m_iconOre = Icons.s_instance.ore16().createImage();
  HTML m_lblOre = new HTML( "&nbsp;: 0  " );
  Image m_iconMoon = Icons.s_instance.moon16().createImage();
  Label m_lblMoon = new HTML( "&nbsp;:&nbsp;" );

  /**
   * 
   */
  public WgtPlayerInfo()
  {
    // subscribe all needed models update event
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );

    m_panel.add( m_iconAction );
    m_iconAction.setTitle( MAppBoard.s_messages.remainingActionPoint() );
    m_panel.add( m_lblAction );
    m_lblAction.setTitle( MAppBoard.s_messages.remainingActionPoint() );
    m_panel.setCellWidth( m_lblAction, "60px" );
    m_lblAction.setStyleName( "fmp-status-text" );
    m_panel.add( m_iconOre );
    m_iconOre.setTitle( MAppBoard.s_messages.oreInHold() );
    m_panel.add( m_lblOre );
    m_lblOre.setTitle( MAppBoard.s_messages.oreInHold() );
    m_panel.setCellWidth( m_lblOre, "40px" );
    m_lblOre.setStyleName( "fmp-status-text" );

    m_panel.add( m_iconMoon );
    m_iconMoon.setTitle( MAppBoard.s_messages.tide() );
    m_panel.add( m_lblMoon );
    m_lblMoon.setTitle( MAppBoard.s_messages.tide() );
    m_lblMoon.setStyleName( "fmp-status-text" );
    m_panel.add( m_panelTide );

    // m_vPanel.setWidth( "100%" );
    // m_vPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( m_panel );
  }


  private AnEvent m_oldGameEvent = new AnEvent();

  protected void redraw()
  {
    Game game = GameEngine.model().getGame();
    AnEvent lastEvent = GameEngine.model().getCurrentAction();

    if( (lastEvent != m_oldGameEvent) )
    {
      m_oldGameEvent = lastEvent;

      // Display current tides
      m_panelTide.clear();
      Image image = BoardIcons.iconTide( game.getCurrentTide() ).createImage();
      image.setTitle( MAppBoard.s_messages.currentTide()
          + Messages.getTideString( 0, game.getCurrentTide() ) );
      m_panelTide.add( image );
      m_panelTide.setCellWidth( image, "20px" );

      if( GameEngine.model().getMyRegistration() != null )
      {
        m_lblAction.setHTML( "&nbsp;: "
            + GameEngine.model().getMyRegistration().getPtAction()
            + "/"
            + (game.getEbConfigGameVariant().getActionPtMaxReserve() + ((GameEngine.model()
                .getMyRegistration().getEnuColor().getNbColor() - 1) * game
                .getEbConfigGameVariant().getActionPtMaxPerExtraShip())) );
        if( game.isAsynchron() )
        {
          Date nextActionIncrement = game.estimateTimeStepDate( game.getCurrentTimeStep() + 1 );
          m_lblAction.setTitle( MAppBoard.s_messages.nextPA(
              EbConfigGameTime.getActionInc( game, GameEngine.model().getMyRegistration() ),
              ClientUtil.formatTimeElapsed( nextActionIncrement
              .getTime() - System.currentTimeMillis() ) ) );
        }
        m_lblOre.setHTML( "&nbsp;: " + GameEngine.model().getMyRegistration().getOreCount(game) );


        // Display current take off turn
        if( game.getAllowedTakeOffTurns().contains( game.getCurrentTimeStep() ) )
        {
          // take off is allowed : display it !
          image = Icons.s_instance.takeOff16().createImage();
          image.setTitle( "Decollage autorisé !" );
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }


        // display next tide (or no forecast)
        // ==================================

        if( GameEngine.model().getMyRegistration().getWorkingWeatherHenCount() <= 0 )
        {
          image = BoardIcons.iconTide( Tide.Unknown ).createImage();
          image.setTitle( MAppBoard.s_messages.noForecast() );
        }
        else
        {
          image = BoardIcons.iconTide( game.getNextTide() ).createImage();
          image.setTitle( MAppBoard.s_messages.nextTide()
              + Messages.getTideString( 0, game.getNextTide() ) );
        }
        if( game.getEbConfigGameTime().isAsynchron() )
        {
          image.setTitle( image.getTitle() + " - "
              + ClientUtil.formatDateTime( game.estimateNextTideChange() ) );
        }
        if( game.getNextTideChangeTimeStep() <= game.getEbConfigGameTime().getTotalTimeStep() )
        {
          // add forecast only if game isn't finished after that turn
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }
        
        if( GameEngine.model().getMyRegistration().getWorkingWeatherHenCount() >= 2
            && (game.getNextTideChangeTimeStep() + game.getEbConfigGameTime().getTideChangeFrequency() 
                  <= game.getEbConfigGameTime().getTotalTimeStep()) )
        {
          image = BoardIcons.iconTide( game.getNextTide2() ).createImage();
          image.setTitle( MAppBoard.s_messages.nextTide()
              + Messages.getTideString( 0, game.getNextTide2() ) );
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }

      }

      // display next take off
      // ===================
      ArrayList<Integer> allowedTakeOff = game.getAllowedTakeOffTurns();
      if( allowedTakeOff != null )
      {
        int index = 0;
        int currentTurn = game.getCurrentTimeStep();
        while( (index < allowedTakeOff.size())
            && (currentTurn >= allowedTakeOff.get( index ).intValue()) )
        {
          index++;
        }
        if( index < allowedTakeOff.size() )
        {
          if( allowedTakeOff.get( index ).intValue() == currentTurn + 1 )
          {
            image = Icons.s_instance.takeOff16().createImage();
          }
          else
          {
            image = Icons.s_instance.takeOffBW16().createImage();
          }
          image.setTitle( "prochain decolage : tour " + allowedTakeOff.get( index ) );
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }
      }
    }

  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(GameEngine p_ModelSender)
  {
    redraw();
  }

}
