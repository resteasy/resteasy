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
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.System.getSecurityManager;
import static java.security.AccessController.doPrivileged;

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
   // Setting this property tells snakeyaml to allow all tags during parsing. The tags will be instead whitelisted by resteasy
   // provided constructor.
   private static final String ALLOW_ALL_TAGS = "org.yaml.snakeyaml.allow-all-tags";
   // These types should likely always be allowed
   private static final Collection<String> DEFAULT_ALLOWED_TYPES = Arrays.asList(
           toPattern(BigDecimal.class),
           toPattern(Boolean.class),
           toPattern(Byte.class),
           toPattern(Character.class),
           toPattern(Double.class),
           toPattern(Float.class),
           toPattern(Integer.class),
           toPattern(List.class),
           toPattern(Long.class),
           toPattern(Map.class),
           toPattern(Set.class),
           toPattern(Short.class),
           toPattern(String.class)
   );
   private final Pattern allowedPattern;

   public YamlProvider() {
      allowedPattern = createAllowPattern();
   }
   // MessageBodyReader

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException {
      // Set "org.yaml.snakeyaml.allow-all-tags" to true to make snakeyaml parsing work in permissive mode, allowing all tags
      // to be parsed. The tags will be checked later by a resteasy provided constructor (TypeSafeConstructor).
      // This is only relevant for snakeyaml 1.33.SP2+, which is Red Hat fork of snakeyaml 1.33.
      final String originalAllowAllTags = getSystemProperty(ALLOW_ALL_TAGS);
      setSystemProperty(ALLOW_ALL_TAGS, "true");

      try {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
         if (isValidInternalType(type)) {
            // Use the old behavior of trusting everything
            if (isTypeCheckDisabled()) {
               return new Yaml().loadAs(entityStream, type);
            }
            final BaseConstructor constructor;
            if (genericType instanceof ParameterizedType) {
               constructor = new TypeSafeConstructor((ParameterizedType) genericType, getClassLoader(type), allowedPattern);
            } else {
               constructor = new TypeSafeConstructor(type, getClassLoader(type), allowedPattern);
            }
            return new Yaml(constructor).loadAs(entityStream, type);
         } else {
            CustomClassLoaderConstructor customClassLoaderConstructor = new CustomClassLoaderConstructor(type, getClassLoader(type));
            return new Yaml(customClassLoaderConstructor).loadAs(entityStream, type);
         }
      } catch (Exception e) {
         LogMessages.LOGGER.debug(Messages.MESSAGES.failedToDecodeYamlMessage(e.getMessage()));
         throw new ReaderException(Messages.MESSAGES.failedToDecodeYaml(), e);
      } finally {
         // set the original value for "org.yaml.snakeyaml.allow-all-tags"
         setSystemProperty(ALLOW_ALL_TAGS, originalAllowAllTags);
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

   private static Pattern createAllowPattern() {
      final String value = getProperty(ALLOWED_LIST);
      final Collection<String> allowed = new ArrayList<>(DEFAULT_ALLOWED_TYPES);
      if (value != null) {
         Collections.addAll(allowed, value.split(","));
      }
      return Pattern.compile(String.join("|", allowed));
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

   private static String getSystemProperty(final String key) {
      return getSecurityManager() == null ? System.getProperty(key) : doPrivileged(new PrivilegedAction<String>() {
         @Override
         public String run() {
            return System.getProperty(key);
         }
      });
   }

   private static void setSystemProperty(final String key, final String value) {
      if (getSecurityManager() == null) {
         if (value == null) {
            System.clearProperty(key);
         } else {
            System.setProperty(key, value);
         }
      } else {
         doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
               if (value == null) {
                  return System.clearProperty(key);
               } else {
                  return System.setProperty(key, value);
               }
            }
         });
      }
   }

   private static String toPattern(final Class<?> type) {
      return Pattern.quote(type.getName());
   }

   private static class TypeSafeConstructor extends CustomClassLoaderConstructor {
      private final Set<Class<?>> types;
      private final Pattern allowedPattern;

      private TypeSafeConstructor(final ParameterizedType parameterizedType, final ClassLoader classLoader,
                                  final Pattern allowedPattern) {
         super(classLoader);
         this.allowedPattern = allowedPattern;
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
                                  final Pattern allowedPattern) {
         super(classLoader);
         this.types = Collections.singleton(type);
         this.allowedPattern = allowedPattern;
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
         // Allow all primitives
         if (type.isPrimitive()) {
            return false;
         }
         // Check the known types, these are a parameter or return type of a method. We assume these are safe.
         boolean denied = true;
         for (Class<?> allowed : types) {
            if (allowed.isAssignableFrom(type)) {
               denied = false;
               break;
            }
         }
         if (denied) {
            // Check the allowed list if we are overriding a denied type
            final String name = type.getName();
            if (allowedPattern.matcher(name).matches()) {
               return false;
            }
         }
         return denied;
      }
   }

}
