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
import java.util.List;

import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.FmgConstants;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.WgtBoardLayerLand;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.MapShape;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */

public class WgtToolsEditLands extends Composite implements ClickHandler, MouseListener, ChangeListener {
	private Panel m_panel = new VerticalPanel();
	private TextBox m_txtLandWidth = new TextBox();
	private TextBox m_txtLandHeight = new TextBox();
	private Button m_btnClear = new Button("effacer");
	private Button m_btnGenerate = new Button("generer");
	private TextBox m_txtLandPercent = new TextBox();
	private CheckBox m_chkRoundMap = new CheckBox();
	private Button m_btnLoadMap = new Button("Charger une carte");
	private DlgLoadMap m_dlgLoadMap = new DlgLoadMap();
	private ListBox m_lstPlanet = new ListBox(false);
	private List<PlanetType> m_planets = new ArrayList<PlanetType>();
	private ListBox m_lstMapShape = new ListBox(false);
	private ListBox m_lstAlgo = new ListBox(false);

	private ListBox m_lstBrush = new ListBox(false);
	private Image m_leftLand = new Image();
	private Image m_rightLand = new Image();
	private Image m_btnPlain = new Image(FmgConstants.boardFolderUri + "desert/strategy/plain1.png", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Small),
			FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Small));
	private Image m_btnMontain = new Image(FmgConstants.boardFolderUri + "desert/strategy/montain1.png", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexMontainWidthMargin(EnuZoom.Small),
			FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexMontainHeightMargin(EnuZoom.Small));
	private Image m_btnReef = new Image(FmgConstants.boardFolderUri + "desert/strategy/reef_low1.png", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Small),
			FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Small));
	private Image m_btnMarsh = new Image(FmgConstants.boardFolderUri + "desert/strategy/swamp_low1.png", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Small),
			FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Small));
	private Image m_btnSea = new Image(FmgConstants.boardFolderUri + "desert/strategy/sea1.png", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Small),
			FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Small));
	private Image m_btnNone = new Image(FmgConstants.boardFolderUri + "desert/strategy/grid.gif", 0, 0,
			FmpConstant.getHexWidth(EnuZoom.Small), FmpConstant.getHexHeight(EnuZoom.Small));

	private WgtBoardEditLand m_wgtlayerEditLand = null;

	/**
	 * 
	 */
	public WgtToolsEditLands(WgtBoardEditLand p_wgtlayerEditLand) {
		assert p_wgtlayerEditLand != null;
		m_wgtlayerEditLand = p_wgtlayerEditLand;
		m_lstPlanet.addChangeListener(this);
		m_lstPlanet.setVisibleItemCount(1);
		for (PlanetType planet : PlanetType.values()) {
			m_lstPlanet.addItem(Messages.getPlanetString(0, planet));
			m_planets.add(planet);
		}
		m_panel.add(m_lstPlanet);

		for (MapShape shape : MapShape.values()) {
			m_lstMapShape.addItem(shape.toString());
		}
		m_lstMapShape.setVisibleItemCount(1);
		m_lstMapShape.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent p_event) {
				GameEngine.model().getGame()
						.setMapShape(MapShape.valueOf(m_lstMapShape.getValue(m_lstMapShape.getSelectedIndex())));
			}
		});
		m_panel.add(m_lstMapShape);

		m_panel.add(new Label("taille de carte"));
		Panel hpanel = new HorizontalPanel();
		m_txtLandWidth.setWidth("30px");
		m_txtLandHeight.setWidth("30px");
		hpanel.add(m_txtLandWidth);
		hpanel.add(m_txtLandHeight);
		m_panel.add(hpanel);
		m_btnClear.addClickHandler(this);
		m_panel.add(m_btnClear);
		m_btnGenerate.addClickHandler(this);
		m_panel.add(m_btnGenerate);

		m_lstAlgo.addItem("Lakes", "" + Boolean.TRUE);
		m_lstAlgo.addItem("Islands", "" + Boolean.FALSE);
		m_lstAlgo.setSelectedIndex(0);
		m_lstAlgo.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent p_event) {
				GameEngine.generator()
						.setLakeBoard(Boolean.parseBoolean(m_lstAlgo.getValue(m_lstAlgo.getSelectedIndex())));
			}
		});
		m_panel.add(m_lstAlgo);

		hpanel = new HorizontalPanel();
		hpanel.add(new Label("terre en %"));
		m_txtLandPercent.addChangeListener(this);
		hpanel.add(m_txtLandPercent);
		m_txtLandPercent.setText("" + GameEngine.generator().getLandPercent());
		m_txtLandPercent.setMaxLength(3);
		m_txtLandPercent.setWidth("30px");
		m_panel.add(hpanel);
		hpanel = new HorizontalPanel();
		hpanel.add(new Label("Hexagonale"));
		hpanel.add(m_chkRoundMap);
		m_chkRoundMap.setChecked(GameEngine.generator().isHexagonMap());
		m_panel.add(hpanel);

		m_btnLoadMap.addClickHandler(this);
		m_panel.add(m_btnLoadMap);

		m_panel.add(new HTML("<hr>"));

		hpanel = new HorizontalPanel();
		hpanel.add(new Label("Brush "));
		m_lstBrush.addItem("1");
		m_lstBrush.addItem("3");
		m_lstBrush.addItem("7");
		m_lstBrush.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent p_event) {
				try {
					m_wgtlayerEditLand
							.setBrushSize(Integer.parseInt(m_lstBrush.getValue(m_lstBrush.getSelectedIndex())));
				} catch (NumberFormatException e) {
				}
			}
		});
		m_lstBrush.setSelectedIndex(0);
		hpanel.add(m_lstBrush);
		m_panel.add(hpanel);

		hpanel = new HorizontalPanel();
		hpanel.add(m_leftLand);
		hpanel.add(m_rightLand);
		m_panel.add(hpanel);
		m_btnNone.addMouseListener(this);
		m_panel.add(m_btnNone);
		m_btnSea.addMouseListener(this);
		m_panel.add(m_btnSea);
		m_btnReef.addMouseListener(this);
		m_panel.add(m_btnReef);
		m_btnMarsh.addMouseListener(this);
		m_panel.add(m_btnMarsh);
		m_btnPlain.addMouseListener(this);
		m_panel.add(m_btnPlain);
		m_btnMontain.addMouseListener(this);
		m_panel.add(m_btnMontain);

		setClicTool(Event.BUTTON_LEFT, LandType.Sea);
		setClicTool(Event.BUTTON_RIGHT, LandType.Montain);

		initWidget(m_panel);
		redraw();
	}

	protected void redraw() {
		m_lstMapShape.setSelectedIndex(GameEngine.model().getGame().getMapShape().ordinal());

		String base = FmgConstants.boardFolderUri + GameEngine.model().getGame().getPlanetType().getFolderName();
		int btnWidth = FmpConstant.getHexWidth(EnuZoom.Small) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Small);
		int btnHeight = FmpConstant.getHexHeight(EnuZoom.Small) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Small);
		m_btnPlain.setUrlAndVisibleRect(base + "/strategy/plain1.png", 0, 0, btnWidth, btnHeight);
		m_btnMontain.setUrl(base + "/strategy/montain1.png");
		m_btnReef.setUrlAndVisibleRect(base + "/strategy/reef_low1.png", 0, 0, btnWidth, btnHeight);
		m_btnMarsh.setUrlAndVisibleRect(base + "/strategy/swamp_low1.png", 0, 0, btnWidth, btnHeight);
		m_btnSea.setUrlAndVisibleRect(base + "/strategy/sea1.png", 0, 0, btnWidth, btnHeight);
		m_leftLand.setUrlAndVisibleRect(base + "/tactic/" + m_wgtlayerEditLand.getLeftClic().getImageName(), 10, 10,
				FmpConstant.getHexWidth(EnuZoom.Medium) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Medium),
				FmpConstant.getHexHeight(EnuZoom.Medium) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Medium));
		m_rightLand.setUrlAndVisibleRect(base + "/tactic/" + m_wgtlayerEditLand.getRightClic().getImageName(), 10, 10,
				FmpConstant.getHexWidth(EnuZoom.Medium) + WgtBoardLayerLand.getHexWidthMargin(EnuZoom.Medium),
				FmpConstant.getHexHeight(EnuZoom.Medium) + WgtBoardLayerLand.getHexHeightMargin(EnuZoom.Medium));

		m_txtLandWidth.setText("" + GameEngine.model().getGame().getLandWidth());
		m_txtLandHeight.setText("" + GameEngine.model().getGame().getLandHeight());
	}

	private void setClicTool(int p_button, LandType p_land) {
		String imageUrl = FmgConstants.boardFolderUri + GameEngine.model().getGame().getPlanetType().getFolderName()
				+ "/tactic/" + p_land.getImageName();
		if (p_button == Event.BUTTON_LEFT) {
			m_wgtlayerEditLand.setLeftClic(p_land);
			m_leftLand.setUrlAndVisibleRect(imageUrl, 10, 10, FmpConstant.getHexWidth(EnuZoom.Medium),
					FmpConstant.getHexHeight(EnuZoom.Medium));
		} else {
			m_wgtlayerEditLand.setRightClic(p_land);
			m_rightLand.setUrlAndVisibleRect(imageUrl, 10, 10, FmpConstant.getHexWidth(EnuZoom.Medium),
					FmpConstant.getHexHeight(EnuZoom.Medium));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client
	 * .ui.Widget)
	 */
	@Override
	public void onClick(ClickEvent p_event) {
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
		int landWidth = Integer.parseInt(m_txtLandWidth.getText());
		int landHeight = Integer.parseInt(m_txtLandHeight.getText());
		if (p_event.getSource() == m_btnGenerate) {
			int percent = Integer.parseInt(m_txtLandPercent.getText());
			GameEngine.generator().setSize(landWidth, landHeight);
			GameEngine.generator().setLandPercent(percent);
			GameEngine.generator().setHexagonMap(m_chkRoundMap.getValue());
			GameEngine.generator().generLands();
			GameEngine.model().getGame().setMapUri(null);
			AppRoot.getEventBus().fireEvent(new ModelUpdateEvent(GameEngine.model()));
		} else if (p_event.getSource() == m_btnClear) {
			GameEngine.generator().setSize(landWidth, landHeight);
			GameEngine.generator().clearLand(m_wgtlayerEditLand.getLeftClic());
			GameEngine.model().getGame().setMapUri(null);
			AppRoot.getEventBus().fireEvent(new ModelUpdateEvent(GameEngine.model()));
		} else if (p_event.getSource() == m_btnLoadMap) {
			m_dlgLoadMap.show();
			m_dlgLoadMap.center();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.
	 * client.ui.Widget, int, int)
	 */
	@Override
	public void onMouseDown(Widget p_arg0, int p_arg1, int p_arg2) {
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.
	 * client.ui.Widget)
	 */
	@Override
	public void onMouseEnter(Widget p_arg0) {
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.
	 * client.ui.Widget)
	 */
	@Override
	public void onMouseLeave(Widget p_arg0) {
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.
	 * client.ui.Widget, int, int)
	 */
	@Override
	public void onMouseMove(Widget p_arg0, int p_arg1, int p_arg2) {
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.
	 * client.ui.Widget, int, int)
	 */
	@Override
	public void onMouseUp(Widget p_sender, int p_arg1, int p_arg2) {
		int button = DOM.eventGetButton(DOM.eventGetCurrentEvent());
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
		if (p_sender == m_btnNone) {
			setClicTool(button, LandType.None);
		} else if (p_sender == m_btnSea) {
			setClicTool(button, LandType.Sea);
		} else if (p_sender == m_btnReef) {
			setClicTool(button, LandType.Reef);
		} else if (p_sender == m_btnMarsh) {
			setClicTool(button, LandType.Marsh);
		} else if (p_sender == m_btnPlain) {
			setClicTool(button, LandType.Plain);
		} else if (p_sender == m_btnMontain) {
			setClicTool(button, LandType.Montain);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.
	 * client.ui.Widget)
	 */
	@Override
	public void onChange(Widget p_sender) {
		if (p_sender == m_txtLandPercent) {
			int percent = Integer.parseInt(m_txtLandPercent.getText());
			if (percent > 100) {
				percent = 100;
			}
			if (percent < 20) {
				percent = 20;
			}
			m_txtLandPercent.setText("" + percent);
		} else if (p_sender == m_lstPlanet) {
			GameEngine.model().getGame().setPlanetType(m_planets.get(m_lstPlanet.getSelectedIndex()));
			redraw();
			AppRoot.getEventBus().fireEvent(new ModelUpdateEvent(GameEngine.model()));
		}

	}

}
