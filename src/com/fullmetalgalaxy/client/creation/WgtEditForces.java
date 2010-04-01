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
package com.fullmetalgalaxy.client.creation;

import java.util.HashMap;
import java.util.Map;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * to edit registrations
 * -> to active puzzle mode
 */
public class WgtEditForces extends Composite implements ClickListener, ChangeListener
{
  private Map<String, EbRegistration> m_mapReg = new HashMap<String, EbRegistration>();

  // UI
  private ListBox m_lstReg = new ListBox();
  private Button m_btnNewReg = new Button( "Nouvelle force" );
  private WgtEditOneRegistration m_wgtOneReg = new WgtEditOneRegistration();

  public WgtEditForces()
  {
    VerticalPanel vpanel = new VerticalPanel();
    m_lstReg.setMultipleSelect( false );
    m_lstReg.addChangeListener( this );
    m_lstReg.setVisibleItemCount( 10 );
    vpanel.add( m_lstReg );
    m_btnNewReg.addClickListener( this );
    vpanel.add( m_btnNewReg );

    HorizontalPanel panel = new HorizontalPanel();
    panel.add( vpanel );
    panel.add( m_wgtOneReg );

    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnNewReg )
    {
      EbGame game = ModelFmpMain.model().getGame();

      if( game.getSetRegistration().size() >= game.getMaxNumberOfPlayer() )
      {
        Window.alert( "Le nombre max est atteind" );
      }
      else
      {
        EbRegistration registration = new EbRegistration();
        EnuColor color = (EnuColor)game.getFreeColors4Registration().toArray()[0];
        registration.setEnuColor( color );
        game.getSetRegistration().add( registration );
        registration.setGame( game );
        registration.setOriginalColor( color.getValue() );
        refreshRegistrationList();
        m_lstReg.setSelectedIndex( game.getSetRegistration().size() - 1 );
        selectRegistration( registration );
      }
    }

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void onChange(Widget p_sender)
  {
    if( p_sender == m_lstReg )
    {
      selectRegistration( m_mapReg.get( m_lstReg.getItemText( m_lstReg.getSelectedIndex() ) ) );
    }
  }

  public void refreshRegistrationList()
  {
    m_mapReg = new HashMap<String, EbRegistration>();
    int selectedIndex = m_lstReg.getSelectedIndex();
    m_lstReg.clear();
    for( EbRegistration registration : ModelFmpMain.model().getGame().getSetRegistration() )
    {
      m_lstReg.addItem( Messages.getColorString( registration.getColor() ) );
      m_mapReg.put( Messages.getColorString( registration.getColor() ), registration );
    }
    m_lstReg.setSelectedIndex( selectedIndex );
  }

  private void selectRegistration(EbRegistration p_registration)
  {
    m_wgtOneReg.loadRegistration( p_registration );
  }

}
