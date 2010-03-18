/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;


/**
 * @author vincent
 *
 */
public class GameLogFactory
{
  static public AnEvent newAdminTimePause(long p_accountId)
  {
    EbAdminTimePause event = new EbAdminTimePause();
    event.setAccountId( p_accountId );
    return event;
  }

  static public AnEvent newAdminTimePlay(long p_accountId)
  {
    EbAdminTimePlay event = new EbAdminTimePlay();
    event.setAccountId( p_accountId );
    return event;
  }

}
