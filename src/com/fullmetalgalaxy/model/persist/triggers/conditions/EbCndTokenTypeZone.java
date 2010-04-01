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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.conditions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.triggers.AnGameZone;



/**
 * @author Vincent Legendre
 * same as EbCndTokenZone, but we don't known token id, just few informations
 */
public class EbCndTokenTypeZone extends AnCondition
{
  static final long serialVersionUID = 125;

  private AnGameZone m_zone = new AnGameZone();
  private TokenType m_tokenType = null;
  private int m_color = EnuColor.Unknown;
  private EbRegistration m_player = null;

  /**
   * 
   */
  public EbCndTokenTypeZone()
  {
    init();
  }

  private void init()
  {
    m_zone = new AnGameZone();
    m_tokenType = null;
    m_color = EnuColor.Unknown;
    m_player = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  /**
   * true if all provided information are corrects on a token located in zone
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#isTrue(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public boolean isTrue(EbGame p_game)
  {
    return getTheToken( p_game ) != null;
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition#getActParams(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public List<Object> getActParams(EbGame p_game)
  {
    List<Object> params = new ArrayList<Object>();
    params.add( getTheToken( p_game ) );
    return params;
  }

  private EbToken getTheToken(EbGame p_game)
  {
    for( EbToken token : p_game.getSetToken() )
    {
      boolean isTheOne = true;
      if( getTokenType() != null && token.getType() != getTokenType() )
      {
        isTheOne = false;
      }
      if( isTheOne && getColor() != EnuColor.Unknown && token.getColor() != getColor() )
      {
        isTheOne = false;
      }
      if( isTheOne && getPlayer() != null && !getPlayer().getEnuColor().contain( getColor() ) )
      {
        isTheOne = false;
      }
      if( isTheOne && getZone().contain( token ) )
      {
        return token;
      }
    }
    return null;
  }

  /**
   * @return the zone
   */
  public AnGameZone getZone()
  {
    return m_zone;
  }

  /**
   * @param p_zone the zone to set
   */
  public void setZone(AnGameZone p_zone)
  {
    m_zone = p_zone;
  }

  /**
   * @return the tokenType
   */
  public TokenType getTokenType()
  {
    return m_tokenType;
  }

  /**
   * @param p_tokenType the tokenType to set
   */
  public void setTokenType(TokenType p_tokenType)
  {
    m_tokenType = p_tokenType;
  }

  /**
   * @return the color
   */
  public int getColor()
  {
    return m_color;
  }

  /**
   * @param p_color the color to set
   */
  public void setColor(int p_color)
  {
    m_color = p_color;
  }

  /**
   * @return the owner
   */
  public EbRegistration getPlayer()
  {
    return m_player;
  }

  /**
   * @param p_owner the owner to set
   */
  public void setPlayer(EbRegistration p_owner)
  {
    m_player = p_owner;
  }


}
