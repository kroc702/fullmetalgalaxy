/**
 * 
 */
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtScroll;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtEditTokens extends WgtView implements WindowResizeListener
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private WgtScroll m_wgtScroll = new WgtScroll();
  private WgtToolsEditTokens m_tools = null;
  private WgtBoardEditTokens m_wgtBoard = new WgtBoardEditTokens();

  /**
   * 
   */
  public WgtEditTokens()
  {
    m_tools = new WgtToolsEditTokens( m_wgtBoard );
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    m_wgtScroll.addScrollListener( m_wgtBoard );
    m_wgtScroll.setWidget( m_wgtBoard );
    m_panel.add( m_tools );
    m_panel.add( m_wgtScroll );
    initWidget( m_panel );
    setSize( "100%", "100%" );
    m_panel.setCellWidth( m_wgtScroll, "100%" );
    m_panel.setCellHeight( m_wgtScroll, "100%" );
    m_panel.setCellWidth( m_tools, "100px" );
    m_panel.setCellHeight( m_tools, "100%" );
    m_tools.setSize( "100px", "100%" );
    Window.addWindowResizeListener( this );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
   */
  public void onWindowResized(int p_width, int p_height)
  {
    if( !isVisible() )
    {
      return;
    }
    // setSize( "100%", "100%" );
    m_wgtScroll.fireScroll();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_modelSender)
  {
    if( isVisible() )
    {
      m_wgtScroll.fireScroll();
      m_wgtBoard.notifyModelUpdate( p_modelSender );
    }
  }



}
