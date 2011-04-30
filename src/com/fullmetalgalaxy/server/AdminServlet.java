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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.image.BlobstoreCache;
import com.fullmetalgalaxy.server.image.MiniMapProducer;

/**
 * @author Vincent
 *
 */
public class AdminServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;
  private final static FmpLogger log = FmpLogger.getLogger( AdminServlet.class.getName() );

  /**
   * 
   */
  public AdminServlet()
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    String strid = null;
    strid = p_req.getParameter( "deletegame" );
    if( strid != null )
    {
      FmgDataStore dataStore = new FmgDataStore(false);
      dataStore.delete( Game.class, Long.parseLong( strid ) );
      dataStore.close();
      p_resp.sendRedirect( "/gamelist.jsp" );
    }

    strid = p_req.getParameter( "deleteaccount" );
    if( strid != null )
    {
      FmgDataStore dataStore = new FmgDataStore(false);
      dataStore.delete( EbAccount.class, Long.parseLong( strid ) );
      dataStore.close();
      p_resp.sendRedirect( "/halloffames.jsp" );
    }

    strid = p_req.getParameter( "downloadgame" );
    if( strid != null )
    {
      ModelFmpInit modelInit = GameServicesImpl.sgetModelFmpInit( strid );
      if( modelInit != null )
      {
        ObjectOutputStream out = new ObjectOutputStream( p_resp.getOutputStream() );
        out.writeObject( modelInit );
      }
    }

  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    Map<String, String> params = new HashMap<String, String>();
    ModelFmpInit modelInit = null;

    try
    {
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          params.put( item.getFieldName(), Streams.asString( item.openStream(), "UTF-8" ) );
        }
        else if( item.getFieldName().equalsIgnoreCase( "gamefile" ) )
        {
          ObjectInputStream in = new ObjectInputStream( item.openStream() );
          modelInit = ModelFmpInit.class.cast( in.readObject() );
          in.close();
        }
      }
    } catch( FileUploadException e )
    {
      log.error( e );
    } catch( ClassNotFoundException e2 )
    {
      log.error( e2 );
    }


    if( modelInit != null )
    {
      // set transient to avoid override data
      modelInit.getGame().setTrancient();
      modelInit.getGame().setMinimapBlobKey( null );
      modelInit.getGame().setMinimapUri( null );

      FmgDataStore dataStore = new FmgDataStore(false);
      dataStore.put( modelInit.getGame() );
      dataStore.close();

      // construct minimap image
      MiniMapProducer miniMapProducer = new MiniMapProducer( GameServicesImpl.s_basePath,
          modelInit.getGame() );
      byte[] data = miniMapProducer.getImage();
      BlobstoreCache.storeMinimap( modelInit.getGame().getId(), new ByteArrayInputStream( data ) );
    }

  }



}
