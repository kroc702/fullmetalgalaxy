/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * @author Vincent Legendre
 * read configs files
 */
public class FmpConfig
{
  protected static final String PROPERTIES_FILE_LOCATION = "/WEB-INF/classes/config.properties";
  private static String s_currentPropertiesFileLocation = null;
  private static Properties s_properties = new Properties();


  public static void saveProperties() throws Exception
  {
    throw new Exception( "since GAE not suported anymore" );
  }

  public static boolean isLoaded()
  {
    return s_currentPropertiesFileLocation != null;
  }

  public static void loadProperties(String p_propertiesFileLocation)
  {
    try
    {
      s_currentPropertiesFileLocation = p_propertiesFileLocation;
      s_properties.load( new FileInputStream( s_currentPropertiesFileLocation ) );
    } catch( FileNotFoundException e )
    {
      e.printStackTrace();
    } catch( IOException e )
    {
      e.printStackTrace();
    }
  }


  /**
   * @param p_key
   * @return
   * @see java.util.Properties#getProperty(java.lang.String)
   */
  public static String getStringProperty(String p_key)
  {
    return s_properties.getProperty( p_key );
  }

  public static int getIntProperty(String p_key)
  {
    try
    {
      return Integer.parseInt( getStringProperty( p_key ) );
    } catch( NumberFormatException e )
    {
      // e.printStackTrace();
    }
    return 0;
  }


  /**
   * @return
   * @see java.util.Hashtable#keySet()
   */
  public static Set<Object> keySet()
  {
    return s_properties.keySet();
  }


  /**
   * @param p_key
   * @param p_value
   * @return
   * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
   */
  public static void setProperty(String p_key, String p_value)
  {
    s_properties.setProperty( p_key, p_value );
  }


}
