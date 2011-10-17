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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.EbAccount.AllowMessage;

/**
 * @author vlegendr
 *
 * <pre>
 * Currently used key:
 * pseudo
 * login
 * password
 * game_name
 * game_url
 * game_currentTimeStep
 * game_results
 * </pre>
 */
public class FmgMessage
{
  private final static FmpLogger log = FmpLogger.getLogger( FmgMessage.class.getName() );

  private String m_subject = "";
  private String m_body = "";
  private String m_name = null;
  private Map<String, String> m_params = new HashMap<String, String>();
  
  
  public FmgMessage()
  {
  }
  
  public FmgMessage(String p_msgName)
  {
    m_name = p_msgName;
  }

  public FmgMessage(String p_msgName, Game p_game)
  {
    this( p_msgName );
    putParams( p_game );
  }

  public FmgMessage(String p_subject, String p_body)
  {
    m_subject = p_subject;
    m_body = p_body;
  }
  
  
  public static FmgMessage buildMessage(String p_locale, String p_msgName)
  {
    // compute file name
    String fileName = "";
    if( ServerUtil.getBasePath() != null )
    {
      fileName = ServerUtil.getBasePath();
    }
    fileName += "/i18n/"+p_locale+"/msg/"+p_msgName;
    if( !fileName.endsWith( ".txt" ) )
    {
      fileName += ".txt";
    }
    
    // then read it
    FmgMessage msg = readFile( fileName );
    if( msg == null && !I18n.getDefaultLocale().equals( p_locale ) )
    {
      return buildMessage( I18n.getDefaultLocale(), p_msgName );
    }
    if( msg == null )
    {
      log.error( "Message '" + p_msgName + "' not found !" );
    }
    return msg;
  }


  /**
   * send this message to a specific account regardless recipients string.
   * According to user preference, this method will send nothing, a PM or email.
   * @param p_account
   * @return
   */
  public boolean send(EbAccount p_account)
  {
    boolean isOk = true;
    String error = null;
    
    if( p_account.getAllowMsgFromGame() == AllowMessage.PM && p_account.isIsforumIdConfirmed() )
    {
      // send a forum private message
      isOk = sendPM(p_account);
    }
    else if( p_account.getAllowMsgFromGame() == AllowMessage.Mail && p_account.haveEmail() )
    {
      // send an mail
      isOk = sendEMail(p_account);
    }
    else
    {
      // send nothing
      error = "player " + p_account.getPseudo() + " don't want any messages";
      log.fine( error );
      send2Archive( this, "?", error );
    }
    return isOk;
  }

  /**
   * send this message as an email regardless user profile
   * @param p_account
   * @return
   */
  public boolean sendEMail(EbAccount p_account)
  {
    if( p_account == null )
    {
      return false;
    }
    boolean isOk = true;
    String error = null;

    // localize msg
    FmgMessage msg = localize(p_account);

    // then send message according to users profile
    //
    if( p_account.haveEmail() )
    {
      // send an email
      Properties props = new Properties();
      Session session = Session.getDefaultInstance( props, null );
      MimeMessage mimemsg = new MimeMessage( session );

      try
      {
        mimemsg.setSender( new InternetAddress( "admin@fullmetalgalaxy.com", "FMG Admin" ) );
        mimemsg.setSubject( "[FMG] " + msg.getSubject() );
        mimemsg.setContent( msg.getBody(), "text/plain" );
        mimemsg.setRecipients( Message.RecipientType.TO,
            InternetAddress.parse( p_account.getEmail() ) );
        mimemsg.setRecipients( Message.RecipientType.BCC,
            InternetAddress.parse( "archive@fullmetalgalaxy.com" ) );
        Transport.send( mimemsg );
        log.fine( "Mail send to " + p_account.getEmail() + " subject:" + msg.getSubject() );
      } catch( Exception e )
      {
        isOk = false;
        log.error( e );
        error = e.getMessage();
      }
    }
    else
    {
      // send nothing
      error = "player " + p_account.getPseudo() + " don't have email addresse";
      log.fine( error );
    }

    // send a copy to archive@fullmetalgalaxy.com
    send2Archive( msg, "EMAIL", error );
    
    return isOk;
  }

  /**
   * send this message as a forum private message, regardless user profile
   * and event if link between forum and fmg isn't confirmed
   * @param p_account
   * @return
   */
  public boolean sendPM(EbAccount p_account)
  {
    if( p_account == null )
    {
      return false;
    }
    boolean isOk = true;
    String error = null;

    // localize msg
    FmgMessage msg = localize(p_account);

    // send a forum private message
    isOk = ServerUtil.forumConnector().sendPMessage( "[FMG] " + msg.getSubject(), msg.getBody(),
        p_account.getPseudo() );

    // send a copy to archive@fullmetalgalaxy.com
    send2Archive( msg, "PM", error );
    
    return isOk;
  }

  /**
   * construct message according to parameters and locale
   * @param p_account
   * @param p_msg
   * @return
   */
  private FmgMessage localize(EbAccount p_account)
  {
    putParams( p_account );
    FmgMessage msg = null;
    if( getName() != null )
    {
      String locale = p_account.getLocale();
      if( locale == null || locale.isEmpty() )
      {
        locale = I18n.getDefaultLocale();
      }
      msg = FmgMessage.buildMessage( locale, getName() );
    }
    if( msg != null )
    {
      return msg.applyParams( m_params ); 
    }
    return applyParams( m_params );
  }
  
  /**
   * send a copy to archive@fullmetalgalaxy.com
   * p_account.getAllowMsgFromGame()
   * @return
   */
  private boolean send2Archive(FmgMessage p_msg, String p_tag, String p_error)
  {
    boolean isOk = true;
    
    // 
    Properties props = new Properties();
    Session session = Session.getDefaultInstance( props, null );
    MimeMessage mimemsg = new MimeMessage( session );

    try
    {
      String subject = "[" + p_tag + "] ";
      String body = p_msg.getBody();
      if( p_error != null )
      {
        subject += "-Failed- ";
        body = p_error + "\n\n" + p_msg.getBody();
      }
      subject += p_msg.getSubject();
      mimemsg.setSender( new InternetAddress( "admin@fullmetalgalaxy.com", "FMG Admin" ) );
      mimemsg.setSubject( subject );
      mimemsg.setContent( body, "text/plain" );
      mimemsg.setRecipients( Message.RecipientType.TO,
          InternetAddress.parse( "archive@fullmetalgalaxy.com" ) );
      Transport.send( mimemsg );
    } catch( Exception e )
    {
      isOk = false;
      log.error( e );
      p_error = e.getMessage();
    }
    return isOk;
  }
  
 
  protected FmgMessage putParams(Map<String, String> p_params)
  {
    m_params.putAll( p_params );
    return this;
  }

  protected FmgMessage putParam(String p_key, String p_value)
  {
    m_params.put( p_key, p_value );
    return this;
  }

  public FmgMessage applyParams()
  {
    return applyParams( m_params );
  }

  /**
   * remplace key present in p_params and found with the following syntax {key} in subject and body
   * template by their corresponding value.
   * @param p_params
   * @return a new instance
   */
  private FmgMessage applyParams(Map<String, String> p_params)
  {
    String subject = getSubject();
    String body = getBody();
    if( subject == null ) subject = "";
    if( body == null ) body = "";
    if( p_params != null )
    {
      for( Entry<String, String> entry : p_params.entrySet() )
      {
        if( entry.getKey() != null && entry.getValue() != null )
        {
          String key = "{" + entry.getKey() + "}";
          subject = subject.replace( key, entry.getValue() );
          body = body.replace( key, entry.getValue() );
        }
      }
    }
    return new FmgMessage( subject, body );
  }
  

  private FmgMessage putParams(EbAccount p_account)
  {
    if( p_account != null )
    {
      m_params.put( "pseudo", p_account.getPseudo() );
      m_params.put( "login", p_account.getLogin() );
      m_params.put( "password", p_account.getPassword() );
      m_params.put( "forumKey", p_account.getForumKey() );
    }
    return this;
  }
  
  public FmgMessage putParams(Game p_game)
  {
    if( p_game != null )
    {
      m_params.put( "game_name", p_game.getName() );
      m_params.put( "game_url", "http://www.fullmetalgalaxy.com/game.jsp?id=" + p_game.getId() );
      m_params.put( "game_currentTimeStep", "" + p_game.getCurrentTimeStep() );
      m_params.put( "game_description", p_game.getDescription() );
      
      // compute game result
      String gameResults = "";
      if( p_game.isFinished() )
      {
        for(EbRegistration registration : p_game.getRegistrationByWinningRank() )
        {
          if( registration.getAccount() != null )
          {
            gameResults += registration.getAccount().getPseudo();
          }
          else
          {
            gameResults += "???";
          }
          gameResults += " : " + registration.getWinningScore( p_game ) + " pts\n";
        }
      }
      m_params.put( "game_results", gameResults );
    }
    return this;
  }

  private static FmgMessage readFile(String p_fileName)
  {
    FmgMessage msg = new FmgMessage();
    StringBuffer page = new StringBuffer();
    try
    {
      FileInputStream fis;
      fis = new FileInputStream( new File( p_fileName ) );
      BufferedReader reader = new BufferedReader( new InputStreamReader( fis, "UTF-8" ) );
  
      String line = reader.readLine();
      if( line != null )
      {
        msg.setSubject( line );
      }
      while( (line = reader.readLine()) != null )
      {
        page.append( line );
        page.append( '\n' );
      }
      reader.close();
      
      msg.setBody( page.toString() );
    } catch( Exception e )
    {
      e.printStackTrace();
      return null;
    }
    
    return msg;
  }

  
  
  public String getSubject()
  {
    return m_subject;
  }

  private void setSubject(String p_subject)
  {
    m_subject = p_subject;
  }

  public String getBody()
  {
    return m_body;
  }

  private void setBody(String p_body)
  {
    m_body = p_body;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return m_name;
  }


}
