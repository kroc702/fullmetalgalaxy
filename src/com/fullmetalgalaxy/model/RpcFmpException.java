/**
 * 
 */
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Kroc
 *
 */
public class RpcFmpException extends Exception implements IsSerializable
{
  static final long serialVersionUID = 0;

  // all possible error code
  public static final int LogonWrongPassword = 1;
  public static final int UnknownGameId = 2;
  public static final int MustBeLogged = 3;
  public static final int NoGameId = 4;
  public static final int UnknownAccount = 5;
  public static final int MaximumPlayerReached = 6;
  public static final int YouDidntJoinThisGame = 7;
  public static final int GameNotStarted = 8;
  public static final int GameFinished = 9;
  public static final int CantMoveDontControl = 10;
  public static final int CantMoveOn = 11;
  public static final int CantUnloadDontControl = 12;
  public static final int CantUnloadDisableTide = 13;
  public static final int CantUnloadDisableFire = 14;
  public static final int MustTwoPositionToUnloadBarge = 15;
  public static final int NotEnouthActionPt = 16;
  public static final int CantMoveDisableFire = 17;
  public static final int CantLoad = 18;
  public static final int MustControlBothToken = 19;
  public static final int MustDestroyAllTurrets = 20;
  public static final int CantFireOn = 21;
  public static final int CantFireDisableTide = 22;
  public static final int CantFireDisableFire = 23;
  public static final int CantMoveAlone = 24;
  public static final int CantLandOn = 25;
  public static final int CantLandCloser = 26;
  public static final int NotYourTurn = 27;
  public static final int TokenWasAlreadyMoved = 28;
  public static final int CantDestroyFreighter = 29;
  public static final int TwoStepAreNotNeighbour = 30;
  public static final int LoginAlreadyExist = 31;


  public int m_errorCode = 0;

  private long m_enumParam[] = new long[3];

  private String m_message;

  public RpcFmpException()
  {
    super();
  }

  public long getLong(int p_paramIndex)
  {
    return m_enumParam[p_paramIndex];
  }

  public int getInt(int p_paramIndex)
  {
    return (int)m_enumParam[p_paramIndex];
  }

  public RpcFmpException(int p_errorCode)
  {
    super();
    m_errorCode = p_errorCode;
  }

  public RpcFmpException(int p_errorCode, long p_param0)
  {
    super();
    m_errorCode = p_errorCode;
    m_enumParam[0] = p_param0;
  }

  public RpcFmpException(int p_errorCode, long p_param0, long p_param1)
  {
    super();
    m_errorCode = p_errorCode;
    m_enumParam[0] = p_param0;
    m_enumParam[1] = p_param1;
  }

  public RpcFmpException(int p_errorCode, long p_param0, long p_param1, long p_param2)
  {
    super();
    m_errorCode = p_errorCode;
    m_enumParam[0] = p_param0;
    m_enumParam[1] = p_param1;
    m_enumParam[2] = p_param2;
  }



  public RpcFmpException(String p_message)
  {
    super( p_message );
    m_message = p_message;
  }

  @Override
  public String getMessage()
  {
    return m_message;
  }

  @Override
  public String toString()
  {
    return getMessage();
  }
}
