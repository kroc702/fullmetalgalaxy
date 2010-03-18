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
