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
package com.fullmetalgalaxy.client.board;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesAppBoard extends Messages
{
  String selectedToken();

  String contain();

  String construct();

  String landing();

  String deployment(String p_token);

  String moving();

  String inOrbit();

  String fireOrControl();

  String xPlayers(int p_nbPlayer);

  String playerDescription(String p_login, int p_points);

  String notJoined();

  String actionPtCount(int p_actionPtCount);

  String nextActionPt(int p_actionPtIncrement, String p_date);

  String tide();

  String nextTide();

  String noForecast();

  String gameCreation(String p_date);

  String gameFinishAt(String p_date);

  String gameFinished();

  String turn();

  String youSureDestroyYourToken();

  String small();

  String medium();

  String large();

  String zoom();

  String action();

  String join();

  String endTurn();

  String fireCover();

  String home();
}
