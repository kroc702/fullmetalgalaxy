package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public class AnimControl extends AnimEvent
{
  protected EbEvtControl m_event = null;
  private EbToken m_target = null;
  private TokenWidget m_tokenWidget = null;

  public AnimControl(WgtBoardLayerToken p_layerToken, EbEvtControl p_event)
  {
    super( p_layerToken );
    m_event = p_event;
    m_target = m_event.getTokenTarget( GameEngine.model().getGame() );
  }


  @Override
  protected void onComplete()
  {
    super.onComplete();
    m_layerToken.updateTokenWidget( m_event.getTokenTarget( GameEngine.model().getGame() ), true );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer1( GameEngine.model().getGame() ),
        false );
    m_layerToken.updateTokenWidget( m_event.getTokenDestroyer2( GameEngine.model().getGame() ),
        false );
  }



  @Override
  protected void onStart()
  {
    if( !m_layerToken.isVisible( m_target ) )
    {
      cancel();
      return;
    }
  }



  @Override
  protected void onUpdate(double p_progress)
  {
    if( m_tokenWidget == null )
    {
      m_tokenWidget = m_layerToken.getTokenWidget( m_target );
    }
    if( m_tokenWidget == null )
    {
      return;
    }

    EnuColor color = null;
    if( ((int)(p_progress * 10) % 2) == 0 )
    {
      color = m_event.getTokenDestroyer1( GameEngine.model().getGame() ).getEnuColor();
    }
    else
    {
      color = new EnuColor( m_event.getOldColor() );
    }
    
    TokenImages.getTokenImage( color, m_layerToken.getZoom().getValue(), m_target.getType(),
        m_target.getPosition().getSector() ).applyTo( m_tokenWidget.getTokenImage() );
  }



  @Override
  public int getDurration()
  {
    return 2000;
  }

}
