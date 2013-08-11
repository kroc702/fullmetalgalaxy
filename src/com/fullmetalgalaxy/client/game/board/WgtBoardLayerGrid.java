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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;


/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerGrid extends WgtBoardLayerBase
{

  /**
   * 
   */
  public WgtBoardLayerGrid()
  {
    setStyleName( "fmp-grid-tactic" );
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    String odd = "";
    if(m_cropLeftHex%2 != 0) odd = "-odd";
    switch( p_zoom.getValue() )
    {
    default:
    case EnuZoom.Medium:
      setStyleName( "fmp-grid-tactic"+odd );
      break;
    case EnuZoom.Small:
      setStyleName( "fmp-grid-strategy"+odd );
      break;
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    if( isVisible() != GameEngine.model().isGridDisplayed() )
    {
      setVisible( GameEngine.model().isGridDisplayed() );
    }
  }



  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    super.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );
    setZoom(GameEngine.model().getZoomDisplayed());
  }



  @SuppressWarnings("unused")
  private static int s_firstGridRuleIndex = createGridRules();

  public static int createGridRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-grid-tactic",
        "{background: url(images/board/"+GameEngine.model().getGame().getPlanetType().getFolderName()+"/tactic/grid.gif);}" ) - 1;
    ClientUtil.setCssRule( ".fmp-grid-tactic-odd",
        "{background: url(images/board/"+GameEngine.model().getGame().getPlanetType().getFolderName()+"/tactic/grid.gif) "
            +(FmpConstant.getHexWidth(EnuZoom.Medium)*3/4)+"px 0px;}" );
    ClientUtil.setCssRule( ".fmp-grid-strategy",
        "{background: url(images/board/"+GameEngine.model().getGame().getPlanetType().getFolderName()+"/strategy/grid.gif);}" );
    ClientUtil.setCssRule( ".fmp-grid-strategy-odd",
        "{background: url(images/board/"+GameEngine.model().getGame().getPlanetType().getFolderName()+"/strategy/grid.gif) "
        +(FmpConstant.getHexWidth(EnuZoom.Small)*3/4)+"px 0px;}" );
    return oldLength;
  }
}
