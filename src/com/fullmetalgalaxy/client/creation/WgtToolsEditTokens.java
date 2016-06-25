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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtToolsEditTokens extends Composite implements ClickHandler, ChangeListener
{
  private Panel m_panel = new VerticalPanel();
  private CheckBox m_chkOreInSea = new CheckBox();
  private CheckBox m_chkUseAllOre = new CheckBox();
  private CheckBox m_chkUseOreGenerator = new CheckBox();
  private Button m_btnOre = new Button( MAppGameCreation.s_messages.putOre() );
  private ListBox m_lstColor = new ListBox();
  private List<EnuColor> m_colors = new ArrayList<EnuColor>();
  private Image m_currentTool = new Image();
  private Label m_lblUnit = new Label();
  private Map<Image, TokenType> m_tools = new HashMap<Image, TokenType>();

  WgtBoardEditTokens m_wgtBoardEditTokens = null;

  /**
   * 
   */
  public WgtToolsEditTokens(WgtBoardEditTokens p_wgtBoardEditTokens)
  {
    assert p_wgtBoardEditTokens != null;
    m_wgtBoardEditTokens = p_wgtBoardEditTokens;
    m_btnOre.addClickHandler( this );
    m_currentTool.addClickHandler( this );
    m_lstColor.setMultipleSelect( false );
    m_lstColor.setVisibleItemCount( 1 );
    // add all colors
    EnuColor color = null;
    int i = 0;
    if( AppMain.instance().iAmAdmin() )
    {
      while( i < EnuColor.getTotalNumberOfColor() )
      {
        color = EnuColor.getColorFromIndex( i );
        m_lstColor.addItem( Messages.getColorString( 0, color.getValue() ) );
        m_colors.add( i, color );
        if( color.getValue() == m_wgtBoardEditTokens.getColor().getValue() )
        {
          m_lstColor.setSelectedIndex( i );
        }
        i++;
      }
    }
    color = new EnuColor( EnuColor.None );
    m_lstColor.addItem( Messages.getColorString( 0, color.getValue() ) );
    m_colors.add( i, color );
    if( color.getValue() == m_wgtBoardEditTokens.getColor().getValue() )
    {
      m_lstColor.setSelectedIndex( i );
    }
    m_lstColor.addChangeListener( this );

    redraw();
    initWidget( m_panel );
  }

  protected void redraw()
  {
    m_tools.clear();
    m_panel.clear();

    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.add( new Label( "Minerai en mer" ) );
    hpanel.add( m_chkOreInSea );
    m_panel.add( hpanel );
    hpanel = new HorizontalPanel();
    hpanel.add( new Label( "Tous les minerais" ) );
    hpanel.add( m_chkUseAllOre );
    m_panel.add( hpanel );
    hpanel = new HorizontalPanel();
    hpanel.add( new Label( "Generateurs" ) );
    hpanel.add( m_chkUseOreGenerator );
    m_panel.add( hpanel );

    m_panel.add( m_btnOre );
    m_panel.add( m_currentTool );
    AbstractImagePrototype.create(
        TokenImages.getTokenImage( m_wgtBoardEditTokens.getColor(), EnuZoom.Medium,
            m_wgtBoardEditTokens.getTokenType(), m_wgtBoardEditTokens.getSector() ) ).applyTo(
        m_currentTool );
    m_panel.add( m_lblUnit );
    m_lblUnit.setText( Messages.getTokenString( 0, m_wgtBoardEditTokens.getTokenType() ) );
    m_panel.add( m_lstColor );

    if( m_wgtBoardEditTokens.getColor().getValue() == EnuColor.None )
    {
      addTokenBtn( TokenType.Ore0 );
      addTokenBtn( TokenType.Ore );
      addTokenBtn( TokenType.Ore3 );
      addTokenBtn( TokenType.Ore5 );
      addTokenBtn( TokenType.Ore2Generator );
      addTokenBtn( TokenType.Ore3Generator );
      addTokenBtn( TokenType.Warp );
      addTokenBtn( TokenType.Pontoon );
      addTokenBtn( TokenType.Sluice );
    }
    addTokenBtn( TokenType.Crab );
    addTokenBtn( TokenType.Tank );
    addTokenBtn( TokenType.Heap );
    addTokenBtn( TokenType.Tarask );
    addTokenBtn( TokenType.Hovertank );
    addTokenBtn( TokenType.Speedboat );
    addTokenBtn( TokenType.Crayfish );
    addTokenBtn( TokenType.Barge );
    addTokenBtn( TokenType.Destroyer );
    addTokenBtn( TokenType.WeatherHen );
    addTokenBtn( TokenType.Teleporter );
    addTokenBtn( TokenType.Freighter );
    addTokenBtn( TokenType.Turret );

  }

  private void addTokenBtn(TokenType p_token)
  {
    Image btn = new Image( TokenImages.getTokenImage( m_wgtBoardEditTokens.getColor(),
        EnuZoom.Small, p_token, Sector.SouthWest ) );
    m_tools.put( btn, p_token );
    btn.addClickHandler( this );
    m_panel.add( btn );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    TokenType token = m_tools.get( p_event.getSource() );
    if( token != null )
    {
      m_wgtBoardEditTokens.setTokenType( token );
    }
    if( p_event.getSource() == m_currentTool )
    {
      m_wgtBoardEditTokens.setSector( m_wgtBoardEditTokens.getSector().getNext() );
    }
    AbstractImagePrototype.create(
        TokenImages.getTokenImage( m_wgtBoardEditTokens.getColor(), EnuZoom.Medium,
            m_wgtBoardEditTokens.getTokenType(), m_wgtBoardEditTokens.getSector() ) ).applyTo(
        m_currentTool );
    m_lblUnit.setText( Messages.getTokenString( 0, m_wgtBoardEditTokens.getTokenType() ) );

    if( p_event.getSource() == m_btnOre )
    {
      if( m_chkOreInSea.getValue() ) {
        GameEngine.generator().s_oreAllowedOnLands.add( LandType.Sea );
      } else {
        GameEngine.generator().s_oreAllowedOnLands.remove( LandType.Sea );
      }
      GameEngine.generator().s_useAllOre = m_chkUseAllOre.getValue();
      GameEngine.generator().s_useOreGenerator = m_chkUseOreGenerator.getValue();

      GameEngine.generator().populateOres();
      m_wgtBoardEditTokens.m_layerToken.cleanToken();
      AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onChange(Widget p_sender)
  {
    if( p_sender == m_lstColor )
    {
      m_wgtBoardEditTokens.getColor().setValue(
          m_colors.get( m_lstColor.getSelectedIndex() ).getValue() );
      redraw();
    }

  }


}
