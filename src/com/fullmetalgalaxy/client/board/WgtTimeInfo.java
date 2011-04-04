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

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbGame;
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

  // private Label m_lblTurn = new Label( " " );

  // private HTML m_html = new HTML();


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
        lbl = new Label( game.getCurrentPlayerRegistration().getAccountPseudo() + ")" );
      }
      else
      {
        lbl = new Label( "unknown)" );
      }
      lbl.setStyleName( "fmp-status-text" );
      lbl.setTitle( "Joueur actuel" );
      m_panel.add( lbl );


      if( ModelFmpMain.model().isJoined()
          && ModelFmpMain.model().getMyRegistration() == game.getCurrentPlayerRegistration() )
      {
        Label lblDate = new HTML( "&nbsp;- "
            + ClientUtil.formatDateTime( game.getCurrentPlayerRegistration().getEndTurnDate() ) );
        lblDate.setStyleName( "fmp-status-text" );
        lblDate.setTitle( "Date de fin de tour" );
        m_panel.add( lblDate );
      }
    }
    else if( game.isAsynchron() )
    {
      Label lblDate = new HTML( "&nbsp;- "
          + ClientUtil.formatDateTime( game.estimateNextTimeStep() ) );
      lblDate.setStyleName( "fmp-status-text" );
      lblDate.setTitle( "Date du prochain increment de temps" );
      m_panel.add( lblDate );
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

}
