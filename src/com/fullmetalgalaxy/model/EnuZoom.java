package com.fullmetalgalaxy.model;


/**
 * @author LEG88888
 *
 */
public class EnuZoom extends MyEnum
{
  static final long serialVersionUID = 16;

  public static final int Small = 0;
  public static final int Medium = 1;
  public static final int Large = 2;


  public EnuZoom(int p_value)
  {
    super( p_value );
  }

  public EnuZoom()
  {
    super();
  }

  @Override
  protected int getMaxValue()
  {
    return 2;
  }

  @Override
  public String toString()
  {
    switch( getValue() )
    {
    case EnuZoom.Small:
      return "strategy";
    case EnuZoom.Medium:
      return "tactic";
    case EnuZoom.Large:
      return "large";
    case EnuZoom.Unknown:
    default:
      return super.toString();
    }
  }

  public String getUrl()
  {
    return "";
  }
}
