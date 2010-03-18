/**
 * 
 */
package com.fullmetalgalaxy.model.persist;

import java.util.Date;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.constant.FmpConstant;




/**
 * @author Kroc
 * Account data that other people are allowed to see.
 */
public class EbAccount extends EbBase
{
  private static final long serialVersionUID = -6721026137413400063L;

  // theses data come from database (Account table)
  // -------------------------------------------
  private String m_login = "";
  private String m_email = "";
  private String m_pseudo = "";
  private Date m_subscriptionDate = new Date();
  private AuthProvider m_authProvider = AuthProvider.Fmg;
  private String m_description = "";
  private boolean m_allowPrivateMsg = true;
  /** to allow message like 'it your turn on game xxx' */
  private boolean m_allowMailFromGame = true;
  private boolean m_allowMailFromNewsLetter = true;

  public EbAccount()
  {
    super();
    init();
  }


  private void init()
  {
    m_login = "";
    m_email = "";
    m_pseudo = "";
    m_subscriptionDate = new Date();
    m_authProvider = AuthProvider.Fmg;
    m_description = "";
    m_allowPrivateMsg = true;
    m_allowMailFromGame = true;
    m_allowMailFromNewsLetter = true;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  public boolean isEmpty()
  {
    return getId() == 0;
  }

  public String getAvatarUrl()
  {
      return FmpConstant.getBaseUrl() + "/images/avatar-default.jpg";
  }

  public boolean canChangePseudo()
  {
    if( (getAuthProvider() == AuthProvider.Google)
        && (computePseudoFromMail( getLogin() ).equals( getPseudo() )) )
    {
      return true;
    }
    return false;
  }

  protected String computePseudoFromMail(String p_mail)
  {
    String pseudo = "";
    if( p_mail != null && !p_mail.isEmpty() )
    {
      pseudo += p_mail;
      int lastindex = pseudo.lastIndexOf( '@' );
      if( lastindex > 1 )
      {
        pseudo = pseudo.substring( 0, lastindex );
      }
    }
    return pseudo;
  }
  
  public String getAuthIconHtml()
  {
    if(getAuthProvider() == AuthProvider.Fmg)
    {
      return("<img style=\"border=none\" src=\"/favicon.ico\" alt=\"FMG\" />");
    } else {
      return("<img style=\"border=none\" src=\"http://www.google.com/favicon.ico\" alt=\"Google\" />");
    }
    
  }

  // getters / setters
  // -----------------
  /**
   * @return the login
   */
  public String getLogin()
  {
    return m_login;
  }

  /**
   * @param p_login the login to set
   */
  public void setLogin(String p_login)
  {
    assert p_login != null;
    if( m_pseudo == null || m_pseudo.isEmpty() )
    {
      m_pseudo = computePseudoFromMail( p_login );
    }
    if( (m_email == null || m_email.isEmpty()) && p_login.contains( "@" ) )
    {
      m_email = p_login;
    }
    m_login = p_login;
  }

  /**
   * @return the email
   */
  public String getEmail()
  {
    return m_email;
  }

  /**
   * @param p_email the email to set
   */
  public void setEmail(String p_email)
  {
    m_email = p_email;
  }


  /**
   * @return the pseudo
   */
  public String getPseudo()
  {
    return m_pseudo;
  }


  /**
   * @param p_pseudo the pseudo to set
   */
  public void setPseudo(String p_pseudo)
  {
    m_pseudo = p_pseudo;
  }


  /**
   * @return the subscriptionDate
   */
  public Date getSubscriptionDate()
  {
    return m_subscriptionDate;
  }


  /**
   * @param p_subscriptionDate the subscriptionDate to set
   */
  public void setSubscriptionDate(Date p_subscriptionDate)
  {
    m_subscriptionDate = p_subscriptionDate;
  }


  /**
   * @return the authProvider
   */
  public AuthProvider getAuthProvider()
  {
    return m_authProvider;
  }


  /**
   * @param p_authProvider the authProvider to set
   */
  public void setAuthProvider(AuthProvider p_authProvider)
  {
    m_authProvider = p_authProvider;
  }


  /**
   * @return the description
   */
  public String getDescription()
  {
    return m_description;
  }


  /**
   * @param p_description the description to set
   */
  public void setDescription(String p_description)
  {
    m_description = p_description;
  }


  /**
   * @return the allowPrivateMsg
   */
  public boolean isAllowPrivateMsg()
  {
    return m_allowPrivateMsg;
  }


  /**
   * @param p_allowPrivateMsg the allowPrivateMsg to set
   */
  public void setAllowPrivateMsg(boolean p_allowPrivateMsg)
  {
    m_allowPrivateMsg = p_allowPrivateMsg;
  }


  /**
   * @return the allowMailFromGame
   */
  public boolean isAllowMailFromGame()
  {
    return m_allowMailFromGame;
  }


  /**
   * @param p_allowMailFromGame the allowMailFromGame to set
   */
  public void setAllowMailFromGame(boolean p_allowMailFromGame)
  {
    m_allowMailFromGame = p_allowMailFromGame;
  }


  /**
   * @return the allowMailFromNewsLetter
   */
  public boolean isAllowMailFromNewsLetter()
  {
    return m_allowMailFromNewsLetter;
  }


  /**
   * @param p_allowMailFromNewsLetter the allowMailFromNewsLetter to set
   */
  public void setAllowMailFromNewsLetter(boolean p_allowMailFromNewsLetter)
  {
    m_allowMailFromNewsLetter = p_allowMailFromNewsLetter;
  }



}
