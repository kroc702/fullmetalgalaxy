/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtRepair extends AnEventPlay
{
  static final long serialVersionUID = 1;


  /**
   * 
   */
  public EbEvtRepair()
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
    setCost( 2 );
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtRepair;
  }


  @Override
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    return getPosition();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    EbToken freighter = p_game.getToken( getPosition(), TokenType.Freighter );
    EbToken turret = p_game.getToken( getPosition(), TokenType.Turret );
    if( (freighter == null) || (turret != null) )
    {
      // no i18n
      throw new RpcFmpException( "you can repair only destroyed turret" );
    }
    // check he don't repair center freighter
    if( freighter.getPosition().equals( getPosition() ) )
    {
      // no i18n
      throw new RpcFmpException( "you can repair only destroyed turret" );
    }
    if( getMyRegistration(p_game).getTurretsToRepair() <= 0 )
    {
      throw new RpcFmpException( "you can't repair any more turrets" );
    }
    if( getMyRegistration( p_game ).getOriginalColor() == freighter.getColor() )
    {
      throw new RpcFmpException( "you can't repair your original turrets" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbToken freighter = p_game.getToken( getPosition(), TokenType.Freighter );
    EbToken turret = new EbToken();
    turret.setType( TokenType.Turret );
    turret.setColor( freighter.getColor() );
    p_game.addToken( turret );
    p_game.moveToken( turret, getPosition() );
    turret.getPosition().setSector( freighter.getPosition().getNeighbourSector( getPosition() ) );
    turret.incVersion();
    EbRegistration registration = getMyRegistration(p_game);
    registration.setTurretsToRepair( registration.getTurretsToRepair() - 1 );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbToken turret = p_game.getToken( getPosition(), TokenType.Turret );
    turret.setLocation( Location.Graveyard );
    EbRegistration registration = getMyRegistration(p_game);
    registration.setTurretsToRepair( registration.getTurretsToRepair() + 1 );
  }



}
