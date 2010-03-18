/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 *
 */
public class WgtModelUpdateEvent extends WgtView
{
  // UI
  VerticalPanel m_panel = new VerticalPanel();

  /**
   * @param p_fmpCtrl
   */
  public WgtModelUpdateEvent()
  {
    super();

    initWidget( m_panel );

    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    // Give the overall composite a style name.
    setStyleName( "WgtModelUpdateEvent" );
  }

  protected void redraw()
  {
    // TODO display all update event of the model, display double call
    m_panel.clear();
    m_panel.add( new Label( "WgtModelUpdateEvent" ) );
    m_panel.add( new Label( "" + ModelFmpMain.model().getGame().getLastServerUpdate() ) );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  public void notifyHmiUpdate()
  {
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ctrModelSender)
  {
    // TODO optimisation: redraw only if required
    redraw();
  }
}
