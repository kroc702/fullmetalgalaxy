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
package com.fullmetalgalaxy.client.ressources;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vincent Legendre
 *
 */
public interface MessagesRpc extends Messages
{
  String freighter();

  String turret();

  String barge();

  String speedboat();

  String tank();

  String heap();

  String crab();

  String weatherHen();

  String pontoon();

  String ore();

  String low();

  String medium();

  String hight();

  String north();

  String north_east();

  String south_east();

  String south();

  String south_west();

  String north_west();

  String none();

  String sea();

  String reef();

  String marsh();

  String plain();

  String montain();

  String desert();

  String grass();

  String ice();

  String lava();

  String colorless();

  String blue();

  String cyan();

  String olive();

  String orange();

  String red();

  String green();

  String purple();

  String yellow();

  String grey();

  String tokenDescription(String p_color, String p_type);
}
