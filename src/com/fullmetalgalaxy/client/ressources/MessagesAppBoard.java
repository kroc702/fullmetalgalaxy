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
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesAppBoard extends Messages
{
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

  // unused ?
  String gameFinished();

  String turn();

}
