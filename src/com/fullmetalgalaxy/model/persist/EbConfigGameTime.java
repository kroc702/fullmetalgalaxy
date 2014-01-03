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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;

import java.util.ArrayList;

import javax.persistence.Transient;



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
  private int m_totalTimeStep = 26;
  /** standard action point increment */
  private int m_actionPtPerTimeStep = 15;
  /** extra action point increment per extra freighter */
  private int m_actionPtPerExtraShip = 5;
  /** maximum number of action points */
  private int m_actionPtMaxReserve = 25;
  /** extra maximum number of action points */
  private int m_actionPtMaxPerExtraShip = 5;

  public int m_bulletCountIncrement = 20;
  private String m_description = "";
  private ArrayList<Integer> m_takeOffTurns = new ArrayList<Integer>();
  /** if true, players can all play at same time. ie parallel mode
   * asynchron is the old name. it is keep for backward compatibility on database */
  private boolean m_isParallel = false;
  /** for parallel game: if a player move a unit, it lock the game for a short time period */
  private int m_lockGameInMillis = 2000;

  /** in turn by turn, action point are rounded to this value */
  private int m_roundActionPt = 1;
  /** in turn by turn it's the time step during which we can deploy token.
   *  in parallel mode it's the time step up to which we can deploy token. */
  private int m_deploymentTimeStep = 1;
  
  /**
   * 
   */
  public EbConfigGameTime()
  {
    super();
    init();
  }

  public EbConfigGameTime(EbConfigGameTime p_config)
  {
    super(p_config);
    m_timeStepDurationInSec = p_config.getTimeStepDurationInSec();
    m_tideChangeFrequency = p_config.getTideChangeFrequency();
    m_totalTimeStep = p_config.getTotalTimeStep();
    m_actionPtPerTimeStep = p_config.getActionPtPerTimeStep();
    m_actionPtPerExtraShip = p_config.getActionPtPerExtraShip();
    m_actionPtMaxReserve = p_config.getActionPtMaxReserve();
    m_actionPtMaxPerExtraShip = p_config.getActionPtMaxPerExtraShip();
    m_takeOffTurns = new ArrayList<Integer>( p_config.getTakeOffTurns() );
    m_isParallel = p_config.isParallel();
    m_lockGameInMillis = p_config.getLockGameInMillis();
    m_roundActionPt = p_config.getRoundActionPt();
    m_deploymentTimeStep = p_config.getDeploymentTimeStep();
    m_description = new String( p_config.getDescription() );
    setBulletCountIncrement( p_config.getBulletCountIncrement() );
  }
  

  private void init()
  {
    m_timeStepDurationInSec = 86400; // one day
    m_tideChangeFrequency = 1; // every time steps
    m_totalTimeStep = 26;
    m_actionPtPerTimeStep = 15;
    m_actionPtPerExtraShip = 5;
    m_actionPtMaxReserve = 25;
    m_actionPtMaxPerExtraShip = 5;
    m_takeOffTurns = new ArrayList<Integer>();
    m_takeOffTurns.add( 21 );
    m_takeOffTurns.add( 26 );
    m_isParallel = false;
    m_roundActionPt = 1;
    m_deploymentTimeStep = 1;
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
  public float getBulletCountIncrement()
  {
    return m_bulletCountIncrement/10f;
  }

  /**
   * @param p_bulletCountIncrement the bulletCountIncrement to set
   */
  public void setBulletCountIncrement(float p_bulletCountIncrement)
  {
    m_bulletCountIncrement = (int)Math.round(p_bulletCountIncrement*10);
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

  /**
   * @return the asynchron
   */
  public boolean isParallel()
  {
    return m_isParallel;
  }

  public boolean isQuick()
  {
    return m_timeStepDurationInSec > 0 && m_timeStepDurationInSec < 60*30; // turn less than 30min  
  }
  
  /**
   * @param p_asynchron the asynchron to set
   */
  public void setParallel(boolean p_asynchron)
  {
    m_isParallel = p_asynchron;
  }

  public int getRoundActionPt()
  {
    return m_roundActionPt;
  }

  public void setRoundActionPt(int p_roundActionPt)
  {
    m_roundActionPt = p_roundActionPt;
  }

  /**
   * @return the deploymentTimeStep
   */
  public int getDeploymentTimeStep()
  {
    return m_deploymentTimeStep;
  }

  /**
   * @param p_deploymentTimeStep the deploymentTimeStep to set
   */
  public void setDeploymentTimeStep(int p_deploymentTimeStep)
  {
    m_deploymentTimeStep = p_deploymentTimeStep;
  }
  
  public int getLockGameInMillis()
  {
    return m_lockGameInMillis;
  }

  public void setLockGameInMillis(int p_lockGameInMillis)
  {
    m_lockGameInMillis = p_lockGameInMillis;
  }

  public int getActionPtMaxReserve()
  {
    // TODO remove this (for backward compatibility)
    if( m_actionPtMaxReserve == 0 )
      m_actionPtMaxReserve = 25;
    return m_actionPtMaxReserve;
  }

  public void setActionPtMaxReserve(int p_actionPtMaxReserve)
  {
    m_actionPtMaxReserve = p_actionPtMaxReserve;
  }

  public int getActionPtMaxPerExtraShip()
  {
    // TODO remove this (for backward compatibility)
    if( m_actionPtMaxPerExtraShip == 0 )
      m_actionPtMaxPerExtraShip = 5;
    return m_actionPtMaxPerExtraShip;
  }

  public void setActionPtMaxPerExtraShip(int p_actionPtMaxPerExtraShip)
  {
    m_actionPtMaxPerExtraShip = p_actionPtMaxPerExtraShip;
  }
  

}
