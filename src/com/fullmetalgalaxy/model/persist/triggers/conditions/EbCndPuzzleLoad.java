/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import com.fullmetalgalaxy.model.persist.EbBase;


/**
 * @author Vincent Legendre
 *  This condition is true at game load.
 */
public class EbCndPuzzleLoad extends AnCondition
{
  static final long serialVersionUID = 124;

  /**
   * 
   */
  public EbCndPuzzleLoad()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbCndPuzzleLoad(EbBase p_base)
  {
    super( p_base );
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
}
