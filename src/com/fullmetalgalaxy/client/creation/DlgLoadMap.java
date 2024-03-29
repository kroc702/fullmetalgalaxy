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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Vincent Legendre
 *
 */

public class DlgLoadMap extends DialogBox implements ClickHandler
{
  // UI
  private Map<Image, String> m_maps = new HashMap<Image, String>();
  private Button m_btnCancel = new Button( "Cancel" );
  private TextBox m_txtCustom = new TextBox();
  private Button m_btnCustom = new Button( "Custom" );
  private Panel m_panel = new FlowPanel();

  // model
  protected DlgLoadMap m_this = this;


  private FmpCallback<ModelFmpInit> m_callbackFmpInit = new FmpCallback<ModelFmpInit>()
  {
    @Override
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      if( p_result.getGame() != null )
      {
        GameEngine.model().getGame().setLandSize( p_result.getGame().getLandWidth(),
            p_result.getGame().getLandHeight() );
        GameEngine.model().getGame().setLands( p_result.getGame().getLands() );
        GameEngine.model().getGame().setPlanetType( p_result.getGame().getPlanetType() );
        GameEngine.model().getGame().setMapUri( p_result.getGame().getMapUri() );
        GameEngine.model().getGame().getSetToken().clear();
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      }
      m_this.hide();
    }
  };



  /**
   * 
   */
  public DlgLoadMap()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "Clickez sur la carte de votre choix" );

    m_btnCancel.addClickHandler( this );
    m_btnCustom.addClickHandler( this );
    redraw();
    setWidget( m_panel );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    super.show();
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    String gameId = null;
    if( p_event.getSource() == m_btnCancel )
    {
      this.hide();
      return;
    }
    else if( p_event.getSource() == m_btnCustom )
    {
      gameId = m_txtCustom.getText();
    }
    else
    {
      gameId = m_maps.get( p_event.getSource() );
    }
    AppMain.getRpcService().getModelFmpInit( gameId, m_callbackFmpInit );
  }


  protected void redraw()
  {
    m_panel.clear();
    m_maps = new HashMap<Image, String>();
    
    // add original map
    Image image = new Image( "/puzzles/original/icon.jpg" );
    //image.setPixelSize( 96, 64 );
    image.addClickHandler( this );
    m_maps.put( image, "/puzzles/original/model.bin" );
    m_panel.add( image );
    
    // add FMC map
    image = new Image( "/puzzles/fullmetalconquete/icon.jpg" );
    //image.setPixelSize( 96, 64 );
    image.addClickHandler( this );
    m_maps.put( image, "/puzzles/fullmetalconquete/model.bin" );
    m_panel.add( image );
    
    // add Madhya map
    image = new Image( "/puzzles/madhya/icon.jpg" );
    // image.setPixelSize( 96, 64 );
    image.addClickHandler( this );
    m_maps.put( image, "/puzzles/madhya/model.bin" );
    m_panel.add( image );

    // add TF22 map
    image = new Image( "/puzzles/tf22/icon.jpg" );
    // image.setPixelSize( 96, 64 );
    image.addClickHandler( this );
    m_maps.put( image, "/puzzles/tf22/model.bin" );
    m_panel.add( image );

    
    m_panel.add( new HTML("ou ID de la partie:<br/>") );
    m_panel.add( m_txtCustom );
    m_panel.add( m_btnCustom );
    m_panel.add( new HTML("<br/>") );
    m_panel.add( m_btnCancel );
  }
}
