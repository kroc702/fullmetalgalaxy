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
package com.fullmetalgalaxy.server.forum;

import com.fullmetalgalaxy.server.EbAccount;

public interface ForumConector
{
  
  /**
   * 
   * @param p_string pseudo or username
   * @return id used to identify this forum account. or null if not found.
   */
  public String getUserId(String p_pseudo);
  
  /**
   * create corresponding account on forum
   * @param p_account
   * @return false if failed
   */
  public boolean createAccount(EbAccount p_account);
  
  /**
   * push account data from FMG to forum
   * @param p_account
   * @return false if failed
   */
  public boolean pushAccount(EbAccount p_account);
  
  /**
   * pull account data from forum to FMG
   * @param p_account
   * @return false if failed
   */
  public boolean pullAccount(EbAccount p_account);
  
  /**
   * send a private message using forum 
   * @param p_account
   * @param p_subject
   * @param p_body
   * @return false if failed
   */
  public boolean sendPMessage( String p_subject, String p_body, String ... p_usernames );
  

  
}
