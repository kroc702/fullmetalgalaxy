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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.googlecode.objectify.Key;

/**
 * @author Vincent
 *
 */
public class FmgSerializationPolicy extends SerializationPolicy
{
  private static FmgSerializationPolicy s_policy = null;

  public static SerializationPolicy getPolicy()
  {
    if( s_policy == null )
    {
      s_policy = new FmgSerializationPolicy();
    }
    return s_policy;
  }


  SerializationPolicy defaultPolicy;

  public FmgSerializationPolicy()
  {
    defaultPolicy = RPC.getDefaultSerializationPolicy();
  }

  @Override
  public boolean shouldDeserializeFields(Class<?> clazz)
  {
    return defaultPolicy.shouldDeserializeFields( clazz );
  }

  @Override
  public boolean shouldSerializeFields(Class<?> clazz)
  {
    // TODO Auto-generated method stub
    return defaultPolicy.shouldDeserializeFields( clazz );
  }

  @Override
  public void validateDeserialize(Class<?> clazz) throws SerializationException
  {
    defaultPolicy.validateDeserialize( clazz );
  }

  @Override
  public void validateSerialize(Class<?> clazz) throws SerializationException
  {
    if( !clazz.isAssignableFrom( Key.class ) )
      defaultPolicy.validateSerialize( clazz );
  }

  }
