/**
 * 
 */
package com.fullmetalgalaxy.client.widget;

import com.fullmetalgalaxy.model.ModelUpdateListener;

/**
 * @author Vincent Legendre
 *
 */
public interface BeanView extends AbstractView, ModelUpdateListener
{
  public void attachBean(Object p_bean);

}
