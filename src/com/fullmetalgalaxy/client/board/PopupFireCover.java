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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */

public class PopupFireCover extends PopupPanel implements ModelUpdateListener, ClickHandler
{
  private Label m_lblAll = new Label( "Tous" );
  private Label m_lblNone = new Label( "Aucun" );
  private Panel m_vPanel = new VerticalPanel();

  public PopupFireCover()
  {
    // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
    // If this is set, the panel closes itself automatically when the user
    // clicks outside of it.
    super( true );

    setStyleName( "gwt-DialogBox" );

    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    m_lblAll.addClickHandler( this );
    m_lblNone.addClickHandler( this );

    // PopupPanel is a SimplePanel, so you have to set it's widget property to
    // whatever you want its contents to be.
    setWidget( m_vPanel );
  }


  /**
   * list of game registration and their associated label
   */
  private Map<HTML, EbRegistration> m_map = new HashMap<HTML, EbRegistration>();

  private long m_gameId = 0;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    ModelFmpMain model = (ModelFmpMain)p_ModelSender;
    if( (m_gameId != model.getGame().getId())
        || (m_map.size() != model.getGame().getSetRegistration().size()) )
    {
      m_gameId = model.getGame().getId();
      m_map.clear();
      m_vPanel.clear();

      m_vPanel.add( m_lblAll );
      m_vPanel.add( m_lblNone );

      for( EbRegistration registration : model.getGame().getSetRegistration() )
      {
        HTML label = new HTML( ModelFmpMain.model().getAccount( registration.getAccountId() )
            .getPseudo() );
        label.addClickHandler( this );
        m_map.put( label, registration );
        m_vPanel.add( label );
      }
    }

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(ClickEvent p_event)
  {
    HistoryState state = AppMain.instance().getHistoryState();
    String[] oldFireCover = state.getStringArray( MAppBoard.s_TokenFireCover );
    String[] newFireCover = new String[m_map.size()];
    boolean isChanged = false;

    if( p_event.getSource() == m_lblAll )
    {
      if( oldFireCover.length != m_map.size() )
      {
        isChanged = true;
      }
      int i = 0;
      for( Map.Entry<HTML, EbRegistration> entry : m_map.entrySet() )
      {
        newFireCover[i] = "" + entry.getValue().getId();
        i++;
      }
      state.setStringArray( MAppBoard.s_TokenFireCover, newFireCover );
    }
    else if( p_event.getSource() == m_lblNone )
    {
      if( oldFireCover.length != 0 )
      {
        isChanged = true;
      }
      state.removeKey( MAppBoard.s_TokenFireCover );
    }
    else
    {
      EbRegistration registration = (EbRegistration)m_map.get( p_event.getSource() );
      assert registration != null;
      boolean isAdded = false;
      int i = 0;
      while( i < oldFireCover.length )
      {
        newFireCover[i] = oldFireCover[i];
        if( Long.parseLong( newFireCover[i] ) == registration.getId() )
        {
          isAdded = true;
        }
        i++;
      }
      if( !isAdded )
      {
        newFireCover[i] = "" + registration.getId();
        isChanged = true;
      }
      state.setStringArray( MAppBoard.s_TokenFireCover, newFireCover );
    }
    if( isChanged )
    {
      History.newItem( state.toString() );
    }
    hide();
  }
}
