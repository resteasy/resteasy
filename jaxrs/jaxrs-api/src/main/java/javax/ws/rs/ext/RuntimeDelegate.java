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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

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
           = "org.resteasy.spi.ResteasyProviderFactory";

   private static AtomicReference<RuntimeDelegate> rdr =
           new AtomicReference<RuntimeDelegate>();

   protected RuntimeDelegate()
   {
   }

   public static void setDelegate(RuntimeDelegate delegate)
   {
      rdr.set(delegate);
   }

   /**
    * Obtain a RuntimeDelegate instance. The first invocation will create
    * an instance which will then be cached for future use.
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
    */
   public static RuntimeDelegate getInstance()
   {
      RuntimeDelegate rd = rdr.get();
      if (rd != null)
         return rd;
      if (rd == null) return null;
      synchronized (rdr)
      {
         rd = rdr.get();
         if (rd != null)
            return rd;
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
            rd = (RuntimeDelegate) delegate;
         }
         catch (Exception ex)
         {
            throw new WebApplicationException(ex, 500);
         }
         rdr.set(rd);
      }
      return rd;
   }

   /**
    * Create a new instance of a UriBuilder.
    *
    * @return new UriBuilder instance
    */
   public abstract UriBuilder createUriBuilder();

   /**
    * Create a new instance of a ResponseBuilder.
    *
    * @return new ResponseBuilder instance
    */
   public abstract ResponseBuilder createResponseBuilder();

   /**
    * Create a new instance of a VariantListBuilder.
    *
    * @return new VariantListBuilder instance
    */
   public abstract VariantListBuilder createVariantListBuilder();

   /**
    * Obtain an instance of a HeaderDelegate for the supplied class. An
    * implementation is required to support the following classes:
    * Cookie, CacheControl, EntityTag, NewCookie, MediaType.
    */
   public abstract <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type);

   /**
    * Defines the contract for a delegate that is responsible for
    * converting between the String form of a HTTP header and
    * the corresponding JAX-RS type <code>T</code>.
    */
   public static interface HeaderDelegate<T>
   {
      /**
       * Parse the supplied value and create an instance of <code>T</code>.
       *
       * @param value the string value
       * @return the newly created instance of <code>T</code>
       * @throws IllegalArgumentException if the supplied string cannot be parsed
       */
      public T fromString(String value) throws IllegalArgumentException;

      /**
       * Convert the supplied value to a String.
       *
       * @param value the value of type <code>T</code>
       * @return a String representation of the value
       * @throws IllegalArgumentException if the supplied object cannot be serialized
       */
      public String toString(T value);
   }
}
