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
package com.fullmetalgalaxy.client.game.board;

import java.util.ArrayList;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * This subclass of ArrayList assumes that
 * all objects added to it will be of type BoardLayer.
 */
public class BoardLayerCollection extends ArrayList<BoardLayer> implements BoardLayer
{
  static final long serialVersionUID = 1;

  private AbsolutePanel m_panel = new AbsolutePanel();

  /**
   * 
   */
  public BoardLayerCollection()
  {
  }

  public void addLayer(BoardLayer p_layer)
  {
    m_panel.add( p_layer.asWidget(), 0, 0 );
    add( p_layer );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#hide()
   */
  @Override
  public void hide()
  {
    for( BoardLayer layer : this )
    {
      layer.hide();
    }
  }

  private long m_gameId = -1;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#onModelChange()
   */
  @Override
  public void onModelChange()
  {
    for( BoardLayer layer : this )
    {
      try
      {
        layer.onModelChange();
      } catch( Exception e )
      {
        // no i18n
        RpcUtil.logError( "a board layer bug while notify a model update", e );
      }
    }
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#redraw(int, int, int, int)
   */
  @Override
  public void redraw(int p_left, int p_top, int p_right, int p_botom)
  {
    for( BoardLayer layer : this )
    {
      layer.redraw( p_left, p_top, p_right, p_botom );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    boolean isSizeReset = false;
    for( BoardLayer layer : this )
    {
      layer.setZoom( p_zoom );
      if( !isSizeReset && layer.asWidget().isVisible() )
      {
        isSizeReset = true;
        m_panel
            .setPixelSize( layer.asWidget().getOffsetWidth(), layer.asWidget().getOffsetHeight() );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#show()
   */
  @Override
  public void show()
  {
    for( BoardLayer layer : this )
    {
      layer.show();
    }

    // zoom
    setZoom( GameEngine.model().getZoomDisplayed() );

  }


  @Override
  public Widget asWidget()
  {
    return m_panel;
  }


  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    for( BoardLayer layer : this )
    {
      layer.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );
    }
  }

  @Override
  public AnPair getCropTopLeft()
  {
    if( !isEmpty() )
    {
      return get(0).getCropTopLeft();
    }
    return new AnPair(0,0);
  }

}
