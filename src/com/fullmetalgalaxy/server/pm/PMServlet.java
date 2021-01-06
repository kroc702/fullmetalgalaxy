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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.server.EbAccount;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.FmpLogger;
import com.fullmetalgalaxy.server.ServerUtil;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;

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
      p_request.setCharacterEncoding( "UTF-8" );

      // build message to send
      // send an email with mailjet API
      String subject = "no subject";
      String content = "";
      JSONArray mailTo = new JSONArray();
      String replyTo = null;
      
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          if( "msg".equalsIgnoreCase( item.getFieldName() ) )
          {
        	  content = Streams.asString( item.openStream(), "UTF-8" );
          }
          if( "subject".equalsIgnoreCase( item.getFieldName() ) )
          {
        	  subject = Streams.asString( item.openStream(), "UTF-8" );
          }
          if( "toid".equalsIgnoreCase( item.getFieldName() ) )
          {
            EbAccount account = null;
            try {
              account = FmgDataStore.dao().get( EbAccount.class, Long.parseLong( Streams.asString( item.openStream(), "UTF-8" ) ) );
            } catch(NumberFormatException e) {}
            if( account != null )
            {
            	mailTo.put(new JSONObject()
                        .put("Email", account.getEmail())
                        .put("Name", account.getPseudo()));
            }
          }
          if( "fromid".equalsIgnoreCase( item.getFieldName() ) )
          {
        	  EbAccount  fromAccount = null;
            try {
              fromAccount = FmgDataStore.dao().get( EbAccount.class, Long.parseLong( Streams.asString( item.openStream(), "UTF-8" ) ) );
            } catch(NumberFormatException e) {}
            if( fromAccount != null )
            {
              if( fromAccount.getAuthProvider() == AuthProvider.Google
                  && !fromAccount.isHideEmailToPlayer() )
              {
            	  replyTo = fromAccount.getEmail();
              }
              else
              {
            	  replyTo =  fromAccount.getFmgEmail();
              }
            }
          }
        }
      }

 	 ClientOptions options = ClientOptions.builder()
	            .apiKey("a2b35f062939e510bfc46852a484487a")
	            .apiSecretKey("ab1e463bc8664bc4093e757431cd245e")
	            .build();
   MailjetClient client = new MailjetClient(options);
   
   MailjetRequest request = new MailjetRequest(Emailv31.resource)
         .property(Emailv31.MESSAGES, new JSONArray()
             .put(new JSONObject()
                 .put(Emailv31.Message.FROM, new JSONObject()
                     .put("Email", "admin@fullmetalgalaxy.com")
                     .put("Name", "FMG Admin"))
                 .put(Emailv31.Message.TO, mailTo)
                 .put(Emailv31.Message.SUBJECT, "[FMG] " + subject)
                 .put(Emailv31.Message.TEXTPART, content)
                 .put(Emailv31.Message.HEADERS, new JSONObject()
                         .put("Reply-To",replyTo))));
   MailjetResponse response = client.post(request);


    } catch( Exception e )
    {
      log.error( e );
      p_response.sendRedirect( "/genericmsg.jsp?title=Error&text="+e.getMessage() );
      return;
    } 

    p_response.sendRedirect( "/genericmsg.jsp?title=Message envoye" );
  }



}
