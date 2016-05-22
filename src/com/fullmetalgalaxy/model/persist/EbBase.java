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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.persist;

import javax.persistence.Id;



/**
 * @author Vincent Legendre
 * This is the base class for any entity bean. 
 */
public class EbBase extends AnPojoBase
{
  private static final long serialVersionUID = 3533417036557857816L;

  @Id
  private Long id = null;

  /**
   * default constructor, call the init method.
   */
  public EbBase()
  {
    super();
    init();
  }

  public EbBase(Long p_id)
  {
    this();
    setId( p_id );
  }

  public EbBase(String p_id)
  {
    this();
    setId( Long.parseLong( p_id ) );
  }

  public EbBase(EbBase p_base)
  {
    super();
    init();
    if( p_base != null )
    {
      // setKey( p_base.getKey() );
    }
  }

  /**
   * @return
   * @WgtHidden
   */
  public EbBase createEbBase()
  {
    EbBase base = new EbBase();
    base.setId( getId() );
    return base;
  }

  /**
   * initialization of the bean.<br/>
   * Do not call 'super.init()' in subclass to avoid calling init() several time during instanciation.<br/>
   * This method is only called by the constructor and reinit() witch both are called by superclass.
   */
  private void init()
  {
    id = null;
  }

  /**
   * To re-initialize the bean.<br/>
   * It simply call init() method, but it can be called by overloaded reinit without any risk of calling
   * init() several times.
   */
  @Override
  public void reinit()
  {
    this.init();
  }


  /**
   * Compare id and last update.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object p_arg)
  {
    if( this == p_arg )
    {
      return true;
    }
    if( !(p_arg instanceof EbBase) )
    {
      return false;
    }
    if( isTrancient() || ((EbBase)p_arg).isTrancient() )
    {
      return false;
    }
    if( getId() == 0 || ((EbBase)p_arg).getId() == 0 )
    {
      return false;
    }
    return(getId() == ((EbBase)p_arg).getId());
  }


  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    /* if I use the timestamp, then method hashtable.remove on client side (only !)
     * doesn't work.
     */
    /*if( getLastUpdate() != null )
    {
      return (int)(getId() * getLastUpdate().getTime());
    }
    else
    {*/
      return super.hashCode();
    // return (int)(getId());
    // }
  }

  /**
   * if this token idn't save 'as this' in data base.
   * @return false if id==0 or lastUpdate==null
   */
  public boolean isTrancient()
  {
    return (id == null) || id == 0;
  }

  public void setTrancient()
  {
    id = null;
  }

  /**
   * this method is usefull to synchronize the id/lastUpdate between
   * client and server.
   * @param p_update
   */
  public void updateFrom(EbBase p_update)
  {
    setId( p_update.getId() );
  }

  public long getId()
  {
    if( id == null )
    {
      return 0;
    }
    return id;
  }

  public void setId(long p_id)
  {
    id = p_id;
    if( id == 0 )
    {
      id = null;
    }
  }


}
