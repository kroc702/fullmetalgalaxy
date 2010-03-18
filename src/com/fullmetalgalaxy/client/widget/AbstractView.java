/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import com.google.gwt.user.client.ui.SourcesChangeEvents;

/**
 * @author Vincent Legendre
 *
 */
public interface AbstractView extends SourcesChangeEvents
{
  public Object getObject();

  public void setReadOnly(boolean p_readOnly);
}
