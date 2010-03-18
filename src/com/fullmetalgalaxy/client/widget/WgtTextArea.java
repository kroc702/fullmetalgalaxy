/**
 * 
 */
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author Vincent Legendre
 *
 */
public class WgtTextArea extends TextArea implements ScalarView
{

  /**
   * 
   */
  public WgtTextArea()
  {
    super();
    setValue( "" );
  }

  protected String getString()
  {
    return getText();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.widget.AbstractView#getObject()
   */
  public Object getObject()
  {
    return getString();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.ModelUpdateListener#onModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    setValue( getString() );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.BeanView#attachBean(java.lang.Object)
   */
  public void setScalarValue(Object p_bean)
  {
    if( p_bean == null )
    {
      setText( "" );
    }
    else
    {
      setText( p_bean.toString() );
    }
  }

}
