/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.util.ArrayList;

import javax.persistence.Transient;

import com.fullmetalgalaxy.model.persist.EbBase;


/**
 * @author Vincent Legendre
 * this class represent a time configuration. it could have been mapped onto an sql table.
 * but for performance reason, it's only a set of constant.
 * see ConfigGameTime
 */
public class EbConfigGameTime extends EbBase
{
  static final long serialVersionUID = 1;

  private int m_timeStepDurationInSec = 86400; // one day
  private int m_tideChangeFrequency = 1; // every time steps
  private int m_totalTimeStep = 25;
  private int m_actionPtPerTimeStep = 15;
  private int m_actionPtPerExtraShip = 5;
  private int m_bulletCountIncrement = 2;
  private String m_description = "";
  private ArrayList<Integer> m_takeOffTurns = new ArrayList<Integer>();

  /**
   * 
   */
  public EbConfigGameTime()
  {
    super();
    init();
  }

  private void init()
  {
    m_timeStepDurationInSec = 86400; // one day
    m_tideChangeFrequency = 1; // every time steps
    m_totalTimeStep = 25;
    m_actionPtPerTimeStep = 15;
    m_actionPtPerExtraShip = 5;
    m_takeOffTurns = new ArrayList<Integer>();
    m_takeOffTurns.add( 21 );
    m_takeOffTurns.add( 25 );
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }


  /**
   * @return the timeStepDuration
   * @WgtHidden
   */
  @Transient
  public long getTimeStepDurationInMili()
  {
    return getTimeStepDurationInSec() * 1000;
  }

  /**
   * 
   * @return an estimation of the total action point during the game.
   */
  public int estimateTotalActionPoint()
  {
    return getActionPtPerTimeStep() * getTotalTimeStep();
  }


  // =============================

  /**
   * @return the timeStepDurationInSec
   */
  public int getTimeStepDurationInSec()
  {
    return m_timeStepDurationInSec;
  }

  /**
   * @param p_timeStepDurationInSec the timeStepDurationInSec to set
   */
  public void setTimeStepDurationInSec(int p_timeStepDurationInSec)
  {
    m_timeStepDurationInSec = p_timeStepDurationInSec;
  }

  /**
   * @return the tideChangeFrequency
   */
  public int getTideChangeFrequency()
  {
    return m_tideChangeFrequency;
  }

  /**
   * @param p_tideChangeFrequency the tideChangeFrequency to set
   */
  public void setTideChangeFrequency(int p_tideChangeFrequency)
  {
    m_tideChangeFrequency = p_tideChangeFrequency;
  }

  /**
   * @return the totalTimeStep
   */
  public int getTotalTimeStep()
  {
    return m_totalTimeStep;
  }

  /**
   * @param p_totalTimeStep the totalTimeStep to set
   */
  public void setTotalTimeStep(int p_totalTimeStep)
  {
    m_totalTimeStep = p_totalTimeStep;
  }

  /**
   * @return the actionPtPerTimeStep
   */
  public int getActionPtPerTimeStep()
  {
    return m_actionPtPerTimeStep;
  }

  /**
   * @param p_actionPtPerTimeStep the actionPtPerTimeStep to set
   */
  public void setActionPtPerTimeStep(int p_actionPtPerTimeStep)
  {
    m_actionPtPerTimeStep = p_actionPtPerTimeStep;
  }

  /**
   * @return the actionPtPerExtraShip
   */
  public int getActionPtPerExtraShip()
  {
    return m_actionPtPerExtraShip;
  }

  /**
   * @param p_actionPtPerExtraShip the actionPtPerExtraShip to set
   */
  public void setActionPtPerExtraShip(int p_actionPtPerExtraShip)
  {
    m_actionPtPerExtraShip = p_actionPtPerExtraShip;
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
   * @return the bulletCountIncrement
   */
  public int getBulletCountIncrement()
  {
    return m_bulletCountIncrement;
  }

  /**
   * @param p_bulletCountIncrement the bulletCountIncrement to set
   */
  public void setBulletCountIncrement(int p_bulletCountIncrement)
  {
    m_bulletCountIncrement = p_bulletCountIncrement;
  }

  /**
   * @return the takeOffTurns
   */
  public ArrayList<Integer> getTakeOffTurns()
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

}
