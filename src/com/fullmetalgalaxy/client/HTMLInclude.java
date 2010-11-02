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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HTML;

/**
 * Renders the contents of an HTML file as a widget.
 * HTMLInclude is a simple subclass of the GWT provided
 * HTML widget, the difference being that you provide a URL
 * to the content in the constructor instead of supplying
 * the actual content in your code. The intended purpose is
 * to keep code clean by keeping verbose HTML content
 * out of the Java source and in seperate HTML files that
 * can be included at run time.
 * 
 * @author rhanson
 */
public class HTMLInclude extends HTML
{
  public HTMLInclude(final String url)
  {
    super();
    final HTMLInclude widget = this;
    try
    {
      new RequestBuilder( RequestBuilder.GET, url ).sendRequest( "", new RequestCallback()
      {
        @Override
        public void onError(Request request, Throwable exception)
        {
          GWT.log( "HTMLInclude: error fetching " + url, exception );
        }

        @Override
        public void onResponseReceived(Request request, Response response)
        {
          if( response.getStatusCode() == 200 )
          {
            widget.setHTML( response.getText() );
          }
          else
          {
            GWT.log( "HTMLInclude: bad code when fetching " + url + "[" + response.getStatusCode()
                + "]", null );
          }
        }
      } );
    } catch( RequestException e )
    {
      GWT.log( "HTMLInclude: exception thrown fetching " + url, e );
    }
  }
}
