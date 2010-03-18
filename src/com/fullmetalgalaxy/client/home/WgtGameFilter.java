/**
 * 
 */
package com.fullmetalgalaxy.client.home;

import java.util.List;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtGameFilter extends Composite implements ClickListener
{
  // UI
  private Button m_btnSwitchDetail = new Button( "Detail" );
  private WgtSimpleFilter m_wgtSimpleFilter = null;
  private WgtDetailFilter m_wgtDetailFilter = null;
  private AsyncCallback<List<EbGamePreview>> m_callbackGameList = null;
  private GameFilter m_gameFilter = new GameFilter();


  /**
   * 
   */
  public WgtGameFilter(AsyncCallback<List<EbGamePreview>> p_callback)
  {
    assert p_callback != null;
    m_callbackGameList = p_callback;
    m_wgtSimpleFilter = new WgtSimpleFilter( this );
    m_wgtSimpleFilter.setSize( "100%", "100%" );
    m_wgtDetailFilter = new WgtDetailFilter( this );
    m_wgtDetailFilter.setSize( "100%", "100%" );
    VerticalPanel panel = new VerticalPanel();
    panel.add( m_wgtSimpleFilter );
    panel.add( m_wgtDetailFilter );
    m_wgtDetailFilter.setVisible( false );
    m_btnSwitchDetail.addClickListener( this );
    panel.add( m_btnSwitchDetail );
    panel.setCellHorizontalAlignment( m_btnSwitchDetail, HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnSwitchDetail )
    {
      m_wgtDetailFilter.setVisible( !m_wgtDetailFilter.isVisible() );
      m_wgtSimpleFilter.setVisible( !m_wgtSimpleFilter.isVisible() );
    }

  }


  public void resfreshGameList()
  {
    ModelFmpMain.model().setGameFilter( m_gameFilter.newInstance() );
    Services.Util.getInstance().getGameList( m_gameFilter, m_callbackGameList );
  }

  /**
   * @return the gameFilter
   */
  public GameFilter getGameFilter()
  {
    return m_gameFilter;
  }


}
