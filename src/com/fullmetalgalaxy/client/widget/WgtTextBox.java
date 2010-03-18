/**
 * 
 */
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Vincent Legendre
 *
 */
public class WgtTextBox extends TextBox implements ScalarView
{
  /**
   * 
   */
  public WgtTextBox()
  {
    super();
    setValue( "" );
  }

  public WgtTextBox(String p_text)
  {
    super();
    setValue( p_text );
  }

  protected String getString()
  {
    return getText();
  }

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
