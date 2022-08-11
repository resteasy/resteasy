package org.jboss.resteasy.plugins.providers.jaxb;

import jakarta.xml.bind.annotation.XmlNs;
import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.jaxb.hacks.RiHacks;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import jakarta.xml.bind.Binder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.SchemaOutputResolver;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

   public JAXBContextWrapper(final JAXBContext wrappedContext, final JAXBConfig config) throws JAXBException
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
   public JAXBContextWrapper(final Class<?>[] classes, final Map<String, Object> properties, final JAXBConfig config) throws JAXBException
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
   public JAXBContextWrapper(final String contextPath, final JAXBConfig config) throws JAXBException
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
   public JAXBContextWrapper(final JAXBConfig config, final Class<?>... classes) throws JAXBException
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
            try {
               final Map<String, String> namespaces = new HashMap<>();
               for (XmlNs xmlNs : config.namespaces()) {
                  namespaces.put(xmlNs.namespaceURI(), xmlNs.prefix());
               }
               final BiFunction<String, String, String> mapperFunction = (namespace, suggestion) -> {
                  if (namespaces.containsKey(namespace)) {
                     return namespaces.get(namespace);
                  }
                  return suggestion;
               };
               mapper = RiHacks.createNamespacePrefixMapper(mapperFunction);
            } catch (JAXBException e) {
               throw e;
            } catch (Exception e) {
               throw Messages.MESSAGES.namespacePrefixMapperNotInClassPath(e);
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
    * @see jakarta.xml.bind.JAXBContext#createBinder()
    */
   public Binder<Node> createBinder()
   {
      return wrappedContext.createBinder();
   }

   /**
    * @param <T> type
    * @param domType dom class
    * @return {@link Binder}
    * @see jakarta.xml.bind.JAXBContext#createBinder(java.lang.Class)
    */
   public <T> Binder<T> createBinder(Class<T> domType)
   {
      return wrappedContext.createBinder(domType);
   }

   /**
    * @return {@link JAXBIntrospector}
    * @see jakarta.xml.bind.JAXBContext#createJAXBIntrospector()
    */
   public JAXBIntrospector createJAXBIntrospector()
   {
      return wrappedContext.createJAXBIntrospector();
   }

   /**
    * @return jaxb marshaller
    * @throws JAXBException jaxb exception
    * @see jakarta.xml.bind.JAXBContext#createMarshaller()
    */
   public Marshaller createMarshaller() throws JAXBException
   {
      Marshaller marshaller = RiHacks.createMarshaller(wrappedContext);
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
    * @see jakarta.xml.bind.JAXBContext#createUnmarshaller()
    */
   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      Unmarshaller u = unmarshaller.get();
      if (u == null)
      {
         u = RiHacks.createUnmarshaller(wrappedContext);
         unmarshaller.set(u);
      }
      return u;
   }

   /**
    * @return xml validator
    * @throws JAXBException jaxb exception
    * @see jakarta.xml.bind.JAXBContext#createValidator()
    * @deprecated See jakarta.xml.bind.JAXBContext#createValidator().
    */
   public jakarta.xml.bind.Validator createValidator() throws JAXBException
   {
      return wrappedContext.createValidator();
   }

   /**
    * @param outputResolver xml schema resolver
    * @throws IOException if I/O error occurred
    * @see jakarta.xml.bind.JAXBContext#generateSchema(jakarta.xml.bind.SchemaOutputResolver)
    */
   public void generateSchema(SchemaOutputResolver outputResolver) throws IOException
   {
      wrappedContext.generateSchema(outputResolver);
   }

}
