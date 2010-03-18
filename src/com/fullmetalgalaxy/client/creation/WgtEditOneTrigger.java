/**
 * 
 */
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.fullmetalgalaxy.model.persist.triggers.actions.ActionClass;
import com.fullmetalgalaxy.model.persist.triggers.actions.AnAction;
import com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition;
import com.fullmetalgalaxy.model.persist.triggers.conditions.ConditionClass;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtEditOneTrigger extends Composite implements ClickListener, ChangeListener
{
  // UI
  private ListBox m_lstConditions = new ListBox();
  private ListBox m_lstActions = new ListBox();
  private ListBox m_lstConditionsClass = new ListBox();
  private ListBox m_lstActionsClass = new ListBox();
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

    m_lstConditionsClass.setMultipleSelect( false );
    m_lstConditionsClass.setVisibleItemCount( 1 );
    for( ConditionClass conditionClass : ConditionClass.values() )
    {
      m_lstConditionsClass.addItem( conditionClass.name() );
    }
    vpanel.add( m_lstConditionsClass );

    m_btnNewCondition.addClickListener( this );
    vpanel.add( m_btnNewCondition );
    m_lstActions.addChangeListener( this );
    m_lstActions.setVisibleItemCount( 10 );
    vpanel.add( m_lstActions );

    m_lstActionsClass.setMultipleSelect( false );
    m_lstActionsClass.setVisibleItemCount( 1 );
    for( ActionClass actionClass : ActionClass.values() )
    {
      m_lstActionsClass.addItem( actionClass.name() );
    }
    vpanel.add( m_lstActionsClass );

    m_btnNewAction.addClickListener( this );
    vpanel.add( m_btnNewAction );

    initWidget( vpanel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( getTrigger() == null )
    {
      return;
    }
    if( p_sender == m_btnNewCondition )
    {
      AnCondition condition = ConditionClass.valueOf(
          m_lstConditionsClass.getItemText( m_lstConditionsClass.getSelectedIndex() ) )
          .newCondition();
      getTrigger().getConditions().add( condition );
      refreshConditions();
    }
    else if( p_sender == m_btnNewAction )
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
  public void onChange(Widget p_sender)
  {
    // TODO Auto-generated method stub

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
