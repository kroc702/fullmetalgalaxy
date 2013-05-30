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
package com.fullmetalgalaxy.client.game.status;

import java.util.ArrayList;
import java.util.Date;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class WgtGameStatus extends WgtView
{
  private VerticalPanel m_panel = new VerticalPanel();
  private Image m_iconTime = new Image();

  Label m_lblGameName = new Label();
  HTML m_lblDate = new HTML( "" );
  Date m_endTurn = null;
  HorizontalPanel m_panelTide = new HorizontalPanel();
  Image m_iconMoon = new Image( Icons.s_instance.moon16() );
  Label m_lblMoon = new HTML( "&nbsp;:&nbsp;" );
  HTML m_lblTurn = new HTML();

  private AnEvent m_oldGameEvent = new AnEvent();


  /**
   * 
   */
  public WgtGameStatus()
  {
    // subscribe all needed models update event
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );

    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

    // game name
    m_lblGameName.setStyleName( "fmp-status-title" );
    m_panel.add( m_lblGameName );

    // game turn
    Panel hPanel = new HorizontalPanel();
    m_iconTime.setUrl( "/images/clear.cache.gif" );
    hPanel.add( m_iconTime );
    m_lblTurn.setStyleName( "fmp-status-text" );
    m_lblTurn.setTitle( "Tour actuel / Nombre total de tours" );
    hPanel.add( m_lblTurn );
    m_lblDate.setText( "" );
    m_lblDate.setStyleName( "fmp-status-text" );
    m_lblDate.setTitle( "Date de fin de tour" );
    hPanel.add( m_lblDate );

    // game tide
    hPanel.add( m_iconMoon );
    m_iconMoon.setTitle( MAppBoard.s_messages.tide() );
    hPanel.add( m_lblMoon );
    m_lblMoon.setTitle( MAppBoard.s_messages.tide() );
    m_lblMoon.setStyleName( "fmp-status-text" );
    hPanel.add( m_panelTide );


    m_panel.setStyleName( "fmp-status-game" );
    m_panel.add( hPanel );

    initWidget( m_panel );
  }

  private long m_gameLastVersion = -1;


  protected void redraw()
  {
    Game game = GameEngine.model().getGame();
    if( game == null )
    {
      return;
    }
    if( m_gameLastVersion == game.getVersion() )
    {
      return;
    }
    m_gameLastVersion = game.getVersion();

    m_lblGameName.setText( game.getName() );


    // display end game date or player's turn
    // ======================================

    if( game.getEbConfigGameTime().isParallel() )
    {
      m_iconTime.setUrl( "/images/icons/parallele16.png" );
    }
    else
    {
      m_iconTime.setUrl( "/images/icons/turnbyturn16.png" );
    }

    m_lblTurn.setHTML( "&nbsp;: " + game.getCurrentTimeStep() + "/"
        + game.getEbConfigGameTime().getTotalTimeStep() + "&nbsp;&nbsp;" );

    if( GameEngine.model().getGame().getStatus() == GameStatus.Running
        && !GameEngine.model().isTimeLineMode()
        && GameEngine.model().getGame().getEbConfigGameTime().getTimeStepDurationInSec() != 0
        && game.getCurrentTimeStep() > 1 )
    {
      if( game.isParallel() )
      {
        displayEndTurn( game.estimateNextTimeStep() );
      }
      else
      {
        displayEndTurn( game.getRegistration( game.getCurrentPlayerIds().get( 0 ) )
.getTeam()
            .getEndTurnDate() );
      }
    }



    // display tide and takeoff turn
    // =============================
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
        // Display current take off turn
        if( game.getAllowedTakeOffTurns().contains( game.getCurrentTimeStep() ) )
        {
          // take off is allowed : display it !
          image = new Image( Icons.s_instance.takeOff16() );
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
        if( game.getEbConfigGameTime().isParallel() )
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
            && (game.getNextTideChangeTimeStep()
                + game.getEbConfigGameTime().getTideChangeFrequency() <= game.getEbConfigGameTime()
                .getTotalTimeStep()) )
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
            image = new Image( Icons.s_instance.takeOff16() );
          }
          else
          {
            image = new Image( Icons.s_instance.takeOffBW16() );
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



  private AsyncCallback<Void> m_dummyCallback = new AsyncCallback<Void>()
  {
    @Override
    public void onFailure(Throwable p_caught)
    {
    }

    @Override
    public void onSuccess(Void p_result)
    {
    }
  };


  private String secToStr(long p_sec)
  {
    if( p_sec <= 0 )
    {
      if( p_sec % 2 == 0 )
      {
        return "0:00";
      }
      else
      {
        return "&nbsp;&nbsp;&nbsp;&nbsp;";
      }
    }
    StringBuffer strBuf = new StringBuffer( 10 );
    strBuf.append( (int)(p_sec / 60) );
    strBuf.append( ":" );
    int sec = (int)(p_sec % 60);
    if( sec < 10 )
    {
      strBuf.append( "0" );
    }
    strBuf.append( sec );
    return strBuf.toString();
  }

  private void displayEndTurn(Date p_endTurn)
  {
    if( p_endTurn == null )
    {
      m_lblDate.setHTML( "" );
      return;
    }
    m_endTurn = p_endTurn;
    m_clockTimer.cancel();
    long sec = (m_endTurn.getTime() - ClientUtil.serverTimeMillis()) / 1000;
    if( sec >= 60 * 60 )
    {
      // if end turn is farrer than one hour, simply display date
      m_lblDate.setHTML( "&nbsp;(" + ClientUtil.formatDateTime( m_endTurn )
          + ")&nbsp;&nbsp;" );
    }
    else
    {
      // otherwise, display remaining time
      m_lblDate.setHTML( "&nbsp;(-" + secToStr( sec ) + ")&nbsp;&nbsp;" );
      // and start clock
      m_clockTimer.schedule( 1000 );
    }
  }

  private Timer m_clockTimer = new Timer()
  {
    @Override
    public void run()
    {
      long sec = (m_endTurn.getTime() - ClientUtil.serverTimeMillis()) / 1000;
      m_lblDate.setHTML( "&nbsp;(-" + secToStr( sec ) + ")&nbsp;&nbsp;" );
      if( sec == 0 )
      {
        AppMain.getRpcService().checkUpdate( GameEngine.model().getGame().getId(),
            m_dummyCallback );
      }
      m_clockTimer.schedule( 1000 );

      // if game is paused, then cancel timer
      if( GameEngine.model().getGame().getStatus() != GameStatus.Running )
      {
        m_lblDate.setHTML( "" );
        m_clockTimer.cancel();
      }
    }
  };


}
