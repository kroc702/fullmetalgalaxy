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
package com.fullmetalgalaxy.client.board;

import java.util.Date;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */
public class WgtTimeInfo extends WgtView
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private Image m_iconTime = new Image();

  HTML m_lblDate = new HTML( "" );
  Date m_endTurn = null;

  /**
   * 
   */
  public WgtTimeInfo()
  {
    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    // m_html.setStyleName( "fmp-status-text" );
    // m_html.setWidth( "100%" );

    m_panel.add( m_iconTime );
    // m_panel.add( m_lblTurn );


    m_lblDate.setStyleName( "fmp-status-text" );
    m_lblDate.setTitle( "Date de fin de tour" );

    initWidget( m_panel );
  }

  private long m_gameLastVersion = 0;


  protected void redraw()
  {
    EbGame game = ModelFmpMain.model().getGame();
    if( game == null )
    {
      return;
    }
    if( m_gameLastVersion == game.getVersion() )
    {
      return;
    }
    m_gameLastVersion = game.getVersion();

    m_panel.clear();

    if( game.getEbConfigGameTime().isAsynchron() )
    {
      m_iconTime.setUrl( "/images/css/icon_parallele.gif" );
    }
    else
    {
      m_iconTime.setUrl( "/images/css/icon_tbt.gif" );
    }
    m_panel.add( m_iconTime );

    // display end game date or player's turn
    // ======================================
    Label lblTurn = new HTML( "&nbsp;: " + game.getCurrentTimeStep() + "/"
        + game.getEbConfigGameTime().getTotalTimeStep() );
    lblTurn.setStyleName( "fmp-status-text" );
    lblTurn.setTitle( "Tour actuel / Nombre total de tours" );
    m_panel.add( lblTurn );

    EnuColor color = new EnuColor( EnuColor.None );
    if( game.getCurrentPlayerRegistration() != null )
    {
      color = game.getCurrentPlayerRegistration().getEnuColor();
      Label lbl = new HTML( "&nbsp;(" );
      lbl.setStyleName( "fmp-status-text" );
      lbl.setTitle( "Joueur actuel" );
      m_panel.add( lbl );

      int colorIndex = 0;
      for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
      {
        if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
        {
          Image image = BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
              .createImage();
          image.setTitle( "Joueur actuel" );
          m_panel.add( image );
        }
      }

      if( game.getCurrentPlayerRegistration().haveAccount() )
      {
        lbl = new Label( game.getCurrentPlayerRegistration().getAccountPseudo() );
      }
      else
      {
        lbl = new Label( "???" );
      }
      lbl.setStyleName( "fmp-status-text" );
      lbl.setTitle( "Joueur actuel" );
      m_panel.add( lbl );


      if( ModelFmpMain.model().getGame().getEbConfigGameTime().getTimeStepDurationInSec() != 0
          && ModelFmpMain.model().getGame().isStarted() )
      {
        displayEndTurn( game.getCurrentPlayerRegistration().getEndTurnDate() );
        m_panel.add( m_lblDate );
      }

      lbl = new Label( ")" );
      lbl.setStyleName( "fmp-status-text" );
      m_panel.add( lbl );
    }
    else if( game.isAsynchron() && ModelFmpMain.model().getGame().isStarted() )
    {
      displayEndTurn( game.estimateNextTimeStep() );
      m_panel.add( m_lblDate );
    }


    if( (!game.isAsynchron()) && (ModelFmpMain.model().getMyRegistration() != null)
        && (ModelFmpMain.model().getMyRegistration() != game.getCurrentPlayerRegistration()) )
    {
      Label lblDate = new Label( ClientUtil.formatDateTime( ModelFmpMain.model()
          .getMyRegistration().getEndTurnDate() ) );
      lblDate.setStyleName( "fmp-status-text" );
      lblDate.setTitle( "Date de fin de prochain tour" );
      m_panel.add( lblDate );
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
        return "    ";
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
      m_lblDate.setHTML( "&nbsp;- ?" );
      return;
    }
    m_endTurn = p_endTurn;
    m_clockTimer.cancel();
    long sec = (m_endTurn.getTime() - System.currentTimeMillis()) / 1000;
    if( sec >= 60 * 60 )
    {
      // if end turn is farrer than one hour, simply display date
      m_lblDate.setHTML( "&nbsp;- " + ClientUtil.formatDateTime( m_endTurn ) );
    }
    else
    {
      // otherwise, display remaining time
      m_lblDate.setHTML( "&nbsp; -" + secToStr( sec ) );
      // and start clock
      m_clockTimer.schedule( 1000 );
    }
  }

  private Timer m_clockTimer = new Timer()
  {
    @Override
    public void run()
    {
      long sec = (m_endTurn.getTime() - System.currentTimeMillis()) / 1000;
      m_lblDate.setHTML( "&nbsp; -" + secToStr( sec ) );
      if( sec == 0 )
      {
        Services.Util.getInstance().checkUpdate( ModelFmpMain.model().getGame().getId(),
            m_dummyCallback );
      }
      m_clockTimer.schedule( 1000 );
    }
  };


}
