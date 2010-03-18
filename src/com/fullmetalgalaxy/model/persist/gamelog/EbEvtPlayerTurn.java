/**
 * 
 */
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 * change currents player turns and update action point for next player.
 */
public class EbEvtPlayerTurn extends AnEvent
{
  static final long serialVersionUID = 1;



  /**
   * 
   */
  public EbEvtPlayerTurn()
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
    return GameLogType.EvtPlayerTurn;
  }


  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( !p_game.isStarted() )
    {
      // TODO i18n
      throw new RpcFmpException( "Cette partie n'est pas demarre" );
    }
    if( p_game.isFinished() )
    {
      throw new RpcFmpException( "Cette partie est termine" );
    }
    if( p_game.isAsynchron() )
    {
      throw new RpcFmpException(
          "Cette partie ne se joue pas en tour par tour mais en mode asynchrone" );
    }
    if( !isAuto() && (getAccountId() != p_game.getCurrentPlayerRegistration().getAccountId()) )
    {
      throw new RpcFmpException( "Seul le joueur dont c'est le tour peut ecourter sont tour de jeu" );
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
    // update current player end turn date to the less optimistic date.
    EbRegistration previousPlayer = game.getPreviousPlayerRegistration();
    long endTurn = 0;
    if( previousPlayer.getEndTurnDate() != null )
    {
      endTurn = previousPlayer.getEndTurnDate().getTime()
          + game.getEbConfigGameTime().getTimeStepDurationInMili();
    }
    else
    {
      endTurn = game.getEbConfigGameTime().getTimeStepDurationInMili()
          * game.getCurrentNumberOfRegiteredPlayer();
    }
    game.getCurrentPlayerRegistration().setEndTurnDate( new Date( endTurn ) );
    // next player
    EbRegistration nextPlayerRegistration = game.getNextPlayerRegistration();
    if( nextPlayerRegistration.getOrderIndex() <= game.getCurrentPlayerRegistration()
        .getOrderIndex() )
    {
      // next turn !
      game.setCurrentTimeStep( game.getCurrentTimeStep() + 1 );
      // update all tokens bullets count
      for( EbToken token : p_game.getSetToken() )
      {
        if( token.getBulletCount() < token.getMaxBulletCount() )
        {
          token.setBulletCount( token.getBulletCount()
              + game.getEbConfigGameTime().getBulletCountIncrement() );
          if( token.getBulletCount() > token.getMaxBulletCount() )
          {
            token.setBulletCount( token.getMaxBulletCount() );
          }
        }
      }
    }

    int actionInc = game.getEbConfigGameTime().getActionPtPerTimeStep();
    if( game.getCurrentTimeStep() == 0 )
    {
      actionInc = 0;
    }
    if( game.getCurrentTimeStep() == 1 )
    {
      actionInc = 5;
    }
    if( game.getCurrentTimeStep() == 2 )
    {
      actionInc = 10;
    }
    int actionExtraPoint = game.getEbConfigGameTime().getActionPtPerExtraShip()
        * (nextPlayerRegistration.getEnuColor().getNbColor() - 1);
    actionInc += actionExtraPoint;
    int actionPt = nextPlayerRegistration.getPtAction() + actionInc;
    if( actionPt > game.getEbConfigGameVariant().getActionPtMaxReserve() + actionExtraPoint )
    {
      actionPt = game.getEbConfigGameVariant().getActionPtMaxReserve() + actionExtraPoint;
    }
    nextPlayerRegistration.setPtAction( actionPt );
    endTurn = getLastUpdate().getTime() + game.getEbConfigGameTime().getTimeStepDurationInMili();
    if( (nextPlayerRegistration.getEndTurnDate() != null)
        && (nextPlayerRegistration.getEndTurnDate().before( new Date( endTurn ) )) )
    {
      nextPlayerRegistration.setEndTurnDate( new Date( endTurn ) );
    }
    game.setCurrentPlayerRegistration( nextPlayerRegistration );
  }

  /**
   * note that this method do not set the player's end turn date.
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbGame game = p_game;
    assert game != null;

    // current player action points
    int actionInc = game.getEbConfigGameTime().getActionPtPerTimeStep();
    if( game.getCurrentTimeStep() == 0 )
    {
      actionInc = 0;
    }
    if( game.getCurrentTimeStep() == 1 )
    {
      actionInc = 5;
    }
    if( game.getCurrentTimeStep() == 2 )
    {
      actionInc = 10;
    }
    int actionExtraPoint = game.getEbConfigGameTime().getActionPtPerExtraShip()
        * (game.getCurrentPlayerRegistration().getEnuColor().getNbColor() - 1);
    actionInc += actionExtraPoint;
    int actionPt = game.getCurrentPlayerRegistration().getPtAction() - actionInc;
    if( actionPt < 0 )
    {
      actionPt = 0;
    }
    game.getCurrentPlayerRegistration().setPtAction( actionPt );

    // find index player
    int index = game.getCurrentPlayerRegistration().getOrderIndex();
    // previous player
    EbRegistration registration = null;
    do
    {
      index--;
      if( index < 0 )
      {
        // previous turn !
        index = game.getSetRegistration().size() - 1;
      }
      registration = game.getRegistrationByOrderIndex( index );
      assert registration != null;
    } while( registration.getColor() == EnuColor.None );

    game.setCurrentPlayerRegistration( registration );

  }

}
