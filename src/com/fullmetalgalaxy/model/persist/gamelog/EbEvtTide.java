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
public class EbEvtTide extends AnEvent
{
  static final long serialVersionUID = 1;



  /**
   * 
   */
  public EbEvtTide()
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
    return GameLogType.EvtTide;
  }


  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    assert p_game.getCurrentTide() == getOldTide();
    if( !p_game.isStarted() )
    {
      // TODO i18n
      throw new RpcFmpException( "Cette partie n'est pas demarre" );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbGame game = p_game;
    assert game != null;
    game.setCurrentTide( game.getNextTide() );
    game.setNextTide( getNextTide() );
    game.setLastTideChange( game.getCurrentTimeStep() );
    game.invalidateFireCover();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbGame game = p_game;
    assert game != null;
    game.setNextTide( game.getCurrentTide() );
    game.setCurrentTide( getOldTide() );
    game.invalidateFireCover();
    game.setLastTideChange( getOldTideChange() );
  }



  @Override
  public void setGame(EbGame p_game)
  {
    assert p_game != null;
    setIdGame( p_game.getId() );

    setOldTide( p_game.getCurrentTide() );
    setOldTideChange( p_game.getLastTideChange() );

  }


  // Bean getter / setter
  // ====================

}
