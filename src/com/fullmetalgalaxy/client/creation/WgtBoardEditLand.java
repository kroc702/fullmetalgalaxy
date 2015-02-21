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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.BoardConvert;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerLand;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardEditLand extends WgtBoardLayerLand implements MouseListener
{
  private FocusPanel m_focus = new FocusPanel();
  private LandType m_leftClic = LandType.Montain;
  private LandType m_rightClic = LandType.None;
  private int m_brushSize = 1;
  
  /**
   * 
   */
  public WgtBoardEditLand()
  {
    super();
    GameEngine.model().setZoomDisplayed( EnuZoom.Small );
    m_focus.add( m_html );
    m_focus.addMouseListener( this );
    m_focus.setSize( "100%", "100%" );
    add( m_focus, 0, 0 );
  }


  /**
   * 
   * @see com.fullmetalgalaxy.client.game.board.WgtBoardLayerBase#redraw()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    cropDisplay( 0, 0, GameEngine.game().getLandWidth(), GameEngine.game().getLandHeight() );
    // always redraw game in construction as all lands may have been regenerated
    if( (GameEngine.game().getId() == 0) || p_forceRedraw )
    {
      m_lastGameId = -1;
    }
    super.onModelChange( true );
  }

  /*private void redrawAllLands()
  {
    m_images.resetImageIndex();
    EbGame game = ModelFmpMain.model().getGame();
    Image image = null;
    for( int ix = 0; ix < game.getLandWidth(); ix++ )
    {
      for( int iy = 0; iy < game.getLandHeight(); iy++ )
      {
        // image = m_images.getImage();
      }
    }


    m_images.hideOtherImage();
  }*/

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseDown(Widget p_sender, int p_x, int p_y)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseEnter(Widget p_sender)
  {
    // m_layerSelect.setHexagonHightVisible( true );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseLeave(Widget p_sender)
  {
    // m_layerSelect.setHexagonHightVisible( false );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseMove(Widget p_sender, int p_x, int p_y)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(Widget p_sender, int p_x, int p_y)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    AnBoardPosition position = BoardConvert.convertPixPositionToHexPosition( new AnPair( p_x,
        p_y ), getZoom(), new AnPair(0,0) );

    LandType land = m_rightClic;
    if( DOM.eventGetButton( DOM.eventGetCurrentEvent() ) == Event.BUTTON_LEFT )
    {
      land = m_leftClic;
    }

    GameEngine.model().getGame().setLand( position, land );
    if( m_brushSize > 1 )
    {
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.NorthWest ), land );
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.SouthWest ), land );
    }
    if( m_brushSize > 3 )
    {
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.NorthEast ), land );
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.SouthEast ), land );
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.North ), land );
      GameEngine.model().getGame().setLand( GameEngine.coordinateSystem().getNeighbor( position, Sector.South ), land );
    }
    
    // GameEngine.model().getGame().setMinimapUri( null );
    // GameEngine.model().getGame().setMapUri( null );
    onModelChange( true );
  }


  
  /**
   * @param p_leftClic the leftClic to set
   */
  public void setLeftClic(LandType p_leftClic)
  {
    m_leftClic = p_leftClic;
  }


  /**
   * @param p_rightClic the rightClic to set
   */
  public void setRightClic(LandType p_rightClic)
  {
    m_rightClic = p_rightClic;
  }


  /**
   * @return the leftClic
   */
  public LandType getLeftClic()
  {
    return m_leftClic;
  }


  /**
   * @return the rightClic
   */
  public LandType getRightClic()
  {
    return m_rightClic;
  }

/**
 * 
 * @param p_brushSize valid value: 1, 3, 7
 */
  public void setBrushSize(int p_brushSize)
  {
    m_brushSize = p_brushSize;
  }

  
  
}
