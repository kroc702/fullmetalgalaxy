package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimMove extends AnimMoveBase
{
  private int landPixOffset = 0;

  public AnimMove(WgtBoardLayerToken p_layerToken, EbEvtMove p_event)
  {
    super( p_layerToken, p_event );
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    landPixOffset = GameEngine.model().getGame().getLandPixOffset( getNewPosition() );
  }

  @Override
  protected AnBoardPosition getOldPosition()
  {
    return m_event.getOldPosition();
  }

  @Override
  protected AnBoardPosition getNewPosition()
  {
    return m_event.getNewPosition();
  }

  @Override
  protected int getLandPixOffset(double p_progress)
  {
    if( m_layerToken.getZoom().getValue() == EnuZoom.Medium
        && GameEngine.model().getGame().getLand( getOldPosition() )
            .getLandValue( GameEngine.model().getGame().getCurrentTide() ) != LandType.Sea )
    {
      return landPixOffset + ((int)(p_progress * 10) % 2);
    }
    return landPixOffset;
  }

  @Override
  public int getDurration()
  {
    return 700;
  }

}
