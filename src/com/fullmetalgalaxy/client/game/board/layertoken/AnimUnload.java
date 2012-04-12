package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtUnLoad;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimUnload extends AnimMoveBase
{

  public AnimUnload(WgtBoardLayerToken p_layerToken, EbEvtUnLoad p_event)
  {
    super( p_layerToken, p_event );
  }

  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenCarrier( GameEngine.model().getGame() ), false );
  }

  @Override
  protected AnBoardPosition getOldPosition()
  {
    return ((EbEvtUnLoad)m_event).getTokenCarrier( GameEngine.model().getGame() ).getPosition();
  }

  @Override
  protected AnBoardPosition getNewPosition()
  {
    return m_event.getNewPosition();
  }

  @Override
  public int getDurration()
  {
    return 1000;
  }


}
