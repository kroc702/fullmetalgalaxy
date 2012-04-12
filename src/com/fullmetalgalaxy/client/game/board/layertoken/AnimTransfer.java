package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTransfer;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimTransfer extends AnimMoveBase
{

  public AnimTransfer(WgtBoardLayerToken p_layerToken, EbEvtTransfer p_event)
  {
    super( p_layerToken, p_event );
  }

  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenCarrier( GameEngine.model().getGame() ), false );
    m_layerToken.updateTokenWidget( m_event.getNewTokenCarrier( GameEngine.model().getGame() ),
        false );
  }

  @Override
  protected AnBoardPosition getOldPosition()
  {
    return ((EbEvtTransfer)m_event).getTokenCarrier( GameEngine.model().getGame() ).getPosition();
  }

  @Override
  protected AnBoardPosition getNewPosition()
  {
    return ((EbEvtTransfer)m_event).getNewTokenCarrier( GameEngine.model().getGame() )
        .getPosition();
  }


  @Override
  public int getDurration()
  {
    return 1000;
  }

}
