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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * allow all sort of cors request
 *
 */
public class CorsFilter implements Filter
{

  /**
   * 
   */
  public CorsFilter()
  {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  @Override
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException
  {
    String allowOrigin = "*";
    String allowMethod = "GET,HEAD,OPTIONS,POST,PUT";
    String allowHeader = "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers";

    if( request instanceof HttpServletRequest )
    {
      HttpServletRequest req = (HttpServletRequest)request;
      /*
      System.out.print( "method:" + req.getMethod() );
      if( req.getRequestURI() != null )
      {
        System.out.print( " " + req.getRequestURI() );
      }
      if( req.getQueryString() != null )
      {
        System.out.print( "?" + req.getQueryString() );
      }
      System.out.println();
      Enumeration<String> headerNames = req.getHeaderNames();
      while( headerNames.hasMoreElements() )
      {
        String headerName = headerNames.nextElement();
        System.out.println( "request header " + headerName + ": " + req.getHeader( headerName ) );
      }
      if( req.getCookies() != null )
      {
        for( Cookie cookie : req.getCookies() )
        {
          System.out.println( "request cookie " + cookie.getName() + ": " + cookie.getValue() );
        }
      }
      if( req.getMethod().equals( "POST" ) && req.getInputStream() != null && false )
      {
        System.out.flush();
        byte[] buf = new byte[8192];
        int length;
        while( (length = req.getInputStream().read( buf )) > 0 )
        {
          System.out.write( buf, 0, length );
        }
        System.out.flush();
        System.out.println();
      }
      */
      allowOrigin = req.getHeader( "Origin" );
      allowMethod = req.getHeader( "Access-Control-Request-Method" );
      allowHeader = req.getHeader( "Access-Control-Request-Headers" );
    }
    if( response instanceof HttpServletResponse )
    {
      HttpServletResponse resp = (HttpServletResponse)response;
      /*
      for( String headerName : resp.getHeaderNames() )
      {
        System.out.println( "response header " + headerName + ": " + resp.getHeader( headerName ) );
      }
      */
      resp.setHeader( "Access-Control-Allow-Origin", allowOrigin );
      resp.setHeader( "Access-Control-Allow-Credentials", "true" );
      resp.setHeader( "Access-Control-Allow-Methods", allowMethod );
      resp.setHeader( "Access-Control-Allow-Headers", allowHeader );
    }
    filterChain.doFilter( request, response );
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(FilterConfig arg0) throws ServletException
  {
    // TODO Auto-generated method stub

  }

}
