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
package com.fullmetalgalaxy.client.game.context;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.BoardFireCover;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * display the selected token informations
 */
public class WgtContextLand extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();
  private long m_actionLastUpdate = 0;

  /**
   * 
   */
  public WgtContextLand()
  {
    super();
    m_panel.setSize( "100%", "100%" );
    initWidget( m_panel );
  }


  public void redraw()
  {
    assert GameEngine.model() != null;
    EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
    if( actionBuilder.getLastUpdate().getTime() != m_actionLastUpdate )
    {
      assert GameEngine.model() != null;
      m_panel.clear();
      LandType land2Display = GameEngine.model().getGame()
          .getLand( actionBuilder.getSelectedPosition() );

      // int pxHexWidth = FmpConstant.getHexWidth( EnuZoom.Medium );
      int pxHexHeight = FmpConstant.getHexHeight( GameEngine.model().getZoomDisplayed() );

      // display land itself
      String landStr = Messages.getLandString( 0, land2Display );
      m_panel.add( new HTML( landStr ) );
      m_panel.add( new HTML( "<div style=\"height: " + pxHexHeight + "px;\" class=\"fmp-"
          + land2Display + "\"></div>" ) );
      
      // display firecover
      BoardFireCover firecover = GameEngine.model().getGame().getBoardFireCover();
      int x = actionBuilder.getSelectedPosition().getX();
      int y = actionBuilder.getSelectedPosition().getY();
      for( int iColor=0; iColor<EnuColor.getTotalNumberOfColor(); iColor++ )
      {
        EnuColor color = EnuColor.getColorFromIndex( iColor );
        byte fireCount = firecover.getFireCover( x, y, color );
        byte disableFireCount = firecover.getDisabledFireCover( x, y, color );
        if( fireCount != 0 || disableFireCount != 0 )
        {
          m_panel.add( new HTML( "couverture de feu "
              + Messages.getColorString( 0, color.getValue() ) + ": " + fireCount + " (+"
              + disableFireCount + ")" ) );
        }
      }
    }
  }
}
