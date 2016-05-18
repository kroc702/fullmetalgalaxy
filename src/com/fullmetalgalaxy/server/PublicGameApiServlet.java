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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.EbGameLog;
import com.fullmetalgalaxy.server.serialization.DriverFactory;
import com.fullmetalgalaxy.server.serialization.DriverFileFormat;

/**
 * @author Vincent
 * 
 * this servlet allow other programs to read game and act as a human player over an http interface.
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
    String strid = p_req.getParameter( "id" );
    DriverFileFormat fileDriver = DriverFactory.get( p_req.getParameter( "format" ) );

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

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
  }




}
