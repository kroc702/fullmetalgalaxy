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
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerBase;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerLand;
import com.fullmetalgalaxy.model.LandType;
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

  /**
   * 
   */
  public WgtBoardEditLand()
  {
    super();
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
    // always redraw game in construction as all lands may have been regenerated
    if( (ModelFmpMain.model().getGame().getId() == 0) || p_forceRedraw )
    {
      m_lastGameId = -1;
    }
    super.onModelChange( p_forceRedraw );
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
    AnBoardPosition position = WgtBoardLayerBase.convertPixPositionToHexPosition( new AnPair( p_x,
        p_y ), getZoom() );

    if( DOM.eventGetButton( DOM.eventGetCurrentEvent() ) == Event.BUTTON_LEFT )
    {
      ModelFmpMain.model().getGame().setLand( position, m_leftClic );
    }
    else
    {
      ModelFmpMain.model().getGame().setLand( position, m_rightClic );
    }
    ModelFmpMain.model().getGame().setMinimapUri( null );
    ModelFmpMain.model().getGame().setMapUri( null );
    onModelChange();
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

}
