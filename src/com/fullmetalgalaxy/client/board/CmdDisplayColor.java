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
