/**
 * 
 */
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface Misc extends Messages
{
  public Misc Messages = GWT.create( Misc.class );

  String shortDateFormat();

  String dateFormat();

  String dateTimeFormat();

  String timeFormat();

}
