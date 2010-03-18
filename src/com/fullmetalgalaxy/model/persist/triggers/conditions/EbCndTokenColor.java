/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbGame;



/**
 * @author Vincent Legendre
 * is token X color X ?
 */
public class EbCndTokenColor extends AnCondition
{
  static final long serialVersionUID = 126;

  private int m_color = EnuColor.None;
  private long m_tokenId = 0;

  /**
   * 
   */
  public EbCndTokenColor()
  {
    init();
  }

  private void init()
  {
    m_color = EnuColor.None;
    m_tokenId = 0;
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
    if( m_tokenId == 0 )
    {
      return false;
    }
    return p_game.getToken( m_tokenId ).getColor() == getColor();
  }



  /**
   * @return the color
   */
  public int getColor()
  {
    return m_color;
  }

  /**
   * @param p_color the color to set
   */
  public void setColor(int p_color)
  {
    m_color = p_color;
  }

}
