/**
 * 
 */
package com.fullmetalgalaxy.model;

/**
 * @author Vincent Legendre
 *
 */
public enum Sector
{
  North, NorthEast, SouthEast, South, SouthWest, NorthWest;


  public Sector getOposite()
  {
    switch( this )
    {
    default:
    case North:
      return South;
    case NorthEast:
      return SouthWest;
    case SouthEast:
      return NorthWest;
    case South:
      return North;
    case SouthWest:
      return NorthEast;
    case NorthWest:
      return SouthEast;
    }
  }

  public Sector getNext()
  {
    int index = ordinal() + 1;
    if( index >= values().length )
    {
      index = 0;
    }
    return values()[index];
  }

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static Sector getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }

  public static Sector getRandom()
  {
    return getFromOrdinal( (int)Math.floor( Math.random() * values().length ) );
  }

}
