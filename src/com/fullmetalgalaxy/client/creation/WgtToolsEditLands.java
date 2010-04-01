/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client.creation;

import java.util.ArrayList;
import java.util.List;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtToolsEditLands extends Composite implements ClickListener, MouseListener,
    ChangeListener
{
  private Panel m_panel = new VerticalPanel();
  private TextBox m_txtLandWidth = new TextBox();
  private TextBox m_txtLandHeight = new TextBox();
  private Button m_btnClear = new Button( "effacer" );
  private Button m_btnGenerate = new Button( "generer" );
  private TextBox m_txtLandPercent = new TextBox();
  private CheckBox m_chkRoundMap = new CheckBox();
  private Button m_btnLoadMap = new Button( "Charger une carte" );
  private DlgLoadMap m_dlgLoadMap = new DlgLoadMap();
  private ListBox m_lstPlanet = new ListBox();
  private List<PlanetType> m_planets = new ArrayList<PlanetType>();

  private Image m_leftLand = new Image();
  private Image m_rightLand = new Image();
  private Image m_btnPlain = new Image( "images/board/desert/strategy/plain.png", 0, 0, FmpConstant
      .getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );
  private Image m_btnMontain = new Image( "images/board/desert/strategy/montain.png", 0, 0,
      FmpConstant.getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );
  private Image m_btnReef = new Image( "images/board/desert/strategy/reef_low.png", 0, 0,
      FmpConstant.getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );
  private Image m_btnMarsh = new Image( "images/board/desert/strategy/swamp_low.png", 0, 0,
      FmpConstant.getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );
  private Image m_btnSea = new Image( "images/board/desert/strategy/sea.png", 0, 0, FmpConstant
      .getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );
  private Image m_btnNone = new Image( "images/board/desert/strategy/grid.gif", 0, 0, FmpConstant
      .getHexWidth( EnuZoom.Small ), FmpConstant.getHexHeight( EnuZoom.Small ) );

  private WgtBoardEditLand m_wgtlayerEditLand = null;

  /**
   * 
   */
  public WgtToolsEditLands(WgtBoardEditLand p_wgtlayerEditLand)
  {
    assert p_wgtlayerEditLand != null;
    m_wgtlayerEditLand = p_wgtlayerEditLand;
    m_lstPlanet.addChangeListener( this );
    m_lstPlanet.setMultipleSelect( false );
    m_lstPlanet.setVisibleItemCount( 1 );
    for( PlanetType planet : PlanetType.values() )
    {
      m_lstPlanet.addItem( Messages.getPlanetString( planet ) );
      m_planets.add( planet );
    }
    m_panel.add( m_lstPlanet );
    m_panel.add( new Label( "taille de carte" ) );
    m_panel.add( m_txtLandWidth );
    m_panel.add( m_txtLandHeight );
    m_btnClear.addClickListener( this );
    m_panel.add( m_btnClear );
    m_btnGenerate.addClickListener( this );
    m_panel.add( m_btnGenerate );
    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.add( new Label( "terre en %" ) );
    m_txtLandPercent.addChangeListener( this );
    hpanel.add( m_txtLandPercent );
    m_txtLandPercent.setText( "" + GameGenerator.getLandPercent() );
    m_txtLandPercent.setMaxLength( 3 );
    m_txtLandPercent.setWidth( "30px" );
    m_panel.add( hpanel );
    hpanel = new HorizontalPanel();
    hpanel.add( new Label( "Hexagonale" ) );
    hpanel.add( m_chkRoundMap );
    m_chkRoundMap.setChecked( GameGenerator.isHexagonMap() );
    m_panel.add( hpanel );

    hpanel = new HorizontalPanel();
    hpanel.add( m_leftLand );
    hpanel.add( m_rightLand );
    m_panel.add( hpanel );
    m_btnNone.addMouseListener( this );
    m_panel.add( m_btnNone );
    m_btnSea.addMouseListener( this );
    m_panel.add( m_btnSea );
    m_btnReef.addMouseListener( this );
    m_panel.add( m_btnReef );
    m_btnMarsh.addMouseListener( this );
    m_panel.add( m_btnMarsh );
    m_btnPlain.addMouseListener( this );
    m_panel.add( m_btnPlain );
    m_btnMontain.addMouseListener( this );
    m_panel.add( m_btnMontain );
    m_btnLoadMap.addClickListener( this );
    m_panel.add( m_btnLoadMap );

    setClicTool( Event.BUTTON_LEFT, LandType.Sea );
    setClicTool( Event.BUTTON_RIGHT, LandType.Montain );

    initWidget( m_panel );
    redraw();
  }

  protected void redraw()
  {
    String base = "images/board/" + ModelFmpMain.model().getGame().getPlanetType().getFolderName();
    m_btnPlain.setUrl( base + "/strategy/plain.png" );
    m_btnMontain.setUrl( base + "/strategy/montain.png" );
    m_btnReef.setUrl( base + "/strategy/reef_low.png" );
    m_btnMarsh.setUrl( base + "/strategy/swamp_low.png" );
    m_btnSea.setUrl( base + "/strategy/sea.png" );
    m_leftLand.setUrlAndVisibleRect( base + "/tactic/"
        + m_wgtlayerEditLand.getLeftClic().getImageName(), 0, 0, FmpConstant
        .getHexWidth( EnuZoom.Medium ), FmpConstant.getHexHeight( EnuZoom.Medium ) );
    m_rightLand.setUrlAndVisibleRect( base + "/tactic/"
        + m_wgtlayerEditLand.getRightClic().getImageName(), 0, 0, FmpConstant
        .getHexWidth( EnuZoom.Medium ), FmpConstant.getHexHeight( EnuZoom.Medium ) );

    m_txtLandWidth.setText( "" + ModelFmpMain.model().getGame().getLandWidth() );
    m_txtLandHeight.setText( "" + ModelFmpMain.model().getGame().getLandHeight() );
  }

  private void setClicTool(int p_button, LandType p_land)
  {
    String imageUrl = "images/board/"
        + ModelFmpMain.model().getGame().getPlanetType().getFolderName() + "/tactic/"
        + p_land.getImageName();
    if( p_button == Event.BUTTON_LEFT )
    {
      m_wgtlayerEditLand.setLeftClic( p_land );
      m_leftLand.setUrlAndVisibleRect( imageUrl, 0, 0, FmpConstant.getHexWidth( EnuZoom.Medium ),
          FmpConstant.getHexHeight( EnuZoom.Medium ) );
    }
    else
    {
      m_wgtlayerEditLand.setRightClic( p_land );
      m_rightLand.setUrlAndVisibleRect( imageUrl, 0, 0, FmpConstant.getHexWidth( EnuZoom.Medium ),
          FmpConstant.getHexHeight( EnuZoom.Medium ) );
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    int landWidth = Integer.parseInt( m_txtLandWidth.getText() );
    int landHeight = Integer.parseInt( m_txtLandHeight.getText() );
    if( p_sender == m_btnGenerate )
    {
      int percent = Integer.parseInt( m_txtLandPercent.getText() );
      GameGenerator.setSize( landWidth, landHeight );
      GameGenerator.setLandPercent( percent );
      GameGenerator.setHexagonMap( m_chkRoundMap.isChecked() );
      GameGenerator.generLands();
      ModelFmpMain.model().fireModelUpdate();
    }
    else if( p_sender == m_btnClear )
    {
      GameGenerator.setSize( landWidth, landHeight );
      GameGenerator.clearLand( m_wgtlayerEditLand.getLeftClic() );
      ModelFmpMain.model().fireModelUpdate();
    }
    else if( p_sender == m_btnLoadMap )
    {
      m_dlgLoadMap.show();
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseDown(Widget p_arg0, int p_arg1, int p_arg2)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseEnter(Widget p_arg0)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseLeave(Widget p_arg0)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseMove(Widget p_arg0, int p_arg1, int p_arg2)
  {
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseUp(Widget p_sender, int p_arg1, int p_arg2)
  {
    int button = DOM.eventGetButton( DOM.eventGetCurrentEvent() );
    DOM.eventPreventDefault( DOM.eventGetCurrentEvent() );
    if( p_sender == m_btnNone )
    {
      setClicTool( button, LandType.None );
    }
    else if( p_sender == m_btnSea )
    {
      setClicTool( button, LandType.Sea );
    }
    else if( p_sender == m_btnReef )
    {
      setClicTool( button, LandType.Reef );
    }
    else if( p_sender == m_btnMarsh )
    {
      setClicTool( button, LandType.Marsh );
    }
    else if( p_sender == m_btnPlain )
    {
      setClicTool( button, LandType.Plain );
    }
    else if( p_sender == m_btnMontain )
    {
      setClicTool( button, LandType.Montain );
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void onChange(Widget p_sender)
  {
    if( p_sender == m_txtLandPercent )
    {
      int percent = Integer.parseInt( m_txtLandPercent.getText() );
      if( percent > 100 )
      {
        percent = 100;
      }
      if( percent < 20 )
      {
        percent = 20;
      }
      m_txtLandPercent.setText( "" + percent );
    }
    else if( p_sender == m_lstPlanet )
    {
      ModelFmpMain.model().getGame()
          .setPlanetType( m_planets.get( m_lstPlanet.getSelectedIndex() ) );
      redraw();
      ModelFmpMain.model().fireModelUpdate();
    }

  }


}
