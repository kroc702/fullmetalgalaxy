package com.fullmetalgalaxy.model.constant;

import com.fullmetalgalaxy.model.EnuZoom;



public class FmpConstant
{
  public static final int initialActionPt = 5;
  public static final int maximumActionPtWithoutLanding = 10;

  public static final String forumUrl = "/jforum";

  // in second
  public static final int minimumResfreshingPeriod = 1;
  public static final int maximumResfreshingPeriod = 2;
  public static final int inactiveResfreshingPeriod = 60; // 1.40 min
  public static final int localResfreshingPeriod = 30;

  public static final int chatMessagesLivePeriod = 100;
  public static final int clientMessagesLivePeriod = 15;
  public static final int cometTimeout = 50;
  public static final int chatConnectionTimeout = 120;

  public static final int miniMapWidth = 240;
  public static final int miniMapHeight = 160;

  // public static final int imageHexWidth = 70;
  // public static final int imageHexHeight = 61;

  public static String getBaseUrl()
  {
    return "";// http://fullmetalgalaxy.com";
  }

  public static String getForumUrl()
  {
    return forumUrl;
  }

  public static String getMiniMapUrl(String p_gameId)
  {
    return "ImageServlet?minimap=" + p_gameId;
  }

  public static String getForumPMUrl(long p_forumId)
  {
    return forumUrl + "/pm/sendTo/" + p_forumId + ".page";
  }

  public static String getMyProfilUrl(long p_forumId)
  {
    return forumUrl + "/user/edit/" + p_forumId + ".page";
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexWidth(EnuZoom p_zoom)
  {
    return getHexWidth( p_zoom.getValue() );
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexWidth(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 34;// 30;
    default:
    case EnuZoom.Medium:
      return 76;// 50;
    case EnuZoom.Large:
      return 70;
    }
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexHeight(EnuZoom p_zoom)
  {
    return getHexHeight( p_zoom.getValue() );
  }

  /**
   * @param p_zoom
   * @return the size in pixel of an hexagon
   */
  public static int getHexHeight(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 29;// 26;
    default:
    case EnuZoom.Medium:
      return 40;// 44;
    case EnuZoom.Large:
      return 61;
    }
  }



}
