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
