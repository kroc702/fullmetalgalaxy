/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.fullmetalgalaxy.model.MyEnum;

/**
 * @author Kroc
 *
 */
public class EnuMark extends MyEnum
{
  public static final int HiLight = 0;
  public static final int Selection = 1;
  public static final int Foots = 2;
  public static final int Target = 3;
  public static final int DisableWater = 4;
  public static final int DisableFire = 5;
  public static final int Warning = 6;


  public EnuMark(int p_value)
  {
    super( p_value );
  }

  public EnuMark()
  {
    super();
  }

  protected int getMaxValue()
  {
    return Warning;
  }

  public String toString()
  {
    return getUrl();
  }

  public String getUrl()
  {
    switch( getValue() )
    {
    case EnuMark.HiLight:
      return "case_contours.gif";
    case EnuMark.Selection:
      return "selection.gif";
    case EnuMark.Foots:
      return "pas.gif";
    case EnuMark.Target:
      return "cible.gif";
    case EnuMark.DisableWater:
      return "disable_water.gif";
    case EnuMark.DisableFire:
      return "disable_fire.gif";
    case EnuMark.Warning:
      return "warning.gif";
    default:
      return "blank.gif";
    }
  }

}
