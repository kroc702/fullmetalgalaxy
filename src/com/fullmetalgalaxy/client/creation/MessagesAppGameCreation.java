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
package com.fullmetalgalaxy.client.creation;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesAppGameCreation extends Messages
{
  String gameName();

  String createGame();

  String cancel();

  String timeStepDuration();

  String actionPtPerTimeStep();

  String actionPtPerExtraShip();

  String tideFrequencyInStep();

  String tideFrequencyInSec();

  String startingDate();

  String endingDate();

  String registrationEndDate();

  String simpleCreation();

  String hardWay();

  String map();

  String gameCreation();

  String gameCreationSuccess();

  String errorEndDate();

  String errorActionPt();

  String errorName();

  String errorMapTooLarge(int p_maxWidth, int p_maxHeight);
}