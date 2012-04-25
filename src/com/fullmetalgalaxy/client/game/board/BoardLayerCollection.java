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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;

import java.util.ArrayList;

import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * This subclass of ArrayList assumes that
 * all objects added to it will be of type BoardLayer.
 */
public class BoardLayerCollection extends ArrayList<BoardLayer> implements BoardLayer
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public BoardLayerCollection()
  {
  }


  /**
   * shouldn't be called, as it always return null. 
   * @see com.fullmetalgalaxy.client.game.board.BoardLayer#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#hide()
   */
  @Override
  public void hide()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      ((BoardLayer)it.next()).hide();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#onModelChange()
   */
  @Override
  public void onModelChange()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      try
      {
        ((BoardLayer)it.next()).onModelChange();
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
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().redraw( p_left, p_top, p_right, p_botom );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().setZoom( p_zoom );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#show()
   */
  @Override
  public void show()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().show();
    }
  }

}
