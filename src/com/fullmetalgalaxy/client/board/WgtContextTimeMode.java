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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */
public class WgtContextTimeMode extends Composite implements ClickHandler
{
  private Panel m_panel = new VerticalPanel();

  Label m_lblTimePosition = new Label();
  
  Image m_btnFastBack = Icons.s_instance.fastBack32().createImage();
  Image m_btnFastPlay = Icons.s_instance.fastPlay32().createImage();
  Image m_btnBack = Icons.s_instance.back32().createImage();
  Image m_btnPlay = Icons.s_instance.play32().createImage();

  Image m_btnOk = Icons.s_instance.ok32().createImage();
  Image m_btnCancel = Icons.s_instance.cancel32().createImage();


  /**
   * 
   */
  public WgtContextTimeMode()
  {
    super();
    
    m_btnFastBack.addClickHandler( this );
    m_btnFastBack.setTitle( "" );
    m_btnFastBack.setStyleName( "fmp-button" );
    m_btnFastPlay.addClickHandler( this );
    m_btnFastPlay.setTitle( "" );
    m_btnFastPlay.setStyleName( "fmp-button" );
    m_btnBack.addClickHandler( this );
    m_btnBack.setTitle( "" );
    m_btnBack.setStyleName( "fmp-button" );
    m_btnPlay.addClickHandler( this );
    m_btnPlay.setTitle( "" );
    m_btnPlay.setStyleName( "fmp-button" );

    m_btnCancel.addClickHandler( this );
    m_btnCancel.setTitle( "Annuler" );
    m_btnCancel.setStyleName( "fmp-button" );
    m_btnOk.addClickHandler( this );
    m_btnOk.setTitle( "Ok" );
    m_btnOk.setStyleName( "fmp-button" );

    initWidget( m_panel );
  }

  public void redraw()
  {
    assert ModelFmpMain.model() != null;
    Game game = ModelFmpMain.model().getGame();

    m_panel.clear();
    
    m_lblTimePosition.setText( ModelFmpMain.model().getCurrentActionIndex() + "/" + game.getLogs().size() );
    m_panel.add( m_lblTimePosition );
        
    Panel panel = new FlowPanel();
    panel.add( m_btnFastBack );
    panel.add( m_btnBack );
    panel.add( m_btnPlay );
    panel.add( m_btnFastPlay );
    m_panel.add( panel );
    
    panel = new FlowPanel();
    if( ModelFmpMain.model().canCancelAction() )
    {
      // in puzzle or turn by turn on several day we allow cancel action
      panel.add( m_btnOk );
    }
    panel.add( m_btnCancel );
    m_panel.add( panel );
    
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    Object sender = p_event.getSource();
    if( sender == m_btnCancel )
    {
      ModelFmpMain.model().setTimeLineMode( false );
    }
    if( sender == m_btnOk )
    {
      // just in case another action was in preparation
      EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
      actionBuilder.clear();
      EbEvtCancel evtCancel = new EbEvtCancel();
      evtCancel.setGame( ModelFmpMain.model().getGame() );
      evtCancel.setFromActionIndex( ModelFmpMain.model().getGame() );
      evtCancel.setToActionIndex( ModelFmpMain.model().getCurrentActionIndex() );
      evtCancel.setAccountId( ModelFmpMain.model().getMyAccount().getId() );
      ModelFmpMain.model().setTimeLineMode( false );
      ModelFmpMain.model().runSingleAction( evtCancel );
    }
    else if( sender == m_btnPlay )
    {
      ModelFmpMain.model().timePlay( 1 );
    }
    else if( sender == m_btnFastPlay )
    {
      ModelFmpMain.model().timePlay( 10 );
    }
    else if( sender == m_btnBack )
    {
      ModelFmpMain.model().timeBack( 1 );
    }
    else if( sender == m_btnFastBack )
    {
      ModelFmpMain.model().timeBack( 10 );
    }
  }


}
