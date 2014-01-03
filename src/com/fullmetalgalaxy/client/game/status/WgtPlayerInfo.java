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
package com.fullmetalgalaxy.client.game.status;


import java.util.Date;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtPlayerInfo extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();
  private Image m_iconAction = new Image( Icons.s_instance.action16() );
  private HTML m_lblAction = new HTML( "&nbsp;: 0  " );
  private EbRegistration m_registration = null;

  /**
   * 
   */
  public WgtPlayerInfo(EbRegistration p_registration)
  {
    m_registration = p_registration;
    Game game = GameEngine.model().getGame();

    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

    // player name and color(s)
    // ========================
    Panel hPanel = new HorizontalPanel();
    EnuColor color = m_registration.getEnuColor();

    int colorIndex = 0;
    for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
    {
      if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
      {
        Image image = BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
            .createImage();
        image.setTitle( Messages.getSingleColorString( AppMain.instance().getMyAccount().getId(),
            color.getValue() ) );
        hPanel.add( image );
      }
    }

    Label lbl = null;
    if( m_registration.haveAccount() )
    {
      lbl = new Label( m_registration.getAccount().getPseudo() );
    }
    else
    {
      lbl = new Label( "???" );
    }
    lbl.setStyleName( "fmp-status-text" );
    hPanel.add( lbl );
    m_panel.add( hPanel );

    // action points
    // =============
    hPanel = new HorizontalPanel();
    hPanel.add( m_iconAction );
    m_iconAction.setTitle( MAppBoard.s_messages.remainingActionPoint() );
    hPanel.add( m_lblAction );
    m_lblAction.setTitle( MAppBoard.s_messages.remainingActionPoint() );
    m_lblAction.setHTML( "&nbsp;: "
        + m_registration.getPtAction()
        + "/"
        + (game.getEbConfigGameTime().getActionPtMaxReserve() + ((m_registration.getEnuColor()
            .getNbColor() - 1) * game.getEbConfigGameTime().getActionPtMaxPerExtraShip())) );
    if( game.isParallel() )
    {
      Date nextActionIncrement = game.estimateTimeStepDate( game.getCurrentTimeStep() + 1 );
      m_lblAction
          .setTitle( MAppBoard.s_messages.nextPA(
              m_registration.getActionInc( game ),
              ClientUtil.formatTimeElapsed( nextActionIncrement.getTime()
                  - SharedMethods.currentTimeMillis() ) ) );
    }
    m_panel.add( hPanel );

    // set style name now
    resfreshStyle();

    // m_vPanel.setWidth( "100%" );
    // m_vPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( m_panel );
  }


  private void resfreshStyle()
  {
    Game game = GameEngine.model().getGame();
    if( game.isParallel() && game.getCurrentTimeStep() > 1 )
    {
      if( m_registration.getTeam( game ).getEndTurnDate() != null
          && m_registration.getTeam( game ).getEndTurnDate().getTime() > SharedMethods
              .currentTimeMillis() )
      {
        m_panel.setStylePrimaryName( "fmp-status-currentplayer" );
        m_clockTimer
            .schedule( (int)(m_registration.getTeam( game ).getEndTurnDate().getTime() - SharedMethods
            .currentTimeMillis()) );
      }
      else
      {
        m_panel.setStylePrimaryName( "fmp-status-player" );
      }
    }
    else if( game.getCurrentPlayerIds().contains( m_registration.getId() ) )
    {
      m_panel.setStylePrimaryName( "fmp-status-currentplayer" );
    }
    else
    {
      m_panel.setStylePrimaryName( "fmp-status-player" );
    }
  }

  private Timer m_clockTimer = new Timer()
  {
    @Override
    public void run()
    {
      resfreshStyle();
    }
  };




}
