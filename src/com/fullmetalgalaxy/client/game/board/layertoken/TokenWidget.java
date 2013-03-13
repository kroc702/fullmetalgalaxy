/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board.layertoken;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 * simply 2 images widget with a lastUpdate timestamp.
 */
public class TokenWidget
{
  private long m_lastHash = 0;

  private Image m_tokenImage = new Image();
  private Image m_iconWarningImage = new Image();


  /**
   * 
   */
  public TokenWidget()
  {
  }

  /**
   * build an integer that 'represent' the token status.
   * if graphical status of the token change, this hash shall change and vice versa
   * @param p_token
   * @return
   */
  public long getDisplayStatusHash(EbToken p_token)
  {
    long hash = p_token.getVersion() + 1;
    hash += p_token.getColor();
    hash ^= p_token.isFireDisabled() ? 0x1000000l : 0;
    hash ^= p_token.isFireDisabling() ? 0x2000000l : 0;

    Game game = GameEngine.model().getGame();
    if( game == null )
    {
      return hash;
    }
    hash += 0xFFFF * game.getLand( p_token.getPosition() ).getLandValue( game.getCurrentTide() )
        .hashCode();

    hash ^= game.isTankCheating( p_token ) ? 0x4000000l : 0;

    if( p_token.getType() == TokenType.Freighter
        && game.getCurrentTimeStep() <= game.getEbConfigGameTime().getDeploymentTimeStep() + 1 )
    {
      hash += 0xFFFFFF * game.getCurrentTimeStep();
    }
    
    return hash;
  }

  /**
   * invalid the freshness of this widget.
   * ie this widget will be redrawn next time.
   */
  public void invalidate()
  {
    m_lastHash = 0;
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
    Game game = GameEngine.model().getGame();
    if( game == null )
    {
      return true;
    }
    return m_lastHash != getDisplayStatusHash( p_token );
  }

  /**
   * @return the tokenImage
   */
  protected Image getTokenImage()
  {
    return m_tokenImage;
  }

  protected void setTokenImage(Image p_image)
  {
    m_tokenImage = p_image;
  }

  protected void setTokenImage(ImageResource p_image)
  {
    AbstractImagePrototype.create( p_image ).applyTo( m_tokenImage );
    // setTokenImage( new Image( p_image ) );
  }


  /**
   * @return the warningImage
   */
  protected Image getIconWarningImage()
  {
    return m_iconWarningImage;
  }

  protected void setIconWarningImage(Image p_image)
  {
    m_iconWarningImage = p_image;
  }

  protected void setIconWarningImage(ImageResource p_image)
  {
    AbstractImagePrototype.create( p_image ).applyTo( m_iconWarningImage );
    // setIconWarningImage( new Image( p_image ) );
  }



  /**
   * @param p_lastFireCover the lastFireCover to set
   */
  public void setLastTokenDrawn(EbToken p_token)
  {
    if( TokenImages.isBundleLoaded() )
    {
      Game game = GameEngine.model().getGame();
      assert game != null;
      m_lastHash = getDisplayStatusHash( p_token );
    }
  }



}
