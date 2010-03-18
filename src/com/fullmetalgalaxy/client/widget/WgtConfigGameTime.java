/**
 * 
 */
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * @BeanClass com.fullmetalgalaxy.model.EbConfigGameTime
 */
public class WgtConfigGameTime extends WgtBean
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();
  private WgtTextBox m_description = new WgtTextBox();
  private WgtIntBox m_timeStepDurationInSec = new WgtIntBox();
  private WgtIntBox m_tideChangeFrequency = new WgtIntBox();
  private WgtIntBox m_totalTimeStep = new WgtIntBox();
  private WgtIntBox m_actionPtPerTimeStep = new WgtIntBox();
  private WgtIntBox m_actionPtPerExtraShip = new WgtIntBox();


  /**
   * 
   */
  public WgtConfigGameTime()
  {
    super();

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Description :" ) );
    hPanel.add( getDescription() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "increment de temps en seconde :" ) );
    hPanel.add( getTimeStepDurationInSec() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Nombre d'increment de temps :" ) );
    hPanel.add( getTotalTimeStep() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Frequence des changements de marrees :" ) );
    hPanel.add( getTideChangeFrequency() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Point d'action par increment de temps :" ) );
    hPanel.add( getActionPtPerTimeStep() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Point d'action supplementaire par astronef :" ) );
    hPanel.add( getActionPtPerExtraShip() );
    m_panel.add( hPanel );


    initWidget( m_panel );

    // receive all model change
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
  }


  /**
   * @return the panel
   */
  protected VerticalPanel getPanel()
  {
    return m_panel;
  }


  /**
   * @return the timeStepDurationInSec
   */
  protected WgtIntBox getTimeStepDurationInSec()
  {
    return m_timeStepDurationInSec;
  }


  /**
   * @return the tideChangeFrequency
   */
  protected WgtIntBox getTideChangeFrequency()
  {
    return m_tideChangeFrequency;
  }


  /**
   * @return the totalTimeStep
   */
  protected WgtIntBox getTotalTimeStep()
  {
    return m_totalTimeStep;
  }


  /**
   * @return the actionPtPerTimeStep
   */
  protected WgtIntBox getActionPtPerTimeStep()
  {
    return m_actionPtPerTimeStep;
  }


  /**
   * @return the actionPtPerExtraShip
   */
  protected WgtIntBox getActionPtPerExtraShip()
  {
    return m_actionPtPerExtraShip;
  }


  /**
   * @return the description
   */
  protected WgtTextBox getDescription()
  {
    return m_description;
  }



}
