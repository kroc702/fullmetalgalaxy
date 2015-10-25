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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.tools;

import java.io.InputStream;
import java.io.OutputStream;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.server.FmpLogger;

/**
 * @author vlegendr
 *
 */
public class DriverEmpty extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverEmpty.class.getName() );
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#loadGame(java.io.InputStream)
   */
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    LOG.error( "unimplemented" );
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#saveGame(com.fullmetalgalaxy.model.ModelFmpInit, java.io.OutputStream)
   */
  @Override
  public void saveGame(ModelFmpInit p_game, OutputStream p_output)
  {
    LOG.error( "unimplemented" );
  }

}
