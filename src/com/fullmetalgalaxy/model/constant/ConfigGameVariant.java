package com.fullmetalgalaxy.model.constant;

import java.util.HashMap;

import com.fullmetalgalaxy.model.EbConfigGameVariant;


public enum ConfigGameVariant
{
  Standard;

  /**
   * not sure that this way is a very good idea...
   * @param p_value
   */
  public static ConfigGameVariant getFromOrdinal(int p_value)
  {
    assert p_value >= 0;
    assert p_value < values().length;
    return values()[p_value];
  }


  public static EbConfigGameVariant getEbConfigGameVariant(ConfigGameVariant p_config)
  {
    return s_configMap.get( p_config );
  }

  private static HashMap<ConfigGameVariant, EbConfigGameVariant> s_configMap = new HashMap<ConfigGameVariant, EbConfigGameVariant>();
  static
  {
    // TODO
    s_configMap.put( Standard, new EbConfigGameVariant() );
  }
}
