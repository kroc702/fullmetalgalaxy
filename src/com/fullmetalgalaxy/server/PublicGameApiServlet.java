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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.server.serialization.DriverFactory;
import com.fullmetalgalaxy.server.serialization.DriverFileFormat;
import com.fullmetalgalaxy.server.serialization.DriverXML;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Vincent
 * 
 * this servlet allow other programs to read game and act as a human player over an http interface.
 * 
 * url mapping: /api/game/*
 */
public class PublicGameApiServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;
  private final static FmpLogger log = FmpLogger.getLogger( PublicGameApiServlet.class.getName() );

  /**
   * 
   */
  public PublicGameApiServlet()
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    String strid = readURIParam( p_req, 0 );
    DriverFileFormat fileDriver = DriverFactory.get( readURIParam( p_req, 1 ) );

    if( strid != null )
    {
      // load game with standard api
      ModelFmpInit modelInit = GameServicesImpl.sgetModelFmpInit( p_req, p_resp, strid );
      if( modelInit.getGame().getAdditionalEventCount() > 0 )
      {
        // load additional events
        EbGameLog gameLog = GameServicesImpl.sgetAdditionalGameLog( modelInit.getGame().getId() );
        gameLog.getLog().addAll( modelInit.getGame().getLogs() );
        modelInit.getGame().setLogs( gameLog.getLog() );
      }
      if( modelInit != null )
      {
        modelInit.getGame().getPreview().onLoad();
        modelInit.getGame().onLoad();

        fileDriver.saveGame( modelInit, p_resp.getOutputStream() );
      }
    }

  }

  private static GameServicesImpl s_service = null;

  protected static GameServicesImpl getGameServices()
  {
    if( s_service == null )
    {
      s_service = new GameServicesImpl();
    }
    return s_service;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    String strid = readURIParam( p_request, 0 );
    DriverFileFormat fileDriver = DriverFactory.get( readURIParam( p_request, 1 ) );

    ModelFmpUpdate responseUpdate = null;
    try
    {
      ModelFmpUpdate requestUpdate = fileDriver.loadGameUpdate( p_request.getInputStream(), strid );
      responseUpdate = getGameServices().runModelUpdate( requestUpdate );
    } catch( Throwable e )
    {
      p_resp.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      if( e instanceof RpcFmpException && ((RpcFmpException)e).getCauseEvent() != null )
      {
        p_resp.getOutputStream().println( ((RpcFmpException)e).getCauseEvent().toString() );
        p_resp.getOutputStream().println( ((RpcFmpException)e).getCauseEvent().getTransientComment() );
      }
      e.printStackTrace( new PrintStream( p_resp.getOutputStream() ) );
      return;
    }
    // a quick and dirty answer
    if( responseUpdate != null )
    {
      XStream xstream = new XStream( new DomDriver() );
      xstream.registerConverter( new DriverXML.MyKeyConverter() );
      xstream.toXML( responseUpdate, p_resp.getOutputStream() );
    }
  }

  private String readURIParam(HttpServletRequest p_request, int index)
  {
    String[] params = p_request.getRequestURI().substring( 10 ).split( "/" );
    if( index >= 0 && index < params.length )
    {
      return params[index];
    }
    return null;
  }


}
