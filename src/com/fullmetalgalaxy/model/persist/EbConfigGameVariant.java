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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fullmetalgalaxy.model.TokenType;


/**
 * @author Vincent Legendre
 * this class represent a rule variant configuration. it could have been mapped onto an sql table.
 * but for performance reason, it's only a set of constant.
 * see ConfigGameVariant
 * 
 * this class is only used to load old data
 */
@Deprecated
public class EbConfigGameVariant extends EbBase
{
  static final long serialVersionUID = 1;



  private Map<TokenType,Integer> m_constructReserve = new HashMap<TokenType,Integer>();

  

  /**
   * 
   */
  public EbConfigGameVariant()
  {
    super();
    init();
  }

  public EbConfigGameVariant(EbConfigGameVariant p_config)
  {
    super(p_config);
    m_constructReserve = new HashMap<TokenType,Integer>(p_config.m_constructReserve);
  }

  private void init()
  {
    m_constructReserve = new HashMap<TokenType,Integer>();
    setConstructQty( TokenType.Pontoon, 1 );
    setConstructQty( TokenType.Crab, 1 );
    setConstructQty( TokenType.Tank, 4 );
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

    
  public Map<TokenType,Integer> getConstructReserve()
  {
    return m_constructReserve;
  }
  

  /**
   * Set allowed construct quantity for a given token type
   * Note that, for predefined variant, theses quantity will be multiply by
   * players number.
   * @param p_type
   * @param p_qty if < 0, unlimited
   */
  private void setConstructQty(TokenType p_type, int p_qty)
  {
    m_constructReserve.put( p_type, p_qty );
  }

  /**
   * @see setConstructQty
   * @param p_playerNumber
   */
  public void multiplyConstructQty(int p_playerNumber)
  {
    for(Entry<TokenType,Integer> entry : m_constructReserve.entrySet())
    {
      entry.setValue( entry.getValue() * p_playerNumber );
    }
  }
  

}
