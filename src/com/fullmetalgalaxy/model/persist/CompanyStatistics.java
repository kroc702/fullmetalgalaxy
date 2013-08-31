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

package com.fullmetalgalaxy.model.persist;

import java.util.Date;

import com.fullmetalgalaxy.model.Company;

/**
 * @author Vincent
 * basic statistics on one company for one year 
 */
public class CompanyStatistics extends EbBase
{
  private static final long serialVersionUID = 1L;

  private Company m_company = null;
  private int m_year = 0;

  private int m_profit = 0;
  private int m_miningCount = 0;
  private float m_profitability = 0.0f;

  /**
   * used by objectify
   */
  protected CompanyStatistics()
  {
    this( null );
  }

  @SuppressWarnings("deprecation")
  public CompanyStatistics(Company p_company)
  {
    m_company = p_company;
    if( m_company == null )
    {
      m_company = Company.Freelancer;
    }
    // we should call setYear after created new statistics
    m_year = (new Date()).getYear();
  }

  /**
   * this method is only here because GregorianCalendar can't be used in shared class
   * (not supported by GWT)
   * @param p_year
   */
  public void setYear(int p_year)
  {
    m_year = p_year;
  }

  public void addResult(int p_score, int p_investement)
  {
    m_profitability = ((m_profitability * m_miningCount) + ((float)p_score / p_investement));
    m_miningCount++;
    m_profitability /= m_miningCount;
    m_profit += p_score;
  }

  public int getProfitabilityInPercent()
  {
    return Math.round( getProfitability() * 100 );
  }

  public Company getCompany()
  {
    return m_company;
  }

  public int getYear()
  {
    return m_year;
  }

  public int getProfit()
  {
    return m_profit;
  }

  public int getMiningCount()
  {
    return m_miningCount;
  }

  public float getProfitability()
  {
    return m_profitability;
  }



}
