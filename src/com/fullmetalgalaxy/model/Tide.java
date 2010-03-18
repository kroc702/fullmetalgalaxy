/**
 * 
 */
package com.fullmetalgalaxy.model;




/**
 * @author Kroc
 *
 */
public enum Tide
{

  Unknown, Low, Medium, Hight;

  public static Tide getRandom()
  {
    switch( (int)Math.floor( Math.random() * 3 ) )
    {
    case 0:
      return Low;
    default:
    case 1:
      return Medium;
    case 2:
      return Hight;
    }
  }


}
