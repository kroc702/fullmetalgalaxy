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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
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
  private boolean m_wasFireDisabling = false;
  private boolean m_wasTankCheating = false;
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
    m_wasFireDisabling = false;
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
    Game game = ModelFmpMain.model().getGame();
    if( game == null )
    {
      return true;
    }
    if( (p_token.getVersion() != getLastUpdate())
        || (p_token.isFireDisabled() != m_wasFireDisable)
        || (p_token.isFireDisabling() != m_wasFireDisabling)
        || (game.getLand( p_token.getPosition() ).getLandValue( game.getCurrentTide() ) != getLastLand())
        || (game.isTankCheating( p_token ) != m_wasTankCheating)
        || ((p_token.getType() == TokenType.Freighter) && (game.getCurrentTimeStep() <= game.getEbConfigGameTime().getDeploymentTimeStep()+1)) )
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
    Game game = ModelFmpMain.model().getGame();
    assert game != null;
    m_lastVersion = p_token.getVersion();
    m_wasFireDisable = p_token.isFireDisabled();
    m_wasFireDisabling = p_token.isFireDisabling();
    m_wasTankCheating = game.isTankCheating( p_token );
    m_lastLand = game.getLand( p_token.getPosition() ).getLandValue( game.getCurrentTide() );
  }



}
