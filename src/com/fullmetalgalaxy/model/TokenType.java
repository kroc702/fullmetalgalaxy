package com.fullmetalgalaxy.model;


/**
 * @author LEG88888
 *
 */
public enum TokenType
{
  /**
   * Theses values are store as is in data base.
   */
  Freighter, Turret, Barge, Speedboat, Tank, Heap, Crab, WeatherHen, Pontoon, Ore, None;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static TokenType getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }


}
