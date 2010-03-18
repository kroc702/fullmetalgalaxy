package com.fullmetalgalaxy.model.persist.triggers.conditions;

public enum ConditionClass
{
  PuzzleLoad, TokenColor, TokenZone, TokenTypeZone, PlayerColor;

  public AnCondition newCondition()
  {
    switch( this )
    {
    case PuzzleLoad:
      return new EbCndPuzzleLoad();
    case TokenColor:
      return new EbCndTokenColor();
    case TokenZone:
      return new EbCndTokenZone();
    case TokenTypeZone:
      return new EbCndTokenTypeZone();
    case PlayerColor:
      return new EbCndPlayerColor();
    }
    return new AnCondition();
  }
}
