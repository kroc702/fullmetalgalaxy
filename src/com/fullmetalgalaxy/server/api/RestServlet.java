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

package com.fullmetalgalaxy.server.api;

/**
 * 
 *
 */
public class RestServlet extends JaxRsImpl
{
  private static final long serialVersionUID = 1L;

  public RestServlet()
  {
    super();
    addService( new AccountService() );
    addService( new GameService() );
    addService( new StatService() );
  }

  /*
   * @Override protected void doGet(HttpServletRequest p_request,
   * HttpServletResponse p_response) throws ServletException, IOException {
   * UriInfoMyImpl myUriInfo = new UriInfoMyImpl(p_request); String uri =
   * p_request.getRequestURI(); uri = uri.substring(6); String content = null;
   * 
   * // AccountService.class.getAnnotation(Path.class) if
   * (uri.startsWith("account")) { uri = uri.substring(8); switch (uri) { case
   * "me": content = accountService.getMe(p_request, p_response); break; case
   * "list": content =
   * accountService.getList(myUriInfo.getQueryParameterInt("from", "0"),
   * myUriInfo.getQueryParameterInt("to", "20"),
   * myUriInfo.getQueryParameterString("orderby", ""),
   * myUriInfo.getQueryParameterBoolean("all", "false")); break; default: content
   * = accountService.getAccount(uri); break; }
   * 
   * } else if (uri.startsWith("game")) { uri = uri.substring(5); switch (uri) {
   * case "list": content = gameService.getList(myUriInfo,
   * myUriInfo.getQueryParameterInt("from", "0"),
   * myUriInfo.getQueryParameterInt("to", "20"),
   * myUriInfo.getQueryParameterString("orderby", "-m_creationDate")); }
   * 
   * }
   * 
   * if (content != null) { p_response.addHeader("Access-Control-Allow-Origin",
   * "*"); p_response.getOutputStream().print(content);
   * p_response.setStatus(HttpServletResponse.SC_OK); } else {
   * p_response.sendError(HttpServletResponse.SC_NOT_FOUND); } }
   */
}
