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

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

/**
 * @author Vincent
 * This class group all serialization method
 */
public class Serializer
{
  private final static FmpLogger log = FmpLogger.getLogger( ChannelManager.class.getName() );

  protected static java.lang.reflect.Method s_getRoom = null;
  protected static java.lang.reflect.Method s_getChatMessage = null;
  protected static java.lang.reflect.Method s_getModelFmpUpdate = null;
  protected static java.lang.reflect.Method s_getModelFmpInit = null;


  static
  {
    synchronized( Serializer.class )
    {
      try
      {
        s_getRoom = GameServicesImpl.class.getMethod( "getRoom", Long.TYPE );
        s_getChatMessage = GameServicesImpl.class.getMethod( "getChatMessage", Long.TYPE );
        s_getModelFmpUpdate = GameServicesImpl.class.getMethod( "getModelFmpUpdate", Long.TYPE );
        s_getModelFmpInit = GameServicesImpl.class.getMethod( "getModelFmpInit", String.class );
      } catch( Exception e )
      {
        // in that case, server push through channel api wont work.
        log.error( e );
      }
    }
  }

  /**
   * serialize PresenceRoom to send to client.
   * @param p_room
   * @return null if any error occur.
   */
  public static String toClient(PresenceRoom p_room)
  {
    String response = null;
    try
    {
      response = RPC.encodeResponseForSuccess( s_getRoom, p_room,
          FmgSerializationPolicy.getPolicy() );
    } catch( SerializationException e )
    {
      log.error( e );
    }
    return response;
  }

  /**
   * serialize ChatMessage to send to client.
   * @param p_msg
   * @return null if any error occur.
   */
  public static String toClient(ChatMessage p_msg)
  {
    String response = null;
    try
    {
      response = RPC.encodeResponseForSuccess( s_getChatMessage, p_msg,
          FmgSerializationPolicy.getPolicy() );
    } catch( SerializationException e )
    {
      log.error( e );
    }
    return response;
  }

  /**
   * serialize ModelFmpUpdate to send to client.
   * @param p_modelUpdate
   * @return null if any error occur.
   */
  public static String toClient(ModelFmpUpdate p_modelUpdate)
  {
    String response = null;
    try
    {
      response = RPC.encodeResponseForSuccess( s_getModelFmpUpdate, p_modelUpdate,
          FmgSerializationPolicy.getPolicy() );
    } catch( SerializationException e )
    {
      log.error( e );
    }
    return response;
  }


  /**
   * serialize EbGame to send to client.
   * @param p_model
   * @return null if any error occur.
   */
  public static String toClient(ModelFmpInit p_model)
  {
    String response = null;
    try
    {
      response = RPC.encodeResponseForSuccess( s_getModelFmpInit, p_model,
          FmgSerializationPolicy.getPolicy() );
    } catch( SerializationException e )
    {
      log.error( e );
    }
    return response;
  }



  public static String escape(String s)
  {
    if( s == null )
    {
      return null;
    }
    return escapeSingleQuotes( escapeBackslash( s ) );
  }


  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * 
   * @param html the html string to escape
   * @return the escaped string
   */
  private static String escapeHtml(String html)
  {
    if( html == null )
    {
      return null;
    }
    return html.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
  }

  private static String escapeBackslash(String s)
  {
    return s.replaceAll( "\\\\", "\\\\\\\\" );
  }

  private static String escapeSingleQuotes(String s)
  {
    return s.replaceAll( "'", "\\\\'" );
  }


}
