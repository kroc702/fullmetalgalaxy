/**
 * 
 */
package com.fullmetalgalaxy.model.persist;



/**
 * @author Vincent Legendre
 * This is the base class for any entity bean. 
 */
public class EbBase extends AnPojoBase
{
  private static final long serialVersionUID = 3533417036557857816L;

  private long m_id = 0;

  private long m_version = 0;

  /**
   * default constructor, call the init method.
   */
  public EbBase()
  {
    super();
    init();
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
    m_id = 0;
  }

  /**
   * To re-initialize the bean.<br/>
   * It simply call init() method, but it can be called by overloaded reinit without any risk of calling
   * init() several times.
   */
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
    return super.hashCode(); // (int)(getId());
    // }
  }

  /**
   * if this token idn't save 'as this' in data base.
   * @return false if id==0 or lastUpdate==null
   */
  // @Transient
  public boolean isTrancient()
  {
    return(getId() == 0);
  }

  // @Transient
  public void setTrancient()
  {
    setId( 0 );
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
    return m_id;
  }

  /**
   * @return the version
   */
  public long getVersion()
  {
    return m_version;
  }

  /**
   * @param p_version the version to set
   */
  public void setVersion(long p_version)
  {
    m_version = p_version;
  }


  public void setId(long p_id)
  {
    m_id = p_id;
  }

  public void incVersion()
  {
    m_version++;
  }

  public void decVersion()
  {
    m_version--;
  }


}
