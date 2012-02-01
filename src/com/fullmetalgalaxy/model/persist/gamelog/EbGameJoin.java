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
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent Legendre
 *
 */
public class EbGameJoin extends AnEventUser
{
  static final long serialVersionUID = 1;

  private int m_color = EnuColor.None;
  private EbPublicAccount m_account = null;

  /**
   * 
   */
  public EbGameJoin()
  {
    super();
    init();
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_color = EnuColor.None;
  }


  @Override
  public GameLogType getType()
  {
    return GameLogType.GameJoin;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( getMyRegistration(p_game) != null )
    {
      throw new RpcFmpException( "You can't join a game several times" );
    }
    if( !getEnuColor().isSingleColor() )
    {
      // not probable error
      throw new RpcFmpException( "You have to select a color before joining a game" );
    }
    if( p_game.isStarted() )
    {
      // not probable error
      throw new RpcFmpException( "You can't join a game wich is started" );
    }
    // check this color isn't already selected in this game
    EbRegistration registration = p_game.getRegistrationByColor( getColor() );
    if( registration != null )
    {
      if( registration.haveAccount() )
      {
        throw new RpcFmpException( "Color " + getEnuColor() + " was already selected in this game" );
      }
    }
    else if( p_game.getMaxNumberOfPlayer() <= p_game.getSetRegistration().size() )
    {
      // no i18n ?
      throw new RpcFmpException( "The maximum number of player is reached for this game." );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);

    EbRegistration registration = p_game.getRegistrationByColor( getColor() );
    if( registration == null )
    {
      registration = createRegistration(p_game);
    }
    registration.setAccount( getAccount() );

    // update isOpen flag
    p_game.isOpen();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    if( getAccount() != null )
    {
      str += getAccount().getPseudo();
    }
    str += " : Join - color " + getEnuColor().toString();
    return str;
  }

  private EbRegistration createRegistration(Game p_game)
  {
    assert p_game != null;
    Game game = p_game;
    EbRegistration registration = new EbRegistration();
    registration.setColor( getColor() );
    registration.setSingleColor( registration.getColor() );
    registration.setOriginalColor( registration.getColor() );
    game.addRegistration( registration );
    if( game.getCurrentTimeStep() > 1 )
    {
      int pt = EbConfigGameTime.getActionInc( game, registration )
          * (game.getCurrentTimeStep() - p_game.getEbConfigGameTime().getDeploymentTimeStep());
      if( pt > FmpConstant.maximumActionPtWithoutLanding )
      {
        pt = FmpConstant.maximumActionPtWithoutLanding;
      }
      registration.setPtAction( pt );
    }
    else
    {
      registration.setPtAction( 0 );
    }
    registration.setOrderIndex( game.getSetRegistration().size() - 1 );
    // set current player as the first player
    game.setCurrentPlayerRegistration( game.getRegistrationByOrderIndex( 0 ) );

    // create all tokens
    EbToken shipToken = new EbToken();
    shipToken.setType( TokenType.Freighter );
    game.addToken( shipToken );
    shipToken.setColor( registration.getColor() );
    shipToken.setLocation( Location.Orbit );
    // shipToken.setLastUpdate( currentDate );
    EbToken token = new EbToken();
    token.setType( TokenType.Barge );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Speedboat );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Speedboat );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Pontoon );
    game.addToken( token );
    token.setColor( EnuColor.None );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.WeatherHen );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Crab );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Heap );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Tank );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Tank );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Tank );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Tank );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Turret );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Turret );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );
    token = new EbToken();
    token.setType( TokenType.Turret );
    game.addToken( token );
    token.setColor( registration.getColor() );
    token.setBulletCount( token.getMaxBulletCount() );
    shipToken.loadToken( token );

    return registration;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbRegistration registration = getMyRegistration(p_game);
    p_game.getSetRegistration().remove( registration );
    for( EbToken freighter : p_game.getSetToken() )
    {
      if( (freighter.getType() == TokenType.Freighter)
          && (registration.getEnuColor().isColored( freighter.getColor() )) )
      {
        if( freighter.containToken() )
        {
          // TODO this isn't working !!!
          for( EbToken token : freighter.getCopyContains() )
          {
            p_game.getSetToken().remove( token );
          }
        }
        p_game.getSetToken().remove( freighter );
      }
    }
  }


  // Bean getter / setter
  // ====================
  /**
   * @return the color
   */
  public EnuColor getEnuColor()
  {
    return new EnuColor( m_color );
  }

  public int getColor()
  {
    return m_color;
  }


  /**
   * @param p_color the color to set
   */
  public void setColor(int p_value)
  {
    m_color = p_value;
  }

  /**
   * @return the account
   */
  public EbPublicAccount getAccount()
  {
    return m_account;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccount(EbPublicAccount p_account)
  {
    m_account = p_account;
  }


}
