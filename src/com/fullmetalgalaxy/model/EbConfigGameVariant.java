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
package com.fullmetalgalaxy.model;

import com.fullmetalgalaxy.model.persist.EbBase;


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

  /**
   * 
   */
  public EbConfigGameVariant()
  {
    super();
    init();
  }

  private void init()
  {
    m_actionPtMaxReserve = 25;
    m_minSpaceBetweenFreighter = 8;
    m_deployementRadius = 4;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
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
  public int getDeployementRadius()
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
