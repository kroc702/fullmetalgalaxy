/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
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
