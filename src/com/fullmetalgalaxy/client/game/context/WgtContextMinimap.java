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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.context;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
  private Canvas canvas = Canvas.createIfSupported();
  private int m_lastResfreshTurn = -1;
  private long m_lastResfreshGameId = -1;


  private static ImageElement[] tokenImages = new ImageElement[EnuColor.getAllSingleColor().length + 1];
  static
  {
    for( int iColor : EnuColor.getAllSingleColor() )
    {
      Image img = new Image( "/images/board/" + EnuColor.singleColorToString( iColor ) + "/minimap/token.png" );
      tokenImages[iColor] = ImageElement.as( img.getElement() );
      Image.prefetch( img.getUrl() );
    }
  }

  /**
   * 
   */
  public WgtContextMinimap()
  {
    super();
    initWidget( m_panel );


    m_panel.add( canvas );

    canvas.setWidth( FmpConstant.miniMapWidth + "px" );
    canvas.setHeight( FmpConstant.miniMapHeight + "px" );

    canvas.addMouseUpHandler( this );
  }

  public void redraw()
  {
    assert GameEngine.model() != null;
    Game game = GameEngine.model().getGame();


    // if( m_lastResfreshTurn != game.getCurrentTimeStep() ||
    // m_lastResfreshGameId != game.getId() )
    {
      m_lastResfreshTurn = game.getCurrentTimeStep();
      m_lastResfreshGameId = game.getId();

      canvas.setCoordinateSpaceWidth( game.getLandWidth() * 8 );
      canvas.setCoordinateSpaceHeight( game.getLandHeight() * 8 + 4 );


      Context2d gc = canvas.getContext2d();
      gc.clearRect( 0, 0, game.getLandWidth(), game.getLandHeight() );

      // draw lands
      ImageElement[] images = new ImageElement[LandType.values().length];
      for( int iLand = 0; iLand < LandType.values().length; iLand++ )
      {
        LandType land = LandType.values()[iLand];
        Image img = new Image( "/images/board/" + game.getPlanetType().getFolderName() + "/minimap/"
            + land.getImageName( game.getCurrentTide() ) );
        img.addLoadHandler( new LoadHandler()
        {
          @Override
          public void onLoad(LoadEvent event)
          {
            m_lastResfreshTurn = -1;
            m_lastResfreshGameId = -1;
            redraw();
          }
        } );
        images[iLand] = ImageElement.as( img.getElement() );
        Image.prefetch( img.getUrl() );
      }
      for( int ix = 0; ix < game.getLandWidth(); ix++ )
      {
        for( int iy = 0; iy < game.getLandHeight(); iy++ )
        {
          if( game.getLand( ix, iy ) == LandType.Montain )
          {
            gc.drawImage( images[game.getLand( ix, iy ).ordinal()], ix * 8, iy * 8 + (ix % 2) * 4, 8, 8 );
          }
          else
          {
            gc.drawImage( images[game.getLand( ix, iy ).ordinal()], ix * 8, iy * 8 + (ix % 2) * 4, 8, 8 );
          }
        }
      }

      // draw units
      for( EbToken token : game.getSetToken() )
      {
        if( token.getLocation() == Location.Board && token.getColor() != EnuColor.None )
        {
          gc.drawImage( tokenImages[token.getColor()], token.getPosition().getX() * 8, token
              .getPosition()
              .getY() * 8 + (token.getPosition().getX() % 2) * 4, 8, 8 );
          for( AnBoardPosition position : token.getExtraPositions( game.getCoordinateSystem() ) )
          {
            gc.drawImage( tokenImages[token.getColor()], position.getX() * 8, position.getY() * 8
                + (position.getX() % 2) * 4, 8, 8 );
          }
        }
      }
    }

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
      EbTeam winnerTeam = game.getWinnerTeam();
      if( (winnerTeam != null) )
      {
        strWinner += winnerTeam.getCompany().getFullName() + ":\n";
        for( EbRegistration registration : winnerTeam.getPlayers( game.getPreview() ))
          if( registration.haveAccount() )
          {
            strWinner += registration.getAccount().getPseudo() + "\n";
          }
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
    if( p_event.getSource() == canvas )
    {
      int hexPositionX = (int)(((float)p_event.getX() * GameEngine.model().getGame().getLandWidth()) / FmpConstant.miniMapWidth);
      int hexPositionY = (int)(((float)p_event.getY() * GameEngine.model().getGame().getLandHeight()) / FmpConstant.miniMapHeight);
      MAppBoard.s_instance.setScrollPosition( hexPositionX, hexPositionY );
    }
  }



}
