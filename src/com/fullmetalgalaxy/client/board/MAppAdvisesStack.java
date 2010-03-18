/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.client.HistoryState;
import com.fullmetalgalaxy.client.MApp;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * display in game message, error and chat messages from other players
 */
public class MAppAdvisesStack extends MApp implements ClickListener
{
  public static final String HISTORY_ID = "advises";

  private Map<Widget, EbEvtMessage> s_advisesMap = new HashMap<Widget, EbEvtMessage>();
  private HorizontalPanel m_panel = new HorizontalPanel();

  public static MAppAdvisesStack s_instance = null;

  /**
   * 
   */
  public MAppAdvisesStack()
  {
    super();
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
    initWidget( m_panel );
    s_instance = this;
  }

  public String getHistoryId()
  {
    return HISTORY_ID;
  }

  public void show(HistoryState p_state)
  {
    super.show( p_state );
    m_panel.clear();
    s_advisesMap.clear();
    for( AnEvent event : ModelFmpMain.model().getGame().getLogs() )
    {
      if( event.getType() == GameLogType.EvtMessage )
      {
        Label label = new Label( "?" );
        label.setTitle( ((EbEvtMessage)event).getTitle() );
        label.setStyleName( "fmp-btn-advises" );
        label.addClickListener( this );
        m_panel.insert( label, 0 );
        s_advisesMap.put( label, (EbEvtMessage)event );
      }
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    EbEvtMessage message = s_advisesMap.get( p_sender );
    if( message != null )
    {
      DlgMessageEvent dlgMsg = new DlgMessageEvent( message );
      dlgMsg.center();
      dlgMsg.show();
    }
  }


}
