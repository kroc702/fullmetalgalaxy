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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 * display the curently selected token and hight light hexagon
 */
public class WgtBoardLayerSelect extends WgtBoardLayerBase
{
  protected Image m_hexagonHightlight = new Image();

  protected Image m_hexagonSelect = new Image();

  /**
   * last update of the currently displayed action
   */
  protected long m_actionLastUpdate = 0;

  /**
   * 
   */
  public WgtBoardLayerSelect()
  {
    super();
    BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( m_hexagonSelect );
    add( m_hexagonSelect, 0, 0 );
    m_hexagonSelect.setVisible( false );
    getHighLightImage().applyTo( m_hexagonHightlight );
    add( m_hexagonHightlight, 0, 0 );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    // TODO Auto-generated method stub
    super.onModelChange( p_forceRedraw );
    EventsPlayBuilder action = ModelFmpMain.model().getActionBuilder();
    if( action.getLastUpdate().getTime() != m_actionLastUpdate || p_forceRedraw )
    {
      redrawAction();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    getHighLightImage().applyTo( m_hexagonHightlight );
    BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( m_hexagonSelect );
    redrawAction();
  }

  private AbstractImagePrototype getHighLightImage()
  {
    EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
    if( actionBuilder.getSelectedAction() != null
        && (actionBuilder.getSelectedAction().getType() == GameLogType.EvtLand || actionBuilder
            .getSelectedAction().getType() == GameLogType.EvtDeployment) )
    {
      AnEventPlay action = (AnEventPlay)actionBuilder.getSelectedAction();
      return TokenImages.getTokenImage( action.getToken( ModelFmpMain.model().getGame() ),
          getZoom().getValue() );
    }
    return BoardIcons.hightlight_hexagon( getZoom().getValue() );
  }

  /**
   * redraw the full action layer.  
   */
  protected void redrawAction()
  {
    EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
    m_actionLastUpdate = actionBuilder.getLastUpdate().getTime();

    if( actionBuilder.isBoardTokenSelected() )
    {
      m_hexagonSelect.setVisible( true );
      setWidgetHexPosition( m_hexagonSelect, actionBuilder.getSelectedPosition() );
    }
    else
    {
      getHighLightImage().applyTo( m_hexagonHightlight );
      m_hexagonSelect.setVisible( false );
    }
  }


  public void moveHightLightHexagon(AnBoardPosition p_anBoardPosition)
  {
    AnEventPlay evDeploy = ModelFmpMain.model().getActionBuilder().getSelectedAction();
    if( evDeploy != null && evDeploy.getType() == GameLogType.EvtDeployment )
    {
      int distance = evDeploy.getToken( ModelFmpMain.model().getGame() ).getCarrierToken()
          .getPosition().getHexDistance( p_anBoardPosition );
      if( distance > ModelFmpMain.model().getGame().getEbConfigGameVariant().getDeploymentRadius() )
      {
        BoardIcons.hightlight_hexagon( getZoom().getValue() ).applyTo( m_hexagonHightlight );
      }
      else
      {
        getHighLightImage().applyTo( m_hexagonHightlight );
      }
    }
    setWidgetHexPosition( m_hexagonHightlight, p_anBoardPosition );
  }

  public void setHexagonHightVisible(boolean p_visible)
  {
    m_hexagonHightlight.setVisible( p_visible );
  }


}
