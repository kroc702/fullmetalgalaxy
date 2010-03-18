/**
 * 
 */
package com.fullmetalgalaxy.client.creation;

/**
 * @author Vincent Legendre
 *
 */
public enum MapSize
{
  Small, Medium, Large, Custom;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static MapSize getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }


  public int getHexagonPerPlayer()
  {
    switch( this )
    {
    case Small:
      return 180;
    default:
    case Medium:
      return 213;
    case Large:
      return 283;
    }
  }


}
