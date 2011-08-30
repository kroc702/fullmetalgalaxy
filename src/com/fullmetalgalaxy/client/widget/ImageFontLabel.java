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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.client.ressources.fonts.ImageFont;
import com.fullmetalgalaxy.client.ressources.fonts.ImageFontBundle;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Vincent Legendre
 *
 */
public class ImageFontLabel extends HTML
{
  private ImageFontBundle m_font = null;

  public ImageFontLabel()
  {
    super();
  }

  public ImageFontLabel(ImageFontBundle p_font)
  {
    super();
    m_font = p_font;
  }

  public ImageFontLabel(String p_html, ImageFontBundle p_font)
  {
    super( p_html );
    m_font = p_font;
  }

  public ImageFontLabel(String p_html)
  {
    super( p_html );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Label#setText(java.lang.String)
   */
  @Override
  public void setText(String p_text)
  {
    super.setText( p_text );
    if( getFont() != null )
    {
      setHTML( ImageFont.getHTML( m_font, getText() ) );
    }
  }

  /**
   * @return the font
   */
  public ImageFontBundle getFont()
  {
    return m_font;
  }

  /**
   * @param p_font the font to set
   */
  public void setFont(ImageFontBundle p_font)
  {
    if( p_font != m_font )
    {
      m_font = p_font;
      setText( getText() );
    }
  }



}
