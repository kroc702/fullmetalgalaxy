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

package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author Vincent
 *
 */
@Unindexed
public class EbGameLog extends EbBase
{
  private static final long serialVersionUID = 1L;

  /**
   * if game log reach this amount of event per player, then database should
   * remove old event.
   */
  public static final int MAX_EVENTS_PER_PLAYER = 100;
  public static final int MIN_EVENTS_PER_PLAYER = 40;
  /**
   * due to database limit (1000000 char max)
   * each blob shouldn't contain more than this amount of event.<br/>
   * real limit was reach with 2300 event.
   */
  public static final int MAX_EVENTS_PER_BLOB = 1700;


  @Parent
  protected Key<EbGamePreview> m_preview;

  /**
   * 
   */
  @Indexed
  private int m_index = 0;

  @Serialized
  private List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> m_log = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();


  public void setKeyPreview(Key<EbGamePreview> p_preview)
  {
    m_preview = p_preview;
  }

  public int getIndex()
  {
    return m_index;
  }

  public void setIndex(int p_index)
  {
    m_index = p_index;
  }

  public List<AnEvent> getLog()
  {
    return m_log;
  }

  public void setLog(List<AnEvent> p_log)
  {
    m_log = p_log;
  }



}
