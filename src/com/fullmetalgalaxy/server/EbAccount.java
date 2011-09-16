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
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbAccountStats;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.PlayerFiability;
import com.fullmetalgalaxy.model.persist.PlayerStyle;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;




/**
 * @author Kroc
 * Account data that other people are allowed to see.
 */
public class EbAccount extends EbPublicAccount
{
  private static final long serialVersionUID = -6721026137413400063L;


  public enum AllowMessage
  {
    No, PM, Mail;
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
  @Unindexed
  private String m_description = "";
  @Unindexed
  private boolean m_allowPrivateMsg = true;
  /** to allow message like 'it your turn on game xxx' */
  @Unindexed
  private AllowMessage m_allowMsgFromGame = AllowMessage.PM;

  private String m_jabberId = null;

  @Unindexed
  private String m_locale = "";
  
  /**
   * A VIP already finished one game and wasn't banned for a while.
   */
  private PlayerFiability m_fiability = PlayerFiability.Normal;
  
  /**
   * player style to recognize people
   */
  private PlayerStyle m_playerStyle = PlayerStyle.Sheep;
  
  /**
   * player main color
   */
  private int m_mainColor = EnuColor.None;
  
  /** because of this data, EbAccount shoudln't be send on client side ! 
   * We may have to encrypt this data.
   * */
  private String m_password = null;

  /**
   * last time user ask for his password.
   */
  private Date m_lastPasswordAsk = null;
  
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
    m_password = null;
    m_subscriptionDate = new Date( System.currentTimeMillis() );
    m_authProvider = AuthProvider.Fmg;
    m_description = "";
    m_allowPrivateMsg = true;
    m_allowMsgFromGame = AllowMessage.PM;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
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

  @Override
  public String getProfileUrl()
  {
    if( isIsforumIdConfirmed() && getForumId() != null )
    {
      return "http://" + FmpConstant.getForumHost() + "/u" + getForumId();
    }
    return super.getProfileUrl();
  }

  @Override
  public String getPMUrl(String p_subject)
  {
    if( p_subject == null ) p_subject = "";
    if( isIsforumIdConfirmed() && getForumId() != null )
    {
      // use forum to send a Private Message
      return EbPublicAccount.getForumPMUrl( p_subject, getPseudo() ) + "&u=" + getForumId() ;
    }
    else if( isAllowPrivateMsg() && haveEmail() )
    {
      // then send an email with our form
      return "/email.jsp?id="+getId()+"&subject="+p_subject;
    }
    return "/genericmsg.jsp?title="+getPseudo()+" ne souhaite pas être contacté";
  }

  /**
   * icon url to illustrate player fiability, level and style
   * @return
   */
  public String getGradUrl()
  {
    String iconName = "";
    if( getFiability() == PlayerFiability.Banned )
    {
      iconName = "b";
    } else if( getFiability() == PlayerFiability.Vip )
    {
      iconName = "v";
    }
    int normalizedLevel = 0;
    int maxLevel = GlobalVars.getMaxLevel(); 
    if( maxLevel > 1 )
    {
      normalizedLevel = (int)(((float)(getCurrentLevel()-1))/(maxLevel-1) *9);
    }
    if( normalizedLevel < 0 ) normalizedLevel=0;
    if( normalizedLevel > 9 ) normalizedLevel=9;
    iconName += normalizedLevel;
    if( getPlayerStyle() == PlayerStyle.Pacific )
    {
      iconName += "p"; 
    } else if( getPlayerStyle() == PlayerStyle.Balanced )
    {
      iconName += "b"; 
    } else if( getPlayerStyle() == PlayerStyle.Aggressive )
    {
      iconName += "a"; 
    } else
    {
      // PlayerStyle.Sheep
      iconName += "s"; 
    }
    // we need to specify full url, as it is used on forum
    return "http://www.fullmetalgalaxy.com/images/icons/user/"+iconName+".png";
  }
  
  
  public String buildHtmlFragment()
  {
    String newsHtml = "<a href='"+getProfileUrl()+"'><table width='100%'><tr>";
    newsHtml += "<td><img src='"+getAvatarUrl()+"' height='40px' border='0' /></td>";
    newsHtml += "<td>"+getPseudo()+"<br/><img src='"+getGradUrl()+"' border='0' /></td>";
    newsHtml += "<td>"+getCurrentLevel()+" Pts</td>";
    newsHtml += "</tr></table></a>";
    return newsHtml;
  }
  
  /**
   * account is considered as active if he connect itself in the last 30 days
   * @return true if account is active
   */
  public boolean isActive()
  {
    return getLastConnexion() != null
        && getLastConnexion().getTime() > System.currentTimeMillis()
            - (1000l * 60 * 60 * 24 * 30);  // 30 days
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
   * @return the allowMsgFromGame
   */
  public AllowMessage getAllowMsgFromGame()
  {
    return m_allowMsgFromGame;
  }

  /**
   * @param p_allowMsgFromGame the allowMsgFromGame to set
   */
  public void setAllowMsgFromGame(AllowMessage p_allowMsgFromGame)
  {
    m_allowMsgFromGame = p_allowMsgFromGame;
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
    if( m_maxLevel < p_maxLevel )
    {
      m_maxLevel = p_maxLevel;
    }
  }


  public List<EbAccountStats> getStats()
  {
    if( m_stats == null )
    {
      // for old account
      m_stats = new ArrayList<EbAccountStats>();
    }
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

  /**
   * @return the playerStyle
   */
  public PlayerStyle getPlayerStyle()
  {
    return m_playerStyle;
  }

  /**
   * @param p_playerStyle the playerStyle to set
   */
  public void setPlayerStyle(PlayerStyle p_playerStyle)
  {
    m_playerStyle = p_playerStyle;
  }
  
  public Date getLastPasswordAsk()
  {
    return m_lastPasswordAsk;
  }

  public void setLastPasswordAsk(Date p_lastPasswordAsk)
  {
    m_lastPasswordAsk = p_lastPasswordAsk;
  }

  /**
   * @return the fiability
   */
  public PlayerFiability getFiability()
  {
    return m_fiability;
  }

  /**
   * @param p_fiability the fiability to set
   */
  public void setFiability(PlayerFiability p_fiability)
  {
    m_fiability = p_fiability;
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

  public int getMainColor()
  {
    return m_mainColor;
  }

  public void setMainColor(int p_mainColor)
  {
    m_mainColor = p_mainColor;
  }



}
