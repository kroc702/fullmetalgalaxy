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

package com.fullmetalgalaxy.model.persist;

import java.util.Date;

import javax.persistence.Embedded;

import com.googlecode.objectify.annotation.Unindexed;


/**
 * @author vincent legendre
 *
 * Base class for all comments :
 * - on account
 * - in games 
 * - ... ?
 */
public class EbCommentBase extends EbBase
{
  private static final long serialVersionUID = 1L;

  @Embedded
  private EbPublicAccount m_fromAcount = null;
  @Unindexed
  private String m_comment = "";
  private Date m_date = new Date();
}
