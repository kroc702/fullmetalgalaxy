package com.fullmetalgalaxy.server.serialization;

/**
 * tiny factory to select a file driver according to format.
 * @author Vincent
 *
 */
public class DriverFactory
{
  public static DriverFileFormat get(String format)
  {
    if( format != null )
    {
      switch( format )
      {
      case "xml":
        return new DriverXML();
      case "fmp":
        return new DriverFMP();
      case "stai":
        return new DriverSTAI();
      }
    }
    return new DriverBin();
  }
}
