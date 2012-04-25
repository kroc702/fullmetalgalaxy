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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.tabmenu;

import java.util.Map.Entry;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbConfigGameVariant;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class WgtConstructReserve extends Composite
{
  private VerticalPanel m_panel = new VerticalPanel();

  /**
   * 
   */
  public WgtConstructReserve()
  {
    initWidget( m_panel );
    redraw();
  }

 
  
  public void redraw()
  {
    m_panel.clear();
    EbConfigGameVariant variant = GameEngine.model().getGame().getEbConfigGameVariant();
    
    EnuColor myColor = new EnuColor();
    if( GameEngine.model().getMyRegistration() != null )
    {
      myColor.setValue( GameEngine.model().getMyRegistration().getSingleColor() );
    }
    
    for(Entry<TokenType,Integer> entry : variant.getConstructReserve().entrySet() )
    {
      Image wgtToken = new Image();
      if( EbToken.canBeColored( entry.getKey() ) )
      {
        TokenImages.getTokenImage( myColor, EnuZoom.Medium, entry.getKey(),
            Sector.SouthEast ).applyTo( wgtToken );
      } else {
        TokenImages.getTokenImage( new EnuColor(EnuColor.None), EnuZoom.Medium, entry.getKey(),
            Sector.SouthEast ).applyTo( wgtToken );
      }
      m_panel.add( wgtToken );
      
      Label label = new Label( entry.getValue().toString() );
      m_panel.add( label );
    }
  }
  
}
