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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.FmpLogger;

/**
 * @author vlegendr
 *
 */
public class DriverBin extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverBin.class.getName() );
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#loadGame(java.io.InputStream)
   */
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    ObjectInputStream in = null;
    ModelFmpInit model = null;
    try
    {
      in = new ObjectInputStream( p_input );
      model = game2Model( in.readObject() );
      in.close();
    } catch( Exception ex )
    {
      ex.printStackTrace();
    }
    if( model == null )
    {
      LOG.error( "no game loaded" );
    }
    else if( model.getGame() != null )
    {
      // call the post load method to fix old data
      model.getGame().getPreview().onLoad();
      model.setGame( new Game( model.getGame().getPreview(), model.getGame().getData() ) );
    }
    return model;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#saveGame(com.fullmetalgalaxy.model.persist.EbGame, java.io.OutputStream)
   */
  @Override
  public void saveGame(ModelFmpInit p_model, OutputStream p_output)
  {
    ObjectOutputStream out = null;
    try
    {
      out = new ObjectOutputStream( p_output );
      out.writeObject( p_model );
      out.close();
    } catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

}
