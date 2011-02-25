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
/**
 * 
 */
package com.fullmetalgalaxy.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.ressources.Misc;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;

/**
 * @author Kroc
 * 
 * This static class give some utility functions for a client-side code written in GWT.
 */
public class ClientUtil
{

  private static native int jsGetNavigator() /*-{
		if (!$doc.styleSheets) {
			return -1;
		}
		if ($doc.styleSheets[0].cssRules) {
			return 0; // FF
		} else if ($doc.styleSheets[0].rules) {
			return 1; // IE
		}
		return -1;
  }-*/;

  public static int getNavigator()
  {
    try
    {
      synchronized( s_rules )
      {
        return jsGetNavigator();
      }
    } catch( Exception e )
    {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * 
   * @return language code or "" if no default language found.
   */
  public static native String getDefaultLanguage() /*-{
		var language = "";
		if (navigator.systemLanguage)
			language = navigator.systemLanguage;
		if (navigator.browserLanguage)
			language = navigator.browserLanguage;
		if (navigator.language)
			language = navigator.language;
		if (navigator.userLanguage)
			language = navigator.userLanguage;
		return language;
  }-*/;


  /**
   * 
   * @param p_url destination URL.
   */
  public static native void gotoUrl(String p_url) /*-{
		$wnd.location = p_url;
  }-*/;

  /**
   * 
   * @return the full URL.
   */
  public static native String getUrl() /*-{
		return $wnd.location.href;
  }-*/;

  // document.location.href;

  public static native void reload() /*-{
		$wnd.location.reload();
  }-*/;

  public static native void scrollToTop() /*-{
		$wnd.scrollTo(0, 0);
  }-*/;


  public static native void sendPM(String p_fromId, String p_toId, String p_subject, String p_msg) /*-{
		// post to /PMServlet
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "/PMServlet");
		form.setAttribute("enctype", "multipart/form-data");

		var hiddenField = document.createElement("input");
		hiddenField.setAttribute("type", "hidden");
		hiddenField.setAttribute("name", "fromid");
		hiddenField.setAttribute("value", p_fromId);
		form.appendChild(hiddenField);

		var hiddenField = document.createElement("input");
		hiddenField.setAttribute("type", "hidden");
		hiddenField.setAttribute("name", "toid");
		hiddenField.setAttribute("value", p_toId);
		form.appendChild(hiddenField);

		var hiddenField = document.createElement("input");
		hiddenField.setAttribute("type", "hidden");
		hiddenField.setAttribute("name", "subject");
		hiddenField.setAttribute("value", p_subject);
		form.appendChild(hiddenField);

		var hiddenField = document.createElement("input");
		hiddenField.setAttribute("type", "hidden");
		hiddenField.setAttribute("name", "msg");
		hiddenField.setAttribute("value", p_msg);
		form.appendChild(hiddenField);

		document.body.appendChild(form); // Not entirely sure if this is necessary
		form.submit();
  }-*/;

  /**
   * Disables the browsers default context menu for the specified element.
   *
   * @param elem the element whos context menu will be disabled
   */
  public static native void disableContextMenu(Element elem) /*-{
		elem.oncontextmenu = function() {
			return false
		};
  }-*/;


  /**
   * @param p_parameter the parameter you want to read
   * @return the parameter value contained in URL, or null if it doesn't exists.
   */
  public static String getUrlParameter(String p_parameter)
  {
    String url = getUrl();
    String param[] = url.split( "[?&]" );
    for( int i = 0; i < param.length; i++ )
    {
      if( param[i].startsWith( p_parameter ) )
      {
        return param[i].substring( p_parameter.length() + 1 );
      }
    }
    return null;
  }

  public static final DateTimeFormat s_dateTimeFormat = DateTimeFormat.getFormat( Misc.Messages
      .dateTimeFormat() );
  private static final DateTimeFormat s_shortDateFormat = DateTimeFormat.getFormat( Misc.Messages
      .shortDateFormat() );
  private static final DateTimeFormat s_timeFormat = DateTimeFormat.getFormat( Misc.Messages
      .timeFormat() );

  /**
   * a formated and localized string representing the given date or time if date is between
   * now and 24 hours later.
   * @param p_date
   * @return
   */
  public static String formatDateTime(Date p_date)
  {
    if( p_date == null )
    {
      return "";
    }
    long shift = p_date.getTime() - System.currentTimeMillis();
    if( (shift < 0) || (shift > 24 * 60 * 60 * 1000) )
    {
      return s_dateTimeFormat.format( p_date );
    }
    return s_timeFormat.format( p_date );
  }

  /**
   *  
   * @return
   */
  public static String getBaseUrl()
  {
    String baseUrl = GWT.getModuleBaseURL();
    int index = baseUrl.indexOf( GWT.getModuleName() );
    if( index > 0 )
    {
      baseUrl = baseUrl.substring( 0, index );
    }
    return baseUrl;
  }

  /**
   * determine is user is surfing on site or in game.
   * @return
   */
  public static boolean isInGame()
  {
    String url = Document.get().getURL();
    return url.contains( "board" );
  }


  private static native int jsNewCssRules(String p_ruleName, String p_rule) /*-{
		if (!$doc.styleSheets) {
			return 0;
		}
		var oldlength = 0;
		if ($doc.styleSheets[0].cssRules) {
			oldlength = $doc.styleSheets[0].cssRules.length;
			$doc.styleSheets[0]
					.insertRule(p_ruleName + ' ' + p_rule, oldlength);
		} else if ($doc.styleSheets[0].rules) {
			oldlength = $doc.styleSheets[0].rules.length;
			$doc.styleSheets[0].addRule(p_ruleName, p_rule);
		}
		return oldlength + 1;
  }-*/;

  private static native void jsSetCssRules(int p_ruleIndex, String p_rule) /*-{
		var theRules = new Array();
		if ($doc.styleSheets[0].cssRules)
			theRules = $doc.styleSheets[0].cssRules
		else if ($doc.styleSheets[0].rules)
			theRules = $doc.styleSheets[0].rules
		else
			return;
		theRules[p_ruleIndex].style = p_rule;
  }-*/;

  private static native void jsSetCssRules(int p_ruleIndex, String p_element, String p_value)/*-{
		var theRules = new Array();
		if ($doc.styleSheets[0].cssRules)
			theRules = $doc.styleSheets[0].cssRules
		else if ($doc.styleSheets[0].rules)
			theRules = $doc.styleSheets[0].rules
		else
			return;
		theRules[p_ruleIndex].style.setProperty(p_element, p_value, null);
		//theRules[p_ruleIndex].style.background-color = p_value;
  }-*/;

  private static Map<String, Integer> s_rules = new HashMap<String, Integer>();

  public static int getCssRuleIndex(String p_ruleName)
  {
    return s_rules.get( p_ruleName );
  }

  public static void setCssRule(int p_ruleIndex, String p_rule)
  {
    try
    {
      synchronized( s_rules )
      {
        jsSetCssRules( p_ruleIndex, p_rule );
      }
    } catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  public static int setCssRule(String p_ruleName, String p_rule)
  {
    int index = 0;
    synchronized( s_rules )
    {
      if( s_rules.containsKey( p_ruleName ) )
      {
        index = s_rules.get( p_ruleName );
        setCssRule( index, p_rule );
      }
      else
      {
        try
        {
          index = jsNewCssRules( p_ruleName, p_rule );
          s_rules.put( p_ruleName, index );
        } catch( Exception e )
        {
          e.printStackTrace();
        }
      }
    }
    return index;
  }

  /**
   * doesn't work :(
   * @param p_ruleIndex
   * @param p_element
   * @param p_value
   */
  protected static void setCssRule(int p_ruleIndex, String p_element, String p_value)
  {
    try
    {
      synchronized( s_rules )
      {
        jsSetCssRules( p_ruleIndex, p_element, p_value );
      }
    } catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  public static int setCssRule(String p_ruleName, String p_element, String p_value)
  {
    int index = 0;
    synchronized( s_rules )
    {
      if( s_rules.containsKey( p_ruleName ) )
      {
        index = s_rules.get( p_ruleName );
        setCssRule( index, p_element, p_value );
      }
      else
      {
        try
        {
          index = jsNewCssRules( p_ruleName, "{" + p_element + ": " + p_value + ";}" );
          s_rules.put( p_ruleName, index );
        } catch( Exception e )
        {
          e.printStackTrace();
        }
      }
    }
    return index;
  }

}
