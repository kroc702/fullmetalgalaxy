/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * classes which implements this interface may be added to any part of the root panel. 
 * it should only use the AppMain static interface.
 */
public interface MiniApp
{
  /**
   * the widget which can be added to the root panel.
   * @return
   */
  public Widget getTopWidget();

  /**
   * show all dialog widget, eventually add/modify the AppMain menu.
   * when this method is called, the 'topwidget' is already added to the root panel.
   * @param p_state contain all new applications status.
   */
  public void show(HistoryState p_state);

  /**
   * hide all dialog widgets, remove any menu it create.
   * This method is usually called before removing the top widget from root panel.
   */
  public void hide();

  public String getHistoryId();
}
