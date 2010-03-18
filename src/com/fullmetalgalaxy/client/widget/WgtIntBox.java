/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Vincent Legendre
 *
 */
public class WgtIntBox extends WgtTextBox implements ChangeListener
{
  /**
   * 
   */
  public WgtIntBox()
  {
    super();
    addChangeListener( this );
    setScalarValue( new Integer( 0 ) );
  }

  public WgtIntBox(Number p_number)
  {
    super();
    addChangeListener( this );
    setScalarValue( p_number );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.NumberView#getValue()
   */
  public Number getNumber()
  {
    return Integer.parseInt( getText() );
  }

  public Object getObject()
  {
    return getNumber();
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
        getNumber().intValue();
      } catch( NumberFormatException ex )
      {
        setText( "0" );
      }
    }
  }


}
