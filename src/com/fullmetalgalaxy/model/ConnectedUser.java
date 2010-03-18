/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author vincent
 *
 */
public class ConnectedUser implements IsSerializable, java.io.Serializable
{
  static final long serialVersionUID = 203;

  private String m_pseudo = "";
  private Date m_lastConnexion = new Date();
  private Date m_endTurnDate = null;

  public ConnectedUser()
  {
  }

  public ConnectedUser(String p_pseudo, Date p_endTurn)
  {
    m_pseudo = p_pseudo;
    m_endTurnDate = p_endTurn;
  }

  public String getPseudo()
  {
    return m_pseudo;
  }

  public void setPseudo(String p_pseudo)
  {
    m_pseudo = p_pseudo;
  }

  public Date getEndTurnDate()
  {
    return m_endTurnDate;
  }

  public void setEndTurnDate(Date p_endTurnDate)
  {
    m_endTurnDate = p_endTurnDate;
  }

  public Date getLastConnexion()
  {
    return m_lastConnexion;
  }

  public void setLastConnexion(Date p_lastConnexion)
  {
    m_lastConnexion = p_lastConnexion;
  }


}
