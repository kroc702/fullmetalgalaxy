/**
 * 
 */
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.creation.GameGenerator;
import com.fullmetalgalaxy.client.creation.MapSize;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * @BeanClass com.fullmetalgalaxy.model.persist.EbGame
 */
public class WgtGameInfo extends WgtBean implements ChangeListener
{
  // UI
  private VerticalPanel m_panel = new VerticalPanel();
  private WgtTextBox m_name = new WgtTextBox();
  private WgtTextArea m_description = new WgtTextArea();
  private WgtIntBox m_maxNumberOfPlayer = new WgtIntBox();
  private WgtBooleanBox m_isAsynchron = new WgtBooleanBox();
  private Label m_asynchronDesc = new Label( "(Les joueurs jouent chacun a leur tour)" );
  private WgtTextBox m_accountCreator = new WgtTextBox();
  private WgtIntBox m_landWidth = new WgtIntBox();
  private WgtIntBox m_landHeight = new WgtIntBox();
  private WgtDateBox m_creationDate = new WgtDateBox();
  private WgtConfigGameTime m_configTime = (WgtConfigGameTime)GWT.create( WgtConfigGameTime.class );

  private ListBox m_mapSize = new ListBox();
  private ListBox m_gameSpeed = new ListBox();

  /**
   * @param p_bean
   */
  public WgtGameInfo()
  {
    super();

    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Nom :" ) );
    hPanel.add( getName() );
    m_panel.add( hPanel );

    m_panel.add( new Label( "Description :" ) );
    m_panel.add( getDescription() );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Nombre maxi de joueur :" ) );
    hPanel.add( getMaxNumberOfPlayer() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "mode asynchrone :" ) );
    hPanel.add( getAsynchron() );
    hPanel.add( m_asynchronDesc );
    getAsynchron().addChangeListener( this );

    m_panel.add( hPanel );
    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Taille de carte :" ) );
    m_mapSize.addItem( "petite" );
    m_mapSize.addItem( "moyenne" );
    m_mapSize.addItem( "grande" );
    m_mapSize.setVisibleItemCount( 1 );
    m_mapSize.setItemSelected( 1, true );
    m_mapSize.addChangeListener( this );
    hPanel.add( m_mapSize );
    getLandWidth().setReadOnly( true );
    getLandWidth().setWidth( "40px" );
    hPanel.add( getLandWidth() );
    hPanel.add( new Label( "x" ) );
    getLandHeight().setReadOnly( true );
    getLandHeight().setWidth( "40px" );
    hPanel.add( getLandHeight() );
    m_panel.add( hPanel );

    hPanel = new HorizontalPanel();
    hPanel.add( new Label( "Vitesse du jeu :" ) );
    m_gameSpeed.addItem( ConfigGameTime.getFromOrdinal( 0 ).name() );
    m_gameSpeed.addItem( ConfigGameTime.getFromOrdinal( 1 ).name() );
    m_gameSpeed.setVisibleItemCount( 1 );
    m_gameSpeed.setItemSelected( 1, true );
    m_gameSpeed.addChangeListener( this );
    hPanel.add( m_gameSpeed );
    m_panel.add( hPanel );
    m_panel.add( getConfigTime() );

    /*
        hPanel = new HorizontalPanel();
        hPanel.add( new Label( "Createur :" ) );
        hPanel.add( getAccountCreator() );
        m_panel.add( hPanel );

        hPanel = new HorizontalPanel();
        hPanel.add( new Label( "date de creation :" ) );
        hPanel.add( getCreationDate() );
        m_panel.add( hPanel );
    */
    initWidget( m_panel );

    // receive all model change
    ModelFmpMain.model().subscribeModelUpdateEvent( this );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
   */
  public void onChange(Widget p_sender)
  {
    if( (p_sender == m_mapSize) || (p_sender == getMaxNumberOfPlayer()) )
    {
      GameGenerator.setSize( MapSize.getFromOrdinal( m_mapSize.getSelectedIndex() ) );
      ModelFmpMain.model().notifyModelUpdate();
    }
    else if( p_sender == m_gameSpeed || p_sender == getAsynchron() )
    {
      ModelFmpMain.model().getGame().setConfigGameTime(
          ConfigGameTime.getFromOrdinal( m_gameSpeed.getSelectedIndex() ) );
      ModelFmpMain.model().notifyModelUpdate();
    }
    if( ModelFmpMain.model().getGame().isAsynchron() )
    {
      m_asynchronDesc.setText( "(Les joueurs jouent tous en meme temps)" );
    }
    else
    {
      m_asynchronDesc.setText( "(Les joueurs jouent chacun a leur tour)" );
    }
  }



  /**
   * @return the name
   */
  protected WgtTextBox getName()
  {
    return m_name;
  }


  /**
   * @return the description
   */
  protected WgtTextArea getDescription()
  {
    return m_description;
  }


  /**
   * @return the maxNumberOfPlayer
   */
  protected WgtIntBox getMaxNumberOfPlayer()
  {
    return m_maxNumberOfPlayer;
  }


  /**
   * @return the isAsynchron
   * @BeanGetter isAsynchron()
   */
  protected WgtBooleanBox getAsynchron()
  {
    return m_isAsynchron;
  }


  /**
   * @return the accountCreator
   * @ReadOnly
   * @BeanGetter getAccountCreator().getLogin()
   * @BeanSetter getAccountCreator().setLogin()
   */
  protected WgtTextBox getAccountCreator()
  {
    return m_accountCreator;
  }


  /**
   * @return the landWidth
   */
  protected WgtIntBox getLandWidth()
  {
    return m_landWidth;
  }


  /**
   * @return the landHeight
   */
  protected WgtIntBox getLandHeight()
  {
    return m_landHeight;
  }


  /**
   * @return the creationDate
   * @ReadOnly
   */
  protected WgtDateBox getCreationDate()
  {
    return m_creationDate;
  }



  /**
   * @return the configTime
   * @ReadOnly
   * @BeanGetter getEbConfigGameTime()
   */
  protected WgtConfigGameTime getConfigTime()
  {
    return m_configTime;
  }

}
