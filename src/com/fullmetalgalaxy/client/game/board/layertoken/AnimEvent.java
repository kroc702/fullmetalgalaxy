package com.fullmetalgalaxy.client.game.board.layertoken;

import com.google.gwt.animation.client.Animation;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public abstract class AnimEvent extends Animation
{
  protected WgtBoardLayerToken m_layerToken = null;

  public AnimEvent(WgtBoardLayerToken p_layerToken)
  {
    m_layerToken = p_layerToken;
  }

  /**
   * 
   * @return animation duration in milliseconds
   */
  public abstract int getDurration();

  /**
   * animation on game event should know their duration
   */
  public void run()
  {
    run( getDurration() );
  }


  @Override
  protected double interpolate(double p_progress)
  {
    return p_progress;
  }

  @Override
  protected void onComplete()
  {
    m_layerToken.nextAnimation();
  }


  @Override
  protected void onCancel()
  {
    m_layerToken.nextAnimation();
  }
  



}
