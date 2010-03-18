/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 * it is the base class to represent any action which an account can do on to a game.
 */
public class AnEventUser extends AnEvent
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public AnEventUser()
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

  public EbRegistration getMyRegistration(EbGame p_game)
  {
    if( p_game.getGameType() == GameType.MultiPlayer )
    {
      return p_game.getRegistrationByIdAccount( getAccountId() );
    }
    else if( p_game.getGameType() == GameType.Puzzle )
    {
      return p_game.getCurrentPlayerRegistration();
    }
    return null;
  }

  /**
   * check this action is allowed.
   * you have to override this method.
   * @throws RpcFmpException
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( ((getAccountId() == 0)) && (!isAuto())
 && (p_game.getGameType() != GameType.Puzzle) )
    {
      // TODO i18n
      throw new RpcFmpException( "Vous devez etre logger pour realiser cette action" );
    }
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += "[" + getAccountId() + "]";
    return str;
  }




}
