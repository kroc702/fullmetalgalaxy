/**
 * 
 */
package com.fullmetalgalaxy.client;



import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * helpper class to implement MiniApp interface
 */
public abstract class MApp extends Composite implements MiniApp, ModelUpdateListener
{

  /**
   * 
   */
  public MApp()
  {
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getTopWidget()
   */
  public Widget getTopWidget()
  {
    return this;
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
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.model.SourceModelUpdateEvents)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    if( !isVisible() )
    {
      return;
    }
    if( AppMain.instance() != null )
    {
      show( AppMain.instance().getHistoryState() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.MiniApp#getHistoryId()
   */
  public abstract String getHistoryId();


}
