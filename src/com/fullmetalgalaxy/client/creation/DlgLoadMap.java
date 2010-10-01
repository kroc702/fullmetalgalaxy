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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Vincent Legendre
 *
 */

public class DlgLoadMap extends DialogBox implements ClickHandler
{
  // UI
  private Map<Image, Long> m_maps = new HashMap<Image, Long>();
  private Button m_btnCancel = new Button( "Cancel" );
  private Panel m_panel = new FlowPanel();

  // model
  private List<EbGamePreview> m_gameList = null;
  private GameFilter m_filter = new GameFilter();
  protected DlgLoadMap m_this = this;

  private FmpCallback<List<EbGamePreview>> m_callbackGameList = new FmpCallback<List<EbGamePreview>>()
  {
    @Override
    public void onSuccess(List<EbGamePreview> p_result)
    {
      super.onSuccess( p_result );
      m_gameList = p_result;
      redraw();
    }
  };


  private FmpCallback<ModelFmpInit> m_callbackFmpInit = new FmpCallback<ModelFmpInit>()
  {
    @Override
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      ModelFmpMain.model().getGame().setLandSize( p_result.getGame().getLandWidth(),
          p_result.getGame().getLandHeight() );
      ModelFmpMain.model().addAllAccounts( p_result.getMapAccounts() );
      ModelFmpMain.model().getGame().setLands( p_result.getGame().getLands() );
      ModelFmpMain.model().getGame().setPlanetType( p_result.getGame().getPlanetType() );
      ModelFmpMain.model().getGame().getSetToken().clear();
      ModelFmpMain.model().fireModelUpdate();
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
    redraw();
    m_filter.reinit();
    m_filter.setStatus( GameStatus.Scenario );
    setWidget( m_panel );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    super.show();
    Services.Util.getInstance().getGameList( m_filter, m_callbackGameList );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnCancel )
    {
      this.hide();
      return;
    }
    Long gameId = m_maps.get( p_event.getSource() );
    Services.Util.getInstance().getModelFmpInit( gameId.toString(), m_callbackFmpInit );
  }


  protected void redraw()
  {
    m_panel.clear();
    m_maps = new HashMap<Image, Long>();
    if( m_gameList != null )
    {
      for( EbGamePreview game : m_gameList )
      {
        Image image = new Image( FmpConstant.getMiniMapUrl( "" + game.getId() ) );
        image.setPixelSize( 96, 64 );
        image.addClickHandler( this );
        m_maps.put( image, game.getId() );
        m_panel.add( image );
      }
    }
    m_panel.add( m_btnCancel );
  }
}