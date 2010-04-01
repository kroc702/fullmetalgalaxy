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
/**
 * 
 */
package com.fullmetalgalaxy.formgen;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

// file is writen here:
// C:\DOCUME~1\VINCEN~1\LOCALS~1\Temp\com.fullmetalgalaxy.client.form.WgtAnGame11911.java
/**
 * @author Vincent Legendre
 *
 */
public class FormGenerator extends Generator
{
  private String m_wgtClassName = "Wgt";
  private String m_msgClassName = "Messages";

  /**
   * 
   */
  public FormGenerator()
  {
    // TODO Auto-generated constructor stub
  }


  @Override
  public String generate(TreeLogger p_logger, GeneratorContext p_context, String p_typeName)
      throws UnableToCompleteException
  {
    SourceWriter writer = null;
    String qualifiedWgtClassName = null;

    // Accessing the Input Class
    try
    {
      TypeOracle typeOracle = p_context.getTypeOracle();
      JClassType requestedClass = typeOracle.getType( p_typeName );
      // String packageName = requestedClass.getPackage().getName();
      String packageName = "com.fullmetalgalaxy.client.form";
      String simpleClassName = requestedClass.getSimpleSourceName();
      m_wgtClassName = "Wgt" + simpleClassName;
      m_msgClassName = "Messages" + simpleClassName;

      clearBeanDescription();
      readBeanMethod( requestedClass );
      writer = getSourceWriter( p_logger, p_context, "com.fullmetalgalaxy.client.form" );
      if( writer != null )
      {
        writeConstructor( writer );
        writeMessageClass( writer );
        writeChangeListener( writer );
        writeGetBean( writer, p_typeName );

        writer.commit( p_logger );
      }

      qualifiedWgtClassName = packageName + "." + m_wgtClassName;
    } catch( NotFoundException e )
    {
      p_logger.log( TreeLogger.ERROR, "Class '" + p_typeName + "' Not Found", e );
      throw new UnableToCompleteException();
    }
    // Accessing Properties of the Context
    /*try
    {
      PropertyOracle properties = p_context.getPropertyOracle();
      String version = properties.getPropertyValue( p_logger, "externalvisibility" );
    } catch( BadPropertyValueException e )
    {
      p_logger.log( TreeLogger.ERROR, "Could not find property value", e );
      throw new UnableToCompleteException();
    }*/

    return qualifiedWgtClassName;
  }

  /**
   * use the member string 'm_wgtClassName'
   * @param logger
   * @param context
   * @param packageName
   * @return
   */
  protected SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext context,
      String packageName)
  {
    PrintWriter printWriter = context.tryCreate( logger, packageName, m_wgtClassName );
    if( printWriter == null )
      return null;
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, m_wgtClassName );
    composerFactory.addImport( "com.google.gwt.user.client.ui.Widget" );
    composerFactory.addImport( "com.fullmetalgalaxy.client.form.WgtFieldAbstract" );
    composerFactory.addImport( "com.fullmetalgalaxy.client.form.WgtForm" );

    composerFactory.addImport( "com.google.gwt.i18n.client.Messages" );
    composerFactory.addImport( "com.google.gwt.core.client.GWT" );

    for( Iterator it = m_importList.iterator(); it.hasNext(); )
    {
      composerFactory.addImport( (String)it.next() );
    }
    composerFactory.setSuperclass( "WgtForm" );
    return composerFactory.createSourceWriter( context, printWriter );
  }


  /**
   * This is the description of one field we will have to add to the form.
   * @author Vincent Legendre
   *
   */
  private class FormFieldDesc
  {
    public String name = "";
    public String wgtClass = "";
    public boolean required = false;
    public boolean hidden = false;
    public boolean readOnly = true;
    public boolean readOnlyTag = false;
    public ArrayList section = new ArrayList();
    public JType type = null;
  }


  /**
   * field list
   */
  HashMap m_field = new HashMap();
  /**
   * all required import
   */
  ArrayList m_importList = new ArrayList();

  protected static Pattern s_patternGetter = Pattern.compile( "get[A-Z]\\w*" );
  protected static Pattern s_patternSetter = Pattern.compile( "set[A-Z]\\w*" );

  /**
   * this method add p_class to m_importList if it's not already
   * @param p_class the fully qualified class name to import
   */
  private void addImport(String p_class)
  {
    if( !m_importList.contains( p_class ) )
    {
      m_importList.add( p_class );
    }
  }

  /**
   * clear previous class description
   */
  private void clearBeanDescription()
  {
    m_importList.clear();
    m_field.clear();
  }

  /**
   * return true: 
   *  - if p_tagName is found with no argument
   *  - if p_tagName is found with first argument = 'true'
   * return false if p_tagName is found with first argument != 'true'
   * return p_default otherwise. 
   */
  private boolean getBooleanMetaTag(JMethod p_method, String p_tagName, boolean p_default)
  {
    boolean tagValue = p_default;
    String metaTag[][] = p_method.getMetaData( p_tagName );
    if( metaTag.length > 0 )
    {
      // tag is found
      if( metaTag[0].length > 0 )
      {
        // arguments are founds
        tagValue = metaTag[0][0].equalsIgnoreCase( "true" );
      }
      else
      {
        tagValue = true;
      }

    }
    return tagValue;
  }

  /**
   * read all bean method to determine which widget we will have to create. 
   * @param p_bean
   */
  private void readBeanMethod(JClassType p_bean)
  {
    // read getter/setter from super class first
    if( p_bean.getSuperclass() != null )
    {
      readBeanMethod( p_bean.getSuperclass() );
    }

    // read all getter
    JMethod methods[] = p_bean.getMethods();
    for( int i = 0; i < methods.length; i++ )
    {
      if( (s_patternGetter.matcher( methods[i].getName() ).matches())
          && (methods[i].getParameters().length == 0) && (methods[i].isPublic()) )
      {
        // this is a Getter
        FormFieldDesc fieldDesc = new FormFieldDesc();
        fieldDesc.name = methods[i].getName().substring( 3 );
        fieldDesc.type = methods[i].getReturnType();
        fieldDesc.hidden = getBooleanMetaTag( methods[i], "WgtHidden", false );
        fieldDesc.required = getBooleanMetaTag( methods[i], "WgtRequired", false );
        fieldDesc.readOnlyTag = getBooleanMetaTag( methods[i], "WgtReadOnly", false );

        fieldDesc.wgtClass = getDefaultWgtClass( fieldDesc.type );
        addImport( fieldDesc.wgtClass );
        m_field.put( fieldDesc.name, fieldDesc );
      }
    }
    // read all setter
    for( int i = 0; i < methods.length; i++ )
    {
      if( (s_patternSetter.matcher( methods[i].getName() ).matches())
          && (methods[i].getReturnType().isPrimitive() == JPrimitiveType.VOID)
          && (methods[i].getParameters().length == 1) && (methods[i].isPublic()) )
      {
        // this is a Setter
        String name = methods[i].getName().substring( 3 );
        FormFieldDesc fieldDesc = (FormFieldDesc)m_field.get( name );
        if( (fieldDesc != null) && (fieldDesc.type == methods[i].getParameters()[0].getType())
            && (fieldDesc.readOnlyTag == false) )
        {
          // this setter is for the same type
          fieldDesc.readOnly = false;
        }
      }
    }

  }

  /**
   * 
   * @param p_class
   * @param p_fullName
   * @return true the class type or one of they parent/interface is the class p_fullName
   */
  private boolean isAssignableTo(JClassType p_class, String p_fullName)
  {
    if( p_class.getQualifiedSourceName().compareTo( p_fullName ) == 0 )
    {
      return true;
    }
    JClassType classes[] = p_class.getImplementedInterfaces();
    for( int i = 0; i < classes.length; i++ )
    {
      if( isAssignableTo( classes[i], p_fullName ) == true )
      {
        return true;
      }
    }
    if( p_class.getSuperclass() != null )
    {
      return isAssignableTo( p_class.getSuperclass(), p_fullName );
    }
    return false;
  }

  private String getDefaultWgtClass(JType p_type)
  {
    JPrimitiveType primitive = p_type.isPrimitive();
    if( primitive != null )
    {
      if( primitive == JPrimitiveType.BOOLEAN )
        return "com.fullmetalgalaxy.client.form.WgtFieldBoolean";
      if( primitive == JPrimitiveType.INT )
        return "com.fullmetalgalaxy.client.form.WgtFieldInteger";
      if( primitive == JPrimitiveType.LONG )
        return "com.fullmetalgalaxy.client.form.WgtFieldInteger";
      if( primitive == JPrimitiveType.SHORT )
        return "com.fullmetalgalaxy.client.form.WgtFieldInteger";
      if( primitive == JPrimitiveType.BYTE )
        return "com.fullmetalgalaxy.client.form.WgtFieldInteger";
    }
    JClassType classe = p_type.isClass();
    if( classe != null )
    {
      if( isAssignableTo( classe, "java.lang.String" ) == true )
        return "com.fullmetalgalaxy.client.form.WgtFieldString";
      if( isAssignableTo( classe, "java.util.Date" ) == true )
        return "com.fullmetalgalaxy.client.form.WgtFieldDate";
    }
    // no default type was found...
    return "com.fullmetalgalaxy.client.form.WgtField";
  }

  /**
   * use the member string 'm_wgtClassName'
   * @param p_writer
   * @param p_wgtClassName
   */
  private void writeConstructor(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }

    Collection fieldSet = m_field.values();
    // create i18n class
    p_writer.println( "protected static " + m_msgClassName + " s_messages = (" + m_msgClassName
        + ")GWT.create( " + m_msgClassName + ".class );" );
    p_writer.println( "" );

    // instanciate all field widget
    for( Iterator it = fieldSet.iterator(); it.hasNext(); )
    {
      FormFieldDesc fieldDesc = (FormFieldDesc)it.next();
      if( fieldDesc.hidden == false )
      {
        p_writer.println( fieldDesc.wgtClass + " m_" + fieldDesc.name + " = new "
            + fieldDesc.wgtClass + "();" );
      }
    }
    p_writer.println( "" );

    // write constructor
    p_writer.println( "public " + m_wgtClassName + "()" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super();" );
    for( Iterator it = fieldSet.iterator(); it.hasNext(); )
    {
      FormFieldDesc fieldDesc = (FormFieldDesc)it.next();
      if( fieldDesc.hidden == false )
      {
        p_writer.println( "m_" + fieldDesc.name + ".setTitle( s_messages." + fieldDesc.name
            + "() );" );
        p_writer.println( "m_" + fieldDesc.name + ".setReadOnly( " + fieldDesc.readOnly + " );" );
        p_writer.println( "m_" + fieldDesc.name + ".setRequired( " + fieldDesc.required + " );" );
        p_writer.println( "m_" + fieldDesc.name + ".addChangeListener( this );" );
        p_writer.println( "m_panel.add( m_" + fieldDesc.name + " );" );
      }
    }

    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }


  private void writeGetBean(SourceWriter p_writer, String p_beanType)
  {
    if( p_writer == null )
    {
      return;
    }

    // write getBean
    p_writer.println( "public " + p_beanType + " getTypedBean()" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "return (" + p_beanType + ")super.getBean();" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );

    // override setBean
    p_writer.println( "public void setBean(Dbb4Form p_bean)" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super.setBean( p_bean );" );
    p_writer.println( "initFromBean();" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );

    // write initFromBean
    p_writer.println( "public void initFromBean()" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super.initFromBean();" );
    p_writer.println( "if( getTypedBean() == null )" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "return;" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
    Collection fieldSet = m_field.values();
    for( Iterator it = fieldSet.iterator(); it.hasNext(); )
    {
      FormFieldDesc fieldDesc = (FormFieldDesc)it.next();
      if( fieldDesc.hidden == false )
      {
        if( fieldDesc.wgtClass.equals( "com.fullmetalgalaxy.client.form.WgtFieldInteger" ) )
        {
          p_writer.println( "m_" + fieldDesc.name + ".setIntValue( getTypedBean().get"
              + fieldDesc.name + "() );" );
        }
        else if( fieldDesc.wgtClass.equals( "com.fullmetalgalaxy.client.form.WgtFieldBoolean" ) )
        {
          p_writer.println( "m_" + fieldDesc.name + ".setBooleanValue( getTypedBean().get"
              + fieldDesc.name + "() );" );
        }
        else
        {
          p_writer.println( "m_" + fieldDesc.name + ".setValue( getTypedBean().get"
              + fieldDesc.name + "() );" );
        }
      }
    }
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }


  private void writeChangeListener(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }

    p_writer.println( "public void onChange(Widget p_sender)" );
    p_writer.println( "{" );
    p_writer.indent();
    Collection fieldSet = m_field.values();
    for( Iterator it = fieldSet.iterator(); it.hasNext(); )
    {
      FormFieldDesc fieldDesc = (FormFieldDesc)it.next();
      if( (fieldDesc.readOnly == false) && (fieldDesc.hidden == false) )
      {
        p_writer.println( "if( p_sender == m_" + fieldDesc.name + ")" );
        p_writer.println( "{" );
        p_writer.indent();

        if( fieldDesc.wgtClass.equals( "com.fullmetalgalaxy.client.form.WgtFieldInteger" ) )
        {
          p_writer.println( "getTypedBean().set" + fieldDesc.name + "( m_" + fieldDesc.name
              + ".getIntValue());" );
        }
        else if( fieldDesc.wgtClass.equals( "com.fullmetalgalaxy.client.form.WgtFieldBoolean" ) )
        {
          p_writer.println( "getTypedBean().set" + fieldDesc.name + "( m_" + fieldDesc.name
              + ".getBooleanValue());" );
        }
        else
        {
          p_writer.println( "getTypedBean().set" + fieldDesc.name + "( ("
              + fieldDesc.type.getQualifiedSourceName() + ")m_" + fieldDesc.name + ".getValue());" );
        }

        p_writer.outdent();
        p_writer.println( "}" );
      }
    }
    p_writer.println( "initFromBean();" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }

  /**
   * Write the message interface used by the GWT generator for i18n.
   * @param p_writer
   * @param p_msgClassName
   */
  private void writeMessageClass(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }
    Collection fieldSet = m_field.values();

    p_writer.println( "public interface " + m_msgClassName + " extends Messages" );
    p_writer.println( "{" );
    p_writer.indent();
    for( Iterator it = fieldSet.iterator(); it.hasNext(); )
    {
      FormFieldDesc fieldDesc = (FormFieldDesc)it.next();
      p_writer.println( "String " + fieldDesc.name + "();" );
    }
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }

}
