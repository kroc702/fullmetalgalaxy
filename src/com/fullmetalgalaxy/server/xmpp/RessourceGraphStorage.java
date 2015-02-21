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
package com.fullmetalgalaxy.server.xmpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import net.charabia.generation.Graph;
import net.charabia.generation.GraphStorage;


/**
 * 
 *
 * 
 * @author Vincent Legendre
 * @version
 */

public class RessourceGraphStorage implements GraphStorage
{
  protected Hashtable<String, Graph> _binding = new Hashtable<String, Graph>();
  protected String m_initialGraph = null;


  public RessourceGraphStorage(String p_ressource)
  {
    this( p_ressource, "initial" );
  }

  public RessourceGraphStorage(String p_ressource, String p_defaultGraph)
  {
    assert p_ressource != null && p_defaultGraph != null;
    if( !p_ressource.endsWith( ".graph" ))
    {
      p_ressource += ".graph";
    }
    m_initialGraph = p_defaultGraph;
    System.err.println( "RessourceGraphStorage : " + p_ressource );
    try
    {
      InputStreamReader fr = new InputStreamReader( getInputStream( p_ressource ), "iso-8859-1" );
      load( fr );
      fr.close();
      System.err.println( "graph " + p_ressource + " loaded" );
    } catch( Exception iox )
    {
      iox.printStackTrace( System.err );
    }
  }

  public RessourceGraphStorage(InputStream p_ressource, String p_defaultGraph)
  {
    m_initialGraph = p_defaultGraph;
    // System.out.println("Storage : " + p_ressource);
    try
    {
      InputStreamReader fr = new InputStreamReader( p_ressource, "iso-8859-1" );
      load( fr );
      fr.close();
    } catch( Exception iox )
    {
      iox.printStackTrace( System.err );
    }
  }


  private static InputStream getInputStream(String s)
  {
    InputStream in = null;
    ClassLoader cl;
    try
    {
    if( in == null )
    {
      cl = RessourceGraphStorage.class.getClassLoader();
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
    }
    if( in == null )
    {
      cl = Thread.currentThread().getContextClassLoader();
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
    }
    if( in == null )
    {
      final Throwable t = new Throwable();
      final StackTraceElement methodCaller = t.getStackTrace()[2];
      cl = methodCaller.getClass().getClassLoader();
      if( cl == null )
      {
        String str = methodCaller.getClassName();
        String folder = "";
        String[] pkgs = str.split( "\\." );
        for( int i=1; i<pkgs.length; i++ )
        {
          folder += pkgs[i-1] + "/";
        }
        s = folder + s;
        cl = Thread.currentThread().getContextClassLoader();
      }
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
    }
    if( in == null )
    {
        // in fact this is forbidden by GAE
      cl = ClassLoader.getSystemClassLoader();
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
    }
    } catch( Exception e )
    {
      e.printStackTrace( System.err );
    }
    if( in == null )
    {
      System.err.println( "getInputStream: failed to load " + s );
    }
    return in;
  }

  @Override
  public String getInitialGraph()
  {
    return m_initialGraph;
  }

  @Override
  public void setInitialGraph(String graphname)
  {
    m_initialGraph = graphname;
  }


  @Override
  public Enumeration<String> availableNames()
  {
    return _binding.keys();
  }

  @Override
  public void put(String name, Graph graph)
  {
    _binding.put( name, graph );
  }

  @Override
  public Graph get(String name)
  {
    return (Graph)_binding.get( name );
  }

  @Override
  public void remove(String name)
  {
    _binding.remove( name );
  }

  @Override
  public void save()
  {
    // unimplemented
  }

  @Override
  public void save(Writer writer) throws IOException
  {
    // unimplemented
  }

  @Override
  public void load(Reader reader) throws IOException
  {
    try
    {
      StreamTokenizer tokenizer = new StreamTokenizer( reader );

      int tok = tokenizer.nextToken();
      while( tok != StreamTokenizer.TT_EOF )
      {
        if( tok == '$' )
        {
          tok = tokenizer.nextToken();
          String name = tokenizer.sval;
          setInitialGraph( name );
        }
        else
        {
          String name = tokenizer.sval;
          //System.out.println( "Loading graph " + name + " !" );
          Graph graph = new Graph();
          graph.load( tokenizer );

          _binding.put( name, graph );
        }
        tok = tokenizer.nextToken();
      }
    } catch( IOException iox )
    {
      iox.printStackTrace( System.err );
    }
  }

  @Override
  public String toString()
  {
    return _binding.toString();
  }

} // LocalGraphStorage
