/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;


/**
 * @author Vincent Legendre
 *
 */
public class EbAdmin extends AnEventUser
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public EbAdmin()
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

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    throw new RpcFmpException( "les actions d'administrations ne peuvent �tre d�faite" );
  }


}
