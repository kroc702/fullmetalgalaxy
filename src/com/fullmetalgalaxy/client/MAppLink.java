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
package com.fullmetalgalaxy.client;

import com.fullmetalgalaxy.client.widget.GuiEntryPoint;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public abstract class MAppLink extends GuiEntryPoint implements ClickListener, MouseListener
{
  private Label m_createAccountLabel = new Label( "link" );

  public MAppLink()
  {
    super();
    m_createAccountLabel.addClickListener( this );
    m_createAccountLabel.addMouseListener( this );
    m_createAccountLabel.setStyleName( "gwt-Hyperlink" );
    initWidget( m_createAccountLabel );
  }



  /**
   * @return
   * @see com.google.gwt.user.client.ui.Label#getText()
   */
  public String getText()
  {
    return m_createAccountLabel.getText();
  }



  /**
   * @param p_text
   * @see com.google.gwt.user.client.ui.Label#setText(java.lang.String)
   */
  public void setText(String p_text)
  {
    m_createAccountLabel.setText( p_text );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_createAccountLabel )
    {
      p_sender.removeStyleName( "underline" );
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseDown(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseEnter(Widget p_sender)
  {
    p_sender.addStyleName( "underline" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onMouseLeave(Widget p_sender)
  {
    p_sender.removeStyleName( "underline" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseMove(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  @Override
  public void onMouseUp(Widget p_sender, int p_x, int p_y)
  {
  }


}
