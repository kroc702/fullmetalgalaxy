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

import javax.persistence.PrePersist;

import jskills.IPlayer;
import jskills.Rating;

import com.fullmetalgalaxy.model.AuthProvider;
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
    Min, Std, Max;
  }

  @PrePersist
  void onPersist()
  {
    // this code is only here for debug purpose
    if( m_styleRatio == 0 )
    {
      System.err.println( "arg m_styleRatio == 0 !!!!!" );
      new Throwable().printStackTrace( System.err );
    }
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
  /** to allow message like 'it your turn on game xxx' */
  @Unindexed
  private boolean m_isAllowMsgFromGame = true;
  /** to allow message from other players */
  @Unindexed
  private boolean m_isAllowMsgFromPlayer = true;
  @Unindexed
  private NotificationQty m_notificationQty = NotificationQty.Std;

  private String m_jabberId = null;

  @Unindexed
  private String m_locale = "";
  
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
  
  /**
   * a list of all fairplay comment
   */
  @Serialized
  private List<FairPlayNote> m_fairplayComments = new ArrayList<FairPlayNote>();

  /**
   * player can give 'm_remainingScoring' score on other players.
   * note that they can score the same player only once.
   */
  @Unindexed
  private int m_remainingScoring = 0;

  /**
   * represent this account activity.
   * -> finished game count in the last 20 months + finished game in the last 8 months
   */
  private int m_activityLevel = 0;


  // the following data are computed from m_stats list
  // =================================================
  /**
   * player style to recognize people
   */
  private float m_styleRatio = 1;
  
  /**
   * easy: it's the sum of all his scores. We can call this: total profit.
   */
  private int m_totalScoreSum = 0;

  /**
   * the sum of player on all his finished game
   */
  @Unindexed
  private int m_totalPlayerSum = 0;

  private int m_finshedGameCount = 0;

  private int m_victoryCount = 0;
  
  /** True skill mean level  */
  @Unindexed
  private double m_trueSkillMean = ServerUtil.getGameInfo().getInitialMean();
  /** True skill standard deviation level */
  @Unindexed
  private double m_trueSkillSD = ServerUtil.getGameInfo().getInitialStandardDeviation();
  /** current true skill conservative level. here to make request and sorting on it. */
  private double m_currentLevel = 0;


  
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

  /**
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
    m_password = null;
    m_subscriptionDate = new Date( System.currentTimeMillis() );
    m_lastConnexion = new Date( System.currentTimeMillis() );
    m_authProvider = AuthProvider.Fmg;
    m_isAllowMsgFromGame = true;
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
      m_isAllowMsgFromGame = true;
      if( p_allowMsgFromGame.equalsIgnoreCase( "No" ) )
      {
        m_isAllowMsgFromGame = false;
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
    m_styleRatio = 1;
    m_activityLevel = 0;
    m_totalScoreSum = 0;
    m_totalPlayerSum = 0;
    m_finshedGameCount = 0;
    m_victoryCount = 0;
    resetTrueSkill();
    m_currentLevel = 0;
  }

  @Override
  public String getAvatarUrl()
  {
    if( getForumAvatarUrl() != null )
    {
      return getForumAvatarUrl();
    }
    return "/images/avatar-default.jpg";
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
   * @return
   */
  @Override
  public String getGradUrl()
  {
    if( getFairplay() < 0 )
    {
      return "http://www.fullmetalgalaxy.com/images/icons/unfair.png";
    }
    if( getFinshedGameCount() == 0 )
    {
      // player didn't finshed any game
      return "http://www.fullmetalgalaxy.com/images/clear.cache.gif";
    }
    String iconName = "";
    iconName += getNormalizedLevel();

    if( getPlayerStyle() == PlayerStyle.Pacific )
    {
      iconName += "p"; 
    } else if( getPlayerStyle() == PlayerStyle.Aggressive )
    {
      iconName += "a"; 
    }
    else
    {
      iconName += "b";
    }

    // we need to specify full url, as it is used on forum
    return "http://www.fullmetalgalaxy.com/images/icons/user/"+iconName+".png";
  }
  
  /**
   * @return the playerStyle
   */
  public PlayerStyle getPlayerStyle()
  {
    if( getFinshedGameCount() <= 0 )
    {
      return PlayerStyle.Mysterious;
    }
    return PlayerStyle.fromStyleRatio( getStyleRatio() );
  }

  

  public String buildHtmlFragment()
  {
    String newsHtml = "<a href='"+getProfileUrl()+"'><table width='100%'><tr>";
    newsHtml += "<td><img src='" + getAvatarUrl() + "' height='40px' /></td>";
    newsHtml += "<td>" + getPseudo() + "<br/>" + (int)getCurrentLevel() + " <img src='"
        + getGradUrl() + "' style='margin:0px'/></td>";
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
   * @return the allowMsgFromGame
   */
  public boolean allowMsgFromGame()
  {
    return m_isAllowMsgFromGame;
  }

  /**
   * @param p_allowMsgFromGame the allowMsgFromGame to set
   */
  public void setAllowMsgFromGame(boolean p_allowMsgFromGame)
  {
    m_isAllowMsgFromGame = p_allowMsgFromGame;
  }

  public String getJabberId()
  {
    return m_jabberId;
  }


  public void setJabberId(String p_jabberId)
  {
    m_jabberId = p_jabberId;
  }



  public List<FairPlayNote> getFairplayComments()
  {
    if( m_fairplayComments == null )
    {
      // for old account
      m_fairplayComments = new ArrayList<FairPlayNote>();
    }
    return m_fairplayComments;
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

  public int getActivityLevel()
  {
    return m_activityLevel;
  }

  public void setActivityLevel(int p_activityLevel)
  {
    m_activityLevel = p_activityLevel;
  }

  public int getTotalScoreSum()
  {
    return m_totalScoreSum;
  }

  public void setTotalScoreSum(int p_totalScoreSum)
  {
    m_totalScoreSum = p_totalScoreSum;
  }

  public int getTotalPlayerSum()
  {
    return m_totalPlayerSum;
  }

  public void setTotalPlayerSum(int p_totalPlayerSum)
  {
    m_totalPlayerSum = p_totalPlayerSum;
  }

  public int getFinshedGameCount()
  {
    return m_finshedGameCount;
  }

  public void setFinshedGameCount(int p_finshedGameCount)
  {
    m_finshedGameCount = p_finshedGameCount;
  }

  public int getVictoryCount()
  {
    return m_victoryCount;
  }

  public void setVictoryCount(int p_victoryCount)
  {
    m_victoryCount = p_victoryCount;
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
    return m_currentLevel;
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
    m_currentLevel = p_rating.getConservativeRating();
  }

  public int getFairplay()
  {
    return m_fairplay;
  }

  public void setFairplay(int p_fairplay)
  {
    m_fairplay = p_fairplay;
  }

  public int getRemainingScoring()
  {
    return m_remainingScoring;
  }

  public void setRemainingScoring(int p_remainingScoring)
  {
    m_remainingScoring = p_remainingScoring;
  }

  public float getStyleRatio()
  {
    return m_styleRatio;
  }

  public void setStyleRatio(float p_styleRatio)
  {
    m_styleRatio = p_styleRatio;
  }



}
