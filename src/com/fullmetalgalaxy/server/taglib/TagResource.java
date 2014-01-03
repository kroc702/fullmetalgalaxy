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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server.taglib;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.fullmetalgalaxy.server.I18n;
import com.fullmetalgalaxy.server.LocaleFmg;

/**
 * @author Vincent
 *
 */
public class TagResource extends TagSupport
{
  private static final long serialVersionUID = 1L;

  private String m_key = null;



  @Override
  public int doStartTag() throws JspException
  {
    HttpServletRequest request = HttpServletRequest.class.cast( pageContext.getRequest() );
    HttpServletResponse response = HttpServletResponse.class.cast( pageContext.getResponse() );
    if( request != null && response != null && m_key != null )
    {
      LocaleFmg locale = LocaleFmg.fromString( I18n.getLocale( request, response ) );
      ResourceBundle res = ResourceBundle.getBundle( "jspResource", locale.locale() );
      JspWriter out = pageContext.getOut();
      try
      {
        String str = "key '" + getKey() + "' not found";
        try
        {
          str = res.getString( getKey() );
        } catch( Exception e )
        {
          e.printStackTrace();
        }
        out.print( str );
      } catch( IOException e )
      {
        e.printStackTrace();
      }
    }
    return SKIP_BODY;
  }



  public String getKey()
  {
    return m_key;
  }

  public void setKey(String p_key)
  {
    m_key = p_key;
  }



}
