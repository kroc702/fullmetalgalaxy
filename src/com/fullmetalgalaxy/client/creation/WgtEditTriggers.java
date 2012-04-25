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

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */

public class WgtEditTriggers extends Composite implements ClickHandler, ChangeListener
{
  private int m_triggerCount = 0;
  private Map<String, EbTrigger> m_mapTrigger = new HashMap<String, EbTrigger>();

  // UI
  private ListBox m_lstTrigger = new ListBox( false );
  private Button m_btnNewTrigger = new Button( "Nouveau Trigger" );
  private WgtEditOneTrigger m_wgtTrigger = new WgtEditOneTrigger();


  /**
   * 
   */
  public WgtEditTriggers()
  {
    VerticalPanel vpanel = new VerticalPanel();
    m_lstTrigger.addChangeListener( this );
    m_lstTrigger.setVisibleItemCount( 10 );
    vpanel.add( m_lstTrigger );
    m_btnNewTrigger.addClickHandler( this );
    vpanel.add( m_btnNewTrigger );

    HorizontalPanel panel = new HorizontalPanel();
    panel.add( vpanel );
    panel.add( m_wgtTrigger );


    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnNewTrigger )
    {
      EbTrigger trigger = new EbTrigger();
      trigger.setName( "trigger " + m_triggerCount );
      m_triggerCount++;
      GameEngine.model().getGame().getTriggers().add( trigger );
      refreshTriggerList();
      m_lstTrigger.setSelectedIndex( GameEngine.model().getGame().getTriggers().size() - 1 );
      selectTrigger( trigger );
    }

  }

  public void refreshTriggerList()
  {
    m_mapTrigger = new HashMap<String, EbTrigger>();
    int selectedIndex = m_lstTrigger.getSelectedIndex();
    m_lstTrigger.clear();
    for( EbTrigger trigger : GameEngine.model().getGame().getTriggers() )
    {
      m_lstTrigger.addItem( trigger.getName() );
      m_mapTrigger.put( trigger.getName(), trigger );
    }
    m_lstTrigger.setSelectedIndex( selectedIndex );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onChange(Widget p_sender)
  {
    if( p_sender == m_lstTrigger )
    {
      selectTrigger( m_mapTrigger.get( m_lstTrigger.getItemText( m_lstTrigger.getSelectedIndex() ) ) );
    }

  }

  private void selectTrigger(EbTrigger p_trigger)
  {
    m_wgtTrigger.loadTrigger( p_trigger );
  }

}
