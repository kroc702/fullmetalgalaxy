/**
 * 
 */
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
