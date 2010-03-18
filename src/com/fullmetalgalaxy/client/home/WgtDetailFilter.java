/**
 * 
 */
package com.fullmetalgalaxy.client.home;


import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.PlanetType;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtDetailFilter extends Composite implements ClickListener
{
  private WgtGameFilter m_mainWgt = null;

  // UI
  private Button m_btnResearch = new Button( "rechercher" );
  private ListBox m_lstStatus = new ListBox();
  private TextBox m_txtName = new TextBox();
  private TextBox m_txtHeight = new TextBox();
  private TextBox m_txtWidth = new TextBox();
  private TextBox m_txtPlayerNumber = new TextBox();
  private TextBox m_txtPlayerName = new TextBox();
  private ListBox m_lstPlanetType = new ListBox();

  /**
   * 
   */
  public WgtDetailFilter(WgtGameFilter p_mainWgt)
  {
    assert p_mainWgt != null;
    m_mainWgt = p_mainWgt;
    Panel panel = new VerticalPanel();

    m_lstStatus.setVisibleItemCount( 1 );
    m_lstStatus.setMultipleSelect( false );
    m_lstStatus.addItem( "", "" );
    for( GameStatus status : GameStatus.values() )
    {
      m_lstStatus.addItem( status.name(), status.name() );
    }
    m_lstPlanetType.setVisibleItemCount( 1 );
    m_lstPlanetType.setMultipleSelect( false );
    m_lstPlanetType.addItem( "", "" );
    for( PlanetType planet : PlanetType.values() )
    {
      m_lstPlanetType.addItem( planet.name(), planet.name() );
    }
    m_btnResearch.addClickListener( this );
    panel.add( m_lstStatus );
    panel.add( m_lstPlanetType );
    panel.add( new Label( "Charactere joker/comparaison: '*' '<' '>' '='" ) );
    panel.add( new Label( "nom de la partie" ) );
    panel.add( m_txtName );
    panel.add( new Label( "hauteur en case" ) );
    panel.add( m_txtHeight );
    panel.add( new Label( "largeur en case" ) );
    panel.add( m_txtWidth );
    panel.add( new Label( "nombre de joueurs" ) );
    panel.add( m_txtPlayerNumber );
    panel.add( new Label( "nom d'un joueur" ) );
    panel.add( m_txtPlayerName );
    panel.add( m_btnResearch );
    initWidget( panel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnResearch )
    {
      m_mainWgt.getGameFilter().reinit();
      String selectedStatus = m_lstStatus.getValue( m_lstStatus.getSelectedIndex() );
      if( !selectedStatus.equals( "" ) )
      {
        m_mainWgt.getGameFilter().setStatus( GameStatus.valueOf( selectedStatus ) );
      }
      String selectedPlanet = m_lstPlanetType.getValue( m_lstPlanetType.getSelectedIndex() );
      if( !selectedPlanet.equals( "" ) )
      {
        m_mainWgt.getGameFilter().setPlanetType( PlanetType.valueOf( selectedStatus ) );
      }
      if( !m_txtName.getText().equals( "" ) )
      {
        m_mainWgt.getGameFilter().setName( m_txtName.getText() );
      }
      if( !m_txtHeight.getText().equals( "" ) )
      {
        m_mainWgt.getGameFilter().setHeight( m_txtHeight.getText() );
      }
      if( !m_txtWidth.getText().equals( "" ) )
      {
        m_mainWgt.getGameFilter().setWidth( m_txtWidth.getText() );
      }
      if( !m_txtPlayerNumber.getText().equals( "" ) )
      {
        m_mainWgt.getGameFilter().setPlayerNumber( m_txtPlayerNumber.getText() );
      }
      if( !m_txtPlayerName.getText().equals( "" ) )
      {
        m_mainWgt.getGameFilter().setPlayerName( m_txtPlayerName.getText() );
      }

      m_mainWgt.resfreshGameList();
    }

  }

}
