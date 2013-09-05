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
      ArrayList<InternetAddress> newToAddresses = new ArrayList<InternetAddress>();
      ArrayList<InternetAddress> newBccAddresses = new ArrayList<InternetAddress>();
      ArrayList<Address> informCCAddresses = new ArrayList<Address>();
      // get original recipients and convert them to real/private email address
      Address[] oldAddresses = message.getRecipients( Message.RecipientType.TO );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress addr = public2private( address );
          if( addr != null )
          {
            newToAddresses.add( addr );
          }
          else if( !isFmgPublicAddress( address ) )
          {
            informCCAddresses.add( address );
          }
        }
      oldAddresses = message.getRecipients( Message.RecipientType.CC );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress addr = public2private( address );
          if( addr != null )
          {
            newToAddresses.add( addr );
          }
          else if( !isFmgPublicAddress( address ) )
          {
            informCCAddresses.add( address );
          }
        }
      oldAddresses = message.getRecipients( Message.RecipientType.BCC );
      if( oldAddresses != null )
        for( Address address : oldAddresses )
        {
          InternetAddress addr = public2private( address );
          if( addr != null )
          {
            newBccAddresses.add( addr );
          }
        }

      // get original sender and convert them to real email address
      for( Address address : message.getFrom() )
      {
        InternetAddress newAddress = private2public( address );
        if( newAddress != null )
        {
          message.setFrom( newAddress );
          message.setReplyTo( null );
        }
      }
      
      if( newToAddresses.isEmpty() )
      {
        log.warning( "email can't be forwarded: "+message );
      }
      else if( newToAddresses.size() == 1 )
      {
        message.setRecipients( Message.RecipientType.TO, newToAddresses.toArray(new Address[0]) );
        message.setRecipients( Message.RecipientType.CC, new Address[0] );
        message.setRecipients( Message.RecipientType.BCC, newBccAddresses.toArray(new Address[0]) );
        addTextPrefix( message, buildTextPrefix( informCCAddresses ) );
        Transport.send( message );
      }
      else
      {
        // hard case as we don't want to send private addresses
        informCCAddresses.addAll( newToAddresses );
        addTextPrefix( message, buildTextPrefix( informCCAddresses ) );
        message.setRecipients( Message.RecipientType.TO, new Address[0] );
        message.setRecipients( Message.RecipientType.CC, new Address[0] );
        if( !newBccAddresses.isEmpty() )
        {
          // first send to BCC recipient only
          message.setRecipients( Message.RecipientType.BCC, newBccAddresses.toArray(new Address[0]) );
          Transport.send( message );
        }
        message.setRecipients( Message.RecipientType.BCC, new Address[0] );
        for( InternetAddress address : newToAddresses )
        {
          message.setRecipients( Message.RecipientType.TO, new Address[]{address} );
          Transport.send( message );
        }
      }
      
    } catch( MessagingException e )
    {
      log.error( e );
    }
  }
  
  
  private String buildTextPrefix(List<Address> p_addresses)
  {
    String prefix = "Forwarded by FullMetalGalaxy.com\n";
    if( p_addresses.isEmpty() )
      return prefix + "\n";
    prefix += "CC: ";
    for( Address address : p_addresses )
    {
      prefix += address + "; ";
    }
    prefix += "\n\n";
    return prefix;
  }

  private void addTextPrefix(MimeMessage p_message, String p_prefix)
  {
    try
    {
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
            part.setText( p_prefix + (String)part.getContent() );
          }
          else if( part.isMimeType( "text/html" ) )
          {
            part.setContent( p_prefix + (String)part.getContent(), "text/html" );
            // result = Jsoup.parse(html).text();
          }
        }
      }
      else if( contentObject instanceof String ) // a simple text message
      {
        p_message.setText( p_prefix + (String)contentObject );
      }
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


  private InternetAddress public2private(Address p_address)
  {
    if( p_address instanceof InternetAddress )
    {
      InternetAddress iAddress = (InternetAddress)p_address;
      Matcher mailMatcher = s_mailPattern.matcher( iAddress.getAddress() );
      if( mailMatcher.matches() )
      {
        String accountStr = ServerUtil.compactTag( mailMatcher.group( 1 ) );
        Query<EbAccount> query = FmgDataStore.dao().query( EbAccount.class )
            .filter( "m_compactPseudo", accountStr );
        EbAccount account = query.get();
        if( account == null )
        {
          log.error( "no account found for compact pseudo " + accountStr );
        }
        else
        {
          try
          {
            return new InternetAddress( account.getEmail(), account.getPseudo() );
          } catch( UnsupportedEncodingException e )
          {
            e.printStackTrace();
          }
        }
      }
    }
    log.error( "no account found for public email " + p_address );
    return null;
  }

  private InternetAddress private2public(Address p_address)
  {
    if( p_address instanceof InternetAddress )
    {
      InternetAddress iAddress = (InternetAddress)p_address;
      Query<EbAccount> query = FmgDataStore.dao().query( EbAccount.class )
          .filter( "m_email", iAddress.getAddress() );
      EbAccount account = query.get();
      if( account != null )
      {
        try
        {
          return new InternetAddress( account.getCompactPseudo() + "@"
              + SystemProperty.applicationId.get() + ".appspotmail.com", account.getPseudo() );
        } catch( UnsupportedEncodingException e )
        {
          e.printStackTrace();
        }
      }
    }
    log.error( "no account found for private email " + p_address );
    return null;
  }



}
