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
package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import com.fullmetalgalaxy.model.BoardFireCover;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameEventStack;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;

/**
 * @author Kroc
 * 
 * this is an old game model used to deserialize old blob or files
 */
public class EbGame extends AnPojoBase implements GameEventStack
{
  static final long serialVersionUID = 11;

  private long m_id = 0;
  private String m_name = "";
  private String m_description = "";
  private int m_maxNumberOfPlayer = 0;
  private boolean m_history = false;
  private Tide m_currentTide = Tide.Medium;
  private Tide m_nextTide = Tide.Medium;
  private Tide m_nextTide2 = Tide.Medium;
  private int m_lastTideChange = 0;
  private int m_currentTimeStep = 0;
  private Date m_lastTimeStepChange = new Date( System.currentTimeMillis() );
  private ArrayList<Integer> m_takeOffTurns = null;
  private GameType m_gameType = GameType.MultiPlayer;
  private PlanetType m_planetType = PlanetType.Desert;
  /** minimap stored here to save some ImageService's CPU time */
  private String m_minimapBlobKey = null;
  private String m_minimapUri = null;
  private String m_mapUri = null;

  /** registration ID */
  private long m_currentPlayerId = 0L;

  // this id is kept only for backward compatibility with older game
  private Long m_accountCreatorId = 0L;
  private EbPublicAccount m_accountCreator = null;

  /**
  * Land description. It's a two dimension array of landWitdh * landHeight
  */
  private byte[] m_lands = new byte[0];
  private int m_landWidth = 0;
  private int m_landHeight = 0;
  private Date m_creationDate = new Date( System.currentTimeMillis() );
  private boolean m_started = false;


  // configuration
  private ConfigGameTime m_configGameTime = ConfigGameTime.Standard;
  private ConfigGameVariant m_configGameVariant = ConfigGameVariant.Standard;
  private EbConfigGameTime m_ebConfigGameTime = null;
  private EbConfigGameVariant m_ebConfigGameVariant = null;

  // theses data come from other table
  // --------------------------------
  private Set<com.fullmetalgalaxy.model.persist.EbToken> m_setToken = new HashSet<com.fullmetalgalaxy.model.persist.EbToken>();

  private Set<com.fullmetalgalaxy.model.persist.EbRegistration> m_setRegistration = new HashSet<com.fullmetalgalaxy.model.persist.EbRegistration>();

  private List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> m_setGameLog = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();

  private List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> m_triggers = new ArrayList<com.fullmetalgalaxy.model.persist.triggers.EbTrigger>();

  private long m_nextLocalId = 0L;

  transient private TokenIndexSet m_tokenIndexSet = null;
  transient private GameEventStack m_eventStack = this;
  transient private BoardFireCover m_fireCover = null;

  public EbGame()
  {
    super();
    init();
  }


  private void init()
  {
    m_id = 0;
    m_maxNumberOfPlayer = 0;
    m_history = false;
    m_currentTide = Tide.Medium;
    m_nextTide = Tide.Medium;
    m_nextTide2 = Tide.Medium;
    m_lastTideChange = 0;
    m_currentTimeStep = 0;
    m_currentPlayerId = 0L;
    m_lands = new byte[0];
    m_landWidth = 0;
    m_landHeight = 0;
    m_creationDate = new Date( System.currentTimeMillis() );
    m_lastTimeStepChange = new Date( System.currentTimeMillis() );
    m_description = "";
    m_name = "";
    setConfigGameTime( ConfigGameTime.Standard );
    setConfigGameVariant( ConfigGameVariant.Standard );
    m_takeOffTurns = null;
    m_gameType = GameType.MultiPlayer;
    m_planetType = PlanetType.Desert;
    m_setToken = new HashSet<com.fullmetalgalaxy.model.persist.EbToken>();
    m_setRegistration = new HashSet<com.fullmetalgalaxy.model.persist.EbRegistration>();
    m_setGameLog = new ArrayList<com.fullmetalgalaxy.model.persist.gamelog.AnEvent>();
    m_triggers = new ArrayList<com.fullmetalgalaxy.model.persist.triggers.EbTrigger>();
    m_nextLocalId = 0L;
  }

  // @Override
  public void reinit()
  {
    // super.reinit();
    this.init();
  }


  /**
   * used for datastore migration
   * @return
   */
  private EbGamePreview createEbGamePreview()
  {
    EbGamePreview preview = new EbGamePreview();
    // preview.setId( getId() );
    preview.setVersion( 1 );
    preview.setName( getName() );
    preview.setDescription( getDescription() );
    preview.setMaxNumberOfPlayer( getMaxNumberOfPlayer() );
    preview.setHistory( isHistory() );
    preview.setCurrentTimeStep( getCurrentTimeStep() );
    preview.setGameType( getGameType() );
    preview.setPlanetType( getPlanetType() );
    preview.setMinimapBlobKey( getMinimapBlobKey() );
    preview.setMinimapUri( getMinimapUri() );
    preview.setAccountCreator( getAccountCreator() );
    if( preview.getAccountCreator() == null )
    {
      preview.setAccountCreator( new EbPublicAccount() );
      preview.getAccountCreator().setId( getAccountCreatorId() );
    }
    preview.setLandWidth( getLandWidth() );
    preview.setLandHeight( getLandHeight() );
    preview.setCreationDate( getCreationDate() );
    preview.setStarted( isStarted() );
    preview.setConfigGameTime( getConfigGameTime() );
    preview.setConfigGameVariant( getConfigGameVariant() );
    preview.setSetRegistration( getSetRegistration() );

    preview.setCurrentPlayerRegistration( getCurrentPlayerRegistration() );
    preview.isOpen();

    for( EbRegistration registration : preview.getSetRegistration() )
    {
      if( registration.getAccount() == null )
      {
        registration.setAccount( new EbPublicAccount() );
      }
      if( registration.getAccount().getId() == 0 )
      {
        registration.getAccount().setId( registration.m_accountId );
      }
      if( registration.getAccount().getPseudo() == null
          || registration.getAccount().getPseudo().isEmpty() )
      {
        registration.getAccount().setPseudo( registration.m_accountPseudo );
      }
        // registration.m_accountPseudo = null;
    }

    return preview;
  }

  /**
   * used for datastore migration
   * @return
   */
  private EbGameData createEbGameData()
  {
    EbGameData data = new EbGameData();
    data.setCurrentTide( getCurrentTide() );
    data.setNextTide( getNextTide() );
    data.setNextTide2( getNextTide2() );
    data.setLastTideChange( getLastTideChange() );
    data.setLastTimeStepChange( getLastTimeStepChange() );
    data.setTakeOffTurns( getAllowedTakeOffTurns() );
    data.setMapUri( getMapUri() );
    data.setLands( getLands() );
    data.setSetToken( getSetToken() );
    data.setLogs( getLogs() );
    data.setTriggers( getTriggers() );
    data.m_nextLocalId = m_nextLocalId;

    return data;
  }

  /**
   *   
   */
  public Game createGame()
  {
    Game game = new Game( createEbGamePreview(), createEbGameData() );
    for( EbToken token : game.getSetToken() )
    {
      token.convertTokenId2Token( game );
    }
    return game;
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbBase#setTrancient()
   */
  // @Override
  public void setTrancient()
  {
    // super.setTrancient();
    m_id = 0;
    for( EbTrigger trigger : getTriggers() )
    {
      trigger.setTrancient();
    }
    setTriggers( new ArrayList<EbTrigger>( getTriggers() ) );
    setSetToken( new HashSet<EbToken>( getSetToken() ) );
    for( EbRegistration registration : getSetRegistration() )
    {
      registration.setTrancient();
    }
    setSetRegistration( new HashSet<EbRegistration>( getSetRegistration() ) );
    for( AnEvent event : getLogs() )
    {
      event.setTrancient();
    }
    setLogs( new ArrayList<AnEvent>( getLogs() ) );
  }



  public int getLandPixWidth(EnuZoom p_zoom)
  {
    return getLandWidth() * ((FmpConstant.getHexWidth( p_zoom ) * 3) / 4)
        + FmpConstant.getHexWidth( p_zoom ) / 4;
  }

  public int getLandPixHeight(EnuZoom p_zoom)
  {
    return(getLandHeight() * FmpConstant.getHexHeight( p_zoom ) + FmpConstant.getHexHeight( p_zoom ) / 2);
  }

  /**
   * 
   * @return true if the game is finished
   */
  public boolean isFinished()
  {
    return(getCurrentTimeStep() > getEbConfigGameTime().getTotalTimeStep());
  }

  /**
   * @return the currentPlayerRegistration
   * @WgtHidden
   */
  public EbRegistration getCurrentPlayerRegistration()
  {
    return getRegistration( m_currentPlayerId );
  }

  /**
   * @param p_currentPlayerRegistration the currentPlayerRegistration to set
   */
  public void setCurrentPlayerRegistration(EbRegistration p_currentPlayerRegistration)
  {
    if( p_currentPlayerRegistration == null )
    {
      m_currentPlayerId = 0L;
    }
    else
    {
      m_currentPlayerId = p_currentPlayerRegistration.getId();
    }
  }


  @Override
  public AnEvent getLastGameLog()
  {
    if( getLogs().size() > 0 )
    {
      return getLogs().get( getLogs().size() - 1 );
    }
    return null;
  }


  public AnEvent getLastLog()
  {
    if( m_eventStack == null )
    {
      return getLastGameLog();
    }
    AnEvent event = m_eventStack.getLastGameLog();
    if( event == null )
    {
      return getLastGameLog();
    }
    return event;
  }


  public GameEventStack getGameEventStack()
  {
    return m_eventStack;
  }

  public void setGameEventStack(GameEventStack p_stack)
  {
    m_eventStack = p_stack;
  }


  public int getNextTideChangeTimeStep()
  {
    return getLastTideChange() + getEbConfigGameTime().getTideChangeFrequency();
  }

  public Date estimateTimeStepDate(int p_step)
  {
    long timeStepDurationInMili = getEbConfigGameTime().getTimeStepDurationInMili();
    if( !isAsynchron() )
    {
      timeStepDurationInMili *= getSetRegistration().size();
    }
    return new Date( getLastTimeStepChange().getTime() + (p_step - getCurrentTimeStep())
        * timeStepDurationInMili );
  }

  public Date estimateNextTimeStep()
  {
    return estimateTimeStepDate( getCurrentTimeStep() + 1 );
  }

  public Date estimateNextTideChange()
  {
    return estimateTimeStepDate( getNextTideChangeTimeStep() );
  }

  /**
   * if getTakeOffTurns() is defined, return it. 
   * otherwise return getEbConfigTimeVariant().getTakeOffTurns()
   * @return
   */
  public ArrayList<Integer> getAllowedTakeOffTurns()
  {
    if( getTakeOffTurns() != null )
    {
      return getTakeOffTurns();
    }
    return getEbConfigGameTime().getTakeOffTurns();
  }


  /**
   * If the game is paused, return a dummy value
   * @return the endingDate
   */
  public Date estimateEndingDate()
  {
    Date date = null;
    if( !isStarted() )
    {
      date = new Date( Long.MAX_VALUE );
    }
    else
    {
      date = estimateTimeStepDate( getEbConfigGameTime().getTotalTimeStep() );
    }
    return date;
  }


  /**
   * @return true if the logged player have at least one Freighter landed.
   */
  public boolean isLanded(EnuColor p_color)
  {
    for( Iterator<EbToken> it = getSetToken().iterator(); it.hasNext(); )
    {
      EbToken token = (EbToken)it.next();
      if( (p_color.isColored( token.getColor() )) && (token.getType() == TokenType.Freighter)
          && (token.getLocation() == Location.Board) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param p_token
   * @return all color of this token owner. no color if p_token have no color
   */
  public EnuColor getTokenOwnerColor(EbToken p_token)
  {
    // first determine the token owner color
    EnuColor tokenOwnerColor = p_token.getEnuColor();
    if( tokenOwnerColor.getValue() != EnuColor.None )
    {
      for( Iterator<EbRegistration> it = getSetRegistration().iterator(); it.hasNext(); )
      {
        EbRegistration registration = (EbRegistration)it.next();
        if( registration.getEnuColor().isColored( p_token.getColor() ) )
        {
          tokenOwnerColor.addColor( registration.getColor() );
          break;
        }
      }
    }
    return tokenOwnerColor;
  }

  /**
   * @param p_token
   *  true if this token is in a forbidden position. ie: if p_token is a tank near another on mountain.
   * @return the token with witch this token is cheating
   */
  public EbToken getTankCheating(EbToken p_token)
  {
    if( p_token.getType() != TokenType.Tank || p_token.getLocation() != Location.Board )
    {
      return null;
    }
    AnBoardPosition tokenPosition = p_token.getPosition();
    if( getLand( tokenPosition ) != LandType.Montain )
    {
      return null;
    }
    Sector sectorValues[] = Sector.values();
    for( int i = 0; i < sectorValues.length; i++ )
    {
      AnBoardPosition neighbourPosition = tokenPosition.getNeighbour( sectorValues[i] );
      EnuColor tokenOwnerColor = getTokenOwnerColor( p_token );
      if( getLand( neighbourPosition ) == LandType.Montain )
      {
        EbToken token = getToken( neighbourPosition );
        if( (token != null) && (token.getType() == TokenType.Tank)
            && (tokenOwnerColor.isColored( token.getColor() )) )
        {
          // this tank is cheating
          return token;
        }
      }
    }
    return null;
  }

  /**
   * similar to 'getTankCheating' but simply return true if this token is in a forbidden position
   * AND is the one which should be tag as cheater. 
   * => only of the tow tank: the lower ID
   * @param p_token
   * @return
   */
  public boolean isTankCheating(EbToken p_token)
  {
    EbToken nearTank = getTankCheating(p_token);
    return (nearTank!=null) && (nearTank.getId() > p_token.getId());
  }
  


  /**
   * @param p_tokenFreighter should be one Freighter
   * @return all winning point contained by p_token
   */
  public int getWinningPoint(EbToken p_tokenFreighter)
  {
    int winningPoint = 0;
    for( Iterator<EbToken> it = getSetToken().iterator(); it.hasNext(); )
    {
      EbToken token = (EbToken)it.next();
      if( (token.getLocation() == Location.Token) && (token.getCarrierToken() == p_tokenFreighter) )
      {
        if( token.getType() == TokenType.Ore )
        {
          winningPoint += 2;
        }
        else
        {
          winningPoint += 1;
        }
      }
    }
    return winningPoint;
  }



  /**
   * @return null if p_idRegistration doesn't exist
   */
  public EbRegistration getRegistration(long p_idRegistration)
  {
    for( Iterator<EbRegistration> it = getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.getId() == p_idRegistration )
      {
        return registration;
      }
    }
    return null;
  }

  /**
   * @return null if p_index doesn't exist
   */
  public EbRegistration getRegistrationByOrderIndex(int p_index)
  {
    for( Iterator<EbRegistration> it = getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.getOrderIndex() == p_index )
      {
        return registration;
      }
    }
    return null;
  }

  /**
   * @return the registration which control p_color. (or null if it doesn't exist)
   */
  public EbRegistration getRegistrationByColor(int p_color)
  {
    for( Iterator<EbRegistration> it = getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.getEnuColor().isColored( p_color ) )
      {
        return registration;
      }
    }
    return null;
  }


  public List<EbRegistration> getRegistrationByPlayerOrder()
  {
    List<EbRegistration> sortedRegistration = new ArrayList<EbRegistration>();
    // sort registration according to their order index.
    for( EbRegistration registration : getSetRegistration() )
    {
      int index = 0;
      while( index < sortedRegistration.size() )
      {
        if( registration.getOrderIndex() < sortedRegistration.get( index ).getOrderIndex() )
        {
          break;
        }
        index++;
      }
      sortedRegistration.add( index, registration );
    }

    return sortedRegistration;
  }



  public EbRegistration getNextPlayerRegistration()
  {
    return getNextPlayerRegistration( getCurrentPlayerRegistration().getOrderIndex() );
  }

  /**
   * return next registration which control at least one freighter on board.
   * If no registration control any freighter on board, return the current players registration.
   * @param p_currentIndex
   * @return
   */
  public EbRegistration getNextPlayerRegistration(int p_currentIndex)
  {
    EbRegistration registration = null;
    int index = p_currentIndex;
    do
    {
      index++;
      registration = getRegistrationByOrderIndex( index );
      if( registration == null )
      {
        // next turn !
        index = 0;
        registration = getRegistrationByOrderIndex( index );
      }
      assert registration != null;
    } while( !haveBoardFreighter( registration ) && (index != p_currentIndex) );
    return registration;
  }

  /**
   * count the number of freighter remaining on board this player control
   * @param p_registration
   * @return true if player have at least one freighter on board or in orbit
   */
  protected boolean haveBoardFreighter(EbRegistration p_registration)
  {
    if( p_registration.getColor() == EnuColor.None )
    {
      return false;
    }
    if( !getAllowedTakeOffTurns().isEmpty()
        && getCurrentTimeStep() < getAllowedTakeOffTurns().get( 0 ) )
    {
      return true;
    }
    for( EbToken token : getSetToken() )
    {
      if( (token.getType() == TokenType.Freighter)
          && (p_registration.getEnuColor().isColored( token.getColor() ))
          && ((token.getLocation() == Location.Board) || (token.getLocation() == Location.Orbit)) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @return time step duration multiply by the players count.
   */

  public long getFullTurnDurationInMili()
  {
    return getEbConfigGameTime().getTimeStepDurationInMili() * getSetRegistration().size();
  }



  /**
   * offset height in pixel to display token image in tactic zoom.
   * it represent the land height.
   * take in account tide.
   * @param p_x
   * @param p_y
   * @return
   */
  public int getLandPixOffset(AnPair p_position)
  {
    LandType land = getLand( p_position );
    switch( land )
    {
    default:
      return getLandPixOffset( land );
    case Sea:
      if( getCurrentTide() == Tide.Low )
      {
        return getLandPixOffset( LandType.Sea );
      }
    case Reef:
      if( getCurrentTide() != Tide.Hight )
      {
        return getLandPixOffset( LandType.Reef );
      }
    case Marsh:
      return getLandPixOffset( LandType.Marsh );
    }
  }

  /**
   * offset height in pixel to display token image in tactic zoom.
   * it represent the land height.
   * do not take in account tide
   */
  public static int getLandPixOffset(LandType p_landValue)
  {
    switch( p_landValue )
    {
    case Montain:
      return -7;
    case Sea:
      return 6;
    case Reef:
      return 5;
    case Marsh:
      return 2;
    default:
      return 0;
    }
  }

  // ===================================================================
  // come from token list

  /**
   * it keep the latest lastUpdate of the token list.
   */
  @Transient
  private Date m_lastTokenUpdate = new Date( 0 );


  public Date getLastTokenUpdate()
  {
    if( m_lastTokenUpdate == null )
    {
      m_lastTokenUpdate = new Date( System.currentTimeMillis() );
    }
    return m_lastTokenUpdate;
  }

  public void updateLastTokenUpdate(Date p_lastUpdate)
  {
    if( p_lastUpdate == null )
    {
      m_lastTokenUpdate = new Date( System.currentTimeMillis() );
    }
    else if( p_lastUpdate.after( m_lastTokenUpdate ) )
    {
      m_lastTokenUpdate = p_lastUpdate;
    }
  }

  /**
   * 
   * @param p_registration
   * @return first freighter owned by p_registration. null if not found.
   */
  public EbToken getFreighter( EbRegistration p_registration)
  {
    if( p_registration == null )
    {
      return null;
    }
    EnuColor color = p_registration.getEnuColor();
    for( EbToken token : getSetToken() )
    {
      if( token.getType() == TokenType.Freighter && color.isColored( token.getColor() ) )
      {
        return token;
      }
    }
    return null;
  }

  public List<EbToken> getAllFreighter(EbRegistration p_registration)
  {
    List<EbToken> list = new ArrayList<EbToken>();
    if( p_registration == null )
    {
      return list;
    }
    EnuColor color = p_registration.getEnuColor();
    for( EbToken token : getSetToken() )
    {
      if( token.getType() == TokenType.Freighter && color.isColored( token.getColor() ) )
      {
        list.add( token );
      }
    }
    return list;
  }

  /**
   * search over m_token for a token
   * @param p_id
   * @return null if no token found
   */
  public EbToken getToken(long p_id)
  {
    return getTokenIndexSet().getToken( p_id );
  }

  /**
   * search over m_token and m_tokenLocation for the most relevant token at a given position
   * @param p_position
   * @return the first colored (or by default the first colorless) token encountered at given position.
   */
  public EbToken getToken(AnBoardPosition p_position)
  {
    Set<EbToken> list = getAllToken( p_position );
    EbToken token = null;
    for( Iterator<EbToken> it = list.iterator(); it.hasNext(); )
    {
      EbToken nextToken = (EbToken)it.next();
      if( (token == null) || (token.getZIndex() < nextToken.getZIndex()) )
      {
        token = nextToken;
      }
    }
    return token;
  }

  /**
   * This method extract one specific token in the token list.
   * @param p_position
   * @param p_tokenType
   * @return null if not found
   */
  public EbToken getToken(AnBoardPosition p_position, TokenType p_tokenType)
  {
    EbToken token = null;
    for( Iterator<EbToken> it = getAllToken( p_position ).iterator(); it.hasNext(); )
    {
      EbToken nextToken = (EbToken)it.next();
      if( nextToken.getType() == p_tokenType )
      {
        token = nextToken;
      }
    }
    return token;
  }

  /**
   * note: this method assume that every token are located only once to a single position.
   * (ie the couple [tokenId;position] is unique in m_tokenLocation)
   * @param p_position
   * @return a list of all token located at p_position
   */
  public Set<EbToken> getAllToken(AnBoardPosition p_position)
  {
    Set<EbToken> allTokens = getTokenIndexSet().getAllToken( p_position );
    if( allTokens != null )
    {
      return new HashSet<EbToken>(allTokens);
    }
    return new HashSet<EbToken>();
  }



  /**
   * determine if this token can load the given token
   * @param p_carrier the token we want load
   * @param p_token the token we want load
   * @return
   */
  public boolean canTokenLoad(EbToken p_carrier, EbToken p_token)
  {
    assert p_carrier != null;
    assert p_token != null;
    if( !p_carrier.canLoad( p_token.getType() ) )
    {
      return false;
    }
    // ArrayList loadedTokenList = getAllTokenInside( p_carrier );
    int freeLoadingSpace = p_carrier.getLoadingCapability();
    if( p_carrier.containToken() )
    {
      for( EbToken token : p_carrier.getContains() )
      {
        freeLoadingSpace -= token.getLoadingSize();
      }
    }
    return freeLoadingSpace >= p_token.getFullLoadingSize();
  }




  /**
   * determine if this token is active depending of current tide. 
   * ie: not under water for lands unit or land for see units.
   * @param p_token
   * @return
   */
  public boolean isTokenTideActive(EbToken p_token)
  {
    if( (p_token == null) )
    {
      return false;
    }
    // if p_token is contained by another token, p_token is active only if
    // this
    // other token is active
    if( (p_token.getLocation() == Location.Token)
        || (p_token.getLocation() == Location.ToBeConstructed) )
    {
      return isTokenTideActive( (EbToken)p_token.getCarrierToken() );
    }
    if( (p_token.getLocation() == Location.Graveyard) )
    {
      return false;
    }
    if( p_token.getLocation() == Location.Orbit )
    {
      return true;
    }
    // ok so token if on board.
    // determine, according to current tide, if the first position is sea,
    // plain or montain
    LandType landValue = getLand( p_token.getPosition() ).getLandValue( getCurrentTide() );
    if( landValue == LandType.None )
    {
      return false;
    }
    switch( p_token.getType() )
    {
    case Barge:
      LandType extraLandValue = getLand( (AnBoardPosition)p_token.getExtraPositions().get( 0 ) )
          .getLandValue( getCurrentTide() );
      if( extraLandValue != LandType.Sea )
      {
        return false;
      }
    case Speedboat:
      if( landValue != LandType.Sea )
      {
        return false;
      }
      return true;
    case Heap:
    case Tank:
    case Crab:
    case WeatherHen:
    case Ore:
      if( getToken( p_token.getPosition(), TokenType.Pontoon ) != null )
      {
        return true;
      }
      if( landValue == LandType.Sea )
      {
        return false;
      }
      return true;
    case Turret:
    case Freighter:
    case Pontoon:
    default:
      return true;
    }
  }


  /**
   * determine weather the given token can produce or not a fire cover.
   * If not, his fire cover will increment the disabled fire cover to show it to player
   * @param p_token
   * @return
   */
  public boolean isTokenFireCoverDisabled(EbToken p_token)
  {
    if( p_token == null || !p_token.isDestroyer() )
    {
      return false;
    }
    // if( !isTokenFireActive( p_token ) )
    if( p_token.isFireDisabled() )
    {
      return true;
    }
    if( !isTokenTideActive( p_token ) )
    {
      return true;
    }
    if( isTankCheating( p_token ) )
    {
      return true;
    }
    return false;
  }


  /**
   * determine if this token can fire onto a given position
   * Warning: it doesn't check tide or fire disable flag !
   * @param p_token
   * @param p_position
   * @return
   */
  public boolean canTokenFireOn(EbToken p_token, AnBoardPosition p_position)
  {
    if( (p_token.getLocation() != Location.Board) || (!p_token.isDestroyer()) )
    {
      return false;
    }
    return p_token.getPosition().getHexDistance( p_position ) <= (getTokenFireLength( p_token ));
  }

  /**
   *  determine if this token can fire onto a given token
   * Warning: it doesn't check tide or fire disable flag !
   * @param p_token
   * @param p_tokenTarget
   * @return
   */
  public boolean canTokenFireOn(EbToken p_token, EbToken p_tokenTarget)
  {
    if( canTokenFireOn( p_token, p_tokenTarget.getPosition() ) )
    {
      return true;
    }
    for( AnBoardPosition position : p_tokenTarget.getExtraPositions() )
    {
      if( canTokenFireOn( p_token, position ) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @return the fire length of this token.
   */
  public int getTokenFireLength(EbToken p_token)
  {
    if( (p_token == null) || p_token.getLocation() != Location.Board )
    {
      return 0;
    }
    switch( p_token.getType() )
    {
    case Turret:
    case Speedboat:
      return 2;
    case Tank:
      if( getLand( p_token.getPosition() ) == LandType.Montain )
      {
        return 3;
      }
      return 2;
    case Heap:
      return 3;
    case Freighter:
    case Barge:
    case Crab:
    case WeatherHen:
    case Pontoon:
    case Ore:
    default:
      return 0;
    }
  }

  public boolean isPontoonLinkToGround(EbToken p_token)
  {
    if( p_token.getType() != TokenType.Pontoon )
    {
      return false;
    }
    Set<EbToken> checkedPontoon = new HashSet<EbToken>();
    return isPontoonLinkToGround( p_token, checkedPontoon );
  }

  public boolean isPontoonLinkToGround(AnBoardPosition p_position)
  {
    LandType land = getLand( p_position ).getLandValue( getCurrentTide() );
    if( (land == LandType.Plain) || (land == LandType.Montain) )
    {
      return true;
    }
    for( Sector sector : Sector.values() )
    {
      land = getLand( p_position.getNeighbour( sector ) ).getLandValue( getCurrentTide() );
      if( (land == LandType.Plain) || (land == LandType.Montain) )
      {
        return true;
      }
    }
    EbToken otherPontoon = getToken( p_position, TokenType.Pontoon );
    Set<EbToken> checkedPontoon = new HashSet<EbToken>();
    if( otherPontoon != null )
    {
      checkedPontoon.add( otherPontoon );
    }
    for( Sector sector : Sector.values() )
    {
      otherPontoon = getToken( p_position.getNeighbour( sector ), TokenType.Pontoon );
      if( otherPontoon != null )
      {
        checkedPontoon.add( otherPontoon );
        if( isPontoonLinkToGround( otherPontoon, checkedPontoon ) )
        {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isPontoonLinkToGround(EbToken p_token, Set<EbToken> p_checkedPontoon)
  {
    LandType land = getLand( p_token.getPosition() ).getLandValue( getCurrentTide() );
    if( (land == LandType.Plain) || (land == LandType.Montain) )
    {
      return true;
    }
    for( Sector sector : Sector.values() )
    {
      land = getLand( p_token.getPosition().getNeighbour( sector ) )
          .getLandValue( getCurrentTide() );
      if( (land == LandType.Plain) || (land == LandType.Montain) )
      {
        return true;
      }
    }
    EbToken otherPontoon = null;
    for( Sector sector : Sector.values() )
    {
      otherPontoon = getToken( p_token.getPosition().getNeighbour( sector ), TokenType.Pontoon );
      if( (otherPontoon != null) && (!p_checkedPontoon.contains( otherPontoon )) )
      {
        p_checkedPontoon.add( otherPontoon );
        if( isPontoonLinkToGround( otherPontoon, p_checkedPontoon ) )
        {
          return true;
        }
      }
    }
    return false;
  }
  




  /**
   * @return the tokenIndexSet. never return null;
   */
  private TokenIndexSet getTokenIndexSet()
  {
    if( m_tokenIndexSet == null )
    {
      m_tokenIndexSet = new TokenIndexSet( getSetToken() );
    }
    return m_tokenIndexSet;
  }

  /**
   * @return a set of free (not controlled by any registration, account may be null)
   *  single color
   */
  public Set<EnuColor> getFreeColors4Registration()
  {
    Set<EnuColor> colors = new HashSet<EnuColor>();
    EnuColor takenColor = new EnuColor( EnuColor.None );
    for( EbRegistration registration : getSetRegistration() )
    {
      takenColor.addColor( registration.getColor() );
    }
    for( int iColor = 0; iColor < EnuColor.getTotalNumberOfColor(); iColor++ )
    {
      EnuColor color = EnuColor.getColorFromIndex( iColor );
      if( !takenColor.isColored( color ) )
      {
        colors.add( color );
      }
    }
    return colors;
  }

  /**
   * @return a set of free (controled by registration with null account)
   *  single color
   */
  public Set<EnuColor> getFreeRegistrationColors()
  {
    Set<EnuColor> colors = new HashSet<EnuColor>();
    for( EbRegistration registration : getSetRegistration() )
    {
      if( !registration.haveAccount() )
      {
        colors.add( registration.getEnuColor() );
      }
    }
    return colors;
  }

  /**
   * @return a set of free (not controlled by any registration with an associated account)
   *  single color
   */
  public Set<EnuColor> getFreePlayersColors()
  {
    Set<EnuColor> colors = new HashSet<EnuColor>();
    EnuColor takenColor = new EnuColor( EnuColor.None );
    for( EbRegistration registration : getSetRegistration() )
    {
      if( registration.haveAccount() )
      {
        takenColor.addColor( registration.getColor() );
      }
    }
    for( int iColor = 0; iColor < EnuColor.getTotalNumberOfColor(); iColor++ )
    {
      EnuColor color = EnuColor.getColorFromIndex( iColor );
      if( !takenColor.isColored( color ) )
      {
        colors.add( color );
      }
    }
    return colors;
  }


  /**
   * 
   * @param x coordinate
   * @param y coordinate
   * @return the land ID.
   */
  // @Deprecated
  public LandType getLand(int x, int y)
  {
    return getLand( new AnPair( x, y ) );
  }

  public LandType getLand(AnPair p_position)
  {
    LandType land = LandType.None;
    if( p_position.getX() < 0 || p_position.getX() >= getLandWidth() || p_position.getY() < 0
        || p_position.getY() >= getLandHeight()
        || p_position.getX() + p_position.getY() * getLandWidth() >= getLands().length * 2 )
    {
      // RpcUtil.logError( "request an hexgon outside of the map" );
      // land = EnuLand.None;
    }
    else
    {
      int position = p_position.getX() + p_position.getY() * getLandWidth();
      int offset = position % 2;
      position = position / 2;
      if( offset == 0 )
      {
        land = LandType.getFromOrdinal( getLands()[position] & 0x0F );
      }
      else
      {
        land = LandType.getFromOrdinal( (getLands()[position] & 0xF0) >> 4 );
      }
    }
    return land;
  }

  public void setLand(AnBoardPosition p_position, LandType p_landValue)
  {
    setLand( p_position.getX(), p_position.getY(), p_landValue );
  }

  public void setLand(int x, int y, LandType p_land)
  {
    setLand( x, y, (byte)p_land.ordinal() );
  }

  protected void setLand(int x, int y, byte id)
  {
    if( x < 0 || x > getLandWidth() || y < 0 || y > getLandHeight() )
    {
      RpcUtil.logError( "set an hexgon outside of the map" );
      return;
    }
    else
    {
      int position = x + y * getLandWidth();
      int offset = position % 2;
      position = position / 2;
      assert position < m_lands.length;
      int intId = (int)id & 0x0F;
      if( offset == 0 )
      {
        m_lands[position] = (byte)(((int)m_lands[position] & 0xF0) + intId);
      }
      else
      {
        m_lands[position] = (byte)(((int)m_lands[position] & 0x0F) + (intId << 4));
      }
    }
  }




  /**
   * @return the accountCreator
   */
  public long getAccountCreatorId()
  {
    if( getAccountCreator() == null )
    {
      return m_accountCreatorId;
    }
    else
    {
      return getAccountCreator().getId();
    }
  }


  /**
   * @return the account
   */
  public EbPublicAccount getAccountCreator()
  {
    return m_accountCreator;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccountCreator(EbPublicAccount p_account)
  {
    m_accountCreator = p_account;
  }

  /**
   * @return the creationDate
   * @WgtHidden
   */
  public Date getCreationDate()
  {
    return m_creationDate;
  }

  /**
   * @param p_creationDate the creationDate to set
   */
  public void setCreationDate(Date p_creationDate)
  {
    m_creationDate = p_creationDate;
  }

  /**
   * Don't use this method directly, it's for hibernate and h4gwt use only
   * @return the lands
   * @WgtHidden
   */
  public byte[] getLands()
  {
    return m_lands;
  }

  /**
   * Don't use this method directly, it's for hibernate and h4gwt use only
   * @param p_lands the lands to set
   */
  public void setLands(byte[] p_lands)
  {
    m_lands = p_lands;
  }

  /**
   * @return the landWitdh in hexagon
   */
  public int getLandWidth()
  {
    return m_landWidth;
  }

  /**
   * @param p_landWidth the landWitdh to set
   * Warning: this method do not change the lands blobs size. use setLandSize(int,int) instead.
   */
  public void setLandWidth(int p_landWidth)
  {
    m_landWidth = p_landWidth;
  }

  /**
   * @return the landHeight in hexagon
   */
  public int getLandHeight()
  {
    return m_landHeight;
  }

  /**
   * @param p_landHeight the landHeight to set
   * Warning: this method do not change the lands blobs size. use setLandSize(int,int) instead.
   */
  public void setLandHeight(int p_landHeight)
  {
    m_landHeight = p_landHeight;
  }


  /**
   * @return the setToken
   * @WgtHidden
   */
  public Set<com.fullmetalgalaxy.model.persist.EbToken> getSetToken()
  {
    return m_setToken;
  }

  /**
   * @param p_setToken the setToken to set
   */
  public void setSetToken(Set<com.fullmetalgalaxy.model.persist.EbToken> p_setToken)
  {
    m_setToken = p_setToken;
  }

  /**
   * @return the setRegistration
   * @WgtHidden
   */
  public Set<com.fullmetalgalaxy.model.persist.EbRegistration> getSetRegistration()
  {
    return m_setRegistration;
  }

  /**
   * @param p_setRegistration the setRegistration to set
   */
  public void setSetRegistration(
      Set<com.fullmetalgalaxy.model.persist.EbRegistration> p_setRegistration)
  {
    m_setRegistration = p_setRegistration;
  }


  /**
   * @return the name
   * @WgtRequired
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * @param p_name the name to set
   */
  public void setName(String p_name)
  {
    m_name = p_name;
  }

  /**
   * @return the maxNumberOfPlayer
   */
  public int getMaxNumberOfPlayer()
  {
    return m_maxNumberOfPlayer;
  }

  /**
   * @param p_maxNumberOfPlayer the maxNumberOfPlayer to set
   */
  public void setMaxNumberOfPlayer(int p_maxNumberOfPlayer)
  {
    m_maxNumberOfPlayer = p_maxNumberOfPlayer;
  }



  /**
   * @return the isAsynchron
   */
  public boolean isAsynchron()
  {
    return getEbConfigGameTime().isAsynchron();
  }


  /**
   * @return the history
   * @WgtHidden
   */
  public boolean isHistory()
  {
    return m_history;
  }

  /**
   * @param p_history the history to set
   */
  public void setHistory(boolean p_history)
  {
    m_history = p_history;
  }



  /**
   * @return the currentTide
   * @WgtHidden
   */
  public Tide getCurrentTide()
  {
    return m_currentTide;
  }

  /**
   * @param p_currentTide the currentTide to set
   */
  public void setCurrentTide(Tide p_currentTide)
  {
    m_currentTide = p_currentTide;
  }



  /**
   * @return the nextTide
   * @WgtHidden
   */
  public Tide getNextTide()
  {
    if( m_nextTide == null )
    {
      m_nextTide = Tide.getRandom();
    }
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
   * @return the nextTide
   * @WgtHidden
   */
  public Tide getNextTide2()
  {
    if( m_nextTide2 == null )
    {
      m_nextTide2 = Tide.getRandom();
    }
    return m_nextTide2;
  }

  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide2(Tide p_nextTide)
  {
    m_nextTide2 = p_nextTide;
  }


  /**
   * @return the currentTimeStep
   */
  public int getCurrentTimeStep()
  {
    return m_currentTimeStep;
  }

  /**
   * @param p_currentTimeStep the currentTimeStep to set
   */
  public void setCurrentTimeStep(int p_currentTimeStep)
  {
    m_currentTimeStep = p_currentTimeStep;
  }

  /**
   * @return the started
   */
  public boolean isStarted()
  {
    return m_started;
  }

  /**
   * @param p_started the started to set
   */
  public void setStarted(boolean p_started)
  {
    m_started = p_started;
  }

  /**
   * Don't forget to reset to null m_configGameTimeDefault if you change any value of this config
   * @return the configGameTime
   */
  public void setEbConfigGameTime(EbConfigGameTime p_config)
  {
    m_ebConfigGameTime = p_config;
  }

  /**
   * Don't forget to reset to null m_configGameTimeDefault if you change any value of this config
   * @return the configGameTime
   */
  public EbConfigGameTime getEbConfigGameTime()
  {
    // this patch is to handle old data game
    if(m_ebConfigGameTime == null && getConfigGameTime() != null)
    {
      setConfigGameTime( getConfigGameTime() );
    }
    return m_ebConfigGameTime;
  }


  /**
   * Don't forget to reset to null m_configGameVariantDefault if you change any value of this config
   * @return the configGameVariant
   */
  public void setEbConfigGameVariant(EbConfigGameVariant p_config)
  {
    m_ebConfigGameVariant = p_config;
  }

  /**
   * Don't forget to reset to null m_configGameVariantDefault if you change any value of this config
   * @return the configGameVariant
   */
  public EbConfigGameVariant getEbConfigGameVariant()
  {
    // this patch is to handle old data game
    if(m_ebConfigGameVariant == null && getConfigGameVariant() != null)
    {
      setConfigGameVariant( getConfigGameVariant() );
      assert m_ebConfigGameVariant != null;
      m_ebConfigGameVariant.multiplyConstructQty( getMaxNumberOfPlayer() );
    }
    return m_ebConfigGameVariant;
  }


  /**
   * @return the description
   */
  public String getDescription()
  {
    return m_description;
  }

  /**
   * @param p_description the description to set
   */
  public void setDescription(String p_description)
  {
    m_description = p_description;
  }

  /**
   * @return the lastTideChange
   */
  public int getLastTideChange()
  {
    return m_lastTideChange;
  }

  /**
   * @param p_lastTideChange the lastTideChange to set
   */
  public void setLastTideChange(int p_lastTideChange)
  {
    m_lastTideChange = p_lastTideChange;
  }

  /**
   * @return the lastTimeStepChange
   */
  public Date getLastTimeStepChange()
  {
    return m_lastTimeStepChange;
  }

  /**
   * @param p_lastTimeStepChange the lastTimeStepChange to set
   */
  public void setLastTimeStepChange(Date p_lastTimeStepChange)
  {
    m_lastTimeStepChange = p_lastTimeStepChange;
  }

  /**
   * @return the configGameTime
   */
  public ConfigGameTime getConfigGameTime()
  {
    return m_configGameTime;
  }

  /**
   * @param p_configGameTime the configGameTime to set
   */
  public void setConfigGameTime(ConfigGameTime p_configGameTime)
  {
    m_configGameTime = p_configGameTime;
    setEbConfigGameTime( new EbConfigGameTime(m_configGameTime.getEbConfigGameTime()) );
  }

  /**
   * @param p_configGameVariant the configGameVariant to set
   */
  public void setConfigGameVariant(ConfigGameVariant p_configGameVariant)
  {
    m_configGameVariant = p_configGameVariant;
    setEbConfigGameVariant( new EbConfigGameVariant(m_configGameVariant.getEbConfigGameVariant()) );
  }

  /**
   * @return the configGameVariant
   */
  public ConfigGameVariant getConfigGameVariant()
  {
    return m_configGameVariant;
  }


  /**
   * @return the setActionLog
   */
  public List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> getLogs()
  {
    return m_setGameLog;
  }

  /**
   * @param p_setActionLog the setActionLog to set
   */
  public void setLogs(List<com.fullmetalgalaxy.model.persist.gamelog.AnEvent> p_setActionLog)
  {
    m_setGameLog = p_setActionLog;
  }

  /**
   * use getAllowedTakeOffTurns() instead
   * @return the takeOffTurns
   */
  protected ArrayList<Integer> getTakeOffTurns()
  {
    return m_takeOffTurns;
  }

  /**
   * @param p_takeOffTurns the takeOffTurns to set
   */
  public void setTakeOffTurns(ArrayList<Integer> p_takeOffTurns)
  {
    m_takeOffTurns = p_takeOffTurns;
  }

  /**
   * @return the gameType
   */
  public GameType getGameType()
  {
    return m_gameType;
  }

  /**
   * @param p_gameType the gameType to set
   */
  public void setGameType(GameType p_gameType)
  {
    m_gameType = p_gameType;
  }

  /**
   * @return the triggers
   */
  public List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> getTriggers()
  {
    return m_triggers;
  }

  /**
   * @param p_triggers the triggers to set
   */
  public void setTriggers(List<com.fullmetalgalaxy.model.persist.triggers.EbTrigger> p_triggers)
  {
    m_triggers = p_triggers;
  }

  /**
   * @return the planetType
   */
  public PlanetType getPlanetType()
  {
    return m_planetType;
  }

  /**
   * @param p_planetType the planetType to set
   */
  public void setPlanetType(PlanetType p_planetType)
  {
    m_planetType = p_planetType;
  }

  /**
   * @return the minimapUri
   */
  public String getMinimapUri()
  {
    return m_minimapUri;
  }

  /**
   * @param p_minimapUri the minimapUri to set
   */
  public void setMinimapUri(String p_minimapUri)
  {
    m_minimapUri = p_minimapUri;
  }

  /**
   * @return the minimapBlobKey
   */
  public String getMinimapBlobKey()
  {
    return m_minimapBlobKey;
  }

  /**
   * @param p_minimapBlobKey the minimapBlobKey to set
   */
  public void setMinimapBlobKey(String p_minimapBlobKey)
  {
    m_minimapBlobKey = p_minimapBlobKey;
  }

  /**
   * @return the mapUri
   */
  public String getMapUri()
  {
    return m_mapUri;
  }

  /**
   * @param p_mapUri the mapUri to set
   */
  public void setMapUri(String p_mapUri)
  {
    m_mapUri = p_mapUri;
  }


}
