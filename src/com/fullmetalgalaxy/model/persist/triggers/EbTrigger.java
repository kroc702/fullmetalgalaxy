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
package com.fullmetalgalaxy.model.persist.triggers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.triggers.actions.AnAction;
import com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition;


/**
 * @author Vincent Legendre
 *
 */
public class EbTrigger extends EbBase
{
  static final long serialVersionUID = 120;

  private Set<com.fullmetalgalaxy.model.persist.triggers.actions.AnAction> m_actions = new HashSet<com.fullmetalgalaxy.model.persist.triggers.actions.AnAction>();

  private Set<com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition> m_conditions = new HashSet<com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition>();

  private boolean m_enable = true;
  private boolean m_autoDisable = true;
  private boolean m_andOperator = false;
  private String m_name = "";

  /**
   * 
   */
  public EbTrigger()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbTrigger(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
    m_actions = new HashSet<com.fullmetalgalaxy.model.persist.triggers.actions.AnAction>();
    m_conditions = new HashSet<com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition>();
    m_enable = true;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.EbBase#setTrancient()
   */
  @Override
  public void setTrancient()
  {
    super.setTrancient();
    for( AnCondition condition : getConditions() )
    {
      condition.setTrancient();
    }
    setConditions( new HashSet<AnCondition>( getConditions() ) );
    Set<AnAction> actList = new HashSet<AnAction>();
    for( AnAction action : getActions() )
    {
      action.setTrancient();
      actList.add( action );
    }
    setActions( actList );
  }

  // main Trigger interface
  // ----------------------
  /**
   * If at least one condition is true, execute all actions.
   * @return the list of all executed events.
   */
  public List<AnEvent> createEvents(Game p_game)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    List<Object> params = new ArrayList<Object>();
    boolean isVerify = false;
    if( isAndOperator() )
    {
      isVerify = true;
    }
    if( isEnable() )
    {
      for( AnCondition condition : getConditions() )
      {
        if( condition.isVerify( p_game ) )
        {
          if( !isAndOperator() )
          {
            isVerify = true;
            break;
          }
          params.addAll( condition.getActParams( p_game ) );
        }
        else
        {
          if( isAndOperator() )
          {
            isVerify = false;
            break;
          }
        }
      }
      if( isVerify )
      {
        for( AnAction action : getActions() )
        {
          events.addAll( action.createEvents( p_game, params ) );
        }
        if( isAutoDisable() )
        {
          setEnable( false );
        }
        return events;
      }
    }
    return events;
  }

  // getters / setters
  // -----------------
  /**
   * @return the actions
   */
  public Set<com.fullmetalgalaxy.model.persist.triggers.actions.AnAction> getActions()
  {
    return m_actions;
  }

  /**
   * @param p_actions the actions to set
   */
  protected void setActions(Set<com.fullmetalgalaxy.model.persist.triggers.actions.AnAction> p_actions)
  {
    m_actions = p_actions;
  }

  /**
   * @return the conditions
   */
  public Set<com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition> getConditions()
  {
    return m_conditions;
  }

  /**
   * @param p_conditions the conditions to set
   */
  protected void setConditions(
      Set<com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition> p_conditions)
  {
    m_conditions = p_conditions;
  }

  /**
   * @return the enable
   */
  public boolean isEnable()
  {
    return m_enable;
  }

  /**
   * @param p_enable the enable to set
   */
  public void setEnable(boolean p_enable)
  {
    m_enable = p_enable;
  }

  /**
   * if trigger have the autoDisable flag set, it turn itself disable after the first
   * true condition encountered. 
   * @return the autoDisable
   */
  public boolean isAutoDisable()
  {
    return m_autoDisable;
  }

  /**
   * @param p_autoDisable the autoDisable to set
   */
  public void setAutoDisable(boolean p_autoDisable)
  {
    m_autoDisable = p_autoDisable;
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
   * determine if actions set are combined with an AND or an OR operator
   * @return the and
   */
  public boolean isAndOperator()
  {
    return m_andOperator;
  }

  /**
   * @param p_and the and to set
   */
  public void setAndOperator(boolean p_and)
  {
    m_andOperator = p_and;
  }



}
