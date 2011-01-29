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


import java.util.ArrayList;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
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
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    m_panel.add( m_iconAction );
    m_iconAction.setTitle( "Point d'action restant" );
    m_panel.add( m_lblAction );
    m_lblAction.setTitle( "Point d'action restant" );
    m_panel.setCellWidth( m_lblAction, "50px" );
    m_lblAction.setStyleName( "fmp-status-text" );
    m_panel.add( m_iconOre );
    m_iconOre.setTitle( "Minerais en soute" );
    m_panel.add( m_lblOre );
    m_lblOre.setTitle( "Minerais en soute" );
    m_panel.setCellWidth( m_lblOre, "40px" );
    m_lblOre.setStyleName( "fmp-status-text" );

    m_panel.add( m_iconMoon );
    m_iconMoon.setTitle( "Marees" );
    m_panel.add( m_lblMoon );
    m_lblMoon.setTitle( "Marees" );
    m_lblMoon.setStyleName( "fmp-status-text" );
    m_panel.add( m_panelTide );

    // m_panel.setWidth( "100%" );
    // m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( m_panel );
  }


  private AnEvent m_oldGameEvent = new AnEvent();

  protected void redraw()
  {
    EbGame game = ModelFmpMain.model().getGame();
    AnEvent lastEvent = game.getLastGameLog();

    if( (lastEvent != m_oldGameEvent) )
    {
      m_oldGameEvent = lastEvent;

      // Display current tides
      m_panelTide.clear();
      Image image = BoardIcons.iconTide( game.getCurrentTide() ).createImage();
      image.setTitle( "maree actuelle: " + Messages.getTideString( game.getCurrentTide() ) );
      m_panelTide.add( image );
      m_panelTide.setCellWidth( image, "20px" );

      if( ModelFmpMain.model().getMyRegistration() != null )
      {
        m_lblAction.setHTML( "&nbsp;: "
            + ModelFmpMain.model().getMyRegistration().getPtAction()
            + "/"
            + (game.getEbConfigGameVariant().getActionPtMaxReserve() + ((ModelFmpMain.model()
                .getMyRegistration().getEnuColor().getNbColor() - 1) * game
                .getEbConfigGameVariant().getActionPtMaxPerExtraShip())) );
        m_lblOre.setHTML( "&nbsp;: " + ModelFmpMain.model().getMyRegistration().getOreCount() );


        // Display current take off turn
        if( game.getAllowedTakeOffTurns().contains( game.getCurrentTimeStep() ) )
        {
          // take off is allowed : display it !
          image = Icons.s_instance.takeOff16().createImage();
          image.setTitle( "Decollage autorisé !" );
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }


        if( ModelFmpMain.model().getMyRegistration().getWorkingWeatherHenCount() >= 1 )
        {
          image = BoardIcons.iconTide( game.getNextTide() ).createImage();
          image.setTitle( "maree futur: " + Messages.getTideString( game.getNextTide() ) );
          if( game.getEbConfigGameTime().isAsynchron() )
          {
            image.setTitle( image.getTitle() + " - "
                + ClientUtil.formatDateTime( game.estimateNextTideChange() ) );
          }
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }

        if( ModelFmpMain.model().getMyRegistration().getWorkingWeatherHenCount() >= 2 )
        {
          image = BoardIcons.iconTide( game.getNextTide2() ).createImage();
          image.setTitle( "maree futur: " + Messages.getTideString( game.getNextTide() ) );
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
          image = Icons.s_instance.takeOff16().createImage();
          image.setTitle( "prochain decolage : tour " + allowedTakeOff.get( index ) );
          m_panelTide.add( image );
          m_panelTide.setCellWidth( image, "20px" );
        }
      }
    }

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
    redraw();
  }

}
