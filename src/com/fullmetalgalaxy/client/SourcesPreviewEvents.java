/**
 * 
 */
package com.fullmetalgalaxy.client;

import com.google.gwt.user.client.EventPreview;

/**
 * @author Vincent Legendre
 * generate event used by 'EventPreview' interface
 */
public interface SourcesPreviewEvents
{
  void addPreviewListener(EventPreview p_listener);

  void removePreviewListener(EventPreview p_listener);

}
