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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.creation;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.WgtTokenQty;
import com.fullmetalgalaxy.model.TokenType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent
 *
 */
public class WgtEditReserve extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();
  private Grid m_grid = new Grid( 3, 5 );

  public WgtEditReserve()
  {
    m_panel.add( new Label( MAppGameCreation.s_messages.tipReserve() ) );
    Button btnReinit = new Button( MAppGameCreation.s_messages.defaultValue() );
    btnReinit.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        // reset to default value
        GameEngine.model().getGame().setConstructReserve( null );
        onTabSelected();
      }
    } );
    m_panel.add( btnReinit );

    m_panel.add( m_grid );

    initWidget( m_panel );
  }


  private Widget createWgt(final TokenType p_tokenType)
  {
    WgtTokenQty wgt = new WgtTokenQty( p_tokenType, GameEngine.model().getGame()
        .getConstructReserve().get( p_tokenType ) );

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
    m_grid.clear();
    m_grid.setWidget( 0, 0, createWgt( TokenType.Pontoon ) );
    m_grid.setWidget( 0, 1, createWgt( TokenType.Sluice ) );
    m_grid.setWidget( 1, 0, createWgt( TokenType.Crab ) );
    m_grid.setWidget( 1, 1, createWgt( TokenType.Crayfish ) );
    m_grid.setWidget( 1, 2, createWgt( TokenType.Barge ) );
    m_grid.setWidget( 1, 3, createWgt( TokenType.WeatherHen ) );
    m_grid.setWidget( 2, 0, createWgt( TokenType.Tank ) );
    m_grid.setWidget( 2, 1, createWgt( TokenType.Hovertank ) );
    m_grid.setWidget( 2, 2, createWgt( TokenType.Speedboat ) );
    m_grid.setWidget( 2, 3, createWgt( TokenType.Heap ) );
    m_grid.setWidget( 2, 4, createWgt( TokenType.Tarask ) );

  }
}
