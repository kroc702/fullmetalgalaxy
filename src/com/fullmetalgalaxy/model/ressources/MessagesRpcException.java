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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.ressources;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesRpcException extends Messages
{
  String mustHaveTwoPlayerToStart();
  String tooManyConstruction();
  String mustWaitToDeploy(int p_turn);
  String cantDeployTooFar(int p_HexagonCount);
  String cantDeployPotoonInSea();
  String cantDestroyYourUnits();
  String cantFireOn(String p_destroyer, String p_target);
  String noMoreAmo(String p_destroyer);
  String gameNotStarted();
  String mustLandFreighter();
  String cantEndTurnTwoTankMontain();
  String cantRepairTurretFireCover();

  String cantRepairTurretTwice();
  String cantEnterLeaveFireCover();
  String cantMoveAndConvert();
  String boardLocked();
  
  String cantBanPlayerNoFreighter();

  String MustBeLogged();
  
  String GameFinished(String p_date);

  String CantMoveDontControl(String p_tokenColor, String p_controlColor);

  String CantMoveOn(String p_token, String p_land);

  String CantUnloadDontControl(String p_tokenColor, String p_controlColor);

  String CantUnloadDisableTide(String p_token);

  String CantUnloadDisableFire(String p_token, String p_fireCover);

  String NotEnouthActionPt();

  String CantMoveDisableFire(String p_token, String p_color);

  String CantLoad(String p_tokenCarrier, String p_token);

  String MustControlBothToken(String p_tokenCarrier, String p_token);

  String CantFireOn(String p_token, String p_tokenTarget);

  String CantFireDisableTide(String p_token);

  String CantFireDisableFire(String p_token, String p_fireCover);

  String CantMoveAlone(String p_token);

  String CantLandOn(String p_land);

  String CantLandCloser(int p_hexagonCount);

  String CantLandTooCloseBorder();

  String NotYourTurn();

  String CantDestroyFreighter();

  String OnlyDestroyerCanControl();

}
