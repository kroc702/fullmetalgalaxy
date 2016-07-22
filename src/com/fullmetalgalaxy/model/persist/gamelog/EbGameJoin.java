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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
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
  private Company m_company = Company.Freelancer;
  private int m_actionPointBonus = 0;

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
      throw new RpcFmpException( "You can't join a game several times", this );
    }
    if( getColor() == EnuColor.None )
    {
      // not probable error
      throw new RpcFmpException( "You have to select a color before joining a game", this );
    }
    if( p_game.getStatus() != GameStatus.Open )
    {
      // not probable error
      throw new RpcFmpException( "You can't join a game wich is started", this );
    }
    // check this color isn't already selected in this game
    EbRegistration registration = p_game.getRegistrationByColor( getColor() );
    if( registration != null )
    {
      if( registration.haveAccount() )
      {
        throw new RpcFmpException( "Color " + getEnuColor() + " was already selected in this game", this );
      }
    }
    else if( p_game.getMaxNumberOfPlayer() <= p_game.getSetRegistration().size() )
    {
      // no i18n ?
      throw new RpcFmpException( "The maximum number of player is reached for this game.", this );
    }
    if( !p_game.isTeamAllowed() && getCompany() != Company.Freelancer
        && p_game.getTeam( getCompany() ) != null )
    {
      // no i18n as HMI shall prevent this error
      throw new RpcFmpException( "This gaame don't allow team", this );
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
    else if( p_game.getCurrentTimeStep() < p_game.getEbConfigGameTime().getDeploymentTimeStep() )
    {
      // this is due to a bug where a player manage to register a game without creating his freighter
      createTokens( p_game, registration.getColor() );
    }
    if( registration.isReplacement() && registration.getOriginalAccountId() == getAccount().getId() )
    {
      // player replace himself
      registration.setOriginalAccountId( 0 );
    }
    registration.setAccount( getAccount() );
    if( p_game.isTimeStepParallelHidden( p_game.getCurrentTimeStep() ) )
    {
      p_game.getCurrentPlayerIds().add( registration.getId() );
    }

    // update isOpen flag
    p_game.updateOpenPauseStatus();

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

  private EbRegistration createRegistration(Game p_game) throws RpcFmpException
  {
    assert p_game != null;
    Game game = p_game;
    // create registration
    EbRegistration registration = new EbRegistration();
    registration.setColor( getColor() );
    registration.setOriginalColor( registration.getColor() );
    registration.setActionPointBonus( getActionPointBonus() );
    game.addRegistration( registration );
    if( game.getCurrentTimeStep() > 1 )
    {
      int pt = registration.getActionInc( game )
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

    // find or create team
    EbTeam team = p_game.getTeam( getCompany() );
    if( team == null || getCompany() == Company.Freelancer )
    {
      team = new EbTeam();
      team.setCompany( getCompany() );
      team.setFireColor( registration.getEnuColor().getSingleColor().getValue() );
      team.setOrderIndex( p_game.getTeams().size() );
      p_game.addTeam( team );
    }
    registration.setTeamId( team.getId() );
    team.getPlayerIds().add( registration.getId() );
    team.clearColorsCache();

    // set current player as the first player
    game.getCurrentPlayerIds().clear();
    game.getCurrentPlayerIds().add( game.getTeamByOrderIndex( 0 ).getPlayerIds().get( 0 ) );

    // create all tokens
    createTokens( p_game, registration.getColor() );

    return registration;
  }

  /**
   * create all token for a given color. if tokens already exists, this method may update initial hold.
   * @param color
   * @throws RpcFmpException 
   */
  public void createTokens(Game p_game, int color) throws RpcFmpException
  {
    if( !EnuColor.isSingleColor( color )){
      throw new RpcFmpException( "can't create token with several color");
    }
    List<EbToken> freighters = p_game.getAllFreighter( color );
    EbToken shipToken = null;
    Map<TokenType, Integer> currentHolds = new HashMap<TokenType,Integer>();
    if( !freighters.isEmpty() )
    {
      shipToken = freighters.get( 0 );
      for(EbToken token : shipToken.getContains() ) {
        Integer current = currentHolds.get( token.getType() );
        if( current == null ) {
          current = 0;
        }
        current++;
        currentHolds.put( token.getType(), current );
      }
    }
    else
    {
      shipToken = new EbToken();
      shipToken.setType( TokenType.Freighter );
      shipToken.setColor( color );
      shipToken.setLocation( Location.Orbit );
      if( shipToken.isTrancient() || p_game.getToken( shipToken.getId() ) == null )
      {
        p_game.addToken( shipToken );
      }
      else
      {
        // warning: ore stored in action are not the same instance as in game
        shipToken = p_game.getToken( shipToken.getId() );
      }
      // shipToken.setLastUpdate( currentDate );
    }

    // create initial freighter holds
    // use TokenType values array instead of holds entryset to always keep the same order (so the same id)
    for( TokenType type : TokenType.values() )
    {
      if( type == TokenType.Freighter )
      {
        continue;
      }
      Integer typeCurrentCount = currentHolds.get( type );
      if( typeCurrentCount == null )
      {
        typeCurrentCount = 0;
      }
      Integer typeTargetCount = p_game.getInitialHolds().get( type );
      if( typeTargetCount == null )
      {
        typeTargetCount = 0;
      }
      for( int i = 0; i < typeTargetCount - typeCurrentCount; i++ )
      {
        EbToken token = new EbToken();
        token.setType( type );
        token.setLocation( Location.Token );
        if( token.canBeColored() )
        {
          token.setColor( color );
        }
        else
        {
          token.setColor( EnuColor.None );
        }
        token.setBulletCount( token.getType().getMaxBulletCount() );
        if( token.isTrancient() || p_game.getToken( token.getId() ) == null )
        {
          p_game.addToken( token );
        }
        else
        {
          // warning: ore stored in action are not the same instance as in game
          token = p_game.getToken( token.getId() );
        }
        shipToken.loadToken( token );
      }
    }

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
    Set<EbToken> tokenToRemove = new HashSet<EbToken>();
    for( EbToken freighter : p_game.getSetToken() )
    {
      if( (freighter.getType() == TokenType.Freighter)
          && (registration.getEnuColor().isColored( freighter.getColor() )) )
      {
        if( freighter.containToken() )
        {
          for( EbToken token : freighter.getContains() )
          {
            tokenToRemove.add( token );
          }
        }
        tokenToRemove.add( freighter );
      }
    }
    p_game.getSetToken().removeAll( tokenToRemove );
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
    setAccountId( m_account.getId() );
  }

  public Company getCompany()
  {
    return m_company;
  }

  public void setCompany(Company p_company)
  {
    m_company = p_company;
  }

  public int getActionPointBonus()
  {
    return m_actionPointBonus;
  }

  public void setActionPointBonus(int p_actionPointBonus)
  {
    m_actionPointBonus = p_actionPointBonus;
  }


}
