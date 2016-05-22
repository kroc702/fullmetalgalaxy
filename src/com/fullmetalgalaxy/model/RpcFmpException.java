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
package com.fullmetalgalaxy.model;

import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Kroc
 *
 */
public class RpcFmpException extends Exception implements IsSerializable
{
  static final long serialVersionUID = 0;

  private String m_message;
  private AnEvent cause;

  public RpcFmpException()
  {
    super();
  }

  public RpcFmpException(String p_message)
  {
    super( p_message );
    m_message = p_message;
  }

  public RpcFmpException(String p_message, AnEvent p_cause)
  {
    super( p_message );
    m_message = p_message;
    cause = p_cause;
  }

  public AnEvent getCauseEvent()
  {
    return cause;
  }

  @Override
  public String getMessage()
  {
    return m_message;
  }

  @Override
  public String toString()
  {
    return getMessage();
  }
}
