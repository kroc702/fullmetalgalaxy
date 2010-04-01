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
package com.fullmetalgalaxy.server;

import com.fullmetalgalaxy.model.DbbPlayerPointsHistory;
import com.fullmetalgalaxy.model.persist.EbRegistration;

/**
 * @author Vincent Legendre
 * contain all needed informations to compute the new account points
 * TODO erosion
 */
public class AccountPointInformation
{
  public AccountPointInformation()
  {
  }

  public EbRegistration gameRegistration = null;
  public int otherPlayerPoints = 0;
  public int nbPlayer = 1;
  public boolean won = false;
  public int winingPoint = 0;

  public DbbPlayerPointsHistory getNewPlayerPointsHistory()
  {
    if( (gameRegistration == null) )
    {
      return null;
    }
    DbbPlayerPointsHistory playerPoints = new DbbPlayerPointsHistory();

    int currentPoint = 0;
    int pointInc = winingPoint - 20;
    if( pointInc < 0 )
    {
      pointInc *= (currentPoint) / 100 + Math.sqrt( currentPoint ) / 6;
    }
    if( won == true )
    {
      pointInc += 4 * (nbPlayer - 1) + (otherPlayerPoints) / 17 + Math.sqrt( otherPlayerPoints );
    }
    playerPoints.setIdAccount( gameRegistration.getAccountId() );
    playerPoints.setIdGame( gameRegistration.getIdGame() );
    playerPoints.setLastUpdate( ServerUtil.currentDate() );
    playerPoints.setCurrentPoints( currentPoint + pointInc );

    return playerPoints;
  }

}
