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
 *  Copyright 2010 to 2016 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.FmgConstants;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 * 
 *         During the game join process, this dialog ask player to choose his
 *         color.
 */

public class DlgJoinChooseColor extends DialogBox
{
  private EbGameJoin account = null;

  // UI
  private ListBox m_companySelection = new ListBox();
  private Image m_companyPreview = new Image();
  private ListBox m_colorSelection = new ListBox();
  private Image m_colorPreview = new Image();

  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Panel m_panel = new VerticalPanel();

  private static DlgJoinChooseColor s_dlg = null;

  public static DlgJoinChooseColor instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgJoinChooseColor();
    }
    return s_dlg;
  }

  /**
   * 
   */
  public DlgJoinChooseColor()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( MAppBoard.s_messages.unitsTitle() );

    // add company list widget
    // =======================
    List<Company> freeCompany = new ArrayList<Company>();
    for( Company company : Company.values() )
    {
      if( company != Company.Freelancer )
      {
        freeCompany.add( company );
      }
    }
    if( !GameEngine.model().getGame().isTeamAllowed() )
    {
      // remove already chosen company
      for( EbTeam team : GameEngine.model().getGame().getTeams() )
      {
        if( team.getCompany() != null && team.getCompany() != Company.Freelancer )
        {
          freeCompany.remove( team.getCompany() );
        }
      }
      freeCompany.add( 0, Company.Freelancer );
    }
    else
    {
      m_panel.add( new HTML( "<b>" + MAppBoard.s_messages.warningTeamAllowed() + "</b>" ) );
      if( GameEngine.model().getGame().getMaxTeamAllowed() <= GameEngine.model().getGame()
          .getTeams().size() )
      {
        // player shouldn't choose other team
        freeCompany.clear();
        for( EbTeam team : GameEngine.model().getGame().getTeams() )
        {
          freeCompany.add( team.getCompany() );
        }
      }
    }

    for( Company company : freeCompany )
    {
      m_companySelection.addItem( company.getFullName(), company.toString() );
    }
    m_companySelection.setSelectedIndex( Random.nextInt( m_companySelection.getItemCount() ) );
    Company company = Company
        .valueOf( m_companySelection.getValue( m_companySelection.getSelectedIndex() ) );
    m_companyPreview.setUrl( FmgConstants.avatarFolderUri + company + ".jpg" );
    m_companySelection.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        Company company = Company
            .valueOf( m_companySelection.getValue( m_companySelection.getSelectedIndex() ) );
        m_companyPreview.setUrl( FmgConstants.avatarFolderUri + company + ".jpg" );
      }
    } );
    Panel hpanel = new HorizontalPanel();
    hpanel.add( m_companySelection );
    hpanel.add( m_companyPreview );
    m_panel.add( new HTML( "<b>" + MAppBoard.s_messages.chooseCompany() + "</b>" ) );
    m_panel.add( hpanel );

    // add color list widget
    // =====================
    Set<EnuColor> freeColors = null;
    if( GameEngine.model().getGame().getSetRegistration().size() >= GameEngine.model().getGame()
        .getMaxNumberOfPlayer() )
    {
      // this is a player replacement: don't allow company selection
      m_companySelection.setVisible( false );
      freeColors = GameEngine.model().getGame().getFreeRegistrationColors();
    }
    else
    {
      freeColors = GameEngine.model().getGame().getFreePlayersColors();
    }
    for( EnuColor color : freeColors )
    {
      if( color.getValue() != EnuColor.None )
      {
        m_colorSelection.addItem( Messages.getColorString( 0, color.getValue() ),
            "" + color.getValue() );
      }
    }
    m_colorSelection.setSelectedIndex( Random.nextInt( m_colorSelection.getItemCount() ) );
    // initialize company icon
    int colorValue = Integer
        .parseInt( m_colorSelection.getValue( m_colorSelection.getSelectedIndex() ) );
    EbRegistration registration = GameEngine.model().getGame().getRegistrationByColor( colorValue );
    if( registration != null && registration.getTeam( GameEngine.model().getGame() ) != null )
    {
      m_companyPreview.setUrl( FmgConstants.avatarFolderUri
          + registration.getTeam( GameEngine.model().getGame() ).getCompany() + ".jpg" );
    }
    // initialize color icon
    m_colorPreview.setUrl(
        FmgConstants.boardFolderUri + (new EnuColor( colorValue )).toString() + "/preview.jpg" );
    m_colorSelection.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        int colorValue = Integer
            .parseInt( m_colorSelection.getValue( m_colorSelection.getSelectedIndex() ) );
        EnuColor color = new EnuColor( colorValue );
        m_colorPreview.setUrl( FmgConstants.boardFolderUri + color.toString() + "/preview.jpg" );
        m_btnOk.setEnabled( true );
        // for replacement: search corresponding team
        EbRegistration registration = GameEngine.model().getGame()
            .getRegistrationByColor( colorValue );
        if( registration != null && registration.getTeam( GameEngine.model().getGame() ) != null )
        {
          m_companyPreview.setUrl( FmgConstants.avatarFolderUri
              + registration.getTeam( GameEngine.model().getGame() ).getCompany() + ".jpg" );
        }
      }
    } );
    hpanel = new HorizontalPanel();
    hpanel.add( m_colorSelection );
    hpanel.add( m_colorPreview );
    m_panel.add( new HTML( "<b>" + MAppBoard.s_messages.chooseColor() + "</b>" ) );
    m_panel.add( hpanel );

    // add buttons
    // ===========
    hpanel = new HorizontalPanel();
    // add cancel button
    m_btnCancel.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        hide();
      }
    } );
    hpanel.add( m_btnCancel );

    // add OK button
    m_btnOk.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        int colorValue = Integer
            .parseInt( m_colorSelection.getValue( m_colorSelection.getSelectedIndex() ) );
        EnuColor color = new EnuColor( colorValue );
        EbGameJoin action = getJoinEvent();
        Company company = Company.Freelancer;
        try
        {
          company = Company
              .valueOf( m_companySelection.getValue( m_companySelection.getSelectedIndex() ) );
        } catch( Exception e )
        {
        }
        action.setCompany( company );
        action.setGame( GameEngine.model().getGame() );
        action.setColor( color.getValue() );
        GameEngine.model().runSingleAction( action );
        hide();
      }
    } );
    hpanel.add( m_btnOk );
    m_panel.add( hpanel );

    setWidget( m_panel );
  }

  public EbGameJoin getJoinEvent()
  {
    if( account == null )
    {
      account = new EbGameJoin();
      account.setAccount( AppMain.instance().getMyAccount() );
    }
    return account;
  }

  public void setJoinEvent(EbGameJoin p_account)
  {
    account = p_account;
  }

  @Override
  public void hide()
  {
    account = null;
    super.hide();
  }
}
