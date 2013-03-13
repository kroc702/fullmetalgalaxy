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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.tabmenu;


import java.util.Iterator;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.widget.EventPresenter;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtChangePlayerOrder;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Vincent Legendre
 *
 */
public class WgtGameLogs extends Composite implements SelectionHandler<TreeItem>
{
  private Tree m_tree = new Tree();
  private int m_additionalEventCount = 0;

  /**
   * 
   */
  public WgtGameLogs()
  {
    ScrollPanel panel = new ScrollPanel();
    panel.setStyleName( "fmp-log-panel" );
    panel.add( m_tree );
    m_tree.addSelectionHandler( this );
    initWidget( panel );
    redraw();
  }

  /**
   * construct tree HMI from game event list
   */
  public void redraw()
  {
    m_tree.clear();
    if( GameEngine.model().getGame().getAdditionalEventCount() > 0 )
    {
      m_tree.addItem( new TreeItemAdditionalEvent( GameEngine.model().getGame()
          .getAdditionalEventCount() ) );
    }
    m_additionalEventCount = GameEngine.model().getGame().getAdditionalEventCount();
    if( GameEngine.model().getGame().getEbConfigGameTime().isParallel() )
    {
      buildTree4Parallel();
    }
    else
    {
      buildTree4Tbt();
    }
    if( GameEngine.model().getGame()
        .isTimeStepParallelHidden( GameEngine.model().getGame().getCurrentTimeStep() ) )
    {
      addParallelHiddenEvent();
    }
  }


  private void addParallelHiddenEvent()
  {
    EbRegistration myRegistration = GameEngine.model().getMyRegistration();
    if( myRegistration != null && !myRegistration.getMyEvents().isEmpty() )
    {
      TreeItem turnTreeItem = new TreeItem( myRegistration.getAccount().getPseudo() );
      m_tree.addItem( turnTreeItem );
      for( AnEvent event : myRegistration.getMyEvents() )
      {
        turnTreeItem.addItem( new TreeItemEvent( event ) );
      }
    }
  }


  private void buildTree4Tbt()
  {
    int currentTurn = 0;
    Iterator<AnEvent> iterator = GameEngine.model().getGame().getLogs().iterator();
    TreeItem turnTreeItem = null;
    if( GameEngine.model().getGame().getAdditionalEventCount() > 0 )
    {
      turnTreeItem = new TreeItem( "tour ?" );
    }
    else
    {
      turnTreeItem = new TreeItem( "inscriptions" );
      m_tree.addItem( turnTreeItem );

      // game starting
      while( iterator.hasNext() )
      {
        AnEvent event = iterator.next();
        turnTreeItem.addItem( new TreeItemEvent( event ) );
        if( event instanceof EbEvtChangePlayerOrder )
        {
          currentTurn++;
          turnTreeItem = new TreeItem( "tour " + currentTurn );
          m_tree.addItem( turnTreeItem );
          break;
        }
      }
    }
    // game turn
    int evtPlayerTurnCount = 0;
    int playerCount = GameEngine.model().getGame().getCurrentNumberOfRegiteredPlayer();
    TreeItem playerTreeItem = null;
    while( iterator.hasNext() )
    {
      AnEvent event = iterator.next();
      if( playerTreeItem == null )
      {
        EbRegistration registration = null;
        String playerPseudo = null;
        if( event instanceof AnEventPlay )
        {
          registration = GameEngine.model().getGame()
              .getRegistrationByIdAccount( ((AnEventPlay)event).getAccountId() );
        }
        else if( event instanceof EbEvtPlayerTurn && !event.isAuto() )
        {
          registration = GameEngine.model().getGame()
              .getRegistrationByIdAccount( ((EbEvtPlayerTurn)event).getAccountId() );
        }
        if( registration != null )
        {
          playerPseudo = registration.getAccount().getPseudo();
        }
        if( playerPseudo != null )
        {
          playerTreeItem = new TreeItemEvent( event );
          playerTreeItem.setText( playerPseudo );
          turnTreeItem.addItem( playerTreeItem );
        }
      }
      if( playerTreeItem != null )
      {
        playerTreeItem.addItem( new TreeItemEvent( event ) );
      }
      else
      {
        turnTreeItem.addItem( new TreeItemEvent( event ) );
      }
      if( event instanceof EbEvtPlayerTurn )
      {
        playerTreeItem = null;
        evtPlayerTurnCount++;
        if( evtPlayerTurnCount >= playerCount )
        {
          // its probably a new turn
          evtPlayerTurnCount = 0;
          currentTurn++;
          turnTreeItem = new TreeItemEvent( event );
          turnTreeItem.setText( "tour " + currentTurn );
          m_tree.addItem( turnTreeItem );
        }
      }
      if( event instanceof EbEvtControlFreighter )
      {
        if( ((EbEvtControlFreighter)event).getOldRegistration( GameEngine.model().getGame() ) != null
            && ((EbEvtControlFreighter)event).getOldRegistration( GameEngine.model().getGame() )
            .getColor() == EnuColor.None )
        {
          playerCount--;
        }
      }
    }
  }

  private void buildTree4Parallel()
  {
    Iterator<AnEvent> iterator = GameEngine.model().getGame().getLogs().iterator();
    if( GameEngine.model().getGame().getAdditionalEventCount() == 0 )
    {
      TreeItem turnTreeItem = new TreeItem( "inscriptions" );
      m_tree.addItem( turnTreeItem );

      // game starting
      while( iterator.hasNext() )
      {
        AnEvent event = iterator.next();
        turnTreeItem.addItem( new TreeItemEvent( event ) );
        if( event instanceof EbAdminTimePlay )
        {
          break;
        }
      }
    }
    // game time step
    TreeItemEvent dateTreeItem = null;
    while( iterator.hasNext() )
    {
      AnEvent event = iterator.next();
      if( dateTreeItem == null
          || event.getLastUpdate().getDate() != dateTreeItem.getEvent().getLastUpdate().getDate() )
      {
        dateTreeItem = new TreeItemEvent( event );
        dateTreeItem.setHTML( "<img src='/images/css/calendar.png'/> "
            + EventPresenter.getDate( event ) );
        m_tree.addItem( dateTreeItem );
      }
      dateTreeItem.addItem( new TreeItemEvent( event ) );
    }
  }

  @Override
  public void onSelection(SelectionEvent<TreeItem> p_event)
  {
    if( p_event.getSelectedItem() instanceof TreeItemEvent )
    {
      GameEngine.model().timePlay( ((TreeItemEvent)p_event.getSelectedItem()).getEvent() );
    }
    if( p_event.getSelectedItem() instanceof TreeItemAdditionalEvent )
    {
      GameEngine.model().loadAdditionalEvents();
    }
  }

  public int getAdditionalEventCount()
  {
    return m_additionalEventCount;
  }



}
