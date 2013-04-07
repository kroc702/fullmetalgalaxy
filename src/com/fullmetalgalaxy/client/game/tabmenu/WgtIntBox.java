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

package com.fullmetalgalaxy.client.game.tabmenu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;

/**
 * @author Vincent
 * 
 * a simple widget that can be used to enter integer value.
 */
public class WgtIntBox extends Composite 
 implements HasValueChangeHandlers<Integer>,
    HasValue<Integer>,
    HasClickHandlers, HasDoubleClickHandlers, HasDropHandlers, HasFocusHandlers, HasKeyUpHandlers,
    HasKeyDownHandlers, HasMouseWheelHandlers
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private Button m_btnMinus = new Button("-");
  private Button m_btnPlus = new Button("+");
  private IntegerBox m_intBox = new IntegerBox();
  
  private int m_minValue = Integer.MIN_VALUE;
  private int m_maxValue = Integer.MAX_VALUE;

  
  public WgtIntBox()
  {
    super();
    m_btnMinus.setWidth( "30px" );
    m_btnMinus.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        setValue( getValue() - 1, true );
      }
    } );
    m_panel.add( m_btnMinus );
    m_intBox.setWidth( "100%" );
    m_intBox.addValueChangeHandler( new ValueChangeHandler<Integer>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<Integer> p_event)
      {
        setValue( getValue() );
      }
    } );
    m_intBox.addMouseWheelHandler( new MouseWheelHandler()
    {
      @Override
      public void onMouseWheel(MouseWheelEvent p_event)
      {
        setValue( getValue() + (p_event.getDeltaY() < 0 ? 1 : -1), true );
      }
    } );
    m_panel.add( m_intBox );
    m_btnPlus.setWidth( "30px" );
    m_btnPlus.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        setValue( getValue() + 1, true );
      }
    } );
    m_panel.add( m_btnPlus );
    initWidget( m_panel );
  }


  public void setMinValue(int p_minValue)
  {
    m_minValue = p_minValue;
  }


  public void setMaxValue(int p_maxValue)
  {
    m_maxValue = p_maxValue;
  }


  /**
   * Always return a value.
   * @return zero if string can't be parsed
   */
  @Override
  public Integer getValue()
  {
    Integer value = m_intBox.getValue();
    if( value == null )
      return 0;
    return value;
  }

  @Override
  public void setValue(Integer p_value)
  {
    setValue( p_value, false );
  }

  @Override
  public void setValue(Integer p_value, boolean p_fireEvents)
  {
    if( p_value < m_minValue )
      m_intBox.setValue( m_minValue, p_fireEvents );
    else if( p_value > m_maxValue )
      m_intBox.setValue( m_maxValue, p_fireEvents );
    else
      m_intBox.setValue( p_value, p_fireEvents );
  }


  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> p_handler)
  {
    return m_intBox.addValueChangeHandler( p_handler );
  }


  @Override
  public HandlerRegistration addClickHandler(ClickHandler p_handler)
  {
    return m_intBox.addClickHandler( p_handler );
  }


  @Override
  public HandlerRegistration addDoubleClickHandler(DoubleClickHandler p_handler)
  {
    return m_intBox.addDoubleClickHandler( p_handler );
  }


  @Override
  public HandlerRegistration addDropHandler(DropHandler p_handler)
  {
    return m_intBox.addDropHandler( p_handler );
  }


  @Override
  public HandlerRegistration addFocusHandler(FocusHandler p_handler)
  {
    return m_intBox.addFocusHandler( p_handler );
  }


  @Override
  public HandlerRegistration addKeyDownHandler(KeyDownHandler p_handler)
  {
    return m_intBox.addKeyDownHandler( p_handler );
  }


  @Override
  public HandlerRegistration addKeyUpHandler(KeyUpHandler p_handler)
  {
    return m_intBox.addKeyUpHandler( p_handler );
  }


  @Override
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler p_handler)
  {
    return m_intBox.addMouseWheelHandler( p_handler );
  }

  public boolean isEnabled()
  {
    return m_intBox.isEnabled();
  }


  public void setEnabled(boolean p_enabled)
  {
    m_btnMinus.setVisible( p_enabled );
    m_btnPlus.setVisible( p_enabled );
    m_intBox.setEnabled( p_enabled );
  }


  public void setFocus(boolean p_focused)
  {
    m_intBox.setFocus( p_focused );
  }




  
}
