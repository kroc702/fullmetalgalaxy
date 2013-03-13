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
package com.fullmetalgalaxy.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Kroc
 * this class, is to use Enum like type with GWT.
 */
public class MyEnum implements Serializable, IsSerializable
{
  static final long serialVersionUID = 20;

  public static final int Unknown = -1;


  private int m_value = Unknown;

  public MyEnum()
  {
    m_value = Unknown;
  }

  public MyEnum(int p_value)
  {
    setValue( p_value );
  }

  /**
   * @return the value
   */
  public int getValue()
  {
    return m_value;
  }

  /**
   * @param p_value the value to set
   */
  public void setValue(int p_value)
  {
    m_value = p_value;
    if( p_value < 0 || p_value > this.getMaxValue() )
    {
      m_value = Unknown;
    }
  }

  @Override
  public String toString()
  {
    switch( getValue() )
    {
    case Unknown:
      return "Unknown";
    default:
      return "Unknown (" + getValue() + ")";
    }
  }

  /**
   * this method have to be override by any child class.
   * @return the maximum value this enum can took
   */
  protected int getMaxValue()
  {
    return -1;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + m_value;
    return result;
  }


  public boolean equals(MyEnum obj)
  {
    return getValue() == obj.getValue();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    MyEnum other = null;
    try
    {
      other = (MyEnum)obj;
    } catch( Throwable th )
    {
      return super.equals( obj );
    }
    return equals( other );
  }


}
