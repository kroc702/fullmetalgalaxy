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
package com.fullmetalgalaxy.client.game.context;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */

public class WgtContextMinimap extends Composite implements MouseUpHandler
{
  private AbsolutePanel m_panel = new AbsolutePanel();
  private Image m_miniMapImage = new Image();

  String m_lastMiniMapUri = null;


  /**
   * 
   */
  public WgtContextMinimap()
  {
    super();
    initWidget( m_panel );
    m_miniMapImage.addMouseUpHandler( this );
  }

  public void redraw()
  {
    assert GameEngine.model() != null;
    Game game = GameEngine.model().getGame();

    m_panel.clear();

    if( game.getMinimapUri() != null
        && (m_lastMiniMapUri == null || !m_lastMiniMapUri.equals( game.getMinimapUri() )) )
    {
      m_lastMiniMapUri = game.getMinimapUri();
      m_miniMapImage.setUrl( game.getMinimapUri() );
      m_miniMapImage.setPixelSize( FmpConstant.miniMapWidth, FmpConstant.miniMapHeight );
    }
    m_panel.add( m_miniMapImage );
    if( game.getStatus() == GameStatus.Open || game.getStatus() == GameStatus.Pause )
    {
      m_panel.add( new Image( Icons.s_instance.pause32() ), FmpConstant.miniMapWidth / 2 - 16,
          FmpConstant.miniMapHeight / 2 - 16 );
      m_panel.add( new Label( "En Pause" ), 0, FmpConstant.miniMapHeight / 2 + 30 );
    }
    else if( game.isFinished() )
    {
      m_panel.add( new Label( "Partie terminée" ), 0, FmpConstant.miniMapHeight / 2 - 40 );
      m_panel.add( new Image( Icons.s_instance.winner32() ), FmpConstant.miniMapWidth / 2 - 16,
          FmpConstant.miniMapHeight / 2 - 16 );
      String strWinner = "";
      EbRegistration winner = game.getWinnerRegistration();
      if( (winner != null) && (winner.haveAccount()) )
      {
        strWinner = winner.getAccount().getPseudo();
      }
      m_panel.add( new Label( strWinner ), 0, FmpConstant.miniMapHeight / 2 + 30 );
    }

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(MouseUpEvent p_event)
  {
    if( p_event.getSource() == m_miniMapImage )
    {
      // TODO recreate this function
      /*
      if( AppMain.instance().getMApp( MAppBoard.HISTORY_ID ) != null )
      {
        int hexPositionX = (int)(((float)p_event.getX() * ModelFmpMain.model().getGame()
            .getLandWidth()) / FmpConstant.miniMapWidth);
        int hexPositionY = (int)(((float)p_event.getY() * ModelFmpMain.model().getGame()
            .getLandHeight()) / FmpConstant.miniMapHeight);
        MAppBoard boardApp = (MAppBoard)AppMain.instance().getMApp( MAppBoard.HISTORY_ID );
        boardApp.setScrollPosition( hexPositionX, hexPositionY );
      }*/
    }
  }



}
