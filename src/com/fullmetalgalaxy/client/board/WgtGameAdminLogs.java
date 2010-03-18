/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtGameAdminLogs extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();

  /**
   * 
   */
  public WgtGameAdminLogs()
  {
    ScrollPanel panel = new ScrollPanel();
    panel.add( m_panel );
    initWidget( panel );
  }

  public void redraw()
  {
    m_panel.clear();

    for( AnEvent event : ModelFmpMain.model().getGame().getLogs() )
    {
      // display all non admin events
      if( event.getType().isEventAdmin() )
      {
        Label label = new Label( event.toString() );
        m_panel.add( label );
      }
    }
  }

}
