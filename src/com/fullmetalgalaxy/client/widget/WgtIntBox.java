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

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Vincent Legendre
 *
 */
public class WgtIntBox extends WgtTextBox implements ChangeListener
{
  /**
   * 
   */
  public WgtIntBox()
  {
    super();
    addChangeListener( this );
    setScalarValue( new Integer( 0 ) );
  }

  public WgtIntBox(Number p_number)
  {
    super();
    addChangeListener( this );
    setScalarValue( p_number );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.NumberView#getValue()
   */
  public Number getNumber()
  {
    return Integer.parseInt( getText() );
  }

  @Override
  public Object getObject()
  {
    return getNumber();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void onChange(Widget p_sender)
  {
    if( p_sender == this )
    {
      try
      {
        getNumber().intValue();
      } catch( NumberFormatException ex )
      {
        setText( "0" );
      }
    }
  }


}
