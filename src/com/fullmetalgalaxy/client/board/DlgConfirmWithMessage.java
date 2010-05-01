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



import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 *
 */

public class DlgConfirmWithMessage extends DialogBox implements ClickHandler
{
  // UI
  private Button m_btnOk = new Button( "OK" );
  private Button m_btnCancel = new Button( "Cancel" );
  private VerticalPanel m_panel = new VerticalPanel();
  private TextArea m_textArea = new TextArea();

  public DlgConfirmWithMessage(String p_question)
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "You sure ?" );

    m_btnOk.addClickHandler( this );
    m_btnCancel.addClickHandler( this );

    m_panel.add( new Label( p_question ) );
    m_panel.add( m_textArea );

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( m_btnOk );
    hPanel.add( m_btnCancel );
    m_panel.add( hPanel );

    setWidget( m_panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnOk )
    {

    }
    else if( p_event.getSource() == m_btnCancel )
    {
      this.hide();
      this.removeFromParent();
    }
  }

}
