/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 * this class is attached to an HasWidgets interface (likely a panel), and can create several
 * image attached to it.
 * The main purpose of this class is to avoid many "new Image".
 */
public class ImagePool
{
  private HasWidgets m_panel = null;
  private int m_nextImageIndex = 0;

  private ArrayList<Image> m_imagePool = new ArrayList<Image>();

  public ImagePool(HasWidgets p_panel)
  {
    m_panel = p_panel;
  }

  public Image getFirstImage()
  {
    resetImageIndex();
    return getNextImage();
  }

  /**
   * random access to an image
   * @param p_index
   * @return
   */
  public Image getImage(int p_index)
  {
    while( p_index >= m_imagePool.size() )
    {
      Image image = new Image();
      m_imagePool.add( image );
      m_panel.add( image );
      image.setVisible( false );
    }
    m_nextImageIndex = p_index + 1;
    Image image = m_imagePool.get( p_index );
    image.setVisible( true );
    return image;
  }



  /**
   * @return
   * @see java.util.ArrayList#size()
   */
  public int size()
  {
    return m_imagePool.size();
  }

  public void resetImageIndex()
  {
    m_nextImageIndex = 0;
  }

  public Image getNextImage()
  {
    Image image = null;
    if( m_nextImageIndex < m_imagePool.size() )
    {
      image = m_imagePool.get( m_nextImageIndex );
      image.setVisible( true );
    }
    else
    {
      image = new Image();
      m_imagePool.add( image );
      m_panel.add( image );
    }
    m_nextImageIndex++;
    return image;
  }

  public void hideOtherImage()
  {
    int indexTmp = m_nextImageIndex;
    while( m_nextImageIndex < m_imagePool.size() )
    {
      Image image = m_imagePool.get( m_nextImageIndex );
      image.setVisible( false );
      image.removeStyleName( "transparent50" );
      m_nextImageIndex++;
    }
    m_nextImageIndex = indexTmp;
  }

}
