/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtFire extends AnEventPlay
{
  static final long serialVersionUID = 1;



  /**
   * 
   */
  public EbEvtFire()
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
    return GameLogType.EvtFire;
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
    // check that player control destroyers
    if( (getTokenTarget(p_game).canBeColored())
        && (getMyRegistration(p_game).getEnuColor().isColored( getTokenTarget(p_game).getColor() )) )
    {
      throw new RpcFmpException( "Vous ne pouvez pas détruire vos propre pions" );
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

    if( (!(p_game.getLastLog() instanceof EbEvtMove))
        || (((EbEvtMove)p_game.getLastLog()).getToken(p_game) != getTokenDestroyer1(p_game)) )
    {
      // check the first destroyer is not fire deactivated
      if( !p_game.isTokenFireActive( getMyRegistration(p_game).getEnuColor(), getTokenDestroyer1(p_game) ) )
      {
        throw new RpcFmpException( RpcFmpException.CantFireDisableFire, getTokenDestroyer1(p_game)
            .getType().ordinal(), p_game.getOpponentFireCover( getTokenDestroyer1(p_game) ).getValue() );
      }
    }
    if( getTokenDestroyer2( p_game ).isFireDisabled() )
    {
      throw new RpcFmpException( RpcFmpException.CantFireDisableFire, getTokenDestroyer2( p_game )
          .getType().ordinal(), p_game.getOpponentFireCover( getTokenDestroyer2( p_game ) )
          .getValue() );
    }

    if( !p_game.canTokenFireOn( getTokenDestroyer1(p_game), getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer1(p_game) + " ne peu pas tirer sur " + getTokenTarget(p_game) );
    }
    if( !p_game.canTokenFireOn( getTokenDestroyer2(p_game), getTokenTarget(p_game) ) )
    {
      throw new RpcFmpException( getTokenDestroyer2(p_game) + " ne peu pas tirer sur " + getTokenTarget(p_game) );
    }

    if( getTokenDestroyer1(p_game).getBulletCount() <= 0 )
    {
      throw new RpcFmpException( getTokenDestroyer1(p_game) + " n'a plus de munitions" );
    }
    if( getTokenDestroyer2(p_game).getBulletCount() <= 0 )
    {
      throw new RpcFmpException( getTokenDestroyer2(p_game) + " n'a plus de munitions" );
    }

    // check that target isn't freighter
    if( getTokenTarget(p_game).getType() == TokenType.Freighter )
    {
      throw new RpcFmpException(
          "les astronefs ne peuvent etres detruits. Ils peuvent cependant �tres control�" );
    }

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  // @Override
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    p_game.moveToken( getTokenTarget(p_game), Location.Graveyard );
    getTokenTarget(p_game).incVersion();
    getTokenDestroyer1(p_game).setBulletCount( getTokenDestroyer1(p_game).getBulletCount() - 1 );
    getTokenDestroyer1(p_game).incVersion();
    getTokenDestroyer2(p_game).setBulletCount( getTokenDestroyer2(p_game).getBulletCount() - 1 );
    getTokenDestroyer2(p_game).incVersion();
    // if token is a pontoon, check that other pontoon are linked to ground
    setMiscTokenIds( null );
    if( getToken(p_game).getType() == TokenType.Pontoon )
    {
      for( Sector sector : Sector.values() )
      {
        EbToken otherPontoon = p_game.getToken( getOldPosition().getNeighbour( sector ),
            TokenType.Pontoon );
        if( otherPontoon != null )
        {
          if( !p_game.isPontoonLinkToGround( otherPontoon ) )
          {
            chainRemovePontoon( p_game, otherPontoon );
          }
        }
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  // @Override
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    p_game.moveToken( getTokenTarget(p_game), getOldPosition() );
    getTokenTarget(p_game).decVersion();
    getTokenDestroyer1(p_game).setBulletCount( getTokenDestroyer1(p_game).getBulletCount() + 1 );
    getTokenDestroyer1(p_game).decVersion();
    getTokenDestroyer2(p_game).setBulletCount( getTokenDestroyer2(p_game).getBulletCount() + 1 );
    getTokenDestroyer2(p_game).decVersion();
    p_game.getBoardFireCover().checkFireDisableFlag( getTokenDestroyer1( p_game ) );
    p_game.getBoardFireCover().checkFireDisableFlag( getTokenDestroyer2( p_game ) );
    // put back pontoon if there is some
    if( getMiscTokenIds() != null )
    {
      for( Long id : getMiscTokenIds() )
      {
        EbToken token = p_game.getToken( id );
        if( (token != null) && (token.getLocation() == Location.Graveyard) )
        {
          p_game.moveToken( token, token.getPosition() );
          token.decVersion();
        }
      }
    }
  }


}
