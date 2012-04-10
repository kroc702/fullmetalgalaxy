package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.AnimationFrames;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimFire extends AnimEvent
{
  protected EbEvtFire m_event = null;
  private EbToken m_target = null;
  private TokenWidget m_tokenWidget = null;
  private AbstractImagePrototype m_lastFrame = null;
  private int m_landPixOffset = 0;

  public AnimFire(WgtBoardLayerToken p_layerToken, EbEvtFire p_event)
  {
    super( p_layerToken );
    m_event = p_event;
    m_target = m_event.getTokenTarget( GameEngine.model().getGame() );
  }


  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenTarget( GameEngine.model().getGame() ), false );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer1( GameEngine.model().getGame() ),
        false );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer2( GameEngine.model().getGame() ),
        false );
  }



  @Override
  protected void onStart()
  {
  }


  private AbstractImagePrototype getTacticFrame(double p_progress)
  {
    switch( (int)(p_progress * 16) )
    {
    case 0:
      return AnimationFrames.s_instance.tactic_blast01();
    case 1:
      return AnimationFrames.s_instance.tactic_blast02();
    case 2:
      return AnimationFrames.s_instance.tactic_blast03();
    case 3:
      return AnimationFrames.s_instance.tactic_blast04();
    case 4:
      return AnimationFrames.s_instance.tactic_blast05();
    case 5:
      return AnimationFrames.s_instance.tactic_blast06();
    case 6:
      return AnimationFrames.s_instance.tactic_blast07();
    case 7:
      return AnimationFrames.s_instance.tactic_blast08();
    case 8:
      return AnimationFrames.s_instance.tactic_blast09();
    case 9:
      return AnimationFrames.s_instance.tactic_blast10();
    case 10:
      return AnimationFrames.s_instance.tactic_blast11();
    case 11:
      return AnimationFrames.s_instance.tactic_blast12();
    case 12:
      return AnimationFrames.s_instance.tactic_blast13();
    case 13:
      return AnimationFrames.s_instance.tactic_blast14();
    case 14:
      return AnimationFrames.s_instance.tactic_blast15();
    default:
    case 15:
      return AnimationFrames.s_instance.tactic_blast16();
    }
  }

  private AbstractImagePrototype getStrategyFrame(double p_progress)
  {
    switch( (int)(p_progress * 14) )
    {
    case 0:
      return AnimationFrames.s_instance.strategy_blast01();
    case 1:
      return AnimationFrames.s_instance.strategy_blast02();
    case 2:
      return AnimationFrames.s_instance.strategy_blast03();
    case 3:
      return AnimationFrames.s_instance.strategy_blast04();
    case 4:
      return AnimationFrames.s_instance.strategy_blast05();
    case 5:
      return AnimationFrames.s_instance.strategy_blast06();
    case 6:
      return AnimationFrames.s_instance.strategy_blast07();
    case 7:
      return AnimationFrames.s_instance.strategy_blast08();
    case 8:
      return AnimationFrames.s_instance.strategy_blast09();
    case 9:
      return AnimationFrames.s_instance.strategy_blast10();
    case 10:
      return AnimationFrames.s_instance.strategy_blast11();
    case 11:
      return AnimationFrames.s_instance.strategy_blast12();
    case 12:
      return AnimationFrames.s_instance.strategy_blast13();
    default:
    case 13:
      return AnimationFrames.s_instance.strategy_blast14();
    }
  }


  @Override
  protected void onUpdate(double p_progress)
  {
    if( m_tokenWidget == null )
    {
      return;
    }
    AbstractImagePrototype newFrame = null;
    if( m_layerToken.getZoom().getValue() == EnuZoom.Medium )
    {
      newFrame = getTacticFrame( p_progress );
    }
    else
    {
      newFrame = getStrategyFrame( p_progress );
    }
    if( newFrame != m_lastFrame )
    {
      m_lastFrame = newFrame;
      m_layerToken.addWarningImage( m_tokenWidget.getTokenImage(), m_lastFrame, m_target,
          m_landPixOffset );
    }
  }


  @Override
  public int getDurration()
  {
    return 2000;
  }


  @Override
  public void run()
  {
    if( m_tokenWidget == null )
    {
      m_tokenWidget = m_layerToken.getTokenWidget( m_target );
    }
    if( m_tokenWidget == null )
    {
      cancel();
    }
    else
    {
      m_landPixOffset = m_target.getLandPixOffset( GameEngine.model().getGame() );
      run( getDurration(), m_tokenWidget.getTokenImage().getElement() );
    }
  }

}
