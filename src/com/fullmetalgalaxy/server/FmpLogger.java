/**
 * 
 */
package com.fullmetalgalaxy.server;

/**
 * @author Vincent
 * this class is a logger to be able to change log behavior easily
 */
public class FmpLogger
{
  public static FmpLogger getLogger(String p_name)
  {
    return new FmpLogger( p_name );
  }

  public FmpLogger(String p_name)
  {

  }


  public void severe(String p_message)
  {
    System.err.println( p_message );
  }

  public void warning(String p_message)
  {
    System.err.println( p_message );
  }

  public void info(String p_message)
  {
    // System.err.println( p_message );
  }

  public void fine(String p_message)
  {
    // System.err.println( p_message );
  }

  public void finer(String p_message)
  {
    // System.err.println( p_message );
  }

  public void finest(String p_message)
  {
    // System.err.println( p_message );
  }

  // non standard
  public void error(String p_message)
  {
    severe( p_message );
  }

  public void error(Exception p_exception)
  {
    error( p_exception.getMessage() );
  }

}
