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

public class BindedWgtGameInfo extends com.fullmetalgalaxy.client.widget.WgtGameInfo implements ChangeListener {
  
  public BindedWgtGameInfo()
  {
    super();
    addChangeListener();
  }
  
  public com.fullmetalgalaxy.model.persist.EbGame getTypedBean()
  {
    return (com.fullmetalgalaxy.model.persist.EbGame)getObject();
  }
  
  protected void addChangeListener()
  {
    getName().addChangeListener( this );
    getDescription().addChangeListener( this );
    getMaxNumberOfPlayer().addChangeListener( this );
    getLandWidth().addChangeListener( this );
    getLandHeight().addChangeListener( this );
    getCreationDate().setReadOnly( true );
    getConfigTime().setReadOnly( true );
  }
  
  @Override
  public void onChange(Widget p_sender)
  {
    if( getTypedBean() == null )
    {
      super.onChange( p_sender );
      return;
    }
    else if( p_sender == getName())
    {
      getTypedBean().setName( (java.lang.String)getName().getObject() );
    }
    else if( p_sender == getDescription())
    {
      getTypedBean().setDescription( (java.lang.String)getDescription().getObject() );
    }
    else if( p_sender == getMaxNumberOfPlayer())
    {
      getTypedBean().setMaxNumberOfPlayer( (Integer)getMaxNumberOfPlayer().getObject() );
    }
    else if( p_sender == getLandWidth())
    {
      getTypedBean().setLandWidth( (Integer)getLandWidth().getObject() );
    }
    else if( p_sender == getLandHeight())
    {
      getTypedBean().setLandHeight( (Integer)getLandHeight().getObject() );
    }
    super.onChange( p_sender );
  }
  
  @Override
  public void attachBean(Object p_bean)
  {
    super.attachBean( p_bean );
    if( getTypedBean() == null )
    {
      return;
    }
    getName().setScalarValue( getTypedBean().getName() );
    getDescription().setScalarValue( getTypedBean().getDescription() );
    getMaxNumberOfPlayer().setScalarValue( getTypedBean().getMaxNumberOfPlayer() );
    getLandWidth().setScalarValue( getTypedBean().getLandWidth() );
    getLandHeight().setScalarValue( getTypedBean().getLandHeight() );
    getCreationDate().setScalarValue( getTypedBean().getCreationDate() );
    getConfigTime().attachBean( getTypedBean().getEbConfigGameTime() );
  }
  
  @Override
  public void setReadOnly(boolean p_readOnly)
  {
    super.setReadOnly( p_readOnly );
    getName().setReadOnly( p_readOnly );
    getDescription().setReadOnly( p_readOnly );
    getMaxNumberOfPlayer().setReadOnly( p_readOnly );
    getLandWidth().setReadOnly( p_readOnly );
    getLandHeight().setReadOnly( p_readOnly );
  }
  
}
