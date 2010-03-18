package com.fullmetalgalaxy.model.persist.triggers.actions;


public enum ActionClass
{
  Message, ChangeTide, TakeOff;

  public AnAction newAction()
  {
    switch( this )
    {
    case Message:
      return new EbActMessage();
    case ChangeTide:
      return new EbActChangeTide();
    case TakeOff:
      return new EbActTakeOff();
    }
    return new AnAction();
  }
}
