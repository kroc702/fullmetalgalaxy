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
package com.fullmetalgalaxy.client.widget;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;

public class BindedWgtConfigGameTime extends com.fullmetalgalaxy.client.widget.WgtConfigGameTime
    implements ChangeListener
{

  public BindedWgtConfigGameTime()
  {
    super();
    addChangeListener();
  }

  public com.fullmetalgalaxy.model.persist.EbConfigGameTime getTypedBean()
  {
    return (com.fullmetalgalaxy.model.persist.EbConfigGameTime)getObject();
  }

  protected void addChangeListener()
  {
    getTimeStepDurationInSec().addChangeListener( this );
    getTideChangeFrequency().addChangeListener( this );
    getTotalTimeStep().addChangeListener( this );
    getActionPtPerTimeStep().addChangeListener( this );
    getActionPtPerExtraShip().addChangeListener( this );
    getDescription().addChangeListener( this );
  }

  @Override
  public void onChange(Widget p_sender)
  {
    if( getTypedBean() == null )
    {
      return;
    }
    else if( p_sender == getTimeStepDurationInSec() )
    {
      getTypedBean().setTimeStepDurationInSec( (Integer)getTimeStepDurationInSec().getObject() );
    }
    else if( p_sender == getTideChangeFrequency() )
    {
      getTypedBean().setTideChangeFrequency( (Integer)getTideChangeFrequency().getObject() );
    }
    else if( p_sender == getTotalTimeStep() )
    {
      getTypedBean().setTotalTimeStep( (Integer)getTotalTimeStep().getObject() );
    }
    else if( p_sender == getActionPtPerTimeStep() )
    {
      getTypedBean().setActionPtPerTimeStep( (Integer)getActionPtPerTimeStep().getObject() );
    }
    else if( p_sender == getActionPtPerExtraShip() )
    {
      getTypedBean().setActionPtPerExtraShip( (Integer)getActionPtPerExtraShip().getObject() );
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
    getTimeStepDurationInSec().setScalarValue( getTypedBean().getTimeStepDurationInSec() );
    getTideChangeFrequency().setScalarValue( getTypedBean().getTideChangeFrequency() );
    getTotalTimeStep().setScalarValue( getTypedBean().getTotalTimeStep() );
    getActionPtPerTimeStep().setScalarValue( getTypedBean().getActionPtPerTimeStep() );
    getActionPtPerExtraShip().setScalarValue( getTypedBean().getActionPtPerExtraShip() );
    getDescription().setScalarValue( getTypedBean().getDescription() );
  }

  @Override
  public void setReadOnly(boolean p_readOnly)
  {
    super.setReadOnly( p_readOnly );
    getTimeStepDurationInSec().setReadOnly( p_readOnly );
    getTideChangeFrequency().setReadOnly( p_readOnly );
    getTotalTimeStep().setReadOnly( p_readOnly );
    getActionPtPerTimeStep().setReadOnly( p_readOnly );
    getActionPtPerExtraShip().setReadOnly( p_readOnly );
    getDescription().setReadOnly( p_readOnly );
  }

}
