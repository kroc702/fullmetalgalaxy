/**
 * 
 */
package com.fullmetalgalaxy.client;


import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Kroc
 * GWT widget which implement this interface can be part of the root widget 
 * (usually the entry point class) to be refreshed every time the model or the HMI is modified.
 */
public class WgtView extends Composite implements ModelUpdateListener
{

  public WgtView()
  {
    super();
  }

  /**
   * this function is called every time the HMI change.
   * usually a new view or a windows resized event.
   */
  public void notifyHmiUpdate()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_modelSender)
  {
  }


}
