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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriInfo;

/**
 * As I didn't manage to make the jersey jax rest service working. I make it
 * manually here
 *
 */
public class JaxRsImpl extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  public JaxRsImpl()
  {
    super();
  }

  Map<String, String> headers = new HashMap<String, String>();

  protected void addHeader(String key, String value)
  {
    headers.put( key, value );
  }

  static class ServiceParameter
  {
    String name = null;
    String defaultValue = null;
    private Class<?> type = null;
    private Constructor<?> typeConstructor = null;

    void setType(Class<?> type)
    {
      this.type = type;
      try
      {
        this.typeConstructor = type.getConstructor( String.class );
      } catch( Exception e )
      {
      }
    }

    ServiceParameter(Parameter parameter)
    {

      QueryParam queryParam = parameter.getAnnotation( QueryParam.class );
      DefaultValue defaultValue = parameter.getAnnotation( DefaultValue.class );
      if( queryParam != null )
      {
        this.name = queryParam.value();
      }
      if( defaultValue != null )
      {
        this.defaultValue = defaultValue.value();
      }
      setType( parameter.getType() );
    }

    Object fromString(String str)
    {
      try
      {
        if( str == null )
        {
          return null;
        }
        else if( typeConstructor != null )
        {
          return typeConstructor.newInstance( str );
        }
        else if( type.isEnum() )
        {
          for( Object enumConstant : type.getEnumConstants() )
          {
            if( ((Enum<?>)enumConstant).name().equals( str ) )
            {
              return enumConstant;
            }
          }
          for( Object enumConstant : type.getEnumConstants() )
          {
            if( ((Enum<?>)enumConstant).toString().equals( str ) )
            {
              return enumConstant;
            }
          }
          return null;
        }
        else if( type == Integer.TYPE )
        {
          return Integer.parseInt( str );
        }
        else if( type == Float.TYPE )
        {
          return Float.parseFloat( str );
        }
        else if( type == Boolean.TYPE )
        {
          return Boolean.parseBoolean( str );
        }
        else if( type == Double.TYPE )
        {
          return Double.parseDouble( str );
        }
        else if( type == Short.TYPE )
        {
          return Short.parseShort( str );
        }
        else if( type == Long.TYPE )
        {
          return Long.parseLong( str );
        }
        else if( type == Byte.TYPE )
        {
          return Byte.parseByte( str );
        }
        else if( type == Character.TYPE )
        {
          return str.charAt( 0 );
        }
      } catch( Exception e )
      {
      }
      return null;
    }

    Object getValue(String str)
    {
      Object obj = fromString( str );
      if( obj == null && defaultValue != null )
      {
        obj = fromString( defaultValue );
      }
      return obj;
    }
  }

  static class Service
  {
    Pattern pattern = null;
    String path = null;
    String httpMethod = null;
    Method objectMethod = null;
    ServiceParameter[] params = new ServiceParameter[] {};
    Object object = null;
  }

  List<Service> services = new ArrayList<Service>();

  protected void addService(Object service)
  {
    Path classPath = service.getClass().getAnnotation( Path.class );
    String strPath = "";
    if( classPath != null && classPath.value() != null )
    {
      if( !classPath.value().startsWith( "/" ) )
        strPath = "/";
      strPath += classPath.value();
      if( strPath.endsWith( "/" ) )
      {
        strPath = strPath.substring( 0, strPath.length() - 1 );
      }
    }
    for( Method method : service.getClass().getDeclaredMethods() )
    {
      Path methodPath = method.getAnnotation( Path.class );
      if( methodPath == null || methodPath.value() == null )
      {
        continue;
      }
      Service newService = new Service();
      try
      {
        newService.path = strPath;
        if( !methodPath.value().startsWith( "/" ) )
          newService.path += "/";
        newService.path += methodPath.value();
        if( newService.path.endsWith( "/" ) )
        {
          newService.path = newService.path.substring( 0, newService.path.length() - 1 );
        }
        if( method.getAnnotation( GET.class ) != null )
        {
          newService.httpMethod = HttpMethod.GET;
        }
        newService.objectMethod = method;
        newService.object = service;

        Parameter[] parameters = method.getParameters();
        newService.params = new ServiceParameter[parameters.length];

        for( int i = 0; i < parameters.length; i++ )
        {
          newService.params[i] = new ServiceParameter( parameters[i] );
        }
      } catch( Exception e )
      {
        continue;
      }
      services.add( newService );
    }
  }

  @Override
  protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    // search for a corresponding service
    String uri = p_request.getRequestURI();
    uri = uri.substring( p_request.getServletPath().length() );
    Service service = null;
    for( Service serviceCandidate : services )
    {
      if( serviceCandidate.httpMethod != null
          && !serviceCandidate.httpMethod.equals( p_request.getMethod() ) )
      {
        continue;
      }
      if( serviceCandidate.path != null && !serviceCandidate.path.equals( uri ) )
      {
        continue;
      }
      if( serviceCandidate.pattern != null && !serviceCandidate.pattern.matcher( uri ).matches() )
      {
        continue;
      }
      service = serviceCandidate;
      break;
    }
    if( service == null )
    {
      p_response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }
    else
    {
      try
      {
        // build method argument from given request
        Object[] args = new Object[service.params.length];
        for( int i = 0; i < args.length; i++ )
        {
          if( service.params[i].type == UriInfo.class )
          {
            args[i] = new UriInfoMyImpl( p_request );
          }
          else if( service.params[i].type == HttpServletRequest.class )
          {
            args[i] = p_request;
          }
          else if( service.params[i].type == HttpServletResponse.class )
          {
            args[i] = p_response;
          }
          else
          {
            String paramValue = null;
            if( service.params[i].name != null )
            {
              paramValue = p_request.getParameter( service.params[i].name );
            }
            // convert String into the right type
            args[i] = service.params[i].getValue( paramValue );
          }
        }
        // call service
        Object content = service.objectMethod.invoke( service.object, args );
        if( content != null )
        {
          for( Entry<String, String> entry : headers.entrySet() )
          {
            p_response.addHeader( entry.getKey(), entry.getValue() );
          }
          p_response.addHeader( "Set-Cookie", "testcookie=value; SameSite=None" );

          p_response.getOutputStream().print( content.toString() );
          p_response.setStatus( HttpServletResponse.SC_OK );
        }
        else
        {
          p_response.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
      } catch( Exception e )
      {
        p_response.getOutputStream().print( e.getMessage() );
        p_response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      }

    }
  }

}
