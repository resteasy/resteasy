package org.jboss.resteasy.plugins.providers;


import org.jboss.resteasy.plugins.providers.yaml.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.yaml.i18n.Messages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.WriterException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Node;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provider for YAML {@literal <->} Object marshalling. Uses the following mime
 * types:<pre><code>
 *   text/yaml
 *   text/x-yaml
 *   application/x-yaml</code></pre>
 *
 * @author Martin Algesten
 */
@Provider
@Consumes({"text/yaml", "text/x-yaml", "application/x-yaml"})
@Produces({"text/yaml", "text/x-yaml", "application/x-yaml"})
@Deprecated
public class YamlProvider extends AbstractEntityProvider<Object> {
   private static final String ALLOWED_LIST = "resteasy.yaml.deserialization.allowed.list.allowIfBaseType";
   private static final String DISABLE_TYPE_CHECK = "resteasy.yaml.deserialization.disable.type.check";
   private static final Collection<String> DENY_LIST = Arrays.asList(
           "javax.script.ScriptEngineManager",
           URLClassLoader.class.getName(),
           Object.class.getName()
   );
   private final Collection<String> allowedList;

   public YamlProvider() {
      allowedList = createAllowedList();
   }
   // MessageBodyReader

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException {

      try {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
         if (isValidInternalType(type)) {
            // Use the old behavior of trusting everything
            if (isTypeCheckDisabled()) {
               return new Yaml().loadAs(entityStream, type);
            }
            final BaseConstructor constructor;
            if (genericType instanceof ParameterizedType) {
               constructor = new TypeSafeConstructor((ParameterizedType) genericType, getClassLoader(type), allowedList);
            } else {
               constructor = new TypeSafeConstructor(type, getClassLoader(type), allowedList);
            }
            return new Yaml(constructor).loadAs(entityStream, type);
         } else {
            CustomClassLoaderConstructor customClassLoaderConstructor = new CustomClassLoaderConstructor(type, getClassLoader(type));
            return new Yaml(customClassLoaderConstructor).loadAs(entityStream, type);
         }
      } catch (Exception e) {
         LogMessages.LOGGER.debug(Messages.MESSAGES.failedToDecodeYamlMessage(e.getMessage()));
         throw new ReaderException(Messages.MESSAGES.failedToDecodeYaml(), e);
      }
   }

   // MessageBodyWriter
   protected boolean isValidInternalType(Class type) {
      if (List.class.isAssignableFrom(type)
            || Set.class.isAssignableFrom(type)
            || Map.class.isAssignableFrom(type)
            || type.isArray()) {
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidType(Class type) {
      if (isValidInternalType(type)) {
         return true;
      }
      if (StreamingOutput.class.isAssignableFrom(type)) return false;
      String className = type.getName();
      if (className.startsWith("java.")) return false;
      if (className.startsWith("javax.")) return false;
      if (type.isPrimitive()) return false;

      return true;
   }


   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return isValidType(type);
   }

   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {

      try {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
         entityStream.write(new Yaml().dump(t).getBytes());

      } catch (Exception e) {

         LogMessages.LOGGER.debug(Messages.MESSAGES.failedToEncodeYaml(t.toString()));
         throw new WriterException(e);

      }

   }

   private static ClassLoader getClassLoader(final Class<?> type) {
      if (System.getSecurityManager() == null) {
         // Get the TCCL first
         ClassLoader result = Thread.currentThread().getContextClassLoader();
         if (result == null) {
            result = type.getClassLoader();
         }
         return result;
      }
      return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
         // Get the TCCL first
         ClassLoader result = Thread.currentThread().getContextClassLoader();
         if (result == null) {
            result = type.getClassLoader();
         }
         return result;
      });
   }

   private static Collection<String> createAllowedList() {
      final String value = getProperty(ALLOWED_LIST);
      return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
   }

   private static boolean isTypeCheckDisabled() {
      final String value = getProperty(DISABLE_TYPE_CHECK);
      return value != null && (value.isEmpty() || value.equalsIgnoreCase("true"));
   }

   private static String getProperty(final String key) {
      final ResteasyConfiguration configuration = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (System.getSecurityManager() == null) {
         String value = null;
         if (configuration != null) {
            value = configuration.getParameter(key);
            if (value == null) {
               value = configuration.getInitParameter(key);
            }
         }
         if (value == null) {
            System.getProperty(key);
         }
         return value;
      } else {
         return AccessController.doPrivileged((PrivilegedAction<String>) () -> {
            String value = null;
            if (configuration != null) {
               value = configuration.getParameter(key);
               if (value == null) {
                  value = configuration.getInitParameter(key);
               }
            }
            if (value == null) {
               System.getProperty(key);
            }
            return value;
         });
      }
   }

   private static class TypeSafeConstructor extends CustomClassLoaderConstructor {
      private final Set<Class<?>> types;
      private final Collection<String> allowedList;

      private TypeSafeConstructor(final ParameterizedType parameterizedType, final ClassLoader classLoader,
                                  final Collection<String> allowedList) {
         super(classLoader);
         this.allowedList = allowedList;
         final Set<Class<?>> genericTypes = new HashSet<>();
         for (Type typeArg : parameterizedType.getActualTypeArguments()) {
            try {
               genericTypes.add(classLoader.loadClass(typeArg.getTypeName()));
            } catch (ClassNotFoundException e) {
               LogMessages.LOGGER.failedToLoadType(typeArg.getTypeName());
            }
         }
         this.types = genericTypes;
      }

      private TypeSafeConstructor(final Class<?> type, final ClassLoader classLoader,
                                  final Collection<String> allowedList) {
         super(classLoader);
         this.types = Collections.singleton(type);
         this.allowedList = allowedList;
      }

      @Override
      protected Object newInstance(final Class<?> ancestor, final Node node, final boolean tryDefault) {
         if (denied(node.getType())) {
            throw Messages.MESSAGES.typeNotAllowed(node.getType());
         }
         return super.newInstance(ancestor, node, tryDefault);
      }

      private boolean denied(final Class<?> type) {
         if (type == null) {
            return false;
         }
         final String name = type.getName();
         // Check the allowed types first
         if (allowedList.contains(name)) {
            return false;
         }
         // Allow all primitives
         if (type.isPrimitive()) {
            return false;
         }
         // Denied types are not safe
         if (DENY_LIST.contains(name)) {
            return true;
         }
         // We should trust known types
         if (name.startsWith("java") || name.startsWith("javax")) {
            return false;
         }
         // Finally check the known types
         boolean denied = true;
         for (Class<?> allowed : types) {
            if (allowed.isAssignableFrom(type)) {
               denied = false;
               break;
            }
         }
         return denied;
      }
   }

}
