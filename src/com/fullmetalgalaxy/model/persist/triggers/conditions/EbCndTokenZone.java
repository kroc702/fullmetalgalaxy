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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.triggers.AnGameZone;



/**
 * @author Vincent Legendre
 * is token X in zone X ?
 */
public class EbCndTokenZone extends AnCondition
{
  static final long serialVersionUID = 125;

  private AnGameZone m_zone = new AnGameZone();
  private long m_tokenId = 0;

  /**
   * 
   */
  public EbCndTokenZone()
  {
    init();
  }

  private void init()
  {
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#isTrue(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public boolean isTrue(EbGame p_game)
  {
    if( m_tokenId == 0 || getZone() == null )
    {
      return false;
    }
    return getZone().contain( p_game.getToken( m_tokenId ) );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#getActParams(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public List<Object> getActParams(EbGame p_game)
  {
    List<Object> params = new ArrayList<Object>();
    params.add( p_game.getToken( m_tokenId ) );
    return params;
  }

  /**
   * @return the zone
   */
  public AnGameZone getZone()
  {
    return m_zone;
  }

  /**
   * @param p_zone the zone to set
   */
  public void setZone(AnGameZone p_zone)
  {
    m_zone = p_zone;
  }

}
