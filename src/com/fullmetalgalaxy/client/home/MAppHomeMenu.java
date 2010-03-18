/**
 * 
 */
package com.fullmetalgalaxy.client.home;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MiniApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class MAppHomeMenu implements MiniApp
{
  public static final String HISTORY_ID = "homemenu";
  public static MessagesAppHome s_messages = (MessagesAppHome)GWT.create( MessagesAppHome.class );

  private Command m_cmdCreateGame = new Command()
  {
    public void execute()
    {
      if( ModelFmpMain.model().isLogged() )
      {
        AppMain.instance().gotoCreateGame();
      }
      else
      {
        Window.alert( s_messages.errorMustBeLogged() );
      }
    }
  };


  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#hide()
   */
  public void hide()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#show()
   */
  public void show(HistoryState p_state)
  {
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getWidget()
   */
  public Widget getTopWidget()
  {
    return null;
  }

}
