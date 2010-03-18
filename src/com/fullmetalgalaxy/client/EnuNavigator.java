/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.fullmetalgalaxy.model.MyEnum;

/**
 * @author Vincent Legendre
 *
 */
public class EnuNavigator extends MyEnum
{
  public static final int FF = 0;
  public static final int IE = 1;

  /**
   * 
   */
  public EnuNavigator()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param p_value
   */
  public EnuNavigator(int p_value)
  {
    super( p_value );
    // TODO Auto-generated constructor stub
  }

  protected int getMaxValue()
  {
    return 1;
  }


}
