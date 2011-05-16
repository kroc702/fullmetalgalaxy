/*
  net.charabia.generation java package : Random text generation package
  Copyright (C) 1999 Rodrigo Reyes, reyes@chez.com, reyes@linuxbox.com

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/
package com.fullmetalgalaxy.server.xmpp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;

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
  protected Hashtable _binding = new Hashtable();
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
    // System.out.println("Storage : " + p_ressource);
    try
    {
      InputStreamReader fr = new InputStreamReader( getInputStream( p_ressource ), "iso-8859-1" );
      load( fr );
      fr.close();
    } catch( Exception iox )
    { iox.printStackTrace();
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
    { iox.printStackTrace();
    }
  }


  private static InputStream getInputStream(String s)
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
      cl = RessourceGraphStorage.class.getClassLoader();
      if( cl != null )
      {
        in = cl.getResourceAsStream( s );
      }
    }
    if( in == null )
    {
      cl = ClassLoader.getSystemClassLoader();
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
  public Enumeration availableNames()
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
      while( tok != tokenizer.TT_EOF )
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
      iox.printStackTrace();
    }
  }

  @Override
  public String toString()
  {
    return _binding.toString();
  }

} // LocalGraphStorage
