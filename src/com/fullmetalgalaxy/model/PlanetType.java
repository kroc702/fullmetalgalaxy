/**
 * 
 */
package com.fullmetalgalaxy.model;

/**
 * @author Vincent Legendre
 * these enum represent different kind of landscape
 */
public enum PlanetType
{
  Desert, Grass, Ice, Lava;

  public String getFolderName()
  {
    switch( this )
    {
    default:
    case Desert:
      return "desert";
    case Grass:
      return "grass";
    case Ice:
      return "ice";
    case Lava:
      return "lava";
    }
  }
}
