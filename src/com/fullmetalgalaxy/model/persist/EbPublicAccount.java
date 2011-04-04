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
package com.fullmetalgalaxy.model.persist;

import com.fullmetalgalaxy.model.constant.FmpConstant;




/**
 * @author Kroc
 * Account data that other people are allowed to see.
 */
public class EbPublicAccount extends EbBase
{
  private static final long serialVersionUID = 1L;

  // theses data come from database (Account table)
  // -------------------------------------------
  private String m_pseudo = "";

  public EbPublicAccount()
  {
    super();
    init();
  }

  public EbPublicAccount(EbPublicAccount p_account)
  {
    super();
    init();
    
    setId( p_account.getId() );
    setPseudo( p_account.getPseudo() );
  }
  

  private void init()
  {
    m_pseudo = "";
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }



  public boolean isEmpty()
  {
    return getId() == 0;
  }

  public String getAvatarUrl()
  {
      return FmpConstant.getBaseUrl() + "/images/avatar-default.jpg";
  }



  // getters / setters
  // -----------------
  /**
   * @return the pseudo
   */
  public String getPseudo()
  {
    return m_pseudo;
  }


  /**
   * @param p_pseudo the pseudo to set
   */
  public void setPseudo(String p_pseudo)
  {
    m_pseudo = p_pseudo;
  }



}
