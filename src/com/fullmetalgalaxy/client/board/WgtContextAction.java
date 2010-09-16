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


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */

public class WgtContextAction extends WgtView implements ClickHandler
{
  HorizontalPanel m_panel = new HorizontalPanel();
  Image m_btnOk = Icons.s_instance.ok32().createImage();
  Image m_btnCancel = Icons.s_instance.cancel32().createImage();
  Image m_btnRepairTurret = Icons.s_instance.repair32().createImage();
  Image m_btnFire = Icons.s_instance.shoot32().createImage();
  Image m_btnControl = Icons.s_instance.control32().createImage();
  Image m_btnFireCoverOn = Icons.s_instance.fireCoverOn32().createImage();
  Image m_btnFireCoverOff = Icons.s_instance.fireCoverOff32().createImage();
  Image m_btnEndTurn = Icons.s_instance.endTurn32().createImage();
  Image m_btnZoomIn = Icons.s_instance.zoomIn32().createImage();
  Image m_btnZoomOut = Icons.s_instance.zoomOut32().createImage();
  Image m_btnGrid = Icons.s_instance.grid32().createImage();
  Image m_btnRegister = Icons.s_instance.register32().createImage();
  FocusPanel m_pnlRegister = null;
  Image m_btnInfo = Icons.s_instance.info32().createImage();
  Image m_btnTakeOff = Icons.s_instance.takeOff32().createImage();
  Image m_btnTimeMode = Icons.s_instance.time32().createImage();
  Image m_btnMiniMap = Icons.s_instance.map32().createImage();
  Image m_btnPlayer = Icons.s_instance.player32().createImage();
  Image m_iconAction = Icons.s_instance.action16().createImage();
  Label m_lblAction = new Label( "" );

  private DlgGameDetail m_dlgGameDetail = new DlgGameDetail();

  /**
   * 
   */
  public WgtContextAction()
  {
    m_btnOk.addClickHandler( this );
    m_btnOk.setTitle( "Valider l'action" );
    m_btnOk.setStyleName( "fmp-button" );
    m_btnCancel.addClickHandler( this );
    m_btnCancel.setTitle( "Annuler l'action [ESC]" );
    m_btnCancel.setStyleName( "fmp-button" );
    m_btnRepairTurret.addClickHandler( this );
    m_btnRepairTurret.setTitle( "Reparer la tourelle" );
    m_btnRepairTurret.setStyleName( "fmp-button" );
    m_btnTakeOff.addClickHandler( this );
    m_btnTakeOff.setTitle( "Decollage" );
    m_btnTakeOff.setStyleName( "fmp-button" );
    m_btnFire.addClickHandler( this );
    m_btnFire.setTitle( "Tirer" );
    m_btnFire.setStyleName( "fmp-button" );
    m_btnControl.addClickHandler( this );
    m_btnControl.setTitle( "Controle" );
    m_btnControl.setStyleName( "fmp-button" );
    m_btnFireCoverOn.addClickHandler( this );
    m_btnFireCoverOn.setTitle( "Afficher les couvertures de feux [F]" );
    m_btnFireCoverOn.setStyleName( "fmp-button" );
    m_btnFireCoverOff.addClickHandler( this );
    m_btnFireCoverOff.setTitle( "Cacher les couvertures de feux [F]" );
    m_btnFireCoverOff.setStyleName( "fmp-button" );
    m_btnEndTurn.addClickHandler( this );
    m_btnEndTurn.setTitle( "Fin de tour" );
    m_btnEndTurn.setStyleName( "fmp-button" );
    m_btnZoomIn.addClickHandler( this );
    m_btnZoomIn.setTitle( "Zoom tactique [+]" );
    m_btnZoomIn.setStyleName( "fmp-button" );
    m_btnZoomOut.addClickHandler( this );
    m_btnZoomOut.setTitle( "Zoom strategique [-]" );
    m_btnZoomOut.setStyleName( "fmp-button" );
    m_btnGrid.addClickHandler( this );
    m_btnGrid.setTitle( "Afficher/cacher la grille [G]" );
    m_btnGrid.setStyleName( "fmp-button" );
    m_btnRegister.addClickHandler( this );
    m_btnRegister.setTitle( "S'inscrire a cette partie" );
    m_btnRegister.setStyleName( "fmp-button" );
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.register32().createImage() );
    hPanel.add( new Label( "Cette partie recherche des joueurs. Inscrivez vous !" ) );
    m_pnlRegister = new FocusPanel( hPanel );
    m_pnlRegister.addClickHandler( this );

    m_btnInfo.addClickHandler( this );
    m_btnInfo.setTitle( "Information detailles sur cette partie" );
    m_btnInfo.setStyleName( "fmp-button" );
    m_btnTimeMode.addClickHandler( this );
    m_btnTimeMode.setTitle( "Annuler ou voir les coups precedents" );
    m_btnTimeMode.setStyleName( "fmp-button" );
    m_btnMiniMap.addClickHandler( this );
    m_btnMiniMap.setTitle( "Affichage de la minimap" );
    m_btnMiniMap.setStyleName( "fmp-button" );
    m_btnPlayer.addClickHandler( this );
    m_btnPlayer.setTitle( "Joueurs connectes" );
    m_btnPlayer.setStyleName( "fmp-button" );
    m_iconAction.setTitle( "Cout en point d'action" );
    m_lblAction.setStyleName( "fmp-status-text" );

    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    m_panel.setSize( "100%", "100%" );
    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    m_panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    initWidget( m_panel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    Object sender = p_event.getSource();
    try
    {
      EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
      if( sender == m_btnOk )
      {
        actionBuilder.userOk();
        ModelFmpMain.model().runCurrentAction();
      }
      else if( sender == m_btnCancel )
      {
        actionBuilder.userCancel();
        ModelFmpMain.model().notifyModelUpdate();
      }
      else if( sender == m_btnRepairTurret )
      {
        EventBuilderMsg eventBuilderMsg = actionBuilder.userAction( GameLogType.EvtRepair );
        if( eventBuilderMsg == EventBuilderMsg.MustRun )
        {
          ModelFmpMain.model().runSingleAction( actionBuilder.getSelectedAction() );
        }
      }
      else if( sender == m_btnFire )
      {
        actionBuilder.userAction( GameLogType.EvtFire );
        MAppMessagesStack.s_instance
            .showMessage( "Selectionez un second destructeur a porte, puis votre cible" );
      }
      else if( sender == m_btnControl )
      {
        actionBuilder.userAction( GameLogType.EvtControl );
        MAppMessagesStack.s_instance
            .showMessage( "Selectionez un second destructeur au contact, puis votre cible" );
      }
      else if( sender == m_btnFireCoverOn )
      {
        ModelFmpMain.model().setFireCoverDisplayed( true );
      }
      else if( sender == m_btnFireCoverOff )
      {
        ModelFmpMain.model().setFireCoverDisplayed( false );
      }
      else if( sender == m_btnGrid )
      {
        ModelFmpMain.model().setGridDisplayed( !ModelFmpMain.model().isGridDisplayed() );
      }
      else if( sender == m_btnZoomIn )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Medium );
      }
      else if( sender == m_btnZoomOut )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Small );
      }
      else if( sender == m_btnMiniMap )
      {
        ModelFmpMain.model().setMiniMapDisplayed( true );
      }
      else if( sender == m_btnPlayer )
      {
        ModelFmpMain.model().setMiniMapDisplayed( false );
      }
      else if( sender == m_btnInfo )
      {
        m_dlgGameDetail.center();
        m_dlgGameDetail.show();
      }
      else if( sender == m_btnTimeMode )
      {
        ModelFmpMain.model().setTimeLineMode( !ModelFmpMain.model().isTimeLineMode() );
      }
      else if( sender == m_btnRegister || sender == m_pnlRegister )
      {
        DlgJoinGame dlg = new DlgJoinGame();
        dlg.show();
        dlg.center();
      }
      else if( sender == m_btnEndTurn )
      {
        if( Window.confirm( "Il vous reste "
                + ModelFmpMain.model().getMyRegistration().getPtAction()
                + " points d'action. Confirmez-vous la fin de tour ?" ) )
        {
          EbEvtPlayerTurn action = new EbEvtPlayerTurn();
          action.setGame( ModelFmpMain.model().getGame() );
          action.setAccountId( ModelFmpMain.model().getMyAccountId() );
          ModelFmpMain.model().runSingleAction( action );
        }
      }
      else if( sender == m_btnTakeOff )
      {
        if( Window.confirm( "Confirmez-vous le decolage de "
            + Messages.getTokenString( actionBuilder.getSelectedToken() ) + " ?" ) )
        {
          EbEvtTakeOff action = new EbEvtTakeOff();
          action.setGame( ModelFmpMain.model().getGame() );
          action.setAccountId( ModelFmpMain.model().getMyAccountId() );
          action.setToken( actionBuilder.getSelectedToken() );
          action.setOldPosition( actionBuilder.getSelectedToken().getPosition() );
          ModelFmpMain.model().runSingleAction( action );
        }
      }
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( Messages.getString( e ) );
    }
  }

  protected void redraw()
  {
    m_panel.clear();
    ModelFmpMain model = ModelFmpMain.model();
    if( (model == null) )
    {
      return;
    }

    EventsPlayBuilder action = ModelFmpMain.model().getActionBuilder();
    EbToken mainToken = action.getSelectedToken();

    if( !action.isTokenSelected() || ModelFmpMain.model().getGame().isFinished() )
    {
      // add standard actions icon set
      if( ModelFmpMain.model().isFireCoverDisplayed() )
      {
        m_panel.add( m_btnFireCoverOff );
      }
      else
      {
        m_panel.add( m_btnFireCoverOn );
      }
      // m_panel.add( m_btnGrid );
      if( ModelFmpMain.model().getZoomDisplayed().getValue() == EnuZoom.Small )
      {
        m_panel.add( m_btnZoomIn );
      }
      else
      {
        m_panel.add( m_btnZoomOut );
      }
      m_panel.add( m_btnInfo );
      m_panel.add( m_btnTimeMode );
      if( !ModelFmpMain.model().isTimeLineMode() )
      {
        if( ModelFmpMain.model().isMiniMapDisplayed() )
        {
          if( model.getGame().getGameType() == GameType.MultiPlayer )
          {
            m_panel.add( m_btnPlayer );
          }
        }
        else
        {
          m_panel.add( m_btnMiniMap );
        }
        if( (!ModelFmpMain.model().getGame().isAsynchron())
            && (ModelFmpMain.model().getMyRegistration() != null)
            && (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == ModelFmpMain
                .model().getMyRegistration()) )
        {
          m_panel.add( m_btnEndTurn );
        }
        if( ModelFmpMain.model().isLogged() && ModelFmpMain.model().getMyRegistration() == null
            && !ModelFmpMain.model().getGame().isStarted() )
        {
          m_panel.add( m_btnRegister );
          MAppMessagesStack.s_instance.showMessage( m_pnlRegister );
        }
      }
    }
    else if( ModelFmpMain.model().getMyRegistration() == null )
    {
      m_panel.add( m_btnCancel );
    }
    else if( action.isRunnable() )
    {
      m_panel.add( m_btnOk );
      m_panel.add( m_iconAction );
      m_lblAction.setText( String.valueOf( ModelFmpMain.model().getActionBuilder().getCost() ) );
      m_panel.add( m_lblAction );
      m_panel.add( m_btnCancel );
    }
    else
    {
      try
      {
        action.check();
      } catch( RpcFmpException e )
      {
        if( (e.getMessage() != null) && !(e.getMessage().trim().length() == 0) )
        {
          MAppMessagesStack.s_instance.showWarning( e.getMessage() );
        }
      }

      // add current actions icon set
      if( (action.isTokenSelected()) || (action.isActionsPending())
          || (action.getActionList().size() != 0) )
      {
        // add cancel button
        m_panel.add( m_btnCancel );
      }

      if( ((ModelFmpMain.model().getGame().getTokenFireLength( action.getSelectedToken() ) > 0) 
          || (action.getSelectedToken().getType() == TokenType.Freighter && ModelFmpMain.model().getGame().getToken( action.getSelectedPosition(), TokenType.Turret ) != null))
          && (ModelFmpMain.model().getMyRegistration().getEnuColor().isColored( action
              .getSelectedToken().getColor() )) )
      {
        try
        {
          action.exec();
          if( (action.getSelectedToken().getBulletCount() > 0 
              && !action.getSelectedToken().isFireDisabled())
           || (action.getSelectedToken().getType() == TokenType.Freighter ) )
          {
            m_panel.add( m_btnFire );
          }
          if( action.getSelectedToken().haveOponentNeighbor() )
          {
            m_panel.add( m_btnControl );
          }
        } catch( RpcFmpException e )
        {
        } finally
        {
          try
          {
            action.unexec();
          } catch( RpcFmpException e )
          {
          }
        }
      }

      if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
          && (mainToken.getType() == TokenType.Freighter) && (model.getMyRegistration() != null)
          && (model.getMyRegistration().getTurretsToRepair() > 0)
          && (model.getMyRegistration().getPtAction() >= 2)
          && (!mainToken.getEnuColor().isColored( model.getMyRegistration().getOriginalColor() ))
          && (model.getMyRegistration().getEnuColor().isColored( mainToken.getColor() ))
          && (model.getGame().getToken( action.getSelectedPosition(), TokenType.Turret ) == null)
          && (!mainToken.getPosition().equals( action.getSelectedPosition() )) )
      {
        // player select a destroyed pod. of a freighter he own (but different
        // from the original one)
        // add the repair turret button
        m_panel.add( m_btnRepairTurret );
      }

      if( (action.isBoardTokenSelected())
          && (!action.isActionsPending())
          && (mainToken.getType() == TokenType.Freighter)
          && (model.getGame().getAllowedTakeOffTurns().contains( model.getGame()
              .getCurrentTimeStep() ))
          && (mainToken.getEnuColor().isColored( model.getMyRegistration().getColor() )) )
      {
        m_panel.add( m_btnTakeOff );
      }
    }

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  @Override
  public void notifyHmiUpdate()
  {
    redraw();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    redraw();
  }


}
