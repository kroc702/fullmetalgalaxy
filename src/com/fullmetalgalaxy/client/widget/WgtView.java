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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.google.gwt.user.client.ui.Composite;

/**
 * TODO get rid of this legacy class
 * 
 * @author Kroc
 * GWT widget which implement this interface can be part of the root widget 
 * (usually the entry point class) to be refreshed every time the model or the HMI is modified.
 */
public abstract class WgtView extends Composite implements ModelUpdateEvent.Handler
{

}
