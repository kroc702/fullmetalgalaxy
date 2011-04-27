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

import java.util.Date;


/**
 * @author Vincent Legendre
 *
 */
public class EbAccountStats extends EbBase
{
  static final long serialVersionUID = 1;

  
  /** Statistic last update:<br/>
   * - if status is Running or CreatorOnly, game creation date<br/>
   * - if status is Finished or Cancel, game end date<br/>
   * - if status is Banned, player ban date<br/>
   *  */
  private Date m_lastUpdate = new Date();
  
  /** game final score, or erosion value
   * may differ from oreCount + tokenCount as ore may have different value.
   * More important, this score depend of other players level !
   *
   * finalScore = (fmpScore - 20)*(sum(otherLevel)/(myLevel*otherPlayerCount))^sign(fmpScore) 
   *
   * for winner, we also add the sum of other players bonus
   * */
  private int m_finalScore = 0;


  public EbAccountStats()
  {
    super();
    init();
  }

  public EbAccountStats(EbBase p_base)
  {
    super( p_base );
    init();
  }


  private void init()
  {
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }


  // getters / setters
  // -----------------

  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }


  public int getFinalScore()
  {
    return m_finalScore;
  }

  public void setFinalScore(int p_finalScore)
  {
    m_finalScore = p_finalScore;
  }



}
