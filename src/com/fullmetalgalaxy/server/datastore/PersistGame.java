/**
 * 
 */
package com.fullmetalgalaxy.server.datastore;

import java.util.Date;

import javax.persistence.Entity;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;

/**
 * @author Vincent
 *
 */
@Entity
public class PersistGame extends PersistEntity
{
  private String m_name = null;
  private String m_description = "";
  private Date m_creationDate = new Date();
  private String m_loginCreator = "";
  private String m_loginCurrentPlayer = "";
  private boolean m_isStarted = false;
  private boolean m_isAsynchron = true;
  private boolean m_history = false;
  private int m_maxNumberOfPlayer = 0;
  private int m_currentNumberOfRegiteredPlayer = 0;
  private GameType m_gameType = GameType.MultiPlayer;
  private PlanetType m_planetType = PlanetType.Desert;
  // variante
  private ConfigGameTime m_configGameTime = ConfigGameTime.Standard;
  private ConfigGameVariant m_configGameVariant = ConfigGameVariant.Standard;
  // map size
  private int m_landWidth = 0;
  private int m_landHeight = 0;

  
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
    setLoginCreator( "" );
    setLoginCurrentPlayer( "" );
    setStarted( !game.isFinished() );
    setAsynchron( game.isAsynchron() );
    setHistory( game.isHistory() );
    setMaxNumberOfPlayer( game.getMaxNumberOfPlayer() );
    setCurrentNumberOfRegiteredPlayer( game.getCurrentNumberOfRegiteredPlayer() );
    setGameType( game.getGameType() );
    setPlanetType( game.getPlanetType() );
    setConfigGameTime( game.getConfigGameTime() );
    setConfigGameVariant( game.getConfigGameVariant() );
    setLandWidth( game.getLandWidth() );
    setLandHeight( game.getLandHeight() );
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
   * @return the isAsynchron
   */
  public boolean isAsynchron()
  {
    return m_isAsynchron;
  }

  /**
   * @param p_isAsynchron the isAsynchron to set
   */
  public void setAsynchron(boolean p_isAsynchron)
  {
    m_isAsynchron = p_isAsynchron;
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
   * @return the loginCreator
   */
  public String getLoginCreator()
  {
    return m_loginCreator;
  }

  /**
   * @param p_loginCreator the loginCreator to set
   */
  public void setLoginCreator(String p_loginCreator)
  {
    m_loginCreator = p_loginCreator;
  }

  /**
   * @return the loginCurrentPlayer
   */
  public String getLoginCurrentPlayer()
  {
    return m_loginCurrentPlayer;
  }

  /**
   * @param p_loginCurrentPlayer the loginCurrentPlayer to set
   */
  public void setLoginCurrentPlayer(String p_loginCurrentPlayer)
  {
    m_loginCurrentPlayer = p_loginCurrentPlayer;
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



}
