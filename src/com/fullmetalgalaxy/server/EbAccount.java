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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Embedded;
import javax.persistence.PrePersist;

import jskills.IPlayer;
import jskills.Rating;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.persist.AccountStatistics;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;




/**
 * @author Kroc
 * Account data that other people are allowed to see.
 */
public class EbAccount extends EbPublicAccount implements IPlayer
{
  private static final long serialVersionUID = -6721026137413400063L;


  public enum NotificationQty
  {
    No, Min, Std, Max;
  }

  @PrePersist
  void onPersist()
  {
  }


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
  /** to allow message from other players */
  @Unindexed
  private boolean m_isAllowMsgFromPlayer = true;
  /** to allow message like 'it your turn on game xxx' */
  @Unindexed
  private NotificationQty m_notificationQty = NotificationQty.Std;

  private String m_jabberId = "";

  @Unindexed
  private LocaleFmg m_locale = LocaleFmg.getDefault();
  
  /** because of this data, EbAccount shoudln't be send on client side ! 
   * We may have to encrypt this data.
   * */
  private String m_password = null;

  /**
   * last time user ask for his password.
   */
  private Date m_lastPasswordAsk = null;

  /**
   * it computed from score given by other users
   */
  @Unindexed
  private int m_fairplay = 0;
  

  // data related to statistics
  // ==========================
  /** True skill mean level  */
  @Unindexed
  private double m_trueSkillMean = ServerUtil.getGameInfo().getInitialMean();
  /** True skill standard deviation level */
  @Unindexed
  private double m_trueSkillSD = ServerUtil.getGameInfo().getInitialStandardDeviation();
  /* current true skill conservative level. here to make request and sorting on it. */
  @AlsoLoad("m_currentLevel")
  private double m_trueSkillLevel = 0;
  /** account game statistic for a limited time period */
  @Embedded
  private AccountStatistics m_currentStats = null;
  /** account game statistic since the beginning */
  @Embedded
  private AccountStatistics m_fullStats = null;
  
  
  // data related to forum
  // =====================
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
  /**
   * To make a link between an FMG account and his corresponding forum account,
   * we send a key by private message. If user give us back this key (through an url)
   * then both account are the same people.
   * If m_forumKey is null, we never send it. Otherwise, PM was sended don't resent it.
   */
  private String m_forumKey = null;
  /**
   * these data is used by forum connector to avoid override some information while pushing data
   */
  @Serialized
  private Object m_forumConnectorData = null;
  
  private static Pattern s_pattern = Pattern.compile( "^(((\\w)+(\\p{Graph})?)+\\w)$" );
  

  
  void importLocale(@AlsoLoad("m_locale") String p_localeStr)
  {
    m_locale = LocaleFmg.fromString( p_localeStr );
  }

  /**
   * check if p_pseudo can be used as a valid username.
   * It is very permissive, but disallow two special char
   * and special char at extremity
   * @param p_pseudo
   * @return
   */
  public static boolean isValidPseudo(String p_pseudo)
  {
    if( p_pseudo == null || p_pseudo.length() < 4 || p_pseudo.length() > 25 )
    {
      return false;
    }
    Matcher matcher = s_pattern.matcher( p_pseudo );
    return matcher.matches();
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
    m_password = null;
    m_subscriptionDate = new Date( System.currentTimeMillis() );
    m_lastConnexion = new Date( System.currentTimeMillis() );
    m_authProvider = AuthProvider.Fmg;
    m_isAllowMsgFromPlayer = true;
    m_notificationQty = NotificationQty.Std;
    clearComputedStats();
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  /** TODO remove this */
  @Deprecated
  public void loadOldAllowMsgFromGame(@AlsoLoad("m_allowMsgFromGame") String p_allowMsgFromGame)
  {
    if( p_allowMsgFromGame != null )
    {
      m_notificationQty = NotificationQty.Std;
      if( p_allowMsgFromGame.equalsIgnoreCase( "No" ) )
      {
        m_notificationQty = NotificationQty.No;
      }
    }
  }

  /** TODO remove this */
  @Deprecated
  public void loadOldAllowMsgFromPlayer(
      @AlsoLoad("m_allowMsgFromPlayer") String p_allowMsgFromPlayer)
  {
    if( p_allowMsgFromPlayer != null )
    {
      m_isAllowMsgFromPlayer = true;
      if( p_allowMsgFromPlayer.equalsIgnoreCase( "No" ) )
      {
        m_isAllowMsgFromPlayer = false;
      }
    }
  }

  /**
   * reset computed statistic from m_stats list to it's initial values
   */
  public void clearComputedStats()
  {
    resetTrueSkill();
    m_currentStats = null;
    m_fullStats = null;
  }

  @Override
  public String getAvatarUrl()
  {
    if( getForumAvatarUrl() != null )
    {
      return getForumAvatarUrl();
    }
    int avatarId = 0;
    for( int i = 0; i < getPseudo().length(); i++ )
    {
      avatarId += getPseudo().charAt( i );
    }
    avatarId %= 12;
    return "/images/avatar/avatar" + avatarId + ".jpg";
  }


  /**
   * 
   * @return account level from 0 to 9
   */
  private int getNormalizedLevel()
  {
    int normalizedLevel = 0;
    double maxLevel = GlobalVars.getMaxLevel() - 0.1;
    if( maxLevel > 1 && getCurrentLevel() > 1 )
    {
      // from 0 to 8
      normalizedLevel = (int)((getCurrentLevel() * 8) / maxLevel);
      normalizedLevel++;
    }
    if( normalizedLevel < 0 )
      normalizedLevel = 0;
    if( normalizedLevel > 8 || getCurrentLevel() > maxLevel )
      normalizedLevel = 9;
    return normalizedLevel;
  }

  /**
   * icon url to illustrate user level
   * @return
   */
  public String getLevelUrl()
  {
    return "/images/icons/level" + getNormalizedLevel() + ".png";
  }
  

  /**
   * icon url to illustrate player fiability, level and style
   * @return a static url (no more server processing)
   */
  public String getGradStaticUrl()
  {
    if( getFairplay() < 0 )
    {
      return "http://www.fullmetalgalaxy.com/images/icons/unfair.png";
    }
    if( !getCurrentStats().isIncludedInRanking() )
    {
      // player didn't finished enough game
      return "http://www.fullmetalgalaxy.com/images/clear.cache.gif";
    }
    int level = (int)(getCurrentStats().getAverageNormalizedRank() * 10);
    if( level > 9 )
      level = 9;
    // we need to specify full url, as it is used on forum
    return "http://www.fullmetalgalaxy.com/images/icons/level" + level + ".png";
  }

  public String buildHtmlFragment()
  {
    String newsHtml = "<a href='"+getProfileUrl()+"'><table width='100%'><tr>";
    newsHtml += "<td><img src='" + getAvatarUrl() + "' height='40px' /></td>";
    newsHtml += "<td>" + getPseudo() + "<br/>" + (int)getCurrentStats().getAverageNormalizedRank()
        + "</td>";
    // newsHtml += "<td>" + (int)getCurrentLevel() + " Pts</td>";
    newsHtml += "</tr></table></a>";
    return newsHtml;
  }
  
  public static Date getLastConnexionDate2beActive()
  {
    // 30 days
    return new Date( System.currentTimeMillis() - (1000l * 60 * 60 * 24 * 30) );
  }

  /**
   * account is considered as active if he connect itself in the last 30 days
   * @return true if account is active
   */
  public boolean isActive()
  {
    return getLastConnexion() != null
        && getLastConnexion().getTime() > getLastConnexionDate2beActive().getTime();
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



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbPublicAccount#setPseudo(java.lang.String)
   */
  @Override
  public void setPseudo(String p_pseudo)
  {
    super.setPseudo( p_pseudo );
    m_compactPseudo = ServerUtil.compactTag( p_pseudo );
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
    if( p_authProvider != null )
    {
      m_authProvider = p_authProvider;
    }
  }


  public String getJabberId()
  {
    return m_jabberId;
  }


  public void setJabberId(String p_jabberId)
  {
    m_jabberId = p_jabberId;
  }





  public String getPassword()
  {
    return m_password;
  }


  public void setPassword(String p_password)
  {
    m_password = p_password;
  }


  /**
   * @return the lastConnexion
   */
  public Date getLastConnexion()
  {
    if( m_lastConnexion == null )
    {
      m_lastConnexion = new Date( 0 );
    }
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
    if( p_forumAvatarUrl != null && p_forumAvatarUrl.isEmpty() )
    {
      p_forumAvatarUrl = null;
    }
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

  public LocaleFmg getLocale()
  {
    return m_locale;
  }

  public void setLocale(LocaleFmg p_locale)
  {
    m_locale = p_locale;
  }

  public void setLocale(String p_locale)
  {
    setLocale( LocaleFmg.fromString( p_locale ));
  }


  public Date getLastPasswordAsk()
  {
    return m_lastPasswordAsk;
  }

  public void setLastPasswordAsk(Date p_lastPasswordAsk)
  {
    m_lastPasswordAsk = p_lastPasswordAsk;
  }

  public String getCompactPseudo()
  {
    return m_compactPseudo;
  }

  public String getForumKey()
  {
    return m_forumKey;
  }

  public void setForumKey(String p_forumKey)
  {
    m_forumKey = p_forumKey;
  }

  public Object getForumConnectorData()
  {
    return m_forumConnectorData;
  }

  public void setForumConnectorData(Object p_forumConnectorData)
  {
    m_forumConnectorData = p_forumConnectorData;
  }

  /**
   * @return the allowMsgFromPlayer
   */
  public boolean allowMsgFromPlayer()
  {
    return m_isAllowMsgFromPlayer;
  }

  /**
   * @param p_allowMsgFromPlayer the allowMsgFromPlayer to set
   */
  public void setAllowMsgFromPlayer(boolean p_allowMsgFromPlayer)
  {
    m_isAllowMsgFromPlayer = p_allowMsgFromPlayer;
  }

  /**
   * @return the notificationQty
   */
  public NotificationQty getNotificationQty()
  {
    // for compatibility
    if( m_notificationQty == null )
    {
      m_notificationQty = NotificationQty.Std;
    }
    return m_notificationQty;
  }

  /**
   * @param p_notificationQty the notificationQty to set
   */
  public void setNotificationQty(NotificationQty p_notificationQty)
  {
    m_notificationQty = p_notificationQty;
  }

  public double getTrueSkillMean()
  {
    return m_trueSkillMean;
  }

  public double getTrueSkillSD()
  {
    return m_trueSkillSD;
  }

  /**
   * 
   * @return current true skill conservative level that may be minored in future release
   * for inactive players
   */
  public double getCurrentLevel()
  {
    return m_trueSkillLevel;
  }

  public void resetTrueSkill()
  {
    setTrueSkill( ServerUtil.getGameInfo().getInitialMean(), ServerUtil.getGameInfo()
        .getInitialStandardDeviation() );
  }

  public void setTrueSkill(double p_mean, double p_standardDeviation)
  {
    setTrueSkill( new Rating( p_mean, p_standardDeviation ) );
  }

  public void setTrueSkill(Rating p_rating)
  {
    m_trueSkillMean = p_rating.getMean();
    m_trueSkillSD = p_rating.getStandardDeviation();
    m_trueSkillLevel = p_rating.getConservativeRating();
  }

  public int getFairplay()
  {
    return m_fairplay;
  }

  public void setFairplay(int p_fairplay)
  {
    m_fairplay = p_fairplay;
  }

  public void setCurrentStats(AccountStatistics p_stats)
  {
    m_currentStats = p_stats;
  }

  public AccountStatistics getCurrentStats()
  {
    if( m_currentStats == null )
    {
      m_currentStats = new AccountStatistics();
    }
    return m_currentStats;
  }

  public AccountStatistics getFullStats()
  {
    if( m_fullStats == null )
    {
      m_fullStats = new AccountStatistics();
    }
    return m_fullStats;
  }
}
