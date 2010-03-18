/**
 * 
 */
package com.fullmetalgalaxy.model;



/**
 * @author Kroc
  */
public interface SourceModelUpdateEvents
{
  /**
   * to add a widget view which will be notified for every update.
   * @param p_view
   */
  public void subscribeModelUpdateEvent(ModelUpdateListener p_listener);

  public void removeModelUpdateEvent(ModelUpdateListener p_listener);
}
