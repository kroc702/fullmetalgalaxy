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


import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBean extends Composite implements BeanView
{
  private ChangeListenerCollection m_listenerCollection = new ChangeListenerCollection();
  private Object m_bean = null;


  public WgtBean()
  {
    super();
  }

  public void attachBean(Object p_bean)
  {
    if( m_bean != p_bean )
    {
      if( (m_bean != null) && (m_bean instanceof SourceModelUpdateEvents) )
      {
        ((SourceModelUpdateEvents)m_bean).removeModelUpdateEvent( this );
      }
      m_bean = p_bean;
      if( (m_bean != null) && (m_bean instanceof SourceModelUpdateEvents) )
      {
        ((SourceModelUpdateEvents)m_bean).subscribeModelUpdateEvent( this );
      }
    }
  }

  public void setReadOnly(boolean p_readOnly)
  {
  }

  public Object getObject()
  {
    return m_bean;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.ModelUpdateListener#onModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    attachBean( getObject() );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void fireChange()
  {
    m_listenerCollection.fireChange( this );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesChangeEvents#addChangeListener(com.google.gwt.user.client.ui.ChangeListener)
   */
  public void addChangeListener(ChangeListener p_listener)
  {
    m_listenerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesChangeEvents#removeChangeListener(com.google.gwt.user.client.ui.ChangeListener)
   */
  public void removeChangeListener(ChangeListener p_listener)
  {
    m_listenerCollection.remove( p_listener );
  }

}
