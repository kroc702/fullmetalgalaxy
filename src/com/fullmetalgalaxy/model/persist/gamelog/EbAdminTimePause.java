/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
public class EbAdminTimePause extends EbAdmin
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public EbAdminTimePause()
  {
    super();
    init();
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
    return GameLogType.AdminTimePause;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    EbRegistration myRegistration = getMyRegistration(p_game);
    if( myRegistration == null )
    {
      // TODO i18n
      throw new RpcFmpException(
          "Vous devez �tre inscrit a cette partie pour r�aliser cette action" );
    }
    if( !p_game.isStarted() )
    {
      throw new RpcFmpException( "la partie est deja en pause" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.setStarted( false );
    if( !p_game.isAsynchron() )
    {
      // game is in pause
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        registration.setEndTurnDate( null );
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += " : Pause";
    return str;
  }

  // Bean getter / setter
  // ====================

}
