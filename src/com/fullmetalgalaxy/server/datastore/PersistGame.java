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
package com.fullmetalgalaxy.server.datastore;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.appengine.api.blobstore.BlobKey;

/**
 * @author Vincent
 *
 */
@Entity
public class PersistGame extends PersistEntity
{
  private String m_name = null;
  private String m_description = "";
  private Date m_creationDate = new Date( System.currentTimeMillis() );
  private boolean m_isStarted = false;
  private boolean m_history = false;
  private int m_maxNumberOfPlayer = 0;
  private int m_currentNumberOfRegiteredPlayer = 0;
  private boolean m_isOpen = false;
  private GameType m_gameType = GameType.MultiPlayer;
  private PlanetType m_planetType = PlanetType.Desert;
  // variante
  private ConfigGameTime m_configGameTime = ConfigGameTime.Standard;
  private ConfigGameVariant m_configGameVariant = ConfigGameVariant.Standard;
  // map size
  private int m_landWidth = 0;
  private int m_landHeight = 0;

  // TODO we will probably need a list here as query like '%player%' are infeasible
  private String m_players = null;

  /** minimap stored here to save some ImageService's CPU time */
  private BlobKey m_minimapBlobKey = null;
  private String m_minimapUri = null;
  

  public PersistGame()
  {
  }

  @Override
  public void setEb(EbBase p_ebBase)
  {
    // TODO we can do this part in an automated way
    EbGame game = EbGame.class.cast( p_ebBase );
    if( game == null )
    {
      return;
    }
    setName( game.getName() );
    game.setName( null );
    setDescription( game.getDescription() );
    game.setDescription( null );
    setCreationDate( game.getCreationDate() );
    game.setCreationDate( null );
    setStarted( game.isStarted() );
    setHistory( game.isHistory() );
    setMaxNumberOfPlayer( game.getMaxNumberOfPlayer() );
    setCurrentNumberOfRegiteredPlayer( game.getCurrentNumberOfRegiteredPlayer() );
    setGameType( game.getGameType() );
    setPlanetType( game.getPlanetType() );
    setConfigGameTime( game.getConfigGameTime() );
    setConfigGameVariant( game.getConfigGameVariant() );
    setLandWidth( game.getLandWidth() );
    setLandHeight( game.getLandHeight() );
    setPlayers( game );
    setOpen( false );
    setMinimapUri( game.getMinimapUri() );
    if( game.getMinimapBlobKey() != null )
    {
      setMinimapBlobKey( new BlobKey( game.getMinimapBlobKey() ) );
    }
    if( !isStarted() && getCurrentNumberOfRegiteredPlayer() < getMaxNumberOfPlayer() )
    {
      setOpen( true );
    }
    super.setEb( p_ebBase );
    game.setName( getName() );
    game.setDescription( getDescription() );
    game.setCreationDate( getCreationDate() );
  }

  public EbGame getGame()
  {
    EbBase base = getEb();
    EbGame game = EbGame.class.cast( base );
    if( game != null )
    {
      game.setName( getName() );
      game.setDescription( getDescription() );
      game.setCreationDate( getCreationDate() );
      game.setVersion( getVersion() );
      if( game.getMinimapUri() == null )
      {
        game.setMinimapUri( getMinimapUri() );
      }
    }
    return game;
  }

  /**
   * @return the name
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
   * @return the creationDate
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
   * @return the isStarted
   */
  public boolean isStarted()
  {
    return m_isStarted;
  }

  /**
   * @param p_isStarted the isStarted to set
   */
  public void setStarted(boolean p_isStarted)
  {
    m_isStarted = p_isStarted;
  }

  /**
   * @return the history
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
  }

  /**
   * @return the configGameVariant
   */
  public ConfigGameVariant getConfigGameVariant()
  {
    return m_configGameVariant;
  }

  /**
   * @param p_configGameVariant the configGameVariant to set
   */
  public void setConfigGameVariant(ConfigGameVariant p_configGameVariant)
  {
    m_configGameVariant = p_configGameVariant;
  }

  /**
   * @return the landWidth
   */
  public int getLandWidth()
  {
    return m_landWidth;
  }

  /**
   * @param p_landWidth the landWidth to set
   */
  public void setLandWidth(int p_landWidth)
  {
    m_landWidth = p_landWidth;
  }

  /**
   * @return the landHeight
   */
  public int getLandHeight()
  {
    return m_landHeight;
  }

  /**
   * @param p_landHeight the landHeight to set
   */
  public void setLandHeight(int p_landHeight)
  {
    m_landHeight = p_landHeight;
  }

  /**
   * @return the currentNumberOfRegiteredPlayer
   */
  public int getCurrentNumberOfRegiteredPlayer()
  {
    return m_currentNumberOfRegiteredPlayer;
  }

  /**
   * @param p_currentNumberOfRegiteredPlayer the currentNumberOfRegiteredPlayer to set
   */
  public void setCurrentNumberOfRegiteredPlayer(int p_currentNumberOfRegiteredPlayer)
  {
    m_currentNumberOfRegiteredPlayer = p_currentNumberOfRegiteredPlayer;
  }



  /**
   * @return the isOpen
   */
  public boolean isOpen()
  {
    return m_isOpen;
  }

  /**
   * @param p_isOpen the isOpen to set
   */
  public void setOpen(boolean p_isOpen)
  {
    m_isOpen = p_isOpen;
  }

  /**
   * @return the players
   */
  public String getPlayers()
  {
    return m_players;
  }

  /**
   * @param p_players the players to set
   */
  public void setPlayers(EbGame p_game)
  {
    StringBuffer strBuf = new StringBuffer( " " );
    // get player order
    List<EbRegistration> sortedRegistration = new ArrayList<EbRegistration>();
    if( !p_game.isAsynchron() )
    {
      for( int index = 0; index < p_game.getSetRegistration().size(); index++ )
      {
        sortedRegistration.add( p_game.getRegistrationByOrderIndex( index ) );
      }
    }
    else
    {
      sortedRegistration.addAll( p_game.getSetRegistration() );
    }

    int playerCount = 0;
    for( EbRegistration player : sortedRegistration )
    {
      playerCount++;
      if(player != null)
      {
        strBuf.append( player.getAccountPseudo() );
      }
      if( p_game.getCurrentPlayerRegistration() == player )
      {
        strBuf
            .append( " <img style='border=none' border=0 src='/images/css/icon_action.cache.png' alt='Current' />" );
      }
      if( playerCount < sortedRegistration.size() )
      {
        if( p_game.isAsynchron() )
        {
          strBuf.append( " - " );
        }
        else
        {
          strBuf.append( " > " );
        }
      }
    }
    strBuf.append( " " );
    m_players = strBuf.toString();
  }

  /**
   * @return the minimapData
   */
  public BlobKey getMinimapBlobKey()
  {
    return m_minimapBlobKey;
  }

  /**
   * @param p_minimapData the minimapData to set
   */
  public void setMinimapBlobKey(BlobKey p_minimapData)
  {
    m_minimapBlobKey = p_minimapData;
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



}
