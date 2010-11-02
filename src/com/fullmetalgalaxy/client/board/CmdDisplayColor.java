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


import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuColor;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author Vincent Legendre
 *
 */
public class CmdDisplayColor extends MenuItem implements Command
{
  protected EnuColor m_color = new EnuColor();
  protected boolean m_isChecked = false;


  public CmdDisplayColor(EnuColor p_color)
  {
    super( "", (Command)null );
    m_color = p_color;
    setHTML( Messages.getColorString( m_color.getValue() ) );
    setCommand( this );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.Command#execute()
   */
  @Override
  public void execute()
  {
    if( m_isChecked )
    {
      m_isChecked = false;
      setHTML( Messages.getColorString( m_color.getValue() ) );
    }
    else
    {
      m_isChecked = true;
      setHTML( "<b>" + Messages.getColorString( m_color.getValue() ) + "<b>" );
    }
  }

}
