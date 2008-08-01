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

package javax.ws.rs.core;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Defines the components of a JAX-RS application and supplies additional
 * metadata.
 */
public abstract class ApplicationConfig
{
   private static final Set<Class<?>> emptySet = Collections.emptySet();
   private static final Map<String, MediaType> emptyMediaMap = Collections.emptyMap();
   private static final Map<String, String> emptyLanguageMap = Collections.emptyMap();

   /**
    * Get a list of root resource classes. Resource classes are not required
    * to implement any specific interface so the return type is necessarily
    * loose, nevertheless, implementations should warn about
    * and ignore classes that do not conform to the requirements of root
    * resource classes.
    *
    * @return a list of root resource classes.
    * @see javax.ws.rs.Path
    */
   public abstract Set<Class<?>> getResourceClasses();

   /**
    * Get a list of provider classes. There is no common base class or
    * interface for providers so the return type is necessarily
    * loose, nevertheless, implementations should warn about
    * and ignore classes that do not conform to the requirements of a
    * provider. The default implementation returns an empty set.
    *
    * @return a set of provider classes
    * @see javax.ws.rs.ext.Provider
    * @see javax.ws.rs.ext.MessageBodyReader
    * @see javax.ws.rs.ext.MessageBodyWriter
    * @see javax.ws.rs.ext.ContextResolver
    * @see javax.ws.rs.ext.ExceptionMapper
    */
   public Set<Class<?>> getProviderClasses()
   {
      return emptySet;
   }

   /**
    * Get a map of file extension to media type. This is used to drive
    * URI-based content negotiation such that, e.g.:
    * <pre>GET /resource.atom</pre>
    * <p>is equivalent to:</p>
    * <pre>GET /resource
    * Accept: application/atom+xml</pre>
    * <p>The default implementation returns an empty map.</p>
    *
    * @return a map of file extension to media type
    */
   public Map<String, MediaType> getMediaTypeMappings()
   {
      return emptyMediaMap;
   }

   /**
    * Get a map of file extension to language. This is used to drive
    * URI-based content negotiation such that, e.g.:
    * <pre>GET /resource.english</pre>
    * <p>is equivalent to:</p>
    * <pre>GET /resource
    * Accept-Language: en</pre>
    * <p>The default implementation returns an empty map.</p>
    *
    * @return a map of file extension to language
    */
   public Map<String, String> getLanguageMappings()
   {
      return emptyLanguageMap;
   }
}
