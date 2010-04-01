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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.ArrayList;
import java.util.Date;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 * it is the base class to represent any event or action which can be performed on to a game.
 */
public class AnEvent extends EbBase
{
  static final long serialVersionUID = 1;

  private Date m_lastUpdate = null;

  private GameLogType m_type = null;

  private ArrayList<Long> m_miscTokenIds = null;

  private int m_factoryIndex = 0;
  private ArrayList<Long> m_newEbBaseList = null;

  private Date m_oldUpdate = null;

  private long m_idGame = 0;


  // these data come from all sub class. put it here as app engine doesn't
  // support polymorphic relation.
  private Date m_oldTimeStepChange = null;
  private Tide m_oldTide = null;
  private int m_oldTideChange = 0;
  private Tide m_nextTide = null;
  private String m_message = null;
  private String m_title = null;
  private long m_accountId = 0L;

  private boolean m_auto = false;

  private String m_remoteAddr = null;
  /**
   * action point cost of this action.
   */
  private int m_cost = 0;

  private int m_color = EnuColor.None;

  private AnBoardPosition m_newPosition = null;
  private EbBase m_packedToken = null;
  private EbBase m_packedTokenCarrier = null;

  private EbBase m_packedNewTokenCarrier = null;

  private AnBoardPosition m_oldPosition = null;
  private boolean m_backInOrbit = false;

  private AnBoardPosition m_position = null;

  private int m_oldColor = EnuColor.None;

  private EbBase m_packedTokenDestroyer1 = null;
  private EbBase m_packedTokenDestroyer2 = null;

  private EbBase m_packedOldRegistration = null;

  private TokenType m_constructType = TokenType.None;


  transient private boolean m_isPersistent = false;



  /**
   * 
   */
  public AnEvent()
  {
    super();
    init();
  }

  public AnEvent(AnEvent p_event)
  {
    super( p_event );
    setAnEvent( p_event );
  }

  public void setAnEvent(AnEvent p_event)
  {
    assert p_event != null;
    if( p_event.getId() != 0 && getId() == 0 )
    {
      setId( p_event.getId() );

      // m_isPersistent = true;

      // setKey( KeyFactory.createKey( p_event.getKey().getParent(), "AnEvent",
      // p_event.getKey()
      // .getId() ) );
    }
    setLastUpdate( p_event.getLastUpdate() );

    m_type = p_event.getType();

    m_miscTokenIds = p_event.getMiscTokenIds();
    m_factoryIndex = p_event.getFactoryIndex();
    m_newEbBaseList = p_event.getNewEbBaseList();
    m_oldUpdate = p_event.getOldUpdate();
    m_idGame = p_event.getIdGame();
    m_oldTimeStepChange = p_event.getOldTimeStepChange();
    m_oldTide = p_event.getOldTide();
    m_oldTideChange = p_event.getOldTideChange();
    m_nextTide = p_event.getNextTide();
    m_message = p_event.getMessage();
    m_title = p_event.getTitle();
    m_accountId = p_event.getAccountId();
    m_auto = p_event.isAuto();
    m_remoteAddr = p_event.getRemoteAddr();
    m_cost = p_event.getCost();
    m_color = p_event.getColor();
    m_newPosition = p_event.getNewPosition();
    m_packedToken = p_event.getPackedToken();
    m_packedTokenCarrier = p_event.getPackedTokenCarrier();
    m_packedNewTokenCarrier = p_event.m_packedNewTokenCarrier;
    m_oldPosition = p_event.getOldPosition();
    m_backInOrbit = p_event.isBackInOrbit();
    m_position = p_event.getPosition();
    m_oldColor = p_event.getOldColor();
    m_packedTokenDestroyer1 = p_event.m_packedTokenDestroyer1;
    m_packedTokenDestroyer2 = p_event.m_packedTokenDestroyer2;
    m_packedOldRegistration = p_event.m_packedOldRegistration;
    m_constructType = p_event.getConstructType();
  }

  @Override
  public boolean isTrancient()
  {
    if( m_isPersistent )
    {
      return false;
    }
    return super.isTrancient();
  }


  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    setLastUpdate( new Date( System.currentTimeMillis() ) );
    m_miscTokenIds = null;
    m_factoryIndex = 0;
    m_newEbBaseList = null;
    m_oldUpdate = null;
    m_idGame = 0;
    m_type = null;

    m_oldTimeStepChange = null;
    m_oldTide = null;
    m_nextTide = null;
    m_oldTideChange = 0;
    m_message = null;
    m_title = null;
    m_accountId = 0;
    m_cost = 0;
    m_color = EnuColor.None;
    m_newPosition = null;
    m_packedToken = null;
    m_packedTokenCarrier = null;

    m_packedNewTokenCarrier = null;

    m_position = null;

    m_oldPosition = null;

    m_packedTokenDestroyer1 = null;
    m_packedTokenDestroyer2 = null;

    m_packedOldRegistration = null;

    m_constructType = TokenType.None;
  }

  public GameLogType getType()
  {
    if( m_type == null )
    {
      return GameLogType.None;
    }
    else
    {
      return m_type;
    }
  }

  /**
   * used to change timestamp to notify modification
   * we don't really need the original timestamp, but you can set it with 'setOldUpdate()'
   * @return last update minus one second
   */
  // @Transient
  protected Date getOldUpdate()
  {
    if( m_oldUpdate != null )
    {
      return m_oldUpdate;
    }
    if( getLastUpdate() == null )
    {
      return null;
    }
    return new Date( getLastUpdate().getTime() - 1000 );
  }

  protected void setOldUpdate(Date p_date)
  {
    m_oldUpdate = p_date;
  }


  /**
   * put p_token and all linked pontoon to graveyard.
   * Save all theses token to MiscTokenIds array.
   * @param p_game TODO
   * @param p_token must be a pontoon
   * @throws RpcFmpException
   */
  protected void chainRemovePontoon(EbGame p_game, EbToken p_token) throws RpcFmpException
  {
    assert p_game != null;
    if( getMiscTokenIds() == null )
    {
      setMiscTokenIds( new ArrayList<Long>() );
    }
    getMiscTokenIds().add( p_token.getId() );
    AnBoardPosition position = p_token.getPosition();
    p_game.moveToken( p_token, Location.Graveyard );
    p_token.incVersion();

    EbToken token = p_game.getToken( position );
    if( token != null )
    {
      // remove token on pontoon
      p_game.moveToken( token, Location.Graveyard );
      token.incVersion();
    }

    for( Sector sector : Sector.values() )
    {
      EbToken otherPontoon = p_game
          .getToken( position.getNeighbour( sector ), TokenType.Pontoon );
      if( otherPontoon != null )
      {
        chainRemovePontoon( p_game, otherPontoon );
      }
    }
  }



  /**
   * @return the action type enum
   */
  /*//@Transient
  public ActionType getType()
  {
    return ActionType.None;
  }*/

  /*public static AnAction getAction(ActionType p_type)
  {
    switch( p_type )
    {
    default:
    case None:
      return new AnAction();
    case Move:
      return new EbActionMove();
    }
  }*/

  /**
   * execute this action
   * @param p_game TODO
   * @throws RpcFmpException
   */
  public void exec(EbGame p_game) throws RpcFmpException
  {
    p_game.setVersion( p_game.getVersion() + 1 );
  }

  /**
   * un execute this action. ie undo what exec did.
   * @param p_game TODO
   * @throws RpcFmpException
   */
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    p_game.setVersion( p_game.getVersion() - 1 );
  }

  /**
   * a conveniance method which call check() and exec().
   * You don't have to overide it.
   * @param p_game TODO
   * @throws RpcFmpException
   */
  public final void checkedExec(EbGame p_game) throws RpcFmpException
  {
    check( p_game );
    exec( p_game );
  }

  /**
   * check this action is allowed.
   * you have to override this method.
   * @param p_game TODO
   * @throws RpcFmpException
   */
  public void check(EbGame p_game) throws RpcFmpException
  {
    if( p_game == null )
    {
      throw new RpcFmpException( "game [" + getIdGame() + "] was null" );
    }
    if( getIdGame() != p_game.getId() )
    {
      throw new RpcFmpException( "try to apply event from game [" + getIdGame() + "] on game ["
          + p_game.getId() + "]" );
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = getLastUpdate().toString();
    str += " " + getType();
    return str;
  }

  // Bean getter / setter
  // ====================


  /**
   * @return the idGame
   */
  public long getIdGame()
  {
    return m_idGame;
  }

  /**
   * @param p_idGame the idGame to set
   */
  public void setIdGame(long p_idGame)
  {
    m_idGame = p_idGame;
  }


  public void setGame(EbGame p_game)
  {
    m_idGame = p_game.getId();
  }


  /**
   * @return the factoryIndex
   */
  // @Transient
  protected int getFactoryIndex()
  {
    return m_factoryIndex;
  }

  /**
   * @param p_factoryIndex the factoryIndex to set
   */
  protected void setFactoryIndex(int p_factoryIndex)
  {
    m_factoryIndex = p_factoryIndex;
  }

  /**
   * @return the newEbBaseList
   */
  protected ArrayList<Long> getNewEbBaseList()
  {
    return m_newEbBaseList;
  }

  /**
   * @param p_newEbBaseList the newEbBaseList to set
   */
  protected void setNewEbBaseList(ArrayList<Long> p_newEbBaseList)
  {
    m_newEbBaseList = p_newEbBaseList;
  }


  /**
   * @return the pontoonId
   */
  protected ArrayList<Long> getMiscTokenIds()
  {
    return m_miscTokenIds;
  }

  /**
   * @param p_pontoonId the pontoonId to set
   */
  protected void setMiscTokenIds(ArrayList<Long> p_pontoonId)
  {
    m_miscTokenIds = p_pontoonId;
  }



  /**
   * @return the account
   */
  /*public EbAccount getAccount()
  {
    return m_account;
  }*/

  /**
   * @param p_account the account to set
   */
  /*public void setAccount(EbAccount p_account)
  {
    m_account = p_account;
    if( m_account != null )
    {
      m_accountId = m_account.getId();
    }
    else
    {
      m_accountId = 0L;
    }
  }*/

  /**
   * @return the account
   */
  public long getAccountId()
  {
    return m_accountId;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccountId(long p_id)
  {
    m_accountId = p_id;
  }

  /**
   * @return the auto
   */
  public boolean isAuto()
  {
    return m_auto;
  }

  /**
   * @param p_auto the auto to set
   */
  public void setAuto(boolean p_auto)
  {
    m_auto = p_auto;
  }

  /**
   * @return the remoteAddr
   */
  public String getRemoteAddr()
  {
    return m_remoteAddr;
  }

  /**
   * @param p_remoteAddr the remoteAddr to set
   */
  public void setRemoteAddr(String p_remoteAddr)
  {
    m_remoteAddr = p_remoteAddr;
  }



  /**
   * if message start with './', '/' or 'http://', message is a web page url
   * @return the message
   */
  public String getMessage()
  {
    return m_message;
  }

  /**
   * @param p_message the message to set
   */
  public void setMessage(String p_message)
  {
    m_message = p_message;
  }

  /**
   * @return the title
   */
  public String getTitle()
  {
    return m_title;
  }

  /**
   * @param p_title the title to set
   */
  public void setTitle(String p_title)
  {
    m_title = p_title;
  }


  /**
   * @return the oldTimeStepChange
   */
  public Date getOldTimeStepChange()
  {
    return m_oldTimeStepChange;
  }


  /**
   * @param p_oldTimeStepChange the oldTimeStepChange to set
   */
  public void setOldTimeStepChange(Date p_oldTimeStepChange)
  {
    m_oldTimeStepChange = p_oldTimeStepChange;
  }


  /**
   * @return the oldTide
   */
  public Tide getOldTide()
  {
    return m_oldTide;
  }


  /**
   * @param p_oldTide the oldTide to set
   */
  public void setOldTide(Tide p_oldTide)
  {
    m_oldTide = p_oldTide;
  }


  /**
   * @return the oldTideChange
   */
  public int getOldTideChange()
  {
    return m_oldTideChange;
  }


  /**
   * @param p_oldTideChange the oldTideChange to set
   */
  public void setOldTideChange(int p_oldTideChange)
  {
    m_oldTideChange = p_oldTideChange;
  }


  /**
   * @return the nextTide
   */
  public Tide getNextTide()
  {
    return m_nextTide;
  }


  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide(Tide p_nextTide)
  {
    m_nextTide = p_nextTide;
  }

  /**
   * @return the cost
   */
  public int getCost()
  {
    return m_cost;
  }

  /**
   * @param p_cost the cost to set
   */
  public void setCost(int p_cost)
  {
    m_cost = p_cost;
  }

  /**
   * @return the color
   */
  // @Transient
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
   * @return the newPosition
   */
  public AnBoardPosition getNewPosition()
  {
    return m_newPosition;
  }

  /**
   * @param p_newPosition the newPosition to set
   */
  public void setNewPosition(AnBoardPosition p_newPosition)
  {
    m_newPosition = p_newPosition;
  }

  /**
   * @return the packedToken
   */
  public EbBase getPackedToken()
  {
    return m_packedToken;
  }


  /**
   * @return the packedTokenCarrier
   */
  public EbBase getPackedTokenCarrier()
  {
    return m_packedTokenCarrier;
  }

  /**
   * @return the packedOldRegistration
   */
  public EbBase getPackedOldRegistration()
  {
    return m_packedOldRegistration;
  }

  public AnBoardPosition getOldPosition()
  {
    return m_oldPosition;
  }

  /**
   * @param p_oldPosition the oldPosition to set
   */
  public void setOldPosition(AnBoardPosition p_oldPosition)
  {
    m_oldPosition = p_oldPosition;
  }


  /**
   * @return the backInOrbit
   */
  public boolean isBackInOrbit()
  {
    return m_backInOrbit;
  }

  /**
   * @param p_backInOrbit the backInOrbit to set
   */
  public void setBackInOrbit(boolean p_backInOrbit)
  {
    m_backInOrbit = p_backInOrbit;
  }

  /**
   * @return the position
   */
  public AnBoardPosition getPosition()
  {
    return m_position;
  }

  /**
   * @param p_position the position to set
   */
  public void setPosition(AnBoardPosition p_position)
  {
    m_position = p_position;
  }

  /**
   * @return the oldColor
   */
  public int getOldColor()
  {
    return m_oldColor;
  }

  /**
   * @param p_oldColor the oldColor to set
   */
  public void setOldColor(int p_oldColor)
  {
    m_oldColor = p_oldColor;
  }


  /**
   * @return the p_constructType
   */
  public TokenType getConstructType()
  {
    return m_constructType;
  }

  /**
   * @param p_type the p_constructType to set
   */
  public void setConstructType(TokenType p_type)
  {
    m_constructType = p_type;
  }

  /**
   * @return the lastUpdate
   */
  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  /**
   * @param p_lastUpdate the lastUpdate to set
   */
  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }



  /**
   * @return the packedNewTokenCarrier
   */
  public EbBase getPackedNewTokenCarrier()
  {
    return m_packedNewTokenCarrier;
  }

  /**
   * @return the packedTokenDestroyer1
   */
  public EbBase getPackedTokenDestroyer1()
  {
    return m_packedTokenDestroyer1;
  }

  /**
   * @return the packedTokenDestroyer2
   */
  public EbBase getPackedTokenDestroyer2()
  {
    return m_packedTokenDestroyer2;
  }



  // cache to avoid researching again and again
  // and to implement getter
  // ===========================================
  transient private EbToken m_token = null;
  transient private EbToken m_tokenCarrier = null;
  transient private EbToken m_newTokenCarrier = null;
  transient private EbToken m_tokenDestroyer1 = null;
  transient private EbToken m_tokenDestroyer2 = null;
  transient private EbRegistration m_oldRegistration = null;

  public EbRegistration getOldRegistration(EbGame p_game)
  {
    if( m_oldRegistration == null )
    {
      m_oldRegistration = p_game.getRegistration( getPackedOldRegistration().getId() );
    }
    return m_oldRegistration;
  }

  /**
   * @param p_game TODO
   * @return the token
   */
  public EbToken getToken(EbGame p_game)
  {
    if( m_token == null && getPackedToken() != null )
    {
      m_token = p_game.getToken( getPackedToken().getId() );
    }
    return m_token;
  }

  /**
   * @param p_game TODO
   * @return the tokenCarrier
   */
  public EbToken getTokenCarrier(EbGame p_game)
  {
    if( m_tokenCarrier == null && getPackedTokenCarrier() != null )
    {
      m_tokenCarrier = p_game.getToken( getPackedTokenCarrier().getId() );
    }
    return m_tokenCarrier;
  }

  /**
   * @param p_game TODO
   * @return the newTokenCarrier
   */
  public EbToken getNewTokenCarrier(EbGame p_game)
  {
    if( m_newTokenCarrier == null && getPackedNewTokenCarrier() != null )
    {
      m_newTokenCarrier = p_game.getToken( getPackedNewTokenCarrier().getId() );
    }
    return m_newTokenCarrier;
  }

  /**
   * @param p_game TODO
   * @return the tokenDestroyer1
   */
  public EbToken getTokenDestroyer1(EbGame p_game)
  {
    if( m_tokenDestroyer1 == null && getPackedTokenDestroyer1() != null )
    {
      m_tokenDestroyer1 = p_game.getToken( getPackedTokenDestroyer1().getId() );
    }
    return m_tokenDestroyer1;
  }

  /**
   * @param p_game TODO
   * @return the tokenDestroyer2
   */
  public EbToken getTokenDestroyer2(EbGame p_game)
  {
    if( m_tokenDestroyer2 == null && getPackedTokenDestroyer2() != null )
    {
      m_tokenDestroyer2 = p_game.getToken( getPackedTokenDestroyer2().getId() );
    }
    return m_tokenDestroyer2;
  }

  /**
   * @param p_game TODO
    * @return the tokenTarget
   */
  public EbToken getTokenTarget(EbGame p_game)
  {
    return getToken( p_game );
  }

  public void setOldRegistration(EbRegistration p_oldRegistration)
  {
    m_packedOldRegistration = p_oldRegistration.createEbBase();
    m_oldRegistration = p_oldRegistration;
  }

  /**
   * @param p_game TODO
    * @return the tokenTarget
   */
  public void setTokenTarget(EbToken p_token)
  {
    setToken( p_token );
  }

  /**
   * @param p_token the token to set
   */
  public void setToken(EbToken p_token)
  {
    m_packedToken = p_token.createEbBase();
    m_token = p_token;
  }

  /**
   * @param p_tokenCarrier the tokenCarrier to set
   */
  public void setTokenCarrier(EbToken p_tokenCarrier)
  {
    m_packedTokenCarrier = p_tokenCarrier.createEbBase();
    m_tokenCarrier = p_tokenCarrier;
  }

  /**
   * @param p_tokenDestroyer1 the tokenDestroyer1 to set
   */
  public void setTokenDestroyer1(EbToken p_tokenDestroyer1)
  {
    m_packedTokenDestroyer1 = p_tokenDestroyer1.createEbBase();
    m_tokenDestroyer1 = p_tokenDestroyer1;
  }

  /**
   * @param p_tokenDestroyer2 the tokenDestroyer2 to set
   */
  public void setTokenDestroyer2(EbToken p_tokenDestroyer2)
  {
    m_packedTokenDestroyer2 = p_tokenDestroyer2.createEbBase();
    m_tokenDestroyer2 = p_tokenDestroyer2;
  }

  /**
   * @param p_tokenDestroyer2 the tokenDestroyer2 to set
   */
  public void setNewTokenCarrier(EbToken p_newTokenCarrier)
  {
    m_packedNewTokenCarrier = p_newTokenCarrier.createEbBase();
    m_newTokenCarrier = p_newTokenCarrier;
  }

}
