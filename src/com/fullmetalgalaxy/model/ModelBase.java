/**
 * 
 */
package com.fullmetalgalaxy.model;

import java.io.Serializable;
import java.util.Date;


/**
 * @author Vincent Legendre
 * This class is the base class of a model. 
 */
public class ModelBase implements Serializable
{
  static final long serialVersionUID = 22;

  /**
   * Date and time of the last update.
   * this is the last server update as a server date. don't compare this date to a client date !
   */
  protected Date m_lastUpdate = new Date( 0 );

  /**
   * 
   */
  public ModelBase()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @return the lastUpdate as a client date.
   * this date is only for widget: do not request an update to the server with this date !
   */
  public Date getLastUpdate()
  {
    return m_lastUpdate;
  }

  /**
   * @param p_lastUpdate the lastUpdate to set
   */
  public void setLastUpdate(Date p_lastUpdate)
  {
    m_lastUpdate = p_lastUpdate;
  }


}
