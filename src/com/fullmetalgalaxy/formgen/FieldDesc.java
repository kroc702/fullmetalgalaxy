/**
 * 
 */
package com.fullmetalgalaxy.formgen;

import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author LEG88888
 * This is the description of one field we will have to add to the form.
 */
public class FieldDesc
{
  /**
   * from widget getter.
   */
  private String m_wgtName = "";
  /**
   * bean setter method.
   * from widget getter annotation.
   */
  private String m_setter = "";
  /**
   * bean getter method.
   * from widget getter annotation.
   */
  private String m_getterName = "";
  private boolean m_readOnly = false;
  /**
   * widget inherit from ScalarView or BeanView ?
   */
  private boolean m_scalar = true;
  private JType m_type = null;
  /**
   * this type provided by meta data overide m_type.
   */
  private String m_strType = null;

  public String getClassTypeString()
  {
    if( m_strType != null )
    {
      return m_strType;
    }
    if( m_type == null )
    {
      return "Object";
    }
    if( m_type.isClass() != null )
    {
      return m_type.isClass().getQualifiedSourceName();
    }
    if( m_type.isPrimitive() == JPrimitiveType.BOOLEAN )
    {
      return "Boolean";
    }
    if( m_type.isPrimitive() == JPrimitiveType.BYTE )
    {
      return "Byte";
    }
    if( m_type.isPrimitive() == JPrimitiveType.CHAR )
    {
      return "Char";
    }
    if( m_type.isPrimitive() == JPrimitiveType.DOUBLE )
    {
      return "Double";
    }
    if( m_type.isPrimitive() == JPrimitiveType.FLOAT )
    {
      return "Float";
    }
    if( m_type.isPrimitive() == JPrimitiveType.INT )
    {
      return "Integer";
    }
    if( m_type.isPrimitive() == JPrimitiveType.LONG )
    {
      return "Long";
    }
    if( m_type.isPrimitive() == JPrimitiveType.SHORT )
    {
      return "Short";
    }
    return "Object";
  }

  public String getNullCheckGetter()
  {
    if( !m_getterName.contains( "." ) )
    {
      return "true";
    }
    String checker = "";
    String currentGetter = "getTypedBean()";
    String[] getters = m_getterName.split( "\\." );
    for( int i = 0; i < getters.length - 1; i++ )
    {
      if( i != 0 )
      {
        checker += " && ";
      }
      currentGetter += "." + getters[i];
      checker += currentGetter + " != null";
    }
    return checker;
  }

  /**
   * @return the wgtName
   */
  public String getWgtName()
  {
    return m_wgtName;
  }

  /**
   * @param p_wgtName the wgtName to set
   */
  public void setWgtName(String p_wgtName)
  {
    m_wgtName = p_wgtName;
  }

  /**
   * @return the setter
   */
  public String getSetter()
  {
    return m_setter;
  }

  /**
   * @param p_setter the setter to set
   */
  public void setSetter(String p_setter)
  {
    if( p_setter.endsWith( "()" ) )
    {
      m_setter = p_setter.substring( 0, p_setter.length() - 2 );
    }
    else
    {
      m_setter = p_setter;
    }
  }

  /**
   * the getter name as returned by 'JMethod.getName()'
   * @return the getter
   */
  public String getGetterName()
  {
    return m_getterName;
  }

  /**
   * 
   * @return the getter name + '()'
   */
  public String getGetterMethod()
  {
    return m_getterName + "()";
  }

  /**
   * @param p_getter the getter method (or method name) to set
   */
  public void setGetter(String p_getter)
  {
    if( p_getter.endsWith( "()" ) )
    {
      m_getterName = p_getter.substring( 0, p_getter.length() - 2 );
    }
    else
    {
      m_getterName = p_getter;
    }
  }

  /**
   * @return the readOnly
   */
  public boolean isReadOnly()
  {
    return m_readOnly;
  }

  /**
   * @param p_readOnly the readOnly to set
   */
  public void setReadOnly(boolean p_readOnly)
  {
    m_readOnly = p_readOnly;
  }

  /**
   * @return the scalar
   */
  public boolean isScalar()
  {
    return m_scalar;
  }

  /**
   * @param p_scalar the scalar to set
   */
  public void setScalar(boolean p_scalar)
  {
    m_scalar = p_scalar;
  }

  /**
   * @return the type
   */
  public JType getJType()
  {
    return m_type;
  }

  /**
   * @param p_type the type to set
   */
  public void setType(JType p_type)
  {
    m_type = p_type;
  }

  /**
   * @return the strType
   */
  public String getStrType()
  {
    return m_strType;
  }

  /**
   * @param p_strType the strType to set
   */
  public void setStrType(String p_strType)
  {
    m_strType = p_strType;
  }



}
