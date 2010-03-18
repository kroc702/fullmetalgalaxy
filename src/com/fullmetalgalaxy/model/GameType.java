/**
 * 
 */
package com.fullmetalgalaxy.model;

/**
 * @author Vincent Legendre
 *
 */
public enum GameType
{
  // Standard game
  MultiPlayer,
  // Game is loaded once and never modified on server. player is alone.
  // in Puzzle game, m_currentPlayerRegistration indicate which registration
  // player have to play.
  Puzzle,
  // Scenario can't be played at all. it's only a game template.
  Scenario;
}
