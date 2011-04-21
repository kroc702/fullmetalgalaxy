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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;

/**
 * @author Vincent
 *
 */
public class EbAdminBan extends EbAdmin
{
  private static final long serialVersionUID = 1L;

  private long m_registrationId = -1;
  private EbPublicAccount m_oldAccount = null;

  /**
   * 
   */
  public EbAdminBan()
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_registrationId = -1;
    m_oldAccount = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.AdminBan;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check( p_game );
    // only admin or game creator sould be able to do this
    // TODO how to check my account is admin ?
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec( p_game );
    p_game.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    EbRegistration registration = p_game.getRegistration( getRegistrationId() );
    if( registration != null )
    {
      // backup to keep track of this action
      m_oldAccount = registration.getAccount();

      registration.setAccount( null );
      registration.setEndTurnDate( null );
      
      // update isOpen flag
      p_game.isOpen();
    }

  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += " : " + m_oldAccount.getPseudo() + "(" + m_oldAccount.getId() + ")";
    return str;
  }

  // Bean getter / setter
  // ====================
  /**
   * @return the registrationId
   */
  public long getRegistrationId()
  {
    return m_registrationId;
  }

  /**
   * @param p_registrationId the registrationId to set
   */
  public void setRegistrationId(long p_registrationId)
  {
    m_registrationId = p_registrationId;
  }


}
