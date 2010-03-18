/**
 * 
 */
package com.fullmetalgalaxy.model;

import com.fullmetalgalaxy.model.pathfinder.PathMobile;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;

/**
 * @author Vincent Legendre
 * 
 */
public class Mobile implements PathMobile
{
  EbToken m_token = null;
  EbRegistration m_registration = null;

  public Mobile(EbRegistration p_registration, EbToken p_token)
  {
    m_token = p_token;
    m_registration = p_registration;
  }

  /**
   * @return the token
   */
  public EbToken getToken()
  {
    return m_token;
  }

  /**
   * @return the registration
   */
  public EbRegistration getRegistration()
  {
    return m_registration;
  }


}
