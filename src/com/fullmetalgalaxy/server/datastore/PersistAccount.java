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
package com.fullmetalgalaxy.server.datastore;

import javax.persistence.Entity;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbBase;

/**
 * @author Vincent
 *
 */
@Entity
public class PersistAccount extends PersistEntity
{
  private String m_login = "";
  private String m_pseudo = "";
  private String m_password = "";
  private AuthProvider m_authProvider = AuthProvider.Fmg;

  public PersistAccount()
  {
  }

  @Override
  public void setEb(EbBase p_ebBase)
  {
    EbAccount account = EbAccount.class.cast( p_ebBase );
    if( account == null )
    {
      return;
    }
    setLogin( account.getLogin() );
    setPseudo( account.getPseudo() );
    account.setPseudo( null );
    setAuthProvider( account.getAuthProvider() );
    super.setEb( p_ebBase );
    account.setPseudo( getPseudo() );
  }

  public EbAccount getAccount()
  {
    EbBase base = getEb();
    EbAccount account = EbAccount.class.cast( base );
    if( account != null )
    {
      account.setPseudo( getPseudo() );
    }
    return account;
  }

  /**
   * @return the login
   */
  public String getLogin()
  {
    return m_login;
  }

  /**
   * @param p_login the login to set
   */
  protected void setLogin(String p_login)
  {
    m_login = p_login;
  }

  /**
   * @return the password
   */
  public String getPassword()
  {
    return m_password;
  }

  /**
   * @param p_password the password to set
   */
  public void setPassword(String p_password)
  {
    m_password = p_password;
  }

  /**
   * @return the authProvider
   */
  public AuthProvider getAuthProvider()
  {
    return m_authProvider;
  }

  /**
   * @param p_authProvider the authProvider to set
   */
  protected void setAuthProvider(AuthProvider p_authProvider)
  {
    m_authProvider = p_authProvider;
  }

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
  protected void setPseudo(String p_pseudo)
  {
    m_pseudo = p_pseudo;
  }


}
