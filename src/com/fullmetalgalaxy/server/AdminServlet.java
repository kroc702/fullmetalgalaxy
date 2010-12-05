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
/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;

/**
 * @author Vincent
 *
 */
public class AdminServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;

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
    FmgDataStore dataStore = new FmgDataStore();

    strid = p_req.getParameter( "deletegame" );
    if( strid != null )
    {
      dataStore.deleteGame( Long.parseLong( strid ) );
      p_resp.sendRedirect( "/gamelist.jsp" );
    }

    strid = p_req.getParameter( "deleteaccount" );
    if( strid != null )
    {
      dataStore.deleteAccount( Long.parseLong( strid ) );
      p_resp.sendRedirect( "/halloffames.jsp" );
    }

    strid = p_req.getParameter( "downloadgame" );
    if( strid != null )
    {
      ModelFmpInit modelInit = ServicesImpl.sgetModelFmpInit( strid );
      if( modelInit != null )
      {
        ObjectOutputStream out = new ObjectOutputStream( p_resp.getOutputStream() );
        out.writeObject( modelInit );
      }
    }

    dataStore.close();
    // p_resp.getOutputStream().println( "game " + p_req.getParameter( "id" ) +
    // " effacé" );

  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    // TODO Auto-generated method stub
    super.doPost( p_req, p_resp );
  }



}
