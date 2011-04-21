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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.server.FmgDataStore;

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
    super.doGet( p_req, p_resp );
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    Map<String, String> params = new HashMap<String, String>();
    ServletFileUpload upload = new ServletFileUpload();
    try
    {
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          params.put( item.getFieldName(), Streams.asString( item.openStream(), "UTF-8" ) );
        }
      }

      sendMail( params );

    } catch( FileUploadException e )
    {
      log.error( e );
    }

    p_response.getOutputStream().println( "Message envoye" );
  }


  protected void sendMail(Map<String, String> params)
  {
    EbAccount accountTo = null;
    EbAccount accountFrom = null;

    FmgDataStore ds = new FmgDataStore(true);
    accountTo = ds.get( EbAccount.class, Long.parseLong( params.get( "toid" ) ) );
    accountFrom = ds.get( EbAccount.class, Long.parseLong( params.get( "fromid" ) ) );
    assert accountTo != null;
    assert accountFrom != null;
    String body = "Vous avez recu un message de "
              + accountFrom.getPseudo()
              + "\n"
              + "Pour repondre vous pouvez utiliser ce lien http://www.fullmetalgalaxy.com/privatemsg.jsp?id="
        + accountFrom.getId() + "\n\n" + params.get( "msg" );
    sendMail( "[FMG] MP de " + accountFrom.getPseudo() + " : " + params.get( "subject" ), body,
        accountTo.getEmail() );
  }

  /**
   * 
   * @param p_subject
   * @param p_body
   * @param p_recipients
   * @return false if an error occur in parameters
   */
  public static synchronized boolean sendMail(String p_subject, String p_body, String p_recipients)
  {
    boolean isOk = true;
    Properties props = new Properties();
    Session session = Session.getDefaultInstance( props, null );
    MimeMessage msg = new MimeMessage( session );

    try
    {
      msg.setSender( new InternetAddress( "admin@fullmetalgalaxy.com", "FMG Admin" ) );
      msg.setSubject( p_subject );
      msg.setContent( p_body, "text/plain" );
      msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( p_recipients ) );
      msg.setRecipients( Message.RecipientType.BCC,
          InternetAddress.parse( "archive@fullmetalgalaxy.com" ) );
      Transport.send( msg );
      log.fine( "Mail send to " + p_recipients + " subject:" + p_subject );
    } catch( AddressException e )
    {
      isOk = false;
      log.error( e );
    } catch( MessagingException e )
    {
      isOk = false;
      log.error( e );
    } catch( UnsupportedEncodingException e )
    {
      isOk = false;
      log.error( e );
    } catch( Exception e )
    {
      isOk = false;
      log.error( e );
    }
    return isOk;
  }

}
