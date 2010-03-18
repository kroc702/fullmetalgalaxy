/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import java.util.Date;


import com.fullmetalgalaxy.client.ClientUtil;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Vincent Legendre
 *
 */
public class WgtDateBox extends WgtTextBox implements ChangeListener
{
  /**
   * 
   */
  public WgtDateBox()
  {
    super();
    addChangeListener( this );
    setScalarValue( new Date() );
  }

  public WgtDateBox(Date p_date)
  {
    super();
    addChangeListener( this );
    setScalarValue( p_date );
  }

  public void setScalarValue(Object p_bean)
  {
    if( (p_bean != null) && (p_bean instanceof Date) )
    {
      setText( ClientUtil.s_dateTimeFormat.format( (Date)p_bean ) );
    }
    else
    {
      super.setScalarValue( p_bean );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.NumberView#getValue()
   */
  public Date getDate()
  {
    return ClientUtil.s_dateTimeFormat.parse( getText() );
  }

  public Object getObject()
  {
    return getDate();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void onChange(Widget p_sender)
  {
    if( p_sender == this )
    {
      try
      {
        getDate();
      } catch( IllegalArgumentException ex )
      {
        setScalarValue( new Date() );
      }
    }
  }


}
