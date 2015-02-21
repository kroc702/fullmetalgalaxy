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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent
 *
 */
public enum Company implements IsSerializable
{
  Freelancer, // No specific company or contract
  Digging, // digging inc - Pole juridique +++ technologie ++
  Stargate, // Stargate Corp - technologie +++ exploration ++
  TTC, // Tiberium Tuco Company - agressivity +++ exploration ++
  DSM, // Deep Space Mining - exploration +++ Pole juridique ++
  Ranger, // Ranger's Corp - agressivity +++ technologie ++
  // MIY, // Mine It Yourself Syndicate - Pole juridique +++ agressivity ++
  MDA; // Les Mineurs d'Astorg - Pole juridique +++ agressivity ++

  public String getFullName()
  {
    switch( this )
    {
    case Digging:
      return "Digging inc";
    case Stargate:
      return "Stargate Corp";
    case TTC:
      return "Tiberium Tuco Company";
    case DSM:
      return "Deep Space Mining";
    case Ranger:
      return "Ranger's Corp";
      // case MIY:
    case MDA:
      return "Les Mineurs d'Astorg";
    default:
    case Freelancer:
      return toString();
    }
  }
}
