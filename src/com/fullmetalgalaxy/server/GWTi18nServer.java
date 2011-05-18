/**
 * Copyright (c) 2008 Raise Partner
 * 22, av. Doyen Louis Weil,
 * 38000 Grenoble, France
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: sebastien@chassande.fr
 */
package com.fullmetalgalaxy.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Key;
import com.google.gwt.i18n.client.Messages.DefaultMessage;


/**
 * This class was downloaded from http://code.google.com/p/gwt-fusionchart
 * and updated to be included in FMG source tree.
 * 
 * This class is similar to the GWT class used on client side. But this class
 * can be only used on server side for building implementation of Messages class.
 * This permit to use I18N properties on server side as on client slide.
 *   
 * @author Vincent
 * @author sebastien Contact: sebastien@chassande.fr
 */
public class GWTi18nServer
{
  public final static Map<String, Object> cache = new HashMap<String, Object>();

  public static <T> T create(Class<T> itf) throws ClassNotFoundException, IOException
  {
    return create( itf, null );
  }

  public static <T> T create(Class<T> itf, String lang) throws ClassNotFoundException, IOException
  {
    if( lang == null )
    {
      DefaultLocale locale = itf.getAnnotation( DefaultLocale.class );
      if( locale != null )
      {
        lang = locale.value();
      }
    }
    final String key = itf.getName() + lang;
    Object msg = null;
    synchronized( cache )
    {
      msg = cache.get( key );
      if( msg == null )
      {
        msg = createProxy( itf, lang );
        cache.put( key, msg );
      }
    }
    return (T)msg;
  }

  @SuppressWarnings("unchecked")
  private static <T> T createProxy(Class<T> itf, String lang) throws IOException
  {
    return (T)Proxy.newProxyInstance( itf.getClassLoader(), new Class[] { itf },
        new GenericMessagesServer( itf, lang ) );
  }


  private static class GenericMessagesServer implements InvocationHandler
  {
    final Properties properties = new Properties();
    final Class itf;

    public GenericMessagesServer(Class itf, String lang) throws IOException
    {
      this.itf = itf;
      String suffix = lang == null ? "" : "_" + lang;
      String baseName = itf.getName().replace( '.', '/' );
      InputStream in = null;
      in = load( baseName + suffix + ".properties" );
      if( in == null )
      {
        in = load( baseName + ".properties" );
      }
      if( in == null )
      {
        throw new IOException( baseName + suffix + ".properties cannot be found from class path" );
      }
      properties.load( in );
      in.close();
    }

    private InputStream load(String s)
    {
      InputStream in = null;
      ClassLoader cl;
      cl = Thread.currentThread().getContextClassLoader();
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
      if( in == null )
      {
        cl = getClass().getClassLoader();
        if( cl != null )
        {
          in = getClass().getClassLoader().getResourceAsStream( s );
        }
        if( in == null )
        {
          cl = ClassLoader.getSystemClassLoader();
          if( cl != null )
          {
            in = cl.getResourceAsStream( s );
          }
        }
      }
      return in;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if( !String.class.equals( method.getReturnType() ) )
      {
        return method.invoke( this, args );
      }
      if( proxy instanceof ConstantsWithLookup )
      {
        // not tested for FMG
        // ==================
        // method of the interface ConstantsWithLookup
        if( args == null || args.length != 1 || !(args[0] instanceof String) )
        {
          return method.invoke( this, args );
        }
        String param = (String)args[0];
        String s = properties.getProperty( param );
        if( s == null )
        {
          Method[] ms = itf.getMethods();
          for( Method m : ms )
          {
            if( m.getName().equals( param ) && m.getParameterTypes().length == 0 )
            {
              Key k = m.getAnnotation( Key.class );
              if( k != null )
              {
                s = properties.getProperty( k.value() );
              }
            }
          }
          if( s == null )
          {
            throw new IllegalArgumentException( param );
          }
        }
        if( "getBoolean".equals( method.getName() ) )
        {
          return Boolean.parseBoolean( s );
        }
        else if( "getDouble".equals( method.getName() ) )
        {
          return Double.parseDouble( s );
        }
        else if( "getFloat".equals( method.getName() ) )
        {
          return Float.parseFloat( s );
        }
        else if( "getInt".equals( method.getName() ) )
        {
          return Integer.parseInt( s );
        }
        else if( "getString".equals( method.getName() ) )
        {
          return s;
        }
        else if( "getMap".equals( method.getName() ) )
        {
          throw new NoSuchMethodException( method.toString() );
        }
        else if( "getStringArray".equals( method.getName() ) )
        {
          throw new NoSuchMethodException( method.toString() );
        }
        else
        {
          return method.invoke( this, args );
        }        
      }
      String key = null;
      Key k = method.getAnnotation( Key.class );
      if( k != null )
      {
        key = k.value();
      }
      if( key == null )
      {
        key = method.getName();
      }

      String s = properties.getProperty( key );
      if( s == null )
      {
        DefaultMessage dm = method.getAnnotation( DefaultMessage.class );
        if( dm == null )
        {
        }
        else
        {
          s = dm.value();
        }
      }
      if( args != null )
      {
        for( int i = 0; i < args.length; i++ )
        {
          String value = args[i].toString();
          // TODO manage plural text
          s.replaceAll( "{" + i + "}", value == null ? "" : value );
        }
      }
      return s;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj == this;
    }

    @Override
    public int hashCode()
    {
      return properties.size();
    }

  }
}
