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
 * game_time_step
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
    return send( p_account, false );
  }

  public boolean sendEMail(EbAccount p_account)
  {
    return send( p_account, true );
  }

  public boolean send(EbAccount p_account, boolean p_forceEMail)
  {
    if( p_account == null )
    {
      return false;
    }
    boolean isOk = true;
    String error = null;

    // construct message according to parameters and locale
    //
    putParams( p_account );
    FmgMessage msg = this;
    if( getName() != null )
    {
      String locale = p_account.getLocale();
      if( locale == null || locale.isEmpty() )
      {
        locale = I18n.getDefaultLocale();
      }
      msg = FmgMessage.buildMessage( locale, getName() );
    }
    msg = msg.applyParams( m_params );


    // then send message according to users profile
    //
    if( (p_account.getAllowMsgFromGame() == AllowMessage.No && !p_forceEMail)
        || !p_account.haveEmail() )
    {
      // send nothing
      log.fine( "player " + p_account.getPseudo() + " don't want any messages" );
    }
    else if( p_account.getAllowMsgFromGame() == AllowMessage.PM && p_account.isIsforumIdConfirmed()
        && !p_forceEMail )
    {
      // send a forum private message
      isOk = ServerUtil.forumConnector().sendPMessage( "[FMG] " + msg.getSubject(), msg.getBody(),
          p_account.getPseudo() );
    }
    else
    {
      // send an mail
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

    // send a copy to archive@fullmetalgalaxy.com
    Properties props = new Properties();
    Session session = Session.getDefaultInstance( props, null );
    MimeMessage mimemsg = new MimeMessage( session );

    try
    {
      String subject = "[" + p_account.getAllowMsgFromGame() + "] ";
      String body = msg.getBody();
      if( isOk == false )
      {
        subject += "-Failed- ";
        body = error + "\n\n" + msg.getBody();
      }
      subject += msg.getSubject();
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
      error = e.getMessage();
    }

    return isOk;
  }

  protected FmgMessage putParams(Map<String, String> p_params)
  {
    m_params.putAll( p_params );
    return this;
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
    if( p_params != null )
    {
      for( Entry<String, String> entry : p_params.entrySet() )
      {
        String key = "{" + entry.getKey() + "}";
        subject = subject.replace( key, entry.getValue() );
        body = body.replace( key, entry.getValue() );
      }
    }
    return new FmgMessage( subject, body );
  }
  
  private FmgMessage putParams(EbAccount p_account)
  {
    m_params.put( "pseudo", p_account.getPseudo() );
    m_params.put( "login", p_account.getLogin() );
    m_params.put( "password", p_account.getPassword() );
    return this;
  }
  
  public FmgMessage putParams(Game p_game)
  {
    m_params.put( "game_name", p_game.getName() );
    m_params.put( "game_url", "http://www.fullmetalgalaxy.com/game.jsp?id=" + p_game.getId() );
    m_params.put( "game_time_step", "" + p_game.getCurrentTimeStep() );
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
