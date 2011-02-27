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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client;



import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * helpper class to implement MiniApp interface
 */
public abstract class MApp extends Composite implements MiniApp, ModelUpdateListener
{

  /**
   * 
   */
  public MApp()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return this;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  @Override
  public void hide()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  @Override
  public void show(HistoryState p_state)
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    if( !isVisible() )
    {
      return;
    }
    if( AppMain.instance() != null )
    {
      show( AppMain.instance().getHistoryState() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getHistoryId()
   */
  @Override
  public abstract String getHistoryId();


}
