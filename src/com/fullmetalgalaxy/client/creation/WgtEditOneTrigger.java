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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.fullmetalgalaxy.model.persist.triggers.actions.ActionClass;
import com.fullmetalgalaxy.model.persist.triggers.actions.AnAction;
import com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition;
import com.fullmetalgalaxy.model.persist.triggers.conditions.ConditionClass;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */

public class WgtEditOneTrigger extends Composite implements ClickHandler, ChangeListener
{
  // UI
  private ListBox m_lstConditions = new ListBox();
  private ListBox m_lstActions = new ListBox();
  private ListBox m_lstConditionsClass = new ListBox( false );
  private ListBox m_lstActionsClass = new ListBox( false );
  private Button m_btnNewAction = new Button( "Nouvelle Action" );
  private Button m_btnNewCondition = new Button( "Nouvelle Condition" );

  // model
  private EbTrigger m_trigger = null;

  /**
   * 
   */
  public WgtEditOneTrigger()
  {
    VerticalPanel vpanel = new VerticalPanel();
    m_lstConditions.addChangeListener( this );
    m_lstConditions.setVisibleItemCount( 10 );
    vpanel.add( m_lstConditions );

    m_lstConditionsClass.setVisibleItemCount( 1 );
    for( ConditionClass conditionClass : ConditionClass.values() )
    {
      m_lstConditionsClass.addItem( conditionClass.name() );
    }
    vpanel.add( m_lstConditionsClass );

    m_btnNewCondition.addClickHandler( this );
    vpanel.add( m_btnNewCondition );
    m_lstActions.addChangeListener( this );
    m_lstActions.setVisibleItemCount( 10 );
    vpanel.add( m_lstActions );

    m_lstActionsClass.setVisibleItemCount( 1 );
    for( ActionClass actionClass : ActionClass.values() )
    {
      m_lstActionsClass.addItem( actionClass.name() );
    }
    vpanel.add( m_lstActionsClass );

    m_btnNewAction.addClickHandler( this );
    vpanel.add( m_btnNewAction );

    initWidget( vpanel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( getTrigger() == null )
    {
      return;
    }
    if( p_event.getSource() == m_btnNewCondition )
    {
      AnCondition condition = ConditionClass.valueOf(
          m_lstConditionsClass.getItemText( m_lstConditionsClass.getSelectedIndex() ) )
          .newCondition();
      getTrigger().getConditions().add( condition );
      refreshConditions();
    }
    else if( p_event.getSource() == m_btnNewAction )
    {
      AnAction action = ActionClass.valueOf(
          m_lstActionsClass.getItemText( m_lstActionsClass.getSelectedIndex() ) ).newAction();
      getTrigger().getActions().add( action );
      refreshActions();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onChange(Widget p_sender)
  {
    // nothing to do
  }

  /**
   * @return the trigger
   */
  public EbTrigger getTrigger()
  {
    return m_trigger;
  }

  /**
   * @param p_trigger the trigger to load
   */
  public void loadTrigger(EbTrigger p_trigger)
  {
    m_trigger = p_trigger;
    refreshConditions();
    refreshActions();
  }

  private void refreshConditions()
  {
    m_lstConditions.clear();
    if( getTrigger() == null )
    {
      return;
    }
    for( AnCondition condition : getTrigger().getConditions() )
    {
      m_lstConditions.addItem( condition.toString() );
    }
  }

  private void refreshActions()
  {
    m_lstActions.clear();
    if( getTrigger() == null )
    {
      return;
    }
    for( AnAction action : getTrigger().getActions() )
    {
      m_lstActions.addItem( action.toString() );
    }
  }
}
