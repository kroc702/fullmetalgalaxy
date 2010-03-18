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
