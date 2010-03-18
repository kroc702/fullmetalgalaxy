/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.io.Serializable;

/**
 * @author Kroc
 * this class, is to use Enum like type with GWT.
 */
public class MyEnum implements Serializable
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
