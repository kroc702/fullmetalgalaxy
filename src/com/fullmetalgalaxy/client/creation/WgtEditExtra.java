package com.fullmetalgalaxy.client.creation;


import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Services;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * other actions
 */
public class WgtEditExtra extends Composite implements ClickListener
{
  private VerticalPanel m_panel = new VerticalPanel();
  private Button m_btnDelete = new Button( "Detruire la partie" );
  private Button m_btnHistory = new Button( "Annuler et archiver la partie" );

  public WgtEditExtra()
  {
    m_panel.add( m_btnHistory );
    m_btnHistory.addClickListener( this );
    m_panel.add( m_btnDelete );
    m_btnDelete.addClickListener( this );
    initWidget( m_panel );
  }

  FmpCallback<Void> m_callback = new FmpCallback<Void>()
  {
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

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnDelete )
    {
      if( Window.confirm( "Etes vous sur de vouloir détruire cette partie ?" ) )
      {
        Services.Util.getInstance().deleteGame( ModelFmpMain.model().getGame().getId(), m_callback );
      }
    }
    else if( p_sender == m_btnHistory )
    {
      Services.Util.getInstance().cancelGame( ModelFmpMain.model().getGame().getId(), m_callback );
    }

  }


}
