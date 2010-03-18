/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public abstract class MABtn implements MiniApp
{
  RootPanel panel = RootPanel.get( getHistoryId() );

  public MABtn()
  {

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getHistoryId()
   */
  public abstract String getHistoryId();

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  public Widget getTopWidget()
  {
    return panel;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  public void hide()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show(com.fullmetalgalaxy.client.HistoryState)
   */
  public void show(HistoryState p_state)
  {
  }

}
