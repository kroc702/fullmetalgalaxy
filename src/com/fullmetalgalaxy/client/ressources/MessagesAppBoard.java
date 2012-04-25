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
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesAppBoard extends Messages
{
  // error
  String wrongGameVersion();
  String unknownError();
  String unknownGame();
  
  // dlg join
  String pleaseCheckGamePasword();
  
  String pasword();
  
  String joinTitle();
  
  String joinWarning();
  
  // player tip on actions
  String yourTurnToPlay();
  String selectDestroyerTarget();
  String selectTwoDestroyers();
  String confirmEndTurn(int p_remainingPA);
  String confirmEndTurnRoundedPA(int p_remainingPA, int p_roundedTo);
  String confirmTakeOff(String p_freighter);         
  String joinThisGame();
  String waitGameStarting();
  String mustLandFreighter();
  String mustDeployUnits();
  String pauseGameAllowNewPlayer();
  String mustEndYourTurn();
  String SelectFreighterToTakeOff();
  String unconnected();
  String trainningMode();
  String clicToLeave();
  String activateTrainningMode();
  String deactivateTrainningMode();
  // icons tooltip
  String validAction();
  String cancelAction();
  String repairTurret();
  String takeOff();
  String fire();
  String control();
  String displayFireCover();
  String hideFireCover();
  String endTurn();
  String tacticalZoom();
  String strategyZoom();
  String displayGrid();
  String hideGrid();
  String joinGame();
  String costInPA();
  
  String contain();

  String construct();

  String bullet();

  String landing();

  String deployment(String p_token);

  String inOrbit();

  String xPlayers(int p_nbPlayer, int p_nbMaxPlayer);

  String playerDescription(String p_login, int p_points);

  String nextActionPt(int p_actionPtIncrement, String p_date);

  String tide();

  String nextTide();

  String currentTide();

  String noForecast();

  String gameCreation(String p_date);

  String gameFinishAt(String p_date);

  String oreInHold();

  String remainingActionPoint();

  String nextPA(int p_paIncrement, String p_remainingTime);
  
  String reportAnIssueToAdmin();
  
  String confirmCancelGame();

  String noMessages();
  
  String ok();
  
  String cancel();
  
  // unused ?
  String gameFinished();

  String turn();

}
