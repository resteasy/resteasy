package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;

/**
 * A wrapper class around a JAXBContext that enables additional features
 * to the RESTEasy JAXB-based providers.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@SuppressWarnings("deprecation")
public class JAXBContextWrapper extends JAXBContext
{

   private static final String NAMESPACE_PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
   private static Constructor mapperConstructor = null;

   static
   {
      try
      {
         // check to see if NamespacePrefixMapper is in classpath
         final Class[] namespace = new Class[1];
         final Class[] mapper = new Class[1];

         if (System.getSecurityManager() == null)
         {
            namespace[0] =  JAXBContextWrapper.class.getClassLoader().loadClass("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
            mapper[0] =  JAXBContextWrapper.class.getClassLoader().loadClass("org.jboss.resteasy.plugins.providers.jaxb.XmlNamespacePrefixMapper");
         }
         else
         {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               @Override public Void run() throws Exception {
                  namespace[0] =  JAXBContextWrapper.class.getClassLoader().loadClass("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
                  mapper[0] =  JAXBContextWrapper.class.getClassLoader().loadClass("org.jboss.resteasy.plugins.providers.jaxb.XmlNamespacePrefixMapper");

                  return null;
               }
            });
         }

         mapperConstructor = mapper[0].getConstructors()[0];
      }
      catch (ClassNotFoundException | PrivilegedActionException e)
      {

      }

   }

   private final JAXBContext wrappedContext;
   private final ThreadLocal<Unmarshaller> unmarshaller = new ThreadLocal<Unmarshaller>();

   /**
    * An optional namespace mapper that is used to apply prefixes to elements with a given namespace.
    */
   private Object mapper;

   /**
    * The optional Schema that is bound to this context
    */
   private Schema schema;

   public JAXBContextWrapper(JAXBContext wrappedContext, JAXBConfig config) throws JAXBException
   {
      processConfig(config);
      this.wrappedContext = wrappedContext;
   }

   /**
    * Create a new JAXBContextWrapper.
    *
    * @param classes classes
    * @param properties properties map
    * @param config jaxb configuration
    * @throws JAXBException jaxb exception
    */
   public JAXBContextWrapper(final Class<?>[] classes, final Map<String, Object> properties, JAXBConfig config) throws JAXBException
   {
      processConfig(config);
      try
      {
         if (System.getSecurityManager() == null)
         {
            wrappedContext = JAXBContext.newInstance(classes, properties);
         }
         else
         {
            wrappedContext = AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>()
            {
               @Override
               public JAXBContext run() throws JAXBException
               {
                     return JAXBContext.newInstance(classes, properties);
               }
            });
         }
      }
      catch (PrivilegedActionException paex)
      {
         throw new JAXBException(paex.getMessage());
      }
   }

   /**
    * Create a new JAXBContextWrapper.
    *
    * @param contextPath context path
    * @param config jaxb config
    * @throws JAXBException jaxb exception
    */
   public JAXBContextWrapper(final String contextPath, JAXBConfig config) throws JAXBException
   {
      processConfig(config);
      try
      {
         if (System.getSecurityManager() == null)
         {
            wrappedContext = JAXBContext.newInstance(contextPath);
         }
         else
         {
            wrappedContext = AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>()
            {
               @Override
               public JAXBContext run() throws JAXBException
               {
                  return JAXBContext.newInstance(contextPath);
               }
            });
         }
      }
      catch (PrivilegedActionException paex)
      {
         throw new JAXBException(paex.getMessage());
      }
   }

   /**
    * Create a new JAXBContextWrapper.
    *
    * @param classes classes
    * @param config jaxb config
    * @throws JAXBException jaxb exception
    */
   public JAXBContextWrapper(JAXBConfig config, Class<?>... classes) throws JAXBException
   {
      this(classes, Collections.<String, Object>emptyMap(), config);
   }

   /**
    * FIXME Comment this
    *
    * @param config jaxb config
    */
   private void processConfig(JAXBConfig config) throws JAXBException
   {
      if (config != null)
      {
         if (config.useNameSpacePrefix())
         {
            if (mapperConstructor == null)
            {
               throw new JAXBException(Messages.MESSAGES.namespacePrefixMapperNotInClassPath());
            }
            try
            {
               mapper = mapperConstructor.newInstance((Object[])config.namespaces());
            }
            catch (Exception e)
            {
               throw new JAXBException(e);
            }
         }
         if (!"".equals(config.schema()))
         {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(config.schemaType());
            try
            {
               InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                       config.schema());
               schema = schemaFactory.newSchema(new StreamSource(in));
            }
            catch (SAXException e)
            {
               throw new JAXBException(Messages.MESSAGES.errorTryingToLoadSchema(config.schema()), e);
            }
         }

      }
   }

   /**
    * Get the schema.
    *
    * @return the schema.
    */
   public Schema getSchema()
   {
      return schema;
   }

   /**
    * Set the schema.
    *
    * @param schema The schema to set.
    */
   public void setSchema(Schema schema)
   {
      this.schema = schema;
   }

   /**
    * @return {@link Binder}
    * @see javax.xml.bind.JAXBContext#createBinder()
    */
   public Binder<Node> createBinder()
   {
      return wrappedContext.createBinder();
   }

   /**
    * @param <T> type
    * @param domType dom class
    * @return {@link Binder}
    * @see javax.xml.bind.JAXBContext#createBinder(java.lang.Class)
    */
   public <T> Binder<T> createBinder(Class<T> domType)
   {
      return wrappedContext.createBinder(domType);
   }

   /**
    * @return {@link JAXBIntrospector}
    * @see javax.xml.bind.JAXBContext#createJAXBIntrospector()
    */
   public JAXBIntrospector createJAXBIntrospector()
   {
      return wrappedContext.createJAXBIntrospector();
   }

   /**
    * @return jaxb marshaller
    * @throws JAXBException jaxb exception
    * @see javax.xml.bind.JAXBContext#createMarshaller()
    */
   public Marshaller createMarshaller() throws JAXBException
   {
      Marshaller marshaller = wrappedContext.createMarshaller();
      if (mapper != null)
      {
         try
         {
            marshaller.setProperty(NAMESPACE_PREFIX_MAPPER, mapper);
         }
         catch (PropertyException e)
         {
            LogMessages.LOGGER.warn(e.getMessage());
         }
      }
      return marshaller;
   }

   /**
    * @return jaxb unmarshaller
    * @throws JAXBException jaxb exception
    * @see javax.xml.bind.JAXBContext#createUnmarshaller()
    */
   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      Unmarshaller u = unmarshaller.get();
      if (u == null)
      {
         u = wrappedContext.createUnmarshaller();
         unmarshaller.set(u);
      }
      return u;
   }

   /**
    * @return xml validator
    * @throws JAXBException jaxb exception
    * @see javax.xml.bind.JAXBContext#createValidator()
    * @deprecated See javax.xml.bind.JAXBContext#createValidator().
    */
   public Validator createValidator() throws JAXBException
   {
      return wrappedContext.createValidator();
   }

   /**
    * @param outputResolver xml schema resolver
    * @throws IOException if I/O error occurred
    * @see javax.xml.bind.JAXBContext#generateSchema(javax.xml.bind.SchemaOutputResolver)
    */
   public void generateSchema(SchemaOutputResolver outputResolver) throws IOException
   {
      wrappedContext.generateSchema(outputResolver);
   }

}
