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