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


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
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
  Image m_iconAction = Icons.s_instance.action16().createImage();
  Label m_lblAction = new Label( " : 0  " );
  Image m_iconOre = Icons.s_instance.ore16().createImage();
  Label m_lblOre = new Label( " : 0  " );
  Image m_iconMoon = Icons.s_instance.moon16().createImage();
  Label m_lblMoon = new Label( " :  " );
  Image m_iconTide1 = Icons.s_instance.tide_unknown().createImage();
  Image m_iconTide2 = Icons.s_instance.tide_unknown().createImage();

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
    m_panel.setCellWidth( m_lblAction, "40px" );
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
    m_panel.add( m_iconTide1 );
    m_panel.setCellWidth( m_iconTide1, "20px" );
    m_iconTide1.setTitle( "Marees actuelle:" );
    m_panel.add( m_iconTide2 );
    m_panel.setCellWidth( m_iconTide2, "20px" );
    m_iconTide2.setTitle( "Marées futur:" );

    // m_panel.setWidth( "100%" );
    // m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( m_panel );
  }


  private AnEvent m_oldGameEvent = null;

  protected void redraw()
  {
    EbGame game = ModelFmpMain.model().getGame();
    AnEvent lastEvent = game.getLastGameLog();
    if( (lastEvent != m_oldGameEvent) && (ModelFmpMain.model().getMyRegistration() != null) )
    {
      m_oldGameEvent = lastEvent;

      m_lblAction.setText( " : " + ModelFmpMain.model().getMyRegistration().getPtAction() + "  " );
      m_lblOre.setText( " : " + ModelFmpMain.model().getMyRegistration().getOreCount() + "  " );

      // Display tides
      // =============
      BoardIcons.iconTide( game.getCurrentTide() ).applyTo( m_iconTide1 );
      m_iconTide1.setTitle( "maree actuelle: " + Messages.getTideString( game.getCurrentTide() ) );

      if( ModelFmpMain.model().getMyRegistration().getWorkingWeatherHenCount() > 0 )
      {
        BoardIcons.iconTide( game.getNextTide() ).applyTo( m_iconTide2 );
        m_iconTide2.setTitle( "maree futur: " + Messages.getTideString( game.getNextTide() ) );
      }
      else
      {
        Icons.s_instance.tide_unknown().applyTo( m_iconTide2 );
        m_iconTide2.setTitle( MAppBoard.s_messages.noForecast() );
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
