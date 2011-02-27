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

import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;


/**
 * @author Vincent Legendre
 * it is the base class to represent any action which an account can do on to a game.
 */
public class AnEventUser extends AnEvent
{
  static final long serialVersionUID = 1;

  private long m_accountId = 0L;
  private String m_remoteAddr = null;

  /**
   * 
   */
  public AnEventUser()
  {
    super();
    init();
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_accountId = 0;
    m_remoteAddr = null;
  }

  public EbRegistration getMyRegistration(EbGame p_game)
  {
    if( p_game.getGameType() == GameType.MultiPlayer )
    {
      return p_game.getRegistrationByIdAccount( getAccountId() );
    }
    else if( p_game.getGameType() == GameType.Puzzle )
    {
      return p_game.getCurrentPlayerRegistration();
    }
    return null;
  }

  /**
   * check this action is allowed.
   * you have to override this method.
   * @throws RpcFmpException
   */
  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( ((getAccountId() == 0)) && (!isAuto())
 && (p_game.getGameType() != GameType.Puzzle) )
    {
      // TODO i18n
      throw new RpcFmpException( "Vous devez etre logger pour realiser cette action" );
    }
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String str = super.toString();
    str += "[" + getAccountId() + "]";
    return str;
  }


  /**
   * @return the account
   */
  public long getAccountId()
  {
    return m_accountId;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccountId(long p_id)
  {
    m_accountId = p_id;
  }

  /**
   * @return the remoteAddr
   */
  public String getRemoteAddr()
  {
    return m_remoteAddr;
  }

  /**
   * @param p_remoteAddr the remoteAddr to set
   */
  public void setRemoteAddr(String p_remoteAddr)
  {
    m_remoteAddr = p_remoteAddr;
  }


}
