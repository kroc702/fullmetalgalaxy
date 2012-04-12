package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLoad;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimLoad extends AnimMoveBase
{

  public AnimLoad(WgtBoardLayerToken p_layerToken, EbEvtLoad p_event)
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
    return m_event.getOldPosition();
  }

  @Override
  protected AnBoardPosition getNewPosition()
  {
    return ((EbEvtLoad)m_event).getTokenCarrier( GameEngine.model().getGame() ).getPosition();
  }

  @Override
  public int getDurration()
  {
    return 1000;
  }

}
