/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtControl extends AnEventPlay
{
  static final long serialVersionUID = 1;



  /**
   * 
   */
  public EbEvtControl()
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
    setCost( 1 );
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtControl;
  }


  @Override
  public AnBoardPosition getSelectedPosition(EbGame p_game)
  {
    if( getTokenDestroyer1(p_game) != null )
    {
      return getTokenDestroyer1(p_game).getPosition();
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    assert getOldColor() == getTokenTarget(p_game).getColor();

    // check that player control destroyers
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer1(p_game).getColor() )
        || getTokenDestroyer1(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenDestroyer1(p_game)
          .getColor(), getMyRegistration(p_game).getColor() );
    }
    if( !getMyRegistration(p_game).getEnuColor().isColored( getTokenDestroyer2(p_game).getColor() )
        || getTokenDestroyer2(p_game).getColor() == EnuColor.None )
    {
      throw new RpcFmpException( RpcFmpException.CantMoveDontControl, getTokenDestroyer2(p_game)
          .getColor(), getMyRegistration(p_game).getColor() );
    }

    // check that two token are destroyer
    if( !getTokenDestroyer1( p_game ).isDestroyer() || !getTokenDestroyer2( p_game ).isDestroyer() )
    {
      throw new RpcFmpException(
          "Il vous faut deux destructeurs pour controler un vehicule adverse" );
    }

    // check the first destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer1(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantFireDisableTide, getTokenDestroyer1(p_game)
          .getType().ordinal() );
    }
    // check the second destroyer is not tide deactivated
    if( !p_game.isTokenTideActive( getTokenDestroyer2(p_game) ) )
    {
      throw new RpcFmpException( RpcFmpException.CantFireDisableTide, getTokenDestroyer2(p_game)
          .getType().ordinal() );
    }

    // check first, second destroyer and target are not under opponents fires
    EnuColor fireCover = p_game.getBoardFireCover()
        .getFireCover( getTokenTarget(p_game).getPosition() );
    fireCover.removeColor( getMyRegistration(p_game).getOriginalColor() );
    if( getTokenDestroyer1(p_game).isFireDisabled() || getTokenDestroyer2(p_game).isFireDisabled()
        || fireCover.getValue() != EnuColor.None )
    {
      throw new RpcFmpException(
          "Pour qu'un control soit possible, il faut qu'aucun des trois pions ne soit sous zone de feu adverse" );
    }


    if( !getTokenDestroyer1(p_game).isNeighbor( getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer1(p_game) + " n'est pas au contact de "
          + getTokenTarget(p_game) );
    }
    if( !getTokenDestroyer2(p_game).isNeighbor( getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer2(p_game) + " n'est pas au contact de "
          + getTokenTarget(p_game) );
    }

    // check that target isn't freighter
    if( getTokenTarget(p_game).getType() == TokenType.Freighter )
    {
      throw new RpcFmpException(
          "les astronefs ne peuvent etres control� de cette facon. Vous devez d�truire toute les tourelles puis entrer dedans" );
    }

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);

    p_game.changeTokenColor( getTokenTarget(p_game), getTokenDestroyer1(p_game).getColor() );
    getTokenTarget(p_game).incVersion();
    for( EbToken token : getTokenTarget(p_game).getSetContain() )
    {
      if( token.canBeColored() )
      {
        token.incVersion();
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);

    getTokenTarget(p_game).decVersion();
    p_game.changeTokenColor( getTokenTarget(p_game), getOldColor() );
    for( EbToken token : getTokenTarget(p_game).getSetContain() )
    {
      if( token.canBeColored() )
      {
        token.decVersion();
      }
    }
  }

}
