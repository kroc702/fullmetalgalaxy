/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbAccountStats;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;




/**
 * @author Kroc
 * Account data that other people are allowed to see.
 */
public class EbAccount extends EbPublicAccount
{
  private static final long serialVersionUID = -6721026137413400063L;

  // theses data come from database (Account table)
  // -------------------------------------------
  /**
   * For this version of pseudo we removed all non alphabetical char.
   * and transform to lower case.
   * This is used to check similar pseudo.
   */
  private String m_compactPseudo = "";
  private String m_login = "";
  private String m_email = "";
  private Date m_subscriptionDate = new Date();
  private Date m_lastConnexion = null;
  @Unindexed
  private AuthProvider m_authProvider = AuthProvider.Fmg;
  @Unindexed
  private String m_description = "";
  @Unindexed
  private boolean m_allowPrivateMsg = true;
  /** to allow message like 'it your turn on game xxx' */
  @Unindexed
  private boolean m_allowMailFromGame = true;
  private boolean m_allowMailFromNewsLetter = true;
  private String m_jabberId = null;

  @Unindexed
  private String m_locale = "";
  
  /**
   * A VIP already finished one game and wasn't banned for a while.
   */
  private boolean m_isVip = false;
  
  
  /** because of this data, EbAccount shoudln't be send on client side ! */
  private String m_password = null;

  /**
   * maximum level reach by this account
   */
  private int m_maxLevel = 1;
  @Serialized
  private List<EbAccountStats> m_stats = new ArrayList<EbAccountStats>();
  
  /**
   * link with forum identity
   */
  private String m_forumId = null;
  /**
   * True if we are sure that forum id correspond to the same user.
   * It can be set even if m_forumId isn't filled to allow FMG to automatically link with forum
   */
  @Unindexed
  private boolean m_isforumIdConfirmed = false;
  @Unindexed
  private String m_forumAvatarUrl = null;
  
  private static Pattern s_pattern = Pattern.compile( "^((?:[\\w]+\\p{Graph}?)+\\w){3,32}$" );
  
  /**
   * check if p_pseudo can be used as a valid username.
   * It is very permissive, but disallow two special char
   * and special char at extremity
   * @param p_pseudo
   * @return
   */
  public static boolean isValidPseudo(String p_pseudo)
  {
    Matcher matcher = s_pattern.matcher( p_pseudo );
    return matcher.matches();
  }

  /**
   * TODO remove this server package dependency. We can move EbAccount to server !
   * compute a compacted pseudo
   * @param p_pseudo
   * @return
   */
  public static String compactPseudo(String p_pseudo)
  {
    // remove accentuated char
    String compact = ServerUtil.convertNonAscii( p_pseudo );
    compact = compact.toLowerCase();
    // remove all non word char
    compact = compact.replaceAll( "[^\\w]", "" );
    return compact;
  }
  
  
  public EbAccount()
  {
    super();
    init();
  }


  private void init()
  {
    m_login = "";
    m_email = "";
    m_subscriptionDate = new Date( System.currentTimeMillis() );
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

  @Override
  public String getProfileUrl()
  {
    if( getForumId() == null )
    {
      return super.getProfileUrl();
    }
    return "http://" + FmpConstant.getForumHost() + "/u" + getForumId();
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
      return("<img style=\"border=none\" src=\"/images/icon_google.cache.ico\" alt=\"Google\" />");
    }
    
  }

  public boolean haveEmail()
  {
    return getEmail() != null && !getEmail().trim().isEmpty() && getEmail().contains( "@" );
  }

  @Override
  public void setCurrentLevel(int p_currentLevel)
  {
    super.setCurrentLevel( p_currentLevel );
    if( getCurrentLevel() > getMaxLevel() )
    {
      setMaxLevel( getCurrentLevel() );
    }
  }
  


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbPublicAccount#setPseudo(java.lang.String)
   */
  @Override
  public void setPseudo(String p_pseudo)
  {
    super.setPseudo( p_pseudo );
    m_compactPseudo = compactPseudo( p_pseudo );
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
    m_login = p_login;
    if( getPseudo() == null || getPseudo().isEmpty() )
    {
      setPseudo( computePseudoFromMail( p_login ) );
    }
    if( (m_email == null || m_email.isEmpty()) && p_login.contains( "@" ) )
    {
      m_email = p_login;
    }
    if( (getJabberId() == null || getJabberId().isEmpty())
        && getAuthProvider() == AuthProvider.Google )
    {
      setJabberId( getLogin() );
    }
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


  public String getJabberId()
  {
    return m_jabberId;
  }


  public void setJabberId(String p_jabberId)
  {
    m_jabberId = p_jabberId;
  }


  public int getMaxLevel()
  {
    return m_maxLevel;
  }


  public void setMaxLevel(int p_maxLevel)
  {
    m_maxLevel = p_maxLevel;
  }


  public List<EbAccountStats> getStats()
  {
    return m_stats;
  }


  public void setStats(List<EbAccountStats> p_stats)
  {
    m_stats = p_stats;
  }


  public String getPassword()
  {
    return m_password;
  }


  public void setPassword(String p_password)
  {
    m_password = p_password;
  }


  public boolean isVip()
  {
    return m_isVip;
  }


  public void setVip(boolean p_isVip)
  {
    m_isVip = p_isVip;
  }

  /**
   * @return the lastConnexion
   */
  public Date getLastConnexion()
  {
    return m_lastConnexion;
  }

  public void setLastConnexion(Date p_date)
  {
    m_lastConnexion = p_date;
  }

  /**
   * @return the forumId
   */
  public String getForumId()
  {
    return m_forumId;
  }

  /**
   * @param p_forumId the forumId to set
   */
  public void setForumId(String p_forumId)
  {
    m_forumId = p_forumId;
  }

  /**
   * @return the forumAvatarUrl
   */
  public String getForumAvatarUrl()
  {
    return m_forumAvatarUrl;
  }

  /**
   * @param p_forumAvatarUrl the forumAvatarUrl to set
   */
  public void setForumAvatarUrl(String p_forumAvatarUrl)
  {
    m_forumAvatarUrl = p_forumAvatarUrl;
  }

  /**
   * @return the isforumIdConfirmed
   */
  public boolean isIsforumIdConfirmed()
  {
    return m_isforumIdConfirmed;
  }

  /**
   * @param p_isforumIdConfirmed the isforumIdConfirmed to set
   */
  public void setIsforumIdConfirmed(boolean p_isforumIdConfirmed)
  {
    m_isforumIdConfirmed = p_isforumIdConfirmed;
  }

  public String getLocale()
  {
    return m_locale;
  }

  public void setLocale(String p_locale)
  {
    m_locale = p_locale;
  }

  public String getCompactPseudo()
  {
    return m_compactPseudo;
  }



}
