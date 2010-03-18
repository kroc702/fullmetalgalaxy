/**
 * 
 */
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesRpcException extends Messages
{
  String LogonWrongPassword();

  String UnknownGameId(long p_gameId);

  String MustBeLogged();

  String NoGameId();

  String UnknownAccount();

  String MaximumPlayerReached();

  String YouDidntJoinThisGame();

  String GameNotStarted();

  String GameFinished(String p_date);

  String CantMoveDontControl(String p_tokenColor, String p_controlColor);

  String CantMoveOn(String p_token, String p_land);

  String CantUnloadDontControl(String p_tokenColor, String p_controlColor);

  String CantUnloadDisableTide(String p_token);

  String CantUnloadDisableFire(String p_token, String p_fireCover);

  String MustTwoPositionToUnloadBarge();

  String NotEnouthActionPt();

  String CantMoveDisableFire(String p_token, String p_color);

  String CantLoad(String p_tokenCarrier, String p_token);

  String MustControlBothToken(String p_tokenCarrier, String p_token);

  String MustDestroyAllTurrets();

  String CantFireOn(String p_token, String p_tokenTarget);

  String CantFireDisableTide(String p_token);

  String CantFireDisableFire(String p_token, String p_fireCover);

  String CantMoveAlone(String p_token);

  String CantLandOn(String p_land);

  String CantLandCloser(int p_hexagonCount);

  String NotYourTurn();

  String TokenWasAlreadyMoved(String p_token);

  String CantDestroyFreighter();

  String TwoStepAreNotNeighbour();

  String LoginAlreadyExist();
}
