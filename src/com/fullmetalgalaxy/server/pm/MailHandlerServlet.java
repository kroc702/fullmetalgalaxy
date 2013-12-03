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

package com.fullmetalgalaxy.server.pm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.FmpLogger;
import com.fullmetalgalaxy.server.ServerUtil;
import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 * 
 * handle incoming mail
 */
public class MailHandlerServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private final static FmpLogger log = FmpLogger.getLogger( MailHandlerServlet.class.getName() );
  private static Pattern s_mailPattern = Pattern.compile( "([^@]*)@"
      + SystemProperty.applicationId.get() + "\\.appspotmail.com" );

  /**
   * 
   */
  public MailHandlerServlet()
  {
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance( props, null );
    try
    {
      MimeMessage message = new MimeMessage( session, req.getInputStream() );

      // new recipients
      ArrayList<InternetAddress> newPrivateToAddresses = new ArrayList<InternetAddress>();
      ArrayList<InternetAddress> newPublicToAddresses = new ArrayList<InternetAddress>();
      ArrayList<InternetAddress> newBccAddresses = new ArrayList<InternetAddress>();
      ArrayList<Address> informCCAddresses = new ArrayList<Address>();
      // get original recipients and convert them to real/private email address
      Address[] oldAddresses = message.getRecipients( Message.RecipientType.TO );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress privateAddr = new InternetAddress();
          boolean hideEmailToPlayer = public2private( address, privateAddr );
          if( privateAddr.getAddress() != null )
          {
            if( hideEmailToPlayer )
              newPrivateToAddresses.add( privateAddr );
            else
              newPublicToAddresses.add( privateAddr );
          }
          else if( !isFmgPublicAddress( address ) )
          {
            // consider as external address: recipient already received it
            informCCAddresses.add( address );
          }
        }
      oldAddresses = message.getRecipients( Message.RecipientType.CC );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress privateAddr = new InternetAddress();
          boolean hideEmailToPlayer = public2private( address, privateAddr );
          if( privateAddr.getAddress() != null )
          {
            if( hideEmailToPlayer )
              newPrivateToAddresses.add( privateAddr );
            else
              newPublicToAddresses.add( privateAddr );
          }
          else if( !isFmgPublicAddress( address ) )
          {
            // consider as external address: recipient already received it
            informCCAddresses.add( address );
          }
        }
      oldAddresses = message.getRecipients( Message.RecipientType.BCC );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress privateAddr = new InternetAddress();
          public2private( address, privateAddr );
          if( privateAddr.getAddress() != null )
          {
            newBccAddresses.add( privateAddr );
          }
        }

      // get original sender and convert them to real email address
      for( Address address : message.getFrom() )
      {
        InternetAddress newFmgAddress = new InternetAddress();
        private2public( address, newFmgAddress );
        // FMG can't sent email for arbitrary address
        if( newFmgAddress.getAddress() != null )
        {
          message.setFrom( newFmgAddress );
          message.setReplyTo( null );
        }
      }
      
      if( newPrivateToAddresses.isEmpty() && newPublicToAddresses.isEmpty() )
      {
        log.warning( "email can't be forwarded: "+message );
      }
      else if( newPrivateToAddresses.size() == 1 )
      {
        message.setRecipients( Message.RecipientType.TO,
            newPrivateToAddresses.toArray( new Address[0] ) );
        message.addRecipients( Message.RecipientType.TO,
            newPublicToAddresses.toArray( new Address[0] ) );
        message.setRecipients( Message.RecipientType.CC, new Address[0] );
        message.setRecipients( Message.RecipientType.BCC, newBccAddresses.toArray(new Address[0]) );
        addPrefix( message, informCCAddresses );
        

        Object contentObject = message.getContent();
        if( contentObject instanceof Multipart )
        {
          Multipart content = (Multipart)contentObject;
          int count = content.getCount();
          for( int i = 0; i < count; i++ )
          {
            BodyPart part = content.getBodyPart( i );
            System.out.println( "ContentType = " + part.getContentType() );
            System.out.println( "Content = " + part.getContent() );
          }
        }
          
        Transport.send( message );
      }
      else
      {
        // hard case as we don't want to send private addresses
        informCCAddresses.addAll( newPrivateToAddresses );
        informCCAddresses.addAll( newPublicToAddresses );
        addPrefix( message, informCCAddresses );
        message.setRecipients( Message.RecipientType.TO, new Address[0] );
        message.setRecipients( Message.RecipientType.CC, new Address[0] );
        if( !newBccAddresses.isEmpty() )
        {
          // first send to BCC recipient only
          message.setRecipients( Message.RecipientType.BCC, newBccAddresses.toArray(new Address[0]) );
          Transport.send( message );
        }
        message.setRecipients( Message.RecipientType.BCC, new Address[0] );
        for( InternetAddress address : newPrivateToAddresses )
        {
          message.setRecipients( Message.RecipientType.TO, new Address[]{address} );
          message.addRecipients( Message.RecipientType.TO,
              newPublicToAddresses.toArray( new Address[0] ) );
          Transport.send( message );
          // only the first email sent will contain real player email
          newPublicToAddresses.clear();
        }
      }
      
    } catch( MessagingException e )
    {
      log.error( e );
    }
  }
  
  
  private String buildTextPrefix(List<Address> p_addresses)
  {
    String prefix = "";
    if( p_addresses.isEmpty() )
      return prefix + "";
    prefix += "CC: ";
    for( Address address : p_addresses )
    {
      prefix += address + "; ";
    }
    prefix += "\n\n";
    return prefix;
  }

  private String buildHtmlPrefix(List<Address> p_addresses)
  {
    String prefix = "";
    if( p_addresses.isEmpty() )
      return prefix;
    prefix += "<b>CC:</b> ";
    for( Address address : p_addresses )
    {
      prefix += address + "; ";
    }
    prefix += "<br/><br/>";
    return prefix;
  }

  private void addPrefix(MimeMessage p_message, List<Address> p_addresses)
  {
    try
    {
      // add CC information in body
      Object contentObject = p_message.getContent();
      if( contentObject instanceof Multipart )
      {
        Multipart content = (Multipart)contentObject;
        int count = content.getCount();
        for( int i = 0; i < count; i++ )
        {
          BodyPart part = content.getBodyPart( i );
          if( part.isMimeType( "text/plain" ) )
          {
            part.setText( buildTextPrefix(p_addresses) + (String)part.getContent() );
          }
          else if( part.isMimeType( "text/html" ) )
          {
            part.setContent( buildHtmlPrefix(p_addresses) + (String)part.getContent(), "text/html" );
            // result = Jsoup.parse(html).text();
          }
        }
        p_message.setContent( content );
      }
      else if( contentObject instanceof String ) // a simple text message
      {
        p_message.setText( buildTextPrefix( p_addresses ) + (String)contentObject );
      }

      // now update subject
      String subject = p_message.getSubject(); 
      if( subject == null ) subject = "[FMG]";
      else if( !subject.contains( "[FMG]" )) subject = "[FMG] " + subject;
      p_message.setSubject( subject );
      
      p_message.saveChanges();
    } catch( MessagingException | IOException e )
    {
      log.error( e );
    }
  }

  private boolean isFmgPublicAddress(Address p_address)
  {
    if( p_address instanceof InternetAddress )
    {
      InternetAddress iAddress = (InternetAddress)p_address;
      Matcher mailMatcher = s_mailPattern.matcher( iAddress.getAddress() );
      return mailMatcher.matches();
    }
    return false;
  }

  /**
   * 
   * @param p_publicAddress email alias provided by FMG
   * @param p_privateAddress real player email
   * @return account.isHideEmailToPlayer(); or false if account wasn't found
   */
  private boolean public2private(Address p_publicAddress, InternetAddress p_privateAddress)
  {
    EbAccount account = null;
    p_privateAddress.setAddress( null );
    if( p_publicAddress instanceof InternetAddress )
    {
      InternetAddress iAddress = (InternetAddress)p_publicAddress;
      Matcher mailMatcher = s_mailPattern.matcher( iAddress.getAddress() );
      if( mailMatcher.matches() )
      {
        String accountStr = ServerUtil.compactTag( mailMatcher.group( 1 ) );
        Query<EbAccount> query = FmgDataStore.dao().query( EbAccount.class )
            .filter( "m_compactPseudo", accountStr );
        account = query.get();
        if( account == null )
        {
          log.error( "no account found for compact pseudo " + accountStr );
        }
        else
        {
          p_privateAddress.setAddress( account.getEmail() );
          try
          {
            p_privateAddress.setPersonal( account.getPseudo() );
          } catch( UnsupportedEncodingException e )
          {
            log.error( e );
          }
        }
      }
    }
    log.error( "no account found for public email " + p_publicAddress );
    if( account != null )
    {
      return account.isHideEmailToPlayer();
    }
    return false;
  }

  /**
   * 
   * @param p_publicAddress email alias provided by FMG
   * @param p_privateAddress real player email
   * @return account.isHideEmailToPlayer(); or false if account wasn't found
   */
  private boolean private2public(Address p_privateAddress, InternetAddress p_publicAddress)
  {
    p_publicAddress.setAddress( null );
    EbAccount account = null;
    if( p_privateAddress instanceof InternetAddress )
    {
      InternetAddress iAddress = (InternetAddress)p_privateAddress;
      Query<EbAccount> query = FmgDataStore.dao().query( EbAccount.class )
          .filter( "m_email", iAddress.getAddress() );
      account = query.get();
      if( account != null )
      {
        p_publicAddress.setAddress( account.getFmgEmail() );
        try
        {
          p_publicAddress.setPersonal( account.getPseudo() );
        } catch( UnsupportedEncodingException e )
        {
          log.error( e );
        }
      }
    }
    log.error( "no account found for private email " + p_privateAddress );
    if( account != null )
    {
      return account.isHideEmailToPlayer();
    }
    return false;
  }



}
