/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Vincent Legendre
 * @BeanClass com.fullmetalgalaxy.model.EbConfigGameVariant
 */
public class WgtConfigGameVariant extends WgtBean
{
  // UI
  private FlowPanel m_panel = new FlowPanel();
  private WgtIntBox m_actionPtMaxReserve = new WgtIntBox();
  private WgtIntBox m_minSpaceBetweenFreighter = new WgtIntBox();
  private WgtIntBox m_deployementRadius = new WgtIntBox();
  private WgtTextBox m_description = new WgtTextBox();


  /**
   * 
   */
  public WgtConfigGameVariant()
  {
    super();
    getActionPtMaxReserve().setTitle( "actionPtMaxReserve" );
    getActionPtMaxReserve().setReadOnly( true );
    m_panel.add( m_actionPtMaxReserve );

    getMinSpaceBetweenFreighter().setTitle( "minSpaceBetweenFreighter" );
    getMinSpaceBetweenFreighter().setReadOnly( true );
    m_panel.add( m_minSpaceBetweenFreighter );

    m_deployementRadius.setTitle( "deployementRadius" );
    m_deployementRadius.setReadOnly( true );
    m_panel.add( m_deployementRadius );

    getDescription().setTitle( "description" );
    getDescription().setReadOnly( true );
    m_panel.add( m_description );

    initWidget( m_panel );
  }



  /**
   * @return the actionPtMaxReserve
   */
  protected WgtIntBox getActionPtMaxReserve()
  {
    return m_actionPtMaxReserve;
  }

  /**
   * @return the minSpaceBetweenFreighter
   */
  protected WgtIntBox getMinSpaceBetweenFreighter()
  {
    return m_minSpaceBetweenFreighter;
  }

  /**
   * @return the deployementRadius
   */
  protected WgtIntBox getDeployementRadius()
  {
    return m_deployementRadius;
  }

  /**
   * @return the description
   */
  protected WgtTextBox getDescription()
  {
    return m_description;
  }


}
