/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.util.Date;

import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 *
 */
public class DbbPlayerPointsHistory
{
  // theses data come from database
  // ------------------------------
  private long m_id = 0;
  private long m_idAccount = 0;
  private long m_idGame = 0;
  private Date m_lastUpdate = null; // new Date();
  private int m_currentPoints = 0;


  /**
   * 
   */
  public DbbPlayerPointsHistory()
  {
    // TODO Auto-generated constructor stub
  }

  public DbbPlayerPointsHistory(EbRegistration p_registration)
  {
    setIdAccount( p_registration.getAccountId() );
    setIdGame( p_registration.getIdGame() );
  }

  // getters / setters
  // -----------------

  /**
   * @return the id
   */
  public long getId()
  {
    return m_id;
  }


  /**
   * @param p_id the id to set
   */
  public void setId(long p_id)
  {
    m_id = p_id;
  }


  /**
   * @return the idAccount
   */
  public long getIdAccount()
  {
    return m_idAccount;
  }


  /**
   * @param p_idAccount the idAccount to set
   */
  public void setIdAccount(long p_idAccount)
  {
    m_idAccount = p_idAccount;
  }


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
   * @return the currentPoints
   */
  public int getCurrentPoints()
  {
    return m_currentPoints;
  }


  /**
   * @param p_currentPoints the currentPoints to set
   */
  public void setCurrentPoints(int p_currentPoints)
  {
    m_currentPoints = p_currentPoints;
  }

}
