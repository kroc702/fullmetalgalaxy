package com.fullmetalgalaxy.client.game.board.layertoken;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.google.gwt.user.client.DOM;

/**
 * 
 * @author Vincent
 *
 * this class make an animation from game action EbEvtMove
 */
public abstract class AnimMoveBase extends AnimEvent
{
  protected AnEventPlay m_event = null;
  protected EbToken m_token = null;
  private TokenWidget m_tokenWidget = null;
  private AnPair wgtPxOldPosition = null;
  private AnPair wgtPxNewPosition = null;

  public AnimMoveBase(WgtBoardLayerToken p_layerToken, AnEventPlay p_event)
  {
    super( p_layerToken );
    m_event = p_event;
    m_token = m_event.getToken( GameEngine.model().getGame() );
  }

  protected abstract AnBoardPosition getOldPosition();

  protected abstract AnBoardPosition getNewPosition();


  @Override
  protected void onComplete()
  {
    super.onComplete();
    DOM.setStyleAttribute( m_tokenWidget.getTokenImage().getElement(), "zIndex",
        Integer.toString( m_token.getZIndex() ) );
  }



  @Override
  protected void onStart()
  {
    if( !m_layerToken.isVisible( getOldPosition() ) )
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
      m_tokenWidget = m_layerToken.getTokenWidget( m_token, true );
      if( m_tokenWidget != null )
      {
        m_layerToken.add( m_tokenWidget.getTokenImage() );
        TokenImages.getTokenImage( m_token.getEnuColor(), m_layerToken.getZoom().getValue(),
            m_token.getType(), getNewPosition().getSector() ).applyTo(
            m_tokenWidget.getTokenImage() );
        m_tokenWidget.getTokenImage().setVisible( true );
        DOM.setStyleAttribute( m_tokenWidget.getTokenImage().getElement(), "zIndex", "999" );


        wgtPxOldPosition = m_layerToken.convertHexPositionToPixPosition( getOldPosition() );
        wgtPxNewPosition = m_layerToken.convertHexPositionToPixPosition( getNewPosition() );
      }
    }
    if( m_tokenWidget == null )
    {
      return;
    }
    AnPair wgtPxPosition = new AnPair(
        (int)((wgtPxNewPosition.getX() - wgtPxOldPosition.getX()) * p_progress),
        (int)((wgtPxNewPosition.getY() - wgtPxOldPosition.getY()) * p_progress) );
    wgtPxPosition.setX( wgtPxPosition.getX() + wgtPxOldPosition.getX() );
    wgtPxPosition.setY( wgtPxPosition.getY() + wgtPxOldPosition.getY()
        + getLandPixOffset( p_progress ) );
    m_layerToken.setWidgetPixPosition( m_tokenWidget.getTokenImage(), wgtPxPosition );
  }


  protected int getLandPixOffset(double p_progress)
  {
    return 0;
  }



}
