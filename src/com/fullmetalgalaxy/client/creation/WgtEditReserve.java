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

package com.fullmetalgalaxy.client.creation;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.WgtReserveToken;
import com.fullmetalgalaxy.model.TokenType;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent
 *
 */
public class WgtEditReserve extends Composite
{
  private Grid m_panel = new Grid( 3, 5 );

  public WgtEditReserve()
  {
    
    

    initWidget( m_panel );
  }


  private Widget createWgt(final TokenType p_tokenType)
  {
    WgtReserveToken wgt = new WgtReserveToken( p_tokenType );

    wgt.setEnabled( true );
    wgt.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        GameEngine.model().getGame().setConstructQty( p_tokenType, p_event.getValue() );
      }
    } );
    return wgt;
  }

  public void onTabSelected()
  {
    m_panel.clear();
    m_panel.setWidget( 0, 0, createWgt( TokenType.Pontoon ) );
    m_panel.setWidget( 0, 1, createWgt( TokenType.Sluice ) );
    m_panel.setWidget( 1, 0, createWgt( TokenType.Crab ) );
    m_panel.setWidget( 1, 1, createWgt( TokenType.Crayfish ) );
    m_panel.setWidget( 1, 2, createWgt( TokenType.Barge ) );
    m_panel.setWidget( 1, 3, createWgt( TokenType.WeatherHen ) );
    m_panel.setWidget( 2, 0, createWgt( TokenType.Tank ) );
    m_panel.setWidget( 2, 1, createWgt( TokenType.Hovertank ) );
    m_panel.setWidget( 2, 2, createWgt( TokenType.Speedboat ) );
    m_panel.setWidget( 2, 3, createWgt( TokenType.Heap ) );
    m_panel.setWidget( 2, 4, createWgt( TokenType.Tarask ) );

  }
}
