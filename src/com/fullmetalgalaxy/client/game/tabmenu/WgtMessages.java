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

package com.fullmetalgalaxy.client.game.tabmenu;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author vlegendr
 *
 */
public class WgtMessages extends Composite 
{
  private Panel m_panel = new VerticalPanel();
  private TextArea m_text = new TextArea();
  
  public WgtMessages()
  {
    super();
    
    if( ModelFmpMain.model().getGame().isMessageWebUrl() )
    {
      Frame frame = new Frame( ModelFmpMain.model().getGame().getMessage() );
      frame.setPixelSize( 700, 350 );
      m_panel.add( frame );
    }
    else
    {
      initEditableMsg(ModelFmpMain.model().getGame().getMessage());
    }
    
    initWidget(m_panel);
  }
  
  private void initEditableMsg(String p_text)
  {
    m_panel.clear();
    m_panel.add( new Label("Ce texte est public et éditable par tous les joueurs de cette partie") );
    m_panel.add( m_text );
    m_text.setPixelSize( 400, 350 );
    m_text.setText( p_text );
    m_text.addBlurHandler( new BlurHandler()
    {
      @Override
      public void onBlur(BlurEvent p_event)
      {
        if( m_text.getText().equalsIgnoreCase( ModelFmpMain.model().getGame().getMessage() ))
        {
          // message didn't change: don't send message event
          return;
        }
        EbEvtMessage message = new EbEvtMessage();
        message.setMessage( m_text.getText() );
        ModelFmpMain.model().runSingleAction(message);
        
      }
    });
  }


}
