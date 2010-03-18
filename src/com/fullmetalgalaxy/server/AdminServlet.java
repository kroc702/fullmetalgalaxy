/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
