/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;



/**
 * @author Vincent Legendre
 *
 */
public class EbEvtMessage extends AnEvent
{
  static final long serialVersionUID = 1;


  /**
   * 
   */
  public EbEvtMessage()
  {
    super();
    this.init();
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtMessage;
  }



  // Bean getter / setter
  // ====================

}
