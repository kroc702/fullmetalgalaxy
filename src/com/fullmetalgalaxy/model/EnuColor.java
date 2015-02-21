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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import com.fullmetalgalaxy.model.ressources.Messages;

/**
 * @author Kroc
 * EnuColor is a bit field of colors.
 */
public class EnuColor extends MyEnum
{
  static final long serialVersionUID = 16;

  public static final int None = 0;
  public static final int Blue = 1;
  public static final int Cyan = 2;
  public static final int Olive = 4;
  public static final int Orange = 8;
  public static final int Red = 16;
  public static final int Green = 32;
  public static final int Purple = 64;
  public static final int Yellow = 128;
  public static final int Grey = 256;
  public static final int Brown = 512;
  public static final int White = 1024;
  public static final int Pink = 2048;
  public static final int Camouflage = 4096;
  public static final int Zebra = 8192;
  public static final int Pantera = 16384;
  public static final int Lightning = 32768;


  public EnuColor(int p_value)
  {
    super( p_value );
  }

  public EnuColor()
  {
    super();
  }

  @Override
  public int getMaxValue()
  {
    return None + Blue + Cyan + Olive + Orange + Red + Green + Purple + Yellow + Grey + Brown + White + Pink + Camouflage + Zebra + Pantera + Lightning;
  }

  public static int getMaxColorValue()
  {
    return None + Blue + Cyan + Olive + Orange + Red + Green + Purple + Yellow + Grey + Brown + White + Pink + Camouflage + Zebra + Pantera + Lightning;
  }

  /**
   * @return the number of possible single color.
   */
  public static int getTotalNumberOfColor()
  {
    return 16;
  }

  /**
   * Warning: no i18n !
   * 
   * this method can't be internationalized, as its returned value is used in url.
   */
  @Override
  public String toString()
  {
    if( isSingleColor() || getValue() == EnuColor.None)
      return singleColorToString(getValue());
      
    if( getValue() == EnuColor.Unknown )
    {
      return Messages.getSingleColorString( 0, EnuColor.Unknown );
    }

    String str = "";
    boolean isEmpty = true;

    for(int color=1; color<=getMaxColorValue(); color*=2)
    {
      if( isColored( color ) )
      {
        str += Messages.getSingleColorString( 0, color );
        isEmpty = false;
      }
    }
 
    if( isEmpty )
    {
      str = Messages.getSingleColorString( 0, EnuColor.None );
    }
    return str;
  }

  /**
   * 
   * @param p_colorValue have to be a single color value.
   * @return returned value is compliant with local url. so no i18n
   */
  public static String singleColorToString(int p_colorValue)
  {
    switch( p_colorValue )
    {
    case EnuColor.Blue:
      return "blue";
    case EnuColor.Cyan:
      return "cyan";
    case EnuColor.Grey:
      return "grey";
    case EnuColor.Yellow:
      return "yellow";
    case EnuColor.Olive:
      return "olive";
    case EnuColor.Red:
      return "red";
    case EnuColor.Orange:
      return "orange";
    case EnuColor.Green:
      return "green";
    case EnuColor.Purple:
      return "purple";
    case EnuColor.Brown:
      return "brown";
    case EnuColor.Camouflage:
      return "camouflage";
    case EnuColor.Lightning:
      return "lightning";
    case EnuColor.Pantera:
      return "pantera";
    case EnuColor.Pink:
      return "pink";
    case EnuColor.White:
      return "white";
    case EnuColor.Zebra:
      return "zebra";
    case EnuColor.None:
      return "colorless";
    case EnuColor.Unknown:
    default:
      return "unknown";
    }
  }


  /**
   * 
   * @return true if p_colorValue is one and only one color.
   */
  public boolean isSingleColor()
  {
    return isSingleColor( getValue() );
  }

  /**
   * @param p_colorValue have to be a valid color value.
   * @return true if p_colorValue is one and only one color.
   *         false if p_colorValue is no color, a combinaison of several color. (or invalid color)
   */
  public static boolean isSingleColor(int p_colorValue)
  {
    switch( p_colorValue )
    {
    case EnuColor.Blue:
    case EnuColor.Cyan:
    case EnuColor.Grey:
    case EnuColor.Yellow:
    case EnuColor.Olive:
    case EnuColor.Red:
    case EnuColor.Orange:
    case EnuColor.Green:
    case EnuColor.Purple:
    case EnuColor.Brown:
    case EnuColor.White:
    case EnuColor.Pink:
    case EnuColor.Camouflage:
    case EnuColor.Pantera:
    case EnuColor.Zebra:
    case EnuColor.Lightning:
      return true;
    case EnuColor.None:
    case EnuColor.Unknown:
    default:
      return false;
    }
  }

  public static EnuColor getColorFromIndex(int p_index)
  {
    switch( p_index )
    {
    case 0:
      return new EnuColor( EnuColor.Blue );
    case 1:
      return new EnuColor( EnuColor.Cyan );
    case 2:
      return new EnuColor( EnuColor.Grey );
    case 3:
      return new EnuColor( EnuColor.Yellow );
    case 4:
      return new EnuColor( EnuColor.Olive );
    case 5:
      return new EnuColor( EnuColor.Red );
    case 6:
      return new EnuColor( EnuColor.Orange );
    case 7:
      return new EnuColor( EnuColor.Green );
    case 8:
      return new EnuColor( EnuColor.Purple );
    case 9:
      return new EnuColor( EnuColor.Brown );
    case 10:
      return new EnuColor( EnuColor.White );
    case 11:
      return new EnuColor( EnuColor.Pink );
    case 12:
      return new EnuColor( EnuColor.Camouflage );
    case 13:
      return new EnuColor( EnuColor.Zebra );
    case 14:
      return new EnuColor( EnuColor.Pantera );
    case 15:
      return new EnuColor( EnuColor.Lightning );
    default:
      return new EnuColor( EnuColor.None );
    }
  }

  public int getColorIndex()
  {
    switch( getValue() )
    {
    case EnuColor.Blue:
      return 0;
    case EnuColor.Cyan:
      return 1;
    case EnuColor.Grey:
      return 2;
    case EnuColor.Yellow:
      return 3;
    case EnuColor.Olive:
      return 4;
    case EnuColor.Red:
      return 5;
    case EnuColor.Orange:
      return 6;
    case EnuColor.Green:
      return 7;
    case EnuColor.Purple:
      return 8;
    case EnuColor.Brown:
      return 9;
    case EnuColor.White:
      return 10;
    case EnuColor.Pink :
      return 11;
    case EnuColor.Camouflage:
      return 12;
    case EnuColor.Zebra:
      return 13;
    case EnuColor.Pantera:
      return 14;
    case EnuColor.Lightning:
      return 15;
    case EnuColor.None:
    case EnuColor.Unknown:
    default:
      return -1;
    }
  }


  /**
   * 
   * @return one random single color such this.isColored(this.getSingleColor()) == true
   */
  public EnuColor getSingleColor()
  {
    EnuColor color = null;
    for( int iColor = 0; iColor < getTotalNumberOfColor(); iColor++ )
    {
      color = getColorFromIndex( iColor );
      if( isColored( color ) )
      {
        return color;
      }
    }
    return new EnuColor( EnuColor.None );
  }

  /**
   * @return return an EnuColor colored by all colors except theses contained by this.
   */
  public EnuColor getAllOtherColor()
  {
    return new EnuColor( getValue() ^ getMaxValue() );
  }

  /**
   * @param p_color
   * @return true if this and p_color have at least on common color.
   */
  public boolean contain(EnuColor p_color)
  {
    return contain( p_color.getValue() );
  }

  /**
   * @param p_colorValue have to be a valid color value.
   * @return true if this and p_colorValue have at least on common color.
   */
  public boolean contain(int p_colorValue)
  {
    return (getValue() & p_colorValue) != 0;
  }

  /**
   * @param p_color
   * @return true if color contain all colors contained in p_colorValue.
   */
  public boolean isColored(EnuColor p_color)
  {
    return isColored( p_color.getValue() );
  }

  /**
   * Warning: this method don't work if <b>this</b> is unknown.
   * @param p_colorValue have to be a valid color value.
   * @return true if color contain all colors contained in p_colorValue.
   */
  public boolean isColored(int p_colorValue)
  {
    return (getValue() & p_colorValue) == p_colorValue;
  }

  /**
   * add to this EnuColor all colors contained in p_colorValue.
   * @param p_colorValue
   */
  public void addColor(EnuColor p_color)
  {
    addColor( p_color.getValue() );
  }

  /**
   * add to this EnuColor all colors contained in p_colorValue.
   * @param p_colorValue
   */
  public void addColor(int p_colorValue)
  {
    setValue( addColor( getValue(), p_colorValue ) );
  }

  public static int addColor(int p_colorValue, int p_colorToAdd)
  {
    return p_colorValue | p_colorToAdd;
  }

  /**
   * remove from this EnuColor all colors contained in p_colorValue.
   * @param p_color
   */
  public void removeColor(EnuColor p_color)
  {
    removeColor( p_color.getValue() );
  }

  /**
   * remove from this EnuColor all colors contained in p_colorValue.
   * @param p_colorValue
   */
  public void removeColor(int p_colorValue)
  {
    setValue( removeColor( getValue(), p_colorValue ) );
  }

  /**
   * 
   * @param p_colorValue
   * @param p_colorToRemove
   * @return the color value result
   */
  public static int removeColor(int p_colorValue, int p_colorToRemove)
  {
    return p_colorValue & (p_colorToRemove ^ getMaxColorValue());
  }

  /**
   * return the number of single color.
   */
  public int getNbColor()
  {
    int nb = 0;
    
    for(int color=1; color<=getMaxColorValue(); color*=2)
    {
      if( isColored( color ) )
        nb++;
    }
    return nb;
  }

};
