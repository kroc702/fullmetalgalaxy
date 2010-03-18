/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBooleanBox extends CheckBox implements ScalarView, ClickListener, KeyboardListener
{
  private ChangeListenerCollection m_changeListnerCollection = new ChangeListenerCollection();
  private boolean m_oldValue = false;

  /**
   * 
   */
  public WgtBooleanBox()
  {
    init();
  }

  /**
   * @param p_label
   */
  public WgtBooleanBox(String p_label)
  {
    super( p_label );
    init();
  }

  /**
   * @param p_label
   * @param p_asHTML
   */
  public WgtBooleanBox(String p_label, boolean p_asHTML)
  {
    super( p_label, p_asHTML );
    init();
  }

  private void init()
  {
    setChecked( m_oldValue );
    addKeyboardListener( this );
    addClickListener( this );
  }

  public void setReadOnly(boolean p_readOnly)
  {
    setEnabled( !p_readOnly );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.ScalarView#setValue(java.lang.Object)
   */
  public void setScalarValue(Object p_value)
  {
    assert p_value != null;
    setChecked( ((Boolean)p_value) );
    m_oldValue = ((Boolean)p_value);
  }

  public boolean getBoolean()
  {
    return isChecked();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.test.AbstractView#getObject()
   */
  public Object getObject()
  {
    return getBoolean();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesChangeEvents#addChangeListener(com.google.gwt.user.client.ui.ChangeListener)
   */
  public void addChangeListener(ChangeListener p_listener)
  {
    m_changeListnerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesChangeEvents#removeChangeListener(com.google.gwt.user.client.ui.ChangeListener)
   */
  public void removeChangeListener(ChangeListener p_listener)
  {
    m_changeListnerCollection.remove( p_listener );

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyDown(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyDown(Widget p_wgt, char p_arg1, int p_arg2)
  {
    if( p_wgt == this )
    {
      mayFireChange();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyPress(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyPress(Widget p_wgt, char p_arg1, int p_arg2)
  {
    if( p_wgt == this )
    {
      mayFireChange();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.KeyboardListener#onKeyUp(com.google.gwt.user.client.ui.Widget, char, int)
   */
  public void onKeyUp(Widget p_wgt, char p_arg1, int p_arg2)
  {
    if( p_wgt == this )
    {
      mayFireChange();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_wgt)
  {
    if( p_wgt == this )
    {
      mayFireChange();
    }
  }

  private void mayFireChange()
  {
    if( m_oldValue != isChecked() )
    {
      m_oldValue = isChecked();
      m_changeListnerCollection.fireChange( this );
    }
  }
}
