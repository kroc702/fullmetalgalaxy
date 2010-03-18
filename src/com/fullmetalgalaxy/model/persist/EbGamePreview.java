/**
 * 
 */
package com.fullmetalgalaxy.model.persist;

import java.util.Date;

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.constant.ConfigGameVariant;



/**
 * @author Kroc
 * should be a read only entity bean.
 */
public class EbGamePreview extends AnPojoBase // EbBase
{
  static final long serialVersionUID = 1;


  // theses data come from gamePreview view
  // ------------------------------------------
  private long m_id = 0;
  private String m_name = "";
  private String m_description = "";
  private int m_maxNumberOfPlayer = 0;
  private Date m_creationDate = new Date();
  private boolean m_isStarted = false;
  private boolean m_isAsynchron = true;
  private boolean m_history = false;
  private GameType m_gameType = GameType.MultiPlayer;
  private PlanetType m_planetType = PlanetType.Desert;
  // map size
  private int m_landWidth = 0;
  private int m_landHeight = 0;
  // configuration
  private ConfigGameTime m_configGameTime = ConfigGameTime.Standard;
  private ConfigGameVariant m_configGameVariant = ConfigGameVariant.Standard;

  private String m_loginCreator = "";
  private String m_loginCurrentPlayer = "";

  private int m_currentNumberOfRegiteredPlayer = 0;


  public EbGamePreview()
  {
    super();
    init();
  }

  public EbGamePreview(String p_name, String p_description, int p_maxNumberOfPlayer)
  {
    super();
    init();

    m_name = p_name;
    m_description = p_description;
    m_maxNumberOfPlayer = p_maxNumberOfPlayer;
  }

  public EbGamePreview(EbGame p_game)
  {
    super();
    assert p_game != null;
    m_id = p_game.getId();
    m_name = p_game.getName();
    m_description = p_game.getDescription();
    m_maxNumberOfPlayer = p_game.getMaxNumberOfPlayer();
    m_creationDate = p_game.getCreationDate();
    m_isStarted = p_game.isStarted();
    m_isAsynchron = p_game.isAsynchron();
    m_history = p_game.isHistory();
    m_landWidth = p_game.getLandWidth();
    m_landHeight = p_game.getLandHeight();
    m_configGameTime = p_game.getConfigGameTime();
    m_configGameVariant = p_game.getConfigGameVariant();
    // TODO solve this (put in persistGame ?)
    /*if( p_game.getAccountCreator() != null )
    {
      m_loginCreator = p_game.getAccountCreator().getLogin();
    }
    else*/
    {
      m_loginCreator = "";
    }
    /*if( p_game.getCurrentPlayerRegistration() != null
        && p_game.getCurrentPlayerRegistration().getAccount() != null )
    {
      m_loginCurrentPlayer = p_game.getCurrentPlayerRegistration().getAccount().getLogin();
    }
    else*/
    {
      m_loginCurrentPlayer = "";
    }
    m_currentNumberOfRegiteredPlayer = p_game.getCurrentNumberOfRegiteredPlayer();
  }

  private void init()
  {
    m_id = 0;
    m_name = "";
    m_description = "";
    m_maxNumberOfPlayer = 0;
    m_creationDate = new Date();
    m_isStarted = true;
    m_isAsynchron = true;
    m_history = false;
    m_landWidth = 0;
    m_landHeight = 0;
    m_configGameTime = ConfigGameTime.Standard;
    m_configGameVariant = ConfigGameVariant.Standard;
    m_loginCreator = "";
    m_loginCurrentPlayer = "";

    m_currentNumberOfRegiteredPlayer = 0;
  }

  public void reinit()
  {
    // super.reinit();
    this.init();
  }


  public long getId()
  {
    return m_id;
  }

  // getters / setters
  // -----------------
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
   * @return the currentNumberOfRegiteredPlayer
   * @WgtHidden
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
   * @WgtHidden
   */
  public boolean getHistory()
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
   * @return the creationDate
   */
  public Date getCreationDate()
  {
    return m_creationDate;
  }

  /**
   * @return the isPaused
   */
  public boolean isStarted()
  {
    return m_isStarted;
  }

  /**
   * @return the landWidth
   */
  public int getLandWidth()
  {
    return m_landWidth;
  }

  /**
   * @return the landHeight
   */
  public int getLandHeight()
  {
    return m_landHeight;
  }

  /**
   * @return the loginCreator
   */
  // @Column(table = "fmp_gamepreview")
  public String getLoginCreator()
  {
    return m_loginCreator;
  }

  /**
   * @return the loginCurrentPlayer
   */
  // @Column(table = "fmp_gamepreview")
  public String getLoginCurrentPlayer()
  {
    return m_loginCurrentPlayer;
  }

  /**
   * @param p_creationDate the creationDate to set
   */
  public void setCreationDate(Date p_creationDate)
  {
    m_creationDate = p_creationDate;
  }

  /**
   * @param p_isPaused the isPaused to set
   */
  public void setStarted(boolean p_isStarted)
  {
    m_isStarted = p_isStarted;
  }

  /**
   * @param p_landWidth the landWidth to set
   */
  public void setLandWidth(int p_landWidth)
  {
    m_landWidth = p_landWidth;
  }

  /**
   * @param p_landHeight the landHeight to set
   */
  public void setLandHeight(int p_landHeight)
  {
    m_landHeight = p_landHeight;
  }


  /**
   * @param p_loginCreator the loginCreator to set
   */
  public void setLoginCreator(String p_loginCreator)
  {
    m_loginCreator = p_loginCreator;
  }

  /**
   * @param p_loginCurrentPlayer the loginCurrentPlayer to set
   */
  public void setLoginCurrentPlayer(String p_loginCurrentPlayer)
  {
    m_loginCurrentPlayer = p_loginCurrentPlayer;
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



}
