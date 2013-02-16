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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import com.fullmetalgalaxy.model.BoardFireCover;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameEventStack;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.Mobile;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.pathfinder.PathGraph;
import com.fullmetalgalaxy.model.pathfinder.PathMobile;
import com.fullmetalgalaxy.model.pathfinder.PathNode;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMove;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;

/**
 * @author Kroc
 * This class represent the board model on both client/server side.
 * ie: all data needed by the board application on client side to run correctly.
 */
public class Game extends GameData implements PathGraph, GameEventStack
{
  private static final long serialVersionUID = -717221858626185781L;

  transient private TokenIndexSet m_tokenIndexSet = null;
  transient private GameEventStack m_eventStack = this;
  transient private BoardFireCover m_fireCover = null;

  public Game()
  {
    super();
    init();
  }

  public Game(EbGamePreview p_preview, EbGameData p_data)
  {
    super(p_preview, p_data);
  }

  private void init()
  {
    setConfigGameTime( ConfigGameTime.Standard );
    setConfigGameVariant( ConfigGameVariant.Standard );
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }
  
  /**
   * 
   * @return true if game message start with recording tag
   */
  public boolean isRecordingScript()
  {
    return getMessage() != null
        && getMessage().startsWith( EventsPlayBuilder.GAME_MESSAGE_RECORDING_TAG );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbBase#setTrancient()
   */
  @Override
  public void setTrancient()
  {
    super.setTrancient();
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


  /**
   * if game is in parallel mode, search the registration that lock an hexagon.
   * @param p_position
   * @return null if no registration lock that hexagon
   */
  public EbRegistration getOtherRegistrationBoardLocked(EbRegistration p_myRegistration,
      AnBoardPosition p_position, long p_currentTime)
  {
    if( !isParallel() || p_position == null || p_position.getX() < 0 )
    {
      return null;
    }
    for( EbRegistration registration : getSetRegistration() )
    {
      if( p_myRegistration != registration && registration.getEndTurnDate() != null
          && registration.getLockedPosition() != null )
      {
        if( registration.getEndTurnDate().getTime() < p_currentTime )
        {
          registration.setEndTurnDate( null );
          registration.setLockedPosition( null );
        }
        else if( registration.getLockedPosition().getHexDistance( p_position ) <= FmpConstant.parallelLockRadius )
        {
          return registration;
        }
      }
    }
    return null;
  }

  public void addToken(EbToken p_token)
  {
    if( p_token.getId() == 0 )
    {
      p_token.setId( getNextLocalId() );
      p_token.setVersion( 1 );
    }
    if( !getSetToken().contains( p_token ) )
    {
      getSetToken().add( p_token );
      getTokenIndexSet().addToken( p_token );
      getBoardFireCover().incFireCover( p_token );
      if( p_token.containToken() )
      {
        for( EbToken token : p_token.getContains() )
        {
          addToken( token );
        }
      }
    }
    updateLastTokenUpdate( null );
  }

  public void addRegistration(EbRegistration p_registration)
  {
    if( p_registration.getId() == 0 )
    {
      p_registration.setId( getNextLocalId() );
    }
    if( !getSetRegistration().contains( p_registration ) )
    {
      getSetRegistration().add( p_registration );
    }
  }

  public boolean haveNewMessage(Date p_since)
  {
    if( p_since == null )
      return true;
    int index = getLogs().size() - 1;
    Date lastEventDate = null;
    while( index > 0 && (lastEventDate == null || lastEventDate.after( p_since )) )
    {
      if( getLogs().get( index ) instanceof EbEvtMessage )
      {
        return true;
      }
      lastEventDate = getLogs().get( index ).getLastUpdate();
      index--;
    }
    return false;
  }



  public void addEvent(AnEvent p_action)
  {
    p_action.setGame( this );
    if( p_action.getId() == 0 )
    {
      p_action.setId( getNextLocalId() );
    }
    else if( p_action.getId() != getNextLocalId() )
    {
      RpcUtil.logError( "EbGame::addEvent(): p_action.getId() != getNextLocalId()" );
    }
    if( !getLogs().contains( p_action ) )
    {
      getLogs().add( p_action );
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

  @Override
  public AnEvent getLastGameLog(int p_count)
  {
    if( getLogs().size() < p_count )
    {
      return null;
    }
    return getLogs().get( getLogs().size() - (1 + p_count) );
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

  public AnEvent getLastLog(int p_count)
  {
    if( m_eventStack == null )
    {
      return getLastGameLog( p_count );
    }
    AnEvent event = m_eventStack.getLastGameLog( p_count );
    if( event == null )
    {
      return getLastGameLog( p_count );
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
    if( !isParallel() )
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
   * If the game is paused, return a dummy value
   * @return the endingDate
   */
  public Date estimateEndingDate()
  {
    Date date = null;
    if( getStatus() != GameStatus.Running )
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
   * return the bitfields of all opponents fire cover at the given position.
   * @param p_token
   * @return
   */
  public EnuColor getOpponentFireCover(int p_myColorValue, AnBoardPosition p_position)
  {
    EnuColor fireCover = getBoardFireCover().getFireCover( p_position );
    fireCover.removeColor( p_myColorValue );
    return fireCover;
  }

  /**
   * @return true if the colored player have at least one weather hen to predict future tides.
   */
  public int countWorkingWeatherHen(EnuColor p_color)
  {
    int count = 0;
    for( Iterator<EbToken> it = getSetToken().iterator(); it.hasNext(); )
    {
      EbToken token = (EbToken)it.next();
      if( (p_color.isColored( token.getColor() ))
          && (token.getType() == TokenType.WeatherHen)
          && (token.getColor() != EnuColor.None)
          && (token.getLocation() == Location.Board)
          && (isTokenTideActive( token ))
          && (getOpponentFireCover( p_color.getValue(), token.getPosition() ).getValue() == EnuColor.None) )
      {
        count++;
      }
    }
    return count;
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
   * return the bitfields of all opponents fire cover on this token.<br/>
   * note: if p_token is color less or not on board, always return EnuColor.None
   * @param p_token
   * @return
   */
  public EnuColor getOpponentFireCover(EbToken p_token)
  {
    if( p_token.getLocation() != Location.Board || p_token.getColor() == EnuColor.None )
    {
      return new EnuColor( EnuColor.None );
    }
    // first determine the token owner color
    EnuColor tokenOwnerColor = getTokenOwnerColor( p_token );
    EnuColor fireCover = getOpponentFireCover( tokenOwnerColor.getValue(), p_token.getPosition() );
    if( p_token.getHexagonSize() == 2 )
    {
      fireCover.addColor( getOpponentFireCover( tokenOwnerColor.getValue(),
          (AnBoardPosition)p_token.getExtraPositions().get( 0 ) ) );
    }
    return fireCover;
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
   * Should be called only on server side.
   * Move a token and all token he contain into any other token in the game.
   * @param p_tokenToMove 
   * @param p_tokenCarrier must be included in TokenList
   * @throws RpcFmpException
   *          
   */
  public void moveToken(EbToken p_tokenToMove, EbToken p_tokenCarrier) // throws
                                                                       // RpcFmpException
  {
    getBoardFireCover().decFireCover( p_tokenToMove );
    m_tokenIndexSet.setPosition( p_tokenToMove, Location.Token );
    if( p_tokenToMove.getCarrierToken() != null )
    {
      // first unload from token
      p_tokenToMove.getCarrierToken().unloadToken( p_tokenToMove );
    }
    p_tokenCarrier.loadToken( p_tokenToMove );
    if( p_tokenToMove.canBeColored() )
    {
      // if a token enter inside another token, it take his color
      p_tokenToMove.setColor( p_tokenCarrier.getColor() );
    }
    getBoardFireCover().incFireCover( p_tokenToMove );
    // incFireCover( p_tokenToMove );
    // this update is here only to refresh token display during time mode
    updateLastTokenUpdate( null );
  }

  /**
   * Warning: this method don't check anything about fire disabling flag.
   * @param p_token
   * @param p_newColor
   */
  public void changeTokenColor(EbToken p_token, int p_newColor)
  {
    getBoardFireCover().decFireCover( p_token );
    p_token.setColor( p_newColor );
    getBoardFireCover().incFireCover( p_token );

    if( p_token.containToken() )
    {
      for( EbToken token : p_token.getContains() )
      {
        if( token.canBeColored() )
        {
          token.setColor( p_newColor );
        }
      }
    }
  }

  /**
   * Move a token to any other position on the board.
   * @param p_token
   * @param p_position have to be inside the board (no check are performed).
   */
  public void moveToken(EbToken p_token, AnBoardPosition p_position)
  {
    getBoardFireCover().decFireCover( p_token );
    if( p_token.getCarrierToken() != null )
    {
      // first unload from token
      p_token.getCarrierToken().unloadToken( p_token );
    }
    getTokenIndexSet().setPosition( p_token, new AnBoardPosition( p_position ) );
    getBoardFireCover().incFireCover( p_token );

    // this update is here only to refresh token display during time mode
    updateLastTokenUpdate( null );
  }

  /**
   * Warning: if you use this method with p_location == Board, you should be sure
   * that his position is already set to the right value.
   * @param p_token
   * @param p_locationValue should be EnuLocation.Graveyard or EnuLocation.Orbit
   * @throws RpcFmpException
   */
  public void moveToken(EbToken p_token, Location p_location) throws RpcFmpException
  {
    getBoardFireCover().decFireCover( p_token );
    if( p_token.getCarrierToken() != null )
    {
      // first unload from token
      p_token.getCarrierToken().unloadToken( p_token );
    }
    getTokenIndexSet().setPosition( p_token, p_location );
    getBoardFireCover().incFireCover( p_token );
    // this update is here only to refresh token display during time mode
    updateLastTokenUpdate( null );
  }

  public void removeToken(EbToken p_token)
  {
    try
    {
      moveToken( p_token, Location.Graveyard );
    } catch( RpcFmpException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getTokenIndexSet().removeToken( p_token );
    getSetToken().remove( p_token );
    p_token.incVersion();
  }

  public EbRegistration getWinnerRegistration()
  {
    EbRegistration winner = null;
    int point = 0;
    for( EbRegistration registration : getSetRegistration() )
    {
      if( registration.estimateWinningScore(this) > point )
      {
        winner = registration;
        point = registration.estimateWinningScore( this );
      }
    }
    return winner;
  }


  public List<EbRegistration> getRegistrationByWinningRank()
  {
    List<EbRegistration> sortedRegistration = new ArrayList<EbRegistration>();
    Map<EbRegistration, Integer> winningPoint = new HashMap<EbRegistration, Integer>();
    for( EbRegistration registration : getSetRegistration() )
    {
      int wp = registration.estimateWinningScore(this);
      int index = 0;
      while( index < sortedRegistration.size() )
      {
        if( wp > winningPoint.get( sortedRegistration.get( index ) ) )
        {
          break;
        }
        index++;
      }
      winningPoint.put( registration, wp );
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
    } while( (!haveBoardFreighter( registration ) || !registration.haveAccount()) && (index != p_currentIndex) );
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
   * @param p_registration
   * @return false if player can't deploy unit for free
   */
  public boolean canDeployUnit(EbRegistration p_registration)
  {
    if( getEbConfigGameTime().getDeploymentTimeStep() < getCurrentTimeStep() )
    {
      // too late
      return false;
    }
    if( !isParallel() && getEbConfigGameTime().getDeploymentTimeStep() != getCurrentTimeStep() )
    {
      // too early
      return false;
    }
    // check that, in parallel, player don't wan't to deploy after his first
    // move
    if( isParallel() )
    {
      int index = getLogs().size();
      while( index > 0 )
      {
        index--;
        AnEvent event = getLogs().get( index );
        if( event instanceof EbEvtMove
            && ((EbEvtMove)event).getMyRegistration( this ).getId() == p_registration.getId() )
        {
          return false;
        }
      }
    }
    return true;
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
      if( token.getType() == TokenType.Freighter && token.getColor() != EnuColor.None
          && color.isColored( token.getColor() ) )
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
      if( token.getType() == TokenType.Freighter && token.getColor() != EnuColor.None
          && color.isColored( token.getColor() ) )
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
    int freeLoadingSpace = p_carrier.getType().getLoadingCapability();
    if( p_carrier.containToken() )
    {
      for( EbToken token : p_carrier.getContains() )
      {
        freeLoadingSpace -= token.getType().getLoadingSize();
      }
    }
    return freeLoadingSpace >= p_token.getFullLoadingSize();
  }



  /**
   * the number of destructor owned by p_registration which can fire on this hexagon
   * @param p_x
   * @param p_y
   * @param p_registration
   * @return
   */
  public int getFireCover(int p_x, int p_y, EbRegistration p_registration)
  {
    EnuColor regColor = new EnuColor( p_registration.getSingleColor() );
    return getBoardFireCover().getFireCover( p_x, p_y, regColor );
  }

  public void invalidateFireCover()
  {
    getBoardFireCover().invalidateFireCover();
  }


  public BoardFireCover getBoardFireCover()
  {
    if( m_fireCover == null )
    {
      m_fireCover = new BoardFireCover( this );
    }
    return m_fireCover;
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
      LandType extraLandValue = getLand( p_token.getExtraPositions().get( 0 ) )
          .getLandValue( getCurrentTide() );
      if( extraLandValue != LandType.Sea )
      {
        if( getToken( p_token.getExtraPositions().get( 0 ), TokenType.Sluice ) != null )
        {
          return true;
        }
        return false;
      }
    case Speedboat:
    case Tarask:
    case Crayfish:
      if( landValue != LandType.Sea )
      {
        if( getToken( p_token.getPosition(), TokenType.Sluice ) != null )
        {
          return true;
        }
        return false;
      }
      return true;
    case Heap:
    case Tank:
    case Crab:
    case WeatherHen:
    case Ore0:
    case Ore:
    case Ore3:
    case Ore5:
      if( landValue == LandType.Sea )
      {
        if( getToken( p_token.getPosition(), TokenType.Pontoon ) != null )
        {
          return true;
        }
        return false;
      }
      return true;
    case Turret:
    case Freighter:
    case Pontoon:
    case Sluice:
    case Hovertank:
    default:
      return true;
    }
  }

  /**
   * determine if this token is active depending of opponents fire cover.<br/>
   * note that even if we don't display any warning, this method will return false
   * for an uncolored token. 
   * @param p_token
   * @return
   */
  public boolean isTokenFireActive(EbToken p_token)
  {
    EnuColor playerColor = getTokenOwnerColor( p_token );
    return((p_token.getType() == TokenType.Freighter) || (p_token.getType() == TokenType.Turret)
        || (playerColor.getValue() == EnuColor.None) || (getOpponentFireCover(
        playerColor.getValue(), p_token.getPosition() ).getValue() == EnuColor.None));
  }


  /**
   * determine weather the given token can produce or not a fire cover.
   * If not, his fire cover will increment the disabled fire cover to show it to player
   * @param p_token
   * @return
   */
  public boolean isTokenFireCoverDisabled(EbToken p_token)
  {
    if( p_token == null || !p_token.getType().isDestroyer() )
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
   * TODO half redundant with isTokenTideActive
   * note that any token are allowed to voluntary stuck themself into reef of marsh
   * @param p_token
   * @param p_position
   * @return true if token can move to p_position according to tide
   */
  public boolean canTokenMoveOn(EbToken p_token, AnBoardPosition p_position)
  {
    if( (p_token == null) || (!isTokenTideActive( p_token )) || (p_position == null)
        || (p_position.getX() < 0) || (p_position.getY() < 0) )
    {
      return false;
    }
    if( p_token.getHexagonSize() == 2 )
    {
      AnBoardPosition position = p_position.getNeighbour( p_position.getSector() );
      EbToken tokenPontoon = getToken( position, TokenType.Pontoon );
      if( tokenPontoon == null )
        tokenPontoon = getToken( position, TokenType.Sluice );
      if( tokenPontoon != null )
      {
        return tokenPontoon.canLoad( p_token.getType() );
      }
      // check this token is allowed to move on this hexagon
      if( p_token.canMoveOn( this, getLand( position ) ) == false )
      {
        return false;
      }
    }

    EbToken tokenPontoon = getToken( p_position, TokenType.Pontoon );
    if( tokenPontoon == null )
      tokenPontoon = getToken( p_position, TokenType.Sluice );
    if( tokenPontoon != null )
    {
      return tokenPontoon.canLoad( p_token.getType() );
    }
    // check this token is allowed to move on this hexagon
    return p_token.canMoveOn( this, getLand( p_position ) );
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
    if( (p_token.getLocation() != Location.Board) || (!p_token.getType().isDestroyer()) )
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
    case Hovertank:
      return 2;
    case Tank:
      if( getLand( p_token.getPosition() ) == LandType.Montain )
      {
        return 3;
      }
      return 2;
    case Heap:
    case Tarask:
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
    // check if pontoon is connected to freighter at high tide
    if( getCurrentTide() == Tide.Hight )
    {
      for( Sector sector : Sector.values() )
      {
        if( getToken( p_position.getNeighbour( sector ), TokenType.Freighter ) != null )
          return true;
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
    // check if pontoon is connected to freighter at high tide
    if( getCurrentTide() == Tide.Hight )
    {
      for( Sector sector : Sector.values() )
      {
        if( getToken( p_token.getPosition().getNeighbour( sector ), TokenType.Freighter ) != null )
          return true;
      }
    }
    return false;
  }
  
  /**
   * put p_token and all linked pontoon to graveyard.
   * @param p_token must be a pontoon
   * @param p_fdRemoved fire disabling that was removed are added to this list
   * @return list of all tokens removed from board to graveyard (pontoons and token on them). 
   *        Token have to be put back on board in the same order.
   * @throws RpcFmpException
   */
  public ArrayList<Long> chainRemovePontoon(EbToken p_token, List<FireDisabling> p_fdRemoved) throws RpcFmpException
  {
    assert p_token.getType() == TokenType.Pontoon;
    ArrayList<Long> pontoons = new ArrayList<Long>();
    // backup pontoon's id first (to put back on board first)
    pontoons.add( p_token.getId() );
    AnBoardPosition position = p_token.getPosition();
    // remove all tokens on pontoon
    Set<EbToken> tokens = getAllToken( position );
    for(EbToken token : tokens)
    {
      if(token != p_token)
      {
        // remove his fire disabling list
        if( token.getFireDisablingList() != null )
        {
          List<FireDisabling> fdRemoved = new ArrayList<FireDisabling>();
          fdRemoved.addAll( token.getFireDisablingList() );
          p_fdRemoved.addAll( fdRemoved );
          getBoardFireCover().removeFireDisabling( fdRemoved );
        }
        // remove token on pontoon
        pontoons.add( token.getId() );
        moveToken( token, Location.Graveyard );
        token.incVersion();
      }
    }
    // then remove pontoon
    moveToken( p_token, Location.Graveyard );
    p_token.incVersion();

    // finally remove other linked pontoons
    for( Sector sector : Sector.values() )
    {
      EbToken otherPontoon = getToken( position.getNeighbour( sector ), TokenType.Pontoon );
      if( otherPontoon != null )
      {
        pontoons.addAll( chainRemovePontoon( otherPontoon, p_fdRemoved ) );
      }
    }
    return pontoons;
  }



  // TODO move this to another class
  @Transient
  private Set<com.fullmetalgalaxy.model.persist.AnBoardPosition> m_allowedNodeGraphCache = new HashSet<com.fullmetalgalaxy.model.persist.AnBoardPosition>();
  @Transient
  private Set<com.fullmetalgalaxy.model.persist.AnBoardPosition> m_forbidenNodeGraphCache = new HashSet<com.fullmetalgalaxy.model.persist.AnBoardPosition>();

  public void resetPathGraph()
  {
    m_allowedNodeGraphCache.clear();
    m_forbidenNodeGraphCache.clear();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.pathfinder.PathGraph#getAvailableNode(com.fullmetalgalaxy.model.pathfinder.PathNode)
   */
  @Override
  public Set<com.fullmetalgalaxy.model.pathfinder.PathNode> getAvailableNode(PathNode p_fromNode,
      PathMobile p_mobile)
  {
    assert p_mobile != null;
    AnBoardPosition position = (AnBoardPosition)p_fromNode;
    Mobile mobile = (Mobile)p_mobile;
    Set<com.fullmetalgalaxy.model.pathfinder.PathNode> nodes = new HashSet<com.fullmetalgalaxy.model.pathfinder.PathNode>();
    Sector sectorValues[] = Sector.values();
    for( int i = 0; i < sectorValues.length; i++ )
    {
      AnBoardPosition neighbourPosition = position.getNeighbour( sectorValues[i] );
      if( mobile.getToken() == null )
      {
        nodes.add( neighbourPosition );
      }
      else
      {
        // looking in cache first
        if( m_allowedNodeGraphCache.contains( neighbourPosition ) )
        {
          nodes.add( neighbourPosition );
        }
        else if( m_forbidenNodeGraphCache.contains( neighbourPosition ) )
        {
          // do nothing
        }
        else
        {
          // this position wasn't explored yet
          if( mobile.getToken().canMoveOn( this, mobile.getRegistration(), neighbourPosition ) )
          {
            m_allowedNodeGraphCache.add( neighbourPosition );
            nodes.add( neighbourPosition );
          }
          else
          {
            m_forbidenNodeGraphCache.add( neighbourPosition );
          }
        }
      }
    }
    return nodes;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.pathfinder.PathGraph#heuristic(com.fullmetalgalaxy.model.pathfinder.PathNode, com.fullmetalgalaxy.model.pathfinder.PathNode)
   */
  @Override
  public float heuristic(PathNode p_fromNode, PathNode p_toNode, PathMobile p_mobile)
  {
    return (float)((AnBoardPosition)p_fromNode).getRealDistance( (AnBoardPosition)p_toNode );
  }

  /**
   */
  public List<AnEvent> createTriggersEvents()
  {
    // get all events
    List<AnEvent> events = new ArrayList<AnEvent>();
    for( EbTrigger trigger : getTriggers() )
    {
      events.addAll( trigger.createEvents( this ) );
    }
    return events;
  }

  /**
   * execute all game's trigger
   * @return true if at least on trigger was executed.
   */
  public void execTriggers()
  {
    // execute new events
    for( AnEvent event : createTriggersEvents() )
    {
      try
      {
        event.exec( this );
        addEvent( event );
      } catch( RpcFmpException e )
      {
        // print error and continue executing events
        e.printStackTrace();
      }
    }
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





}
