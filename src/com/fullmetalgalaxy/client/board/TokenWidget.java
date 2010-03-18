/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 * simply 2 images widget with a lastUpdate timestamp.
 */
public class TokenWidget
{
  private long m_lastVersion = 0;
  private LandType m_lastLand = LandType.None;
  private boolean m_wasFireDisable = false;
  private Image m_tokenImage = new Image();
  private Image m_iconWarningImage = new Image();


  /**
   * 
   */
  public TokenWidget()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * invalid the freshness of this widget.
   * ie this widget will be redrawn next time.
   */
  public void invalidate()
  {
    m_lastVersion = 0;
    m_lastLand = LandType.None;
    m_wasFireDisable = false;
  }

  public void setVisible(boolean p_isVisible)
  {
    if( m_tokenImage != null )
    {
      m_tokenImage.setVisible( p_isVisible );
    }
    if( m_iconWarningImage != null )
    {
      m_iconWarningImage.setVisible( p_isVisible );
    }
  }

  public boolean isUpdateRequired(EbToken p_token)
  {
    EbGame game = p_token.getGame();
    if( game == null )
    {
      return true;
    }
    if( (p_token.getVersion() != getLastUpdate())
        || (p_token.isFireDisabled() != m_wasFireDisable)
        || (game.getLand( p_token.getPosition() ).getLandValue( game.getCurrentTide() ) != m_lastLand) )
    {
      return true;
    }
    return false;
  }

  /**
   * @return the p_lastUpdate
   */
  protected long getLastUpdate()
  {
    return m_lastVersion;
  }


  /**
   * @return the tokenImage
   */
  protected Image getTokenImage()
  {
    return m_tokenImage;
  }


  /**
   * @return the warningImage
   */
  protected Image getIconWarningImage()
  {
    return m_iconWarningImage;
  }

  /**
   * @return the lastLand
   */
  private LandType getLastLand()
  {
    return m_lastLand;
  }


  /**
   * @param p_lastFireCover the lastFireCover to set
   */
  public void setLastTokenDrawn(EbToken p_token)
  {
    EbGame game = p_token.getGame();
    assert game != null;
    m_lastVersion = p_token.getVersion();
    m_wasFireDisable = p_token.isFireDisabled();
    m_lastLand = game.getLand( p_token.getPosition() ).getLandValue( game.getCurrentTide() );
  }



}
