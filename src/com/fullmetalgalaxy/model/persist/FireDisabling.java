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
package com.fullmetalgalaxy.model.persist;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent
 * Describe a fire disabling action. in other word, it contain a target and two destroyer.
 */
public class FireDisabling implements IsSerializable, java.io.Serializable
{
  private static final long serialVersionUID = 4170862379330741389L;

  private long m_targetId = 0;
  private long m_destroyer1Id = 0;
  private long m_destroyer2Id = 0;

  transient private EbToken m_target = null;
  transient private EbToken m_destroyer1 = null;
  transient private EbToken m_destroyer2 = null;


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object p_obj)
  {
    if( !(p_obj instanceof FireDisabling) )
    {
      return false;
    }
    FireDisabling fd = (FireDisabling)p_obj;
    return (fd != null) && (m_targetId == fd.m_targetId)
        && ((m_destroyer1Id == fd.m_destroyer1Id) && (m_destroyer2Id == fd.m_destroyer2Id))
        || ((m_destroyer1Id == fd.m_destroyer2Id) && (m_destroyer2Id == fd.m_destroyer1Id));
  }

  @Override
  public String toString()
  {
    return "FD[" + m_destroyer1+ "+"+ m_destroyer2+" -> "+m_target+"]";
  }

  /**
   * @return the target
   */
  public EbToken getTarget(Game p_game)
  {
    if( m_target == null && m_targetId != 0 )
    {
      m_target = p_game.getToken( m_targetId );
    }
    return m_target;
  }

  /**
   * @param p_target the target to set
   */
  public void setTarget(EbToken p_target)
  {
    m_target = p_target;
    if( m_target != null )
    {
      m_targetId = m_target.getId();
    }
    else
    {
      m_targetId = 0;
    }
  }

  /**
   * @return the destroyer1
   */
  public EbToken getDestroyer1(Game p_game)
  {
    if( m_destroyer1 == null && m_destroyer1Id != 0 )
    {
      m_destroyer1 = p_game.getToken( m_destroyer1Id );
    }
    return m_destroyer1;
  }

  /**
   * @param p_destroyer1 the destroyer1 to set
   */
  public void setDestroyer1(EbToken p_destroyer1)
  {
    m_destroyer1 = p_destroyer1;
    if( m_destroyer1 != null )
    {
      m_destroyer1Id = m_destroyer1.getId();
    }
    else
    {
      m_destroyer1Id = 0;
    }
  }

  /**
   * @return the destroyer2
   */
  public EbToken getDestroyer2(Game p_game)
  {
    if( m_destroyer2 == null && m_destroyer2Id != 0 )
    {
      m_destroyer2 = p_game.getToken( m_destroyer2Id );
    }
    return m_destroyer2;
  }

  /**
   * @param p_destroyer2 the destroyer2 to set
   */
  public void setDestroyer2(EbToken p_destroyer2)
  {
    m_destroyer2 = p_destroyer2;
    if( m_destroyer2 != null )
    {
      m_destroyer2Id = m_destroyer2.getId();
    }
    else
    {
      m_destroyer2Id = 0;
    }
  }

  /**
   * @return the targetId
   */
  public long getTargetId()
  {
    return m_targetId;
  }

  /**
   * @return the destroyer1Id
   */
  public long getDestroyer1Id()
  {
    return m_destroyer1Id;
  }

  /**
   * @return the destroyer2Id
   */
  public long getDestroyer2Id()
  {
    return m_destroyer2Id;
  }

  
  
}
