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
