/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.model.EnuZoom;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public interface BoardLayer
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
   * should return the widget representing this board layer on html page. 
   * @return
   */
  public Widget getTopWidget();

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

}
