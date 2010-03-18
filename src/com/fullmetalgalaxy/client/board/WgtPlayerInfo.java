/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */
public class WgtPlayerInfo extends WgtView
{
  HorizontalPanel m_panel = new HorizontalPanel();
  Image m_iconAction = Icons.s_instance.action16().createImage();
  Label m_lblAction = new Label( " : 0  " );
  Image m_iconOre = Icons.s_instance.ore16().createImage();
  Label m_lblOre = new Label( " : 0  " );
  Image m_iconChar = Icons.s_instance.char16().createImage();
  Label m_lblChar = new Label( " : 0  " );

  /**
   * 
   */
  public WgtPlayerInfo()
  {
    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    m_panel.add( m_iconAction );
    m_iconAction.setTitle( "Point d'action restant" );
    m_panel.add( m_lblAction );
    m_lblAction.setTitle( "Point d'action restant" );
    m_panel.setCellWidth( m_lblAction, "40px" );
    m_lblAction.setStyleName( "fmp-status-text" );
    m_panel.add( m_iconOre );
    m_iconOre.setTitle( "Minerais en soute" );
    m_panel.add( m_lblOre );
    m_lblOre.setTitle( "Minerais en soute" );
    m_panel.setCellWidth( m_lblOre, "40px" );
    m_lblOre.setStyleName( "fmp-status-text" );
    m_panel.add( m_iconChar );
    m_iconChar.setTitle( "Nombre de vehicule" );
    m_panel.add( m_lblChar );
    m_lblChar.setTitle( "Nombre de vehicule" );
    m_panel.setCellWidth( m_lblChar, "40px" );
    m_lblChar.setStyleName( "fmp-status-text" );

    // m_panel.setWidth( "100%" );
    // m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    initWidget( m_panel );
  }


  private AnEvent m_oldGameEvent = null;

  protected void redraw()
  {
    AnEvent lastEvent = ModelFmpMain.model().getGame().getLastLog();
    if( (lastEvent != m_oldGameEvent) && (ModelFmpMain.model().getMyRegistration() != null) )
    {
      m_oldGameEvent = lastEvent;

      m_lblAction.setText( " : " + ModelFmpMain.model().getMyRegistration().getPtAction() + "  " );
      m_lblOre.setText( " : " + ModelFmpMain.model().getMyRegistration().getOreCount() + "  " );
      m_lblChar.setText( " : " + ModelFmpMain.model().getMyRegistration().getTokenCount() + "  " );
    }

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  public void notifyHmiUpdate()
  {
    redraw();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    redraw();
  }

}
