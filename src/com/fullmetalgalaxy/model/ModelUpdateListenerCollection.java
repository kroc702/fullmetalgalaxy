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
package com.fullmetalgalaxy.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Kroc
 * A helper class for implementers of the SourcesChangeEvents interface. This subclass of ArrayList assumes that
 * all objects added to it will be of type ModelUpdateListener.
 */
public class ModelUpdateListenerCollection extends ArrayList<ModelUpdateListener>
{
  static final long serialVersionUID = 1;

  public ModelUpdateListenerCollection()
  {
  }


  public void fireModelUpdate(SourceModelUpdateEvents p_sender)
  {
    ModelUpdateListener listener = null;
    for( Iterator<ModelUpdateListener> it = iterator(); it.hasNext(); )
    {
      listener = it.next();
      try
      {
        listener.onModelUpdate( p_sender );
      } catch( Exception e )
      {
        RpcUtil.logError( "the listener " + listener.getClass()
            + " bug while notify a model update", e );
      }
    }
  }

}
