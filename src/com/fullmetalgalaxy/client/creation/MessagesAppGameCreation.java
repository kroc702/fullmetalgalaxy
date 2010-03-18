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
