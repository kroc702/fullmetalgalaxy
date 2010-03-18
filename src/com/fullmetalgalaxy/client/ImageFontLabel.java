/**
 * 
 */
package com.fullmetalgalaxy.client;


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
