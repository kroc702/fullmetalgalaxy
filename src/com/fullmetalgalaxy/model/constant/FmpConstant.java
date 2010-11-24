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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model.constant;

import com.fullmetalgalaxy.model.EnuZoom;



public class FmpConstant
{
  public static final int initialActionPt = 5;
  public static final int maximumActionPtWithoutLanding = 10;

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
    return "http://fullmetalplanete.forum2jeux.com/programme-de-vincent-f33/";
  }

  public static String getMiniMapUrl(String p_gameId)
  {
    return "ImageServlet?minimap=" + p_gameId;
  }

  public static String getPMUrl(long p_accountId)
  {
    return "/privatemsg.jsp?id=" + p_accountId;
  }

  public static String getProfileUrl(long p_accountId)
  {
    return "/profile.jsp?id=" + p_accountId;
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
