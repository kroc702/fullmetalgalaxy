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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Vincent
 * 
 * this event is used to update "remember" string of a registration
 * 
 */
public class EbRemember extends AnEventPlay
{
  private static final long serialVersionUID = 1L;

  private String m_toRemember = null;
  private String m_oldRemember = null;

  public EbRemember()
  {
    super();
    init();
  }
  /**
   * 
   */
  public EbRemember(String p_toRemember)
  {
    super();
    init();
    m_toRemember = p_toRemember;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    setCost( 0 );
  }


  @Override
  public GameLogType getType()
  {
    return GameLogType.EbRememeber;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec( p_game );
    EbRegistration reg = p_game.getRegistration( getRegistrationId() );
    m_oldRemember = reg.getRemember();
    reg.setRemember( m_toRemember );
  }

  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec( p_game );
    EbRegistration reg = p_game.getRegistration( getRegistrationId() );
    reg.setRemember( m_oldRemember );
  }


}
