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
package com.fullmetalgalaxy.client.game.tabmenu;

import java.util.Map.Entry;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.WgtTokenQty;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.TokenType;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

/**
 * @author vlegendr
 *
 */
public class WgtConstructReserve extends Composite
{
  private Grid m_panel = new Grid( 5, 3 );

  /**
   * 
   */
  public WgtConstructReserve()
  {
    initWidget( m_panel );
    redraw();
  }

 
  
  public void redraw()
  {
    int iRow = 0;
    int iCol = 0;
    m_panel.clear();


    EnuColor myColor = new EnuColor( 0 );
    if( GameEngine.model().getMyRegistration() != null )
    {
      myColor.setValue( GameEngine.model().getMyRegistration()
          .getTeam( GameEngine.model().getGame() ).getFireColor() );
    }
    
    for( Entry<TokenType, Integer> entry : GameEngine.model().getGame().getConstructReserve()
        .entrySet() )
    {
      WgtTokenQty wgt = new WgtTokenQty( entry.getKey(), myColor, GameEngine.model()
          .getGame().getConstructReserve().get( entry.getKey() ) );
      m_panel.setWidget( iRow, iCol, wgt );
      iCol++;
      if( iCol >= m_panel.getColumnCount() )
      {
        iCol = 0;
        iRow++;
      }
    }
  }
  
}
