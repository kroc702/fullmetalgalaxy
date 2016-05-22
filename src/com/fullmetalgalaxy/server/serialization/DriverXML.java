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
package com.fullmetalgalaxy.server.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.FmpLogger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author vlegendr
 *
 */
public class DriverXML extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverXML.class.getName() );
  
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#loadGame(java.io.InputStream)
   */
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    ModelFmpInit model = null;
    try
    {
      XStream xstream = new XStream( new DomDriver() );
      xstream.registerConverter( new MyKeyConverter() );
      model = game2Model( xstream.fromXML( p_input ) );
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



  @Override
  public ModelFmpUpdate loadGameUpdate(InputStream p_input, String gameId)
  {
    ModelFmpUpdate model = null;
    try
    {
      XStream xstream = new XStream( new DomDriver() );
      xstream.registerConverter( new MyKeyConverter() );
      model = ModelFmpUpdate.class.cast( xstream.fromXML( p_input ) );
    } catch( Exception ex )
    {
      ex.printStackTrace();
    }
    if( model == null )
    {
      LOG.error( "no game loaded" );
    }
    return model;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#saveGame(com.fullmetalgalaxy.model.persist.EbGame, java.io.OutputStream)
   */
  @Override
  public void saveGame(ModelFmpInit p_game, OutputStream p_output)
  {
    XStream xstream = new XStream( new DomDriver() );
    xstream.registerConverter( new MyKeyConverter() );
    xstream.toXML( p_game, p_output );
  }

  // /////////////////////////////////////////////////
  /**
   * this converter skip com.google.appengine.api.datastore.Key class that cause security issue.
   * @author Vincent
   *
   */
  public static class MyKeyConverter implements Converter
  {

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context)
    {
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
      return null;
    }

    @Override
    public boolean canConvert(Class clazz)
    {
      return clazz.equals( com.google.appengine.api.datastore.Key.class );
    }
  }
}
