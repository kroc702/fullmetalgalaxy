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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vincent Legendre
 * other actions
 */

public class WgtEditExtra extends Composite  
{
  private VerticalPanel m_panel = new VerticalPanel();

  private IntegerBox m_intCurrentTurn = new IntegerBox();


  public WgtEditExtra()
  {
    m_panel.add( new Label( "current turn:" ) );
    m_panel.add( m_intCurrentTurn );
    m_intCurrentTurn.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        if( m_intCurrentTurn.getValue() == null )
        {
          m_intCurrentTurn.setValue( 0 );
        }
        GameEngine.model().getGame().setCurrentTimeStep( m_intCurrentTurn.getValue() );
      }
    } );

    initWidget( m_panel );
  }


  public void onTabSelected()
  {
    m_intCurrentTurn.setValue( GameEngine.model().getGame().getCurrentTimeStep() );
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
        Window.alert( ((RpcFmpException)p_caught).getLocalizedMessage() );
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
