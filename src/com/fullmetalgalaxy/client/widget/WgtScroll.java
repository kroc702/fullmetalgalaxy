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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.widget;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollListenerCollection;
import com.google.gwt.user.client.ui.SourcesScrollEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtScroll extends Composite implements SourcesScrollEvents,
    MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler,
	TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler,
    NativePreviewHandler
{
  private Widget m_contentWidget = new AbsolutePanel();
  // private
  private AbsolutePanel m_absPanel = new AbsolutePanel();
  private FocusPanel m_focusPanel = new FocusPanel();
  /**
   * This mask panel is used to prevent child widget to receive any mouse event while dragging.
   */
  private Widget m_maskPanel = new AbsolutePanel();

  private boolean m_isMouseDraging = false;
  private boolean m_isMouseDown = false;
  private int m_lastMouseX = 0;
  private int m_lastMouseY = 0;

  private int m_keyDragingX = 0;
  private int m_keyDragingY = 0;

  private int m_horizontalScrollPosition = 0;
  private int m_verticalScrollPosition = 0;

  private int m_mouseSensitivity = 10;
  private int m_keyDraggingStep = 18;
  private int m_keyDraggingFrequency = 25;

  private ScrollListenerCollection m_scrollListenerCollection = new ScrollListenerCollection();

  private int m_mouseArrowSpaceNorth = 60;
  private int m_mouseArrowSpaceEast = 60;
  private int m_mouseArrowSpaceSouth = 60;
  private int m_mouseArrowSpaceWest = 60;
  private Image m_mouseScrollingImage = new Image();
  private int m_mouseScrollingKey = 0;
  
  private int m_currentTouchEventId = -1;

  /**
   * 
   */
  public WgtScroll()
  {
    m_absPanel.add( m_contentWidget, 0, 0 );
    m_absPanel.setSize( "100%", "100%" );
    m_absPanel.add( m_mouseScrollingImage );
    m_mouseScrollingImage.setVisible( false );
    m_mouseScrollingImage.addMouseDownHandler( this );
    m_mouseScrollingImage.addMouseMoveHandler( this );
    m_mouseScrollingImage.addMouseUpHandler( this );
    m_mouseScrollingImage.addMouseOutHandler( this );
    m_focusPanel.setSize( "100%", "100%" );
    m_focusPanel.setWidget( m_absPanel );
    m_focusPanel.addMouseDownHandler( this );
    m_focusPanel.addMouseMoveHandler( this );
    m_focusPanel.addMouseUpHandler( this );
    m_focusPanel.addMouseOutHandler( this );
    m_focusPanel.addTouchStartHandler( this );
    m_focusPanel.addTouchMoveHandler( this );
    m_focusPanel.addTouchEndHandler( this );
    m_focusPanel.addTouchCancelHandler( this );
    initWidget( m_focusPanel );

    m_maskPanel = m_contentWidget;
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onAttach()
   */
  @Override
  protected void onLoad()
  {
    super.onLoad();
    AppMain.instance().addPreviewListener( this );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onDetach()
   */
  @Override
  protected void onUnload()
  {
    super.onUnload();
    AppMain.instance().removePreviewListener( this );
  }



  /**
   * reinit the scrolling position to 0,0
   */
  @Override
  public void setWidget(Widget p_wgt)
  {
    m_absPanel.remove( m_contentWidget );
    m_contentWidget = p_wgt;
    m_absPanel.add( m_contentWidget, 0, 0 );
    m_absPanel.add( m_maskPanel, 0, 0 );
    m_maskPanel.setVisible( false );
    ensureWidgetIsVisible();
    // be sure that arrow are always visible
    m_absPanel.add( m_mouseScrollingImage );
    DOM.setStyleAttribute( m_mouseScrollingImage.getElement(), "zIndex", "9999" );
    m_mouseScrollingImage.setVisible( false );
  }

  /**
   * if content widget isn't visible,
   * set the scroll position to see it.
   */
  public void centerContentWidget()
  {
    setScrollPosition( (m_contentWidget.getOffsetWidth() - getOffsetWidth()) / 2, (m_contentWidget
        .getOffsetHeight() - getOffsetHeight()) / 2 );
  }

  /**
   * if content widget isn't visible,
   * set the scroll position to see it.
   */
  public void ensureWidgetIsVisible()
  {
    if( m_verticalScrollPosition > m_contentWidget.getOffsetHeight() - getOffsetHeight()
        + getOffsetHeight() / 2 )
    {
      m_verticalScrollPosition = m_contentWidget.getOffsetHeight() - getOffsetHeight()
          + getOffsetHeight() / 2;
    }
    if( m_verticalScrollPosition < -1 * getOffsetHeight() / 2 )
    {
      m_verticalScrollPosition = -1 * getOffsetHeight() / 2;
    }
    if( m_horizontalScrollPosition > m_contentWidget.getOffsetWidth() - getOffsetWidth()
        + getOffsetWidth() / 2 )
    {
      m_horizontalScrollPosition = m_contentWidget.getOffsetWidth() - getOffsetWidth()
          + getOffsetWidth() / 2;
    }
    if( m_horizontalScrollPosition < -1 * getOffsetWidth() / 2 )
    {
      m_horizontalScrollPosition = -1 * getOffsetWidth() / 2;
    }
  }

  public void setSensitivity(int p_sensitivity)
  {
    m_mouseSensitivity = p_sensitivity;
  }

  private boolean m_cancelMouseUpEvent = false;
  private Timer m_cancelTimer = new Timer()
  {
    @Override
    public void run()
    {
      m_cancelMouseUpEvent = false;
    }
  };


  private boolean onKeyDown(int p_keyCode)
  {
    switch( p_keyCode )
    {
    case KeyboardListener.KEY_DOWN:
      if( m_keyDragingY != m_keyDraggingStep )
      {
        m_keyDragingY = m_keyDraggingStep;
        m_keyDraggingTimer.cancel();
        m_keyDraggingTimer.schedule( 1 );
      }
      return false;
    case KeyboardListener.KEY_UP:
      if( m_keyDragingY != -1 * m_keyDraggingStep )
      {
        m_keyDragingY = -1 * m_keyDraggingStep;
        m_keyDraggingTimer.cancel();
        m_keyDraggingTimer.schedule( 1 );
      }
      return false;
    case KeyboardListener.KEY_LEFT:
      if( m_keyDragingX != -1 * m_keyDraggingStep )
      {
        m_keyDragingX = -1 * m_keyDraggingStep;
        m_keyDraggingTimer.cancel();
        m_keyDraggingTimer.schedule( 1 );
      }
      return false;
    case KeyboardListener.KEY_RIGHT:
      if( m_keyDragingX != m_keyDraggingStep )
      {
        m_keyDragingX = m_keyDraggingStep;
        m_keyDraggingTimer.cancel();
        m_keyDraggingTimer.schedule( 1 );
      }
      // cancel event
      return false;
    default:
      return true;
    }
  }

  private boolean onKeyUp(int p_keyCode)
  {
    boolean stopScroll = false;
    switch( p_keyCode )
    {
    case KeyboardListener.KEY_DOWN:
    case KeyboardListener.KEY_UP:
      m_keyDragingY = 0;
      stopScroll = true;
      break;
    case KeyboardListener.KEY_LEFT:
    case KeyboardListener.KEY_RIGHT:
      m_keyDragingX = 0;
      stopScroll = true;
      break;
    default:
      return true;
    }
    if( (stopScroll == true) && (m_keyDragingX == 0) && (m_keyDragingY == 0) )
    {
      fireScroll();
    }
    // cancel event
    return false;
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
   */
  @Override
  public void onPreviewNativeEvent(NativePreviewEvent p_event)
  {
    if( p_event.getTypeInt() == Event.ONKEYDOWN )
    {
      // start scrolling ?
      onKeyDown( p_event.getNativeEvent().getKeyCode() );
      return;
    }
    if( p_event.getTypeInt() == Event.ONKEYUP )
    {
      // stop scrolling ?
      onKeyUp( p_event.getNativeEvent().getKeyCode() );
      return;
    }
    if( (p_event.getTypeInt() == Event.ONMOUSEUP) && (m_cancelMouseUpEvent) )
    {
      // cancel event
      return;
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesScrollEvents#addScrollListener(com.google.gwt.user.client.ui.ScrollListener)
   */
  @Override
  public void addScrollListener(ScrollListener p_listener)
  {
    m_scrollListenerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesScrollEvents#removeScrollListener(com.google.gwt.user.client.ui.ScrollListener)
   */
  @Override
  public void removeScrollListener(ScrollListener p_listener)
  {
    m_scrollListenerCollection.remove( p_listener );
  }



  private Timer m_keyDraggingTimer = new Timer()
  {
    @Override
    public void run()
    {
      if( (m_keyDragingX == 0) && (m_keyDragingY == 0) )
      {
        return;
      }
      setScrollPositionSilent( getHorizontalScrollPosition() + m_keyDragingX,
          getVerticalScrollPosition() + m_keyDragingY );
      m_keyDraggingTimer.schedule( m_keyDraggingFrequency );
    }
  };


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onDown(Element p_sender, int p_x, int p_y)
  {
    if( p_sender == m_mouseScrollingImage.getElement() )
    {
      onKeyDown( m_mouseScrollingKey );
    }
    else
    {
      m_isMouseDown = true;
      m_lastMouseX = p_x;
      m_lastMouseY = p_y;
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  public void onOut(Element p_sender)
  {
	  if( p_sender == m_mouseScrollingImage.getElement() )
    {
      if( (m_keyDragingX != 0) || (m_keyDragingY != 0) )
      {
        onKeyUp( m_mouseScrollingKey );
      }
      m_mouseScrollingImage.setVisible( false );
      m_mouseScrollingKey = 0;
    }
    else
    {
      if( m_isMouseDraging )
      {
        fireScroll();
      }
      m_isMouseDown = false;
      m_isMouseDraging = false;
      m_maskPanel.setVisible( false );
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMove(Element p_sender, int p_x, int p_y)
  {
    int scrollX = m_lastMouseX - p_x;
    int scrollY = m_lastMouseY - p_y;
    if( (m_isMouseDown) && (!m_isMouseDraging) )
    {
      if( (Math.abs( scrollX ) > m_mouseSensitivity) || Math.abs( scrollY ) > m_mouseSensitivity )
      {
        m_isMouseDraging = true;
        m_maskPanel.setPixelSize( m_contentWidget.getOffsetWidth(), m_contentWidget
            .getOffsetHeight() );
        m_maskPanel.setVisible( true );
      }
    }
    else if( m_isMouseDraging )
    {
      setScrollPositionSilent( getHorizontalScrollPosition() + scrollX, getVerticalScrollPosition()
          + scrollY );
      m_lastMouseX = p_x;
      m_lastMouseY = p_y;
    }
    else
    {
      int mouseScrollingKey = 0;
      if( p_x > getOffsetWidth() - m_mouseArrowSpaceEast )
      {
        AbstractImagePrototype.create( Icons.s_instance.arrow_e() ).applyTo( m_mouseScrollingImage );
        m_absPanel.setWidgetPosition( m_mouseScrollingImage, getOffsetWidth()
            - m_mouseScrollingImage.getWidth(), p_y - m_mouseScrollingImage.getHeight() / 2 );
        m_mouseScrollingImage.setVisible( true );
        mouseScrollingKey = KeyboardListener.KEY_RIGHT;
      }
      else if( p_x < m_mouseArrowSpaceWest )
      {
        AbstractImagePrototype.create( Icons.s_instance.arrow_w() ).applyTo( m_mouseScrollingImage );
        m_absPanel.setWidgetPosition( m_mouseScrollingImage, 0, p_y
            - m_mouseScrollingImage.getHeight() / 2 );
        m_mouseScrollingImage.setVisible( true );
        mouseScrollingKey = KeyboardListener.KEY_LEFT;
      }
      else if( p_y > getOffsetHeight() - m_mouseArrowSpaceSouth )
      {
        AbstractImagePrototype.create( Icons.s_instance.arrow_s() ).applyTo( m_mouseScrollingImage );
        m_absPanel.setWidgetPosition( m_mouseScrollingImage, p_x - m_mouseScrollingImage.getWidth()
            / 2, getOffsetHeight() - m_mouseScrollingImage.getHeight() );
        m_mouseScrollingImage.setVisible( true );
        mouseScrollingKey = KeyboardListener.KEY_DOWN;
      }
      else if( p_y < m_mouseArrowSpaceNorth )
      {
        AbstractImagePrototype.create( Icons.s_instance.arrow_n() ).applyTo( m_mouseScrollingImage );
        m_absPanel.setWidgetPosition( m_mouseScrollingImage, p_x - m_mouseScrollingImage.getWidth()
            / 2, 0 );
        m_mouseScrollingImage.setVisible( true );
        mouseScrollingKey = KeyboardListener.KEY_UP;
      }
      else if( m_mouseScrollingKey != 0 )
      {
        m_mouseScrollingImage.setVisible( false );
        m_mouseScrollingKey = 0;
      }
      DOM.setStyleAttribute( m_mouseScrollingImage.getElement(), "zIndex", "9999" );
      if( (mouseScrollingKey != 0) && (m_keyDragingX == 0) && (m_keyDragingY == 0) )
      {
        m_mouseScrollingKey = mouseScrollingKey;
      }
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onUp(Element p_sender)
  {
	if( p_sender == m_mouseScrollingImage.getElement() )
    {
      onKeyUp( m_mouseScrollingKey );
    }
    else
    {
      m_isMouseDown = false;
      if( m_isMouseDraging )
      {
        fireScroll();
        m_cancelMouseUpEvent = true;
        m_cancelTimer.schedule( 500 );
      }
      m_isMouseDraging = false;
      m_maskPanel.setVisible( false );
    }
  }

  /**
   * @return the horizontalScrollPosition
   */
  public int getHorizontalScrollPosition()
  {
    return m_horizontalScrollPosition;
  }

  /**
   * @param p_horizontalScrollPosition the horizontalScrollPosition to set
   */
  public void setHorizontalScrollPosition(int p_horizontalScrollPosition)
  {
    setScrollPosition( p_horizontalScrollPosition, getVerticalScrollPosition() );
  }

  /**
   * @return the verticalScrollPosition
   */
  public int getVerticalScrollPosition()
  {
    return m_verticalScrollPosition;
  }

  /**
   * @param p_verticalScrollPosition the verticalScrollPosition to set
   */
  public void setVerticalScrollPosition(int p_verticalScrollPosition)
  {
    setScrollPosition( getHorizontalScrollPosition(), p_verticalScrollPosition );
  }

  /**
   * change the scroll position
   * @param p_horizontalScrollPosition in pixel
   * @param p_verticalScrollPosition in pixel
   */
  public void setScrollPosition(int p_horizontalScrollPosition, int p_verticalScrollPosition)
  {
    setScrollPositionSilent( p_horizontalScrollPosition, p_verticalScrollPosition );
    fireScroll();
  }

  /**
   * change the scroll position but do not fire scrolling event
   * @param p_horizontalScrollPosition in pixel
   * @param p_verticalScrollPosition in pixel
   */
  public void setScrollPositionSilent(int p_horizontalScrollPosition,
      int p_verticalScrollPosition)
  {
    m_verticalScrollPosition = p_verticalScrollPosition;
    m_horizontalScrollPosition = p_horizontalScrollPosition;
    ensureWidgetIsVisible();
    m_absPanel.setWidgetPosition( m_contentWidget, -1 * getHorizontalScrollPosition(), -1
        * getVerticalScrollPosition() );
  }

  /**
   * This method fire onScroll event on every scroll listener. It could be usefull in case 
   * of a window resize event and if the listener take care of the size of this widget.
   */
  public void fireScroll()
  {
    m_scrollListenerCollection.fireScroll( this, m_horizontalScrollPosition,
        m_verticalScrollPosition );
  }

  public static native boolean isZoomed()
  /*-{
  	// If the document width is larger than the window's innerwidth
  	// we consider that the user has zoomed on the website.
  	// The 30px threshold is arbitraily chosen. Some devices have a few
  	// pixels difference between window.innerWidth and document.clientWidth
  	// even when the user hasn't zoomed
  	return Math.abs($wnd.innerWidth - $doc.documentElement.clientWidth) > 30;
  }-*/;

  private Touch getTouch(JsArray<Touch> touches, int identifier)
  {
    if (touches != null && !isZoomed()) {
      if (identifier == -1) {
        if (touches.length() > 0) {
          return touches.get(0);
        }
      } else if (touches.length() == 1) {
        Touch touch = touches.get(0);
        if (touch.getIdentifier() == identifier) {
          return touch;
        }
      }
    }
    return null;
  }

  @Override
  public void onTouchStart(TouchStartEvent p_event)
  {
    Touch touch = getTouch(p_event.getChangedTouches(), -1);
    if (touch != null) {
      m_currentTouchEventId = touch.getIdentifier();
      onDown(Element.as(touch.getTarget()), touch.getClientX(), touch.getClientY());
    }
  }

  @Override
  public void onTouchMove(TouchMoveEvent p_event)
  {
    Touch touch = getTouch(p_event.getChangedTouches(), m_currentTouchEventId);
    if (touch != null) {
      if (p_event.getTouches().length() < 2) {
    	  // A single finger touch shouldn't be propagated upwards.
    	  // This prevents iOS 'bounce-scroll' to happen
    	  p_event.preventDefault();
      }
      onMove(Element.as(touch.getTarget()), touch.getClientX(), touch.getClientY());
    }
  }

  @Override
  public void onTouchEnd(TouchEndEvent p_event)
  {
    Touch touch = getTouch(p_event.getChangedTouches(), m_currentTouchEventId);
    if (touch != null) {
      m_currentTouchEventId = -1;
      onUp(Element.as(touch.getTarget()));
    }
  }

  @Override
  public void onTouchCancel(TouchCancelEvent p_event)
  {
    Touch touch = getTouch(p_event.getChangedTouches(), m_currentTouchEventId);
    if (touch != null) {
      m_currentTouchEventId = -1;
      onOut(Element.as(touch.getTarget()));
    }
  }

  @Override
  public void onMouseDown(MouseDownEvent p_event)
  {
    p_event.preventDefault();
    onDown(Element.as(p_event.getNativeEvent().getEventTarget()), p_event.getClientX(), p_event.getClientY());
  }

  @Override
  public void onMouseMove(MouseMoveEvent p_event)
  {
    p_event.preventDefault();
    onMove(Element.as(p_event.getNativeEvent().getEventTarget()), p_event.getClientX(), p_event.getClientY());
  }

  @Override
  public void onMouseUp(MouseUpEvent p_event)
  {
    onUp(Element.as(p_event.getNativeEvent().getEventTarget()));
  }

  @Override
  public void onMouseOut(MouseOutEvent p_event)
  {
    onOut(Element.as(p_event.getNativeEvent().getEventTarget()));
  }
}
