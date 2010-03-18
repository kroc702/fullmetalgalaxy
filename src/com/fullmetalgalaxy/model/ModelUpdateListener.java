/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.util.EventListener;

/**
 * @author Kroc
 * To subscribe to model update event, the class must implements this interface.
 * @see CtrlModel
 */
public interface ModelUpdateListener extends EventListener
{
  /**
   * this function is called every time the model this widget is displaying, change.
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender);

}
