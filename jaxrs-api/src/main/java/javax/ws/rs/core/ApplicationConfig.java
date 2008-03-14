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

   /**
    * Get a list of root resource classes. Classes
    * not annotated with {@link javax.ws.rs.Path} will be ignored.
    *
    * @return a list of root resource classes.
    * @see javax.ws.rs.Path
    */
   public abstract Set<Class<?>> getResourceClasses();

   /**
    * Get a list of provider classes. Classes not implementing an extension
    * interface (one or more of {@link javax.ws.rs.ext.MessageBodyReader},
    * {@link javax.ws.rs.ext.MessageBodyWriter} or
    * {@link javax.ws.rs.ext.ContextResolver}) will be ignored. The default
    * implementation returns an empty set.
    *
    * @return a set of provider classes
    * @see javax.ws.rs.ext.Provider
    * @see javax.ws.rs.ext.MessageBodyReader
    * @see javax.ws.rs.ext.MessageBodyWriter
    * @see javax.ws.rs.ext.ContextResolver
    */
   public Set<Class<?>> getProviderClasses()
   {
      return Collections.EMPTY_SET;
   }

   /**
    * Get a map of file extensions to media types. This is used to drive
    * URI-based content negotiation such that, e.g.:
    * <pre>GET /resource.atom</pre>
    * <p>is equivalent to:</p>
    * <pre>GET /resource
    * Accept: application/atom+xml</pre>
    * <p>The default implementation returns an empty map.</p>
    *
    * @return a map of file extension to media type
    */
   public Map<String, MediaType> getExtensionMappings()
   {
      return Collections.EMPTY_MAP;
   }
}
