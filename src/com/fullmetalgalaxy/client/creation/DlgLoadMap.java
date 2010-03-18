/**
 * 
 */
package com.fullmetalgalaxy.client.creation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.client.FmpCallback;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class DlgLoadMap extends DialogBox implements ClickListener
{
  // UI
  private Map<Image, Long> m_maps = new HashMap<Image, Long>();
  private Button m_btnCancel = new Button( "Cancel" );
  private Panel m_panel = new FlowPanel();

  // model
  private List<EbGamePreview> m_gameList = null;
  private GameFilter m_filter = new GameFilter();
  protected DlgLoadMap m_this = this;

  private FmpCallback<List<EbGamePreview>> m_callbackGameList = new FmpCallback<List<EbGamePreview>>()
  {
    public void onSuccess(List<EbGamePreview> p_result)
    {
      super.onSuccess( p_result );
      m_gameList = p_result;
      redraw();
    }
  };


  private FmpCallback<ModelFmpInit> m_callbackFmpInit = new FmpCallback<ModelFmpInit>()
  {
    public void onSuccess(ModelFmpInit p_result)
    {
      super.onSuccess( p_result );
      ModelFmpMain.model().getGame().setLandSize( p_result.getGame().getLandWidth(),
          p_result.getGame().getLandHeight() );
      ModelFmpMain.model().addAllAccounts( p_result.getMapAccounts() );
      ModelFmpMain.model().getGame().setLands( p_result.getGame().getLands() );
      ModelFmpMain.model().getGame().setPlanetType( p_result.getGame().getPlanetType() );
      ModelFmpMain.model().getGame().getSetToken().clear();
      ModelFmpMain.model().fireModelUpdate();
      m_this.hide();
    }
  };



  /**
   * 
   */
  public DlgLoadMap()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "Clickez sur la carte de votre choix" );

    m_btnCancel.addClickListener( this );
    redraw();
    m_filter.reinit();
    m_filter.setStatus( GameStatus.Scenario );
    setWidget( m_panel );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    super.show();
    Services.Util.getInstance().getGameList( m_filter, m_callbackGameList );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnCancel )
    {
      this.hide();
      return;
    }
    Long gameId = m_maps.get( p_sender );
    Services.Util.getInstance().getModelFmpInit( gameId.toString(), m_callbackFmpInit );
  }


  protected void redraw()
  {
    m_panel.clear();
    m_maps = new HashMap<Image, Long>();
    if( m_gameList != null )
    {
      for( EbGamePreview game : m_gameList )
      {
        Image image = new Image( FmpConstant.getMiniMapUrl( "" + game.getId() ) );
        image.setPixelSize( 96, 64 );
        image.addClickListener( this );
        m_maps.put( image, game.getId() );
        m_panel.add( image );
      }
    }
    m_panel.add( m_btnCancel );
  }
}
