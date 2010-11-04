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
package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fullmetalgalaxy.model.TokenType;


/**
 * @author Vincent Legendre
 * this class represent a rule variant configuration. it could have been mapped onto an sql table.
 * but for performance reason, it's only a set of constant.
 * see ConfigGameVariant
 */
public class EbConfigGameVariant extends EbBase
{
  static final long serialVersionUID = 1;


  private int m_actionPtMaxReserve = 25;
  private int m_minSpaceBetweenFreighter = 8;
  private int m_deployementRadius = 4;
  private String m_description = "";
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
    m_actionPtMaxReserve = p_config.getActionPtMaxReserve();
    m_minSpaceBetweenFreighter = p_config.getMinSpaceBetweenFreighter();
    m_deployementRadius = p_config.getDeploymentRadius();
    m_description = new String( p_config.getDescription() );
    m_constructReserve = new HashMap<TokenType,Integer>(p_config.m_constructReserve);
  }

  private void init()
  {
    m_actionPtMaxReserve = 25;
    m_minSpaceBetweenFreighter = 8;
    m_deployementRadius = 4;
    m_description = "";
    m_constructReserve = new HashMap<TokenType,Integer>();
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
  
  public boolean canConstruct(TokenType p_type)
  {
    Integer qty = m_constructReserve.get( p_type );
    return qty != null && qty != 0;
  }
  
  public void incConstructQty(TokenType p_type)
  {
    Integer qty = m_constructReserve.get( p_type );
    if(qty != null && qty >= 0)
    {
      qty++;
      setConstructQty(p_type,qty);
    }
  }
  
  public void decConstructQty(TokenType p_type)
  {
    Integer qty = m_constructReserve.get( p_type );
    if(qty != null && qty > 0)
    {
      qty--;
      setConstructQty(p_type,qty);
    }
  }
  
  /**
   * Set allowed construct quantity for a given token type
   * Note that, for predefined variant, theses quantity will be multiply by
   * players number.
   * @param p_type
   * @param p_qty if < 0, unlimited
   */
  public void setConstructQty(TokenType p_type, int p_qty)
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
  
  /**
   * @return the actionPtMaxReserve
   */
  public int getActionPtMaxReserve()
  {
    return m_actionPtMaxReserve;
  }

  /**
   * @param p_actionPtMaxReserve the actionPtMaxReserve to set
   */
  public void setActionPtMaxReserve(int p_actionPtMaxReserve)
  {
    m_actionPtMaxReserve = p_actionPtMaxReserve;
  }

  /**
   * @return the minSpaceBetweenFreighter
   */
  public int getMinSpaceBetweenFreighter()
  {
    return m_minSpaceBetweenFreighter;
  }

  /**
   * @param p_minSpaceBetweenFreighter the minSpaceBetweenFreighter to set
   */
  public void setMinSpaceBetweenFreighter(int p_minSpaceBetweenFreighter)
  {
    m_minSpaceBetweenFreighter = p_minSpaceBetweenFreighter;
  }

  /**
   * @return the deployementRadius
   */
  public int getDeploymentRadius()
  {
    return m_deployementRadius;
  }

  /**
   * @param p_deployementRadius the deployementRadius to set
   */
  public void setDeployementRadius(int p_deployementRadius)
  {
    m_deployementRadius = p_deployementRadius;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return m_description;
  }

  /**
   * @param p_description the description to set
   */
  public void setDescription(String p_description)
  {
    m_description = p_description;
  }

}
