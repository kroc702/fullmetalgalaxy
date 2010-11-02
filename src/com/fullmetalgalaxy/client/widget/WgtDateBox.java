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
package com.fullmetalgalaxy.client.widget;

import java.util.Date;

import com.fullmetalgalaxy.client.ClientUtil;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Vincent Legendre
 *
 */
public class WgtDateBox extends WgtTextBox implements ChangeListener
{
  /**
   * 
   */
  public WgtDateBox()
  {
    super();
    addChangeListener( this );
    setScalarValue( new Date() );
  }

  public WgtDateBox(Date p_date)
  {
    super();
    addChangeListener( this );
    setScalarValue( p_date );
  }

  @Override
  public void setScalarValue(Object p_bean)
  {
    if( (p_bean != null) && (p_bean instanceof Date) )
    {
      setText( ClientUtil.s_dateTimeFormat.format( (Date)p_bean ) );
    }
    else
    {
      super.setScalarValue( p_bean );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.NumberView#getValue()
   */
  public Date getDate()
  {
    return ClientUtil.s_dateTimeFormat.parse( getText() );
  }

  @Override
  public Object getObject()
  {
    return getDate();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onChange(Widget p_sender)
  {
    if( p_sender == this )
    {
      try
      {
        getDate();
      } catch( IllegalArgumentException ex )
      {
        setScalarValue( new Date() );
      }
    }
  }


}
