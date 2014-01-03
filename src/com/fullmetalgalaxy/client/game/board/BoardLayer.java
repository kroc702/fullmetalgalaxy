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


import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Vincent Legendre
 *
 */
public interface BoardLayer extends IsWidget
{
  /**
   * called once, when this board layer is shown.
   * note: it doesn't mean that layer have valid data to show.
   */
  public void show();

  /**
   * called once, when this board layer is hidden.
   */
  public void hide();

  /**
   * called when the board zoom is changed.
   * @param p_zoom
   */
  public void setZoom(EnuZoom p_zoom);


  /**
   * called each time the visible part of this layer is changed. (ie when user drag the board)
   * @param p_left in pixel
   * @param p_top in pixel
   * @param p_right in pixel
   * @param p_botom in pixel
   */
  public void redraw(int p_left, int p_top, int p_right, int p_botom);

  /**
   * called each time the model changed.
   */
  public void onModelChange();

  /**
   * we can restrict drawing area 
   */
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex, int p_cropBotomHex);

  public AnPair getCropTopLeft();
}
