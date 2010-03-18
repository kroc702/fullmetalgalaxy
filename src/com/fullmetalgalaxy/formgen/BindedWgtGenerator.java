/**
 * 
 */
package com.fullmetalgalaxy.formgen;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.HasMetaData;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Vincent Legendre
 * possible Meta data on widget class:
 * - BeanClass       [String]
 * - ReadOnly        [Boolean]
 * possible Meta data on view:
 * - ReadOnly        [Boolean]
 * - BeanGetter      [String]
 * - BeanGetterType  [String]
 * - BeanSetter      [String]
 */
public class BindedWgtGenerator extends Generator
{
  protected final static String ABSTRACT_VIEW = "com.fullmetalgalaxy.client.widget.AbstractView";
  protected final static String SCALAR_VIEW = "com.fullmetalgalaxy.client.widget.ScalarView";
  protected final static Pattern PATTERN_GETTER = Pattern.compile( "(get|is)([A-Z]\\w*)" );
  protected final static int NAME_GROUP_INDEX = 2;
  protected final static int GET_GROUP_INDEX = 1;

  private String m_wgtClassName = "Binded";
  private String m_beanClassName = "Eb";
  private boolean m_isWgtReadOnly = false;
  private boolean m_isBaseWgtAssignableToChangeListener = false;
  ArrayList<JConstructor> m_wgtConstructorList = new ArrayList<JConstructor>();
  /**
   * field list
   */
  ArrayList<FieldDesc> m_field = new ArrayList<FieldDesc>();
  /**
   * all required import
   */
  ArrayList<String> m_importList = new ArrayList<String>();


  /**
   * 
   */
  public BindedWgtGenerator()
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.core.ext.Generator#generate(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext, java.lang.String)
   */
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
      String packageName = requestedClass.getPackage().getName();
      String simpleClassName = requestedClass.getSimpleSourceName();
      m_isBaseWgtAssignableToChangeListener = isAssignableTo( requestedClass,
          "com.google.gwt.user.client.ui.ChangeListener" );

      m_wgtClassName = "Binded" + simpleClassName;

      clearBeanDescription();
      readWgtMethod( requestedClass );
      m_beanClassName = getMetaString( requestedClass, "BeanClass", "Eb"
          + simpleClassName.replaceFirst( "Wgt", "" ) );
      m_isWgtReadOnly = getMetaBoolean( requestedClass, "ReadOnly", false );
      readBeanMethod( typeOracle.getType( m_beanClassName ) );
      cleanFieldDesc( p_logger, typeOracle );
      writer = getSourceWriter( p_logger, p_context, packageName, p_typeName );
      if( writer != null )
      {
        writeConstructor( writer );
        writeChangeListener( writer );
        writeAttach( writer );
        writeSetRO( writer );

        writer.commit( p_logger );
      }

      qualifiedWgtClassName = packageName + "." + m_wgtClassName;
    } catch( NotFoundException e )
    {
      p_logger.log( TreeLogger.ERROR, "Class '" + p_typeName + "' Not Found", e );
      throw new UnableToCompleteException();
    }
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
      String packageName, String p_wgtBase)
  {
    PrintWriter printWriter = context.tryCreate( logger, packageName, m_wgtClassName );
    if( printWriter == null )
    {
      return null;
    }
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, m_wgtClassName );
    composerFactory.addImport( "com.google.gwt.user.client.ui.Widget" );
    composerFactory.addImport( "com.google.gwt.user.client.ui.ChangeListener" );
    for( String type : m_importList )
    {
      composerFactory.addImport( type );
    }

    composerFactory.setSuperclass( p_wgtBase );
    composerFactory.addImplementedInterface( "ChangeListener" );
    return composerFactory.createSourceWriter( context, printWriter );
  }

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

  private void addImport(JType p_class)
  {
    if( p_class.isPrimitive() == null )
    {
      addImport( p_class.getQualifiedSourceName() );
    }
  }

  /**
   * clear previous class description
   */
  private void clearBeanDescription()
  {
    m_importList.clear();
    m_field.clear();
    m_wgtConstructorList.clear();
  }

  /**
   * read all bean method to determine which widget we will have to create. 
   * @param p_bean
   */
  private void readWgtMethod(JClassType p_bean)
  {
    // read getter/setter from super class first
    if( p_bean.getSuperclass() != null )
    {
      readWgtMethod( p_bean.getSuperclass() );
    }

    addImport( p_bean );

    // read all widget getters
    for( JMethod method : p_bean.getMethods() )
    {
      Matcher matcher = PATTERN_GETTER.matcher( method.getName() );
      if( (matcher.matches()) && (method.getParameters().length == 0) )
      {
        // this is a Getter
        if( (method.getReturnType() instanceof JClassType)
            && (isAssignableTo( (JClassType)method.getReturnType(), ABSTRACT_VIEW )) )
        {
          // this getter return a view, we have to map it
          FieldDesc fieldDesc = new FieldDesc();
          String name = matcher.group( NAME_GROUP_INDEX );
          // String name = method.getName().substring( 3 );
          fieldDesc.setWgtName( method.getName() + "()" );
          fieldDesc.setReadOnly( getMetaBoolean( method, "ReadOnly", false ) );
          addImport( method.getReturnType() );
          fieldDesc.setScalar( isAssignableTo( (JClassType)method.getReturnType(), SCALAR_VIEW ) );
          fieldDesc.setGetter( getMetaString( method, "BeanGetter", matcher.group( GET_GROUP_INDEX )
              + name ) );
          fieldDesc.setStrType( getMetaString( method, "BeanGetterType", null ) );
          fieldDesc.setSetter( getMetaString( method, "BeanSetter", "set" + name ) );

          m_field.add( fieldDesc );
        }
      }
      else if( method.isConstructor() != null )
      {
        m_wgtConstructorList.add( method.isConstructor() );
      }
    }
  }

  private void readBeanMethod(JClassType p_bean)
  {
    // read getter/setter from super class first
    if( p_bean.getSuperclass() != null )
    {
      readBeanMethod( p_bean.getSuperclass() );
    }

    addImport( p_bean );

    // read all bean getters
    for( JMethod method : p_bean.getMethods() )
    {
      if( (PATTERN_GETTER.matcher( method.getName() ).matches())
          && (method.getParameters().length == 0) && (method.isPublic()) )
      {
        // this is a Getter
        FieldDesc fieldDesc = getFieldFromBeanGetter( method.getName() );
        if( (fieldDesc != null) && (fieldDesc.getStrType() == null) )
        {
          // the corresponding getter was found in widget
          fieldDesc.setType( method.getReturnType() );
        }
      }
    }

    // find composed getter and try finding returned type
    for( FieldDesc field : m_field )
    {
      if( (field.getStrType() == null) && (field.getJType() == null)
          && (field.getGetterMethod().contains( "." )) )
      {
        try
        {
          JClassType currentBean = p_bean;
          JType currentType = null;
          String[] getters = field.getGetterMethod().split( "\\." );
          for( String getter : getters )
          {
            // extract getter name
            // getter = getter.replaceAll( "\\(.*\\)", "" );
            // JMethod method = currentBean.getMethod( getter, new JType[0] );
            JMethod method = getMethod( currentBean, getter );
            currentType = method.getReturnType();
            currentBean = currentType.isClass();
            if( currentBean == null )
            {
              throw new NotFoundException();
            }
          }
          field.setType( currentType );
        } catch( NotFoundException e )
        {
          // do nothing: this field will be deleted in cleanFieldDesc method
        }
      }
    }
  }

  private JMethod getMethod(JClassType p_bean, String p_getter) throws NotFoundException
  {
    String getterName = p_getter.replaceAll( "\\(\\)", "" );
    int paramCount = 0;
    JMethod method = null;
    // first, look for the most obvious getter
    try
    {
      method = p_bean.getMethod( getterName, new JType[0] );
    } catch( NotFoundException e )
    {
    }
    if( method == null )
    {
      // ok try something better
      getterName = p_getter.replaceAll( "\\(.*\\)", "" );
      paramCount = p_getter.split( "," ).length;
      for( JMethod jmethod : p_bean.getMethods() )
      {
        if( jmethod.getName().equals( getterName ) && jmethod.getParameters().length == paramCount )
        {
          // we are not sure, but lets bet it's the one we are looking for.
          method = jmethod;
          break;
        }
      }
    }
    if( method == null )
    {
      throw new NotFoundException();
    }
    return method;
  }

  private FieldDesc getFieldFromBeanGetter(String p_getter)
  {
    for( FieldDesc field : m_field )
    {
      if( (field.getGetterName() != null) && (field.getGetterName().equals( p_getter )) )
      {
        return field;
      }
    }
    return null;
  }

  private void cleanFieldDesc(TreeLogger p_logger, TypeOracle p_typeOracle)
  {
    int i = 0;
    FieldDesc field = null;
    while( i < m_field.size() )
    {
      field = m_field.get( i );
      if( field.getJType() == null && field.getStrType() == null )
      {
        p_logger.log( TreeLogger.WARN, "Widget " + field.getWgtName()
            + " won't be binded as no data type was found." );
        m_field.remove( i );
      }
      else
      {
        i++;
      }
    }
  }

  private void writeConstructor(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }

    // write constructors
    p_writer.println( "" );
    p_writer.println( "public " + m_wgtClassName + "()" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super();" );
    // p_writer.println( "attachBean( p_bean );" );
    p_writer.println( "addChangeListener();" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );

    // write getType
    p_writer.println( "public " + m_beanClassName + " getTypedBean()" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "return (" + m_beanClassName + ")getObject();" );
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );

    // write addChangeListener
    p_writer.println( "protected void addChangeListener()" );
    p_writer.println( "{" );
    p_writer.indent();
    for( FieldDesc field : m_field )
    {
      if( !field.isReadOnly() && !m_isWgtReadOnly )
      {
        if( field.isScalar() )
        {
          p_writer.println( field.getWgtName() + ".addChangeListener( this );" );
        }
      }
      else
      {
        p_writer.println( field.getWgtName() + ".setReadOnly( true );" );
      }
    }
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }



  private void writeAttach(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }
    p_writer.println( "public void attachBean(Object p_bean)" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super.attachBean( p_bean );" );
    p_writer.println( "if( getTypedBean() == null )" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "return;" );
    p_writer.outdent();
    p_writer.println( "}" );

    for( FieldDesc field : m_field )
    {
      if( field.getGetterMethod().contains( "." ) )
      {
        p_writer.println( "if( " + field.getNullCheckGetter() + " )" );
        p_writer.println( "{" );
        p_writer.indent();
      }

      if( field.isScalar() )
      {
        p_writer.println( field.getWgtName() + ".setScalarValue( getTypedBean()."
            + field.getGetterMethod() + " );" );
      }
      else
      {
        p_writer.println( field.getWgtName() + ".attachBean( getTypedBean()."
            + field.getGetterMethod() + " );" );
      }

      if( field.getGetterMethod().contains( "." ) )
      {
        p_writer.outdent();
        p_writer.println( "} else {" );
        p_writer.indent();
        if( field.isScalar() )
        {
          p_writer.println( field.getWgtName() + ".setScalarValue( null );" );
        }
        else
        {
          p_writer.println( field.getWgtName() + ".attachBean( null );" );
        }
        p_writer.outdent();
        p_writer.println( "}" );

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
    p_writer.println( "if( getTypedBean() == null )" );
    p_writer.println( "{" );
    p_writer.indent();
    if( m_isBaseWgtAssignableToChangeListener )
    {
      p_writer.println( "super.onChange( p_sender );" );
    }
    p_writer.println( "return;" );
    p_writer.outdent();
    p_writer.println( "}" );
    for( FieldDesc field : m_field )
    {
      if( !field.isReadOnly() && !m_isWgtReadOnly && field.isScalar() )
      {
        p_writer.println( "else if( p_sender == " + field.getWgtName() + ")" );
        p_writer.println( "{" );
        p_writer.indent();
        p_writer.println( "getTypedBean()." + field.getSetter() + "( ("
            + field.getClassTypeString() + ")" + field.getWgtName() + ".getObject() );" );
        p_writer.outdent();
        p_writer.println( "}" );
      }
    }
    if( m_isBaseWgtAssignableToChangeListener )
    {
      p_writer.println( "super.onChange( p_sender );" );
    }
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }

  private void writeSetRO(SourceWriter p_writer)
  {
    if( p_writer == null )
    {
      return;
    }
    p_writer.println( "public void setReadOnly(boolean p_readOnly)" );
    p_writer.println( "{" );
    p_writer.indent();
    p_writer.println( "super.setReadOnly( p_readOnly );" );
    for( FieldDesc field : m_field )
    {
      if( !field.isReadOnly() && !m_isWgtReadOnly )
      {
        p_writer.println( field.getWgtName() + ".setReadOnly( p_readOnly );" );
      }
    }
    p_writer.outdent();
    p_writer.println( "}" );
    p_writer.println( "" );
  }



  // ===================================================

  /**
   * return true: 
   *  - if p_tagName is found with no argument
   *  - if p_tagName is found with first argument = 'true'
   * return false if p_tagName is found with first argument != 'true'
   * return p_default otherwise. 
   */
  private boolean getMetaBoolean(HasMetaData p_method, String p_tagName, boolean p_default)
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
   * 
   * @param p_method
   * @param p_tagName
   * @param p_default
   * @return first parameter of meta tag 'p_tagName'
   * p_default if tag not found or first param not found.
   */
  private String getMetaString(HasMetaData p_method, String p_tagName, String p_default)
  {
    String tagValue = p_default;
    String metaTag[][] = p_method.getMetaData( p_tagName );
    if( metaTag.length > 0 )
    {
      // tag is found
      if( metaTag[0].length > 0 )
      {
        // arguments are founds
        tagValue = metaTag[0][0];
      }
    }
    return tagValue;
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
}
