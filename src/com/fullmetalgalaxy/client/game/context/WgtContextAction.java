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
package com.fullmetalgalaxy.client.game.context;


import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.board.DlgJoinGame;
import com.fullmetalgalaxy.client.game.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogFactory;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author Vincent Legendre
 *
 */

public class WgtContextAction extends WgtView implements ClickHandler
{
  HorizontalPanel m_panel = new HorizontalPanel();
  PushButton m_btnOk = new PushButton( Icons.s_instance.ok32().createImage() );
  PushButton m_btnCancel = new PushButton( Icons.s_instance.cancel32().createImage() );
  PushButton m_btnRepairTurret = new PushButton( Icons.s_instance.repair32().createImage() );
  PushButton m_btnFire = new PushButton( Icons.s_instance.shoot32().createImage() );
  PushButton m_btnControl = new PushButton( Icons.s_instance.control32().createImage() );
  PushButton m_btnFireCoverOn = new PushButton( Icons.s_instance.fireCoverOn32().createImage() );
  PushButton m_btnFireCoverOff = new PushButton( Icons.s_instance.fireCoverOff32().createImage() );
  PushButton m_btnEndTurn = new PushButton( Icons.s_instance.endTurn32().createImage() );
  PushButton m_btnZoomIn = new PushButton( Icons.s_instance.zoomIn32().createImage() );
  PushButton m_btnZoomOut = new PushButton( Icons.s_instance.zoomOut32().createImage() );
  PushButton m_btnGrid = new PushButton( Icons.s_instance.grid32().createImage() );
  PushButton m_btnRegister = new PushButton( Icons.s_instance.register32().createImage() );
  FocusPanel m_pnlRegister = null;
  FocusPanel m_pnlWait = null;
  FocusPanel m_pnlLand = null;
  FocusPanel m_pnlDeploy = null;
  FocusPanel m_pnlPause = null;
  FocusPanel m_pnlEndTurn = null;
  FocusPanel m_pnlTakeOff = null;
  FocusPanel m_pnlChannelDisconnected = null;
  PushButton m_btnTakeOff = new PushButton( Icons.s_instance.takeOff32().createImage() );
  Image m_iconAction = Icons.s_instance.action16().createImage();
  Label m_lblAction = new Label( "" );

  /**
   * 
   */
  public WgtContextAction()
  {
    m_btnOk.addClickHandler( this );
    m_btnOk.setTitle( "Valider l'action" );
    m_btnOk.setStyleName( "fmp-PushButton32" );
    m_btnCancel.addClickHandler( this );
    m_btnCancel.setTitle( "Annuler l'action [ESC]" );
    m_btnCancel.setStyleName( "fmp-PushButton32" );
    m_btnRepairTurret.addClickHandler( this );
    m_btnRepairTurret.setTitle( "Reparer la tourelle" );
    m_btnRepairTurret.setStyleName( "fmp-PushButton32" );
    m_btnTakeOff.addClickHandler( this );
    m_btnTakeOff.setTitle( "Decollage" );
    m_btnTakeOff.setStyleName( "fmp-PushButton32" );
    m_btnFire.addClickHandler( this );
    m_btnFire.setTitle( "Tirer" );
    m_btnFire.setStyleName( "fmp-PushButton32" );
    m_btnControl.addClickHandler( this );
    m_btnControl.setTitle( "Controle" );
    m_btnControl.setStyleName( "fmp-PushButton32" );
    m_btnFireCoverOn.addClickHandler( this );
    m_btnFireCoverOn.setTitle( "Afficher les couvertures de feux [F]" );
    m_btnFireCoverOn.setStyleName( "fmp-PushButton32" );
    m_btnFireCoverOff.addClickHandler( this );
    m_btnFireCoverOff.setTitle( "Cacher les couvertures de feux [F]" );
    m_btnFireCoverOff.setStyleName( "fmp-PushButton32" );
    m_btnEndTurn.addClickHandler( this );
    m_btnEndTurn.setTitle( "Fin de tour" );
    m_btnEndTurn.setStyleName( "fmp-PushButton32" );
    m_btnZoomIn.addClickHandler( this );
    m_btnZoomIn.setTitle( "Zoom tactique [+]" );
    m_btnZoomIn.setStyleName( "fmp-PushButton32" );
    m_btnZoomOut.addClickHandler( this );
    m_btnZoomOut.setTitle( "Zoom strategique [-]" );
    m_btnZoomOut.setStyleName( "fmp-PushButton32" );
    m_btnGrid.addClickHandler( this );
    m_btnGrid.setTitle( "Afficher/cacher la grille [G]" );
    m_btnGrid.setStyleName( "fmp-PushButton32" );
    m_btnRegister.addClickHandler( this );
    m_btnRegister.setTitle( "S'inscrire a cette partie" );
    m_btnRegister.setStyleName( "fmp-PushButton32" );
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.register32().createImage() );
    hPanel.add( new Label( "Cette partie recherche des joueurs. Inscrivez vous !" ) );
    m_pnlRegister = new FocusPanel( hPanel );
    m_pnlRegister.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.pause32().createImage() );
    hPanel.add( new Label( "Vous devez attendre le démarage de la partie." ) );
    m_pnlWait = new FocusPanel( hPanel );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.takeOff32().createImage() );
    hPanel.add( new Label( "Vous devez vous poser sur la carte." ) );
    m_pnlLand = new FocusPanel( hPanel );
    m_pnlLand.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.takeOff32().createImage() );
    hPanel.add( new Label( "Vous pouvez déployer vos unités." ) );
    m_pnlDeploy = new FocusPanel( hPanel );
    m_pnlDeploy.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.pause32().createImage() );
    hPanel
        .add( new Label(
            "Pour permettre l'inscription d'un nouveau joueur vous devriez mettre la partie en pause" ) );
    m_pnlPause = new FocusPanel( hPanel );
    m_pnlPause.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.endTurn32().createImage() );
    hPanel.add( new Label( "Vous devez maintenant terminez votre tour !" ) );
    m_pnlEndTurn = new FocusPanel( hPanel );
    m_pnlEndTurn.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.takeOff32().createImage() );
    hPanel.add( new Label( "Clickez sur votre astronef pour le faire décoller" ) );
    m_pnlTakeOff = new FocusPanel( hPanel );
    m_pnlTakeOff.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.takeOff32().createImage() );
    hPanel.add( new Label( "Déconnecté du serveur" ) );
    m_pnlChannelDisconnected = new FocusPanel( hPanel );
    m_pnlChannelDisconnected.addClickHandler( this );


    m_iconAction.setTitle( "Cout en point d'action" );
    m_lblAction.setStyleName( "fmp-status-text" );

    // subscribe all needed models update event
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );

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
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(ModelFmpMain.model()) );
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
        if( ((EbEvtFire)actionBuilder.getSelectedAction()).getTokenTarget( ModelFmpMain.model()
            .getGame() ) == null )
        {
          // TODO i18n
          MAppMessagesStack.s_instance
              .showMessage( "Selectionez un second destructeur à porté, puis votre cible" );
        }
        else
        {
          // TODO i18n
          MAppMessagesStack.s_instance.showMessage( "Selectionez les deux destructeurs à porté" );
        }
      }
      else if( sender == m_btnControl )
      {
        actionBuilder.userAction( GameLogType.EvtControl );
        if( ((EbEvtFire)actionBuilder.getSelectedAction()).getTokenTarget( ModelFmpMain.model()
            .getGame() ) == null )
        {
          // TODO i18n
          MAppMessagesStack.s_instance
              .showMessage( "Selectionez un second destructeur au contact, puis votre cible" );
        }
        else
        {
          // TODO i18n
          MAppMessagesStack.s_instance.showMessage( "Selectionez les deux destructeurs au contact" );
        }
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
      else if( sender == m_btnRegister || sender == m_pnlRegister )
      {
        DlgJoinGame dlg = new DlgJoinGame();
        dlg.show();
        dlg.center();
      }
      else if( sender == m_btnEndTurn || sender == m_pnlEndTurn )
      {
        String msg = null;
        int oldPt = ModelFmpMain.model().getMyRegistration().getPtAction();
        int newPt = ModelFmpMain.model().getMyRegistration().getRoundedActionPt(ModelFmpMain.model().getGame());
        // TODO i18n
        if( oldPt == newPt )
        {
          msg = "Il vous reste " + oldPt + " points d'action. Confirmez-vous la fin de tour ?";
        }
        else
        {
          msg = "Vos " + oldPt + " points d'action seront arrondi a " + newPt
              + " pts. Confirmez-vous la fin de tour ?";
        }
        if( Window.confirm( msg ) )
        {
          EbEvtPlayerTurn action = new EbEvtPlayerTurn();
          action.setGame( ModelFmpMain.model().getGame() );
          action.setAccountId( ModelFmpMain.model().getMyAccount().getId() );
          ModelFmpMain.model().runSingleAction( action );
        }
      }
      else if( sender == m_btnTakeOff )
      {
        // TODO i18n
        if( Window.confirm( "Confirmez-vous le decolage de "
            + Messages.getTokenString( 0, actionBuilder.getSelectedToken() ) + " ?" ) )
        {
          EbEvtTakeOff action = new EbEvtTakeOff();
          action.setGame( ModelFmpMain.model().getGame() );
          action.setAccountId( ModelFmpMain.model().getMyAccount().getId() );
          action.setToken( actionBuilder.getSelectedToken() );
          ModelFmpMain.model().runSingleAction( action );
        }
      }
      else if( sender == m_pnlPause )
      {
        AnEvent gameLog = GameLogFactory.newAdminTimePause( ModelFmpMain.model().getMyAccount()
            .getId() );
        gameLog.setGame( ModelFmpMain.model().getGame() );
        ModelFmpMain.model().runSingleAction( gameLog );
      }
      else if( sender == m_pnlLand )
      {
        // search for my freighter to land
        ModelFmpMain
            .model()
            .getActionBuilder()
            .userTokenClick(
                ModelFmpMain.model().getGame()
                    .getFreighter( ModelFmpMain.model().getMyRegistration() ) );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(ModelFmpMain.model()) );
      }
      else if( sender == m_pnlDeploy )
      {
        // search for any unit to deploy
        EbToken myFreighter = ModelFmpMain.model().getGame()
            .getFreighter( ModelFmpMain.model().getMyRegistration() );
        if( myFreighter.containToken() )
        {
          EbToken firstToken = myFreighter.getContains().iterator().next();
          ModelFmpMain.model().getActionBuilder().userTokenClick( firstToken );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(ModelFmpMain.model()) );
        }
      }
      else if( sender == m_pnlTakeOff )
      {
        ModelFmpMain.model().getActionBuilder().clear();
        // search for my freighter to take off
        for( EbToken token : ModelFmpMain.model().getGame()
            .getAllFreighter( ModelFmpMain.model().getMyRegistration() ) )
        {
          if( token.getLocation() == Location.Board )
          {
            ModelFmpMain.model().getActionBuilder().userTokenClick( token );
            break;
          }
        }
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(ModelFmpMain.model()) );
      }
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( e.getLocalizedMessage() );
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
    EbToken mainSelectedToken = action.getSelectedToken();
    EbRegistration myRegistration = model.getMyRegistration();

    if( !model.isChannelConnected()
        && (System.currentTimeMillis() - ClientUtil.pageLoadTimeMillis()) > 2000 )
    {
      MAppMessagesStack.s_instance.showMessage( m_pnlChannelDisconnected );
    }

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
      m_panel.add( m_btnGrid );
      if( ModelFmpMain.model().getZoomDisplayed().getValue() == EnuZoom.Small )
      {
        m_panel.add( m_btnZoomIn );
      }
      else
      {
        m_panel.add( m_btnZoomOut );
      }
      if( !ModelFmpMain.model().isTimeLineMode() )
      {
        // display wait panel advise
        if( (myRegistration != null) && (model.getGame().getCurrentTimeStep() <= 0)
            && (!model.getGame().isStarted()) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlWait );
        }
        // display end turn button ?
        if( (!ModelFmpMain.model().getGame().isAsynchron()) && (myRegistration != null)
            && (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == myRegistration)
            && (ModelFmpMain.model().getGame().isStarted()) )
        {
          m_panel.add( m_btnEndTurn );

          // and even end turn panel !?
          if( myRegistration.getPtAction() <= 0 )
          {
            
            if( ModelFmpMain.model().getGame().getCurrentTimeStep() > ModelFmpMain.model()
                .getGame().getEbConfigGameTime().getDeploymentTimeStep() )
            {
              MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
            }
            else 
            {
              EbToken myFreighter = ModelFmpMain.model().getGame().getFreighter( myRegistration );
              if( (ModelFmpMain.model().getGame().getCurrentTimeStep() < ModelFmpMain.model()
                .getGame().getEbConfigGameTime().getDeploymentTimeStep())
                && (myFreighter.getLocation() == Location.Board) )
              {
                MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
              }
              else if( (ModelFmpMain.model().getGame().getCurrentTimeStep() == ModelFmpMain.model()
                  .getGame().getEbConfigGameTime().getDeploymentTimeStep()) )
              {
                if( !myFreighter.containToken() )
                {
                  MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
                }
                else
                {
                  MAppMessagesStack.s_instance.showMessage( m_pnlDeploy );
                }
              }
            }
          }
        }
        if( ModelFmpMain.model().isLogged()
            && myRegistration == null
            && !ModelFmpMain.model().getGame().isStarted()
            && ModelFmpMain.model().getGame().getMaxNumberOfPlayer() > ModelFmpMain.model()
                .getGame().getCurrentNumberOfRegiteredPlayer() )
        {
          m_panel.add( m_btnRegister );
          MAppMessagesStack.s_instance.showMessage( m_pnlRegister );
        }
        
        // should we display pause to allow subscription advise ?
        if( (ModelFmpMain.model().getGame().getCurrentNumberOfRegiteredPlayer() < ModelFmpMain.model().getGame().getMaxNumberOfPlayer())
          && (ModelFmpMain.model().getGame().isStarted()) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlPause );
        }
        
        // should we display landing advise ?
        if( ModelFmpMain.model().getGame().getCurrentTimeStep() < ModelFmpMain.model()
            .getGame().getEbConfigGameTime().getDeploymentTimeStep())
        {
          EbToken myFreighter = ModelFmpMain.model().getGame().getFreighter( myRegistration );
          if( myFreighter != null
              && model.getGame().isStarted()
              && myFreighter.getLocation() == Location.Orbit
              && (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == myRegistration) )
          {
            MAppMessagesStack.s_instance.showMessage( m_pnlLand );
          }
        }

        // should we display take off advise ?
        if( (model.getGame().getAllowedTakeOffTurns().contains( model.getGame()
            .getCurrentTimeStep() ))
            && (ModelFmpMain.model().getGame().isAsynchron() || ModelFmpMain.model().getGame()
                .getCurrentPlayerRegistration() == myRegistration) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlTakeOff );
        }
      }
    }
    else if( myRegistration == null )
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

      if( !mainSelectedToken.getEnuColor().contain( myRegistration.getColor() ) )
      {
        if( mainSelectedToken.canBeATarget( ModelFmpMain.model().getGame() ) )
        {
          m_panel.add( m_btnFire );
        }
      }
      else if( ((ModelFmpMain.model().getGame().getTokenFireLength( mainSelectedToken ) > 0) || (mainSelectedToken
          .getType() == TokenType.Freighter && ModelFmpMain.model().getGame()
          .getToken( action.getSelectedPosition(), TokenType.Turret ) != null)) )
      {
        try
        {
          action.exec();
          if( (mainSelectedToken.getBulletCount() > 0)
              || (mainSelectedToken.getType() == TokenType.Freighter) )
          {
            m_panel.add( m_btnFire );
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
      if( action.getSelectedToken().canControlNeighbor( ModelFmpMain.model().getGame(),
          action.getSelectedPosition() ) )
      {
        m_panel.add( m_btnControl );
      }

      if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
          && (mainSelectedToken.getType() == TokenType.Freighter) && (myRegistration != null)
          && (myRegistration.getTurretsToRepair() > 0) && (myRegistration.getPtAction() >= 2)
          && (!mainSelectedToken.getEnuColor().isColored( myRegistration.getOriginalColor() ))
          && (myRegistration.getEnuColor().isColored( mainSelectedToken.getColor() ))
          && (model.getGame().getToken( action.getSelectedPosition(), TokenType.Turret ) == null)
          && (!mainSelectedToken.getPosition().equals( action.getSelectedPosition() )) )
      {
        // player select a destroyed pod. of a freighter he own (but different
        // from the original one)
        // add the repair turret button
        m_panel.add( m_btnRepairTurret );
      }

      if( (model.getGame().getAllowedTakeOffTurns().contains( model.getGame().getCurrentTimeStep() ))
          && (ModelFmpMain.model().getGame().isAsynchron() || ModelFmpMain.model().getGame()
              .getCurrentPlayerRegistration() == myRegistration) )
      {
        if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
            && (mainSelectedToken.getType() == TokenType.Freighter)
            && (mainSelectedToken.getEnuColor().contain( myRegistration.getColor() )) )
        {
          m_panel.add( m_btnTakeOff );
        }
      }
    }

  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(ModelFmpMain p_ModelSender)
  {
    redraw();
  }


}