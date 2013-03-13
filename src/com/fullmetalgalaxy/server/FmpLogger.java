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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
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
    System.err.println( p_message );
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
    error( p_exception.toString() );
  }

}
