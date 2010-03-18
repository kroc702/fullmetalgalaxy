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
