/**
 * 
 */
package com.fullmetalgalaxy.model;

/**
 * @author Kroc
 *
 */
public enum ActionType
{
  /** no action */
  None,
  /** it's not a real action, just a first step during action construction */
  Selected,
  /** games actions */
  Move, Landing, Fire, Control, TakeOff, Unload, EndTurn, RepairTurret,
  /** game admin action */
  Pause, Play;

  public boolean isAdminAction()
  {
    switch( this )
    {
    case Pause:
    case Play:
      return true;
    default:
      return false;
    }
  }

  public boolean isGameAction()
  {
    switch( this )
    {
    case Move:
    case Landing:
    case Fire:
    case Control:
    case TakeOff:
    case Unload:
    case EndTurn:
    case RepairTurret:
      return true;
    default:
      return false;
    }
  }



}
