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
import com.google.gwt.user.client.ui.Widget;

public class BindedWgtConfigGameVariant extends
    com.fullmetalgalaxy.client.widget.WgtConfigGameVariant implements ChangeListener
{

  public BindedWgtConfigGameVariant()
  {
    super();
    addChangeListener();
  }

  public com.fullmetalgalaxy.model.persist.EbConfigGameVariant getTypedBean()
  {
    return (com.fullmetalgalaxy.model.persist.EbConfigGameVariant)getObject();
  }

  protected void addChangeListener()
  {
    getActionPtMaxReserve().addChangeListener( this );
    getMinSpaceBetweenFreighter().addChangeListener( this );
    getDeployementRadius().addChangeListener( this );
    getDescription().addChangeListener( this );
  }

  @Override
  public void onChange(Widget p_sender)
  {
    if( getTypedBean() == null )
    {
      return;
    }
    else if( p_sender == getActionPtMaxReserve() )
    {
      getTypedBean().setActionPtMaxReserve( (Integer)getActionPtMaxReserve().getObject() );
    }
    else if( p_sender == getMinSpaceBetweenFreighter() )
    {
      getTypedBean().setMinSpaceBetweenFreighter(
          (Integer)getMinSpaceBetweenFreighter().getObject() );
    }
    else if( p_sender == getDeployementRadius() )
    {
      getTypedBean().setDeployementRadius( (Integer)getDeployementRadius().getObject() );
    }
    else if( p_sender == getDescription() )
    {
      getTypedBean().setDescription( (java.lang.String)getDescription().getObject() );
    }
  }

  @Override
  public void attachBean(Object p_bean)
  {
    super.attachBean( p_bean );
    if( getTypedBean() == null )
    {
      return;
    }
    getActionPtMaxReserve().setScalarValue( getTypedBean().getActionPtMaxReserve() );
    getMinSpaceBetweenFreighter().setScalarValue( getTypedBean().getMinSpaceBetweenFreighter() );
    getDeployementRadius().setScalarValue( getTypedBean().getDeploymentRadius() );
    getDescription().setScalarValue( getTypedBean().getDescription() );
  }

  @Override
  public void setReadOnly(boolean p_readOnly)
  {
    super.setReadOnly( p_readOnly );
    getActionPtMaxReserve().setReadOnly( p_readOnly );
    getMinSpaceBetweenFreighter().setReadOnly( p_readOnly );
    getDeployementRadius().setReadOnly( p_readOnly );
    getDescription().setReadOnly( p_readOnly );
  }

}
