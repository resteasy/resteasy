/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * RuntimeDelegate.java
 *
 * Created on November 15, 2007, 4:00 PM
 *
 */

package javax.ws.rs.ext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import java.lang.reflect.ReflectPermission;
import java.net.URL;

/**
 * Implementations of JAX-RS provide a concrete subclass of RuntimeDelegate and
 * various JAX-RS API methods defer to methods of RuntimeDelegate for their
 * functionality. Regular users of JAX-RS are not expected to use this class
 * directly and overriding an implementation of this class with a user supplied
 * subclass may cause unexpected behavior.
 */
public abstract class RuntimeDelegate
{

   public static final String JAXRS_RUNTIME_DELEGATE_PROPERTY
           = "javax.ws.rs.ext.RuntimeDelegate";
   private static final String JAXRS_DEFAULT_RUNTIME_DELEGATE
           = "com.sun.ws.rs.ext.RuntimeDelegateImpl";

   private static ReflectPermission rp = new ReflectPermission("suppressAccessChecks");

   protected RuntimeDelegate()
   {
   }

   private static volatile RuntimeDelegate rd;

   /**
    * Obtain a RuntimeDelegate instance. If an instance had not already been
    * created and set via {@link #setInstance}, the first invocation will
    * create an instance which will then be cached for future use.
    * <p/>
    * <p/>
    * The algorithm used to locate the RuntimeDelegate subclass to use consists
    * of the following steps:
    * <p/>
    * <ul>
    * <li>
    * If a resource with the name of
    * <code>META-INF/services/javax.ws.rs.ext.RuntimeDelegate</code>
    * exists, then its first line, if present, is used as the UTF-8 encoded
    * name of the implementation class.
    * </li>
    * <li>
    * If the $java.home/lib/jaxrs.properties file exists and it is readable by
    * the <code>java.util.Properties.load(InputStream)</code> method and it contains
    * an entry whose key is <code>javax.ws.rs.ext.RuntimeDelegate</code>, then the value of
    * that entry is used as the name of the implementation class.
    * </li>
    * <li>
    * If a system property with the name <code>javax.ws.rs.ext.RuntimeDelegate</code>
    * is defined, then its value is used as the name of the implementation class.
    * </li>
    * <li>
    * Finally, a default implementation class name is used.
    * </li>
    * </ul>
    *
    * @return an instance of RuntimeDelegate
    */
   public static RuntimeDelegate getInstance()
   {
      // Double-check idiom for lazy initialization of fields.
      RuntimeDelegate result = rd;
      if (result == null)
      { // First check (no locking)
         synchronized (RuntimeDelegate.class)
         {
            result = rd;
            if (result == null)
            { // Second check (with locking)
               rd = result = findDelegate();
            }
         }
      }
      return result;
   }

   /**
    * Obtain a RuntimeDelegate instance using the method described in
    * {@link #getInstance}.
    *
    * @return an instance of RuntimeDelegate
    */
   private static RuntimeDelegate findDelegate()
   {
      try
      {
         Object delegate =
                 FactoryFinder.find(JAXRS_RUNTIME_DELEGATE_PROPERTY,
                         JAXRS_DEFAULT_RUNTIME_DELEGATE);
         if (!(delegate instanceof RuntimeDelegate))
         {
            Class pClass = RuntimeDelegate.class;
            String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
            ClassLoader loader = pClass.getClassLoader();
            if (loader == null)
            {
               loader = ClassLoader.getSystemClassLoader();
            }
            URL targetTypeURL = loader.getResource(classnameAsResource);
            throw new LinkageError("ClassCastException: attempting to cast" +
                    delegate.getClass().getClassLoader().getResource(classnameAsResource) +
                    "to" + targetTypeURL.toString());
         }
         return (RuntimeDelegate) delegate;
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Set the runtime delegate that will be used by JAX-RS classes. If this method
    * is not called prior to {@link #getInstance} then an implementation will
    * be sought as described in {@link #getInstance}.
    *
    * @param rd the runtime delegate instance
    * @throws SecurityException if there is a security manager and the permission
    *                           ReflectPermission("suppressAccessChecks") has not been granted.
    */
   public static void setInstance(RuntimeDelegate rd) throws SecurityException
   {
      SecurityManager security = System.getSecurityManager();
      if (security != null)
      {
         security.checkPermission(rp);
      }
      synchronized (RuntimeDelegate.class)
      {
         RuntimeDelegate.rd = rd;
      }
   }

   /**
    * Create a new instance of a {@link javax.ws.rs.core.UriBuilder}.
    *
    * @return new UriBuilder instance
    * @see javax.ws.rs.core.UriBuilder
    */
   public abstract UriBuilder createUriBuilder();

   /**
    * Create a new instance of a {@link javax.ws.rs.core.Response.ResponseBuilder}.
    *
    * @return new ResponseBuilder instance
    * @see javax.ws.rs.core.Response.ResponseBuilder
    */
   public abstract ResponseBuilder createResponseBuilder();

   /**
    * Create a new instance of a {@link javax.ws.rs.core.Variant.VariantListBuilder}.
    *
    * @return new VariantListBuilder instance
    * @see javax.ws.rs.core.Variant.VariantListBuilder
    */
   public abstract VariantListBuilder createVariantListBuilder();

   /**
    * Create a configured instance of the supplied endpoint type. How the
    * returned endpoint instance is published is dependent on the type of
    * endpoint.
    *
    * @param application  the application configuration
    * @param endpointType the type of endpoint instance to be created.
    * @return a configured instance of the requested type.
    * @throws java.lang.IllegalArgumentException
    *          if application is null or the
    *          requested endpoint type is not supported.
    * @throws java.lang.UnsupportedOperationException
    *          if the implementation
    *          supports no endpoint types.
    */
   public abstract <T> T createEndpoint(Application application,
                                        Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException;

   /**
    * Obtain an instance of a HeaderDelegate for the supplied class. An
    * implementation is required to support the following values for type:
    * {@link javax.ws.rs.core.Cookie}, {@link javax.ws.rs.core.CacheControl},
    * {@link javax.ws.rs.core.EntityTag}, {@link javax.ws.rs.core.NewCookie},
    * {@link javax.ws.rs.core.MediaType} and {@code java.util.Date}.
    *
    * @param type the class of the header
    * @return an instance of HeaderDelegate for the supplied type
    * @throws java.lang.IllegalArgumentException
    *          if type is null
    */
   public abstract <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type);

   /**
    * Defines the contract for a delegate that is responsible for
    * converting between the String form of a HTTP header and
    * the corresponding JAX-RS type <code>T</code>.
    *
    * @param T a JAX-RS type that corresponds to the value of a HTTP header
    */
   public static interface HeaderDelegate<T>
   {
      /**
       * Parse the supplied value and create an instance of <code>T</code>.
       *
       * @param value the string value
       * @return the newly created instance of <code>T</code>
       * @throws IllegalArgumentException if the supplied string cannot be
       *                                  parsed or is null
       */
      public T fromString(String value) throws IllegalArgumentException;

      /**
       * Convert the supplied value to a String.
       *
       * @param value the value of type <code>T</code>
       * @return a String representation of the value
       * @throws IllegalArgumentException if the supplied object cannot be
       *                                  serialized or is null
       */
      public String toString(T value);
   }
}
