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

package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.tabmenu.WgtIntBox;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent
 *
 */
public class WgtReserveToken extends Composite implements HasValueChangeHandlers<Integer>,
    HasValue<Integer>
{
  private VerticalPanel m_panel = new VerticalPanel();
  WgtIntBox m_tokenReserveCount = new WgtIntBox();

  public WgtReserveToken(TokenType p_tokenType)
  {
    this( p_tokenType, new EnuColor( EnuColor.None ) );
  }

  public WgtReserveToken(TokenType p_tokenType, EnuColor p_color)
  {
    m_panel.setHeight( "100%" );
    m_panel.add( new Label( Messages.getTokenString( 0, p_tokenType ) ) );
    Image wgtToken = null;
    if( p_tokenType.canBeColored() )
    {
      wgtToken = new Image( TokenImages.getTokenImage( p_color,
        EnuZoom.Medium, p_tokenType, Sector.SouthEast ) );
    }
    else
    {
      wgtToken = new Image( TokenImages.getTokenImage( new EnuColor( EnuColor.None ),
          EnuZoom.Medium, p_tokenType, Sector.SouthEast ) );
    }
    m_panel.add( wgtToken );

    m_tokenReserveCount.setMinValue( 0 );
    if( GameEngine.model().getGame().getConstructReserve().get( p_tokenType ) != null )
    {
      m_tokenReserveCount.setValue( GameEngine.model().getGame().getConstructReserve()
          .get( p_tokenType ) );
    }
    m_panel.add( m_tokenReserveCount );
    setEnabled( false );
    initWidget( m_panel );
  }

  public void setEnabled(boolean p_enabled)
  {
    m_tokenReserveCount.setEnabled( p_enabled );
  }

  public void setFocus(boolean p_focused)
  {
    m_tokenReserveCount.setFocus( p_focused );
  }

  @Override
  public Integer getValue()
  {
    return m_tokenReserveCount.getValue();
  }


  @Override
  public void setValue(Integer p_value)
  {
    m_tokenReserveCount.setValue( p_value );
  }


  @Override
  public void setValue(Integer p_value, boolean p_fireEvents)
  {
    m_tokenReserveCount.setValue( p_value );
  }


  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> p_handler)
  {
    return m_tokenReserveCount.addValueChangeHandler( p_handler );
  }

}
