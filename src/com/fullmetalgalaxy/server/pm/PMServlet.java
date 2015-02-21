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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server.pm;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.FmpLogger;
import com.fullmetalgalaxy.server.ServerUtil;

/**
 * @author Vincent
 *
 */
public class PMServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;
  private final static FmpLogger log = FmpLogger.getLogger( PMServlet.class.getName() );

  /**
   * 
   */
  public PMServlet()
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    EbAccount account = ServerUtil.findRequestedAccount(p_req);
    if( account == null ) 
    { 
      p_resp.sendRedirect( "/genericmsg.jsp?title=Le profil n'a pas été trouvé." );
      return;
    }
    String subject = p_req.getParameter("subject");
    p_resp.sendRedirect( account.getEMailUrl( subject ) );
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    try
    {
      // build message to send
      Properties props = new Properties();
      Session session = Session.getDefaultInstance( props, null );
      MimeMessage msg = new MimeMessage( session );
      msg.setSubject( "[FMG] no subject", "text/plain" );
      msg.setSender( new InternetAddress( "admin@fullmetalgalaxy.com", "FMG Admin" ) );
      msg.setFrom( new InternetAddress( "admin@fullmetalgalaxy.com", "FMG Admin" ) );
      EbAccount fromAccount = null;

      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          if( "msg".equalsIgnoreCase( item.getFieldName() ) )
          {
            msg.setContent( Streams.asString( item.openStream(), "UTF-8" ), "text/plain" );
          }
          if( "subject".equalsIgnoreCase( item.getFieldName() ) )
          {
            msg.setSubject( "[FMG] " + Streams.asString( item.openStream(), "UTF-8" ), "text/plain" );
          }
          if( "toid".equalsIgnoreCase( item.getFieldName() ) )
          {
            EbAccount account = null;
            try {
              account = FmgDataStore.dao().get( EbAccount.class, Long.parseLong( Streams.asString( item.openStream(), "UTF-8" ) ) );
            } catch(NumberFormatException e) {}
            if( account != null )
            {
              msg.addRecipient( Message.RecipientType.TO, new InternetAddress( account.getEmail(),
                  account.getPseudo() ) );
            }
          }
          if( "fromid".equalsIgnoreCase( item.getFieldName() ) )
          {
            try {
              fromAccount = FmgDataStore.dao().get( EbAccount.class, Long.parseLong( Streams.asString( item.openStream(), "UTF-8" ) ) );
            } catch(NumberFormatException e) {}
            if( fromAccount != null )
            {
              if( fromAccount.getAuthProvider() == AuthProvider.Google
                  && !fromAccount.isHideEmailToPlayer() )
              {
                msg.setFrom( new InternetAddress( fromAccount.getEmail(), fromAccount.getPseudo() ) );
              }
              else
              {
                msg.setFrom( new InternetAddress( fromAccount.getFmgEmail(), fromAccount
                    .getPseudo() ) );
              }
            }
          }
        }
      }

      // msg.addRecipients( Message.RecipientType.BCC, InternetAddress.parse(
      // "archive@fullmetalgalaxy.com" ) );
      Transport.send( msg );

    } catch( Exception e )
    {
      log.error( e );
      p_response.sendRedirect( "/genericmsg.jsp?title=Error&text="+e.getMessage() );
      return;
    } 

    p_response.sendRedirect( "/genericmsg.jsp?title=Message envoye" );
  }



}
