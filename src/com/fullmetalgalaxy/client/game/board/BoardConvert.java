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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.game.board;

import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.AnPair;

/**
 * @author Vincent
 *
 * this class regroup several static function to convert map hex coordinate 
 * into pixel screen coordinate
 */
public class BoardConvert
{
  public static int landWidthHex2Pix(int p_landWidthHex, EnuZoom p_zoom)
  {
    return p_landWidthHex * ((FmpConstant.getHexWidth( p_zoom ) * 3) / 4)
        + FmpConstant.getHexWidth( p_zoom ) / 4;
  }

  public static int landHeightHex2Pix(int p_landHeightHex, EnuZoom p_zoom)
  {
    return(p_landHeightHex * FmpConstant.getHexHeight( p_zoom ) + FmpConstant.getHexHeight( p_zoom ) / 2);
  }


  /**
   * @param p_boardPosition board position in hexagon
   * @param p_zoom
   * @return widget center position in pixel
   */
  public static AnPair convertHexPositionToPixPosition(AnPair p_wgtHexPosition, EnuZoom p_zoom, AnPair p_wgtHexTopLeftCrop)
  {
    AnPair wgtPxPosition = new AnPair();
  
    wgtPxPosition.setX( (p_wgtHexPosition.getX() - p_wgtHexTopLeftCrop.getX()) * (FmpConstant.getHexWidth( p_zoom ) * 3 / 4)
        + FmpConstant.getHexWidth( p_zoom ) / 2 );
    if( p_wgtHexPosition.getX() % 2 == 0 )
    {
      wgtPxPosition.setY( (p_wgtHexPosition.getY() - p_wgtHexTopLeftCrop.getY()) * FmpConstant.getHexHeight( p_zoom )
          + FmpConstant.getHexHeight( p_zoom ) / 2 );
    }
    else
    {
      wgtPxPosition.setY( (p_wgtHexPosition.getY() - p_wgtHexTopLeftCrop.getY()) * FmpConstant.getHexHeight( p_zoom )
          + FmpConstant.getHexHeight( p_zoom ) );
    }
    return wgtPxPosition;
  }

  /**
   * warning, for borderless map shap returned position may be outside of game board.
   * @param p_wgtPosition widget position in pixel
   * @return board position in hexagon
   */
  public static AnBoardPosition convertPixPositionToHexPosition(AnPair p_pixPosition, EnuZoom p_zoom, AnPair p_wgtHexTopLeftCrop)
  {
    AnBoardPosition anBoardPosition = new AnBoardPosition( 0, 0 );
  
    anBoardPosition.setX( (p_pixPosition.getX() - FmpConstant.getHexWidth( p_zoom ) / 8)
        / (FmpConstant.getHexWidth( p_zoom ) * 3 / 4) );
  
    if( anBoardPosition.getX() % 2 == p_wgtHexTopLeftCrop.getX() % 2 )
    {
      anBoardPosition.setY( p_pixPosition.getY() / FmpConstant.getHexHeight( p_zoom ) );
    }
    else
    {
      anBoardPosition.setY( (p_pixPosition.getY() - FmpConstant.getHexHeight( p_zoom ) / 2)
          / FmpConstant.getHexHeight( p_zoom ) );
    }
    
    anBoardPosition.setX( anBoardPosition.getX() + p_wgtHexTopLeftCrop.getX() );
    anBoardPosition.setY( anBoardPosition.getY() + p_wgtHexTopLeftCrop.getY() );
    return anBoardPosition;
  }


}
