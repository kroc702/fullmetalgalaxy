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


import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Services;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * other actions
 */

public class WgtEditExtra extends Composite  
{
  private VerticalPanel m_panel = new VerticalPanel();

  public WgtEditExtra()
  {

    initWidget( m_panel );
  }

  FmpCallback<Void> m_callback = new FmpCallback<Void>()
  {
    @Override
    public void onSuccess(Void p_result)
    {
      super.onSuccess( p_result );
      // load newly created game to show it
      Window.alert( "Operation reussi" );
    }

    /* (non-Javadoc)
     * @see com.fullmetalgalaxy.client.FmpCallback#onFailure(java.lang.Throwable)
     */
    @Override
    public void onFailure(Throwable p_caught)
    {
      try
      {
        Window.alert( Messages.getString( (RpcFmpException)p_caught ) );
      } catch( Throwable th )
      {
        if( (p_caught.getMessage() == null) || (p_caught.getMessage().length() == 0) )
        {
          Window.alert( "Unknown error or serveur is unreachable\n" );
        }
        else
        {
          Window.alert( p_caught.getMessage() );
        }
      }
    }


  };



}
