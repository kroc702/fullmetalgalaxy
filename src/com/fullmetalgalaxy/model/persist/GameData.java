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
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is a concatenation of preview data and other data.
 * It shoudln't contain any business logic, only data access.
 * 
 * @author vlegendr
 *
 */
public class GameData implements java.io.Serializable, IsSerializable
{
  private static final long serialVersionUID = -7121387606249451938L;

  private EbGamePreview m_preview = null;
  private EbGameData m_data = null;
  
  public GameData()
  {
    super();
    init();
  }
  
  public GameData(EbGamePreview p_preview, EbGameData p_data)
  {
    super();
    m_preview = p_preview;
    m_data = p_data;
  }

  private void init()
  {
    m_preview = new EbGamePreview();
    m_data = new EbGameData();
    // allocate default land size
    setLandSize( m_preview.getLandWidth(), m_preview.getLandHeight() );
    setLand( 0, 0, LandType.Plain );    
  }
  
  
  public EbGamePreview getPreview()
  {
    return m_preview;
  }

  public EbGameData getData()
  {
    return m_data;
  }
  
  // data access that need both class
  // ================================

  /**
   * this only an estimation
   * @return
   */
  public int getNumberOfHexagon()
  {
    int hexagonCount = getLandHeight() * getLandWidth();
    if( !isMapSquare() )
    {
      // assume map is an hexagon
      hexagonCount *= 3f / 4f;
    }
    return hexagonCount;
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
   * 
   * @param x coordinate
   * @param y coordinate
   * @return the land ID.
   */
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
      assert position < m_data.getLands().length;
      int intId = (int)id & 0x0F;
      if( offset == 0 )
      {
        m_data.getLands()[position] = (byte)(((int)m_data.getLands()[position] & 0xF0) + intId);
      }
      else
      {
        m_data.getLands()[position] = (byte)(((int)m_data.getLands()[position] & 0x0F) + (intId << 4));
      }
    }
  }

  /**
   * Warning: this method clear lands array.
   * @param p_width
   * @param p_height
   */
  public void setLandSize(int p_width, int p_height)
  {
    boolean isMapSquare = isMapSquare();
    m_data.setLands( new byte[(p_width * p_height) / 2 + 1] );
    setLandWidth( p_width );
    setLandHeight( p_height );
    // this is a little workaround to make isMapSquare() continue working after this call
    if(isMapSquare)
    {
      setLand( 0, 0, LandType.Plain );
    }
  }


  /**
   * 
   * @param p_position
   * @return true if p_position is on the board
   */
  public boolean isInsideBoard(AnBoardPosition p_position)
  {
    if( (p_position.getX() >= 0) && (p_position.getX() < getLandWidth())
        && (p_position.getY() >= 0) && (p_position.getY() < getLandHeight())
        && (getLand( p_position ) != LandType.None) )
    {
      return true;
    }
    return false;
  }

  public boolean isMapSquare()
  {
    return getLand( 0, 0 ) != LandType.None;
  }

  
  // common methods
  // ==============
  
  public void setId(long p_id)
  {
    m_preview.setId( p_id );
    m_data.setId( p_id );
  }

  
  public EbBase createEbBase()
  {
    return m_preview.createEbBase();
  }

  public boolean isTrancient()
  {
    return m_preview.isTrancient();
  }

  public void setTrancient()
  {
    m_preview.setTrancient();
    m_data.setTrancient();
  }

  public void updateFrom(EbBase p_update)
  {
    m_preview.updateFrom( p_update );
    m_data.updateFrom( p_update );
  }

  public long getId()
  {
    return m_preview.getId();
  }

  public void reinit()
  {
    m_preview.reinit();
    m_data.reinit();
  }

  @Override
  public String toString()
  {
    return m_preview.getName();
  }

  // delegate methods
  // ================
  
  /**
   * 
   * @return true if the game is finished
   */
  public boolean isFinished()
  {
    return m_preview.isFinished();
  }

  /**
   * 
   * @return true if this game was aborted before the end. ie not finished but history
   */
  public boolean isAborted()
  {
    return m_preview.isAborted();
  }
  
  public int getLandPixWidth(EnuZoom p_zoom)
  {
    return m_preview.getLandPixWidth( p_zoom );
  }

  public int getLandPixHeight(EnuZoom p_zoom)
  {
    return m_preview.getLandPixHeight( p_zoom );
  }

  public EbRegistration getCurrentPlayerRegistration()
  {
    return m_preview.getCurrentPlayerRegistration();
  }

  public void setCurrentPlayerRegistration(EbRegistration p_currentPlayerRegistration)
  {
    m_preview.setCurrentPlayerRegistration( p_currentPlayerRegistration );
  }

  public EbRegistration getRegistration(long p_idRegistration)
  {
    return m_preview.getRegistration( p_idRegistration );
  }

  public EbRegistration getRegistrationByOrderIndex(int p_index)
  {
    return m_preview.getRegistrationByOrderIndex( p_index );
  }

  public EbRegistration getRegistrationByIdAccount(long p_idAccount)
  {
    return m_preview.getRegistrationByIdAccount( p_idAccount );
  }

  public EbRegistration getRegistrationByColor(int p_color)
  {
    return m_preview.getRegistrationByColor( p_color );
  }

  public List<EbRegistration> getRegistrationByPlayerOrder()
  {
    return m_preview.getRegistrationByPlayerOrder();
  }

  public EbRegistration getPreviousPlayerRegistration()
  {
    return m_preview.getPreviousPlayerRegistration();
  }

  public Set<EnuColor> getFreeColors4Registration()
  {
    return m_preview.getFreeColors4Registration();
  }

  public Set<EnuColor> getFreeRegistrationColors()
  {
    return m_preview.getFreeRegistrationColors();
  }

  public Set<EnuColor> getFreePlayersColors()
  {
    return m_preview.getFreePlayersColors();
  }

  public boolean isOpen()
  {
    return m_preview.isOpen();
  }

  public int getCurrentNumberOfRegiteredPlayer()
  {
    return m_preview.getCurrentNumberOfRegiteredPlayer();
  }

  public EbPublicAccount getAccountCreator()
  {
    return m_preview.getAccountCreator();
  }

  public void setAccountCreator(EbPublicAccount p_account)
  {
    m_preview.setAccountCreator( p_account );
  }

  public Date getCreationDate()
  {
    return m_preview.getCreationDate();
  }

  public void setCreationDate(Date p_creationDate)
  {
    m_preview.setCreationDate( p_creationDate );
  }

  public int getLandWidth()
  {
    return m_preview.getLandWidth();
  }

  public void setLandWidth(int p_landWidth)
  {
    m_preview.setLandWidth( p_landWidth );
  }

  public int getLandHeight()
  {
    return m_preview.getLandHeight();
  }

  public void setLandHeight(int p_landHeight)
  {
    m_preview.setLandHeight( p_landHeight );
  }

  public Set<EbRegistration> getSetRegistration()
  {
    return m_preview.getSetRegistration();
  }

  public void setSetRegistration(Set<EbRegistration> p_setRegistration)
  {
    m_preview.setSetRegistration( p_setRegistration );
  }

  public String getName()
  {
    return m_preview.getName();
  }

  public void setName(String p_name)
  {
    m_preview.setName( p_name );
  }

  public int getMaxNumberOfPlayer()
  {
    return m_preview.getMaxNumberOfPlayer();
  }

  public void setMaxNumberOfPlayer(int p_maxNumberOfPlayer)
  {
    m_preview.setMaxNumberOfPlayer( p_maxNumberOfPlayer );
  }

  public boolean isAsynchron()
  {
    return m_preview.isAsynchron();
  }

  public boolean isHistory()
  {
    return m_preview.isHistory();
  }

  public void setHistory(boolean p_history)
  {
    m_preview.setHistory( p_history );
  }

  public int getCurrentTimeStep()
  {
    return m_preview.getCurrentTimeStep();
  }

  public void setCurrentTimeStep(int p_currentTimeStep)
  {
    m_preview.setCurrentTimeStep( p_currentTimeStep );
  }

  public boolean isStarted()
  {
    return m_preview.isStarted();
  }

  public void setStarted(boolean p_started)
  {
    m_preview.setStarted( p_started );
  }

  public String getDescription()
  {
    return m_preview.getDescription();
  }

  public void setDescription(String p_description)
  {
    m_preview.setDescription( p_description );
  }

  public ConfigGameTime getConfigGameTime()
  {
    return m_preview.getConfigGameTime();
  }

  public ConfigGameVariant getConfigGameVariant()
  {
    return m_preview.getConfigGameVariant();
  }

  public GameType getGameType()
  {
    return m_preview.getGameType();
  }

  public void setGameType(GameType p_gameType)
  {
    m_preview.setGameType( p_gameType );
  }

  public PlanetType getPlanetType()
  {
    return m_preview.getPlanetType();
  }

  public void setPlanetType(PlanetType p_planetType)
  {
    m_preview.setPlanetType( p_planetType );
  }

  public String getMinimapUri()
  {
    return m_preview.getMinimapUri();
  }

  public void setMinimapUri(String p_minimapUri)
  {
    m_preview.setMinimapUri( p_minimapUri );
  }

  public String getMinimapBlobKey()
  {
    return m_preview.getMinimapBlobKey();
  }

  public void setMinimapBlobKey(String p_minimapBlobKey)
  {
    m_preview.setMinimapBlobKey( p_minimapBlobKey );
  }

  public void setEbConfigGameTime(EbConfigGameTime p_config)
  {
    m_preview.setEbConfigGameTime( p_config );
  }

  public EbConfigGameTime getEbConfigGameTime()
  {
    return m_preview.getEbConfigGameTime();
  }

  public void setEbConfigGameVariant(EbConfigGameVariant p_config)
  {
    m_preview.setEbConfigGameVariant( p_config );
  }

  public EbConfigGameVariant getEbConfigGameVariant()
  {
    return m_preview.getEbConfigGameVariant();
  }

  public void setConfigGameTime(ConfigGameTime p_configGameTime)
  {
    m_preview.setConfigGameTime( p_configGameTime );
  }

  public void setConfigGameVariant(ConfigGameVariant p_configGameVariant)
  {
    m_preview.setConfigGameVariant( p_configGameVariant );
  }

  public long getVersion()
  {
    return m_preview.getVersion();
  }

  public void setVersion(long p_version)
  {
    m_preview.setVersion( p_version );
  }

  public void incVersion()
  {
    m_preview.incVersion();
  }

  public void decVersion()
  {
    m_preview.decVersion();
  }

  /**
   * @return
   * @see com.fullmetalgalaxy.model.persist.EbGamePreview#getScoreBonus()
   */
  public int getScoreBonus()
  {
    return m_preview.getScoreBonus();
  }

  /**
   * @return
   * @see com.fullmetalgalaxy.model.persist.EbGamePreview#isVip()
   */
  public boolean isVip()
  {
    return m_preview.isVip();
  }

  /**
   * @param p_isVip
   * @see com.fullmetalgalaxy.model.persist.EbGamePreview#setVip(boolean)
   */
  public void setVip(boolean p_isVip)
  {
    m_preview.setVip( p_isVip );
  }

  public long getNextLocalId()
  {
    return m_data.getNextLocalId();
  }

  public byte[] getLands()
  {
    return m_data.getLands();
  }

  public void setLands(byte[] p_lands)
  {
    m_data.setLands( p_lands );
  }

  public Set<EbToken> getSetToken()
  {
    return m_data.getSetToken();
  }

  public void setSetToken(Set<EbToken> p_setToken)
  {
    m_data.setSetToken( p_setToken );
  }

  public Tide getCurrentTide()
  {
    return m_data.getCurrentTide();
  }

  public void setCurrentTide(Tide p_currentTide)
  {
    m_data.setCurrentTide( p_currentTide );
  }

  public Tide getNextTide()
  {
    return m_data.getNextTide();
  }

  public void setNextTide(Tide p_nextTide)
  {
    m_data.setNextTide( p_nextTide );
  }

  public Tide getNextTide2()
  {
    return m_data.getNextTide2();
  }

  public void setNextTide2(Tide p_nextTide)
  {
    m_data.setNextTide2( p_nextTide );
  }

  public int getLastTideChange()
  {
    return m_data.getLastTideChange();
  }

  public void setLastTideChange(int p_lastTideChange)
  {
    m_data.setLastTideChange( p_lastTideChange );
  }

  public Date getLastTimeStepChange()
  {
    return m_data.getLastTimeStepChange();
  }

  public void setLastTimeStepChange(Date p_lastTimeStepChange)
  {
    m_data.setLastTimeStepChange( p_lastTimeStepChange );
  }

  public List<AnEvent> getLogs()
  {
    return m_data.getLogs();
  }

  public void setLogs(List<AnEvent> p_setActionLog)
  {
    m_data.setLogs( p_setActionLog );
  }

  public ArrayList<Integer> getTakeOffTurns()
  {
    return m_data.getTakeOffTurns();
  }

  public void setTakeOffTurns(ArrayList<Integer> p_takeOffTurns)
  {
    m_data.setTakeOffTurns( p_takeOffTurns );
  }

  public List<EbTrigger> getTriggers()
  {
    return m_data.getTriggers();
  }

  public void setTriggers(List<EbTrigger> p_triggers)
  {
    m_data.setTriggers( p_triggers );
  }

  public String getMapUri()
  {
    return m_data.getMapUri();
  }

  public void setMapUri(String p_mapUri)
  {
    m_data.setMapUri( p_mapUri );
  }

  public String getMessage()
  {
    return m_data.getMessage();
  }

  public void setMessage(String p_messages)
  {
    m_data.setMessage( p_messages );
  }

  public boolean isMessageWebUrl()
  {
    return m_data.isMessageWebUrl();
  }
  
  
  
}