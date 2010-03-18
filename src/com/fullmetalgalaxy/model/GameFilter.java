/**
 * 
 */
package com.fullmetalgalaxy.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Vincent Legendre
 *
 */
public class GameFilter implements IsSerializable
{
  static final long serialVersionUID = 24;

  private GameStatus m_status = null;
  private String m_name = null;
  private String m_height = null;
  private String m_width = null;
  private String m_playerNumber = null;
  private PlanetType m_planetType = null;
  private String m_playerName = null;

  public GameFilter()
  {
  }

  public void reinit()
  {
    m_status = null;
    m_name = null;
    m_height = null;
    m_width = null;
    m_playerNumber = null;
    m_planetType = null;
    m_playerName = null;
  }


  public String getWhereClause()
  {
    String sql = "";
    String sqlPart = null;
    if( getStatus() == null )
    {
      sql += " history = false ";
    }
    else
    {
      switch( getStatus() )
      {
      case Open:
        sql += " history = false and started = false and currentNumberOfRegiteredPlayer < maxNumberOfPlayer and gameType = "
            + GameType.MultiPlayer.ordinal() + "";
        break;
      case Playing:
        sql += " history = false and started = true ";
        break;
      case Closed:
        sql += " history = true ";
        break;
      case Puzzle:
        sql += " gameType = " + GameType.Puzzle.ordinal() + " ";
        break;
      case Scenario:
        sql += " gameType = " + GameType.Scenario.ordinal() + " ";
        break;
      default:
        sql += " history = false ";
        break;
      }
    }
    if( m_playerName != null )
    {
      sqlPart = convert2SafeSql( m_playerName );
      sql += " and exists(from EbRegistration where game_id = g.id and account.login " + sqlPart
          + ")";
    }
    if( m_name != null )
    {
      sqlPart = convert2SafeSql( m_name );
      sql += " and name " + sqlPart;
    }
    if( m_height != null )
    {
      sqlPart = convert2SafeSql( m_height );
      sql += " and height " + sqlPart;
    }
    if( m_width != null )
    {
      sqlPart = convert2SafeSql( m_width );
      sql += " and width " + sqlPart;
    }
    if( m_playerNumber != null )
    {
      sqlPart = convert2SafeSql( m_playerNumber );
      sql += " and playerNumber " + sqlPart;
    }
    if( m_planetType != null )
    {
      sql += " and planetType = " + m_planetType.ordinal();
    }
    return sql;
  }

  /**
   * 
   * @param p_str
   */
  private String convert2SafeSql(String p_str)
  {
    p_str = p_str.trim();
    p_str = p_str.replace( '*', '%' );
    p_str = p_str.replace( "'", "''" );
    String operator = "like";
    if( p_str.startsWith( ">" ) || p_str.startsWith( "=" ) || p_str.startsWith( "<" ) )
    {
      operator = p_str.substring( 0, 1 );
      p_str = p_str.substring( 1 );
    }
    p_str = " " + operator + " '" + p_str + "' ";
    return p_str;
  }



  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_height == null) ? 0 : m_height.hashCode());
    result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
    result = prime * result + ((m_planetType == null) ? 0 : m_planetType.hashCode());
    result = prime * result + ((m_playerName == null) ? 0 : m_playerName.hashCode());
    result = prime * result + ((m_playerNumber == null) ? 0 : m_playerNumber.hashCode());
    result = prime * result + ((m_status == null) ? 0 : m_status.hashCode());
    result = prime * result + ((m_width == null) ? 0 : m_width.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    final GameFilter other = (GameFilter)obj;
    if( m_height == null )
    {
      if( other.m_height != null )
        return false;
    }
    else if( !m_height.equals( other.m_height ) )
      return false;
    if( m_name == null )
    {
      if( other.m_name != null )
        return false;
    }
    else if( !m_name.equals( other.m_name ) )
      return false;
    if( m_planetType == null )
    {
      if( other.m_planetType != null )
        return false;
    }
    else if( !m_planetType.equals( other.m_planetType ) )
      return false;
    if( m_playerName == null )
    {
      if( other.m_playerName != null )
        return false;
    }
    else if( !m_playerName.equals( other.m_playerName ) )
      return false;
    if( m_playerNumber == null )
    {
      if( other.m_playerNumber != null )
        return false;
    }
    else if( !m_playerNumber.equals( other.m_playerNumber ) )
      return false;
    if( m_status == null )
    {
      if( other.m_status != null )
        return false;
    }
    else if( !m_status.equals( other.m_status ) )
      return false;
    if( m_width == null )
    {
      if( other.m_width != null )
        return false;
    }
    else if( !m_width.equals( other.m_width ) )
      return false;
    return true;
  }

  public GameFilter newInstance()
  {
    GameFilter clone = new GameFilter();
    clone.setStatus( m_status );
    clone.setPlayerName( m_playerName );
    clone.setName( m_name );
    clone.setHeight( m_height );
    clone.setWidth( m_width );
    clone.setPlayerNumber( m_playerNumber );
    clone.setPlanetType( m_planetType );
    return clone;
  }

  /**
   * @return the status
   */
  public GameStatus getStatus()
  {
    return m_status;
  }

  /**
   * @param p_status the status to set
   */
  public void setStatus(GameStatus p_status)
  {
    m_status = p_status;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * @param p_name the name to set
   */
  public void setName(String p_name)
  {
    m_name = p_name;
  }

  /**
   * @return the height
   */
  public String getHeight()
  {
    return m_height;
  }

  /**
   * @param p_height the height to set
   */
  public void setHeight(String p_height)
  {
    m_height = p_height;
  }

  /**
   * @return the width
   */
  public String getWidth()
  {
    return m_width;
  }

  /**
   * @param p_width the width to set
   */
  public void setWidth(String p_width)
  {
    m_width = p_width;
  }

  /**
   * @return the playerNumber
   */
  public String getPlayerNumber()
  {
    return m_playerNumber;
  }

  /**
   * @param p_playerNumber the playerNumber to set
   */
  public void setPlayerNumber(String p_playerNumber)
  {
    m_playerNumber = p_playerNumber;
  }

  /**
   * @return the planetType
   */
  public PlanetType getPlanetType()
  {
    return m_planetType;
  }

  /**
   * @param p_planetType the planetType to set
   */
  public void setPlanetType(PlanetType p_planetType)
  {
    m_planetType = p_planetType;
  }

  /**
   * @return the playerName
   */
  public String getPlayerName()
  {
    return m_playerName;
  }

  /**
   * @param p_playerName the playerName to set
   */
  public void setPlayerName(String p_playerName)
  {
    m_playerName = p_playerName;
  }



}
