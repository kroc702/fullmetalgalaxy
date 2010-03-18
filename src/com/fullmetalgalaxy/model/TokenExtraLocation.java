/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.io.Serializable;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Kroc
 *
 */
public class TokenExtraLocation implements Serializable
{
  static final long serialVersionUID = 16;

  private AnBoardPosition m_position = new AnBoardPosition();
  private EbToken m_token = null;


  /**
   * @return the position
   */
  public AnBoardPosition getPosition()
  {
    return m_position;
  }

  /**
   * @param p_position the position to set
   */
  public void setPosition(AnBoardPosition p_position)
  {
    m_position = p_position;
  }

  /**
   * @return the token
   */
  public EbToken getToken()
  {
    return m_token;
  }

  /**
   * @param p_token the token to set
   */
  public void setToken(EbToken p_token)
  {

    m_token = p_token;
  }

}
