/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;



/**
 * @author Vincent Legendre
 * is player control a given color ?
 */
public class EbCndPlayerColor extends AnCondition
{
  static final long serialVersionUID = 126;

  private int m_color = EnuColor.None;
  private EbRegistration m_player = null;

  /**
   * 
   */
  public EbCndPlayerColor()
  {
    init();
  }

  private void init()
  {
    m_color = EnuColor.None;
    m_player = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /** true if player control given color
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#isTrue(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public boolean isTrue(EbGame p_game)
  {
    if( getPlayer() == null )
    {
      return false;
    }
    return getPlayer().getEnuColor().isColored( getColor() );
  }



  /**
   * @return the player
   */
  public EbRegistration getPlayer()
  {
    return m_player;
  }

  /**
   * @param p_player the player to set
   */
  public void setPlayer(EbRegistration p_player)
  {
    m_player = p_player;
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
