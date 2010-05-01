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
package com.fullmetalgalaxy.client.home;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */

public class MAppGameList extends MApp implements TableListener, ClickHandler
{
  public static final String HISTORY_ID = "list";

  // history token
  public static final String s_TokenGameFiler = "filter";

  // UI
  private FlowPanel m_panel = new FlowPanel();
  private Grid m_gamePreviewGrid = new Grid( 1, 5 );
  private WgtBasicGameFilter m_wgtFilter = null;
  // Model
  private ArrayList<EbGamePreview> m_gamePreviewList = null;

  /**
   * 
   */
  public MAppGameList()
  {
    m_wgtFilter = new WgtBasicGameFilter( m_callbackGameList );
    // Put some values in the grid cells.
    m_gamePreviewGrid.setText( 0, 0, "" );
    m_gamePreviewGrid.setText( 0, 1, MAppHomeMenu.s_messages.gameName() );
    m_gamePreviewGrid.setText( 0, 2, MAppHomeMenu.s_messages.player() );
    m_gamePreviewGrid.setText( 0, 3, "Tour de..." );
    m_gamePreviewGrid.setText( 0, 4, MAppHomeMenu.s_messages.creator() );
    m_gamePreviewGrid.addTableListener( this );
    m_gamePreviewGrid.setStyleName( "fmp-home-gameLine" );
    m_gamePreviewGrid.addStyleName( "fmp-array" );
    m_gamePreviewGrid.getColumnFormatter().setWidth( 0, "32px" );

    m_gamePreviewGrid.getRowFormatter().addStyleName( 0, "fmp-home-gameline-caption" );
    // m_gamePreviewGrid.setBorderWidth( 2 );
    m_gamePreviewGrid.setWidth( "100%" );

    // construct main panel
    m_wgtFilter.setWidth( "100%" );
    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setWidget( m_gamePreviewGrid );
    scrollPanel.setSize( "100%", "100%" );
    m_gamePreviewGrid.setWidth( "100%" );
    m_panel.setSize( "100%", "100%" );
    m_panel.add( m_wgtFilter );
    m_panel.add( scrollPanel );

    initWidget( m_panel );
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
  }


  @Override
  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  @Override
  public void hide()
  {
    // TODO Auto-generated method stub

  }

  private boolean m_isLogged = false;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  @Override
  public void show(HistoryState p_state)
  {
    if( !ModelFmpMain.model().isLogged() )
    {
      m_isLogged = false;
      m_wgtFilter.getGameFilter().reinit();
      m_wgtFilter.getGameFilter().setStatus( GameStatus.Puzzle );
      m_wgtFilter.setVisible( false );
    }
    else if( m_isLogged == false )
    {
      m_isLogged = true;
      m_wgtFilter.getGameFilter().reinit();
      m_wgtFilter.getGameFilter().setPlayerName( ModelFmpMain.model().getMyPseudo() );
      m_wgtFilter.setVisible( true );
    }

    /*if( AppMain.instance().getHistoryState().containsKey( s_TokenGameFiler ) )
    {
      m_wgtFilter.getGameFilter().setType(
          AppMain.instance().getHistoryState().getInt( s_TokenGameFiler ) );
    }*/
    if( !m_wgtFilter.getGameFilter().equals( ModelFmpMain.model().getGameFilter() ) )
    {
      refreshList();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  @Override
  public Widget getTopWidget()
  {
    return this;
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.TableListener#onCellClicked(com.google.gwt.user.client.ui.SourcesTableEvents, int, int)
   */
  public void onCellClicked(SourcesTableEvents p_sender, int p_row, int p_cell)
  {
    EbGamePreview gamePreview = (EbGamePreview)m_gamePreviewList.get( p_row - 1 );
    AppMain.instance().gotoGame( gamePreview.getId() );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
  }


  private int m_resfreshCount = 0;

  /**
   * refresh game list and display it.
   */
  public void refreshList()
  {
    m_resfreshCount++;
    // (3) Create an asynchronous callback to handle the result.

    m_wgtFilter.resfreshGameList();
  }

  private AsyncCallback<List<EbGamePreview>> m_callbackGameList = new FmpCallback<List<EbGamePreview>>()
  {

    @Override
    public void onSuccess(List<EbGamePreview> p_result)
    {
      super.onSuccess( p_result );
      m_gamePreviewList = (ArrayList<EbGamePreview>)p_result;
      if( (m_gamePreviewList == null) || (m_gamePreviewList.size() == 0) )
      {
        if( m_resfreshCount < 2 )
        {
          refreshList();
        }
        else
        {
          // Window.alert( "We encouter some dificulty with server, sorry..."
          // );
          m_gamePreviewGrid.resizeRows( 1 );
        }
        AppMain.instance().stopLoading();
        return;
      }
      m_resfreshCount = 0;
      // do some UI stuff to show success
      int gameCount = m_gamePreviewList.size();
      m_gamePreviewGrid.resizeRows( gameCount + 1 );
      for( int i = 0; i < gameCount; i++ )
      {
        EbGamePreview gamePreview = (EbGamePreview)m_gamePreviewList.get( i );
        m_gamePreviewGrid.setWidget( i + 1, 0, Icons.s_instance.desert_planet_medium()
            .createImage() );
        m_gamePreviewGrid.setText( i + 1, 1, gamePreview.getName() );
        m_gamePreviewGrid.getCellFormatter().addStyleName( i + 1, 1, "gwt-Hyperlink" );
        m_gamePreviewGrid.setText( i + 1, 2, "" + gamePreview.getCurrentNumberOfRegiteredPlayer()
            + "/" + gamePreview.getMaxNumberOfPlayer() );
        if( gamePreview.isAsynchron() )
        {
          m_gamePreviewGrid.setText( i + 1, 3, "Asnchrone" );
        }
        else
        {
          m_gamePreviewGrid.setText( i + 1, 3, gamePreview.getLoginCurrentPlayer() );
        }
        m_gamePreviewGrid.setText( i + 1, 4, gamePreview.getLoginCreator() );
        if( i % 2 == 0 )
        {
          m_gamePreviewGrid.getRowFormatter().addStyleName( i, "fmp-home-gameline-odd" );
        }
        else
        {
          m_gamePreviewGrid.getRowFormatter().addStyleName( i, "fmp-home-gameline-even" );
        }

      }
      AppMain.instance().stopLoading();
    }

    @Override
    public void onFailure(Throwable p_caught)
    {
      super.onFailure( p_caught );
      AppMain.instance().stopLoading();
      if( (m_gamePreviewList == null) || (m_gamePreviewList.size() == 0) )
      {
        if( m_resfreshCount < 2 )
        {
          refreshList();
        }
        else
        {
          // Window.alert( "We encouter some dificulty with server, sorry..."
          // );
          m_gamePreviewGrid.resizeRows( 1 );
        }
        AppMain.instance().stopLoading();
        return;
      }
    }
  };


}
