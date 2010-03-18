/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
public class EbAdminTimePlay extends EbAdmin
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public EbAdminTimePlay()
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
    return GameLogType.AdminTimePlay;
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
          "Vous devez être inscrit a cette partie pour réaliser cette action" );
    }
    if( p_game.isStarted() )
    {
      throw new RpcFmpException( "la partie est deja en cours" );
    }
    if( p_game.getCurrentNumberOfRegiteredPlayer() < 2 )
    {
      throw new RpcFmpException( "Pour démarrer une partie il faut au moins deux joueurs" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    p_game.setStarted( true );
    if( !p_game.isAsynchron() )
    {
      // every player but me shouldn't have a time constain
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        registration.setEndTurnDate( null );
      }
      EbRegistration myRegistration = getMyRegistration(p_game);
      if( myRegistration != null )
      {
        myRegistration.setEndTurnDate( new Date( System.currentTimeMillis()
            + p_game.getFullTurnDurationInMili() ) );
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
    str += " : Play";
    return str;
  }


  // Bean getter / setter
  // ====================

}
